package com.springboot.api.common.exception;

import com.springboot.api.common.message.ExceptionMessages;

public class JsonConvertException extends RuntimeException {

    public JsonConvertException() {
        super(ExceptionMessages.FAIL_JSON_CONVERT);
    }
}
