package com.springboot.api.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "waste_medication_records")
@Data
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"counselSession"})
@ToString(callSuper = true, exclude = {"counselSession"})
public class WasteMedicationRecord extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "cousel_session_id", nullable = false)
    private CounselSession counselSession;

    @OneToOne
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    private String unit;

    private String disposalReason;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }
}
