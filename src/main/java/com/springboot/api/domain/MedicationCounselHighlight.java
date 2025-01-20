package com.springboot.api.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "medication_counsel_highlights")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"medicationCounsel"})
@ToString(callSuper = true, exclude = {"medicationCounsel"})
public class MedicationCounselHighlight extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "medication_counsel_id")
    private MedicationCounsel medicationCounsel;

    private String highlight;
    private Integer startIndex;
    private Integer endIndex;






}
