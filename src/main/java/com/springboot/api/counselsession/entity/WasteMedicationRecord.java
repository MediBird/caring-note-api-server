package com.springboot.api.counselsession.entity;

import com.springboot.api.common.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "waste_medication_records")
@Data
@Builder
@EqualsAndHashCode(callSuper = true, exclude = { "counselSession", "medication" })
@ToString(callSuper = true, exclude = { "counselSession", "medication" })
@NoArgsConstructor
@AllArgsConstructor
public class WasteMedicationRecord extends BaseEntity {

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "cousel_session_id", nullable = false)
    private CounselSession counselSession;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "medication_id", nullable = true)
    private Medication medication;

    private String medicationName;

    private Integer unit;

    private String disposalReason;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }
}
