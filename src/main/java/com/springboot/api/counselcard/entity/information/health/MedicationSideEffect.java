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

    @Column(nullable = false)
    private String suspectedMedicationNote;

    @Column(nullable = false)
    private String symptomsNote;

    public static MedicationSideEffect initializeDefault(){
        MedicationSideEffect medicationSideEffect = new MedicationSideEffect();
        medicationSideEffect.isMedicationSideEffect = false;
        medicationSideEffect.suspectedMedicationNote = "";
        medicationSideEffect.symptomsNote = "";
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
        this.isMedicationSideEffect = Objects.requireNonNullElse(medicationSideEffectDTO.isMedicationSideEffect(), this.isMedicationSideEffect);
        this.suspectedMedicationNote = Objects.requireNonNullElse(medicationSideEffectDTO.suspectedMedicationNote(), this.suspectedMedicationNote);
        this.symptomsNote = Objects.requireNonNullElse(medicationSideEffectDTO.symptomsNote(), this.symptomsNote);

        if(this.isMedicationSideEffect.equals(false)){
            this.suspectedMedicationNote = "";
            this.symptomsNote = "";
        }
    }
}
