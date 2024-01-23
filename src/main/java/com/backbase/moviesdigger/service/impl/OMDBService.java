package com.backbase.moviesdigger.service.impl;

import com.backbase.moviesdigger.domain.Movie;
import com.backbase.moviesdigger.exceptions.GeneralException;
import com.backbase.moviesdigger.exceptions.NotFoundException;
import com.backbase.moviesdigger.service.MovieProviderService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OMDBService implements MovieProviderService {

    @Value("${moviesdigger.movie-providers.omdb.api-key}")
    private String apiKey;
    @Value("${moviesdigger.movie-providers.omdb.base-url}")
    private String baseUrl;

    @Override
    public <T> T getMovieFieldByTitle(String movieName, String movieField, Class<T> className) {
        try {
            Map<String, Object> responseMap = fetchMovieDataWithRetry(movieName, null);

            if (responseMap.containsKey(movieField)) {
                Object fieldValue = responseMap.get(movieField);
                if (fieldValue == null || fieldValue.equals("N/A")) {
                    return null;
                }
                if (movieField.equals("BoxOffice")) {
                    return className.cast(parseBoxOfficeValue(fieldValue.toString()));
                }
                return className.cast(fieldValue);
            } else {
                log.warn("Provided field {} was not found in OMDB response", movieField);
                throw new GeneralException("Field " + movieField + " not found in the response");
            }
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Request to OMDB service failed, reason is {}", e.getMessage());
            throw new GeneralException("An unexpected condition was encountered when OMDB was requested, " +
                    "reason is " + e.getMessage());
        }
    }

    @Override
    public Movie getMovieByTitleAndYearOptional(String movieName, Integer year) {
        try {
            Map<String, Object> responseMap = fetchMovieDataWithRetry(movieName, year);
            return mapResponseToMovieEntity(responseMap);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Request to OMDB service failed, reason is {}", e.getMessage());
            throw new GeneralException("An unexpected condition was encountered when OMDB was requested, " +
                    "reason is " + e.getMessage());
        }
    }

    private Map<String, Object> fetchMovieDataWithRetry(String movieName, Integer year) throws Exception {
        Map<String, Object> responseMap = fetchMovieData(movieName, year);
        if (isMovieNotFound(responseMap)) {
            if (movieName.contains(" ")) {
                String firstWord = movieName.split("\\s+")[0];
                responseMap = fetchMovieData(firstWord, year);
                if (isMovieNotFound(responseMap)) {
                    log.warn("Provided movie {} was not found", movieName);
                    throw new NotFoundException("A movie " + movieName + " was not found");
                }
            } else {
                log.warn("Provided movie {} was not found", movieName);
                throw new NotFoundException("A movie " + movieName + " was not found");
            }
        }
        return responseMap;
    }

    private Map<String, Object> fetchMovieData(String movieName, Integer year) throws Exception {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("apikey", apiKey);
        queryParams.add("t", movieName);
        queryParams.add("plot", "short");
        if (year != null) {
            queryParams.add("y", year.toString());
        }

        ResponseEntity<String> omdbResponse = processAPI(
                baseUrl,
                HttpMethod.GET,
                queryParams,
                null,
                String.class
        );

        return new ObjectMapper().readValue(omdbResponse.getBody(), new TypeReference<>() {
        });
    }

    private boolean isMovieNotFound(Map<String, Object> responseMap) {
        String errorResponse = (String) responseMap.get("Error");
        return "Movie not found!".equals(errorResponse);
    }

    private Movie mapResponseToMovieEntity(Map<String, Object> responseMap) {
        log.debug("Trying to map response from OMDB service {} to Movie entity", responseMap.toString());

        Movie movie = new Movie();
        movie.setName((String) responseMap.get("Title"));
        String yearString = (String) responseMap.get("Year");
        if (yearString.contains("–") || yearString.contains("-")) {
            String firstYear = yearString.split("–")[0];
            movie.setReleaseYear(Integer.parseInt(firstYear));
        } else {
            movie.setReleaseYear(Integer.parseInt(yearString));
        }

        String awards = (String) responseMap.get("Awards");
        if (awards != null && !awards.equals("N/A") &&
                 awards.toLowerCase().contains("won") && awards.toLowerCase().contains("oscar")) {
            movie.setIsWinner(1);
        } else {
            movie.setIsWinner(0);
        }

        String boxOffice = (String) responseMap.get("BoxOffice");
        if (boxOffice != null && !boxOffice.equals("N/A")) {
            movie.setOfficeBoxValue(parseBoxOfficeValue(boxOffice));
        }
        return movie;
    }

    private BigDecimal parseBoxOfficeValue(String boxOfficeString) {
        try {
            String numberString = boxOfficeString.substring(1).replaceAll(",", "");
            return new BigDecimal(numberString);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Error parsing BoxOffice value", e);
        }
    }

    private <T> ResponseEntity<T> processAPI(
                                              String path,
                                              HttpMethod method,
                                              MultiValueMap<String, String> queryParams,
                                              Object body, Class<T> responseType) throws RestClientException {
        log.debug("Trying to call OMDB api to {} movie with query params {}", method.toString(), queryParams.toString());

        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(path)
                .queryParams(queryParams);

        HttpEntity<?> entity = new HttpEntity<>(body);
        ResponseEntity<T> response = restTemplate.exchange(
                URI.create(builder.toUriString()),
                method,
                entity,
                responseType
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        } else {
            log.warn("Throwing RestClient exception cause response code was not 200 but {}", response.getStatusCode());
            throw new RestClientException("OMDB API returned " + response.getStatusCode() + " , try again later or contact with admin");
        }
    }
}
