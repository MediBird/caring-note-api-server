package com.springboot.api.dto.medicationRecordHist;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.springboot.enums.MedicationDivision;
import com.springboot.enums.MedicationUsageStatus;

public record SelectMedicationRecordHistRes(
                String rowId,
                String medicationId,
                String medicationName,
                MedicationDivision divisionCode,
                String usageObject,
                LocalDate prescriptionDate,
                int prescriptionDays,
                String unit,
                MedicationUsageStatus usageStatusCode,
                LocalDateTime updatedDatetime,
                LocalDateTime createdDatetime,
                String createdBy,
                String updatedBy) {

}
