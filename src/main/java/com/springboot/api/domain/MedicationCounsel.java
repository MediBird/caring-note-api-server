package com.springboot.api.domain;

import com.springboot.enums.CounselNeedStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "medication_counsels")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"counselSession"})
@ToString(callSuper = true, exclude = {"counselSession"})
public class MedicationCounsel extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "counsel_session_id", nullable = false)
    private CounselSession counselSession;

    @Lob
    private String counselRecord;


    @Enumerated(EnumType.STRING)
    private CounselNeedStatus counselNeedStatus;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }
}
