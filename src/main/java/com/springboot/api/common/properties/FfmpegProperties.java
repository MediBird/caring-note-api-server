package com.springboot.api.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="ffmpeg")
@Getter
@Setter
public class FfmpegProperties {
    private String path;
}
