package com.springboot.api.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.Medication;
import com.springboot.api.domain.MedicationRecordHist;
import com.springboot.api.dto.medicationRecordHist.AddMedicationRecordHistReq;
import com.springboot.api.dto.medicationRecordHist.AddMedicationRecordHistRes;
import com.springboot.api.dto.medicationRecordHist.UpdateMedicationRecordHistReq;
import com.springboot.api.dto.medicationRecordHist.UpdateMedicationRecordHistRes;
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

    public MedicationRecordHist getMedicationRecordHistById(String id) {
        return medicationRecordHistRepository.findById(id).orElse(null);
    }

    public List<MedicationRecordHist> selectMedicationRecordHistByCounselSessionId(String counselSessionId) {
        return medicationRecordHistRepository.findByCounselSessionId(counselSessionId);
    }

    public MedicationRecordHist saveMedicationRecordHist(MedicationRecordHist medicationRecordHist) {
        return medicationRecordHistRepository.save(medicationRecordHist);
    }

    public void deleteMedicationRecordHist(String counselSessionId, String id) {
        medicationRecordHistRepository.deleteById(id);
    }

    public void deleteMedicationRecordHistByCounselSessionId(String counselSessionId) {
        medicationRecordHistRepository.deleteByCounselSessionId(counselSessionId);
    }

    public void updateMedicationRecordHist(String id, MedicationRecordHist medicationRecordHist) {
        medicationRecordHistRepository.save(medicationRecordHist);
    }

    public AddMedicationRecordHistRes addMedicationRecordHist(String counselSessionId, AddMedicationRecordHistReq addMedicationRecordHistReq) {
        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid counsel session ID"));

        Medication medication = medicationRepository.findById(addMedicationRecordHistReq.getMedicationId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid medication ID"));

        MedicationRecordHist medicationRecordHist = MedicationRecordHist.builder()
                .counselSession(counselSession)
                .medication(medication)
                .medicationDivision(addMedicationRecordHistReq.getMedicationDivision())
                .prescriptionDate(LocalDate.parse(addMedicationRecordHistReq.getPrescriptionDate()))
                .prescriptionDays(addMedicationRecordHistReq.getPrescriptionDays())
                .name(addMedicationRecordHistReq.getName())
                .usageObject(addMedicationRecordHistReq.getUsageObject())
                .unit(addMedicationRecordHistReq.getUnit())
                .build();
        return new AddMedicationRecordHistRes(medicationRecordHist.getId());
    }

    public List<AddMedicationRecordHistRes> addMedicationRecordHists(String counselSessionId,
            List<AddMedicationRecordHistReq> addMedicationRecordHistReqs) {
        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid counsel session ID"));

        List<MedicationRecordHist> medicationRecordHists = new ArrayList<>();
        for (AddMedicationRecordHistReq addMedicationRecordHistReq : addMedicationRecordHistReqs) {
            MedicationRecordHist medicationRecordHist = MedicationRecordHist.builder()
                    .counselSession(counselSession)
                    .medication(
                            medicationRepository.findById(addMedicationRecordHistReq.getMedicationId()).orElse(null))
                    .medicationDivision(addMedicationRecordHistReq.getMedicationDivision())
                    .prescriptionDate(LocalDate.parse(addMedicationRecordHistReq.getPrescriptionDate()))
                    .prescriptionDays(addMedicationRecordHistReq.getPrescriptionDays())
                    .name(addMedicationRecordHistReq.getName())
                    .usageObject(addMedicationRecordHistReq.getUsageObject())
                    .unit(addMedicationRecordHistReq.getUnit())
                    .build();
            medicationRecordHists.add(medicationRecordHist);
        }
        medicationRecordHistRepository.saveAll(medicationRecordHists);
        return medicationRecordHists.stream().map(MedicationRecordHist::getId).map(AddMedicationRecordHistRes::new)
                .collect(Collectors.toList());
    }

    public UpdateMedicationRecordHistRes updateMedicationRecordHist(String counselSessionId, UpdateMedicationRecordHistReq updateMedicationRecordHistReq) {
        MedicationRecordHist medicationRecordHist = medicationRecordHistRepository.findById(updateMedicationRecordHistReq.getId()).orElse(null);
        medicationRecordHist.setMedication(medicationRepository.findById(updateMedicationRecordHistReq.getMedicationId()).orElse(null));
        medicationRecordHist.setMedicationDivision(updateMedicationRecordHistReq.getMedicationDivision());
        medicationRecordHist.setPrescriptionDate(LocalDate.parse(updateMedicationRecordHistReq.getPrescriptionDate()));
        medicationRecordHist.setPrescriptionDays(updateMedicationRecordHistReq.getPrescriptionDays());
        medicationRecordHist.setName(updateMedicationRecordHistReq.getName());
        medicationRecordHist.setUsageObject(updateMedicationRecordHistReq.getUsageObject());
        medicationRecordHist.setUnit(updateMedicationRecordHistReq.getUnit());
        return new UpdateMedicationRecordHistRes(medicationRecordHist.getId());
    }

    public List<UpdateMedicationRecordHistRes> updateMedicationRecordHists(String counselSessionId, List<UpdateMedicationRecordHistReq> updateMedicationRecordHistReqs) {
        List<MedicationRecordHist> medicationRecordHists = new ArrayList<>();
        for (UpdateMedicationRecordHistReq updateMedicationRecordHistReq : updateMedicationRecordHistReqs) {
            MedicationRecordHist medicationRecordHist = medicationRecordHistRepository.findById(updateMedicationRecordHistReq.getId()).orElse(null);
            medicationRecordHist.setMedication(medicationRepository.findById(updateMedicationRecordHistReq.getMedicationId()).orElse(null));
            medicationRecordHist.setMedicationDivision(updateMedicationRecordHistReq.getMedicationDivision());
            medicationRecordHist.setPrescriptionDate(LocalDate.parse(updateMedicationRecordHistReq.getPrescriptionDate()));
            medicationRecordHist.setPrescriptionDays(updateMedicationRecordHistReq.getPrescriptionDays());
            medicationRecordHist.setName(updateMedicationRecordHistReq.getName());
            medicationRecordHist.setUsageObject(updateMedicationRecordHistReq.getUsageObject());
            medicationRecordHist.setUnit(updateMedicationRecordHistReq.getUnit());
            medicationRecordHists.add(medicationRecordHist);
        }
        medicationRecordHistRepository.saveAll(medicationRecordHists);
        return medicationRecordHists.stream().map(MedicationRecordHist::getId).map(UpdateMedicationRecordHistRes::new).collect(Collectors.toList());
    }
}
