package com.backbase.moviesdigger.util;

import com.backbase.moviesdigger.client.spec.model.LoggedInUserResponse;
import com.backbase.moviesdigger.exceptions.UnauthorizedException;
import com.backbase.moviesdigger.utils.KeycloakMethodsUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KeycloakMethodsUtilTest {

    @Mock
    private RealmResource applicationRealm;
    @Mock
    private UsersResource usersResource;
    @InjectMocks
    private KeycloakMethodsUtil keycloakMethodsUtil;

    @Test
    void testBuildLoggedInUserResponseWithValidResponse() {
        String jsonResponse = "{\"access_token\":\"token\",\"expires_in\":300,\"refresh_token\":\"refreshToken\",\"refresh_expires_in\":1800}";
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn(jsonResponse);

        LoggedInUserResponse result = keycloakMethodsUtil.buildLoggedInUserResponse(mockResponse);
        assertNotNull(result);
        assertThat(result.getAccessToken(), is("token"));
        assertThat(result.getExpiresIn(), is(300));
        assertThat(result.getRefreshToken(), is("refreshToken"));
        assertThat(result.getRefreshExpiresIn(), is(1800));
    }

    @Test
    void testBuildLoggedInUserResponseWithInvalidResponse() {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("invalid json");

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () ->
                keycloakMethodsUtil.buildLoggedInUserResponse(mockResponse));
        assertThat(exception.getMessage(), is("Failed authorization for user, try again or speak with admin"));
    }

    @Test
    void testIsUserLoggedInTrueWithActiveSession() {
        String userName = "user";
        UserRepresentation user = mock(UserRepresentation.class);
        when(usersResource.search(userName)).thenReturn(List.of(user));
        when(user.getId()).thenReturn("userId");

        UserSessionRepresentation session = mock(UserSessionRepresentation.class);
        UserResource userResource = mock(UserResource.class);
        when(usersResource.get(any())).thenReturn(userResource);
        when(usersResource.get("userId").getUserSessions()).thenReturn(List.of(session));

        boolean isLoggedIn = keycloakMethodsUtil.isUserLoggedIn(applicationRealm, usersResource, userName);
        assertTrue(isLoggedIn);
    }

    @Test
    void testIsUserLoggedInFalseNoActiveSession() {
        String userName = "user";
        UserRepresentation user = mock(UserRepresentation.class);
        when(usersResource.search(userName)).thenReturn(List.of(user));
        when(user.getId()).thenReturn("userId");

        UserResource userResource = mock(UserResource.class);
        when(usersResource.get(any())).thenReturn(userResource);
        when(usersResource.get("userId").getUserSessions()).thenReturn(Collections.emptyList()); //no sessions

        boolean isLoggedIn = keycloakMethodsUtil.isUserLoggedIn(applicationRealm, usersResource, userName);
        assertFalse(isLoggedIn);
    }
}
