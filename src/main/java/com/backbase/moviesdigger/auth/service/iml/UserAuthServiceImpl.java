package com.backbase.moviesdigger.auth.service.iml;

import com.backbase.moviesdigger.auth.service.UserAuthService;
import com.backbase.moviesdigger.client.spec.model.LoggedInUserInformation;
import com.backbase.moviesdigger.client.spec.model.LoggedInUserResponse;
import com.backbase.moviesdigger.exceptions.ConflictException;
import com.backbase.moviesdigger.exceptions.NotFoundException;
import com.backbase.moviesdigger.exceptions.UnauthorizedException;
import com.backbase.moviesdigger.utils.consts.KeycloakMethodsUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.backbase.moviesdigger.utils.consts.KeycloakConsts.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {

    private final Keycloak keycloak;
    private final TokenService tokenService;
    private final KeycloakService keycloakService;
    private final UserPersistenceService userPersistenceService;
    private final KeycloakMethodsUtil keycloakMethodsUtil;

    @Override
    @Transactional
    public LoggedInUserResponse login(LoggedInUserInformation loggedInUserInformation) {
        String userName = loggedInUserInformation.getUserName();
        String userPassword = loggedInUserInformation.getPassword();
        UsersResource usersResource = keycloak.realm(APPLICATION_REALM).users();

        if (!isUserAlreadyExists(usersResource, userName)) { //if user does not exist
            return registerAndProcessNewUser(usersResource, userName, userPassword);
        }
        if (keycloakMethodsUtil.getUserTokensByUsernameAndPassword(userName, userPassword).statusCode() != 200) { //if token by user creds
            throw new UnauthorizedException("A user with username " +
                    userName + ", already exists, try another username");
        }
        if (userPersistenceService.isLoggedInByUserName(userName)) { // if this userName already exists in keycloak
            throw new ConflictException("A user " + userName + " is already logged in! ");
        }
        return processExistingUserLogin(userName, userPassword); //if user exists but his status is logged out
    }

    @Override
    public LoggedInUserResponse getAccessToken(String userName) {
        UsersResource usersResource = keycloak.realm(APPLICATION_REALM).users();

        if (!isUserAlreadyExists(usersResource, userName)) {
            throw new NotFoundException("A user with userName " + userName + " doesn't exist!");
        }

        String loggedInUserRefreshToken = userPersistenceService.findRefreshTokenByUserNameIfLoggedIn(userName);
        if (loggedInUserRefreshToken.isEmpty()) {
            throw new UnauthorizedException("A user " + userName + " is not authorized! Please, log in!");
        }
        HttpResponse<String> tokensResponse =
                keycloakMethodsUtil.getUserTokenByRefreshToken(loggedInUserRefreshToken);
        validateTokensResponse(tokensResponse, userName);

        Pair<String, LoggedInUserResponse> refreshTokenToResponsePair =
                keycloakMethodsUtil.buildLoggedInUserResponse(tokensResponse);
        tokenService.handleRefreshTokenExpiration(userName, refreshTokenToResponsePair);
        return refreshTokenToResponsePair.getValue();
    }

    private LoggedInUserResponse processExistingUserLogin(String userName, String userPassword) {
        HttpResponse<String> tokensResponse =
                keycloakMethodsUtil.getUserTokensByUsernameAndPassword(userName, userPassword);
        validateTokensResponse(tokensResponse, userName);

        Pair<String, LoggedInUserResponse> refreshTokenToResponsePair =
                keycloakMethodsUtil.buildLoggedInUserResponse(tokensResponse);
        tokenService.handleRefreshTokenExpiration(userName, refreshTokenToResponsePair);
        userPersistenceService.updateUserStatusToLoggedIn(userName);
        return refreshTokenToResponsePair.getValue();
    }

    private LoggedInUserResponse registerAndProcessNewUser(UsersResource usersResource, String userName, String userPassword) {
        keycloakService.createUserInKeycloak(keycloak, usersResource, userName, userPassword);
        HttpResponse<String> tokensResponse =
                keycloakMethodsUtil.getUserTokensByUsernameAndPassword(userName, userPassword);
        validateTokensResponse(tokensResponse, userName);

        Pair<String, LoggedInUserResponse> refreshTokenToResponsePair =
                keycloakMethodsUtil.buildLoggedInUserResponse(tokensResponse);
        userPersistenceService.saveLoggedInUserAndHisToken(userName, refreshTokenToResponsePair);
        return refreshTokenToResponsePair.getValue();
    }

    private void validateTokensResponse(HttpResponse<String> tokensResponse, String userName) {
        if (tokensResponse == null) {
            throw new UnauthorizedException("Failed authorization for user " +
                    userName + ", try again or speak with admin");
        }
        if (tokensResponse.statusCode() == 401) {
            throw new UnauthorizedException("A user with username " +
                    userName + ", already exists, try another username");
        }
    }

    private boolean isUserAlreadyExists(UsersResource usersResource, String userName) {
        List<UserRepresentation> existingUsers = usersResource.search(userName);
        return !existingUsers.isEmpty();
    }
}
