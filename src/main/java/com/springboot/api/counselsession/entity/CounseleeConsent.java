package com.springboot.api.counselsession.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.springboot.api.common.entity.BaseEntity;
import com.springboot.api.counselee.entity.Counselee;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "counselee_consents", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "counsel_session_id", "counselee_id" })
})
@Data
@SuperBuilder
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
