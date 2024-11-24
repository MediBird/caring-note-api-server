package com.springboot.api.common.annotation;

import com.springboot.enums.RoleType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE}) // 클래스나 메서드에 적용
@Retention(RetentionPolicy.RUNTIME) // 런타임에 유지
public @interface RoleSecured {
    RoleType[] value(); // 적용할 권한을 지정할 수 있도록
}
