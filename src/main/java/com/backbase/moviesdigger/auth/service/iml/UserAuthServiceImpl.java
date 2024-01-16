package com.backbase.moviesdigger.auth.service.iml;

import com.backbase.moviesdigger.auth.service.UserAuthService;
import com.backbase.moviesdigger.client.spec.model.AccessTokenResponse;
import com.backbase.moviesdigger.client.spec.model.LoggedInUserInformation;
import com.backbase.moviesdigger.client.spec.model.LoggedInUserResponse;
import com.backbase.moviesdigger.exceptions.ConflictException;
import com.backbase.moviesdigger.exceptions.UnauthorizedException;
import com.backbase.moviesdigger.models.BearerTokenModel;
import com.backbase.moviesdigger.utils.KeycloakMethodsUtil;
import com.backbase.moviesdigger.utils.TokenMethodsUtil;
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
public class UserAuthServiceImpl implements UserAuthService {

    private final Keycloak keycloak;
    private final KeycloakService keycloakService;
    private final UserPersistenceService userPersistenceService;
    private final KeycloakMethodsUtil keycloakMethodsUtil;
    private final TokenMethodsUtil tokenMethodsUtil;
    private final BearerTokenModel tokenWrapper;

    @Override
    @Transactional
    public LoggedInUserResponse login(LoggedInUserInformation loggedInUserInformation) {
        String userName = loggedInUserInformation.getUserName();
        String userPassword = loggedInUserInformation.getPassword();
        UsersResource usersResource = keycloak.realm(APPLICATION_REALM).users();

        if (!isUserAlreadyExists(usersResource, userName)) { //if user does not exist
            return registerAndProcessNewUser(usersResource, userName, userPassword);
        }
        if (keycloakMethodsUtil.isUserLoggedIn(keycloak.realm(APPLICATION_REALM), usersResource, userName)) { //check if user has sessions and limit them
            throw new ConflictException("A user " + userName + " is already logged in! ");
        }
        return processExistingLoggedOutUserLogin(userName, userPassword); //if user exists but his status is logged out
    }

    @Override
    public void endSession() {
        String usernameFromClaim = tokenMethodsUtil.getUserTokenClaimValue(tokenWrapper.getToken(), PREFERRED_USERNAME_CLAIM);
        UsersResource usersResource = keycloak.realm(APPLICATION_REALM).users();
        keycloakService.endUserSession(keycloak.realm(APPLICATION_REALM), usersResource, usernameFromClaim);
    }

    @Override
    public AccessTokenResponse getAccessToken(String refreshToken) { //TODO mb check if current access token is active and if so - dont generate a new one
        HttpResponse<String> tokensResponse =
                keycloakMethodsUtil.getUserAccessTokenByRefreshToken(refreshToken); //is response not 200 - user have to log in
        if (tokensResponse.statusCode() != 200) { //in case a refresh token was expired as well
            throw new UnauthorizedException("A user is not authorized! Please, log in!");
        }
        LoggedInUserResponse refreshTokenToResponse =
                keycloakMethodsUtil.buildLoggedInUserResponse(tokensResponse);
        return new AccessTokenResponse()
                .accessToken(refreshTokenToResponse.getAccessToken())
                .expiresIn(refreshTokenToResponse.getExpiresIn());
    }

    private LoggedInUserResponse processExistingLoggedOutUserLogin(String userName, String userPassword) {
        HttpResponse<String> tokensResponse =
                keycloakMethodsUtil.getUserTokensByUsernameAndPassword(userName, userPassword);
        validateTokensResponse(tokensResponse, userName);

        LoggedInUserResponse refreshTokenToResponsePair =
                keycloakMethodsUtil.buildLoggedInUserResponse(tokensResponse);
        //tokenService.handleRefreshTokenExpiration(userName, refreshTokenToResponsePair);
        userPersistenceService.updateUserStatusToLoggedIn(userName);
        return refreshTokenToResponsePair;
    }

    private LoggedInUserResponse registerAndProcessNewUser(UsersResource usersResource, String userName, String userPassword) {
        keycloakService.createUserInKeycloak(keycloak, usersResource, userName, userPassword);
        HttpResponse<String> tokensResponse =
                keycloakMethodsUtil.getUserTokensByUsernameAndPassword(userName, userPassword);
        validateTokensResponse(tokensResponse, userName);

        LoggedInUserResponse refreshTokenToResponse =
                keycloakMethodsUtil.buildLoggedInUserResponse(tokensResponse);
        userPersistenceService.saveLoggedInUserAndHisToken(userName, refreshTokenToResponse);
        return refreshTokenToResponse;
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
