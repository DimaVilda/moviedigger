package com.backbase.moviesdigger.service.impl;

import com.backbase.moviesdigger.client.spec.model.MovieRatingRequestBody;
import com.backbase.moviesdigger.domain.Movie;
import com.backbase.moviesdigger.domain.Rating;
import com.backbase.moviesdigger.domain.User;
import com.backbase.moviesdigger.repository.RatingJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingPersistenceService {

    private final RatingJpaRepository ratingJpaRepository;

    @Transactional
    public void deleteMovieRating(String ratingId) {
        ratingJpaRepository.deleteById(ratingId);
    }

    public List<Rating> findMovieRatings(String movieId) {
        return ratingJpaRepository.findRatingsByMovieId(movieId);
    }

    public boolean isRatingWasAlreadyProvidedByUser(String userName) {
        return ratingJpaRepository.findRatingByUserName(userName);
    }

    @Transactional
    public void saveNewRatingValueForMovie(Movie movie, User user, Integer ratingValue) {
        Rating rating = new Rating();
        rating.setRatingValue(ratingValue);
        rating.setUser(user);
        rating.setMovie(movie);

        ratingJpaRepository.save(rating);
    }
}
