package com.springboot.api.dto.counselsession;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class SelectCounselSessionListByBaseDateAndCursorAndSizeReq {

    private int size;
    private String cursor;
    private LocalDate baseDate;


}
