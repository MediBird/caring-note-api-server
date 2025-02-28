package com.springboot.api.counselsession.dto.wasteMedication;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SelectMedicationRecordListBySessionIdReq {

    @NotBlank
    private String counselSessionId;
}
