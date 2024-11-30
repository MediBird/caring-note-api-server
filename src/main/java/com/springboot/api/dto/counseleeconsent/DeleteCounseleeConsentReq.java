package com.springboot.api.dto.counseleeconsent;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteCounseleeConsentReq {
    private String counseleeConsentId;
}
