package com.backbase.moviesdigger.it.moviesdigger;

import com.backbase.moviesdigger.TestUtils;
import com.backbase.moviesdigger.client.spec.model.MovieResponseBodyItem;
import com.backbase.moviesdigger.config.it.BaseIntegrationTestConfig;
import com.backbase.moviesdigger.config.it.IntegrationTest;
import com.backbase.moviesdigger.domain.Movie;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
public class GetMoviesByNameAndYearIT extends BaseIntegrationTestConfig {

    private final static String GET_MOVIES_URL = "/client-api/v1/movies/{movieName}";

    @Test
    void shouldSuccessfullyProvideMoviesByNameAndYearWithUpdateBoxOffice() {
        Movie movie = TestUtils.createMovieFixture("Lord of the Rings", 0, 2001,
                null, null);
        Movie savedMovie = movieJpaRepository.save(movie);

        Response response = given()
                .pathParam("movieName", savedMovie.getName())
                .queryParam("year", savedMovie.getReleaseYear())
                .when()
                .get(GET_MOVIES_URL)
                .then()
                .statusCode(200)
                .extract()
                .response();

        List<MovieResponseBodyItem> responseBody = response.jsonPath().getList("", MovieResponseBodyItem.class);
        assertFalse(responseBody.isEmpty());
        assertThat(responseBody.size(), is(1));

        MovieResponseBodyItem responseItem = responseBody.get(0);
        assertThat(responseItem.getId(), is(savedMovie.getId()));
        assertThat(responseItem.getName(), is(savedMovie.getName()));
        assertFalse(responseItem.getBoxOffice().isEmpty());
        assertThat(responseItem.getYear(), is(savedMovie.getReleaseYear()));
    }

    @Test
    void shouldThrowNotFoundIfMovieDoesntExist() {
        given()
                .pathParam("movieName", "non-existent-movie")
                .when()
                .get(GET_MOVIES_URL)
                .then()
                .statusCode(404);
    }
}
