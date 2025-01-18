package com.springboot.api.dto.medicationcounsel;

import java.util.List;

public record SelectMedicationCounselRes(
        String medicationCounselId
        ,String counselRecord
        ,List<String>counselRecordHighlights
){}
