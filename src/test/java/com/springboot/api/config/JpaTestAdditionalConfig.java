package com.springboot.api.config;

import jakarta.persistence.EntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class JpaTestAdditionalConfig {

    @Bean
    public EntityManager entityManager(EntityManager entityManager) {
        return entityManager;
    }

}

