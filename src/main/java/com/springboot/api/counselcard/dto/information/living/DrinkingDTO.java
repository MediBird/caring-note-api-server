package com.springboot.api.counselcard.dto.information.living;

import com.springboot.api.counselcard.entity.information.living.Drinking;
import com.springboot.enums.DrinkingAmount;

public record DrinkingDTO(
    DrinkingAmount drinkingAmount) {

    public DrinkingDTO(Drinking drinking) {
        this(drinking != null ? drinking.getDrinkingAmount() : null);
    }
}