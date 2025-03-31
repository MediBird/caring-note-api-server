package com.springboot.api.counselcard.entity.information.health;

import java.util.Objects;

import com.springboot.api.counselcard.dto.information.health.AllergyDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Allergy {
    private String allergyNote;

    @Column(nullable = false)
    private Boolean isAllergic;

    public static Allergy initializeDefault() {
        Allergy allergy = new Allergy();
        allergy.allergyNote = null;
        allergy.isAllergic = false;
        return allergy;
    }

    public static Allergy copy(Allergy allergy) {
        Allergy copiedAllergy = new Allergy();
        copiedAllergy.allergyNote = allergy.allergyNote;
        copiedAllergy.isAllergic = allergy.isAllergic;
        return copiedAllergy;
    }

    public void update(AllergyDTO allergyDTO) {
        if(Objects.isNull(allergyDTO)) {
            return;
        }

        this.allergyNote = allergyDTO.allergyNote();
        this.isAllergic = Objects.requireNonNullElse(allergyDTO.isAllergic(), false);

        if(this.isAllergic.equals(false)) {
            this.allergyNote = null;
        }
    }
}

