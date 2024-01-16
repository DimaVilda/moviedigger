package com.backbase.moviesdigger.utils;

import com.backbase.moviesdigger.client.spec.model.LoggedInUserResponse;
import com.backbase.moviesdigger.exceptions.UnauthorizedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.backbase.moviesdigger.utils.consts.KeycloakConsts.*;

@Component
@Slf4j
public class KeycloakMethodsUtil {

    @Value("${keycloak.open-id-connect.api.token-endpoint}")
    private String tokenEndpoint;

    public void assignRealmRoleForUser(Keycloak keycloak,
                                       UsersResource usersResource,
                                       Response response,
                                       String userRole) {
        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        RoleRepresentation role = keycloak.realm(APPLICATION_REALM).roles().get(userRole).toRepresentation();
        UserResource userResource = usersResource.get(userId);
        userResource.roles().realmLevel().add(Collections.singletonList(role));
    }

    public LoggedInUserResponse buildLoggedInUserResponse(HttpResponse<String> response) {
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

        return new LoggedInUserResponse()
                        .accessToken(accessToken)
                        .expiresIn(expiresIn)
                        .refreshToken(refreshToken)
                        .refreshExpiresIn(refreshExpiresIn);
    }

    public HttpResponse<String> getUserAccessTokenByRefreshToken(String refreshToken) {
        Map<Object, Object> data = new HashMap<>();
        data.put("client_id", APPLICATION_CLIENT_ID);
        data.put("grant_type", "refresh_token");
        data.put("refresh_token", refreshToken);

        return getToken(data);
    }

    public HttpResponse<String> getUserTokensByUsernameAndPassword(String username, String password) {
        Map<Object, Object> data = new HashMap<>();
        data.put("client_id", APPLICATION_CLIENT_ID);
        data.put("username", username);
        data.put("password", password);
        data.put("grant_type", "password");

        return getToken(data);
    }

    private HttpResponse<String> getToken(Map<Object, Object> data) {
        HttpClient client = HttpClient.newHttpClient();
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

    public boolean isUserLoggedIn(RealmResource applicationRealm, UsersResource usersResource, String userName) {
        UserRepresentation user = usersResource.search(userName).get(0);
        List<UserSessionRepresentation> userCurrentSessions = usersResource.get(user.getId()).getUserSessions();
        if (userCurrentSessions.size() > 1) {
            limitUserSessionsToTheFirstOne(applicationRealm, userCurrentSessions);
            return true;
        }
        return !usersResource.get(user.getId()).getUserSessions().isEmpty();// if user has sessions, user still logged in
    }

    private void limitUserSessionsToTheFirstOne(RealmResource applicationRealm,
                                                List<UserSessionRepresentation> userCurrentSessions) {

        log.debug("User sessions more than one, deleting a new one");
        userCurrentSessions.sort(Comparator.comparing(UserSessionRepresentation::getStart));
        applicationRealm.deleteSession(userCurrentSessions.get(1).getId());
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
}
