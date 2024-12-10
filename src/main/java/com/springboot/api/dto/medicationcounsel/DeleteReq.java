package com.springboot.api.dto.medicationcounsel;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteReq {

    @NotBlank
    private String medicationCounselId;
}
