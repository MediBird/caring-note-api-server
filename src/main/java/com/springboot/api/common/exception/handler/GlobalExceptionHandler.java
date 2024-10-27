package com.springboot.api.common.exception.handler;

import com.springboot.api.common.dto.ErrorRes;
import com.springboot.api.common.message.HttpMessages;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
@Order(2)
public class GlobalExceptionHandler extends CommonHandler {


    // 400 - 잘못된 요청 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRes> handleValidationException(MethodArgumentNotValidException ex) {
        String message = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        return buildErrorResponse(HttpStatus.BAD_REQUEST, HttpMessages.BAD_REQUEST);
    }

    // 잘못된 요청 파라미터 처리
    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorRes> handleBadRequest(Exception ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, HttpMessages.BAD_REQUEST_PARAMETER);
    }

    // Constraint Validation 실패
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorRes> handleConstraintViolation(ConstraintViolationException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, HttpMessages.INVALID_REQUEST_BODY);
    }

    // 500 - 서버 에러 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRes> handleGeneralException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, HttpMessages.INTERNAL_SERVER_ERROR);
    }

}
