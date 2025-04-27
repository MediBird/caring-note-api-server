package com.springboot.api.counselcard.entity.information.health;

import jakarta.persistence.ForeignKey;
import java.util.List;
import java.util.Objects;

import com.springboot.api.counselcard.dto.information.health.DiseaseInfoDTO;
import com.springboot.enums.DiseaseType;

import jakarta.persistence.CollectionTable;
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
    @CollectionTable(name = "disease_info",
        joinColumns = @JoinColumn(name = "disease_info_id"),
        foreignKey = @ForeignKey(
            foreignKeyDefinition = "FOREIGN KEY (disease_info_id) REFERENCES counsel_cards(id) ON DELETE CASCADE")
    )
    @Enumerated(EnumType.STRING)
    private List<DiseaseType> diseases;

    public static DiseaseInfo initializeDefault() {
        DiseaseInfo diseaseInfo = new DiseaseInfo();
        diseaseInfo.historyNote = null;
        diseaseInfo.mainInconvenienceNote = null;
        diseaseInfo.diseases = List.of();
        return diseaseInfo;
    }

    public static DiseaseInfo copy(DiseaseInfo diseaseInfo) {
        DiseaseInfo copiedDiseaseInfo = new DiseaseInfo();
        copiedDiseaseInfo.historyNote = diseaseInfo.historyNote;
        copiedDiseaseInfo.mainInconvenienceNote = diseaseInfo.mainInconvenienceNote;
        copiedDiseaseInfo.diseases = List.copyOf(diseaseInfo.diseases);
        return copiedDiseaseInfo;
    }

    public void update(DiseaseInfoDTO diseaseInfoDTO) {
        if (Objects.isNull(diseaseInfoDTO)) {
            return;
        }

        this.historyNote = diseaseInfoDTO.historyNote();
        this.mainInconvenienceNote = diseaseInfoDTO.mainInconvenienceNote();
        this.diseases = diseaseInfoDTO.diseases();
    }
}