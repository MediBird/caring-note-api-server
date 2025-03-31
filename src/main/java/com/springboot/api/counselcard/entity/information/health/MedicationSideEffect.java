package com.springboot.api.counselcard.entity.information.health;

import com.springboot.api.counselcard.dto.information.health.MedicationSideEffectDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class MedicationSideEffect {
    @Column(nullable = false)
    private Boolean isMedicationSideEffect;

    private String suspectedMedicationNote;

    private String symptomsNote;

    public static MedicationSideEffect initializeDefault(){
        MedicationSideEffect medicationSideEffect = new MedicationSideEffect();
        medicationSideEffect.isMedicationSideEffect = false;
        medicationSideEffect.suspectedMedicationNote = null;
        medicationSideEffect.symptomsNote = null;
        return medicationSideEffect;
    }

    public static MedicationSideEffect copy(MedicationSideEffect medicationSideEffect){
        MedicationSideEffect copiedMedicationSideEffect = new MedicationSideEffect();
        copiedMedicationSideEffect.isMedicationSideEffect = medicationSideEffect.isMedicationSideEffect;
        copiedMedicationSideEffect.suspectedMedicationNote = medicationSideEffect.suspectedMedicationNote;
        copiedMedicationSideEffect.symptomsNote = medicationSideEffect.symptomsNote;
        return copiedMedicationSideEffect;
    }

    public void update(MedicationSideEffectDTO medicationSideEffectDTO){
        if(Objects.isNull(medicationSideEffectDTO)){
            return;
        }

        this.isMedicationSideEffect = Objects.requireNonNullElse(medicationSideEffectDTO.isMedicationSideEffect(), false);
        this.suspectedMedicationNote = medicationSideEffectDTO.suspectedMedicationNote();
        this.symptomsNote = medicationSideEffectDTO.symptomsNote();

        if(this.isMedicationSideEffect.equals(false)){
            this.suspectedMedicationNote = null;
            this.symptomsNote = null;
        }
    }
}
