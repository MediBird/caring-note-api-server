package com.springboot.api.dto.aiCounselSummary;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AnalyseTextReq {
    @NotBlank
    private String counselSessionId;
    private List<String> speakers;
}
