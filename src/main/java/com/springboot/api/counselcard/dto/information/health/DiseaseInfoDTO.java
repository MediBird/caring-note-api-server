package com.springboot.api.counselcard.dto.information.health;

import java.util.List;

import com.springboot.api.counselcard.entity.information.health.DiseaseInfo;
import com.springboot.enums.DiseaseType;

public record DiseaseInfoDTO(
    List<DiseaseType> diseases,
    String historyNote,
        String mainInconvenienceNote) {

    public DiseaseInfoDTO (DiseaseInfo diseaseInfo){
        this(
            diseaseInfo.getDiseases(),
            diseaseInfo.getHistoryNote(),
            diseaseInfo.getMainInconvenienceNote()
        );
    }
}
