package com.springboot.api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "counselee_consents", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "counsel_session_id", "counselee_id" })
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = { "counselSession", "counselee", })
@ToString(callSuper = true, exclude = { "counselSession", "counselee" })
public class CounseleeConsent extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "counselee_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
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
