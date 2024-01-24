package com.backbase.moviesdigger.it.moviesdigger;

import com.backbase.moviesdigger.TestUtils;
import com.backbase.moviesdigger.client.spec.model.MovieWinnerResponseBodyItem;
import com.backbase.moviesdigger.config.it.BaseIntegrationTestConfig;
import com.backbase.moviesdigger.config.it.IntegrationTest;
import com.backbase.moviesdigger.domain.Movie;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
public class GetMovieWinnerByNameAndYearIT extends BaseIntegrationTestConfig {

    private final static String GET_MOVIE_WINNER_URL = "/client-api/v1/movies/{movieName}/iswon";

    @Test
    void shouldSuccessfullyGetBestPictureWinner() {
        Movie movieWinner = TestUtils.createMovieFixture("movieWinner", 1, 2001,
                BigDecimal.valueOf(10), BigDecimal.valueOf(100));
        Movie savedMovie = movieJpaRepository.save(movieWinner);

        Response response = given()
                .pathParam("movieName", movieWinner.getName())
                .when()
                .get(GET_MOVIE_WINNER_URL)
                .then()
                .statusCode(200)
                .extract()
                .response();

        List<MovieWinnerResponseBodyItem> responseBody = response.jsonPath().getList("", MovieWinnerResponseBodyItem.class);
        assertFalse(responseBody.isEmpty());
        assertThat(responseBody.size(), is(1));

        MovieWinnerResponseBodyItem movieWinnerResponseBodyItem = responseBody.get(0);
        assertThat(movieWinnerResponseBodyItem.getId(), is(savedMovie.getId()));
        assertThat(movieWinnerResponseBodyItem.getName(), is(savedMovie.getName()));
    }

    @Test
    void shouldReturnEmptyResponseIfNoWinners() {
        Movie movieLoser = TestUtils.createMovieFixture("movieLoser", 0, 2001,
                BigDecimal.valueOf(10), BigDecimal.valueOf(100));
        movieJpaRepository.save(movieLoser);

        Response response = given()
                .pathParam("movieName", movieLoser.getName())
                .when()
                .get(GET_MOVIE_WINNER_URL)
                .then()
                .statusCode(200)
                .extract()
                .response();

        List<MovieWinnerResponseBodyItem> responseBody = response.jsonPath().getList("", MovieWinnerResponseBodyItem.class);
        assertTrue(responseBody.isEmpty());
    }
}
