package com.springboot.api.counselsession.dto.aiCounselSummary;


import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AnalyseTextReq {

    @NotBlank
    private String counselSessionId;
}
