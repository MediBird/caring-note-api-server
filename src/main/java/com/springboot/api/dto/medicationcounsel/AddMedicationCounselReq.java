package com.springboot.api.dto.medicationcounsel;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AddMedicationCounselReq {
    @NotBlank
    private String counselSessionId;
    private String counselRecord;
    private List<MedicationCounselHighlightDTO> counselRecordHighlights;
}
