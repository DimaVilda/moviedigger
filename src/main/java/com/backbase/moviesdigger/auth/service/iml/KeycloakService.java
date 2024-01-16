package com.backbase.moviesdigger.auth.service.iml;

import com.backbase.moviesdigger.exceptions.ConflictException;
import com.backbase.moviesdigger.exceptions.UnauthorizedException;
import com.backbase.moviesdigger.utils.KeycloakMethodsUtil;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static com.backbase.moviesdigger.utils.consts.KeycloakConsts.APPLICATION_REALM;
import static com.backbase.moviesdigger.utils.consts.KeycloakConsts.REALM_USER_ROLE;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakService {

    private final KeycloakMethodsUtil keycloakMethodsUtil;

    public void createUserInKeycloak(Keycloak keycloak, UsersResource usersResource, String userName, String userPassword) {
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

        keycloakMethodsUtil.assignRealmRoleForUser(keycloak, usersResource, response, REALM_USER_ROLE);
    }

    public void endUserSession(RealmResource applicationRealm, UsersResource usersResource, String userNameFromClaim) {
        UserRepresentation user = usersResource.search(userNameFromClaim).get(0);
        usersResource.get(user.getId()).logout();
        if (keycloakMethodsUtil.isUserLoggedIn(applicationRealm, usersResource, userNameFromClaim)) {
            throw new ConflictException("A user's " + userNameFromClaim + " the session ended unsuccessfully! " +
                    "Pls, try again or contact with support");
        }
    }
}
