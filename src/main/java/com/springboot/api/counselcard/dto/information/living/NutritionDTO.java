package com.springboot.api.counselcard.dto.information.living;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.api.counselcard.entity.information.living.Nutrition;
import com.springboot.enums.MealPattern;

public record NutritionDTO(
    @ValidEnum(enumClass = MealPattern.class)
    MealPattern mealPattern, String nutritionNote) {

    public NutritionDTO (Nutrition nutrition){
        this(
            nutrition.getMealPattern(),
            nutrition.getNutritionNote()
        );
    }
}
