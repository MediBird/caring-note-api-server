package com.springboot.api.tus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "tus")
@Data
public class TusProperties {
    private String uploadPath;
    private String extension;
    private String mergePath;
    private String pathPrefix;
}
