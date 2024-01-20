package com.backbase.moviesdigger.service.impl;

import com.backbase.moviesdigger.client.spec.model.MovieResponseBodyItem;
import com.backbase.moviesdigger.service.MoviesDiggerService;
import com.backbase.moviesdigger.client.spec.model.MovieRatingRequestBody;
import com.backbase.moviesdigger.client.spec.model.MovieRatingResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MoviesDiggerServiceImpl implements MoviesDiggerService {

    private final MoviePersistenceService moviePersistenceService;

    private final RatingPersistenceService ratingPersistenceService;

    private final OMDBsyncService omdbSyncService;

    @Override
    public List<MovieResponseBodyItem> getMovies(String movieName) {
        omdbSyncService.getMovies(movieName);
        return null;
    }

    @Override
    public MovieRatingResponseBody provideMovieRating(MovieRatingRequestBody movieRatingRequestBody) {
        omdbSyncService.provideMovieRating(movieRatingRequestBody);
        return null;
    }

    @Override
    public void deleteMovieRating(String ratingId) {
        ratingPersistenceService.deleteMovieRating(ratingId);
    }
}
