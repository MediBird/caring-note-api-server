package com.springboot.api.common.exception.handler;

import com.springboot.api.common.dto.ErrorRes;
import com.springboot.api.common.exception.DuplicatedEmailException;
import com.springboot.api.common.exception.InvalidPasswordException;
import com.springboot.api.common.exception.JsonConvertException;
import com.springboot.api.common.exception.NoContentException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(1)
public class UserExceptionHandler extends CommonHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorRes handleUsernameNotFound(UsernameNotFoundException ex) {

        return buildErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(DuplicatedEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRes handleDuplicatedEmail(DuplicatedEmailException ex) {

        return buildErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorRes handleInvalidPassword(InvalidPasswordException ex) {
        return buildErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(NoContentException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleNoContent(NoContentException ex) {

    }

    @ExceptionHandler(JsonConvertException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorRes handleJsonConvertException(JsonConvertException ex) {
        return buildErrorResponse(ex.getMessage());
    }

}
