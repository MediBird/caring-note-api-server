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


    public AddMedicationCounselRes addMedicationCounsel(AddMedicationCounselReq addMedicationCounselReq){

        CounselSession counselSession = counselSessionRepository.findById(addMedicationCounselReq.getCounselSessionId())
                .orElseThrow(IllegalArgumentException::new);

        MedicationCounsel medicationCounsel = MedicationCounsel.builder()
                .counselSession(counselSession)
                .counselRecord(addMedicationCounselReq.getCounselRecord())
                .counselRecordHighlights(addMedicationCounselReq.getCounselRecordHighlights())
                .counselNeedStatus(addMedicationCounselReq.getCounselNeedStatus())
                .build();

        MedicationCounsel savedMedicationCounsel = medicationCounselRepository.save(medicationCounsel);

        return new AddMedicationCounselRes(savedMedicationCounsel.getId());
    }

    public SelectMedicationCounselRes selectMedicationCounsel(String counselSessionId){

        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
                .orElseThrow(IllegalArgumentException::new);

        MedicationCounsel medicationCounsel = medicationCounselRepository.findByCounselSessionId(counselSession.getId())
                .orElseThrow(NoContentException::new);


        return new SelectMedicationCounselRes(
                medicationCounsel.getId()
                , medicationCounsel.getCounselRecord()
                , medicationCounsel.getCounselRecordHighlights()
                , medicationCounsel.getCounselNeedStatus()
        );
    }

    public SelectPreviousMedicationCounselRes selectPreviousMedicationCounsel(String counselSessionId){

        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
                .orElseThrow(IllegalArgumentException::new);

        Counselee counselee= Optional.ofNullable(counselSession.getCounselee())
                .orElseThrow(NoContentException::new);


        List<CounselSession> counselSessions = counselSessionRepository.findByCounseleeIdAndScheduledStartDateTimeLessThan(counselee.getId(),
                counselSession.getScheduledStartDateTime());


        CounselSession previousCounselSession = counselSessions.stream().filter(cs -> ScheduleStatus.COMPLETED.equals(cs.getStatus())).findFirst()
                .orElseThrow(NoContentException::new);


        MedicationCounsel medicationCounsel = Optional.ofNullable(previousCounselSession.getMedicationCounsel())
                .orElseThrow(NoContentException::new);

        return new SelectPreviousMedicationCounselRes(
                previousCounselSession.getId()
                ,medicationCounsel.getCounselRecordHighlights()
                , medicationCounsel.getCounselRecord() //STT 도입 이후 STT 결과로 변경 예정
        );



    }

    @Transactional
    public UpdateMedicationCounselRes updateMedicationCounsel(UpdateMedicationCounselReq updateMedicationCounselReq){

        MedicationCounsel medicationCounsel = medicationCounselRepository.findById(updateMedicationCounselReq.getMedicationCounselId())
                .orElseThrow(NoContentException::new);

        medicationCounsel.setCounselRecord(updateMedicationCounselReq.getCounselRecord());
        medicationCounsel.setCounselRecordHighlights(updateMedicationCounselReq.getCounselRecordHighlights());
        medicationCounsel.setCounselNeedStatus(updateMedicationCounselReq.getCounselNeedStatus());

        return new UpdateMedicationCounselRes(medicationCounsel.getId());
    }

    @Transactional
    public DeleteMedicationCounselRes deleteMedicationCounsel(DeleteMedicationCounselReq deleteMedicationCounselReq){

        MedicationCounsel medicationCounsel = medicationCounselRepository.findById(deleteMedicationCounselReq.getMedicationCounselId())
                .orElseThrow(NoContentException::new);

        medicationCounselRepository.deleteById(medicationCounsel.getId());

        return new DeleteMedicationCounselRes(medicationCounsel.getId());
    }



}
