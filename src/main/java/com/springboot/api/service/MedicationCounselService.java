package com.springboot.api.service;

import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.Counselee;
import com.springboot.api.domain.MedicationCounsel;
import com.springboot.api.dto.medicationcounsel.*;
import com.springboot.api.repository.CounselSessionRepository;
import com.springboot.api.repository.MedicationCounselRepository;
import com.springboot.enums.ScheduleStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MedicationCounselService {

    private final MedicationCounselRepository medicationCounselRepository;
    private final CounselSessionRepository counselSessionRepository;


    public AddRes add(String id, AddReq addReq){

        CounselSession counselSession = counselSessionRepository.findById(addReq.getCounselSessionId())
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

    public SelectPreviousByCounselSessionIdRes selectPreviousByCounselSessionId(String id, String counselSessionId){

        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
                .orElseThrow(NoContentException::new);

        Counselee counselee= Optional.ofNullable(counselSession.getCounselee())
                .orElseThrow(NoContentException::new);


        List<CounselSession> counselSessions = counselSessionRepository.findByCounseleeIdAndScheduledStartDateTimeLessThan(counselee.getId(),
                counselSession.getScheduledStartDateTime());


        CounselSession previousCounselSession = counselSessions.stream().filter(cs -> ScheduleStatus.COMPLETED.equals(cs.getStatus())).findFirst()
                .orElseThrow(NoContentException::new);


        MedicationCounsel medicationCounsel = Optional.ofNullable(previousCounselSession.getMedicationCounsel())
                .orElseThrow(NoContentException::new);

        return new SelectPreviousByCounselSessionIdRes(
                previousCounselSession.getId()
                ,medicationCounsel.getCounselRecordHighlights()
                , medicationCounsel.getCounselRecord() //STT 도입 이후 STT 결과로 변경 예정
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
