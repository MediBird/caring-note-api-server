package com.springboot.api.counselsession.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.springboot.api.common.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "medication_counsel_highlights")
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = { "medicationCounsel" })
@ToString(callSuper = true, exclude = { "medicationCounsel" })
public class MedicationCounselHighlight extends BaseEntity {

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "medication_counsel_id")
    private MedicationCounsel medicationCounsel;

    private String highlight;
    private Integer startIndex;
    private Integer endIndex;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }

}
