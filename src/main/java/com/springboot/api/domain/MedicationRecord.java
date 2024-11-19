package com.springboot.api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medication_records")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"counselee", "medication", "prescribingCounselor"})
@ToString(callSuper = true, exclude = {"counselee", "medication", "prescribingCounselor"})
public class MedicationRecord extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "counselee_id", nullable = false)
    private Counselee counselee;

    @ManyToOne
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    private String dosage;

    private LocalDateTime administrationDateTime;

    private String routeOfAdministration;

    @ManyToOne
    @JoinColumn(name = "prescribing_counselor_id")
    private Counselor prescribingCounselor;

    private String notes;


    @NotNull
    @PastOrPresent
    private LocalDate prescriptionDate;

    @NotNull
    @FutureOrPresent
    private LocalDate expiryDate;

    @Min(0)
    private int remainingQuantity;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }


}
