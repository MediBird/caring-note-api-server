package com.springboot.api.dto.medicationRecordHist;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.springboot.enums.MedicationDivision;

public record SelectMedicationRecordHistRes(
        String id,
        String medicationId,
        String name,
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
