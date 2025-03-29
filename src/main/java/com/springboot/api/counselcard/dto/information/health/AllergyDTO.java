package com.springboot.api.counselcard.dto.information.health;

import com.springboot.api.counselcard.entity.information.health.Allergy;

public record AllergyDTO(
    Boolean isAllergic,
    String allergyNote) {

    public AllergyDTO(Allergy allergy) {
        this(allergy.getIsAllergic(), allergy.getAllergyNote());
    }
}
