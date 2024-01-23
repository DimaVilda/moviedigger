package com.backbase.moviesdigger.service.impl;

import com.backbase.moviesdigger.exceptions.ConflictException;
import com.backbase.moviesdigger.exceptions.UnauthorizedException;
import com.backbase.moviesdigger.utils.KeycloakMethodsUtil;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static com.backbase.moviesdigger.utils.consts.KeycloakConsts.APPLICATION_REALM;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakService {

    private final KeycloakMethodsUtil keycloakMethodsUtil;

    public Response createUserInKeycloak(UsersResource usersResource, String userName, String userPassword) {
        log.debug("Trying to create new user {} in keycloak", userName);

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(userName);
        userRepresentation.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userPassword);
        userRepresentation.setCredentials(Collections.singletonList(credential));

        Response response = usersResource.create(userRepresentation);
        if (response.getStatus() != 201) {
            log.warn("A new user creation in keycloak failed, instead os 201 created, response is {} ", response.getStatus());
            throw new UnauthorizedException("Failed authorization for user " +
                    userName + ", try again or speak with admin");
        }
        return response;
    }

    public void deleteUserFromKeycloak(UsersResource usersResource, String userName) {
        log.debug("Trying to delete user {} from keycloak", userName);

        String userId = usersResource.search(userName).get(0).getId();
        try (Response response = usersResource.delete(userId)) {
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                log.error("Failed to delete user from Keycloak. HTTP status: {}", response.getStatus());
                throw new UnauthorizedException("Failed to delete user from Keycloak: HTTP status " + response.getStatus());
            }
        } catch (Exception e) {
            log.warn("User removing from keycloak failed, reason is {} ", e.getMessage());
            throw new UnauthorizedException("Failed authorization for user " +
                    userName + ", try again or speak with admin");
        }
    }

    public void assignRealmRoleForUser(Keycloak keycloak,
                                       UsersResource usersResource,
                                       Response response,
                                       String userRole) {
        log.debug("Trying to assign role {} for created user in keycloak", userRole);

        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        RoleRepresentation role = keycloak.realm(APPLICATION_REALM).roles().get(userRole).toRepresentation();
        UserResource userResource = usersResource.get(userId);
        userResource.roles().realmLevel().add(Collections.singletonList(role));
    }

    public void endUserSession(RealmResource applicationRealm, UsersResource usersResource, String userNameFromClaim) {
        log.debug("Trying to end user's {} session", userNameFromClaim);

        UserRepresentation user = usersResource.search(userNameFromClaim).get(0);
        usersResource.get(user.getId()).logout();
        if (keycloakMethodsUtil.isUserLoggedIn(applicationRealm, usersResource, userNameFromClaim)) {
            throw new ConflictException("A user's " + userNameFromClaim + " the session ended unsuccessfully! " +
                    "Pls, try again or contact with support");
        }
    }
}
