package com.springboot.api.common.entity;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.querydsl.core.annotations.QueryEntity;

import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@MappedSuperclass
@Data
@EqualsAndHashCode
@QueryEntity
public abstract class BaseEntity {

    @Id
    @Column(length = 26)
    private String id;

    @Column(updatable = false)
    private LocalDateTime createdDatetime;

    @Column
    private LocalDateTime updatedDatetime;

    @Column(updatable = false)
    private String createdBy;

    @Column
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
