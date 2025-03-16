package com.springboot.api.counselcard.entity.information.health;

import java.util.Objects;
import java.util.Set;

import com.springboot.api.counselcard.dto.information.health.DiseaseInfoDTO;
import com.springboot.enums.DiseaseType;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class DiseaseInfo {

    private String historyNote;

    private String mainInconvenienceNote;

    @ElementCollection
    @CollectionTable(name = "disease_info", joinColumns = @JoinColumn(name = "disease_info_id"))
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<DiseaseType> diseases;

    public static DiseaseInfo initializeDefault() {
        DiseaseInfo diseaseInfo = new DiseaseInfo();
        diseaseInfo.historyNote = "";
        diseaseInfo.mainInconvenienceNote = "";
        diseaseInfo.diseases = Set.of();
        return diseaseInfo;
    }

    public static DiseaseInfo copy(DiseaseInfo diseaseInfo) {
        DiseaseInfo copiedDiseaseInfo = new DiseaseInfo();
        copiedDiseaseInfo.historyNote = diseaseInfo.historyNote;
        copiedDiseaseInfo.mainInconvenienceNote = diseaseInfo.mainInconvenienceNote;
        copiedDiseaseInfo.diseases = Set.copyOf(diseaseInfo.diseases);
        return copiedDiseaseInfo;
    }

    public void update(DiseaseInfoDTO diseaseInfoDTO) {
        this.historyNote = Objects.requireNonNullElse(diseaseInfoDTO.historyNote(), this.historyNote);
        this.mainInconvenienceNote = Objects.requireNonNullElse(diseaseInfoDTO.mainInconvenienceNote(), this.mainInconvenienceNote);
        this.diseases = Objects.requireNonNullElse(diseaseInfoDTO.diseases(), this.diseases);
    }
}