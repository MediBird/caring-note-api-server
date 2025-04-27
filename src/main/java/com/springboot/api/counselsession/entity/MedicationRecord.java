package com.springboot.api.counselsession.entity;

import com.springboot.api.common.entity.BaseEntity;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselor.entity.Counselor;
import com.springboot.api.medication.entity.Medication;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "medication_records")
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"counselee", "medication", "prescribingCounselor"})
@ToString(callSuper = true, exclude = {"counselee", "medication", "prescribingCounselor"})
public class MedicationRecord extends BaseEntity {

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "counselee_id", nullable = false)
    private Counselee counselee;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
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
