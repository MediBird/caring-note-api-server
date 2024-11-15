package com.springboot.api.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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

    @Column(updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    @NotNull
    @PastOrPresent
    private LocalDate prescriptionDate;

    @NotNull
    @FutureOrPresent
    private LocalDate expiryDate;

    @Min(0)
    private int remainingQuantity;

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        createdDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }

}
