package com.backbase.moviesdigger.it.moviesdigger;

import com.backbase.moviesdigger.TestUtils;
import com.backbase.moviesdigger.config.it.BaseIntegrationTestConfig;
import com.backbase.moviesdigger.config.it.IntegrationTest;
import com.backbase.moviesdigger.domain.Movie;
import com.backbase.moviesdigger.domain.Rating;
import com.backbase.moviesdigger.domain.User;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static io.restassured.RestAssured.given;

@IntegrationTest
class DeleteMovieRatingByIdIT extends BaseIntegrationTestConfig {
    private final static String DELETE_RATING_URL = "/client-api/v1/movies/rating/{ratingId}";

    @Test
    void shouldSuccessfullyDeleteProvidedRating() {
        User user = TestUtils.createUserFixture("user", null);
        User savedUser = userJpaRepository.save(user);

        Movie movie = TestUtils.createMovieFixture("movie", 1, 1991, null, null);
        Movie savedMovie = movieJpaRepository.save(movie);

        Rating rating = TestUtils.createRatingFixture(2, savedUser, savedMovie);
        Rating savedRating = ratingJpaRepository.save(rating);

        given()
                .pathParam("ratingId", savedRating.getId())
                .when()
                .delete(DELETE_RATING_URL)
                .then()
                .statusCode(200);

        assertTrue(ratingJpaRepository.findAll().isEmpty());
    }

    @Test
    void shouldThrowNotFoundIfRatingDoesntExist() {
        given()
                .pathParam("ratingId", "466b8c0e-238e-46f3-9dba-dae01439bc26")
                .when()
                .delete(DELETE_RATING_URL)
                .then()
                .statusCode(404);
    }
}
