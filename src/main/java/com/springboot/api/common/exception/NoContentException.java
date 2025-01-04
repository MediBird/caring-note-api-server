package com.springboot.api.common.exception;

import com.springboot.api.common.message.ExceptionMessages;

public class NoContentException extends RuntimeException {

    public NoContentException() {
        super(ExceptionMessages.NO_CONTENT);
    }

    public NoContentException(String message) {
        super(message);
    }
}
