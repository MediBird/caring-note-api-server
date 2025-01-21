package com.springboot.api.service;

import com.springboot.api.domain.*;
import com.springboot.api.dto.medicationcounsel.SelectPreviousMedicationCounselRes;
import com.springboot.api.repository.CounselSessionRepository;
import com.springboot.api.repository.MedicationCounselRepository;
import com.springboot.enums.ScheduleStatus;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
@Transactional
class MedicationCounselServiceTest {

    private static final Logger log = LoggerFactory.getLogger(MedicationCounselServiceTest.class);

    @InjectMocks
    MedicationCounselService medicationCounselService;



    @Mock
    MedicationCounselRepository medicationCounselRepository;

    @Mock
    CounselSessionRepository counselSessionRepository;

    @Test
    void selectPreviousByCounselSessionId() {

        //when
        String counselSessionId ="TEST-COUNSEL-SESSION-01";

        Counselee counselee = Counselee.builder()
                .name("Test Counselee")
                .dateOfBirth(LocalDate.of(1995,7,19))
                .phoneNumber("01090654118")
                .build();
        counselee.setId("TEST-COUNSELEE-01");

        Counselor counselor = Counselor.builder()
                .email("roung4119@gmail.com")
                .name("Test Counselor")
                .phoneNumber("01083432753")
                .build();


        CounselSession counselSession = CounselSession.builder()
                .status(ScheduleStatus.SCHEDULED)
                .counselee(counselee) // 실제 영속 상태의 엔티티 사용
                .counselor(counselor) // 실제 영속 상태의 엔티티 사용
                .scheduledStartDateTime(LocalDateTime.of(2024, 12, 10, 10, 0, 0))
                .build();

        counselSession.setId(counselSessionId);

        List<CounselSession> previousCounselSessions = List.of(

                CounselSession.builder()
                        .status(ScheduleStatus.COMPLETED)
                        .counselee(counselee) // 실제 영속 상태의 엔티티 사용
                        .counselor(counselor) // 실제 영속 상태의 엔티티 사용
                        .scheduledStartDateTime(LocalDateTime.of(2024, 12, 9, 10, 0, 0))
                        .build()
                ,CounselSession.builder()
                        .status(ScheduleStatus.CANCELED)
                        .counselee(counselee) // 실제 영속 상태의 엔티티 사용
                        .counselor(counselor) // 실제 영속 상태의 엔티티 사용
                        .scheduledStartDateTime(LocalDateTime.of(2024, 12, 8, 10, 0, 0))
                        .build()
        );

        IntStream.range(0,previousCounselSessions.size())
                        .forEach(i-> previousCounselSessions.get(i).setId("TEST-PREVIOUS-COUNSEL-SESSION-0"+i));

        MedicationCounsel medicationCounsel = MedicationCounsel.builder()
                        .medicationCounselHighlights(
                                List.of(MedicationCounselHighlight.builder()
                                        .highlight("안녕")
                                        .startIndex(0)
                                        .endIndex(2)
                                        .build())
                        )
                        .counselRecord("안녕하세요.")
                        .build();

        previousCounselSessions.getFirst().setMedicationCounsel(medicationCounsel);

        when(counselSessionRepository.findById(any())).thenReturn(Optional.of(counselSession));
        when(counselSessionRepository.findByCounseleeIdAndScheduledStartDateTimeLessThan(any(), any())).thenReturn(
                previousCounselSessions
        );


        //then
        SelectPreviousMedicationCounselRes selectPreviousMedicationCounselRes = medicationCounselService
                .selectPreviousMedicationCounsel(counselSessionId);

        //valid
        assertEquals(selectPreviousMedicationCounselRes.previousCounselSessionId(),"TEST-PREVIOUS-COUNSEL-SESSION-00");
        assertEquals(selectPreviousMedicationCounselRes.counselRecordHighlights().getFirst().highlight(),medicationCounsel.getMedicationCounselHighlights().getFirst().getHighlight());

    }

}