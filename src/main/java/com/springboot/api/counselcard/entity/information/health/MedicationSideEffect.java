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
    private Boolean isMedicationSideEffect;

    public static MedicationSideEffect initializeDefault(){
        MedicationSideEffect medicationSideEffect = new MedicationSideEffect();
        medicationSideEffect.isMedicationSideEffect = false;
        return medicationSideEffect;
    }

    public static MedicationSideEffect copy(MedicationSideEffect medicationSideEffect){
        MedicationSideEffect copiedMedicationSideEffect = new MedicationSideEffect();
        copiedMedicationSideEffect.isMedicationSideEffect = medicationSideEffect.isMedicationSideEffect;
        return copiedMedicationSideEffect;
    }

    public void update(MedicationSideEffectDTO medicationSideEffectDTO){
        this.isMedicationSideEffect = Objects.requireNonNullElse(medicationSideEffectDTO.isMedicationSideEffect(), this.isMedicationSideEffect);
    }
}
