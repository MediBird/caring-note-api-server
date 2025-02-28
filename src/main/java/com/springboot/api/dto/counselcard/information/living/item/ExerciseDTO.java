package com.springboot.api.dto.counselcard.information.living.item;

import lombok.Builder;

@Builder
public record ExerciseDTO(
                String exercisePattern, String exerciseNote) {
}
