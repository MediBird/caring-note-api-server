package com.springboot.api.counselcard.dto.information.living.item;

import lombok.Builder;

@Builder
public record SmokingDTO(
        boolean isSmoking, String smokingPeriodNote, String smokingAmount) {
}
