package com.springboot.api.dto.wasteMedication;

import java.util.List;

import com.springboot.api.domain.WasteMedicationRecord;

public record SelectMedicationRecordListBySessionIdRes(List<WasteMedicationRecord> wasteMedicationRecords) {

}
