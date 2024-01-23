package com.backbase.moviesdigger.service;

import com.backbase.moviesdigger.domain.Movie;

public interface MovieProviderService {

    /**
     * Fetches movie details by its title.
     *
     * @param movieName the name of the movie to fetch.
     * @param year - movie's release year
     *
     * @return - {@link Movie} - object with the details.
     */
    Movie getMovieByTitleAndYearOptional(String movieName, Integer year);

    <T> T getMovieFieldByTitle(String movieName, String movieField, Class<T> className);
}
