package com.springboot.api.counselcard.dto.information.health;

import com.springboot.api.counselcard.entity.information.health.MedicationSideEffect;

public record MedicationSideEffectDTO(
        Boolean isMedicationSideEffect) {

    public MedicationSideEffectDTO(MedicationSideEffect medicationSideEffect) {
            this(
                medicationSideEffect.getIsMedicationSideEffect()
            );
    }
}
