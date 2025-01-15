package com.springboot.enums;

public enum MedicalCoverageType {
    HEALTH_INSURANCE("건강보험"),
    MEDICAL_CARE("의료급여"),
    VETERANS("보훈"),
    NON_PAYMENT("비급여");

    private final String description;

    MedicalCoverageType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
