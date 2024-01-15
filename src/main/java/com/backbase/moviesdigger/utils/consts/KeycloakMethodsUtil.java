package com.backbase.moviesdigger.utils.consts;

import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static com.backbase.moviesdigger.utils.consts.KeycloakConsts.APPLICATION_REALM;
import static com.backbase.moviesdigger.utils.consts.KeycloakConsts.REALM_USER_ROLE;

@Component
public class KeycloakMethodsUtil {

    public void assignRealmRoleForUser(Keycloak keycloak,
                                       UsersResource usersResource,
                                       Response response,
                                       String userRole) {
        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        RoleRepresentation role = keycloak.realm(APPLICATION_REALM).roles().get(userRole).toRepresentation();
        UserResource userResource = usersResource.get(userId);
        userResource.roles().realmLevel().add(Collections.singletonList(role));
    }
}
