package com.backbase.moviesdigger.service.impl;

import com.backbase.moviesdigger.client.spec.model.MovieRatingRequestBody;
import com.backbase.moviesdigger.client.spec.model.MovieRatingResponseBody;
import com.backbase.moviesdigger.client.spec.model.MovieResponseBodyItem;
import com.backbase.moviesdigger.domain.Movie;
import com.backbase.moviesdigger.domain.Rating;
import com.backbase.moviesdigger.domain.User;
import com.backbase.moviesdigger.mappers.MovieRatingResponseBodyMapper;
import com.backbase.moviesdigger.mappers.MovieResponseBodyItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncService {

    private final MoviePersistenceService moviePersistenceService;
    private final RatingPersistenceService ratingPersistenceService;
    private final UserPersistenceService userPersistenceService;
    private final OMDBService omdbService;

    private final MovieResponseBodyItemMapper movieResponseBodyItemMapper;
    private final MovieRatingResponseBodyMapper movieRatingResponseBodyMapper;

    public List<MovieResponseBodyItem> getMovies(String movieName) {
        List<Movie> moviesListFromDb = moviePersistenceService.getMoviesByName(movieName);
        if (moviesListFromDb.isEmpty()) {
            return Collections.singletonList(
                    movieResponseBodyItemMapper.toMovieResponseBodyItemModel(findMovieInOMDBbyName(movieName))
            );
        }
        List<Movie> updatedMovies = updateMoviesWithBoxOfficeValue(moviesListFromDb);
        return movieResponseBodyItemMapper.toMovieResponseBodyItemList(
                updatedMovies.isEmpty() ? moviesListFromDb : updatedMovies);
    }

    private Movie findMovieInOMDBbyName(String movieName) { // here the OMDB getMovie contract gives only one movie in response
        log.debug("Movie {} was not found in a local store so let's try to get it from OMDB service", movieName);

        Movie movieFromOMDB = omdbService.getMovieByTitle(movieName);
        return moviePersistenceService.saveMovie(movieFromOMDB);
    }

    private List<Movie> updateMoviesWithBoxOfficeValue(List<Movie> moviesListFromDb) {
        log.debug("Some of movies from local store might " +
                "not have boxOffice value, let's check it and update them from OMDB service");

        return moviesListFromDb.stream()
                .filter(movie -> Objects.isNull(movie.getOfficeBoxValue())) // Filter movies with null officeBoxValue
                .map(movie -> omdbService.getMovieByTitle(movie.getName()))
                .collect(Collectors.toList());
    }

    public MovieRatingResponseBody provideMovieRating(MovieRatingRequestBody movieRatingRequestBody, String userName) {
        String movieId = movieRatingRequestBody.getMovieId();
        Integer ratingValue = movieRatingRequestBody.getRating();
        log.debug("Trying to save new rating {} provided by user {} for movie {}", ratingValue, userName, movieId);

        Movie movie = moviePersistenceService.getMovieById(movieId);
        User user = userPersistenceService.findByUserName(userName);
        ratingPersistenceService.saveNewRatingValueForMovie(movie, user, ratingValue);

        return movieRatingResponseBodyMapper.toMovieResponseBodyItemModel(calculateAverageRatingForMovie(movie));
    }

    private Movie calculateAverageRatingForMovie(Movie movie) {
        List<Rating> movieRatings = ratingPersistenceService.findMovieRatings(movie.getId());
        BigDecimal total = movieRatings.stream()
                .map(Rating::getRatingValue)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal average = total.divide(BigDecimal.valueOf(movieRatings.size()), 2, RoundingMode.HALF_UP);
        movie.setAvgRating(average);
        return moviePersistenceService.saveMovie(movie);
    }
}
