package com.springboot.api.domain;

import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

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
        if (this.id == null) {
            ULID ulid = new ULID();
            this.id = ulid.nextULID();
        }
        createdDatetime = LocalDateTime.now();
        updatedDatetime = LocalDateTime.now();
        createdBy = getCurrentUserId();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDatetime = LocalDateTime.now();
        updatedBy = getCurrentUserId();
    }

    private String getCurrentUserId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
        String username = jwt.getClaimAsString("preferred_username");
        if (authentication != null && authentication.isAuthenticated()) {
            return username;
        }
        return "system";

    }

}
