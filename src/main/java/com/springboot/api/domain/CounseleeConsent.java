package com.springboot.api.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "counselee_consents", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"counsel_session_id", "counselee_id"})
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"counselSession", "counselee",})
@ToString(callSuper = true, exclude = {"counselSession", "counselee"})
public class CounseleeConsent extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "counsel_session_id", nullable = false)
    private CounselSession counselSession;

    @ManyToOne
    @JoinColumn(name ="counselee_id", nullable = false)
    private Counselee counselee;

    @NotNull
    private LocalDate consentedDate;

    @NotNull
    private boolean isConsent;



}
