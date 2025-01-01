package com.springboot.api.dto.wasteMedication;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddAndUpdateWasteMedicationRecordReq {

    private String rowId;

    private String medicationId;

    @NotBlank
    private String unit;

    @NotBlank
    private String disposalReason;

    @NotBlank
    private String medicationName;

}
