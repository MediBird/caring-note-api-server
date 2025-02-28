package com.springboot.api.counselsession.dto.wasteMedication;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddAndUpdateWasteMedicationRecordReq {

    @Nullable
    private String rowId;

    @NotBlank(message = "약물 ID는 필수 입력값입니다")
    @Size(min = 26, max = 26, message = "약물 ID는 26자여야 합니다")
    private String medicationId;

    private Integer unit;

    @NotBlank(message = "약물 폐기 이유는 필수 입력값입니다")
    private String disposalReason;

    @NotBlank(message = "약물 이름은 필수 입력값입니다")
    private String medicationName;

}
