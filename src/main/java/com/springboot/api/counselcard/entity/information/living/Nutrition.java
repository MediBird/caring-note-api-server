package com.springboot.api.counselcard.entity.information.living;

import java.util.Objects;

import com.springboot.api.counselcard.dto.information.living.NutritionDTO;
import com.springboot.enums.MealPattern;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Nutrition {
    @Enumerated(EnumType.STRING)
    private MealPattern mealPattern;

    private String nutritionNote;

    public static Nutrition initializeDefault() {
        Nutrition nutrition = new Nutrition();
        nutrition.mealPattern = null;
        nutrition.nutritionNote = null;
        return nutrition;
    }

    public static Nutrition copy(Nutrition nutrition) {
        Nutrition copiedNutrition = new Nutrition();
        copiedNutrition.mealPattern = nutrition.mealPattern;
        copiedNutrition.nutritionNote = nutrition.nutritionNote;
        return copiedNutrition;
    }

    public void update(NutritionDTO nutritionDTO) {
        if(Objects.isNull(nutritionDTO)) {
            return;
        }

        this.mealPattern = nutritionDTO.mealPattern();
        this.nutritionNote = nutritionDTO.nutritionNote();
    }
}
