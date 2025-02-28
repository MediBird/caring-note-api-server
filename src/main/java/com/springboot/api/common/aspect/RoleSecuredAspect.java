package com.springboot.api.common.aspect;

import java.util.Arrays;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.springboot.api.common.annotation.RoleSecured;
import com.springboot.enums.RoleType;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class RoleSecuredAspect {

    @Before("@within(roleSecured) || @annotation(roleSecured)")
    public void checkRoleAccess(RoleSecured roleSecured) throws RuntimeException {
        if (roleSecured == null) {
            throw new IllegalArgumentException("RoleSecured annotation is required");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
        }

        RoleType[] requiredRoles = roleSecured.value();
        log.info("Checking access for roles: {}", Arrays.toString(requiredRoles));
        log.info("Current user authentication: {}", authentication.getName());

        boolean hasRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> Arrays.asList(requiredRoles)
                        .stream()
                        .map(RoleType::name)
                        .anyMatch(authority::equals));

        if (!hasRole) {
            log.warn("Access denied for user: {} - Required roles: {}",
                    authentication.getName(), Arrays.toString(requiredRoles));
            throw new AccessDeniedException("User does not have the required role");
        }
    }
}
