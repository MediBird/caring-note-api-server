package com.springboot.api.dto.counselcard.information.health.item;

import lombok.Builder;

@Builder
public record MedicationSideEffectDTO(
        boolean isSideEffect
        , String suspectedMedicationNote
        , String symptomsNote
){}
