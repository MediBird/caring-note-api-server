package com.springboot.api.dto.medicationRecordHist;

import com.springboot.api.common.annotation.ValidEnum;
import com.springboot.enums.MedicationDivision;
import com.springboot.enums.MedicationUsageStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class AddAndUpdateMedicationRecordHistReq {
    @NotBlank(message = "행 ID는 필수 입력값입니다")
    @Size(min = 26, max = 26, message = "행 ID는 26자여야 합니다")
    private String rowId;

    @NotBlank(message = "약물 ID는 필수 입력값입니다")
    @Size(min = 26, max = 26, message = "약물 ID는 26자여야 합니다")
    private String medicationId;

    @ValidEnum(enumClass = MedicationDivision.class)
    private MedicationDivision divisionCode;

    @NotBlank(message = "처방일자는 필수 입력값입니다")
    private String prescriptionDate;

    @NotNull(message = "처방일수는 필수 입력값입니다")
    @Min(value = 1, message = "처방일수는 1일 이상이어야 합니다")
    private int prescriptionDays;

    @NotBlank(message = "약물 이름은 필수 입력값입니다")
    private String medicationName;

    @NotBlank(message = "약물 사용 대상은 필수 입력값입니다")
    private String usageObject;

    @NotBlank(message = "약물 단위는 필수 입력값입니다")
    private String unit;

    @ValidEnum(enumClass = MedicationUsageStatus.class)
    private MedicationUsageStatus usageStatusCode;
}
