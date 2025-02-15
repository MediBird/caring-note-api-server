package com.springboot.api.dto.medicationcounsel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AddMedicationCounselReq {
    @NotBlank(message = "상담 세션 ID는 필수 입력값입니다")
    @Size(min = 26, max = 26, message = "상담 세션 ID는 26자여야 합니다")
    private String counselSessionId;

    @NotBlank(message = "상담 노트는 필수 입력값입니다")
    private String counselRecord;

    private List<MedicationCounselHighlightDTO> counselRecordHighlights;
}
