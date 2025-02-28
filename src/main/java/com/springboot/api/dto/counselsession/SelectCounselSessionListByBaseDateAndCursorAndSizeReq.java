package com.springboot.api.dto.counselsession;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record SelectCounselSessionListByBaseDateAndCursorAndSizeReq(
                int size,
                String cursor,
                LocalDate baseDate) {
}
