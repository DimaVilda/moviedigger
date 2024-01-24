package com.backbase.moviesdigger.it.moviesdigger;

import com.backbase.moviesdigger.TestUtils;
import com.backbase.moviesdigger.client.spec.model.MovieResponseBodyItem;
import com.backbase.moviesdigger.client.spec.model.TopRatedMovieResponseBodyItem;
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

@IntegrationTest
public class GetTopRatedMoviesIT extends BaseIntegrationTestConfig {

    private final static String GET_TOP_RATED_MOVIES_URL = "/client-api/v1/movies/top-rated";

    @Test
    void shouldSuccessfullyGetTopRatedMoviesSortedDESCbyBoxOfficeValue() {
        Movie movie1 = TestUtils.createMovieFixture("movie1", 0, 2001,
                BigDecimal.valueOf(10), BigDecimal.valueOf(100));
        Movie movie2 = TestUtils.createMovieFixture("movie2", 0, 2001,
                BigDecimal.valueOf(10), BigDecimal.valueOf(200));
        Movie movie3 = TestUtils.createMovieFixture("movie3", 0, 2001,
                BigDecimal.valueOf(1), BigDecimal.valueOf(300));
        Movie movie4 = TestUtils.createMovieFixture("movie4", 0, 2001,
                BigDecimal.valueOf(10), BigDecimal.valueOf(0));
        movieJpaRepository.saveAll(List.of(movie1, movie2, movie3, movie4));

        Response response = given()
                .queryParam("page", 1)
                .queryParam("page-size", 3)
                .queryParam("sort-direction", "DESC")
                .when()
                .get(GET_TOP_RATED_MOVIES_URL)
                .then()
                .statusCode(200)
                .extract()
                .response();

        List<TopRatedMovieResponseBodyItem> responseBody = response.jsonPath().getList("", TopRatedMovieResponseBodyItem.class);
        assertFalse(responseBody.isEmpty());
        assertThat(responseBody.size(), is(3)); //only 3 in response cause page-size was limited to 3

        assertThat(responseBody.get(0).getName(), is(movie2.getName())); //sorted desc by rating and then my box office
        assertThat(responseBody.get(1).getName(), is(movie1.getName()));
        assertThat(responseBody.get(2).getName(), is(movie4.getName()));
    }
}
