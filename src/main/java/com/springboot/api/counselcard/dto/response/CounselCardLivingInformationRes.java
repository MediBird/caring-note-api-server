package com.springboot.api.counselcard.dto.response;

import com.springboot.api.counselcard.dto.information.living.DrinkingDTO;
import com.springboot.api.counselcard.dto.information.living.ExerciseDTO;
import com.springboot.api.counselcard.dto.information.living.MedicationManagementDTO;
import com.springboot.api.counselcard.dto.information.living.NutritionDTO;
import com.springboot.api.counselcard.dto.information.living.SmokingDTO;
import com.springboot.api.counselcard.entity.CounselCard;


public record CounselCardLivingInformationRes(

    DrinkingDTO drinking,

    ExerciseDTO exercise,

    MedicationManagementDTO medicationManagement,

    NutritionDTO nutrition,

    SmokingDTO smoking
) {

    public CounselCardLivingInformationRes(CounselCard counselCard) {
        this(
            new DrinkingDTO(counselCard.getDrinking()),
            new ExerciseDTO(counselCard.getExercise()),
            new MedicationManagementDTO(counselCard.getMedicationManagement()),
            new NutritionDTO(counselCard.getNutrition()),
            new SmokingDTO(counselCard.getSmoking())
        );
    }
}
