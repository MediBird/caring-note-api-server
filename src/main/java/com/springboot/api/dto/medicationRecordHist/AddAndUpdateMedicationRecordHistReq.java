package com.springboot.api.dto.medicationRecordHist;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddAndUpdateMedicationRecordHistReq {

    private String rowId;
    private String medicationId;
    @NotBlank
    private String divisionCode;

    private String prescriptionDate;

    private int prescriptionDays;
    @NotBlank
    private String name;

    private String usageObject;

    private String unit;

    private String usageStatusCode;
}
