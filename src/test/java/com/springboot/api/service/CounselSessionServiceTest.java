package com.springboot.api.service;

import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselee.repository.CounseleeRepository;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.api.counselsession.repository.CounselSessionRepository;
import com.springboot.api.counselsession.service.CounselSessionService;
import com.springboot.enums.GenderType;
import com.springboot.enums.HealthInsuranceType;
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

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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
    @DisplayName("patchCounselSession이 CounselSession이 없을 경우에 예약을 생성한다.")
    public void testPatchCounselSession_CreateCounselSession() {
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

        CounselSession counselSession = Mockito.spy(CounselSession.builder()
            .counselee(counselee)
            .scheduledStartDateTime(scheduleDateTime)
            .status(ScheduleStatus.SCHEDULED)
            .build());

        given(counseleeRepository.findById(any(String.class))).willReturn(java.util.Optional.of(counselee));
        given(counselSessionRepository.existsByCounseleeAndScheduledStartDateTime(counselee, scheduleDateTime)).willReturn(false);
        given(counselSessionRepository.save(any())).willReturn(counselSession);
        given(counselSession.getId()).willReturn("01JNBYN04P2JGB7CVQBPX02EXD");
        // When
        String sessionId = counselSessionService.patchCounselSession("01JNBYN04P2JGB7CVQBPX02EXD", scheduledStartDateTime);
        // Then
        Assertions.assertThat(counselSession.getId()).isEqualTo(sessionId);
    }

    @Test
    @DisplayName("patchCounselSession이 Counselee가 없을 경우에 예외를 발생시킨다.")
    public void testPatchCounselSession_ThrowExceptionWhenCounseleeNotFound() {
        // Given
        String scheduledStartDateTime = "2024-01-01 10:00";
        given(counseleeRepository.findById(any(String.class))).willReturn(java.util.Optional.empty());
        // When
        // Then
        Assertions.assertThatThrownBy(() -> counselSessionService.patchCounselSession("01JNBYN04P2JGB7CVQBPX02EXD", scheduledStartDateTime))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않는 내담자 ID입니다");
    }
}