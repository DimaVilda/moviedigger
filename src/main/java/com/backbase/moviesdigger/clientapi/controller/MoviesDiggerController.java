package com.backbase.moviesdigger.clientapi.controller;

import com.backbase.moviesdigger.client.spec.model.*;
import com.backbase.moviesdigger.service.MoviesDiggerService;
import com.backbase.moviesdigger.client.spec.api.MovieDiggerClientApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
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

    private final MoviesDiggerService moviesDiggerService;

    @Override
    @PreAuthorize("hasRole('" + REALM_USER_ROLE + "')")
    public ResponseEntity<MovieRatingResponseBody> getMovieRating(String movieName) { //TODO by id ?
        return null;
    }

    @Override
    @PreAuthorize("hasRole('" + REALM_USER_ROLE + "')")
    public ResponseEntity<List<MovieResponseBodyItem>> getMovies(String movieName) {
        log.debug("Trying to retrieve movies by provided movie name {}", movieName);

        return new ResponseEntity<>(moviesDiggerService.getMovies(movieName),HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<TopRatedMovieResponseBodyItem>> getTopRatedMovies(Integer page,
                                                                                 Integer pageSize,
                                                                                 String sortDirection) { //TODO implementing now
        log.debug("Trying to retrieve top rated movies for {} page of {} size, " +
                "ordered by box office in {} direction",page, pageSize, sortDirection);

        return new ResponseEntity<>(moviesDiggerService.getTopRatedMovies(
                page,
                pageSize,
                Sort.Direction.valueOf(sortDirection.toUpperCase())
        ),HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('" + REALM_USER_ROLE + "')")
    public ResponseEntity<List<MovieWinnerResponseBodyItem>> getWinner(String movieName) {
        return new ResponseEntity<>(Collections.emptyList(),HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasRole('" + REALM_USER_ROLE + "')")
    public ResponseEntity<MovieRatingResponseBody> provideMovieRating(MovieRatingRequestBody movieRatingRequestBody) {
        log.debug("Trying to provide a rating {} to movie {}",
                movieRatingRequestBody.getRating(),
                movieRatingRequestBody.getMovieId());

        return new ResponseEntity<>(moviesDiggerService.provideMovieRating(movieRatingRequestBody), HttpStatus.CREATED);
    }

    @Override
    @PreAuthorize("hasRole('" + REALM_ADMIN_ROLE + "')")
    public ResponseEntity<Void> deleteMovieRating(UUID ratingId) {
        log.debug("Trying to delete movie's rating by id {} ", ratingId);

        moviesDiggerService.deleteMovieRating(ratingId.toString());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
