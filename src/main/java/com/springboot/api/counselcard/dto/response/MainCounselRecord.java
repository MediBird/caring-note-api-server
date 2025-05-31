package com.springboot.api.counselcard.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MainCounselRecord<T> {

    private final T currentState;
    private final List<TimeRecordedRes<T>> history;
}


