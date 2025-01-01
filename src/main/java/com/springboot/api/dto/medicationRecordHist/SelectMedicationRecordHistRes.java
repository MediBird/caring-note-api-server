package com.springboot.api.dto.medicationRecordHist;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SelectMedicationRecordHistRes(
        String rowId,
        String medicationId,
        String medicationName,
        String DivisionCode,
        String usageObject,
        LocalDate prescriptionDate,
        int prescriptionDays,
        String unit,
        String usageStatusCode,
        LocalDateTime updatedDatetime,
        LocalDateTime createdDatetime,
        String createdBy,
        String updatedBy) {

}
