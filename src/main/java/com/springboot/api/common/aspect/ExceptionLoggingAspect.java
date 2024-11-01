package com.springboot.api.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExceptionLoggingAspect {

    // @ExceptionHandler 메서드를 포인트컷으로 지정
    @Pointcut("@annotation(org.springframework.web.bind.annotation.ExceptionHandler)")
    public void exceptionHandlerMethods() {}

    // 모든 예외 처리 핸들러 실행 전 logExceptionMessage 로직 실행
    @Before("exceptionHandlerMethods() && args(ex)")
    public void logExceptionMessage(Exception ex) {
        log.error("An error occurred: {}", ex.getMessage(), ex);
    }
}
