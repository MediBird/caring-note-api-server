package com.springboot.api.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@RestController
@RequestMapping
@Tag(name = "")
public @interface ApiController {
    @AliasFor(annotation = RequestMapping.class, attribute = "value")
    String path();

    @AliasFor(annotation = Tag.class, attribute = "name")
    String name();

    @AliasFor(annotation = Tag.class, attribute = "description")
    String description();
}
