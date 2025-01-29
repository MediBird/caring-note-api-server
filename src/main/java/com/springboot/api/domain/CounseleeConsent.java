package com.springboot.api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "counselee_consents", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "counselee_id" })
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = { "counselee" })
@ToString(callSuper = true, exclude = { "counselee" })
public class CounseleeConsent extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "counselee_id", nullable = false)
    private Counselee counselee;

    @NotNull
    private LocalDateTime consentDateTime;

    @NotNull
    private boolean isConsent;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }

}
