package com.springboot.api.counselsession.entity;

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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "counselee_consents", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"counsel_session_id", "counselee_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CounseleeConsent extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counsel_session_id", nullable = false)
    private CounselSession counselSession;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "counselee_id", nullable = false)
    private Counselee counselee;

    private LocalDateTime consentDateTime;

    @NotNull
    private boolean isConsent;

    protected CounseleeConsent(CounselSession counselSession, Counselee counselee) {
        if (counselSession == null || counselee == null) {
            throw new IllegalArgumentException("CounselSession and Counselee must not be null");
        }
        this.counselSession = counselSession;
        this.counselee = counselee;
        this.isConsent = false;
    }

    public static CounseleeConsent create(CounselSession counselSession, Counselee counselee) {
        return new CounseleeConsent(counselSession, counselee);
    }

    public void accept() {
        this.consentDateTime = LocalDateTime.now();
        this.isConsent = true;
    }

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }
}
