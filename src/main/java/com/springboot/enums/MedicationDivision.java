package com.springboot.enums;

public enum MedicationDivision {
    PRESCRIPTION("처방 의약품"),
    OTC("일반 의약품");

    private final String description;

    MedicationDivision(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
