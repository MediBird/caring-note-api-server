package com.springboot.api.counselcard.dto.information.health.item;

import lombok.Builder;

@Builder
public record AllergyDTO(
        boolean isAllergy, String allergyNote) {
}
