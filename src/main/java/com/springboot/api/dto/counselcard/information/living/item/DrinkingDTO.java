package com.springboot.api.dto.counselcard.information.living.item;

import lombok.Builder;

@Builder
public record DrinkingDTO(
        boolean isDrinking
        ,String drinkingAmount
){
}
