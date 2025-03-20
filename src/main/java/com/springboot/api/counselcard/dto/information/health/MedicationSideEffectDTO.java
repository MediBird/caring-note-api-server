package com.springboot.api.counselcard.dto.information.health;

import com.springboot.api.counselcard.entity.information.health.MedicationSideEffect;

public record MedicationSideEffectDTO(
    String suspectedMedicationNote, String symptomsNote) {

    public MedicationSideEffectDTO(MedicationSideEffect medicationSideEffect) {
        this(
            medicationSideEffect.getSuspectedMedicationNote(),
            medicationSideEffect.getSymptomsNote()
        );
    }
}
