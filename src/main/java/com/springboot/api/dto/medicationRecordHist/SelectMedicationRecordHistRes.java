package com.springboot.api.dto.medicationRecordHist;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.springboot.enums.MedicationDivision;

public record SelectMedicationRecordHistRes(
        String rowId,
        String medicationId,
        String medicationName,
        MedicationDivision medicationDivision,
        String usageObject,
        LocalDate prescriptionDate,
        int prescriptionDays,
        String unit,
        LocalDateTime updatedDatetime,
        LocalDateTime createdDatetime,
        String createdBy,
        String updatedBy) {

}
