package com.springboot.api.counselsession.enums.wasteMedication;

public enum DisposalMethodType {
    KEEP("쌓아둠"),
    TRASH("쓰레기통에 버림"),
    GIVE_AWAY("지인에게 나눠줌"),
    OFFICIAL("폐의약품 수거함 등 지정된 폐기 장소에 버림"),
    ETC("기타");

    private final String label;

    DisposalMethodType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
