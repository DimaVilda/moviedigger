package com.backbase.moviesdigger.service.impl;

import com.backbase.moviesdigger.service.UserService;
import com.backbase.moviesdigger.client.spec.model.AccessTokenResponse;
import com.backbase.moviesdigger.client.spec.model.LoggedInUserResponse;
import com.backbase.moviesdigger.client.spec.model.UserInformationRequestBody;
import com.backbase.moviesdigger.exceptions.ConflictException;
import com.backbase.moviesdigger.exceptions.NotFoundException;
import com.backbase.moviesdigger.exceptions.UnauthorizedException;
import com.backbase.moviesdigger.dtos.BearerTokenModel;
import com.backbase.moviesdigger.utils.KeycloakMethodsUtil;
import com.backbase.moviesdigger.utils.TokenMethodsUtil;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.http.HttpResponse;
import java.util.*;

import static com.backbase.moviesdigger.utils.consts.JwtClaimsConst.PREFERRED_USERNAME_CLAIM;
import static com.backbase.moviesdigger.utils.consts.KeycloakConsts.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Keycloak keycloak;
    private final KeycloakService keycloakService;
    private final UserPersistenceService userPersistenceService;
    private final KeycloakMethodsUtil keycloakMethodsUtil;
    private final TokenMethodsUtil tokenMethodsUtil;
    private final BearerTokenModel tokenWrapper;

    @Override
    @Transactional
    public void createUser(UserInformationRequestBody userInformationRequestBody) {
        String userName = userInformationRequestBody.getUserName();
        String userPassword = userInformationRequestBody.getPassword();
        userPersistenceService.saveUser(userName);

        UsersResource usersResource = keycloak.realm(APPLICATION_REALM).users();
        if (isUserExists(usersResource, userName)) {
            throw new ConflictException("A user " + userName + " is already exist.");
        }
        Response responseFromKeycloak =
                keycloakService.createUserInKeycloak(usersResource, userName, userPassword);
        keycloakService.assignRealmRoleForUser(keycloak, usersResource, responseFromKeycloak, REALM_USER_ROLE);
    }

    @Override
    public void deleteUser(String userName) {
        UsersResource usersResource = keycloak.realm(APPLICATION_REALM).users();
        if (!isUserExists(usersResource, userName)) {
            throw new NotFoundException("A user " + userName + " was not found in keycloak. " +
                    "Try to create user again or contact tech support.");
        }
        keycloakService.deleteUserFromKeycloak(usersResource, userName);
        userPersistenceService.deleteUser(userName);
    }

    @Override
    public LoggedInUserResponse login(UserInformationRequestBody loggedInUserInformation) {
        String userName = loggedInUserInformation.getUserName();
        String userPassword = loggedInUserInformation.getPassword();
        UsersResource usersResource = keycloak.realm(APPLICATION_REALM).users();

        if (!userPersistenceService.isUserCreated(userName)) { //check if a such user was created
            throw new UnauthorizedException("Incorrect username or password");
        }
        if (!isUserExists(usersResource, userName)) { //if user does not exist in keycloak
            throw new NotFoundException("A user " + userName + " was not found in keycloak. " +
                    "Try to create user again or contact tech support.");
        }
        return loginUser(usersResource, userName, userPassword);
    }

    @Override
    public void endSession() {
        String usernameFromClaim = tokenMethodsUtil.getUserTokenClaimValue(tokenWrapper.getToken(), PREFERRED_USERNAME_CLAIM);
        UsersResource usersResource = keycloak.realm(APPLICATION_REALM).users();
        keycloakService.endUserSession(keycloak.realm(APPLICATION_REALM), usersResource, usernameFromClaim);
    }

    @Override
    public AccessTokenResponse getAccessToken(String refreshToken, String previousAccessToken) {
        HttpResponse<String> tokensResponse =
                keycloakMethodsUtil.getUserAccessTokenByRefreshToken(refreshToken); //is response not 200 - user have to log in
        if (tokensResponse.statusCode() != 200) { //in case a refresh token was expired as well
            throw new UnauthorizedException("A user is not authorized! Please, log in.");
        }
        keycloakMethodsUtil.revokePreviousAccessToken(previousAccessToken); // revoke prev access token to prevent multiple access tokens for one user

        LoggedInUserResponse refreshTokenToResponse =
                keycloakMethodsUtil.buildLoggedInUserResponse(tokensResponse);
        return new AccessTokenResponse()
                .accessToken(refreshTokenToResponse.getAccessToken())
                .expiresIn(refreshTokenToResponse.getExpiresIn());
    }

    private LoggedInUserResponse loginUser(UsersResource usersResource, String userName, String userPassword) {
        if (keycloakMethodsUtil.isUserLoggedIn(keycloak.realm(APPLICATION_REALM), usersResource, userName)) { //check if user has sessions and limit them
            throw new ConflictException("A user " + userName + " is already logged in.");
        }
        HttpResponse<String> tokensResponse =
                keycloakMethodsUtil.getUserTokensByUsernameAndPassword(userName, userPassword);
        validateTokensResponse(tokensResponse, userName);

        return keycloakMethodsUtil.buildLoggedInUserResponse(tokensResponse);
    }

    private void validateTokensResponse(HttpResponse<String> tokensResponse, String userName) {
        if (tokensResponse == null) {
            throw new UnauthorizedException("Failed authorization for user " +
                    userName + ", try again or contact tech support.");
        }
        if (tokensResponse.statusCode() == 401) {
            throw new UnauthorizedException("Incorrect username or password");
        }
    }

    private boolean isUserExists(UsersResource usersResource, String userName) {
        List<UserRepresentation> existingUsers = usersResource.search(userName);
        return !existingUsers.isEmpty();
    }
}
