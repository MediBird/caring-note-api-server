package com.springboot.api.counselcard.dto.request;

import com.springboot.api.counselcard.dto.information.living.DrinkingDTO;
import com.springboot.api.counselcard.dto.information.living.ExerciseDTO;
import com.springboot.api.counselcard.dto.information.living.MedicationManagementDTO;
import com.springboot.api.counselcard.dto.information.living.NutritionDTO;
import com.springboot.api.counselcard.dto.information.living.SmokingDTO;
import jakarta.validation.constraints.NotNull;


public record UpdateLivingInformationReq(
    @NotNull
    DrinkingDTO drinking,

    @NotNull
    ExerciseDTO exercise,

    @NotNull
    MedicationManagementDTO medicationManagement,

    @NotNull
    NutritionDTO nutrition,

    @NotNull
    SmokingDTO smoking
) {

}
