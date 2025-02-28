package com.springboot.api.counselsession.enums.wasteMedication;

public enum RecoveryAgreementType {
    AGREE("회수 동의"),
    DISAGREE("회수 미동의");

    private final String label;

    RecoveryAgreementType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
