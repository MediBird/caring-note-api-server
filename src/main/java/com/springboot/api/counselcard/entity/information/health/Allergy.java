package com.springboot.api.counselcard.entity.information.health;

import com.springboot.api.counselcard.dto.information.health.AllergyDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Allergy {
    @Column(nullable = false)
    private String allergyNote;

    public static Allergy initializeDefault() {
        Allergy allergy = new Allergy();
        allergy.allergyNote = "";
        return allergy;
    }

    public static Allergy copy(Allergy allergy) {
        Allergy copiedAllergy = new Allergy();
        copiedAllergy.allergyNote = allergy.allergyNote;
        return copiedAllergy;
    }

    public void update(AllergyDTO allergyDTO) {
        this.allergyNote = Objects.requireNonNullElse(allergyDTO.allergyNote(), this.allergyNote);
    }
}
