package com.backbase.moviesdigger.it;

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
        User user = new User();
        user.setName("user");
        User savedUser = userJpaRepository.save(user);

        Movie movie = new Movie();
        movie.setName("movie");
        movie.setIsWinner(1);
        Movie savedMovie = movieJpaRepository.save(movie);

        Rating rating = new Rating();
        rating.setRatingValue(2);
        rating.setUser(savedUser);
        rating.setMovie(savedMovie);
        Rating savedRating = ratingJpaRepository.save(rating);

        given()
                .pathParam("ratingId", savedRating.getId())
                .when()
                .delete(DELETE_RATING_URL)
                .then()
                .statusCode(200);

        assertTrue(ratingJpaRepository.findAll().isEmpty());
    }
}
