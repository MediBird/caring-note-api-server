package com.springboot.enums.wasteMedication;

public enum UnusedReasonType {
    /**
     * 상태 호전으로 필요 없음
     */
    RECOVERED("상태가 호전되어 먹을 필요가 없어짐"),
    /**
     * 부작용
     */
    SIDE_EFFECT("부작용이 나타나 사용 중단함"),
    /**
     * 재처방
     */
    RETREATED("재처방 받음"),
    /**
     * 다른 약으로 대체
     */
    REPLACED("다른 약으로 대체함"),
    /**
     * 복용을 깜빡함
     */
    FORGOTTEN("약 먹는 것을 잊어버림"),
    /**
     * 필요시 복용하려고 남겨둠
     */
    RESERVED("필요시 복용하려고 남겨둠"),
    /**
     * 기타
     */
    ETC("기타");

    private final String label;

    UnusedReasonType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
