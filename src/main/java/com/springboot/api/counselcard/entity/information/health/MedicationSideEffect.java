package com.springboot.api.counselcard.entity.information.health;

import com.springboot.api.counselcard.dto.information.health.MedicationSideEffectDTO;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class MedicationSideEffect {
    private String suspectedMedicationNote;
    private String symptomsNote;

    public static MedicationSideEffect from(MedicationSideEffectDTO medicationSideEffectDTO){
        MedicationSideEffect medicationSideEffect = new MedicationSideEffect();
        medicationSideEffect.suspectedMedicationNote = Objects.requireNonNullElse(medicationSideEffectDTO.suspectedMedicationNote(), "");
        medicationSideEffect.symptomsNote = Objects.requireNonNullElse(medicationSideEffectDTO.symptomsNote(), "");
        return medicationSideEffect;
    }

    public void update(MedicationSideEffectDTO medicationSideEffectDTO){
        this.suspectedMedicationNote = Objects.requireNonNullElse(medicationSideEffectDTO.suspectedMedicationNote(), this.suspectedMedicationNote);
        this.symptomsNote = Objects.requireNonNullElse(medicationSideEffectDTO.symptomsNote(), this.symptomsNote);
    }
}
