package com.backbase.moviesdigger.it.auth;

import com.backbase.moviesdigger.client.spec.model.UserInformationRequestBody;
import com.backbase.moviesdigger.config.it.BaseIntegrationTestConfig;
import com.backbase.moviesdigger.config.it.IntegrationTest;
import com.backbase.moviesdigger.domain.User;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@IntegrationTest
public class PostCreateUserIT extends BaseIntegrationTestConfig {

    private final static String CREATE_USER_URL = "/client-api/v1/users";

    @Test
    void shouldSuccessfullyCreateUser() {
        UserInformationRequestBody requestBody = new UserInformationRequestBody();
        requestBody.setUserName("dima");
        requestBody.setPassword("123");

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(CREATE_USER_URL)
                .then()
                .statusCode(201);

        User createdUser = userJpaRepository.findByNameIs(requestBody.getUserName()).get();
        assertThat(createdUser.getName(), is(requestBody.getUserName()));
        userJpaRepository.deleteAll();
    }
}
