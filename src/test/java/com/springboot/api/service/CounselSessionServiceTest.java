package com.springboot.api.service;

import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.Counselee;
import com.springboot.api.domain.Counselor;
import com.springboot.api.dto.counselsession.*;
import com.springboot.api.repository.CounselSessionRepository;
import com.springboot.enums.RoleType;
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
    private EntityManager entityManager;

    @MockBean
    private DateTimeUtil dateTimeUtil;

    private Counselor mockCounselor;
    private Counselee mockCounselee;
    private CounselSession mockCounselSession;

    @BeforeEach
    void setUp() {
        mockCounselor = new Counselor();
        mockCounselor.setId("counselor-1");
        mockCounselor.setName("Test Counselor");

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

        AddCounselSessionRes res = service.addCounselSession("user-1", req);
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
        SelectCounselSessionListReq req = SelectCounselSessionListReq
                .builder()
               .cursor(null)
                .baseDateTime(LocalDateTime.now())
                .size(5)
                .build();

        when(sessionRepository.findByCursor(any(), any(), any(), any()))
                .thenReturn(List.of(mockCounselSession));

        SelectCounselSessionListRes res = service.selectCounselSessionList("user-1", RoleType.USER, req);

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
}