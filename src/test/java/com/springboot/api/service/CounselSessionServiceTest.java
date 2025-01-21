package com.springboot.api.service;

import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.Counselee;
import com.springboot.api.domain.Counselor;
import com.springboot.api.dto.counselsession.*;
import com.springboot.api.repository.CounselSessionRepository;
import com.springboot.api.repository.CounselorRepository;
import com.springboot.enums.ScheduleStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class CounselSessionServiceTest {

    private static final Logger log = LoggerFactory.getLogger(CounselSessionServiceTest.class);
    @Autowired
    private CounselSessionService service;

    @MockBean
    private CounselSessionRepository sessionRepository;

    @MockBean
    private CounselorRepository counselorRepository;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private DateTimeUtil dateTimeUtil;

    private Counselor mockCounselor;

    private Counselor mockCounselor2;

    private Counselee mockCounselee;

    private CounselSession mockCounselSession;

    @BeforeEach
    void setUp() {
        mockCounselor = new Counselor();
        mockCounselor.setId("counselor-1");
        mockCounselor.setName("Test Counselor");

        mockCounselor2 = new Counselor();
        mockCounselor2.setId("counselor-2");
        mockCounselor2.setName("Test Counselor2");

        mockCounselee = new Counselee();
        mockCounselee.setId("counselee-1");
        mockCounselee.setName("Test Counselee");

        mockCounselSession = new CounselSession();
        mockCounselSession.setId("session-1");
        mockCounselSession.setCounselor(mockCounselor);
        mockCounselSession.setCounselee(mockCounselee);
        mockCounselSession.setScheduledStartDateTime(LocalDateTime.now());
    }

    @Test
    void addCounselSession() {
        AddCounselSessionReq req =  AddCounselSessionReq
                .builder()
                .counselorId("counselor-1")
                .counseleeId("counselee-1")
                .scheduledStartDateTime("2024-11-25 14:00")
                .status(ScheduleStatus.SCHEDULED)
                .build();

        when(entityManager.getReference(Counselor.class, req.getCounselorId())).thenReturn(mockCounselor);
        when(entityManager.getReference(Counselee.class, req.getCounseleeId())).thenReturn(mockCounselee);
        when(dateTimeUtil.parseToDateTime(req.getScheduledStartDateTime())).thenReturn(LocalDateTime.of(2024, 11, 25, 14, 0));
        when(sessionRepository.save(any(CounselSession.class))).thenReturn(mockCounselSession);

        AddCounselSessionRes res = service.addCounselSession(req);
        assertNotNull(res);
        assertEquals("session-1", res.counselSessionId());
        verify(sessionRepository, times(1)).save(any(CounselSession.class));
    }

    @Test
    void selectCounselSession() {
        when(sessionRepository.findById("session-1")).thenReturn(Optional.of(mockCounselSession));

        SelectCounselSessionRes res = service.selectCounselSession("session-1");

        assertNotNull(res);
        assertEquals("session-1", res.getCounselSessionId());
        assertEquals("Test Counselee", res.getCounseleeName());
    }

    @Test
    void selectCounselSessionList() {
        SelectCounselSessionListByBaseDateAndCursorAndSizeReq req = SelectCounselSessionListByBaseDateAndCursorAndSizeReq
                .builder()
               .cursor(null)
                .baseDate(LocalDate.now())
                .size(5)
                .build();

        when(sessionRepository.findByDateAndCursor(any(),any(), any(), any(), any()))
                .thenReturn(List.of(mockCounselSession));

        SelectCounselSessionListByBaseDateAndCursorAndSizeRes res = service.selectCounselSessionListByBaseDateAndCursorAndSize(req);

        assertNotNull(res);
        assertEquals(1, res.sessionListItems().size());

        res.sessionListItems()
                .forEach(s->{
                            log.debug("session :"+ s.toString());
                        });

        assertEquals("Test Counselee", res.sessionListItems().getFirst().getCounseleeName());
    }

    @Test
    void updateCounselSession() {
        UpdateCounselSessionReq req = UpdateCounselSessionReq
                .builder()
                .counselSessionId("session-1")
                .counselorId("counselor-1")
                .counseleeId("counselee-1")
                .scheduledStartDateTime("2024-11-26 10:00")
                .build();

        when(sessionRepository.findById("session-1")).thenReturn(Optional.of(mockCounselSession));
        when(entityManager.getReference(Counselor.class, req.getCounselorId())).thenReturn(mockCounselor);
        when(entityManager.getReference(Counselee.class, req.getCounseleeId())).thenReturn(mockCounselee);
        when(dateTimeUtil.parseToDateTime(req.getScheduledStartDateTime())).thenReturn(LocalDateTime.of(2024, 11, 26, 10, 0));

        UpdateCounselSessionRes res = service.updateCounselSession(req);

        assertNotNull(res);
        assertEquals("session-1", res.updatedCounselSessionId());
        verify(sessionRepository, times(1)).findById(eq("session-1"));
    }

    @Test
    void deleteCounselSession() {

        doNothing().when(sessionRepository).deleteById("session-1");

        DeleteCounselSessionRes res = service.deleteCounselSessionRes(DeleteCounselSessionReq.builder().counselSessionId("session-1").build());

        assertNotNull(res);
        assertEquals("session-1", res.deletedCounselSessionId());
        verify(sessionRepository, times(1)).deleteById("session-1");
    }

    @Test
    void updateCounselorInCounselSession() {

        String userId ="counselor-2";
        UpdateCounselorInCounselSessionReq updateCounselorInCounselSessionReq
                = UpdateCounselorInCounselSessionReq.builder()
                .counselSessionId("session-1")
                .build();


        // Setting mock behaviors
        when(sessionRepository.findById(anyString())).thenReturn(Optional.of(mockCounselSession));
        when(counselorRepository.findById(anyString())).thenReturn(Optional.of(mockCounselor2));

        // Act
        UpdateCounselorInCounselSessionRes response = service.updateCounselorInCounselSession(updateCounselorInCounselSessionReq);

        // Assert
        assertNotNull(response);
        assertEquals("session-1", response.updatedCounselSessionId());


    }

    @Test
    void updateStatusInCounselSession() {

        UpdateStatusInCounselSessionReq updateStatusInCounselSessionReq
                = UpdateStatusInCounselSessionReq.builder()
                .counselSessionId("session-1")
                .status(ScheduleStatus.CANCELED)
                .build();

        // Setting mock behaviors
        when(sessionRepository.findById(anyString())).thenReturn(Optional.of(mockCounselSession));

        // Act
        UpdateStatusInCounselSessionRes response = service.updateStatusInCounselSession(updateStatusInCounselSessionReq);

        // Assert
        assertNotNull(response);
        assertEquals("session-1", response.updatedCounselSessionId());


    }
}