package com.springboot.api.common.exception;

import com.springboot.api.common.message.ExceptionMessages;

public class DuplicatedEmailException extends RuntimeException {
    // 기본 생성자
    public DuplicatedEmailException() {
        super(ExceptionMessages.DUPLICATED_USER_EMAIL);
    }

}
