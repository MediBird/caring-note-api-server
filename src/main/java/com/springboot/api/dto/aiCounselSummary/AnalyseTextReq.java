package com.springboot.api.dto.aiCounselSummary;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class AnalyseTextReq {
    @NotBlank
    private String counselSessionId;
    private List<String> speakers;
}
