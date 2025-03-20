package com.springboot.api.counselcard.dto.information.health;

import com.springboot.api.counselcard.entity.information.health.Allergy;

public record AllergyDTO(
    String allergyNote) {

    public AllergyDTO(Allergy allergy) {
        this(allergy.getAllergyNote());
    }
}
