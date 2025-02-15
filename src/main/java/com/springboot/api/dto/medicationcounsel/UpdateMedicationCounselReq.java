package com.springboot.api.dto.medicationcounsel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdateMedicationCounselReq {
    @NotBlank(message = "상담 노트 ID는 필수 입력값입니다")
    @Size(min = 26, max = 26, message = "상담 노트 ID는 26자여야 합니다")
    private String medicationCounselId;

    @NotBlank(message = "상담 노트는 필수 입력값입니다")
    private String counselRecord;

    @NotEmpty(message = "상담 노트 하이라이트는 필수 입력값입니다")
    private List<MedicationCounselHighlightDTO> counselRecordHighlights;

}
