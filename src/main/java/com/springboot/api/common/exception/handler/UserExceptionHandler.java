package com.springboot.api.common.exception.handler;

import com.springboot.api.common.dto.ErrorRes;
import com.springboot.api.common.exception.DuplicatedEmailException;
import com.springboot.api.common.exception.InvalidPasswordException;
import com.springboot.api.common.message.ExceptionMessages;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(1)
public class UserExceptionHandler extends CommonHandler{

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorRes handleUsernameNotFound(UsernameNotFoundException ex) {

        return buildErrorResponse(ExceptionMessages.USER_NOT_FOUND);
    }

    @ExceptionHandler(DuplicatedEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRes handleDuplicatedEmail(DuplicatedEmailException ex) {

        return buildErrorResponse(ExceptionMessages.DUPLICATED_USER_EMAIL);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorRes handleInvalidPassword(InvalidPasswordException ex) {
        return buildErrorResponse(ExceptionMessages.INVALID_PASSWORD);
    }
}
