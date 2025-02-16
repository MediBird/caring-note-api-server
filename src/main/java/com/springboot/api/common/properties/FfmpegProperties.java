package com.springboot.api.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="stt.file.path")
@Getter
@Setter
public class FfmpegProperties {
    private String path;
}
