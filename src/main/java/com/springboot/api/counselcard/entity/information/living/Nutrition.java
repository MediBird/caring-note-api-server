package com.springboot.api.counselcard.entity.information.living;

import java.util.Objects;

import com.springboot.api.counselcard.dto.information.living.NutritionDTO;
import com.springboot.enums.MealPattern;
import com.springboot.enums.WaterIntake;

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

    @Enumerated(EnumType.STRING)
    private WaterIntake waterIntake;

    public static Nutrition initializeDefault() {
        Nutrition nutrition = new Nutrition();
        nutrition.mealPattern = null;
        nutrition.nutritionNote = null;
        nutrition.waterIntake = null;
        return nutrition;
    }

    public static Nutrition copy(Nutrition nutrition) {
        Nutrition copiedNutrition = new Nutrition();
        copiedNutrition.mealPattern = nutrition.mealPattern;
        copiedNutrition.nutritionNote = nutrition.nutritionNote;
        copiedNutrition.waterIntake = nutrition.waterIntake;
        return copiedNutrition;
    }

    public void update(NutritionDTO nutritionDTO) {
        if (Objects.isNull(nutritionDTO)) {
            return;
        }

        this.mealPattern = nutritionDTO.mealPattern();
        this.nutritionNote = nutritionDTO.nutritionNote();
        this.waterIntake = nutritionDTO.waterIntake();
    }
}
