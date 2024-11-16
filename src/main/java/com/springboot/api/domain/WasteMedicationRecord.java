package com.springboot.api.domain;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "waste_medication_records")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"counselSession"})
@ToString(callSuper = true, exclude = {"counselSession"})
public class WasteMedicationRecord extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "cousel_session_id", nullable = false)
    private CounselSession counselSession;

    private String name;

    private String unit;

    private String disposalReason;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }
}
