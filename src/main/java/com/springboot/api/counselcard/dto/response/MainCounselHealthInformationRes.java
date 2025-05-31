package com.springboot.api.counselcard.dto.response;

import com.springboot.api.counselcard.dto.information.health.AllergyDTO;
import com.springboot.api.counselcard.dto.information.health.MedicationSideEffectDTO;
import com.springboot.enums.DiseaseType;
import java.util.List;

public record MainCounselHealthInformationRes(
    MainCounselRecord<List<DiseaseType>> diseases,
    MainCounselRecord<String> historyNote,
    MainCounselRecord<String> mainInconvenienceNote,
    MainCounselRecord<MedicationSideEffectDTO> medicationSideEffect,
    MainCounselRecord<AllergyDTO> allergy
) {

}


