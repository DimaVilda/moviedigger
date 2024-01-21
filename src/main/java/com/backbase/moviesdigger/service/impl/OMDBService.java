package com.backbase.moviesdigger.service.impl;

import com.backbase.moviesdigger.domain.Movie;
import com.backbase.moviesdigger.exceptions.GeneralException;
import com.backbase.moviesdigger.exceptions.NotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
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
// api key is 3293300e
public class OMDBService {

    private final String OMDB_API_KEY = "3293300e";
    private final String OMDB_API_URL = "https://www.omdbapi.com/";

    public Movie getMovieByTitle(String movieName) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("t", movieName);

        try {
            ResponseEntity<String> omdbResponse = processOMDBAPI(
                    OMDB_API_URL,
                    HttpMethod.GET,
                    queryParams,
                    null,
                    String.class
            );

            Map<String, Object> responseMap = new ObjectMapper().readValue(omdbResponse.getBody(), new TypeReference<>() {
            });
            checkIfMovieFound(responseMap, movieName);

            return mapResponseToMovieEntity(responseMap);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Request to OMDB service failed, reason is {}", e.getMessage());
            throw new GeneralException("An unexpected condition was encountered when OMDB was requested, " +
                    "reason is " + e.getMessage());
        }
    }

    private void checkIfMovieFound(Map<String, Object> responseMap, String movieName) {
        String errorResponse = (String) responseMap.get("Error");
        if ("Movie not found!".equals(errorResponse)) {
            log.warn("Movie {} was not found in OMDB service and in movieDigger database", movieName);

            throw new NotFoundException("A movie " + movieName + " was not found");
        }
    }

    private Movie mapResponseToMovieEntity(Map<String, Object> responseMap) {
        log.debug("Trying to map response from OMDB service {} to Movie entity", responseMap.toString());

        Movie movie = new Movie();
        movie.setName((String) responseMap.get("Title"));

        String awards = (String) responseMap.get("Awards");
        if (awards != null && awards.equals("Best Picture")) {
            movie.setIsWinner(1);
        }
        movie.setIsWinner(0);

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

    private <T> ResponseEntity<T> processOMDBAPI(
            String path,
            HttpMethod method,
            MultiValueMap<String, String> queryParams,
            Object body, Class<T> responseType) throws RestClientException {
        log.debug("Trying to call OMDB api to {} movie with query params {}", method.toString(), queryParams.toString());

        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(path)
                .queryParam("apikey", OMDB_API_KEY)
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
