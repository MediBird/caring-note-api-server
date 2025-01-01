package com.springboot.api.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.MedicationRecordHist;
import com.springboot.api.dto.medicationRecordHist.AddAndUpdateMedicationRecordHistReq;
import com.springboot.api.dto.medicationRecordHist.AddAndUpdateMedicationRecordHistRes;
import com.springboot.api.dto.medicationRecordHist.SelectMedicationRecordHistRes;
import com.springboot.api.repository.CounselSessionRepository;
import com.springboot.api.repository.MedicationRecordHistRepository;
import com.springboot.api.repository.MedicationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicationRecordHistService {

    private final MedicationRecordHistRepository medicationRecordHistRepository;

    private final CounselSessionRepository counselSessionRepository;

    private final MedicationRepository medicationRepository;

    public List<SelectMedicationRecordHistRes> selectMedicationRecordHistByCounselSessionId(String counselSessionId) {
        List<MedicationRecordHist> medicationRecordHists = medicationRecordHistRepository
                .findByCounselSessionId(counselSessionId);

        return medicationRecordHists.stream().map(medicationRecordHist -> new SelectMedicationRecordHistRes(
                medicationRecordHist.getId(),
                medicationRecordHist.getMedication().getId(),
                medicationRecordHist.getName(),
                medicationRecordHist.getMedicationDivision(),
                medicationRecordHist.getUsageObject(),
                medicationRecordHist.getPrescriptionDate(),
                medicationRecordHist.getPrescriptionDays(),
                medicationRecordHist.getUnit(),
                medicationRecordHist.getMedicationUsageStatus(),
                medicationRecordHist.getUpdatedDatetime(),
                medicationRecordHist.getCreatedDatetime(),
                medicationRecordHist.getCreatedBy(),
                medicationRecordHist.getUpdatedBy()
        )).collect(Collectors.toList());
    }

    public void deleteMedicationRecordHist(String counselSessionId, String id) {
        medicationRecordHistRepository.deleteById(id);
    }

    public void deleteMedicationRecordHistByCounselSessionId(String counselSessionId) {
        medicationRecordHistRepository.deleteByCounselSessionId(counselSessionId);
    }

    public List<AddAndUpdateMedicationRecordHistRes> addAndUpdateMedicationRecordHists(String counselSessionId, List<AddAndUpdateMedicationRecordHistReq> addAndUpdateMedicationRecordHistReqs) {

        List<MedicationRecordHist> medicationRecordHists = new ArrayList<>();
        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid counsel session ID"));
        MedicationRecordHist medicationRecordHist;
        for (AddAndUpdateMedicationRecordHistReq addAndUpdateMedicationRecordHistReq : addAndUpdateMedicationRecordHistReqs) {
            if (addAndUpdateMedicationRecordHistReq.getRowId().isEmpty()) {
                medicationRecordHist = MedicationRecordHist.builder()
                        .counselSession(counselSession)
                        .medication(medicationRepository.findById(addAndUpdateMedicationRecordHistReq.getMedicationId()).orElse(null))
                        .medicationDivision(addAndUpdateMedicationRecordHistReq.getDivisionCode())
                        .prescriptionDate(LocalDate.parse(addAndUpdateMedicationRecordHistReq.getPrescriptionDate()))
                        .prescriptionDays(addAndUpdateMedicationRecordHistReq.getPrescriptionDays())
                        .name(addAndUpdateMedicationRecordHistReq.getName())
                        .usageObject(addAndUpdateMedicationRecordHistReq.getUsageObject())
                        .unit(addAndUpdateMedicationRecordHistReq.getUnit())
                        .medicationUsageStatus(addAndUpdateMedicationRecordHistReq.getUsageStatusCode())
                        .build();
            } else {
                medicationRecordHist = medicationRecordHistRepository.findById(addAndUpdateMedicationRecordHistReq.getRowId()).orElse(null);
                medicationRecordHist.setMedication(medicationRepository.findById(addAndUpdateMedicationRecordHistReq.getMedicationId()).orElse(null));
                medicationRecordHist.setMedicationDivision(addAndUpdateMedicationRecordHistReq.getDivisionCode());
                medicationRecordHist.setPrescriptionDate(LocalDate.parse(addAndUpdateMedicationRecordHistReq.getPrescriptionDate()));
                medicationRecordHist.setPrescriptionDays(addAndUpdateMedicationRecordHistReq.getPrescriptionDays());
                medicationRecordHist.setName(addAndUpdateMedicationRecordHistReq.getName());
                medicationRecordHist.setUsageObject(addAndUpdateMedicationRecordHistReq.getUsageObject());
                medicationRecordHist.setUnit(addAndUpdateMedicationRecordHistReq.getUnit());
                medicationRecordHist.setMedicationUsageStatus(addAndUpdateMedicationRecordHistReq.getUsageStatusCode());
            }
            medicationRecordHists.add(medicationRecordHist);
        }
        medicationRecordHistRepository.saveAll(medicationRecordHists);
        return medicationRecordHists.stream().map(MedicationRecordHist::getId).map(AddAndUpdateMedicationRecordHistRes::new).collect(Collectors.toList());
    }
}
