package com.springboot.api.counselcard.dto.request;

import com.springboot.api.common.annotation.ValidEnum;
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
import com.springboot.enums.CardRecordStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddCounselCardReq(
        @NotBlank(message = "상담 세션 ID는 필수 입력값입니다")
        @Size(min = 26, max = 26, message = "상담 세션 ID는 26자여야 합니다")
        String counselSessionId,

        @ValidEnum(enumClass = CardRecordStatus.class)
        @Schema(description = "상담카드기록상태(IN_PROGRESS, COMPLETED", example = "IN_PROGRESS")
        CardRecordStatus cardRecordStatus,

        @NotNull
        CounselPurposeAndNoteDTO counselPurposeAndNote,

        @NotNull
        AllergyDTO allergy,

        @NotNull
        DiseaseInfoDTO diseaseInfo,

        @NotNull
        MedicationSideEffectDTO medicationSideEffect,

        @NotNull
        DrinkingDTO drinking,

        @NotNull
        ExerciseDTO exercise,

        @NotNull
        MedicationManagementDTO medicationManagement,

        @NotNull
        NutritionDTO nutrition,

        @NotNull
        SmokingDTO smoking
    ) {}