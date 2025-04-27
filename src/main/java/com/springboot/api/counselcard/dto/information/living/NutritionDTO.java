package com.springboot.api.counselcard.dto.information.living;

import com.springboot.api.counselcard.entity.information.living.Nutrition;
import com.springboot.enums.MealPattern;
import com.springboot.enums.WaterIntake;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "영양 DTO")
public record NutritionDTO(
        @Schema(description = "식사 양상", example = "REGULAR") MealPattern mealPattern,

        @Schema(description = "수분섭취량", example = "BETWEEN_1L_AND_1_5L") WaterIntake waterIntake,

        @Schema(description = "영양 특이사항",
                example = "식사량이 적음") @Size(max = 500) String nutritionNote) {

    public NutritionDTO(Nutrition nutrition) {
        this(nutrition != null ? nutrition.getMealPattern() : null,
                nutrition != null ? nutrition.getWaterIntake() : null,
                nutrition != null ? nutrition.getNutritionNote() : null);
    }
}
