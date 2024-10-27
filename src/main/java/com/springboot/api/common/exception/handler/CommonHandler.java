package com.springboot.api.common.exception.handler;

import com.springboot.api.common.dto.ErrorRes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CommonHandler {

    // 예외 발생 시 기본 처리 로직
    protected ResponseEntity<ErrorRes> buildErrorResponse(HttpStatus status, String message) {
        ErrorRes errorRes = new ErrorRes(message);
        return new ResponseEntity<>(errorRes, status);
    }
}
