package com.springboot.api.common.exception;

import com.springboot.api.common.message.ExceptionMessages;

/**
 * 콘텐츠가 없을 때 발생하는 예외
 * 스택트레이스를 생성하지 않아 성능을 최적화하고 정확한 정보만 전달합니다.
 */
public class NoContentException extends RuntimeException {

    public NoContentException() {
        super(ExceptionMessages.NO_CONTENT, null, false, false);
    }

    public NoContentException(String message) {
        super(message, null, false, false);
    }
    
    /**
     * 원인과 함께 예외를 생성하는 생성자
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public NoContentException(String message, Throwable cause) {
        super(message, cause, false, false);
    }
}
