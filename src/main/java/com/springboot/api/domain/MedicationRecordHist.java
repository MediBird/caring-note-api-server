package com.springboot.api.domain;

import com.springboot.enums.MedicationDivision;
import com.springboot.enums.MedicationUsageStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "medication_records_hist")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
