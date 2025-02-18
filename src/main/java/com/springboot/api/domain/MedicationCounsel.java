package com.springboot.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "medication_counsels")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"counselSession","medicationCounselHighlights"})
@ToString(callSuper = true, exclude = {"counselSession","medicationCounselHighlights"})
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
