package com.springboot.api.dto.wasteMedication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddAndUpdateWasteMedicationRecordReq {

    private String rowId;

    @NotBlank(message = "약물 ID는 필수 입력값입니다")
    @Size(min = 26, max = 26, message = "약물 ID는 26자여야 합니다")
    private String medicationId;

    @NotBlank(message = "약물 단위는 필수 입력값입니다")
    private Integer unit;

    @NotBlank(message = "약물 폐기 이유는 필수 입력값입니다")
    private String disposalReason;

    @NotBlank(message = "약물 이름은 필수 입력값입니다")
    private String medicationName;

}
