package com.springboot.api.counselsession.dto.wasteMedicationDisposal;

import java.util.List;

import com.springboot.api.counselsession.enums.wasteMedication.DrugRemainActionType;
import com.springboot.api.counselsession.enums.wasteMedication.RecoveryAgreementType;
import com.springboot.api.counselsession.enums.wasteMedication.UnusedReasonType;

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
