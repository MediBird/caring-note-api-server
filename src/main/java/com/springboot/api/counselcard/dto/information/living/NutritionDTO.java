package com.springboot.api.counselcard.dto.information.living;

import com.springboot.api.counselcard.entity.information.living.Nutrition;
import com.springboot.enums.MealPattern;

public record NutritionDTO(
    MealPattern mealPattern, String nutritionNote) {

    public NutritionDTO(Nutrition nutrition) {
        this(
            nutrition != null ? nutrition.getMealPattern() : null,
            nutrition != null ? nutrition.getNutritionNote() : null
        );
    }
}
