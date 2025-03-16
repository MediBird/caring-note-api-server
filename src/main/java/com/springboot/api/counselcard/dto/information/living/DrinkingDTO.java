package com.springboot.api.counselcard.dto.information.living;

import com.springboot.api.counselcard.entity.information.living.Drinking;

public record DrinkingDTO(
    Boolean isDrinking) {

    public DrinkingDTO (Drinking drinking){
        this(drinking.getIsDrinking());
    }
}
