package com.springboot.api.dto.counselcard.information.living;

import com.springboot.api.dto.counselcard.information.living.item.*;
import lombok.Builder;

@Builder
public record LivingInformationDTO(
        String version
        , SmokingDTO smoking
        , DrinkingDTO drinking
        , NutritionDTO nutrition
        , ExerciseDTO exercise
        , MedicationManagementDTO medicationManagement
        ){
}
