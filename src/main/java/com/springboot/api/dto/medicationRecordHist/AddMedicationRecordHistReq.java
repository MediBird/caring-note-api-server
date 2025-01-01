package com.springboot.api.dto.medicationRecordHist;

import com.springboot.enums.MedicationDivision;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddMedicationRecordHistReq {

    @NotBlank
    private String medicationId;

    @NotBlank
    private MedicationDivision medicationDivision;

    private String prescriptionDate;

    private int prescriptionDays;

    private String name;

    @NotBlank
    private String usageObject;

    @NotBlank
    private String unit;

    private String disposalReason;

}
