package com.springboot.api.counselsession.entity;

import com.springboot.api.common.entity.BaseEntity;
import com.springboot.api.medication.entity.Medication;
import com.springboot.enums.MedicationDivision;
import com.springboot.enums.MedicationUsageStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "medication_records_hist")
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"counselSession", "medication"})
@ToString(callSuper = true, exclude = {"counselSession", "medication"})
public class MedicationRecordHist extends BaseEntity {

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "counsel_session_id", nullable = false)
    private CounselSession counselSession;

    @Enumerated(EnumType.STRING)
    private MedicationDivision medicationDivision;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "medication_id")
    private Medication medication;

    private String name;

    private String usageObject;

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
