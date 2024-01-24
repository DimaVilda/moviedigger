package com.backbase.moviesdigger.service.impl;

import com.backbase.moviesdigger.client.spec.model.AccessTokenResponse;
import com.backbase.moviesdigger.client.spec.model.LoggedInUserResponse;
import com.backbase.moviesdigger.client.spec.model.UserInformationRequestBody;
import com.backbase.moviesdigger.exceptions.ConflictException;
import com.backbase.moviesdigger.exceptions.NotFoundException;
import com.backbase.moviesdigger.exceptions.UnauthorizedException;
import com.backbase.moviesdigger.utils.KeycloakMethodsUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpResponse;
import java.util.List;

import static com.backbase.moviesdigger.utils.consts.KeycloakConsts.REALM_USER_ROLE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private Keycloak keycloak;
    @Mock
    private KeycloakService keycloakService;
    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;
    @Mock
    private UserPersistenceService userPersistenceService;
    @Mock
    private KeycloakMethodsUtil keycloakMethodsUtil;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private HttpResponse<String> mockHttpResponse;

    private static final String USER_NAME_CRED = "user";
    private static final String USER_PASSWORD_CRED = "password";

    //createUser
    @Test
    void shouldCreateUserInKeycloakAndAssignUserRole() {
        when(keycloak.realm(any())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);

        UserInformationRequestBody requestBody = new UserInformationRequestBody();
        requestBody.setUserName(USER_NAME_CRED);
        requestBody.setPassword(USER_PASSWORD_CRED);

        doNothing().when(keycloakService).assignRealmRoleForUser(keycloak, usersResource, null, REALM_USER_ROLE);
        userService.createUser(requestBody);

        verify(userPersistenceService).saveUser(USER_NAME_CRED);
        verify(keycloakService).createUserInKeycloak(usersResource, USER_NAME_CRED, USER_PASSWORD_CRED);
        verify(keycloakService).assignRealmRoleForUser(keycloak, usersResource, null, REALM_USER_ROLE);
    }

    @Test
    void shouldThrowConflictAndDontSaveIfUserAlreadyCreated() {
        when(keycloak.realm(any())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);

        UserInformationRequestBody requestBody = new UserInformationRequestBody();
        requestBody.setUserName(USER_NAME_CRED);
        requestBody.setPassword(USER_PASSWORD_CRED);

        UserRepresentation existingUser = new UserRepresentation();
        existingUser.setUsername(USER_NAME_CRED);
        when(usersResource.search(USER_NAME_CRED)).thenReturn(List.of(existingUser));

        ConflictException exception = assertThrows(ConflictException.class, () -> userService.createUser(requestBody));
        assertThat(exception.getMessage(), is("A user " + requestBody.getUserName() + " is already exist."));
        verifyNoInteractions(keycloakService);
    }

    //deleteUser
    @Test
    void shouldDeleteUserFromKeycloakAndDb() {
        when(keycloak.realm(any())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);

        UserRepresentation existingUser = new UserRepresentation();
        existingUser.setUsername(USER_NAME_CRED);
        when(usersResource.search(USER_NAME_CRED)).thenReturn(List.of(existingUser));

        userService.deleteUser(USER_NAME_CRED);

        verify(keycloakService).deleteUserFromKeycloak(usersResource, USER_NAME_CRED);
        verify(userPersistenceService).deleteUser(USER_NAME_CRED);
    }

    @Test
    void shouldThrowNotFoundIfUserToDeleteIsNotFound() {
        when(keycloak.realm(any())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.deleteUser(USER_NAME_CRED));
        assertThat(exception.getMessage(), is("A user " + USER_NAME_CRED + " was not found in keycloak. " +
                "Try to create user again or contact tech support."));
        verifyNoInteractions(keycloakService);
        verifyNoInteractions(userPersistenceService);
    }

    //login
    @Test
    void shouldLoginUserInKeycloak() {
        when(keycloak.realm(any())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);

        UserInformationRequestBody requestBody = new UserInformationRequestBody();
        requestBody.setUserName(USER_NAME_CRED);
        requestBody.setPassword(USER_PASSWORD_CRED);

        when(userPersistenceService.isUserCreated(USER_NAME_CRED)).thenReturn(true);

        UserRepresentation existingUser = new UserRepresentation();
        existingUser.setUsername(USER_NAME_CRED);
        when(usersResource.search(USER_NAME_CRED)).thenReturn(List.of(existingUser));
        when(keycloakMethodsUtil.getUserTokensByUsernameAndPassword(anyString(), anyString()))
                .thenReturn(mockHttpResponse);

        userService.login(requestBody);
        verify(keycloakMethodsUtil).getUserTokensByUsernameAndPassword(USER_NAME_CRED, USER_PASSWORD_CRED);
        verify(keycloakMethodsUtil).buildLoggedInUserResponse(any());
    }

    @Test
    void shouldThrowUnauthorizedLoginIfUserNotCreatedInDb() {
        when(keycloak.realm(any())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);

        UserInformationRequestBody requestBody = new UserInformationRequestBody();
        requestBody.setUserName(USER_NAME_CRED);
        requestBody.setPassword(USER_PASSWORD_CRED);

        when(userPersistenceService.isUserCreated(USER_NAME_CRED)).thenReturn(false);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> userService.login(requestBody));
        assertThat(exception.getMessage(), is("Incorrect username or password"));
        verifyNoInteractions(keycloakMethodsUtil);
        verifyNoInteractions(keycloakMethodsUtil);
    }

    @Test
    void shouldThrowNotFoundIfUserToLigonNotCreatedInKeycloak() {
        when(keycloak.realm(any())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);

        UserInformationRequestBody requestBody = new UserInformationRequestBody();
        requestBody.setUserName(USER_NAME_CRED);
        requestBody.setPassword(USER_PASSWORD_CRED);
        when(userPersistenceService.isUserCreated(USER_NAME_CRED)).thenReturn(true);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.login(requestBody));
        assertThat(exception.getMessage(), is("A user " + USER_NAME_CRED + " was not found in keycloak. " +
                "Try to create user again or contact tech support."));
        verifyNoInteractions(keycloakMethodsUtil);
        verifyNoInteractions(keycloakMethodsUtil);
    }

    //getAccessToken
    @Test
    void shouldSuccessFullyGetAccessToken() {
        String accessTokenTest = "access";
        String refreshTokenTest = "refresh";

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(keycloakMethodsUtil.getUserAccessTokenByRefreshToken(refreshTokenTest))
                .thenReturn(mockHttpResponse);

        LoggedInUserResponse response = new LoggedInUserResponse();
        response.setAccessToken("newAccess");
        response.setRefreshToken("newRefresh");
        when(keycloakMethodsUtil.buildLoggedInUserResponse(any())).thenReturn(response);
        AccessTokenResponse newTokenResponse = userService.getAccessToken(refreshTokenTest, accessTokenTest);

        assertThat(newTokenResponse.getAccessToken(), is(response.getAccessToken()));
        verify(keycloakMethodsUtil).revokePreviousAccessToken(any());
        verify(keycloakMethodsUtil).buildLoggedInUserResponse(any());
    }

    @Test
    void shouldThrowUnauthorizedExceptionOnGetAccessToken() {
        String accessTokenTest = "access";
        String refreshTokenTest = "refresh";

        when(mockHttpResponse.statusCode()).thenReturn(401);
        when(keycloakMethodsUtil.getUserAccessTokenByRefreshToken(refreshTokenTest))
                .thenReturn(mockHttpResponse);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
                userService.getAccessToken(refreshTokenTest, accessTokenTest));
        assertThat(exception.getMessage(), is("A user is not authorized! Please, log in."));
    }

}
