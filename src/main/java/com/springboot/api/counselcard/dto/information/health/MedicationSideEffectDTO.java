package com.springboot.api.counselcard.dto.information.health;

import com.springboot.api.counselcard.entity.information.health.MedicationSideEffect;

public record MedicationSideEffectDTO(
    Boolean isMedicationSideEffect, String suspectedMedicationNote, String symptomsNote) {

    public MedicationSideEffectDTO(MedicationSideEffect medicationSideEffect) {
        this(
            medicationSideEffect.getIsMedicationSideEffect(),
            medicationSideEffect.getSuspectedMedicationNote(),
            medicationSideEffect.getSymptomsNote()
        );
    }
}
