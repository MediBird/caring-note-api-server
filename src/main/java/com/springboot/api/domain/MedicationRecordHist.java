package com.springboot.api.domain;

import java.time.LocalDate;

import com.springboot.enums.MedicationDivision;
import com.springboot.enums.MedicationUsageStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "medication_records_hist")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"counselSession", "medication"})
@ToString(callSuper = true, exclude = {"counselSession", "medication"})
public class MedicationRecordHist extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "counsel_session_id", nullable = false)
    private CounselSession counselSession;

    @Enumerated(EnumType.STRING)
    private MedicationDivision medicationDivision;

    @ManyToOne
    @JoinColumn(name = "medication_id", nullable = true)
    private Medication medication;

    private String name;

    private String usageObject;

    @NotNull
    @PastOrPresent
    private LocalDate prescriptionDate;

    private int prescriptionDays;

    private String unit;

    @Enumerated(EnumType.STRING)
    private MedicationUsageStatus usageStatus;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }

}
