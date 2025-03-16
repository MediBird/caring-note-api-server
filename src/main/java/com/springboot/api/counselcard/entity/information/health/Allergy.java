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
    private Boolean isAllergy;

    public static Allergy initializeDefault() {
        Allergy allergy = new Allergy();
        allergy.isAllergy = false;
        return allergy;
    }

    public static Allergy copy(Allergy allergy) {
        Allergy copiedAllergy = new Allergy();
        copiedAllergy.isAllergy = allergy.isAllergy;
        return copiedAllergy;
    }

    public void update(AllergyDTO allergyDTO) {
        this.isAllergy = Objects.requireNonNullElse(allergyDTO.isAllergy(), this.isAllergy);
    }
}
