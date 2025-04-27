package com.springboot.api.counselor.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Data
public class KeycloakAdminClient {

    private String url;
    private String realm;
    private String adminUsername;
    private String adminPassword;
    private final String clientId = "admin-cli";

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
            .serverUrl(url)
            .realm("master")
            .username(adminUsername)
            .password(adminPassword)
            .clientId(clientId)
            .build();
    }

    @Bean
    public RealmResource realmResource() {
        return keycloak().realm(realm);
    }
}
