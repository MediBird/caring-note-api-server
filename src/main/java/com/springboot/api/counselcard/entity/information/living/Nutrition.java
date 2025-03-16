package com.springboot.api.counselcard.entity.information.living;

import com.springboot.api.counselcard.dto.information.living.NutritionDTO;
import com.springboot.enums.MealPattern;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Nutrition {
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MealPattern mealPattern;

    @Column(nullable = false)
    private String nutritionNote;

    public static Nutrition from(NutritionDTO nutritionDTO) {
        Nutrition nutrition = new Nutrition();
        nutrition.mealPattern = Objects.requireNonNullElse(nutritionDTO.mealPattern(), MealPattern.IRREGULAR_MEALS);
        nutrition.nutritionNote = Objects.requireNonNullElse(nutritionDTO.nutritionNote(), "");
        return nutrition;
    }

    public void update(NutritionDTO nutritionDTO) {
        this.mealPattern = Objects.requireNonNullElse(nutritionDTO.mealPattern(), this.mealPattern);
        this.nutritionNote = Objects.requireNonNullElse(nutritionDTO.nutritionNote(), this.nutritionNote);
    }
}
