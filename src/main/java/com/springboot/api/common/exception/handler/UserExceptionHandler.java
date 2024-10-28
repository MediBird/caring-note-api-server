package com.springboot.api.common.exception.handler;

import com.springboot.api.common.dto.ErrorRes;
import com.springboot.api.common.exception.DuplicatedEmailException;
import com.springboot.api.common.exception.InvalidPasswordException;
import com.springboot.api.common.message.ExceptionMessages;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(1)
public class UserExceptionHandler extends CommonHandler{

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorRes> handleUsernameNotFound(UsernameNotFoundException ex) {

        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ExceptionMessages.USER_NOT_FOUND);
    }

    @ExceptionHandler(DuplicatedEmailException.class)
    public ResponseEntity<ErrorRes> handleDuplicatedEmail(DuplicatedEmailException ex) {

        return buildErrorResponse(HttpStatus.BAD_REQUEST, ExceptionMessages.DUPLICATED_USER_EMAIL);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorRes> handleInvalidPassword(InvalidPasswordException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ExceptionMessages.INVALID_PASSWORD);
    }
}
