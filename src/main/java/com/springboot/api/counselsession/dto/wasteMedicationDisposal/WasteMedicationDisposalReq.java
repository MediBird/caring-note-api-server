package com.springboot.api.dto.wasteMedicationDisposal;

import java.util.List;

import com.springboot.enums.wasteMedication.DrugRemainActionType;
import com.springboot.enums.wasteMedication.RecoveryAgreementType;
import com.springboot.enums.wasteMedication.UnusedReasonType;

import lombok.Data;

@Data
public class WasteMedicationDisposalReq {

    private List<UnusedReasonType> unusedReasonTypes;
    private String unusedReasonDetail;
    private DrugRemainActionType drugRemainActionType;
    private String drugRemainActionDetail;
    private RecoveryAgreementType recoveryAgreementType;
    private Integer wasteMedicationGram;
}
