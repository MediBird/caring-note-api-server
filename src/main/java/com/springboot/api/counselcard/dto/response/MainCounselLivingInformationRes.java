package com.springboot.api.counselcard.dto.response;

import com.springboot.api.counselcard.dto.information.living.ExerciseDTO;
import com.springboot.api.counselcard.dto.information.living.MedicationManagementDTO;
import com.springboot.api.counselcard.dto.information.living.NutritionDTO;
import com.springboot.api.counselcard.dto.information.living.SmokingDTO;
import com.springboot.enums.DrinkingAmount;

public record MainCounselLivingInformationRes(
    MainCounselRecord<SmokingDTO> smoking,
    MainCounselRecord<DrinkingAmount> drinkingAmount,
    MainCounselRecord<ExerciseDTO> exercise,
    MainCounselRecord<MedicationManagementDTO> medicationManagement,
    MainCounselRecord<NutritionDTO> nutrition
) {

}


