package com.springboot.api.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommonCursorRes<T> {

    private T data;
    private String nextCursor;
    private boolean hasNext;
}