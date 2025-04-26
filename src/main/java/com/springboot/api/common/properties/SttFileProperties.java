package com.springboot.api.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "stt.file.path")
@Getter
@Setter
public class SttFileProperties {

    private String origin;
    private String convert;
}
