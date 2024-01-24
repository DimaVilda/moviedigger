package com.backbase.moviesdigger.it.moviesdigger;

import com.backbase.moviesdigger.TestUtils;
import com.backbase.moviesdigger.client.spec.model.MovieRatingRequestBody;
import com.backbase.moviesdigger.client.spec.model.MovieRatingResponseBody;
import com.backbase.moviesdigger.config.it.BaseIntegrationTestConfig;
import com.backbase.moviesdigger.config.it.IntegrationTest;
import com.backbase.moviesdigger.domain.Movie;
import com.backbase.moviesdigger.domain.Rating;
import com.backbase.moviesdigger.domain.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@IntegrationTest
public class PostMovieRatingIT extends BaseIntegrationTestConfig {

    private final static String PROVIDE_MOVIE_RATING_URL = "/client-api/v1/movies/rating";

    @Test
    void shouldSuccessfullyCreateRatingForMovie() {
        Movie movie = TestUtils.createMovieFixture("movie", 0, 2001,
                null, BigDecimal.valueOf(100));
        Movie savedMovie = movieJpaRepository.save(movie);

        User user = TestUtils.createUserFixture("dima", null); //username from hardcoded test access token
        userJpaRepository.save(user);

        MovieRatingRequestBody requestBody = new MovieRatingRequestBody();
        requestBody.setMovieId(savedMovie.getId());
        requestBody.setRating(9);

        Response response = given()
                .header("Authorization", "Bearer " + TestUtils.accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(PROVIDE_MOVIE_RATING_URL)
                .then()
                .statusCode(201)
                .extract()
                .response();

        Movie updatedMovie = movieJpaRepository.findById(savedMovie.getId()).get();
        MovieRatingResponseBody movieRatingResponseBody = response.jsonPath().getObject("", MovieRatingResponseBody.class);
        assertThat(movieRatingResponseBody.getName(), is(updatedMovie.getName()));
        assertThat(movieRatingResponseBody.getRating(), is(updatedMovie.getAvgRating().toString()));
    }

    @Test
    void shouldReturnConflictIfRatingWasAlreadyProvidedByUser() {
        Movie movie = TestUtils.createMovieFixture("movie", 0, 2001,
                BigDecimal.valueOf(9), BigDecimal.valueOf(100));
        Movie savedMovie = movieJpaRepository.save(movie);

        User user = TestUtils.createUserFixture("dima", null);
        User savedUser = userJpaRepository.save(user);

        Rating rating = TestUtils.createRatingFixture(9, savedUser, savedMovie);
        ratingJpaRepository.save(rating);

        MovieRatingRequestBody requestBody = new MovieRatingRequestBody();
        requestBody.setMovieId(savedMovie.getId());
        requestBody.setRating(9);

        given()
                .header("Authorization", "Bearer " + TestUtils.accessToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(PROVIDE_MOVIE_RATING_URL)
                .then()
                .statusCode(409);

        movieJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }
}
