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
    @Column(nullable = false)
    private String allergyNote;

    @Column(nullable = false)
    private boolean isAllergic;

    public static Allergy initializeDefault() {
        Allergy allergy = new Allergy();
        allergy.allergyNote = "";
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
        this.allergyNote = Objects.requireNonNullElse(allergyDTO.allergyNote(), this.allergyNote);
        this.isAllergic = Objects.requireNonNullElse(allergyDTO.isAllergic(), this.isAllergic);
    }
}

