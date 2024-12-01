package com.springboot.api.domain;

import com.springboot.enums.MedicationDivision;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "medication_records_hist")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"counselSession","medication"})
@ToString(callSuper = true, exclude = {"counselSession", "medication"})
public class MedicationRecordHist extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "counsel_session_id", nullable = false)
    private CounselSession counselSession;

    @Enumerated(EnumType.STRING)
    private MedicationDivision medicationDivision;

    @ManyToOne
    @JoinColumn(name = "medication_id")
    private Medication medication;

    private String name;

    private String usageObject;

    @NotNull
    @PastOrPresent
    private LocalDate prescriptionDate;

    private int prescriptionDays;

    private String unit;

    @PrePersist
    @Override
    protected void  onCreate(){
        super.onCreate();
    }






}
