package com.springboot.api.service;

import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.MedicationCounsel;
import com.springboot.api.dto.medicationcounsel.AddReq;
import com.springboot.api.dto.medicationcounsel.AddRes;
import com.springboot.api.dto.medicationcounsel.SelectByCounselSessionIdRes;
import com.springboot.api.repository.CounselSessionRepository;
import com.springboot.api.repository.MedicationCounselRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class MedicationCounselService {

    private final MedicationCounselRepository medicationCounselRepository;
    private final CounselSessionRepository counselSessionRepository;
    private final EntityManager entityManager;


    public MedicationCounselService(MedicationCounselRepository medicationCounselRepository
            , CounselSessionRepository counselSessionRepository
            , EntityManager entityManager) {

        this.medicationCounselRepository = medicationCounselRepository;
        this.counselSessionRepository = counselSessionRepository;
        this.entityManager = entityManager;
    }


    public AddRes add(String id, AddReq addReq){

        CounselSession counselSession = counselSessionRepository.findById(id)
                .orElseThrow(NoContentException::new);

        MedicationCounsel medicationCounsel = MedicationCounsel.builder()
                .counselSession(counselSession)
                .counselRecord(addReq.getCounselRecord())
                .counselRecordHighlights(addReq.getCounselRecordHighlights())
                .counselNeedStatus(addReq.getCounselNeedStatus())
                .build();

        MedicationCounsel savedMedicationCounsel = medicationCounselRepository.save(medicationCounsel);

        return new AddRes(savedMedicationCounsel.getId());
    }

    public SelectByCounselSessionIdRes selectByCounselSessionId(String id, String counselSessionId){

        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
                .orElseThrow(NoContentException::new);

        MedicationCounsel medicationCounsel = medicationCounselRepository.findByCounselSessionId(counselSession.getId())
                .orElseThrow(NoContentException::new);


        return new SelectByCounselSessionIdRes(
                medicationCounsel.getId()
                , medicationCounsel.getCounselRecord()
                , medicationCounsel.getCounselRecordHighlights()
                , medicationCounsel.getCounselNeedStatus()
        );
    }





}
