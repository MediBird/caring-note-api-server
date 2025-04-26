package com.springboot.api.common.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.springboot.api.common.message.ExceptionMessages;
import com.springboot.enums.RoleType;

@Component
public class AuthUtil {

    public RoleType getRoleType(UserDetails userDetails) throws RuntimeException {

        return userDetails.getAuthorities().stream()
            .findFirst()
            .map(GrantedAuthority::getAuthority)
            .map(RoleType::valueOf)
            .orElseThrow(() -> new IllegalArgumentException(ExceptionMessages.INVALID_AUTHORITY));

    }
}
