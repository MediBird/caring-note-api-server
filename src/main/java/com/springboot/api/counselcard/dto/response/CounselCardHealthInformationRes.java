package com.springboot.api.counselcard.dto.response;

import com.springboot.api.counselcard.dto.information.health.AllergyDTO;
import com.springboot.api.counselcard.dto.information.health.DiseaseInfoDTO;
import com.springboot.api.counselcard.dto.information.health.MedicationSideEffectDTO;
import com.springboot.api.counselcard.entity.CounselCard;


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
