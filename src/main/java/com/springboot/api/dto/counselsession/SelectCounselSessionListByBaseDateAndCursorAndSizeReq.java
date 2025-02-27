package com.springboot.api.dto.counselsession;


import lombok.Builder;

import java.time.LocalDate;

@Builder
public record SelectCounselSessionListByBaseDateAndCursorAndSizeReq(
        int size,
        String cursor,
        LocalDate baseDate
) {
}
