package com.backbase.moviesdigger.runners;

import com.backbase.moviesdigger.service.iml.KeycloakService;
import com.backbase.moviesdigger.service.iml.UserPersistenceService;
import com.backbase.moviesdigger.cleanup.KeycloakCleanup;
import com.backbase.moviesdigger.utils.KeycloakMethodsUtil;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static com.backbase.moviesdigger.utils.consts.KeycloakConsts.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakInitializerRunner implements CommandLineRunner {

    private final Keycloak keycloak;
    private final KeycloakCleanup keycloakCleanup;
    private final KeycloakService keycloakService;
    private final UserPersistenceService userPersistenceService;
    private final KeycloakMethodsUtil keycloakMethodsUtil;
    private String internalClientId;

    @Override
    public void run(String... args) {
        try {
            if (!isRealmExists()) {
                log.debug("Creating application realm: {}", APPLICATION_REALM);
                defineKeycloacPreset();
            } else {
                log.debug("Realm {} already exists in keycloak, removing it", APPLICATION_REALM);
                keycloakCleanup.removeRealm();
                defineKeycloacPreset();
            }
        } catch (Exception e) {
            log.warn("Could not create application's keycloak configuration, reason is: {}", e.getMessage());
        }
    }

    private void defineKeycloacPreset() {
        createRealm();
        createClient();
        associateClientRoleToRealmRole(CLIENT_ADMIN_ROLE, REALM_ADMIN_ROLE);
        associateClientRoleToRealmRole(CLIENT_USER_ROLE, REALM_USER_ROLE);
        createAdminUser();
        // loginAdminUser();
    }

    private boolean isRealmExists() {
        return keycloak.realms().findAll().stream().anyMatch(realm -> APPLICATION_REALM.equals(realm.getRealm()));
    }

    private void createRealm() {
        RoleRepresentation userRole = new RoleRepresentation();
        userRole.setName(REALM_USER_ROLE);

        RoleRepresentation adminRole = new RoleRepresentation();
        adminRole.setName(REALM_ADMIN_ROLE);

        RolesRepresentation rolesRepresentation = new RolesRepresentation();
        rolesRepresentation.setRealm(List.of(userRole, adminRole));

        RealmRepresentation rr = new RealmRepresentation();
        rr.setId(APPLICATION_REALM_ID);
        rr.setRealm(APPLICATION_REALM);
        rr.setDisplayName(APPLICATION_REALM);
        rr.setEnabled(true);
        rr.setRoles(rolesRepresentation);

        keycloak.realms().create(rr);
    }

    private void createClient() {
        ClientRepresentation client = new ClientRepresentation();
        client.setClientId(APPLICATION_CLIENT_ID); // Set your client ID
        client.setName(APPLICATION_CLIENT); // Set a name for your client
        client.setEnabled(true);
        client.setDirectAccessGrantsEnabled(true);
        client.setPublicClient(true);

        ClientsResource clientsResource = keycloak.realm(APPLICATION_REALM).clients();
        Response response = clientsResource.create(client);
        if (response.getStatus() == 201) {
            createClientRoles();
        } else {
            log.warn("Could not create client, status: {}", response.getStatus());
        }
    }

    private void createClientRoles() {
        ClientsResource clientsResource = keycloak.realm(APPLICATION_REALM).clients();
        List<ClientRepresentation> clients = clientsResource.findByClientId(APPLICATION_CLIENT_ID);
        if (clients.isEmpty()) {
            throw new IllegalStateException("Client not found: " + APPLICATION_CLIENT_ID);
        }
        internalClientId = clients.get(0).getId();
        ClientResource clientResource = keycloak.realm(APPLICATION_REALM).clients().get(internalClientId);
        RolesResource rolesResource = clientResource.roles();

        RoleRepresentation clientAdminRole = createClientRole(CLIENT_ADMIN_ROLE);
        rolesResource.create(clientAdminRole);

        RoleRepresentation clientUserRole = createClientRole(CLIENT_USER_ROLE);
        rolesResource.create(clientUserRole);
        log.debug("Roles created within client: {}", APPLICATION_CLIENT_ID);
    }

    private RoleRepresentation createClientRole(String roleName) {
        RoleRepresentation role = new RoleRepresentation();
        role.setName(roleName);
        return role;
    }

    private void associateClientRoleToRealmRole(String clientRoleName, String realmRoleName) {
        // Retrieve the client role
        RoleRepresentation clientRole = getClientRole(clientRoleName);

        // Retrieve the realm role
        RoleRepresentation realmRole = getRealmRole(realmRoleName);

        if (clientRole != null && realmRole != null) {
            // Get the realm role's resource
            RoleResource realmRoleResource = keycloak.realm(APPLICATION_REALM).roles().get(realmRole.getName());

            // Add the client role to the realm role's composites
            realmRoleResource.addComposites(Collections.singletonList(clientRole));
            log.debug("Associated client role {} to realm role {}", clientRoleName, realmRoleName);
        } else {
            log.warn("Could not associate client role {} to realm role {} because one of them is null", clientRoleName, realmRoleName);
        }
    }

    private RoleRepresentation getClientRole(String clientRoleName) {
        return keycloak.realm(APPLICATION_REALM).clients().get(internalClientId).roles().get(clientRoleName).toRepresentation();
    }

    private RoleRepresentation getRealmRole(String realmRoleName) {
        return keycloak.realm(APPLICATION_REALM).roles().get(realmRoleName).toRepresentation();
    }

    private void createAdminUser() {
        keycloakService.createUserInKeycloak(
                keycloak,
                keycloak.realm(APPLICATION_REALM).users(),
                ADMIN_USER_NAME,
                ADMIN_USER_PASSWORD);
        userPersistenceService.saveUser(ADMIN_USER_NAME);
/*        UserRepresentation adminUser = new UserRepresentation();
        adminUser.setUsername(ADMIN_USER_NAME);
        adminUser.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue("admin");
        adminUser.setCredentials(Collections.singletonList(credential));

        Response response = keycloak.realm(APPLICATION_REALM).users().create(adminUser);

        if (response.getStatus() == 201) {
            keycloakService.assignRealmRoleForUser(keycloak,
                    keycloak.realm(APPLICATION_REALM).users(),
                    response,
                    REALM_ADMIN_ROLE);
        }*/
    }

    private void loginAdminUser() {
        keycloakMethodsUtil.getUserTokensByUsernameAndPassword(ADMIN_USER_NAME, ADMIN_USER_PASSWORD);
    }
}
