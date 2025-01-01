package com.springboot.api.dto.medicationRecordHist;

import com.springboot.enums.MedicationDivision;
import com.springboot.enums.MedicationUsageStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddAndUpdateMedicationRecordHistReq {

    private String rowId;
    private String medicationId;
    @NotBlank
    private MedicationDivision medicationDivisionCode;

    private String prescriptionDate;

    private int prescriptionDays;
    @NotBlank
    private String name;

    private String usageObject;

    private String unit;

    private MedicationUsageStatus medicationUsageStatusCode;
}
