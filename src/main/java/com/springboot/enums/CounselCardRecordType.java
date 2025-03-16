package com.springboot.enums;

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
import java.util.function.Function;

public enum CounselCardRecordType {
    SMOKING(CounselCard::getSmoking, SmokingDTO::new),
    DRINKING(CounselCard::getDrinking, DrinkingDTO::new),
    NUTRITION(CounselCard::getNutrition, NutritionDTO::new),
    EXERCISE(CounselCard::getExercise, ExerciseDTO::new),
    MEDICATION_MANAGEMENT(CounselCard::getMedicationManagement, MedicationManagementDTO::new),
    DISEASE_INFO(CounselCard::getDiseaseInfo, DiseaseInfoDTO::new),
    ALLERGY(CounselCard::getAllergy, AllergyDTO::new),
    MEDICATION_SIDE_EFFECT(CounselCard::getMedicationSideEffect, MedicationSideEffectDTO::new),
    COUNSEL_PURPOSE_AND_NOTE(CounselCard::getCounselPurposeAndNote, CounselPurposeAndNoteDTO::new);

    private final Function<CounselCard, ?> extractor;
    private final Function<?, ?> dtoConverter;

    <T, R> CounselCardRecordType(Function<CounselCard, T> extractor, Function<T, R> dtoConverter) {
        this.extractor = extractor;
        this.dtoConverter = dtoConverter;
    }

    @SuppressWarnings("unchecked")
    public <T, R> Function<CounselCard, T> getExtractor() {
        return (Function<CounselCard, T>) extractor;
    }

    @SuppressWarnings("unchecked")
    public <T, R> Function<T, R> getDtoConverter() {
        return (Function<T, R>) dtoConverter;
    }
}