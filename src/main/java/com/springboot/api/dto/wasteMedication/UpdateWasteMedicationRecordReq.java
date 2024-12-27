package com.springboot.api.dto.wasteMedication;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateWasteMedicationRecordReq {

    @NotBlank
    private String id;

    private String medicationId;

    private String unit;

    private String disposalReason;
}
