package com.springboot.api.counselsession.dto.counselsession;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record SelectCounselSessionListByBaseDateAndCursorAndSizeReq(
    int size,
    String cursor,
    LocalDate baseDate) {

}
