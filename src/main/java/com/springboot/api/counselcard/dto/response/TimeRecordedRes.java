package com.springboot.api.counselcard.dto.response;

import lombok.Getter;

@Getter
public class TimeRecordedRes<T>{

    private final String counselDate;
    private final T data;

    public TimeRecordedRes(String counselDate, T counselPurposeAndNote) {
        this.counselDate = counselDate;
        this.data = counselPurposeAndNote;
    }
}
