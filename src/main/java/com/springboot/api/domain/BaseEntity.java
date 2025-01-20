package com.springboot.api.domain;

import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.LocalDateTime;
import java.util.Optional;

@MappedSuperclass
@Data
@EqualsAndHashCode
public abstract class BaseEntity {

    @Id
    @Column(length = 26)
    private String id;

    @Column(updatable = false)
    private LocalDateTime createdDatetime;

    @Column
    private LocalDateTime updatedDatetime;

    private String createdBy;

    private String updatedBy;

    protected void onCreate() {
        id = Optional.ofNullable(this.id).orElseGet(() -> new ULID().nextULID());
        createdDatetime = LocalDateTime.now();
        createdBy = Optional.ofNullable(createdBy).orElseGet(this::getCurrentUserId);
        updatedDatetime = LocalDateTime.now();
        updatedBy = Optional.ofNullable(updatedBy).orElseGet(this::getCurrentUserId);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDatetime = LocalDateTime.now();
        updatedBy = Optional.ofNullable(updatedBy).orElseGet(this::getCurrentUserId);
    }


    private String getCurrentUserId() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .filter(auth -> auth instanceof JwtAuthenticationToken)
                .map(auth -> (JwtAuthenticationToken) auth)
                .map(JwtAuthenticationToken::getToken)
                .map(jwt -> jwt.getClaimAsString("preferred_username"))
                .orElse("system");
    }

}
