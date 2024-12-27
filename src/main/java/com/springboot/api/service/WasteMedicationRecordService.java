package com.springboot.api.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.Medication;
import com.springboot.api.domain.WasteMedicationRecord;
import com.springboot.api.dto.wasteMedication.AddWasteMedicationRecordReq;
import com.springboot.api.dto.wasteMedication.AddWasteMedicationRecordRes;
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

    public SelectMedicationRecordListBySessionIdRes getWasteMedicationRecord(String counselSessionId) {
        List<WasteMedicationRecord> wasteMedicationRecords = wasteMedicationRecordRepository
                .findByCounselSessionId(counselSessionId);
        SelectMedicationRecordListBySessionIdRes selectMedicationRecordListBySessionIdRes = new SelectMedicationRecordListBySessionIdRes(wasteMedicationRecords);
        return selectMedicationRecordListBySessionIdRes;
    }

    public WasteMedicationRecord getWasteMedicationRecordByCounselSessionIdAndMedicationId(String counselSessionId, String medicationId) {
        return wasteMedicationRecordRepository.findByCounselSessionIdAndMedicationId(counselSessionId, medicationId);
    }

    public WasteMedicationRecord getWasteMedicationRecordByCounselSessionIdAndMedicationIdAndMedicationDate(
            String counselSessionId, String medicationId, ZonedDateTime medicationDateTime) {
        return wasteMedicationRecordRepository.findByCounselSessionIdAndMedicationIdAndMedicationDateTime(
                counselSessionId, medicationId, medicationDateTime);
    }

    public AddWasteMedicationRecordRes createWasteMedicationRecord(AddWasteMedicationRecordReq addWasteMedicationRecordReq) {
        CounselSession counselSession = counselSessionRepository.findById(addWasteMedicationRecordReq.getCounselSessionId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid counsel session ID"));

        Medication medication = medicationRepository.findById(addWasteMedicationRecordReq.getMedicationId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid medication ID"));

        WasteMedicationRecord wasteMedicationRecord = WasteMedicationRecord.builder()
                .counselSession(counselSession)
                .medication(medication)
                .unit(addWasteMedicationRecordReq.getUnit())
                .disposalReason(addWasteMedicationRecordReq.getDisposalReason())
                .build();
        WasteMedicationRecord savedWasteMedicationRecord = wasteMedicationRecordRepository.save(wasteMedicationRecord);
        return new AddWasteMedicationRecordRes(savedWasteMedicationRecord.getId());
    }

    public List<AddWasteMedicationRecordRes> createWasteMedicationRecords(
            List<AddWasteMedicationRecordReq> addWasteMedicationRecordReqs) {
        List<WasteMedicationRecord> wasteMedicationRecords = addWasteMedicationRecordReqs.stream()
                .map(addWasteMedicationRecordReq -> WasteMedicationRecord.builder()
                .counselSession(counselSessionRepository.findById(addWasteMedicationRecordReq.getCounselSessionId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid counsel session ID")))
                .medication(medicationRepository.findById(addWasteMedicationRecordReq.getMedicationId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid medication ID")))
                .unit(addWasteMedicationRecordReq.getUnit())
                .disposalReason(addWasteMedicationRecordReq.getDisposalReason())
                .build())
                .collect(Collectors.toList());
        List<WasteMedicationRecord> savedWasteMedicationRecords = wasteMedicationRecordRepository.saveAll(wasteMedicationRecords);
        return savedWasteMedicationRecords.stream()
                .map(wasteMedicationRecord -> new AddWasteMedicationRecordRes(wasteMedicationRecord.getId()))
                .collect(Collectors.toList());
    }

    public List<WasteMedicationRecord> updateWasteMedicationRecords(
            List<WasteMedicationRecord> wasteMedicationRecords) {
        return wasteMedicationRecordRepository.saveAll(wasteMedicationRecords);
    }

    public List<WasteMedicationRecord> deleteWasteMedicationRecords(List<WasteMedicationRecord> wasteMedicationRecords) {
        wasteMedicationRecordRepository.deleteAll(wasteMedicationRecords);
        return wasteMedicationRecords;
    }

    public WasteMedicationRecord updateWasteMedicationRecord(WasteMedicationRecord wasteMedicationRecord) {
        return wasteMedicationRecordRepository.save(wasteMedicationRecord);
    }

    public void deleteWasteMedicationRecord(Long id) {
        wasteMedicationRecordRepository.deleteById(id);
    }
}
