package com.springboot.api.service;

import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselee.repository.CounseleeRepository;
import com.springboot.api.counselor.entity.Counselor;
import com.springboot.api.counselsession.dto.counselsession.AddCounselSessionByCounseleeReq;
import com.springboot.api.counselsession.dto.counselsession.AddCounselSessionRes;
import com.springboot.api.counselsession.dto.counselsession.UpdateCounselSessionRes;
import com.springboot.api.counselsession.dto.counselsession.UpdateStartTimeInCounselSessionReq;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.api.counselsession.repository.CounselSessionRepository;
import com.springboot.api.counselsession.service.CounselSessionService;
import com.springboot.enums.GenderType;
import com.springboot.enums.HealthInsuranceType;
import com.springboot.enums.ScheduleStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CounselSessionServiceTest {

    @Spy
    private DateTimeUtil dateTimeUtil;

    @Mock
    private CounselSessionRepository counselSessionRepository;

    @Mock
    private CounseleeRepository counseleeRepository;

    @InjectMocks
    private CounselSessionService counselSessionService;

    @Test
    @DisplayName("성공: addCounselSessionByCounselee가 예약을 생성")
    public void addCounselSessionByCounselee() {
        // Given
        Counselee counselee = Counselee.builder().build();

        String counselSessionId = "01JNBYN04P2JGB7CVQBPX02EXD";
        String scheduledStartDateTime = "2024-01-01 10:00";
        LocalDateTime scheduleDateTime = dateTimeUtil.parseToDateTime(scheduledStartDateTime);

        CounselSession counselSession = Mockito.spy(CounselSession.builder()
            .counselee(counselee)
            .scheduledStartDateTime(scheduleDateTime)
            .status(ScheduleStatus.SCHEDULED)
            .build());

        given(counseleeRepository.findById(any(String.class))).willReturn(
            java.util.Optional.of(counselee));
        given(counselSessionRepository.existsByCounseleeAndScheduledStartDateTime(counselee,
            scheduleDateTime)).willReturn(false);
        given(counselSessionRepository.save(any())).willReturn(counselSession);
        given(counselSession.getId()).willReturn(counselSessionId);
        // When
        AddCounselSessionRes response = counselSessionService.addCounselSessionByCounselee(
            new AddCounselSessionByCounseleeReq(counselSessionId, scheduledStartDateTime));
        // Then
        Assertions.assertThat(counselSession.getId()).isEqualTo(response.counselSessionId());
    }

    @Test
    @DisplayName("실패: 상담 시간 중복")
    public void addCounselSessionByCounselee_CounselSessionExists() {
        // Given
        Counselee counselee = Counselee.builder()
            .name("홍박사")
            .dateOfBirth(LocalDate.of(1980, 1, 1))
            .phoneNumber("010-1234-5678")
            .genderType(GenderType.MALE)
            .healthInsuranceType(HealthInsuranceType.HEALTH_INSURANCE)
            .counselCount(0)
            .registrationDate(LocalDate.now())
            .build();

        String scheduledStartDateTime = "2024-01-01 10:00";
        LocalDateTime scheduleDateTime = dateTimeUtil.parseToDateTime(scheduledStartDateTime);

        given(counseleeRepository.findById(any(String.class))).willReturn(Optional.of(counselee));
        given(counselSessionRepository.existsByCounseleeAndScheduledStartDateTime(counselee,scheduleDateTime)).willReturn(true);
        // When
        // Then
        Assertions.assertThatThrownBy(() -> counselSessionService.addCounselSessionByCounselee(
                new AddCounselSessionByCounseleeReq("01JNBYN04P2JGB7CVQBPX02EXD",
                    scheduledStartDateTime)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("해당 시간에 이미 상담이 예약되어 있습니다");
    }

    @Test
    @DisplayName("실패: 존재하지 않는 내담자 ID")
    public void addCounselSessionByCounselee_CounseleeNotFound() {
        //Given
        //When&Then
        Assertions.assertThatThrownBy(() -> counselSessionService.addCounselSessionByCounselee(
                new AddCounselSessionByCounseleeReq("01JNBYN04P2JGB7CVQBPX02EXD", "2024-01-01 10:00")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 내담자 ID입니다");
    }
    
    @Test
    @DisplayName("성공 : updateStartDateTimeInCounselSession가 상담 시간을 수정")
    public void updateStartDateTimeInCounselSession(){
        //Given
        Counselee counselee = Counselee.builder().build();
        Counselor counselor = Counselor.builder().build();

        String counselSessionId = "01JNBYN04P2JGB7CVQBPX02EXD";
        String scheduledStartDateTime = "2024-01-01 10:00";
        LocalDateTime scheduleDateTime = dateTimeUtil.parseToDateTime(scheduledStartDateTime);

        CounselSession counselSession = Mockito.spy(CounselSession.builder()
            .counselee(counselee)
            .counselor(counselor)
            .scheduledStartDateTime(scheduleDateTime)
            .status(ScheduleStatus.SCHEDULED)
            .build());

        given(counselSessionRepository.findByIdWithCounseleeAndCounselor(counselSessionId)).willReturn(Optional.of(counselSession));
        given(counselSessionRepository.existsByCounseleeAndScheduledStartDateTime(counselee,
            scheduleDateTime)).willReturn(false);
        given(counselSessionRepository.existsByCounselorAndScheduledStartDateTime(counselor,
            scheduleDateTime)).willReturn(false);
        given(counselSession.getId()).willReturn(counselSessionId);
        //When
        UpdateCounselSessionRes response = counselSessionService.updateStartDateTimeInCounselSession(
            new UpdateStartTimeInCounselSessionReq(counselSessionId, scheduledStartDateTime)
        );
        //Then
        verify(counselSession).updateScheduledStartDateTime(scheduleDateTime);
        Assertions.assertThat(counselSession.getId()).isEqualTo(response.updatedCounselSessionId());
    }

    @Test
    @DisplayName("실패: 상담 시간 중복")
    public void updateStartDateTimeInCounselSession_CounseleeAndStartDateTime() {
        // Given
        Counselee counselee = Counselee.builder().build();
        Counselor counselor = Counselor.builder().build();

        String counselSessionId = "01JNBYN04P2JGB7CVQBPX02EXD";
        String scheduledStartDateTime = "2024-01-01 10:00";
        LocalDateTime scheduleDateTime = dateTimeUtil.parseToDateTime(scheduledStartDateTime);

        CounselSession counselSession = CounselSession.builder()
            .counselee(counselee)
            .counselor(counselor)
            .build();

        given(counselSessionRepository.findByIdWithCounseleeAndCounselor(counselSessionId)).willReturn(Optional.of(counselSession));
        given(counselSessionRepository.existsByCounseleeAndScheduledStartDateTime(counselee, scheduleDateTime)).willReturn(true);
        // When
        // Then
        Assertions.assertThatThrownBy(() -> counselSessionService.updateStartDateTimeInCounselSession(
                new UpdateStartTimeInCounselSessionReq(counselSessionId, scheduledStartDateTime)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("해당 시간에 이미 상담이 예약되어 있습니다");
    }

    @Test
    @DisplayName("실패: 상담 시간 중복")
    public void updateStartDateTimeInCounselSession_CounselorAndStartDateTime() {
        // Given
        Counselee counselee = Counselee.builder().build();
        Counselor counselor = Counselor.builder().build();

        String counselSessionId = "01JNBYN04P2JGB7CVQBPX02EXD";
        String scheduledStartDateTime = "2024-01-01 10:00";
        LocalDateTime scheduleDateTime = dateTimeUtil.parseToDateTime(scheduledStartDateTime);

        CounselSession counselSession = CounselSession.builder()
            .counselee(counselee)
            .counselor(counselor)
            .build();

        given(counselSessionRepository.findByIdWithCounseleeAndCounselor(counselSessionId)).willReturn(Optional.of(counselSession));
        given(counselSessionRepository.existsByCounseleeAndScheduledStartDateTime(counselee, scheduleDateTime)).willReturn(false);
        given(counselSessionRepository.existsByCounselorAndScheduledStartDateTime(counselor, scheduleDateTime)).willReturn(true);
        // When
        // Then
        Assertions.assertThatThrownBy(() -> counselSessionService.updateStartDateTimeInCounselSession(
                new UpdateStartTimeInCounselSessionReq(counselSessionId, scheduledStartDateTime)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("해당 시간에 이미 상담이 예약되어 있습니다");
    }

    @Test
    @DisplayName("실패: 존재하지 않는 상담 세션 ID")
    public void updateStartDateTimeInCounselSession_CounseleeNotFound() {
        // Given
        String counselSessionId = "01JNBYN04P2JGB7CVQBPX02EXD";
        String scheduledStartDateTime = "2024-01-01 10:00";
        // When
        // Then
        Assertions.assertThatThrownBy(() -> counselSessionService.updateStartDateTimeInCounselSession(
                new UpdateStartTimeInCounselSessionReq(counselSessionId, scheduledStartDateTime)))
            .isInstanceOf(NoContentException.class);
    }
}