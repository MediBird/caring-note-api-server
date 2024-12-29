package com.springboot.api.dto.counselcard.information.health.item;

import lombok.Builder;

import java.util.List;

@Builder
public record DiseaseInfoDTO(
        List<String> diseases
        , String historyNote
        , String mainInconvenienceNote
){
}
