package com.springboot.api.domain;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "medication_counsels")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = { "counselSession", "medicationCounselHighlights" })
@ToString(callSuper = true, exclude = { "counselSession", "medicationCounselHighlights" })
public class MedicationCounsel extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "counsel_session_id", nullable = false)
    private CounselSession counselSession;

    @Column(name = "counsel_record", columnDefinition = "TEXT")
    private String counselRecord;

    @OneToMany(mappedBy = "medicationCounsel", cascade = CascadeType.REMOVE)
    private List<MedicationCounselHighlight> medicationCounselHighlights;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }
}
