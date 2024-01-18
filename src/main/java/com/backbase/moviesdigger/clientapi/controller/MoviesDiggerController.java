package com.backbase.moviesdigger.clientapi.controller;

import com.backbase.moviesdigger.client.spec.api.MovieDiggerClientApi;
import com.backbase.moviesdigger.client.spec.model.MovieRatingResponseBody;
import com.backbase.moviesdigger.client.spec.model.MovieWinnerResponseBodyItem;
import com.backbase.moviesdigger.client.spec.model.ProvideMovieRatingRequest;
import com.backbase.moviesdigger.client.spec.model.TopRatedMovieResponseBodyItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.backbase.moviesdigger.utils.consts.KeycloakConsts.REALM_ADMIN_ROLE;
import static com.backbase.moviesdigger.utils.consts.KeycloakConsts.REALM_USER_ROLE;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MoviesDiggerController implements MovieDiggerClientApi {

    @Override
    @PreAuthorize("hasRole('" + REALM_USER_ROLE + "')")
    public ResponseEntity<MovieRatingResponseBody> getMovieRating(String movieName) {
        return null;
    }

    @Override
    @PreAuthorize("hasRole('" + REALM_USER_ROLE + "')")
    public ResponseEntity<List<TopRatedMovieResponseBodyItem>> getTopRatedMovies(String sortDirection) {
        return null;
    }

    @Override
    @PreAuthorize("hasRole('" + REALM_USER_ROLE + "')")
    public ResponseEntity<List<MovieWinnerResponseBodyItem>> getWinner(String movieName) {
        return new ResponseEntity<>(Collections.emptyList(),HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('" + REALM_USER_ROLE + "')")
    public ResponseEntity<MovieRatingResponseBody> provideMovieRating(String movieName, ProvideMovieRatingRequest provideMovieRatingRequest) {
        return null;
    }

    @Override
    @PreAuthorize("hasRole('" + REALM_ADMIN_ROLE + "')")
    public ResponseEntity<Void> deleteMovieRating(UUID ratingId) {
        return null;
    }
}
