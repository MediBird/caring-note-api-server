package com.springboot.api.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import com.springboot.api.common.config.security.SecurityProperties;
import com.springboot.api.common.aspect.RoleSecuredAspect;
import java.util.Arrays;
import java.util.List;

@TestConfiguration
@EnableAspectJAutoProxy
public class TestSecurityConfig {

    @Bean
    public SecurityProperties securityProperties() {
        return new SecurityProperties() {
            @Override
            public List<String> getPermitAllUrls() {
                return Arrays.asList("/v1/auth/**", "/v1/counselor/signup");
            }
        };
    }

    @Bean
    public RoleSecuredAspect roleSecuredAspect() {
        return new RoleSecuredAspect();
    }
}