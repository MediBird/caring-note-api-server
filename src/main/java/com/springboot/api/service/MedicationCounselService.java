package com.springboot.api.service;

import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.MedicationCounsel;
import com.springboot.api.dto.medicationcounsel.*;
import com.springboot.api.repository.CounselSessionRepository;
import com.springboot.api.repository.MedicationCounselRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MedicationCounselService {

    private final MedicationCounselRepository medicationCounselRepository;
    private final CounselSessionRepository counselSessionRepository;


    public MedicationCounselService(MedicationCounselRepository medicationCounselRepository
            , CounselSessionRepository counselSessionRepository) {

        this.medicationCounselRepository = medicationCounselRepository;
        this.counselSessionRepository = counselSessionRepository;
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

    @Transactional
    public UpdateRes update(String id, UpdateReq updateReq){

        MedicationCounsel medicationCounsel = medicationCounselRepository.findById(updateReq.getMedicationCounselId())
                .orElseThrow(NoContentException::new);

        medicationCounsel.setCounselRecord(updateReq.getCounselRecord());
        medicationCounsel.setCounselRecordHighlights(updateReq.getCounselRecordHighlights());
        medicationCounsel.setCounselNeedStatus(updateReq.getCounselNeedStatus());

        return new UpdateRes(medicationCounsel.getId());
    }

    @Transactional
    public DeleteRes delete(String id, DeleteReq deleteReq){

        MedicationCounsel medicationCounsel = medicationCounselRepository.findById(deleteReq.getMedicationCounselId())
                .orElseThrow(NoContentException::new);

        medicationCounselRepository.deleteById(medicationCounsel.getId());

        return new DeleteRes(medicationCounsel.getId());
    }





}
