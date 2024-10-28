package com.springboot.api.common.exception;

import com.springboot.api.common.message.ExceptionMessages;

public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException() {
        super(ExceptionMessages.INVALID_PASSWORD);
    }

}
