package com.backbase.moviesdigger.service;

import com.backbase.moviesdigger.domain.Movie;

public interface MovieProviderService {

    /**
     * Fetches movie details by its title.
     *
     * @param movieName The name of the movie to fetch.
     *
     * @return - {@link Movie} - object with the details.
     */
    Movie getMovieByTitle(String movieName);

    <T> T getMovieFieldByTitle(String movieName, String movieField, Class<T> className);
}
