package com.springboot.api.common.exception.handler;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.api.common.dto.CommonRes;
import com.springboot.api.common.dto.ErrorRes;
import com.springboot.api.common.dto.ValidationError;
import com.springboot.api.common.dto.ValidationErrorRes;
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
    public ResponseEntity<CommonRes<ValidationErrorRes>> handleValidationException(
        MethodArgumentNotValidException ex) {
        List<ValidationError> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> ValidationError.builder()
                .field(error.getField())
                .message(error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid request")
                .build())
            .collect(Collectors.toList());

        ValidationErrorRes errorResponse = ValidationErrorRes.builder()
            .errors(errors)
            .build();

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new CommonRes<>(errorResponse));
    }

    // 잘못된 요청 파라미터 처리
    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class,
        IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRes handleBadRequest(Exception ex) {
        return buildErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorRes handleNotFound(Exception ex) {
        return buildErrorResponse(HttpMessages.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorRes handleAccessDenied(Exception ex) {
        return buildErrorResponse(HttpMessages.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorRes handleAuthenticationCredentialsNotFoundException(Exception ex) {
        return buildErrorResponse(HttpMessages.UNAUTHORIZED);
    }

    // JPA 엔터티가 이미 존재할 때 처리
    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorRes handleEntityExistsException(EntityExistsException ex) {
        return buildErrorResponse(HttpMessages.CONFLICT_DUPLICATE);
    }

    @ExceptionHandler(JsonProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorRes handleJsonProcessingException(JsonProcessingException ex) {
        return buildErrorResponse(ExceptionMessages.FAIL_JSON_CONVERT);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRes handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return buildErrorResponse(HttpMessages.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<CommonRes<ValidationErrorRes>> handleMethodValidationException(
        HandlerMethodValidationException ex) {
        List<ValidationError> errors = ex.getValueResults()
            .stream()
                .map(result -> ValidationError.builder()
                        .field(result.getMethodParameter().getParameterName())
                        .message(result.getResolvableErrors().stream()
                    .findFirst()
                    .map(err -> err.getDefaultMessage())
                    .orElse("Invalid request"))
                .build())
            .collect(Collectors.toList());

        ValidationErrorRes errorResponse = ValidationErrorRes.builder()
            .errors(errors)
            .build();

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new CommonRes<>(errorResponse));
    }

    // 500 - 서버 에러 처리
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorRes handleGeneralException(Exception ex) {
        return buildErrorResponse(HttpMessages.INTERNAL_SERVER_ERROR);
    }
}
