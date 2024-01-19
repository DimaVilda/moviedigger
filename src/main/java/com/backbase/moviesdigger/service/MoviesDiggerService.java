package com.backbase.moviesdigger.service;


import com.backbase.moviesdigger.client.spec.model.MovieRatingRequestBody;
import com.backbase.moviesdigger.client.spec.model.MovieRatingResponseBody;

/**
 * Service responsible for interacting movies and it's rating
 */
public interface MoviesDiggerService {

    /**
     *
     * @param movieName
     * @param movieRatingRequestBody
     * @return
     */
    MovieRatingResponseBody provideMovieRating(String movieName, MovieRatingRequestBody movieRatingRequestBody);
    /**
     *
     * @param ratingId - id of certain rating to delete
     */
    void deleteMovieRating(String ratingId);
}
