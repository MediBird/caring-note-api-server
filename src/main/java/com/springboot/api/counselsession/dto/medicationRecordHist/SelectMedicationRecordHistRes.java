package com.springboot.api.counselsession.dto.medicationRecordHist;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.springboot.api.counselsession.entity.MedicationRecordHist;
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
        public static SelectMedicationRecordHistRes from(MedicationRecordHist medicationRecordHist) {
                return new SelectMedicationRecordHistRes(
                                medicationRecordHist.getId(),
                                medicationRecordHist.getMedication() != null
                                                                ? medicationRecordHist.getMedication().getId()
                                                : null,
                                medicationRecordHist.getName(),
                                medicationRecordHist.getMedicationDivision(),
                                medicationRecordHist.getUsageObject(),
                                medicationRecordHist.getPrescriptionDate(),
                                medicationRecordHist.getPrescriptionDays(),
                                medicationRecordHist.getUnit(),
                                medicationRecordHist.getUsageStatus(),
                                medicationRecordHist.getUpdatedDatetime(),
                                medicationRecordHist.getCreatedDatetime(),
                                medicationRecordHist.getCreatedBy(),
                                medicationRecordHist.getUpdatedBy());
        }

}
