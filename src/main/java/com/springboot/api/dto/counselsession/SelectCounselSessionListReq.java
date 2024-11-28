package com.springboot.api.dto.counselsession;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SelectCounselSessionListReq {

    private int size;
    private String cursor;
    private LocalDateTime baseDateTime;


}
