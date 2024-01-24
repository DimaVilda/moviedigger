package com.backbase.moviesdigger.service.impl;

import com.backbase.moviesdigger.client.spec.model.MovieRatingRequestBody;
import com.backbase.moviesdigger.client.spec.model.MovieRatingResponseBody;
import com.backbase.moviesdigger.domain.Movie;
import com.backbase.moviesdigger.dtos.BearerTokenModel;
import com.backbase.moviesdigger.exceptions.ConflictException;
import com.backbase.moviesdigger.mappers.MovieRatingResponseBodyMapper;
import com.backbase.moviesdigger.utils.TokenMethodsUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static com.backbase.moviesdigger.utils.consts.JwtClaimsConst.PREFERRED_USERNAME_CLAIM;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MoviesDiggerServiceImplTest {
    @Mock
    private MoviePersistenceService moviePersistenceService;
    @Mock
    private RatingPersistenceService ratingPersistenceService;
    @Mock
    private UserPersistenceService userPersistenceService;
    @Mock
    private TokenMethodsUtil tokenMethodsUtil;
    @Mock
    private BearerTokenModel tokenWrapper;
    @Mock
    private MovieRatingResponseBodyMapper movieRatingResponseBodyMapper;
    @InjectMocks
    private MoviesDiggerServiceImpl moviesDiggerService;

    private static final String TEST_TOKEN = "someToken";
    private static final String TEST_USER_NAME = "user";

    @Test
    void shouldThrowConflictExceptionWhenRatingAlreadyProvided() {
        when(tokenWrapper.getToken()).thenReturn(TEST_TOKEN);
        when(tokenMethodsUtil.getUserTokenClaimValue(TEST_TOKEN, PREFERRED_USERNAME_CLAIM))
                .thenReturn(TEST_USER_NAME);
        when(ratingPersistenceService.isRatingWasAlreadyProvidedByUser(TEST_USER_NAME, "movieId"))
                .thenReturn(true);

        MovieRatingRequestBody request = new MovieRatingRequestBody();
        request.setMovieId("movieId");
        request.setRating(5);

        ConflictException exception = assertThrows(ConflictException.class, () ->
                moviesDiggerService.provideMovieRating(request));
        assertThat(exception.getMessage(), is("User " + TEST_USER_NAME + " already provided rating for movie " +
                request.getMovieId() + " before." +
                "To update rating, please contact admin"));
    }

    @Test
    void testSuccessfullyProvideMovieRating() {
        when(tokenWrapper.getToken()).thenReturn(TEST_TOKEN);
        when(tokenMethodsUtil.getUserTokenClaimValue(TEST_TOKEN, PREFERRED_USERNAME_CLAIM))
                .thenReturn(TEST_USER_NAME);
        when(ratingPersistenceService.isRatingWasAlreadyProvidedByUser(TEST_USER_NAME, "movieId"))
                .thenReturn(false);
        Movie movie = new Movie();
        movie.setId("movieId");
        when(moviePersistenceService.getMovieById(any())).thenReturn(movie);
        when(ratingPersistenceService.getMovieRatings(any())).thenReturn(Collections.emptyList());
        when(movieRatingResponseBodyMapper.toMovieResponseBodyItemModel(any())).thenReturn(new MovieRatingResponseBody());

        MovieRatingRequestBody request = new MovieRatingRequestBody();
        request.setMovieId("movieId");
        request.setRating(5);
        moviesDiggerService.provideMovieRating(request);

        verify(moviePersistenceService).getMovieById(any());
        verify(userPersistenceService).findByUserName(any());
        verify(ratingPersistenceService).saveNewRatingValueForMovie(any(), any(), any());
    }
}
