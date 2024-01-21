package com.backbase.moviesdigger.service;

import com.backbase.moviesdigger.client.spec.model.MovieRatingRequestBody;
import com.backbase.moviesdigger.client.spec.model.MovieRatingResponseBody;
import com.backbase.moviesdigger.client.spec.model.MovieResponseBodyItem;

import java.util.List;

/**
 * Service responsible for interacting movies and it's rating
 */
public interface MoviesDiggerService {

    /**
     *
     * @param movieName - The movie's name to provide
     *
     * @return - {@link MovieResponseBodyItem} - list of movies similar to provided movieName in request
     */
    List<MovieResponseBodyItem> getMovies(String movieName);

    /**
     *
     * @param movieRatingRequestBody - a request body contains a rating value to a certain movie by its id
     *
     * @return - {@link MovieRatingResponseBody} - a movie name with recalculated rating
     */
    MovieRatingResponseBody provideMovieRating(MovieRatingRequestBody movieRatingRequestBody);
    /**
     *
     * @param ratingId - id of certain rating to delete
     */
    void deleteMovieRating(String ratingId);
}
