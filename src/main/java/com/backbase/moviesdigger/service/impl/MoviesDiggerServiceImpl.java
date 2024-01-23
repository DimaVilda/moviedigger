package com.backbase.moviesdigger.service.impl;

import com.backbase.moviesdigger.client.spec.model.*;
import com.backbase.moviesdigger.domain.Movie;
import com.backbase.moviesdigger.domain.Rating;
import com.backbase.moviesdigger.domain.User;
import com.backbase.moviesdigger.dtos.BearerTokenModel;
import com.backbase.moviesdigger.exceptions.ConflictException;
import com.backbase.moviesdigger.mappers.MovieRatingResponseBodyMapper;
import com.backbase.moviesdigger.service.MoviesDiggerService;
import com.backbase.moviesdigger.utils.TokenMethodsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.backbase.moviesdigger.utils.consts.JwtClaimsConst.PREFERRED_USERNAME_CLAIM;

@Service
@Slf4j
@RequiredArgsConstructor
public class MoviesDiggerServiceImpl implements MoviesDiggerService {

    private final MoviePersistenceService moviePersistenceService;
    private final RatingPersistenceService ratingPersistenceService;
    private final UserPersistenceService userPersistenceService;
    private final SyncService syncService;
    private final TokenMethodsUtil tokenMethodsUtil;
    private final BearerTokenModel tokenWrapper;

    private final MovieRatingResponseBodyMapper movieRatingResponseBodyMapper;

    @Override
    public List<MovieWinnerResponseBodyItem> getWinner(String movieName, Integer year) {
        return syncService.getWinner(movieName, year);
    }

    @Override
    public List<TopRatedMovieResponseBodyItem> getTopRatedMovies(Integer page,
                                                                 Integer pageSize,
                                                                 Sort.Direction sortDirection) {
        return syncService.getTopRatedMovies(page, pageSize, sortDirection);
    }

    @Override
    public List<MovieResponseBodyItem> getMovies(String movieName, Integer year) {
        return syncService.getMovies(movieName, year);
    }

    @Override
    public MovieRatingResponseBody provideMovieRating(MovieRatingRequestBody movieRatingRequestBody) {
        String userNameFromClaim = tokenMethodsUtil.getUserTokenClaimValue(tokenWrapper.getToken(), PREFERRED_USERNAME_CLAIM);
        log.debug("Check if user {} already provided a rating before", userNameFromClaim);
        if (ratingPersistenceService.isRatingWasAlreadyProvidedByUser(userNameFromClaim, movieRatingRequestBody.getMovieId())) {
            throw new ConflictException("User " + userNameFromClaim + " already provided rating for movie " +
                    movieRatingRequestBody.getMovieId() + " before." +
                    "To update rating, please contact admin");
        }
        return provideMovieRating(movieRatingRequestBody, userNameFromClaim);
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
        List<Rating> movieRatings = ratingPersistenceService.getMovieRatings(movie.getId());
        if (movieRatings.isEmpty()) {
            movie.setAvgRating(BigDecimal.ZERO);
            return moviePersistenceService.saveMovie(movie);
        }
        BigDecimal total = movieRatings.stream()
                .map(Rating::getRatingValue)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal average = total.divide(BigDecimal.valueOf(movieRatings.size()), 2, RoundingMode.HALF_UP);
        movie.setAvgRating(average);
        return moviePersistenceService.saveMovie(movie);
    }

    @Override
    public void deleteMovieRating(String ratingId) {
        Movie movie = moviePersistenceService.getMovieByRatingId(ratingId);
        ratingPersistenceService.deleteMovieRating(ratingId);
        calculateAverageRatingForMovie(movie);
        log.debug("Rating {} was successfully deleted", ratingId);
    }
}
