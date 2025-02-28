package com.springboot.api.dto.wasteMedication;

import java.time.LocalDateTime;

public record SelectMedicationRecordListBySessionIdRes(
                String rowId,
                String medicationId,
                String medicationName,
                Integer unit,
                String disposalReason,
                LocalDateTime createdDatetime,
                LocalDateTime updatedDatetime,
                String createdBy,
                String updatedBy) {

}
