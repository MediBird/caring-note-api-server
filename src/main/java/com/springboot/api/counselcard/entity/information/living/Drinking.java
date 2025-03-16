package com.springboot.api.counselcard.entity.information.living;

import com.springboot.api.counselcard.dto.information.living.DrinkingDTO;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Drinking {
    private Boolean isDrinking;

    public static Drinking initializeDefault() {
        Drinking drinking = new Drinking();
        drinking.isDrinking = false;
        return drinking;
    }

    public static Drinking copy(Drinking drinking) {
        Drinking copiedDrinking = new Drinking();
        copiedDrinking.isDrinking = drinking.isDrinking;
        return copiedDrinking;
    }

    public void update(DrinkingDTO drinkingDTO) {
        this.isDrinking = Objects.requireNonNullElse(drinkingDTO.isDrinking(), this.isDrinking);
    }
}