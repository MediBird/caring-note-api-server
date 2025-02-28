package com.springboot.api.counselsession.enums.wasteMedication;

public enum DrugRemainActionType {
    /**
     * 의사 또는 약사의 지시에 따름
     */
    DOCTOR_OR_PHARMACIST("의/약사 지시"),
    /**
     * 본인 스스로의 판단
     */
    SELF_DECISION("본인판단"),
    /**
     * 폐의약품 없음
     */
    NONE("폐의약품 없음");

    private final String description;

    DrugRemainActionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
