package com.springboot.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class JpaTestAdditionalConfig {

    @Bean
    public EntityManager entityManager(EntityManager entityManager) {
        return entityManager;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}

