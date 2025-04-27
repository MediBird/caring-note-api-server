package com.springboot.api.counselcard.dto.request;

import com.springboot.api.counselcard.dto.information.health.AllergyDTO;
import com.springboot.api.counselcard.dto.information.health.DiseaseInfoDTO;
import com.springboot.api.counselcard.dto.information.health.MedicationSideEffectDTO;
import jakarta.validation.constraints.NotNull;


public record UpdateHealthInformationReq(
    @NotNull
    AllergyDTO allergy,

    @NotNull
    DiseaseInfoDTO diseaseInfo,

    @NotNull
    MedicationSideEffectDTO medicationSideEffect
) {

}
