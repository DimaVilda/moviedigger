package com.backbase.moviesdigger.it.auth;

import com.backbase.moviesdigger.TestUtils;
import com.backbase.moviesdigger.client.spec.model.UserInformationRequestBody;
import com.backbase.moviesdigger.config.it.BaseIntegrationTestConfig;
import com.backbase.moviesdigger.config.it.IntegrationTest;
import com.backbase.moviesdigger.domain.User;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
public class DeleteUserIT extends BaseIntegrationTestConfig {

    private final static String DELETE_USER_URL = "client-api/v1/users/{userName}";
    private final static String CREATE_USER_URL = "/client-api/v1/users";

    @Test
    void shouldSuccessfullyDeleteUser() {
        User createdUser = createUserInDbAndKeycloak();

        given()
                .pathParam("userName", createdUser.getName())
                .when()
                .delete(DELETE_USER_URL)
                .then()
                .statusCode(200);

        assertFalse(userJpaRepository.findByNameIs(createdUser.getName()).isPresent());
    }

    private User createUserInDbAndKeycloak() {
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

        Optional<User> createdUser = userJpaRepository.findByNameIs(requestBody.getUserName());
        assertTrue(createdUser.isPresent());
        return createdUser.get();
    }
}
