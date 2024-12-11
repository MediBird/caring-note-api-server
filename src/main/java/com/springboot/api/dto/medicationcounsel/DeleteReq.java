package com.springboot.api.dto.medicationcounsel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteReq {

    @NotBlank
    private String medicationCounselId;

    @JsonCreator
    public DeleteReq(@JsonProperty("medicationCounselId") String medicationCounselId) {
        this.medicationCounselId = medicationCounselId;
    }
}
