package com.springboot.api.common.exception.handler;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.api.common.dto.ErrorRes;
import com.springboot.api.common.message.ExceptionMessages;
import com.springboot.api.common.message.HttpMessages;

import jakarta.persistence.EntityExistsException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
@Order(2)
public class GlobalExceptionHandler extends CommonHandler {

    // 400 - 잘못된 요청 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRes handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        return buildErrorResponse(HttpMessages.INVALID_REQUEST_BODY);
    }

    // 잘못된 요청 파라미터 처리
    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRes handleBadRequest(Exception ex) {
        log.error("Bad request: {}", ex.getMessage());
        return buildErrorResponse(HttpMessages.BAD_REQUEST_PARAMETER);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorRes handleNotFound(Exception ex) {
        log.error("Resource not found: {}", ex.getMessage());
        return buildErrorResponse(HttpMessages.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorRes handleAccessDenied(Exception ex) {
        log.error("Access denied: {}", ex.getMessage());
        return buildErrorResponse(HttpMessages.UNAUTHORIZED);
    }

    // JPA 엔터티가 이미 존재할 때 처리
    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorRes handleEntityExistsException(EntityExistsException ex) {
        log.error("Entity exists: {}", ex.getMessage());
        return buildErrorResponse(HttpMessages.CONFLICT_DUPLICATE);
    }

    @ExceptionHandler(JsonProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorRes handleJsonProcessingException(JsonProcessingException ex) {
        log.error("JSON processing error: {}", ex.getMessage());
        return buildErrorResponse(ExceptionMessages.FAIL_JSON_CONVERT);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRes handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("Message not readable: {}", ex.getMessage());
        return buildErrorResponse(HttpMessages.BAD_REQUEST);
    }

    // 500 - 서버 에러 처리
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorRes handleGeneralException(Exception ex) {
        log.error("Internal server error: {}", ex.getMessage());
        return buildErrorResponse(HttpMessages.INTERNAL_SERVER_ERROR);
    }
}
