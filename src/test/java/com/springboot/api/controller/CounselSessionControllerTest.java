package com.springboot.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.Collection;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.common.config.security.SecurityConfig;
import com.springboot.api.common.converter.CustomJwtRoleConverter;
import com.springboot.api.config.TestSecurityConfig;
import com.springboot.api.counselsession.controller.CounselSessionController;
import com.springboot.api.counselsession.dto.counselsession.CreateCounselReservationReq;
import com.springboot.api.counselsession.dto.counselsession.CreateCounselReservationRes;
import com.springboot.api.counselsession.service.CounselSessionService;

@WebMvcTest(CounselSessionController.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
public class CounselSessionControllerTest {

    private static final String VALID_COUNSEL_SESSION_ID = "01HQ8VQXG7RZDQ1234567890AB";
    private static final String VALID_COUNSELEE_ID = "01HQ8VQXG7RZDQ1234567890AB";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private CounselSessionService counselSessionService;
    @MockitoBean
    private JwtDecoder jwtDecoder;
    @MockitoBean
    private CustomJwtRoleConverter customJwtRoleConverter;

    @Test
    @DisplayName("성공: 내담자로 상담 세션 생성")
    void success_createCounselReservation() throws Exception {
        // Given
        Collection<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_ADMIN"));
        mockJwtToken(authorities);
        CreateCounselReservationRes mockResponse = new CreateCounselReservationRes(VALID_COUNSEL_SESSION_ID);
        CreateCounselReservationReq mockRequest = new CreateCounselReservationReq(VALID_COUNSELEE_ID,
            "2024-01-01 10:00");
        given(counselSessionService.createReservation(any(CreateCounselReservationReq.class)))
            .willReturn(mockResponse);

        // When&Then
        mockMvc.perform(post("/v1/counsel/session")
                .content(objectMapper.writeValueAsBytes(mockRequest))
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.data.id").value(VALID_COUNSEL_SESSION_ID));
    }

    // @Test
    // @DisplayName("성공: 상담 세션 시간 변경 완료")
    // void updateStartDateTimeInCounselSession() throws Exception {
    // // Given
    // Collection<GrantedAuthority> authorities = Collections.singletonList(
    // new SimpleGrantedAuthority("ROLE_ADMIN"));
    // mockJwtToken(authorities);

    // UpdateStartTimeInCounselSessionReq mockRequest = new
    // UpdateStartTimeInCounselSessionReq(
    // VALID_COUNSEL_SESSION_ID, "2024-01-01 10:00");
    // UpdateCounselSessionRes mockResponse = new
    // UpdateCounselSessionRes(VALID_COUNSEL_SESSION_ID);
    // given(counselSessionService.updateStartDateTimeInCounselSession(mockRequest)).willReturn(mockResponse);

    // // When&Then
    // mockMvc.perform(put("/v1/counsel/session/start-time")
    // .content(objectMapper.writeValueAsBytes(mockRequest))
    // .header("Authorization", "Bearer token")
    // .contentType(MediaType.APPLICATION_JSON))
    // .andExpect(status().isOk())
    // .andExpect(jsonPath("$.data.updatedCounselSessionId").value(VALID_COUNSEL_SESSION_ID));
    // }

    private void mockJwtToken(Collection<GrantedAuthority> authorities) {
        Jwt jwt = Jwt.withTokenValue("token")
            .header("alg", "none")
            .claim("preferred_username", "testUser")
            .build();

        when(jwtDecoder.decode(anyString())).thenReturn(jwt);
        when(customJwtRoleConverter.convert(jwt)).thenReturn(authorities);
    }
}
