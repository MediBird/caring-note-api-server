package com.springboot.api.counselcard.dto.information.health;

import com.springboot.api.counselcard.dto.information.health.item.AllergyDTO;
import com.springboot.api.counselcard.dto.information.health.item.DiseaseInfoDTO;
import com.springboot.api.counselcard.dto.information.health.item.MedicationSideEffectDTO;

import lombok.Builder;

@Builder
public record HealthInformationDTO(
        String version, DiseaseInfoDTO diseaseInfo, AllergyDTO allergy,
        MedicationSideEffectDTO medicationSideEffect) {
}
