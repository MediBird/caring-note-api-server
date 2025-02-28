package com.springboot.api.dto.counselcard.information.health.item;

import lombok.Builder;

@Builder
public record AllergyDTO(
                boolean isAllergy, String allergyNote) {
}
