package com.springboot.api.dto.counselcard.information.living.item;

import lombok.Builder;

@Builder
public record SmokingDTO(
                boolean isSmoking, String smokingPeriodNote, String smokingAmount) {
}
