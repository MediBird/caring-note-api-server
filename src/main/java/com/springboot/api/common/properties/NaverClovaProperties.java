package com.springboot.api.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "naver.clova")
@Getter
@Setter
public class NaverClovaProperties {
    private String apiKey;
}
