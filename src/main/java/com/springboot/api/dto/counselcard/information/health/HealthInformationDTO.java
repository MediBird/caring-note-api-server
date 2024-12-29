package com.springboot.api.dto.counselcard.information.health;

import com.springboot.api.dto.counselcard.information.health.item.AllergyDTO;
import com.springboot.api.dto.counselcard.information.health.item.DiseaseInfoDTO;
import com.springboot.api.dto.counselcard.information.health.item.MedicationSideEffectDTO;
import lombok.Builder;

@Builder
public record HealthInformationDTO(
        String version
        , DiseaseInfoDTO diseaseInfo
        , AllergyDTO allergy
        , MedicationSideEffectDTO medicationSideEffect
        ) {}
