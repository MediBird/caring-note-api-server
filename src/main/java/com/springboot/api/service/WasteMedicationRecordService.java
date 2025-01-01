package com.springboot.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.WasteMedicationRecord;
import com.springboot.api.dto.wasteMedication.AddAndUpdateWasteMedicationRecordReq;
import com.springboot.api.dto.wasteMedication.AddAndUpdateWasteMedicationRecordRes;
import com.springboot.api.dto.wasteMedication.SelectMedicationRecordListBySessionIdRes;
import com.springboot.api.repository.CounselSessionRepository;
import com.springboot.api.repository.MedicationRepository;
import com.springboot.api.repository.WasteMedicationRecordRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WasteMedicationRecordService {

    private final WasteMedicationRecordRepository wasteMedicationRecordRepository;
    private final CounselSessionRepository counselSessionRepository;
    private final MedicationRepository medicationRepository;

    public List<SelectMedicationRecordListBySessionIdRes> getWasteMedicationRecord(String counselSessionId) {
        List<WasteMedicationRecord> wasteMedicationRecords = wasteMedicationRecordRepository
                .findByCounselSessionId(counselSessionId);
        return wasteMedicationRecords.stream()
                .map(wasteMedicationRecord -> new SelectMedicationRecordListBySessionIdRes(
                wasteMedicationRecord.getId(),
                wasteMedicationRecord.getMedication().getId(),
                wasteMedicationRecord.getMedicationName(),
                wasteMedicationRecord.getUnit(),
                wasteMedicationRecord.getDisposalReason(),
                wasteMedicationRecord.getCreatedDatetime(),
                wasteMedicationRecord.getUpdatedDatetime(),
                wasteMedicationRecord.getCreatedBy(),
                wasteMedicationRecord.getUpdatedBy()))
                .collect(Collectors.toList());
    }

    public WasteMedicationRecord getWasteMedicationRecordByCounselSessionIdAndMedicationId(String counselSessionId,
            String medicationId) {
        return wasteMedicationRecordRepository.findByCounselSessionIdAndMedicationId(counselSessionId, medicationId);
    }

    public List<AddAndUpdateWasteMedicationRecordRes> addAndUpdateWasteMedicationRecords(String counselSessionId,
            List<AddAndUpdateWasteMedicationRecordReq> addAndUpdateWasteMedicationRecordReqs) {
        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid counsel session ID"));
        List<WasteMedicationRecord> wasteMedicationRecords = new ArrayList<>();
        WasteMedicationRecord wasteMedicationRecord;
        for (AddAndUpdateWasteMedicationRecordReq addAndUpdateWasteMedicationRecordReq : addAndUpdateWasteMedicationRecordReqs) {
            if (addAndUpdateWasteMedicationRecordReq.getRowId() != null) {

                wasteMedicationRecord = WasteMedicationRecord.builder()
                        .counselSession(counselSession)
                        .medication(
                                medicationRepository.findById(addAndUpdateWasteMedicationRecordReq.getMedicationId())
                                        .orElse(null))
                        .unit(addAndUpdateWasteMedicationRecordReq.getUnit())
                        .disposalReason(addAndUpdateWasteMedicationRecordReq.getDisposalReason())
                        .medicationName(addAndUpdateWasteMedicationRecordReq.getMedicationName())
                        .build();
            } else {
                wasteMedicationRecord = wasteMedicationRecordRepository
                        .findById(addAndUpdateWasteMedicationRecordReq.getRowId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid waste medication record ID"));
                wasteMedicationRecord.setMedication(
                        medicationRepository.findById(addAndUpdateWasteMedicationRecordReq.getMedicationId())
                                .orElse(null));
                wasteMedicationRecord.setUnit(addAndUpdateWasteMedicationRecordReq.getUnit());
                wasteMedicationRecord.setDisposalReason(addAndUpdateWasteMedicationRecordReq.getDisposalReason());
                wasteMedicationRecord.setMedicationName(addAndUpdateWasteMedicationRecordReq.getMedicationName());
            }
            wasteMedicationRecords.add(wasteMedicationRecord);
        }
        wasteMedicationRecordRepository.saveAll(wasteMedicationRecords);

        return wasteMedicationRecords.stream()
                .map(savedWasteMedicationRecord -> new AddAndUpdateWasteMedicationRecordRes(savedWasteMedicationRecord.getId()))
                .collect(Collectors.toList());
    }

    // public AddWasteMedicationRecordRes addWasteMedicationRecord(String counselSessionId, AddWasteMedicationRecordReq addWasteMedicationRecordReq) {
    //     CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
    //             .orElseThrow(() -> new IllegalArgumentException("Invalid counsel session ID"));
    //     Medication medication = medicationRepository.findById(addWasteMedicationRecordReq.getMedicationId())
    //             .orElseThrow(() -> new IllegalArgumentException("Invalid medication ID"));
    //     WasteMedicationRecord wasteMedicationRecord = WasteMedicationRecord.builder()
    //             .counselSession(counselSession)
    //             .medication(medication)
    //             .unit(addWasteMedicationRecordReq.getUnit())
    //             .disposalReason(addWasteMedicationRecordReq.getDisposalReason())
    //             .build();
    //     WasteMedicationRecord savedWasteMedicationRecord = wasteMedicationRecordRepository.save(wasteMedicationRecord);
    //     return new AddWasteMedicationRecordRes(savedWasteMedicationRecord.getId());
    // }
    // public List<AddWasteMedicationRecordRes> addWasteMedicationRecords(String counselSessionId,
    //         List<AddWasteMedicationRecordReq> addWasteMedicationRecordReqs) {
    //     CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
    //             .orElseThrow(() -> new IllegalArgumentException("Invalid counsel session ID"));
    //     List<WasteMedicationRecord> wasteMedicationRecords = addWasteMedicationRecordReqs.stream()
    //             .map(addWasteMedicationRecordReq -> {
    //                 Medication medication = medicationRepository.findById(addWasteMedicationRecordReq.getMedicationId())
    //                         .orElseThrow(() -> new IllegalArgumentException("Invalid medication ID"));
    //                 return WasteMedicationRecord.builder()
    //                         .counselSession(counselSession)
    //                         .medication(medication)
    //                         .unit(addWasteMedicationRecordReq.getUnit())
    //                         .disposalReason(addWasteMedicationRecordReq.getDisposalReason())
    //                         .build();
    //             })
    //             .collect(Collectors.toList());
    //     List<WasteMedicationRecord> savedWasteMedicationRecords = wasteMedicationRecordRepository.saveAll(wasteMedicationRecords);
    //     return savedWasteMedicationRecords.stream()
    //             .map(wasteMedicationRecord -> new AddWasteMedicationRecordRes(wasteMedicationRecord.getId()))
    //             .collect(Collectors.toList());
    // }
    // public List<WasteMedicationRecord> updateWasteMedicationRecords(
    //         List<WasteMedicationRecord> wasteMedicationRecords) {
    //     return wasteMedicationRecordRepository.saveAll(wasteMedicationRecords);
    // }
    public void deleteWasteMedicationRecords(String counselSessionId, List<String> wasteMedicationRecords) {
        wasteMedicationRecordRepository.deleteAllById(wasteMedicationRecords);
    }

    public WasteMedicationRecord updateWasteMedicationRecord(WasteMedicationRecord wasteMedicationRecord) {
        return wasteMedicationRecordRepository.save(wasteMedicationRecord);
    }

    public void deleteWasteMedicationRecord(String counselSessionId, String id) {
        wasteMedicationRecordRepository.deleteById(id);
    }
}
