package com.springboot.enums;

public enum MedicationUsageStatus {
    REGULAR("상시 복용"), // 상시 복용
    AS_NEEDED("필요 시 복용"), // 필요 시 복용 
    STOPPED("복용 중단"); // 복용 중단

    private final String description;

    MedicationUsageStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
