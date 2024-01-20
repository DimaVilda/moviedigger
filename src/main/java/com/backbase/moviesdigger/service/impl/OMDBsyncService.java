package com.backbase.moviesdigger.service.impl;

import com.backbase.moviesdigger.client.spec.model.MovieRatingRequestBody;
import com.backbase.moviesdigger.domain.Movie;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
// api key is 3293300e
public class OMDBsyncService {

    private final MoviePersistenceService moviePersistenceService;

    private final RatingPersistenceService ratingPersistenceService;

    private final String OMDB_API_KEY = "3293300e";

    private final String OMDB_API_URL = "https://www.omdbapi.com/";

    public List<MovieRatingRequestBody> getMovies(String movieName) {
       if (moviePersistenceService.getMoviesByName(movieName).isEmpty()) {
           findMoviesInOMDBbyName(movieName);
       }
       return null;
    }

    private List<Movie> findMoviesInOMDBbyName(String movieName) {
        RestTemplate restTemplate = new RestTemplate();
        String url = OMDB_API_URL + "?apikey=" + OMDB_API_KEY + "&t=" + movieName;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String omdbResponse  = restTemplate.getForObject(url, String.class);
            Map<String, Object> responseMap = objectMapper.readValue(omdbResponse, new TypeReference<Map<String, Object>>() {});
            saveFoundMovieToLocalDb(responseMap);
            // Process the response and convert it to a List<Movie>
            // Note: You'll need to create a class (e.g., OmdbApiResponse) to map the response
            return convertResponseToMovies(response);
        } catch (Exception e) {
            // Handle exceptions, possibly related to reaching the API request limit
            throw new RuntimeException("Error querying OMDb API", e);
        }
    }

    private List<Movie> saveFoundMovieToLocalDb(Map<String, Object> responseMap) {
        Movie movie = new Movie();
        movie.setName((String) responseMap.get("Title"));
        movie.setIsWinner(0); // Set default or based on some condition
        // Set other properties as needed
        return List.of(movie);
    }
    private List<Movie> convertResponseToMovies(Object response) {
       return null;
    }
    public void provideMovieRating(MovieRatingRequestBody movieRatingRequestBody) {
        isRatingExistsInDb(movieRatingRequestBody);
    }

    private boolean isRatingExistsInDb(MovieRatingRequestBody movieRatingRequestBody) {
       return ratingPersistenceService.isRatingExistsInDb(movieRatingRequestBody);
    }
}
