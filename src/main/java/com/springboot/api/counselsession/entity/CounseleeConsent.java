package com.springboot.api.counselsession.entity;

import jakarta.persistence.FetchType;
import java.time.LocalDateTime;

import com.springboot.api.common.entity.BaseEntity;
import com.springboot.api.counselee.entity.Counselee;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "counsel_session_id", nullable = false)
    private CounselSession counselSession;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
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
