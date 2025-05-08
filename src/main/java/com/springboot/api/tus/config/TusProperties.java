package com.springboot.api.tus.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "tus")
@Getter
@Setter
public class TusProperties {

    String uploadPath;
    String extension;
    String mergePath;
}
