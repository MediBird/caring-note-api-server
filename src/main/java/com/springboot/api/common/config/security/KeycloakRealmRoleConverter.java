package com.springboot.api.common.config.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        log.info("jwt: {}", jwt);

        // (Optional) realm_access.roles 추출
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof List) {
            List<String> realmRoles = (List<String>) realmAccess.get("roles");
            realmRoles.forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
        }

        // ** resource_access.{client-id}.roles 추출 **
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null) {
            // resourceAccess 예: {"my-spring-client": {"roles":["manager","user"]}, "account": {"roles": [...]}}
            resourceAccess.forEach((resourceName, resource) -> {
                if (resource instanceof Map) {
                    Object rolesObj = ((Map<?, ?>) resource).get("roles");
                    if (rolesObj instanceof List) {
                        List<String> resourceRoles = (List<String>) rolesObj;
                        resourceRoles.forEach(role
                                -> // prefix를 붙여 GrantedAuthority 생성
                                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role))
                        );
                    }
                }
            });
        }

        return grantedAuthorities;
    }
}
