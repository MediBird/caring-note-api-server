package com.springboot.api.counselsession.dto.medicationRecordHist;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.enums.MedicationDivision;
import com.springboot.enums.MedicationUsageStatus;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddAndUpdateMedicationRecordHistReq {
    @Nullable
    private String rowId;

    @Nullable
    private String medicationId;

    @ValidEnum(enumClass = MedicationDivision.class)
    private MedicationDivision divisionCode;

    @Nullable
    private String prescriptionDate;

    @Nullable
    private int prescriptionDays;

    @NotBlank(message = "약물 이름은 필수 입력값입니다")
    private String medicationName;

    @Nullable
    private String usageObject;

    @Nullable
    private String unit;

    @ValidEnum(enumClass = MedicationUsageStatus.class)
    private MedicationUsageStatus usageStatusCode;
}
