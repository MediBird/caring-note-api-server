package com.springboot.api.dto.wasteMedication;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddWasteMedicationRecordReq {

    @NotBlank
    private String counselSessionId;

    @NotBlank
    private String medicationId;

    private String unit;

    private String disposalReason;
}
