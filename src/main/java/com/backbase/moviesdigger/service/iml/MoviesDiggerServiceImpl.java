package com.backbase.moviesdigger.service.iml;

import com.backbase.moviesdigger.service.MoviesDiggerService;
import com.backbase.moviesdigger.client.spec.model.MovieRatingRequestBody;
import com.backbase.moviesdigger.client.spec.model.MovieRatingResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MoviesDiggerServiceImpl implements MoviesDiggerService {

    private final MoviePersistenceService moviePersistenceService;

    private final RatingPersistenceService ratingPersistenceService;

    @Override
    public MovieRatingResponseBody provideMovieRating(String movieName, MovieRatingRequestBody movieRatingRequestBody) {
        return null;
    }

    @Override
    public void deleteMovieRating(String ratingId) {
        ratingPersistenceService.deleteMovieRating(ratingId);
    }
}
