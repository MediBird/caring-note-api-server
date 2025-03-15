package com.springboot.api.counselcard.dto.information.health;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.api.counselcard.entity.information.health.DiseaseInfo;
import com.springboot.enums.DiseaseType;

import java.util.Set;

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
