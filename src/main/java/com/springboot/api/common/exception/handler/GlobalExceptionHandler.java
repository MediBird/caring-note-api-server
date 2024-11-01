package com.springboot.api.common.exception.handler;

import com.springboot.api.common.dto.ErrorRes;
import com.springboot.api.common.message.HttpMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
@Slf4j
@Order(2)
public class GlobalExceptionHandler extends CommonHandler {


    // 400 - 잘못된 요청 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRes handleValidationException(MethodArgumentNotValidException ex) {
        return buildErrorResponse(HttpMessages.INVALID_REQUEST_BODY);
    }

    // 잘못된 요청 파라미터 처리
    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRes handleBadRequest(Exception ex) {
        return buildErrorResponse(HttpMessages.BAD_REQUEST_PARAMETER);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorRes handleNotFound(Exception ex) {
        return buildErrorResponse(HttpMessages.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorRes handleAccessDenied(Exception ex) {
        return buildErrorResponse(HttpMessages.UNAUTHORIZED);
    }



    // 500 - 서버 에러 처리
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorRes handleGeneralException(Exception ex) {
        return buildErrorResponse(HttpMessages.INTERNAL_SERVER_ERROR);
    }

}
