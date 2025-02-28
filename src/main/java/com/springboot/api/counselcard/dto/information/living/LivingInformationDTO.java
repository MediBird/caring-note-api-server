package com.springboot.api.counselcard.dto.information.living;

import com.springboot.api.counselcard.dto.information.living.item.DrinkingDTO;
import com.springboot.api.counselcard.dto.information.living.item.ExerciseDTO;
import com.springboot.api.counselcard.dto.information.living.item.MedicationManagementDTO;
import com.springboot.api.counselcard.dto.information.living.item.NutritionDTO;
import com.springboot.api.counselcard.dto.information.living.item.SmokingDTO;

import lombok.Builder;

@Builder
public record LivingInformationDTO(
                String version, SmokingDTO smoking, DrinkingDTO drinking, NutritionDTO nutrition, ExerciseDTO exercise,
                MedicationManagementDTO medicationManagement) {
}
