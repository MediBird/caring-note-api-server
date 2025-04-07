package com.springboot.api.counselcard.entity.information.living;

import java.util.Objects;

import com.springboot.api.counselcard.dto.information.living.DrinkingDTO;
import com.springboot.enums.DrinkingAmount;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Drinking {

    @Enumerated(EnumType.STRING)
    private DrinkingAmount drinkingAmount;

    public static Drinking initializeDefault() {
        Drinking drinking = new Drinking();
        drinking.drinkingAmount = null;
        return drinking;
    }

    public static Drinking copy(Drinking drinking) {
        Drinking copiedDrinking = new Drinking();
        copiedDrinking.drinkingAmount = drinking.drinkingAmount;
        return copiedDrinking;
    }

    public void update(DrinkingDTO drinkingDTO) {
        if (Objects.isNull(drinkingDTO)) {
            return;
        }

        this.drinkingAmount = drinkingDTO.drinkingAmount();
    }
}