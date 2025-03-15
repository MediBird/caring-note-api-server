package com.springboot.api.counselcard.dto;

import lombok.Getter;

@Getter
public class TimeRecordedDTO<T>{

    private final String counselDate;
    private final T data;

    public TimeRecordedDTO(String counselDate, T counselPurposeAndNote) {
        this.counselDate = counselDate;
        this.data = counselPurposeAndNote;
    }
}
