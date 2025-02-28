package com.springboot.api.dto.counselcard.information.health.item;

import java.util.List;

import lombok.Builder;

@Builder
public record DiseaseInfoDTO(
                List<String> diseases, String historyNote, String mainInconvenienceNote) {
}
