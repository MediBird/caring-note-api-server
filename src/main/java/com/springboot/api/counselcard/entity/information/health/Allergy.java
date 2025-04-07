package com.springboot.api.counselcard.entity.information.health;

import java.util.Objects;

import com.springboot.api.counselcard.dto.information.health.AllergyDTO;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Allergy {

    private String allergyNote;

    private Boolean isAllergic;

    public static Allergy initializeDefault() {
        Allergy allergy = new Allergy();
        allergy.allergyNote = null;
        allergy.isAllergic = null;
        return allergy;
    }

    public static Allergy copy(Allergy allergy) {
        Allergy copiedAllergy = new Allergy();
        copiedAllergy.allergyNote = allergy.allergyNote;
        copiedAllergy.isAllergic = allergy.isAllergic;
        return copiedAllergy;
    }

    public void update(AllergyDTO allergyDTO) {
        if (Objects.isNull(allergyDTO)) {
            return;
        }

        this.allergyNote = allergyDTO.allergyNote();
        this.isAllergic = allergyDTO.isAllergic();

        if (Objects.equals(this.isAllergic, false)) {
            this.allergyNote = null;
        }
    }
}

