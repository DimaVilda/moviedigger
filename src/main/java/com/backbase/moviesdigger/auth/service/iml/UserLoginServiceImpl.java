package com.backbase.moviesdigger.auth.service.iml;

import com.backbase.moviesdigger.auth.service.UserLoginService;
import com.backbase.moviesdigger.client.spec.model.LoggedInUserInformation;
import com.backbase.moviesdigger.client.spec.model.LoggedInUserResponse;
import com.backbase.moviesdigger.exceptions.UnauthorizedException;
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
public class UserLoginServiceImpl implements UserLoginService {

    private final Keycloak keycloak;
    private final TokenService tokenService;
    private final KeycloakService keycloakService;
    private final UserPersistenceService userPersistenceService;

    @Override
    @Transactional
    public LoggedInUserResponse login(LoggedInUserInformation loggedInUserInformation) {
        String userName = loggedInUserInformation.getUserName();
        String userPassword = loggedInUserInformation.getPassword();
        UsersResource usersResource = keycloak.realm(APPLICATION_REALM).users();

        if (isUserAlreadyExists(usersResource, userName)) {
            return processExistingUserLogin(userName, userPassword);
        } else {

            return registerAndProcessNewUser(usersResource, loggedInUserInformation);
        }
    }

    public LoggedInUserResponse processExistingUserLogin(String userName, String userPassword) {
        HttpResponse<String> tokensResponse = getTokensForUser(userName, userPassword);
        validateTokensResponse(tokensResponse, userName);

        Pair<String, LoggedInUserResponse> refreshTokenToResponsePair = buildLoggedInUserResponse(tokensResponse);
        tokenService.handleRefreshTokenExpiration(userName, refreshTokenToResponsePair);
        return refreshTokenToResponsePair.getValue();
    }

    private LoggedInUserResponse registerAndProcessNewUser(UsersResource usersResource, LoggedInUserInformation userInfo) {
        keycloakService.createUserInKeycloak(keycloak, usersResource, userInfo);
        HttpResponse<String> tokensResponse = getTokensForUser(userInfo.getUserName(), userInfo.getPassword());
        validateTokensResponse(tokensResponse, userInfo.getUserName());

        Pair<String, LoggedInUserResponse> refreshTokenToResponsePair = buildLoggedInUserResponse(tokensResponse);
        userPersistenceService.saveLoggedInUserAndHisToken(userInfo.getUserName(), refreshTokenToResponsePair);
        return refreshTokenToResponsePair.getValue();
    }

    private void validateTokensResponse(HttpResponse<String> tokensResponse, String userName) {
        if (tokensResponse == null) {
            throw new UnauthorizedException("Failed authorization for user " +
                    userName + ", try again or speak with admin");
        }
    }

    private boolean isUserAlreadyExists(UsersResource usersResource, String userName) {
        List<UserRepresentation> existingUsers = usersResource.search(userName);
        return !existingUsers.isEmpty();
    }

    private HttpResponse<String> getTokensForUser(String username, String password) {
        HttpClient client = HttpClient.newHttpClient();
        String tokenEndpoint = String.format("http://localhost:8080/realms/%s/protocol/openid-connect/token", APPLICATION_REALM);

        Map<Object, Object> data = new HashMap<>();
        data.put("client_id", APPLICATION_CLIENT_ID);
        data.put("username", username);
        data.put("password", password);
        data.put("grant_type", "password");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenEndpoint))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(buildFormDataFromMap(data))
                .build();

        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.warn("Could not send a token request to Open ID Connect, reason is {}", e.getMessage());
            return null;
        }
    }

    private HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) {
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            sj.add(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8) +
                    "=" +
                    URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(sj.toString());
    }

    private Pair<String, LoggedInUserResponse> buildLoggedInUserResponse(HttpResponse<String> response) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = objectMapper.readTree(response.body());
        } catch (JsonProcessingException e) {
            log.warn("Could not read json body, reason is {}", e.getMessage());
            throw new UnauthorizedException("Failed authorization for user, try again or speak with admin");
        }
        String accessToken = rootNode.path("access_token").asText();
        int expiresIn = rootNode.path("expires_in").asInt();
        int refreshExpiresIn = rootNode.path("refresh_expires_in").asInt();
        String refreshToken = rootNode.path("refresh_token").asText();

        return Pair.of(
                refreshToken,
                new LoggedInUserResponse()
                        .accessToken(accessToken)
                        .expiresIn(expiresIn)
                        .refreshExpiresIn(refreshExpiresIn)
        );
    }
}
