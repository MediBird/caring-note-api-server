package com.springboot.api.counselsession.service;

import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselsession.dto.medicationcounsel.AddMedicationCounselReq;
import com.springboot.api.counselsession.dto.medicationcounsel.AddMedicationCounselRes;
import com.springboot.api.counselsession.dto.medicationcounsel.DeleteMedicationCounselReq;
import com.springboot.api.counselsession.dto.medicationcounsel.DeleteMedicationCounselRes;
import com.springboot.api.counselsession.dto.medicationcounsel.SelectMedicationCounselRes;
import com.springboot.api.counselsession.dto.medicationcounsel.SelectPreviousMedicationCounselRes;
import com.springboot.api.counselsession.dto.medicationcounsel.UpdateMedicationCounselReq;
import com.springboot.api.counselsession.dto.medicationcounsel.UpdateMedicationCounselRes;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.api.counselsession.entity.MedicationCounsel;
import com.springboot.api.counselsession.repository.CounselSessionRepository;
import com.springboot.api.counselsession.repository.MedicationCounselRepository;
import com.springboot.enums.ScheduleStatus;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedicationCounselService {

    private final MedicationCounselRepository medicationCounselRepository;
    private final CounselSessionRepository counselSessionRepository;

    @Transactional
    public AddMedicationCounselRes addMedicationCounsel(AddMedicationCounselReq addMedicationCounselReq) {

        CounselSession counselSession = counselSessionRepository
            .findById(addMedicationCounselReq.getCounselSessionId())
            .orElseThrow(IllegalArgumentException::new);

        MedicationCounsel medicationCounsel = MedicationCounsel.builder()
            .counselSession(counselSession)
            .counselRecord(addMedicationCounselReq.getCounselRecord())
            .build();

        MedicationCounsel savedMedicationCounsel = medicationCounselRepository.save(medicationCounsel);

        return new AddMedicationCounselRes(savedMedicationCounsel.getId());
    }

    public SelectMedicationCounselRes selectMedicationCounsel(String counselSessionId) {

        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
            .orElseThrow(IllegalArgumentException::new);

        MedicationCounsel medicationCounsel = medicationCounselRepository
            .findByCounselSessionId(counselSession.getId())
            .orElseThrow(NoContentException::new);

        return new SelectMedicationCounselRes(
            medicationCounsel.getId(),
            medicationCounsel.getCounselRecord());
    }

    public SelectPreviousMedicationCounselRes selectPreviousMedicationCounsel(String counselSessionId) {

        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
            .orElseThrow(IllegalArgumentException::new);

        Counselee counselee = Optional.ofNullable(counselSession.getCounselee())
            .orElseThrow(NoContentException::new);

        List<CounselSession> counselSessions = counselSessionRepository
            .findByCounseleeIdAndScheduledStartDateTimeLessThan(counselee.getId(),
                counselSession.getScheduledStartDateTime());

        CounselSession previousCounselSession = counselSessions.stream()
            .filter(cs -> ScheduleStatus.COMPLETED.equals(cs.getStatus())).findFirst()
            .orElseThrow(NoContentException::new);

        MedicationCounsel medicationCounsel = medicationCounselRepository
            .findByCounselSessionId(previousCounselSession.getId())
            .orElseThrow(NoContentException::new);

        return new SelectPreviousMedicationCounselRes(
            previousCounselSession.getId(),
            medicationCounsel.getCounselRecord()
        );
    }

    @Transactional
    public UpdateMedicationCounselRes updateMedicationCounsel(
        UpdateMedicationCounselReq updateMedicationCounselReq) {

        MedicationCounsel medicationCounsel = medicationCounselRepository
            .findById(updateMedicationCounselReq.getMedicationCounselId())
            .orElseThrow(NoContentException::new);

        medicationCounsel.setCounselRecord(updateMedicationCounselReq.getCounselRecord());

        return new UpdateMedicationCounselRes(medicationCounsel.getId());
    }

    @Transactional
    public DeleteMedicationCounselRes deleteMedicationCounsel(
        DeleteMedicationCounselReq deleteMedicationCounselReq) {

        MedicationCounsel medicationCounsel = medicationCounselRepository
            .findById(deleteMedicationCounselReq.getMedicationCounselId())
            .orElseThrow(NoContentException::new);

        medicationCounselRepository.deleteById(medicationCounsel.getId());

        return new DeleteMedicationCounselRes(medicationCounsel.getId());
    }
}
