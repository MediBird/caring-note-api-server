package com.springboot.api.counselsession.service;

import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.counselsession.dto.medicationRecordHist.AddAndUpdateMedicationRecordHistReq;
import com.springboot.api.counselsession.dto.medicationRecordHist.AddAndUpdateMedicationRecordHistRes;
import com.springboot.api.counselsession.dto.medicationRecordHist.SelectMedicationRecordHistRes;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.api.counselsession.entity.MedicationRecordHist;
import com.springboot.api.counselsession.repository.CounselSessionRepository;
import com.springboot.api.counselsession.repository.MedicationRecordHistRepository;
import com.springboot.api.medication.repository.MedicationRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedicationRecordHistService {

    private final MedicationRecordHistRepository medicationRecordHistRepository;

    private final CounselSessionRepository counselSessionRepository;

    private final MedicationRepository medicationRepository;

    public List<SelectMedicationRecordHistRes> selectMedicationRecordHistByCounselSessionId(
        String counselSessionId) {
        List<MedicationRecordHist> medicationRecordHists = medicationRecordHistRepository
            .findByCounselSessionId(counselSessionId);

        if (medicationRecordHists.isEmpty()) {
            throw new NoContentException("No medication record history found");
        }

        return medicationRecordHists.stream()
            .sorted(Comparator.comparing(MedicationRecordHist::getId))
            .map(SelectMedicationRecordHistRes::from)
            .collect(Collectors.toList());
    }

    public void deleteMedicationRecordHist(String counselSessionId, String id) {
        medicationRecordHistRepository.deleteById(id);
    }

    public void deleteMedicationRecordHistByCounselSessionId(String counselSessionId) {
        medicationRecordHistRepository.deleteByCounselSessionId(counselSessionId);
    }

    public List<AddAndUpdateMedicationRecordHistRes> addAndUpdateMedicationRecordHists(String counselSessionId,
        List<AddAndUpdateMedicationRecordHistReq> addAndUpdateMedicationRecordHistReqs) {

        List<MedicationRecordHist> medicationRecordHists = new ArrayList<>();
        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid counsel session ID"));
        MedicationRecordHist medicationRecordHist;
        for (AddAndUpdateMedicationRecordHistReq addAndUpdateMedicationRecordHistReq : addAndUpdateMedicationRecordHistReqs) {
            if (addAndUpdateMedicationRecordHistReq.getRowId() == null) {
                medicationRecordHist = MedicationRecordHist.builder()
                    .counselSession(counselSession)
                    .medication(medicationRepository
                        .findById(addAndUpdateMedicationRecordHistReq
                            .getMedicationId())
                        .orElse(null))
                    .medicationDivision(addAndUpdateMedicationRecordHistReq
                        .getDivisionCode())
                    .prescriptionDate(addAndUpdateMedicationRecordHistReq
                        .getPrescriptionDate() != null
                        ? LocalDate.parse(
                        addAndUpdateMedicationRecordHistReq
                            .getPrescriptionDate())
                        : null)
                    .prescriptionDays(addAndUpdateMedicationRecordHistReq
                        .getPrescriptionDays())
                    .name(addAndUpdateMedicationRecordHistReq.getMedicationName())
                    .usageObject(addAndUpdateMedicationRecordHistReq
                        .getUsageObject())
                    .unit(addAndUpdateMedicationRecordHistReq.getUnit())
                    .usageStatus(addAndUpdateMedicationRecordHistReq
                        .getUsageStatusCode())
                    .build();
            } else {
                medicationRecordHist = medicationRecordHistRepository
                    .findById(addAndUpdateMedicationRecordHistReq.getRowId()).orElse(null);
                medicationRecordHist.setMedication(medicationRepository
                    .findById(addAndUpdateMedicationRecordHistReq.getMedicationId())
                    .orElse(null));
                medicationRecordHist.setMedicationDivision(
                    addAndUpdateMedicationRecordHistReq.getDivisionCode());
                medicationRecordHist.setPrescriptionDate(
                    addAndUpdateMedicationRecordHistReq.getPrescriptionDate() != null
                        ? LocalDate.parse(addAndUpdateMedicationRecordHistReq
                        .getPrescriptionDate())
                        : null);
                medicationRecordHist.setPrescriptionDays(
                    addAndUpdateMedicationRecordHistReq.getPrescriptionDays());
                medicationRecordHist.setName(addAndUpdateMedicationRecordHistReq.getMedicationName());
                medicationRecordHist
                    .setUsageObject(addAndUpdateMedicationRecordHistReq.getUsageObject());
                medicationRecordHist.setUnit(addAndUpdateMedicationRecordHistReq.getUnit());
                medicationRecordHist.setUsageStatus(
                    addAndUpdateMedicationRecordHistReq.getUsageStatusCode());
            }
            medicationRecordHists.add(medicationRecordHist);
        }
        medicationRecordHistRepository.saveAll(medicationRecordHists);
        return medicationRecordHists.stream().map(MedicationRecordHist::getId)
            .map(AddAndUpdateMedicationRecordHistRes::new).collect(Collectors.toList());
    }
}
