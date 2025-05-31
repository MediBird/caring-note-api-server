package com.springboot.api.counselcard.dto.response;

import java.util.List;

public record MainCounselRecord<T>(
    T currentState,
    List<TimeRecordedRes<T>> history
) {

}


