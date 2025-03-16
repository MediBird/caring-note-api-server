package com.springboot.api.counselcard.dto.information.health;

import com.springboot.api.counselcard.entity.information.health.Allergy;

public record AllergyDTO(
        Boolean isAllergy) {

    public AllergyDTO(Allergy allergy) {
        this(allergy.getIsAllergy());
    }
}
