package com.springboot.api.counselcard.dto.information.health.item;

import lombok.Builder;

@Builder
public record MedicationSideEffectDTO(
        boolean isSideEffect, String suspectedMedicationNote, String symptomsNote) {
}
