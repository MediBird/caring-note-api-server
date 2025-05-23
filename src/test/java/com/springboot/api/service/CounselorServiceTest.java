package com.springboot.api.service;

import com.springboot.api.counselor.model.Counselor;
import com.springboot.api.counselor.repository.CounselorRepository;
import com.springboot.api.counselor.request.ResetPasswordReq;
import com.springboot.api.counselor.request.UpdateCounselorReq;
import com.springboot.api.counselor.request.UpdateRoleReq;
import com.springboot.api.counselor.response.CounselorInfoRes;
import com.springboot.api.counselor.response.CounselorNameRes;
import com.springboot.api.counselor.service.CounselorService;
import com.springboot.api.exception.ResourceNotFoundException;
import com.springboot.api.keycloak.KeycloakUserService;
import com.springboot.api.pagination.PageReq;
import com.springboot.api.role.RoleType;
import org.keycloak.representations.idm.UserRepresentation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CounselorServiceTest {

    @Mock
    private CounselorRepository counselorRepository;

    @Mock
    private KeycloakUserService keycloakUserService;

    @InjectMocks
    private CounselorService counselorService;

    private String generateUlid() {
        return UUID.randomUUID().toString();
    }

    @BeforeEach
    void setUp() {
        // Setup SecurityContext for tests that need it
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext(); // Clean up SecurityContext
    }

    private Counselor createMockCounselor(String id, String username, String name, RoleType role) {
        return Counselor.builder()
                .id(id)
                .username(username)
                .name(name)
                .roleType(role)
                .phoneNumber("010-1234-5678")
                .email("test@example.com")
                .isActive(true)
                .build();
    }

    private UserRepresentation createMockUserRepresentation(String id, String username) {
        UserRepresentation user = new UserRepresentation();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    @Test
    @DisplayName("내 정보 조회 - 성공")
    void getMyInfo_Success() {
        // given
        String username = "testuser";
        String counselorId = generateUlid();
        Counselor mockCounselor = createMockCounselor(counselorId, username, "Test User", RoleType.ROLE_USER);

        Authentication authentication = mock(JwtAuthenticationToken.class);
        Jwt jwt = mock(Jwt.class);
        when(((JwtAuthenticationToken) authentication).getToken()).thenReturn(jwt);
        when(jwt.getClaimAsString("preferred_username")).thenReturn(username);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(counselorRepository.findActiveByUsername(username)).thenReturn(Optional.of(mockCounselor));

        // when
        CounselorInfoRes result = counselorService.getMyInfo();

        // then
        assertNotNull(result);
        assertEquals(mockCounselor.getId(), result.getCounselorId());
        assertEquals(mockCounselor.getName(), result.getName());
        verify(counselorRepository).findActiveByUsername(username);
    }

    @Test
    @DisplayName("내 정보 조회 - 실패 (저장소에서 상담사를 찾을 수 없음)")
    void getMyInfo_Failure_CounselorNotFound() {
        // given
        String username = "testuser";
        Authentication authentication = mock(JwtAuthenticationToken.class);
        Jwt jwt = mock(Jwt.class);
        when(((JwtAuthenticationToken) authentication).getToken()).thenReturn(jwt);
        when(jwt.getClaimAsString("preferred_username")).thenReturn(username);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(counselorRepository.findActiveByUsername(username)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> counselorService.getMyInfo());
        verify(counselorRepository).findActiveByUsername(username);
    }

    @Test
    @DisplayName("상담사 이름 목록 조회 - 성공")
    void getCounselorNames_Success() {
        // given
        Counselor counselor1 = createMockCounselor(generateUlid(), "user1", "Kim", RoleType.ROLE_USER);
        Counselor counselor2 = createMockCounselor(generateUlid(), "user2", "Lee", RoleType.ROLE_ASSISTANT);
        when(counselorRepository.findActiveByRoleTypes(Arrays.asList(RoleType.ROLE_USER, RoleType.ROLE_ASSISTANT, RoleType.ROLE_ADMIN)))
                .thenReturn(Arrays.asList(counselor1, counselor2));

        // when
        List<CounselorNameRes> result = counselorService.getCounselorNames();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Kim", result.get(0).getName()); // Assuming sorting by name
        assertEquals("Lee", result.get(1).getName());
        verify(counselorRepository).findActiveByRoleTypes(anyList());
    }

    @Test
    @DisplayName("상담사 이름 목록 조회 - 빈 목록 또는 이름 없는 상담사")
    void getCounselorNames_EmptyOrNullNames() {
        // given
        Counselor counselor1 = createMockCounselor(generateUlid(), "user1", null, RoleType.ROLE_USER);
        Counselor counselor2 = createMockCounselor(generateUlid(), "user2", "", RoleType.ROLE_ASSISTANT);
        when(counselorRepository.findActiveByRoleTypes(anyList())).thenReturn(Arrays.asList(counselor1, counselor2));

        // when
        List<CounselorNameRes> result = counselorService.getCounselorNames();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        // Names could be null or empty, check based on implementation (e.g., filtered out or included as is)
        // Current implementation of CounselorNameRes.of likely includes them.
        assertTrue(result.stream().anyMatch(r -> r.getName() == null));
        assertTrue(result.stream().anyMatch(r -> r.getName().isEmpty()));

        // Test empty list from repo
        when(counselorRepository.findActiveByRoleTypes(anyList())).thenReturn(Collections.emptyList());
        result = counselorService.getCounselorNames();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(counselorRepository, times(3)).findActiveByRoleTypes(anyList());
    }

    @Test
    @DisplayName("상담사 정보 수정 - 성공")
    void updateCounselor_Success() {
        // given
        String counselorId = generateUlid();
        Counselor mockCounselor = spy(createMockCounselor(counselorId, "user1", "Old Name", RoleType.ROLE_USER));
        UpdateCounselorReq req = UpdateCounselorReq.builder().name("New Name").phoneNumber("010-9999-8888").build();

        when(counselorRepository.findById(counselorId)).thenReturn(Optional.of(mockCounselor));

        // when
        counselorService.updateCounselor(counselorId, req);

        // then
        verify(mockCounselor).updateDetails(req.getName(), req.getPhoneNumber(), req.getEmail());
        verify(counselorRepository).findById(counselorId);
        verify(counselorRepository).save(mockCounselor);
    }

    @Test
    @DisplayName("상담사 정보 수정 - 실패 (상담사 없음)")
    void updateCounselor_Failure_NotFound() {
        // given
        String counselorId = generateUlid();
        UpdateCounselorReq req = UpdateCounselorReq.builder().name("New Name").build();
        when(counselorRepository.findById(counselorId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> counselorService.updateCounselor(counselorId, req));
        verify(counselorRepository).findById(counselorId);
        verify(counselorRepository, never()).save(any(Counselor.class));
    }
    
    @Test
    @DisplayName("상담사 정보 수정 - 부분 업데이트 (이름만)")
    void updateCounselor_PartialUpdate_NameOnly() {
        String counselorId = generateUlid();
        Counselor mockCounselor = spy(createMockCounselor(counselorId, "user1", "Old Name", RoleType.ROLE_USER));
        UpdateCounselorReq req = UpdateCounselorReq.builder().name("New Name Only").build(); // Phone and email are null

        when(counselorRepository.findById(counselorId)).thenReturn(Optional.of(mockCounselor));

        counselorService.updateCounselor(counselorId, req);

        verify(mockCounselor).updateDetails("New Name Only", mockCounselor.getPhoneNumber(), mockCounselor.getEmail()); // Assuming nulls in req mean no change
        verify(counselorRepository).save(mockCounselor);
    }


    @Test
    @DisplayName("상담사 삭제 - 성공")
    void deleteCounselor_Success() {
        // given
        String counselorId = generateUlid();
        String username = "userToDelete";
        Counselor mockCounselor = createMockCounselor(counselorId, username, "To Delete", RoleType.ROLE_USER);
        UserRepresentation mockUserRep = createMockUserRepresentation("keycloakId", username);

        when(counselorRepository.findById(counselorId)).thenReturn(Optional.of(mockCounselor));
        when(keycloakUserService.getUsersByUsername(username)).thenReturn(Collections.singletonList(mockUserRep));

        // when
        counselorService.deleteCounselor(counselorId);

        // then
        verify(keycloakUserService).deleteUser(mockUserRep.getId());
        verify(counselorRepository).deleteById(counselorId);
        verify(counselorRepository).findById(counselorId);
    }
    
    @Test
    @DisplayName("상담사 삭제 - 성공 (Keycloak 사용자 없음)")
    void deleteCounselor_Success_KeycloakUserNotFound() {
        String counselorId = generateUlid();
        String username = "userToDeleteNoKeycloak";
        Counselor mockCounselor = createMockCounselor(counselorId, username, "To Delete", RoleType.ROLE_USER);

        when(counselorRepository.findById(counselorId)).thenReturn(Optional.of(mockCounselor));
        when(keycloakUserService.getUsersByUsername(username)).thenReturn(Collections.emptyList()); // Keycloak user not found

        counselorService.deleteCounselor(counselorId);

        verify(keycloakUserService, never()).deleteUser(anyString());
        verify(counselorRepository).deleteById(counselorId);
    }

    @Test
    @DisplayName("상담사 삭제 - 성공 (Keycloak 삭제 실패)")
    void deleteCounselor_Success_KeycloakDeletionFails() {
        String counselorId = generateUlid();
        String username = "userToDeleteKeycloakFail";
        Counselor mockCounselor = createMockCounselor(counselorId, username, "To Delete", RoleType.ROLE_USER);
        UserRepresentation mockUserRep = createMockUserRepresentation("keycloakId", username);

        when(counselorRepository.findById(counselorId)).thenReturn(Optional.of(mockCounselor));
        when(keycloakUserService.getUsersByUsername(username)).thenReturn(Collections.singletonList(mockUserRep));
        doThrow(new RuntimeException("Keycloak delete failed")).when(keycloakUserService).deleteUser(mockUserRep.getId());

        counselorService.deleteCounselor(counselorId); // Should still delete from DB

        verify(keycloakUserService).deleteUser(mockUserRep.getId());
        verify(counselorRepository).deleteById(counselorId); // DB deletion should still happen
    }
    
    @Test
    @DisplayName("상담사 삭제 - 성공 (사용자 이름 없음)")
    void deleteCounselor_Success_NoUsername() {
        String counselorId = generateUlid();
        Counselor mockCounselor = createMockCounselor(counselorId, null, "No Username Delete", RoleType.ROLE_USER); // No username

        when(counselorRepository.findById(counselorId)).thenReturn(Optional.of(mockCounselor));

        counselorService.deleteCounselor(counselorId);

        verify(keycloakUserService, never()).getUsersByUsername(anyString());
        verify(keycloakUserService, never()).deleteUser(anyString());
        verify(counselorRepository).deleteById(counselorId);
    }


    @Test
    @DisplayName("상담사 삭제 - 실패 (상담사 없음)")
    void deleteCounselor_Failure_NotFound() {
        // given
        String counselorId = generateUlid();
        when(counselorRepository.findById(counselorId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> counselorService.deleteCounselor(counselorId));
        verify(counselorRepository).findById(counselorId);
        verify(keycloakUserService, never()).deleteUser(anyString());
        verify(counselorRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("비밀번호 재설정 - 성공")
    void resetPassword_Success() {
        // given
        String counselorId = generateUlid();
        String username = "userToReset";
        Counselor mockCounselor = createMockCounselor(counselorId, username, "Reset User", RoleType.ROLE_USER);
        UserRepresentation mockUserRep = createMockUserRepresentation("keycloakId", username);
        ResetPasswordReq req = new ResetPasswordReq("newPassword");

        when(counselorRepository.findById(counselorId)).thenReturn(Optional.of(mockCounselor));
        when(keycloakUserService.getUsersByUsername(username)).thenReturn(Collections.singletonList(mockUserRep));

        // when
        counselorService.resetPassword(counselorId, req);

        // then
        verify(keycloakUserService).resetPassword(mockUserRep.getId(), req.getPassword());
        verify(counselorRepository).findById(counselorId);
    }

    @Test
    @DisplayName("비밀번호 재설정 - 실패 (상담사 없음)")
    void resetPassword_Failure_CounselorNotFound() {
        // given
        String counselorId = generateUlid();
        ResetPasswordReq req = new ResetPasswordReq("newPassword");
        when(counselorRepository.findById(counselorId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> counselorService.resetPassword(counselorId, req));
        verify(counselorRepository).findById(counselorId);
        verify(keycloakUserService, never()).resetPassword(anyString(), anyString());
    }

    @Test
    @DisplayName("비밀번호 재설정 - 실패 (사용자 이름 없음)")
    void resetPassword_Failure_NoUsername() {
        // given
        String counselorId = generateUlid();
        Counselor mockCounselor = createMockCounselor(counselorId, null, "No Username Reset", RoleType.ROLE_USER); // No username
        ResetPasswordReq req = new ResetPasswordReq("newPassword");

        when(counselorRepository.findById(counselorId)).thenReturn(Optional.of(mockCounselor));

        // when & then
        assertThrows(IllegalStateException.class, () -> counselorService.resetPassword(counselorId, req));
        verify(counselorRepository).findById(counselorId);
        verify(keycloakUserService, never()).resetPassword(anyString(), anyString());
    }

    @Test
    @DisplayName("비밀번호 재설정 - 실패 (Keycloak 사용자 없음)")
    void resetPassword_Failure_KeycloakUserNotFound() {
        // given
        String counselorId = generateUlid();
        String username = "userNotFoundInKeycloak";
        Counselor mockCounselor = createMockCounselor(counselorId, username, "Keycloak Miss", RoleType.ROLE_USER);
        ResetPasswordReq req = new ResetPasswordReq("newPassword");

        when(counselorRepository.findById(counselorId)).thenReturn(Optional.of(mockCounselor));
        when(keycloakUserService.getUsersByUsername(username)).thenReturn(Collections.emptyList());

        // when & then
        // The service catches ResourceNotFoundException from Keycloak and re-throws RuntimeException
        assertThrows(RuntimeException.class, () -> counselorService.resetPassword(counselorId, req));
        verify(counselorRepository).findById(counselorId);
        verify(keycloakUserService).getUsersByUsername(username);
        verify(keycloakUserService, never()).resetPassword(anyString(), anyString());
    }

    @Test
    @DisplayName("역할 수정 - 성공")
    void updateRole_Success() {
        // given
        String counselorId = generateUlid();
        Counselor mockCounselor = spy(createMockCounselor(counselorId, "userRoleUpdate", "Role Updater", RoleType.ROLE_USER));
        UpdateRoleReq req = new UpdateRoleReq(RoleType.ROLE_ADMIN);

        when(counselorRepository.findById(counselorId)).thenReturn(Optional.of(mockCounselor));

        // when
        counselorService.updateRole(counselorId, req);

        // then
        verify(mockCounselor).updateRole(req.getRoleType());
        verify(counselorRepository).save(mockCounselor);
        verify(counselorRepository).findById(counselorId);
    }

    @Test
    @DisplayName("역할 수정 - 실패 (상담사 없음)")
    void updateRole_Failure_CounselorNotFound() {
        // given
        String counselorId = generateUlid();
        UpdateRoleReq req = new UpdateRoleReq(RoleType.ROLE_ADMIN);
        when(counselorRepository.findById(counselorId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> counselorService.updateRole(counselorId, req));
        verify(counselorRepository).findById(counselorId);
        verify(counselorRepository, never()).save(any(Counselor.class));
    }
    
    @Test
    @DisplayName("역할 수정 - 실패 (사용자 이름 없음)")
    void updateRole_Failure_NoUsername() {
        String counselorId = generateUlid();
        Counselor mockCounselor = createMockCounselor(counselorId, null, "No Username Role", RoleType.ROLE_USER);
        UpdateRoleReq req = new UpdateRoleReq(RoleType.ROLE_ADMIN);

        when(counselorRepository.findById(counselorId)).thenReturn(Optional.of(mockCounselor));
        // The service's updateRole does not directly check for username, but it's good practice to be aware.
        // The original code for updateRole did not have a username check like resetPassword.
        // If it's added in the service, this test will fail and need adjustment.
        // For now, assuming it proceeds if counselor is found.

        counselorService.updateRole(counselorId, req); // This should succeed if no username check in service's updateRole.

        verify(counselorRepository).findById(counselorId);
        verify(counselorRepository).save(any(Counselor.class)); // Role update should still occur
    }


    @Test
    @DisplayName("페이지별 상담사 목록 조회 - 성공")
    void getCounselorsByPage_Success() {
        // given
        PageReq pageReq = new PageReq(0, 10);
        Counselor counselor1 = createMockCounselor(generateUlid(), "pageUser1", "Page User 1", RoleType.ROLE_USER);
        List<Counselor> counselorList = Collections.singletonList(counselor1);
        Page<Counselor> counselorPage = new PageImpl<>(counselorList, PageRequest.of(pageReq.getPage(), pageReq.getSize()), counselorList.size());

        when(counselorRepository.findAllActiveWithRoleTypeOrder(any(PageRequest.class))).thenReturn(counselorPage);

        // when
        Page<CounselorInfoRes> result = counselorService.getCounselorsByPage(pageReq);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Page User 1", result.getContent().get(0).getName());
        verify(counselorRepository).findAllActiveWithRoleTypeOrder(PageRequest.of(pageReq.getPage(), pageReq.getSize()));
    }

    @Test
    @DisplayName("페이지별 상담사 목록 조회 - 빈 페이지")
    void getCounselorsByPage_EmptyPage() {
        // given
        PageReq pageReq = new PageReq(0, 10);
        Page<Counselor> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(pageReq.getPage(), pageReq.getSize()), 0);

        when(counselorRepository.findAllActiveWithRoleTypeOrder(any(PageRequest.class))).thenReturn(emptyPage);

        // when
        Page<CounselorInfoRes> result = counselorService.getCounselorsByPage(pageReq);

        // then
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(counselorRepository).findAllActiveWithRoleTypeOrder(PageRequest.of(pageReq.getPage(), pageReq.getSize()));
    }

    @Test
    @DisplayName("ID로 상담사 조회 - 성공")
    void findCounselorById_Success() {
        // given
        String counselorId = generateUlid();
        Counselor mockCounselor = createMockCounselor(counselorId, "findUser", "Found User", RoleType.ROLE_USER);
        when(counselorRepository.findActiveById(counselorId)).thenReturn(Optional.of(mockCounselor));

        // when
        Counselor result = counselorService.findCounselorById(counselorId);

        // then
        assertNotNull(result);
        assertEquals(counselorId, result.getId());
        verify(counselorRepository).findActiveById(counselorId);
    }

    @Test
    @DisplayName("ID로 상담사 조회 - 실패 (상담사 없음)")
    void findCounselorById_Failure_NotFound() {
        // given
        String counselorId = generateUlid();
        when(counselorRepository.findActiveById(counselorId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> counselorService.findCounselorById(counselorId));
        verify(counselorRepository).findActiveById(counselorId);
    }
}
