package com.springboot.api.counselcard.dto.response;

import com.springboot.api.counselcard.dto.information.base.BaseInfoDTO;
import com.springboot.api.counselcard.dto.information.base.CounselPurposeAndNoteDTO;
import com.springboot.api.counselcard.dto.information.health.AllergyDTO;
import com.springboot.api.counselcard.dto.information.health.DiseaseInfoDTO;
import com.springboot.api.counselcard.dto.information.health.MedicationSideEffectDTO;
import com.springboot.api.counselcard.dto.information.living.DrinkingDTO;
import com.springboot.api.counselcard.dto.information.living.ExerciseDTO;
import com.springboot.api.counselcard.dto.information.living.MedicationManagementDTO;
import com.springboot.api.counselcard.dto.information.living.NutritionDTO;
import com.springboot.api.counselcard.dto.information.living.SmokingDTO;
import com.springboot.api.counselcard.entity.CounselCard;
import com.springboot.enums.CardRecordStatus;


public record CounselCardHealthInformationRes(

    AllergyDTO allergy,

    DiseaseInfoDTO diseaseInfo,

    MedicationSideEffectDTO medicationSideEffect
) {

    public CounselCardHealthInformationRes(CounselCard counselCard) {
        this(
            new AllergyDTO(counselCard.getAllergy()),
            new DiseaseInfoDTO(counselCard.getDiseaseInfo()),
            new MedicationSideEffectDTO(counselCard.getMedicationSideEffect())
        );
    }
}
