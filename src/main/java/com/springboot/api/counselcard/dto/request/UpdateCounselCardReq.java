package com.springboot.api.counselcard.dto.request;

import com.springboot.api.counselcard.dto.information.base.CounselPurposeAndNoteDTO;
import com.springboot.api.counselcard.dto.information.health.AllergyDTO;
import com.springboot.api.counselcard.dto.information.health.DiseaseInfoDTO;
import com.springboot.api.counselcard.dto.information.health.MedicationSideEffectDTO;
import com.springboot.api.counselcard.dto.information.independentlife.CommunicationDTO;
import com.springboot.api.counselcard.dto.information.independentlife.EvacuationDTO;
import com.springboot.api.counselcard.dto.information.independentlife.WalkingDTO;
import com.springboot.api.counselcard.dto.information.living.DrinkingDTO;
import com.springboot.api.counselcard.dto.information.living.ExerciseDTO;
import com.springboot.api.counselcard.dto.information.living.MedicationManagementDTO;
import com.springboot.api.counselcard.dto.information.living.NutritionDTO;
import com.springboot.api.counselcard.dto.information.living.SmokingDTO;


public record UpdateCounselCardReq(
    CounselPurposeAndNoteDTO counselPurposeAndNote,

    AllergyDTO allergy,

    DiseaseInfoDTO diseaseInfo,

    MedicationSideEffectDTO medicationSideEffect,

    DrinkingDTO drinking,

    ExerciseDTO exercise,

    MedicationManagementDTO medicationManagement,

    NutritionDTO nutrition,

    SmokingDTO smoking,

    CommunicationDTO communication,

    EvacuationDTO evacuation,

    WalkingDTO walking
) {}
