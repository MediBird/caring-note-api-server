package com.springboot.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.config.TestSecurityConfig;
import com.springboot.api.counselor.controller.CounselorController;
import com.springboot.api.counselor.request.ResetPasswordReq;
import com.springboot.api.counselor.request.UpdateCounselorReq;
import com.springboot.api.counselor.request.UpdateRoleReq;
import com.springboot.api.counselor.response.CounselorInfoRes;
import com.springboot.api.counselor.response.CounselorNameRes;
import com.springboot.api.counselor.service.CounselorService;
import com.springboot.api.exception.ResourceNotFoundException;
import com.springboot.api.pagination.PageReq;
import com.springboot.api.role.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CounselorController.class)
@Import(TestSecurityConfig.class)
class CounselorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CounselorService counselorService;

    @Autowired
    private ObjectMapper objectMapper;

    private String generateUlid() {
        return UUID.randomUUID().toString();
    }

    // GET /my-info
    @Test
    @DisplayName("내 정보 조회 - 성공 (ROLE_USER)")
    @WithMockUser(username = "user", roles = "USER")
    void getMyInfo_Success() throws Exception {
        CounselorInfoRes mockResponse = CounselorInfoRes.builder().counselorId(generateUlid()).name("Test User").build();
        when(counselorService.getMyInfo()).thenReturn(mockResponse);

        mockMvc.perform(get("/v1/counselor/my-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Test User"));
    }

    @Test
    @DisplayName("내 정보 조회 - 서비스 예외 발생")
    @WithMockUser(username = "user", roles = "USER")
    void getMyInfo_ServiceException() throws Exception {
        when(counselorService.getMyInfo()).thenThrow(new IllegalArgumentException("Counselor not found"));

        mockMvc.perform(get("/v1/counselor/my-info"))
                .andExpect(status().isBadRequest()); // Assuming @ControllerAdvice handles IllegalArgumentException to 400
    }

    @Test
    @DisplayName("내 정보 조회 - 인증되지 않은 사용자")
    void getMyInfo_Unauthenticated() throws Exception {
        mockMvc.perform(get("/v1/counselor/my-info"))
                .andExpect(status().isForbidden()); // RoleSecuredAspect
    }

    // GET /names
    @Test
    @DisplayName("상담사 이름 목록 조회 - 성공 (ROLE_USER)")
    @WithMockUser(username = "user", roles = "USER")
    void getCounselorNames_Success() throws Exception {
        List<CounselorNameRes> mockResponse = List.of(CounselorNameRes.builder().name("Counselor A").build());
        when(counselorService.getCounselorNames()).thenReturn(mockResponse);

        mockMvc.perform(get("/v1/counselor/names"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Counselor A"));
    }

    @Test
    @DisplayName("상담사 이름 목록 조회 - 인증되지 않은 사용자")
    void getCounselorNames_Unauthenticated() throws Exception {
        mockMvc.perform(get("/v1/counselor/names"))
                .andExpect(status().isForbidden()); // RoleSecuredAspect
    }
    
    // PUT /{counselorId} (updateCounselor)
    @Test
    @DisplayName("상담사 정보 수정 - 성공 (ROLE_ADMIN)")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCounselor_Success() throws Exception {
        String counselorId = generateUlid();
        UpdateCounselorReq reqBody = UpdateCounselorReq.builder().name("New Name").phoneNumber("010-1111-2222").email("new@example.com").build();
        doNothing().when(counselorService).updateCounselor(eq(counselorId), any(UpdateCounselorReq.class));

        mockMvc.perform(put("/v1/counselor/{counselorId}", counselorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("상담사 정보 수정 - 상담사 없음 (ROLE_ADMIN)")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCounselor_NotFound() throws Exception {
        String counselorId = generateUlid();
        UpdateCounselorReq reqBody = UpdateCounselorReq.builder().name("New Name").build();
        doThrow(new IllegalArgumentException("Counselor not found")).when(counselorService).updateCounselor(eq(counselorId), any(UpdateCounselorReq.class));

        mockMvc.perform(put("/v1/counselor/{counselorId}", counselorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isBadRequest()); // IllegalArgumentException -> 400
    }

    @Test
    @DisplayName("상담사 정보 수정 - 잘못된 요청 본문 (ROLE_ADMIN)")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateCounselor_InvalidRequestBody() throws Exception {
        String counselorId = generateUlid();
        // Assuming UpdateCounselorReq has @NotBlank on name for example
        UpdateCounselorReq reqBody = UpdateCounselorReq.builder().name("").build(); // Invalid: empty name

        mockMvc.perform(put("/v1/counselor/{counselorId}", counselorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isBadRequest()); // @Valid
    }

    @Test
    @DisplayName("상담사 정보 수정 - 권한 부족 (ROLE_USER)")
    @WithMockUser(username = "user", roles = "USER")
    void updateCounselor_AccessDenied_UserRole() throws Exception {
        String counselorId = generateUlid();
        UpdateCounselorReq reqBody = UpdateCounselorReq.builder().name("New Name").build();

        mockMvc.perform(put("/v1/counselor/{counselorId}", counselorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("상담사 정보 수정 - 인증되지 않은 사용자")
    void updateCounselor_Unauthenticated() throws Exception {
        String counselorId = generateUlid();
        UpdateCounselorReq reqBody = UpdateCounselorReq.builder().name("New Name").build();

        mockMvc.perform(put("/v1/counselor/{counselorId}", counselorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isForbidden());
    }

    // DELETE /{counselorId}
    @Test
    @DisplayName("상담사 삭제 - 성공 (ROLE_ADMIN)")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteCounselor_Success() throws Exception {
        String counselorId = generateUlid();
        doNothing().when(counselorService).deleteCounselor(counselorId);

        mockMvc.perform(delete("/v1/counselor/{counselorId}", counselorId))
                .andExpect(status().isOk()) // Assuming it returns 200 OK with CommonRes
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("상담사 삭제 - 상담사 없음 (ROLE_ADMIN)")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteCounselor_NotFound() throws Exception {
        String counselorId = generateUlid();
        doThrow(new ResourceNotFoundException("Counselor not found")).when(counselorService).deleteCounselor(counselorId);

        mockMvc.perform(delete("/v1/counselor/{counselorId}", counselorId))
                .andExpect(status().isNotFound()); // ResourceNotFoundException -> 404
    }

    @Test
    @DisplayName("상담사 삭제 - 권한 부족 (ROLE_USER)")
    @WithMockUser(username = "user", roles = "USER")
    void deleteCounselor_AccessDenied_UserRole() throws Exception {
        String counselorId = generateUlid();
        mockMvc.perform(delete("/v1/counselor/{counselorId}", counselorId))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("상담사 삭제 - 인증되지 않은 사용자")
    void deleteCounselor_Unauthenticated() throws Exception {
        String counselorId = generateUlid();
        mockMvc.perform(delete("/v1/counselor/{counselorId}", counselorId))
                .andExpect(status().isForbidden());
    }

    // POST /{counselorId}/reset-password
    @Test
    @DisplayName("비밀번호 재설정 - 성공 (ROLE_ADMIN)")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void resetPassword_Success() throws Exception {
        String counselorId = generateUlid();
        ResetPasswordReq reqBody = new ResetPasswordReq("newValidPassword1!");
        doNothing().when(counselorService).resetPassword(eq(counselorId), any(ResetPasswordReq.class));

        mockMvc.perform(post("/v1/counselor/{counselorId}/reset-password", counselorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("비밀번호 재설정 - 잘못된 요청 본문 (비밀번호 누락) (ROLE_ADMIN)")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void resetPassword_InvalidRequestBody_MissingPassword() throws Exception {
        String counselorId = generateUlid();
        // Assuming ResetPasswordReq has @NotBlank on password
        ResetPasswordReq reqBody = new ResetPasswordReq(null); // Invalid

        mockMvc.perform(post("/v1/counselor/{counselorId}/reset-password", counselorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isBadRequest()); // @Valid
    }
    
    @Test
    @DisplayName("비밀번호 재설정 - 잘못된 요청 본문 (비밀번호 짧음) (ROLE_ADMIN)")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void resetPassword_InvalidRequestBody_ShortPassword() throws Exception {
        String counselorId = generateUlid();
        // ResetPasswordReq DTO has @Size(min = 8) on newPassword field.
        // Sending "short" (5 chars) should trigger a validation error.
        ResetPasswordReq reqBody = new ResetPasswordReq("short"); 

        mockMvc.perform(post("/v1/counselor/{counselorId}/reset-password", counselorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isBadRequest()); // Expect 400 due to @Valid DTO validation
        
        // Verify that the service method is not called due to validation failure
        verify(counselorService, never()).resetPassword(anyString(), any(ResetPasswordReq.class));
    }


    @Test
    @DisplayName("비밀번호 재설정 - 상담사 없음 (ROLE_ADMIN)")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void resetPassword_CounselorNotFound() throws Exception {
        String counselorId = generateUlid();
        ResetPasswordReq reqBody = new ResetPasswordReq("newValidPassword1!");
        doThrow(new ResourceNotFoundException("Counselor not found")).when(counselorService).resetPassword(eq(counselorId), any(ResetPasswordReq.class));

        mockMvc.perform(post("/v1/counselor/{counselorId}/reset-password", counselorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isNotFound()); // ResourceNotFoundException -> 404
    }

    @Test
    @DisplayName("비밀번호 재설정 - 서비스 IllegalStateException (ROLE_ADMIN)")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void resetPassword_ServiceIllegalState() throws Exception {
        String counselorId = generateUlid();
        ResetPasswordReq reqBody = new ResetPasswordReq("newValidPassword1!");
        doThrow(new IllegalStateException("Cannot reset password for this user")).when(counselorService).resetPassword(eq(counselorId), any(ResetPasswordReq.class));

        mockMvc.perform(post("/v1/counselor/{counselorId}/reset-password", counselorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isConflict()); // Assuming IllegalStateException maps to 409
    }

    @Test
    @DisplayName("비밀번호 재설정 - 권한 부족 (ROLE_USER)")
    @WithMockUser(username = "user", roles = "USER")
    void resetPassword_AccessDenied_UserRole() throws Exception {
        String counselorId = generateUlid();
        ResetPasswordReq reqBody = new ResetPasswordReq("newValidPassword1!");
        mockMvc.perform(post("/v1/counselor/{counselorId}/reset-password", counselorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("비밀번호 재설정 - 인증되지 않은 사용자")
    void resetPassword_Unauthenticated() throws Exception {
        String counselorId = generateUlid();
        ResetPasswordReq reqBody = new ResetPasswordReq("newValidPassword1!");
        mockMvc.perform(post("/v1/counselor/{counselorId}/reset-password", counselorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isForbidden());
    }

    // PUT /{counselorId}/role (updateRole)
    @Test
    @DisplayName("역할 수정 - 성공 (ROLE_ADMIN)")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateRole_Success() throws Exception {
        String counselorId = generateUlid();
        UpdateRoleReq reqBody = new UpdateRoleReq(RoleType.ROLE_ASSISTANT);
        doNothing().when(counselorService).updateRole(eq(counselorId), any(UpdateRoleReq.class));

        mockMvc.perform(put("/v1/counselor/{counselorId}/role", counselorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("역할 수정 - 잘못된 요청 본문 (ROLE_ADMIN)")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateRole_InvalidRequestBody() throws Exception {
        String counselorId = generateUlid();
        // Assuming UpdateRoleReq has @NotNull on roleType
        UpdateRoleReq reqBody = new UpdateRoleReq(null); // Invalid

        mockMvc.perform(put("/v1/counselor/{counselorId}/role", counselorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isBadRequest()); // @Valid
    }

    @Test
    @DisplayName("역할 수정 - 상담사 없음 (ROLE_ADMIN)")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateRole_CounselorNotFound() throws Exception {
        String counselorId = generateUlid();
        UpdateRoleReq reqBody = new UpdateRoleReq(RoleType.ROLE_USER);
        doThrow(new ResourceNotFoundException("Counselor not found")).when(counselorService).updateRole(eq(counselorId), any(UpdateRoleReq.class));

        mockMvc.perform(put("/v1/counselor/{counselorId}/role", counselorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isNotFound()); // ResourceNotFoundException -> 404
    }

    @Test
    @DisplayName("역할 수정 - 권한 부족 (ROLE_USER)")
    @WithMockUser(username = "user", roles = "USER")
    void updateRole_AccessDenied_UserRole() throws Exception {
        String counselorId = generateUlid();
        UpdateRoleReq reqBody = new UpdateRoleReq(RoleType.ROLE_ADMIN);
        mockMvc.perform(put("/v1/counselor/{counselorId}/role", counselorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("역할 수정 - 인증되지 않은 사용자")
    void updateRole_Unauthenticated() throws Exception {
        String counselorId = generateUlid();
        UpdateRoleReq reqBody = new UpdateRoleReq(RoleType.ROLE_USER);
        mockMvc.perform(put("/v1/counselor/{counselorId}/role", counselorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqBody)))
                .andExpect(status().isForbidden());
    }

    // GET /page (getCounselorsByPage)
    @Test
    @DisplayName("페이지별 상담사 목록 조회 - 성공 (ROLE_ADMIN, 기본값)")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getCounselorsByPage_Success_Defaults() throws Exception {
        Page<CounselorInfoRes> mockPage = new PageImpl<>(
                List.of(CounselorInfoRes.builder().name("Admin User").build()),
                org.springframework.data.domain.PageRequest.of(0, 20), 1);
        when(counselorService.getCounselorsByPage(any(PageReq.class))).thenReturn(mockPage);

        mockMvc.perform(get("/v1/counselor/page"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].name").value("Admin User"));
        
        verify(counselorService).getCounselorsByPage(argThat(pageReq -> pageReq.getPage() == 0 && pageReq.getSize() == 20));
    }

    @Test
    @DisplayName("페이지별 상담사 목록 조회 - 성공 (ROLE_ADMIN, 파라미터 지정)")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getCounselorsByPage_Success_WithParams() throws Exception {
        Page<CounselorInfoRes> mockPage = new PageImpl<>(Collections.emptyList());
        when(counselorService.getCounselorsByPage(any(PageReq.class))).thenReturn(mockPage);

        mockMvc.perform(get("/v1/counselor/page")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isEmpty());

        verify(counselorService).getCounselorsByPage(argThat(pageReq -> pageReq.getPage() == 1 && pageReq.getSize() == 5));
    }

    @Test
    @DisplayName("페이지별 상담사 목록 조회 - 권한 부족 (ROLE_USER)")
    @WithMockUser(username = "user", roles = "USER")
    void getCounselorsByPage_AccessDenied_UserRole() throws Exception {
        mockMvc.perform(get("/v1/counselor/page"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("페이지별 상담사 목록 조회 - 인증되지 않은 사용자")
    void getCounselorsByPage_Unauthenticated() throws Exception {
        mockMvc.perform(get("/v1/counselor/page"))
                .andExpect(status().isForbidden());
    }
}
