package com.springboot.api.common.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Value("${spring.security.oauth2.client.provider.keycloak.authorization-uri}")
    private String authorizationUri;

    @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}")
    private String tokenUri;

    @Bean
    public OpenAPI customOpenAPI() {
        OAuthFlow authorizationCodeFlow = new OAuthFlow()
                .authorizationUrl(authorizationUri)
                .tokenUrl(tokenUri)
                .scopes(new Scopes()
                        .addString("openid", "OpenID Connect")
                        .addString("profile", "Profile")
                        .addString("email", "Email"));

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("oauth2", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .authorizationCode(
                                                authorizationCodeFlow))))
                .security(List.of(new SecurityRequirement().addList("oauth2")));
    }

}
