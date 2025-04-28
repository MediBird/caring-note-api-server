package com.springboot.api.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.counselcard.service.CounselCardService;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselee.repository.CounseleeRepository;
import com.springboot.api.counselsession.dto.counselsession.CreateCounselReservationReq;
import com.springboot.api.counselsession.dto.counselsession.CreateCounselReservationRes;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.api.counselsession.repository.CounselSessionRepository;
import com.springboot.api.counselsession.service.CounselSessionService;
import com.springboot.api.counselsession.service.CounseleeConsentService;
import com.springboot.enums.ScheduleStatus;
import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CounselSessionServiceTest {

    @Spy
    private DateTimeUtil dateTimeUtil;

    @Mock
    private CounselSessionRepository counselSessionRepository;

    @Mock
    private CounseleeRepository counseleeRepository;

    @Mock
    private CounselCardService counselCardService;

    @Mock
    private CounseleeConsentService counseleeConsentService;

    @InjectMocks
    private CounselSessionService counselSessionService;

    @Test
    @DisplayName("성공: createReservation가 예약을 생성")
    public void createReservation() {
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
        CreateCounselReservationRes response = counselSessionService.createReservation(
            new CreateCounselReservationReq(counselSessionId, scheduledStartDateTime));
        // Then
        Assertions.assertThat(counselSession.getId()).isEqualTo(response.getId());
    }

    // @Test
    // @DisplayName("성공 : updateStartDateTimeInCounselSession가 상담 시간을 수정")
    // public void updateStartDateTimeInCounselSession() {
    // // Given
    // Counselee counselee = Counselee.builder().build();
    // Counselor counselor = Counselor.builder().build();

    // String counselSessionId = "01JNBYN04P2JGB7CVQBPX02EXD";
    // String scheduledStartDateTime = "2024-01-01 10:00";
    // LocalDateTime scheduleDateTime =
    // dateTimeUtil.parseToDateTime(scheduledStartDateTime);

    // CounselSession counselSession = Mockito.spy(CounselSession.builder()
    // .counselee(counselee)
    // .counselor(counselor)
    // .scheduledStartDateTime(scheduleDateTime)
    // .status(ScheduleStatus.SCHEDULED)
    // .build());

    // given(counselSessionRepository.findByIdWithCounseleeAndCounselor(counselSessionId))
    // .willReturn(Optional.of(counselSession));
    // given(counselSessionRepository.existsByCounseleeAndScheduledStartDateTime(counselee,
    // scheduleDateTime)).willReturn(false);
    // given(counselSessionRepository.existsByCounselorAndScheduledStartDateTime(counselor,
    // scheduleDateTime)).willReturn(false);
    // given(counselSession.getId()).willReturn(counselSessionId);
    // // When
    // UpdateCounselSessionRes response =
    // counselSessionService.updateStartDateTimeInCounselSession(
    // new UpdateStartTimeInCounselSessionReq(counselSessionId,
    // scheduledStartDateTime));
    // // Then
    // verify(counselSession).updateScheduledStartDateTime(scheduleDateTime);
    // Assertions.assertThat(counselSession.getId()).isEqualTo(response.updatedCounselSessionId());
    // }

    // @Test
    // @DisplayName("실패: 상담 시간 중복")
    // public void updateStartDateTimeInCounselSession_CounseleeAndStartDateTime() {
    // // Given
    // Counselee counselee = Counselee.builder().build();
    // Counselor counselor = Counselor.builder().build();

    // String counselSessionId = "01JNBYN04P2JGB7CVQBPX02EXD";
    // String scheduledStartDateTime = "2024-01-01 10:00";
    // LocalDateTime scheduleDateTime =
    // dateTimeUtil.parseToDateTime(scheduledStartDateTime);

    // CounselSession counselSession = CounselSession.builder()
    // .counselee(counselee)
    // .counselor(counselor)
    // .build();

    // given(counselSessionRepository.findByIdWithCounseleeAndCounselor(counselSessionId))
    // .willReturn(Optional.of(counselSession));
    // given(counselSessionRepository.existsByCounseleeAndScheduledStartDateTime(counselee,
    // scheduleDateTime))
    // .willReturn(true);
    // // When
    // // Then
    // Assertions.assertThatThrownBy(() ->
    // counselSessionService.updateStartDateTimeInCounselSession(
    // new UpdateStartTimeInCounselSessionReq(counselSessionId,
    // scheduledStartDateTime)))
    // .isInstanceOf(IllegalArgumentException.class)
    // .hasMessage("해당 시간에 이미 상담이 예약되어 있습니다");
    // }

    // @Test
    // @DisplayName("실패: 상담 시간 중복")
    // public void updateStartDateTimeInCounselSession_CounselorAndStartDateTime() {
    // // Given
    // Counselee counselee = Counselee.builder().build();
    // Counselor counselor = Counselor.builder().build();

    // String counselSessionId = "01JNBYN04P2JGB7CVQBPX02EXD";
    // String scheduledStartDateTime = "2024-01-01 10:00";
    // LocalDateTime scheduleDateTime =
    // dateTimeUtil.parseToDateTime(scheduledStartDateTime);

    // CounselSession counselSession = CounselSession.builder()
    // .counselee(counselee)
    // .counselor(counselor)
    // .build();

    // given(counselSessionRepository.findByIdWithCounseleeAndCounselor(counselSessionId))
    // .willReturn(Optional.of(counselSession));
    // given(counselSessionRepository.existsByCounseleeAndScheduledStartDateTime(counselee,
    // scheduleDateTime))
    // .willReturn(false);
    // given(counselSessionRepository.existsByCounselorAndScheduledStartDateTime(counselor,
    // scheduleDateTime))
    // .willReturn(true);
    // // When
    // // Then
    // Assertions.assertThatThrownBy(() ->
    // counselSessionService.updateStartDateTimeInCounselSession(
    // new UpdateStartTimeInCounselSessionReq(counselSessionId,
    // scheduledStartDateTime)))
    // .isInstanceOf(IllegalArgumentException.class)
    // .hasMessage("해당 시간에 이미 상담이 예약되어 있습니다");
    // }

    // @Test
    // @DisplayName("실패: 존재하지 않는 상담 세션 ID")
    // public void updateStartDateTimeInCounselSession_CounseleeNotFound() {
    // // Given
    // String counselSessionId = "01JNBYN04P2JGB7CVQBPX02EXD";
    // String scheduledStartDateTime = "2024-01-01 10:00";
    // // When
    // // Then
    // Assertions.assertThatThrownBy(() ->
    // counselSessionService.updateStartDateTimeInCounselSession(
    // new UpdateStartTimeInCounselSessionReq(counselSessionId,
    // scheduledStartDateTime)))
    // .isInstanceOf(NoContentException.class);
    // }
}