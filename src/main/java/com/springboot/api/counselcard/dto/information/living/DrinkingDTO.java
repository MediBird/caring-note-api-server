package com.springboot.api.counselcard.dto.information.living;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.api.counselcard.entity.information.living.Drinking;
import com.springboot.enums.DrinkingAmount;

public record DrinkingDTO(
        @ValidEnum(enumClass = DrinkingAmount.class) DrinkingAmount drinkingAmount) {

    public DrinkingDTO(Drinking drinking) {
        this(drinking.getDrinkingAmount());
    }
}