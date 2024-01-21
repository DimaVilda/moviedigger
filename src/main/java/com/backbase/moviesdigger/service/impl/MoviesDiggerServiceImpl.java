package com.backbase.moviesdigger.service.impl;

import com.backbase.moviesdigger.client.spec.model.MovieResponseBodyItem;
import com.backbase.moviesdigger.dtos.BearerTokenModel;
import com.backbase.moviesdigger.exceptions.ConflictException;
import com.backbase.moviesdigger.service.MoviesDiggerService;
import com.backbase.moviesdigger.client.spec.model.MovieRatingRequestBody;
import com.backbase.moviesdigger.client.spec.model.MovieRatingResponseBody;
import com.backbase.moviesdigger.utils.TokenMethodsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.backbase.moviesdigger.utils.consts.JwtClaimsConst.PREFERRED_USERNAME_CLAIM;

@Service
@Slf4j
@RequiredArgsConstructor
public class MoviesDiggerServiceImpl implements MoviesDiggerService {

    //private final MoviePersistenceService moviePersistenceService;
    private final RatingPersistenceService ratingPersistenceService;
    private final SyncService syncService;
    private final TokenMethodsUtil tokenMethodsUtil;
    private final BearerTokenModel tokenWrapper;

    @Override
    public List<MovieResponseBodyItem> getMovies(String movieName) {
        return syncService.getMovies(movieName);
    }

    @Override
    public MovieRatingResponseBody provideMovieRating(MovieRatingRequestBody movieRatingRequestBody) {
        String userNameFromClaim = tokenMethodsUtil.getUserTokenClaimValue(tokenWrapper.getToken(), PREFERRED_USERNAME_CLAIM);
        log.debug("Check if user {} already provided a rating before", userNameFromClaim);
        if (ratingPersistenceService.isRatingWasAlreadyProvidedByUser(userNameFromClaim)) {
            throw new ConflictException("User " + userNameFromClaim + " already provided rating for movie " +
                    movieRatingRequestBody.getMovieId() + " before." +
                    "To update rating, please contact admin");
        }
        return syncService.provideMovieRating(movieRatingRequestBody, userNameFromClaim);
    }

    @Override
    public void deleteMovieRating(String ratingId) {
        ratingPersistenceService.deleteMovieRating(ratingId);
        log.debug("Rating {} was successfully deleted", ratingId);
    }
}
