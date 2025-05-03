package com.springboot.api.tus.util;

import io.micrometer.common.util.StringUtils;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;

public class TusUtil {

    public static Map<String, String> parseMetadata(@NonNull String metadata) {
        return Arrays.stream(Optional.of(metadata).filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new RuntimeException("metadata 가 없습니다."))
                .split(","))
            .map(keyAndValue -> keyAndValue.split(" "))
            .collect(
                Collectors.toMap(values -> values[0], values -> new String(Base64.getDecoder().decode(values[1]))));
    }
}
