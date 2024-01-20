package com.backbase.moviesdigger.service.impl;

import com.backbase.moviesdigger.client.spec.model.MovieRatingRequestBody;
import com.backbase.moviesdigger.repository.RatingJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingPersistenceService {

    private final RatingJpaRepository ratingJpaRepository;

    public void deleteMovieRating(String ratingId) {
        ratingJpaRepository.deleteById(ratingId);

        log.debug("Rating {} was successfully deleted", ratingId);
    }

    public boolean isRatingExistsInDb(MovieRatingRequestBody movieRatingRequestBody) {
        // ratingJpaRepository.
        return true;
    }
}
