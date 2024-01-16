package com.backbase.moviesdigger.cleanup;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import static com.backbase.moviesdigger.utils.consts.KeycloakConsts.APPLICATION_REALM;

@Component
@Slf4j
public class KeycloakCleanup implements DisposableBean {

    private final Keycloak keycloak;

    public KeycloakCleanup(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @Override
    public void destroy() {
        removeRealm();
    }

    public void removeRealm() {
        try {
            keycloak.realm(APPLICATION_REALM).remove();
            log.debug("Realm removed successfully!");
        } catch (Exception e) {
            log.warn("Error removing realm, reason is {}", e.getMessage());
        }
    }
}
