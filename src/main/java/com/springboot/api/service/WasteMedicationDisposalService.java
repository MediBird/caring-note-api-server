package com.springboot.api.service;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.WasteMedicationDisposal;
import com.springboot.api.dto.wasteMedicationDisposal.WasteMedicationDisposalReq;
import com.springboot.api.repository.CounselSessionRepository;
import com.springboot.api.repository.WasteMedicationDisposalRepository;
import com.springboot.enums.wasteMedication.UnusedReasonType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WasteMedicationDisposalService {

    private final WasteMedicationDisposalRepository wasteMedicationDisposalRepository;
    private final CounselSessionRepository counselSessionRepository;

    public String save(String counselSessionId, WasteMedicationDisposalReq wasteMedicationDisposalReq) {
        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
                .orElseThrow(() -> new IllegalArgumentException("CounselSession not found"));
        WasteMedicationDisposal wasteMedicationDisposal;
        if (wasteMedicationDisposalRepository.findByCounselSessionId(counselSessionId).isPresent()) {
            wasteMedicationDisposal = wasteMedicationDisposalRepository
                    .findByCounselSessionId(counselSessionId).get();
        } else {
            wasteMedicationDisposal = new WasteMedicationDisposal();
        }
        wasteMedicationDisposal.setCounselSession(counselSession);
        wasteMedicationDisposal.setUnusedReasons(wasteMedicationDisposalReq.getUnusedReasonTypes().stream()
                .map(UnusedReasonType::name)
                .collect(Collectors.toList()));
        wasteMedicationDisposal.setUnusedReasonDetail(wasteMedicationDisposalReq.getUnusedReasonDetail());
        wasteMedicationDisposal.setDrugRemainActionType(wasteMedicationDisposalReq.getDrugRemainActionType());
        wasteMedicationDisposal.setDrugRemainActionDetail(wasteMedicationDisposalReq.getDrugRemainActionDetail());
        wasteMedicationDisposal.setRecoveryAgreementType(wasteMedicationDisposalReq.getRecoveryAgreementType());
        wasteMedicationDisposal.setWasteMedicationGram(wasteMedicationDisposalReq.getWasteMedicationGram());
        return wasteMedicationDisposalRepository.save(wasteMedicationDisposal).getId();
    }

    public WasteMedicationDisposal get(String counselSessionId) {
        return wasteMedicationDisposalRepository.findByCounselSessionId(counselSessionId)
                .orElseThrow(() -> new NoContentException("WasteMedicationDisposal not found"));
    }

    public void delete(String counselSessionId) {
        wasteMedicationDisposalRepository.deleteByCounselSessionId(counselSessionId);
    }
}