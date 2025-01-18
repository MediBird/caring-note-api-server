package com.springboot.api.dto.medicationcounsel;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdateMedicationCounselReq {
    @NotBlank
    private String medicationCounselId;

    private String counselRecord;

    private List<String> counselRecordHighlights;

}
