package com.backbase.moviesdigger.it.auth;

import com.backbase.moviesdigger.client.spec.model.LoggedInUserResponse;
import com.backbase.moviesdigger.client.spec.model.UserInformationRequestBody;
import com.backbase.moviesdigger.config.it.BaseIntegrationTestConfig;
import com.backbase.moviesdigger.config.it.IntegrationTest;
import com.backbase.moviesdigger.domain.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.backbase.moviesdigger.utils.consts.KeycloakConsts.APPLICATION_REALM;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@IntegrationTest
public class PostUserLoginIT extends BaseIntegrationTestConfig {

    private final static String CREATE_USER_URL = "/client-api/v1/users";
    private final static String LOGIN_USER_URL = "/client-api/v1/users/login";
    private final static String USER_NAME_CRED = "user";
    private final static String USER_PASSWORD_CRED = "123";

    @AfterEach
    void afterEach() {
        userJpaRepository.deleteAll();
        String realmUserId = keycloak.realm(APPLICATION_REALM).users().search(USER_NAME_CRED).get(0).getId();
        keycloak.realm(APPLICATION_REALM).users().get(realmUserId).remove();
    }

    @Test
    void shouldSuccessfullyLoginUser() {
        createUserInDbAndKeycloak();

        UserInformationRequestBody requestBody = new UserInformationRequestBody();
        requestBody.setUserName(USER_NAME_CRED);
        requestBody.setPassword(USER_PASSWORD_CRED);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(LOGIN_USER_URL)
                .then()
                .statusCode(201)
                .extract()
                .response();

        LoggedInUserResponse loggedInUserResponse = response.jsonPath().getObject("", LoggedInUserResponse.class);
        assertFalse(loggedInUserResponse.getAccessToken().isEmpty()); //meaning user sessions was created in keycloak
        assertFalse(loggedInUserResponse.getRefreshToken().isEmpty());
        assertFalse(keycloak.realm(APPLICATION_REALM).users().search(requestBody.getUserName()).isEmpty());

        String keycloakLoggedInUserName = keycloak.realm(APPLICATION_REALM).users()
                .search(requestBody.getUserName()).get(0).getUsername();
        assertThat(keycloakLoggedInUserName, is(USER_NAME_CRED));
    }

    @Test
    void shouldThrowConflictIfUserAlreadyLoggedIn() {
        createUserInDbAndKeycloak();
        loginUser(); //call login endpoint first time

        UserInformationRequestBody requestBody = new UserInformationRequestBody();
        requestBody.setUserName(USER_NAME_CRED);
        requestBody.setPassword(USER_PASSWORD_CRED);

         given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(LOGIN_USER_URL) //call login endpoint with same userName and password creds as before in loginUser()
                .then()
                .statusCode(409);
    }

    private void createUserInDbAndKeycloak() {
        UserInformationRequestBody requestBody = new UserInformationRequestBody();
        requestBody.setUserName(USER_NAME_CRED);
        requestBody.setPassword(USER_PASSWORD_CRED);

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(CREATE_USER_URL)
                .then()
                .statusCode(201);

        Optional<User> createdUser = userJpaRepository.findByNameIs(requestBody.getUserName());
        assertTrue(createdUser.isPresent());
    }

    private void loginUser() {
        UserInformationRequestBody requestBody = new UserInformationRequestBody();
        requestBody.setUserName(USER_NAME_CRED);
        requestBody.setPassword(USER_PASSWORD_CRED);

         given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(LOGIN_USER_URL)
                .then()
                .statusCode(201)
                .extract()
                .response();
    }
}
