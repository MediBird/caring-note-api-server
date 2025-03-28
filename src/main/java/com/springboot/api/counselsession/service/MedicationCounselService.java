package com.springboot.api.counselsession.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselsession.dto.medicationcounsel.AddMedicationCounselReq;
import com.springboot.api.counselsession.dto.medicationcounsel.AddMedicationCounselRes;
import com.springboot.api.counselsession.dto.medicationcounsel.DeleteMedicationCounselReq;
import com.springboot.api.counselsession.dto.medicationcounsel.DeleteMedicationCounselRes;
import com.springboot.api.counselsession.dto.medicationcounsel.MedicationCounselHighlightDTO;
import com.springboot.api.counselsession.dto.medicationcounsel.SelectMedicationCounselRes;
import com.springboot.api.counselsession.dto.medicationcounsel.SelectPreviousMedicationCounselRes;
import com.springboot.api.counselsession.dto.medicationcounsel.UpdateMedicationCounselReq;
import com.springboot.api.counselsession.dto.medicationcounsel.UpdateMedicationCounselRes;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.api.counselsession.entity.MedicationCounsel;
import com.springboot.api.counselsession.entity.MedicationCounselHighlight;
import com.springboot.api.counselsession.repository.CounselSessionRepository;
import com.springboot.api.counselsession.repository.MedicationCounselHighlightRepository;
import com.springboot.api.counselsession.repository.MedicationCounselRepository;
import com.springboot.enums.ScheduleStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicationCounselService {

        private final MedicationCounselRepository medicationCounselRepository;
        private final MedicationCounselHighlightRepository medicationCounselHighlightRepository;
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

                List<MedicationCounselHighlight> medicationCounselHighlights = Optional
                                .ofNullable(addMedicationCounselReq.getCounselRecordHighlights())
                                .orElse(Collections.emptyList())
                                .stream()
                                .map(highlight -> MedicationCounselHighlight.builder()
                                                .medicationCounsel(medicationCounsel)
                                                .highlight(highlight.highlight())
                                                .startIndex(highlight.startIndex())
                                                .endIndex(highlight.endIndex())
                                                .build())
                                .collect(Collectors.toList());

                medicationCounsel.setMedicationCounselHighlights(medicationCounselHighlights);

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
                                medicationCounsel.getId(), medicationCounsel.getCounselRecord(),
                                Optional.ofNullable(medicationCounsel.getMedicationCounselHighlights())
                                                .orElse(Collections.emptyList())
                                                .stream()
                                                .map(h -> MedicationCounselHighlightDTO
                                                                .builder()
                                                                .highlight(h.getHighlight())
                                                                .startIndex(h.getStartIndex())
                                                                .endIndex(h.getEndIndex())
                                                                .build())
                                                .collect(Collectors.toList()));
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
                                Optional.ofNullable(medicationCounsel.getMedicationCounselHighlights())
                                                .orElse(Collections.emptyList())
                                                .stream()
                                                .map(h -> MedicationCounselHighlightDTO
                                                                .builder()
                                                                .highlight(h.getHighlight())
                                                                .startIndex(h.getStartIndex())
                                                                .endIndex(h.getEndIndex())
                                                                .build())
                                                .collect(Collectors.toList()),
                                                medicationCounsel.getCounselRecord() // STT 도입 이후 STT 결과로 변경 예정
                );

        }

        @Transactional
        public UpdateMedicationCounselRes updateMedicationCounsel(
                        UpdateMedicationCounselReq updateMedicationCounselReq) {

                MedicationCounsel medicationCounsel = medicationCounselRepository
                                .findById(updateMedicationCounselReq.getMedicationCounselId())
                                .orElseThrow(NoContentException::new);

                medicationCounselHighlightRepository.deleteAll(medicationCounsel.getMedicationCounselHighlights());
                medicationCounselRepository.flush();

                medicationCounsel.setCounselRecord(updateMedicationCounselReq.getCounselRecord());

                List<MedicationCounselHighlight> medicationCounselHighlights = Optional
                                .ofNullable(updateMedicationCounselReq.getCounselRecordHighlights())
                                .orElse(Collections.emptyList())
                                .stream()
                                .map(highlight -> MedicationCounselHighlight.builder()
                                                .medicationCounsel(medicationCounsel)
                                                .highlight(highlight.highlight())
                                                .startIndex(highlight.startIndex())
                                                .endIndex(highlight.endIndex())
                                                .build())
                                .collect(Collectors.toList());

                medicationCounselHighlightRepository.saveAll(medicationCounselHighlights);
                medicationCounsel.setMedicationCounselHighlights(medicationCounselHighlights);

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
