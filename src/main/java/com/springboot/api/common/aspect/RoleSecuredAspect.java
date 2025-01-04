package com.springboot.api.common.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated");
        }

        RoleType[] requiredRoles = roleSecured.value();
        log.info("requiredRoles: {}", (Object[]) requiredRoles);
        log.info("authentication: {}", authentication);
        boolean hasRole = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> {
                    for (RoleType role : requiredRoles) {
                        if (grantedAuthority.getAuthority().equals(role.name())) {
                            return true;
                        }
                    }
                    return false;
                });

        if (!hasRole) {
            throw new AccessDeniedException("User does not have the required role");
        }
    }
}
