package com.springboot.api.counselcard.dto.information.health;

import java.util.Set;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.api.counselcard.entity.information.health.DiseaseInfo;
import com.springboot.enums.DiseaseType;

public record DiseaseInfoDTO(
    @ValidEnum(enumClass = DiseaseType.class)
    Set<DiseaseType> diseases,
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
