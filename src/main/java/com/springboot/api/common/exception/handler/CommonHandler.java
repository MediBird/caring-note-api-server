package com.springboot.api.common.exception.handler;

import com.springboot.api.common.dto.ErrorRes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonHandler {

    // 예외 발생 시 기본 처리 로직
    protected ErrorRes buildErrorResponse(String message) {
        return new ErrorRes(message);
    }

}
