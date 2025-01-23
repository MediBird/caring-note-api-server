package com.springboot.api.dto.medicationcounsel;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record MedicationCounselHighlightDTO(
        @NotBlank
        @JsonProperty(required = true)
        String highlight
        ,@NotNull
         @JsonProperty(required = true)
        Integer startIndex
        ,@NotNull
         @JsonProperty(required = true)
        Integer endIndex
){}
