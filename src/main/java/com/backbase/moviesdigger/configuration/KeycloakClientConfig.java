package com.backbase.moviesdigger.configuration;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakClientConfig {
    @Value("${keycloak.client.admin.auth-server-url}")
    private String adminServerUrl;

    @Value("${keycloak.client.admin.realm}")
    private String adminRealm;

    @Value("${keycloak.client.admin.client-id}")
    private String adminClientId;

    @Value("${keycloak.client.admin.admin-username}")
    private String adminUsername;

    @Value("${keycloak.client.admin.admin-password}")
    private String adminPassword;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(adminServerUrl)
                .realm(adminRealm)
                .clientId(adminClientId)
                .username(adminUsername)
                .password(adminPassword)
                .build();
    }
}
