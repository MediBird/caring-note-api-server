package com.springboot.api.common.dto;

import com.springboot.api.common.message.HttpMessages;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommonRes<T> {
    private String message;
    private T data;

    public CommonRes(T data) {
        this.message = HttpMessages.SUCCESS;
        this.data = data;
    }

}
