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
                counselCard != null ? new DrinkingDTO(counselCard.getDrinking()) : null,
                counselCard != null ? new ExerciseDTO(counselCard.getExercise()) : null,
                counselCard != null ? new MedicationManagementDTO(counselCard.getMedicationManagement()) : null,
                counselCard != null ? new NutritionDTO(counselCard.getNutrition()) : null,
                counselCard != null ? new SmokingDTO(counselCard.getSmoking()) : null
        );
    }
}
