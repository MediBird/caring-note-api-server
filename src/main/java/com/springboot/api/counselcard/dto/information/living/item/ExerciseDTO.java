package com.springboot.api.counselcard.dto.information.living.item;

import lombok.Builder;

@Builder
public record ExerciseDTO(
        String exercisePattern, String exerciseNote) {
}
