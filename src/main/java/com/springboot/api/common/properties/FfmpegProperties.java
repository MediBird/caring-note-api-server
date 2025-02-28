package com.springboot.api.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "ffmpeg")
@Getter
@Setter
public class FfmpegProperties {
    private String path;
}
