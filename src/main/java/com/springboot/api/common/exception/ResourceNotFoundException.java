package com.springboot.api.common.exception;

import com.springboot.api.common.message.ExceptionMessages;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super(ExceptionMessages.DUPLICATED_USER_EMAIL);
    }
}
