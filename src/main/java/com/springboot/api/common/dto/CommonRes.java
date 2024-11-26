package com.springboot.api.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommonRes<T> {
    private T data;
}
