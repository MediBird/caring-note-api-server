package com.springboot.api.counselsession.entity;

import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.springboot.api.common.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "medication_counsels")
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = { "counselSession", "medicationCounselHighlights" })
@ToString(callSuper = true, exclude = { "counselSession", "medicationCounselHighlights" })
public class MedicationCounsel extends BaseEntity {

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "counsel_session_id", nullable = false)
    private CounselSession counselSession;

    @Column(name = "counsel_record", columnDefinition = "TEXT")
    private String counselRecord;

    @OneToMany(mappedBy = "medicationCounsel", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<MedicationCounselHighlight> medicationCounselHighlights;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }
}
