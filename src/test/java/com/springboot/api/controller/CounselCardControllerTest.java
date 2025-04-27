package com.springboot.api.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.common.message.HttpMessages;
import com.springboot.api.config.TestSecurityConfig;
import com.springboot.api.counselcard.controller.CounselCardController;
import com.springboot.api.counselcard.dto.information.independentlife.CommunicationDTO;
import com.springboot.api.counselcard.dto.request.UpdateCounselCardStatusReq;
import com.springboot.api.counselcard.dto.response.CounselCardBaseInformationRes;
import com.springboot.api.counselcard.dto.response.CounselCardHealthInformationRes;
import com.springboot.api.counselcard.dto.response.CounselCardIdRes;
import com.springboot.api.counselcard.dto.response.TimeRecordedRes;
import com.springboot.api.counselcard.entity.CounselCard;
import com.springboot.api.counselcard.entity.information.independentlife.Communication;
import com.springboot.api.counselcard.service.CounselCardService;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.enums.CardRecordStatus;
import com.springboot.enums.CounselCardRecordType;
import com.springboot.enums.RoleType;

@WebMvcTest(CounselCardController.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class CounselCardControllerTest {

    private static final String VALID_COUNSEL_SESSION_ID = "01HQ8VQXG7RZDQ1234567890AB";
    private static final String VALID_COUNSEL_CARD_ID = "01HQ8VQXG7RZDQ1234567890AB";
    
    //TODO: MockMvcTester로 수정
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CounselCardService counselCardService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private CustomJwtRoleConverter customJwtRoleConverter;

    @Test
    @DisplayName("성공: 상담 카드 기본 정보 조회")
    void selectCounselCardBaseInformation_Success() throws Exception {
        Collection<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(RoleType.ROLE_ADMIN.name()));
        mockJwtToken(authorities);

        Counselee counselee = Counselee.builder()
            .id("01HQ8VQXG7RZDQ1234567890AB")
            .name("테스트 상담자")
            .isDisability(false)
            .dateOfBirth(LocalDate.of(1990, 1, 1))
            .build();
        CounselSession counselSession = CounselSession.builder()
            .id(VALID_COUNSEL_SESSION_ID)
            .counselee(counselee)
            .build();

        CounselCardBaseInformationRes mockResponse = new CounselCardBaseInformationRes(
            CounselCard.createFromSession(counselSession));

        when(counselCardService.selectCounselCardBaseInformation(VALID_COUNSEL_SESSION_ID))
            .thenReturn(mockResponse);

        mockMvc.perform(get("/v1/counsel/card/{counselSessionId}/base-information", VALID_COUNSEL_SESSION_ID)
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("성공: 상담 카드 상태 수정")
    void updateCounselCardStatus_Success() throws Exception {
        Collection<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(RoleType.ROLE_ADMIN.name()));
        mockJwtToken(authorities);

        UpdateCounselCardStatusReq request = new UpdateCounselCardStatusReq(CardRecordStatus.COMPLETED);
        CounselCardIdRes mockResponse = new CounselCardIdRes(VALID_COUNSEL_CARD_ID);

        when(counselCardService.updateCounselCardStatus(anyString(), any(CardRecordStatus.class)))
            .thenReturn(mockResponse);

        mockMvc.perform(put("/v1/counsel/card/{counselSessionId}/status", VALID_COUNSEL_SESSION_ID)
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.counselCardId").value(VALID_COUNSEL_CARD_ID));
    }

    @Test
    @DisplayName("성공: 상담 카드 삭제")
    void deleteCounselCard_Success() throws Exception {
        Collection<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(RoleType.ROLE_ADMIN.name()));
        mockJwtToken(authorities);

        CounselCardIdRes mockResponse = new CounselCardIdRes(VALID_COUNSEL_CARD_ID);

        when(counselCardService.deleteCounselCard(VALID_COUNSEL_SESSION_ID))
            .thenReturn(mockResponse);

        mockMvc.perform(delete("/v1/counsel/card/{counselSessionId}", VALID_COUNSEL_SESSION_ID)
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.counselCardId").value(VALID_COUNSEL_CARD_ID));
    }

    @Test
    @DisplayName("성공: 이전 상담 카드 item 목록 조회")
    void selectPreviousItemList_Success() throws Exception {
        Collection<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(RoleType.ROLE_ADMIN.name()));
        mockJwtToken(authorities);

        TimeRecordedRes<Object> mockResponse = new TimeRecordedRes<>("2024-01-01",
            new CommunicationDTO(Communication.initializeDefault()));

        when(counselCardService.selectPreviousRecordsByType(anyString(), any(CounselCardRecordType.class)))
            .thenReturn(List.of(mockResponse));

        mockMvc.perform(get("/v1/counsel/card/{counselSessionId}/previous/item/list", VALID_COUNSEL_SESSION_ID)
                .header("Authorization", "Bearer token")
                .param("type", CounselCardRecordType.COMMUNICATION.name())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].counselDate").value("2024-01-01"));
    }

    @Test
    @DisplayName("실패: 이전 상담 카드 item 목록 조회 - 데이터 없음")
    void selectPreviousItemList_NoContent() throws Exception {
        Collection<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(RoleType.ROLE_ADMIN.name()));
        mockJwtToken(authorities);

        when(counselCardService.selectPreviousRecordsByType(anyString(), any(CounselCardRecordType.class)))
            .thenThrow(new NoContentException("이전 상담 카드가 없습니다"));

        mockMvc.perform(get("/v1/counsel/card/{counselSessionId}/previous/item/list", VALID_COUNSEL_SESSION_ID)
                .header("Authorization", "Bearer token")
                .param("type", CounselCardRecordType.COMMUNICATION.name())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("실패: 이전 상담 카드 item 목록 조회 - 잘못된 상담 세션 ID")
    void selectPreviousItemList_InvalidSessionId() throws Exception {
        Collection<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(RoleType.ROLE_ADMIN.name()));
        mockJwtToken(authorities);

        when(counselCardService.selectPreviousRecordsByType(anyString(), any(CounselCardRecordType.class)))
            .thenThrow(new IllegalArgumentException("상담 세션을 찾을 수 없습니다"));

        mockMvc.perform(get("/v1/counsel/card/{counselSessionId}/previous/item/list", VALID_COUNSEL_SESSION_ID)
                .header("Authorization", "Bearer token")
                .param("type", CounselCardRecordType.COMMUNICATION.name())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("실패: 인증되지 않은 사용자")
    void unauthorized_Access() throws Exception {
        mockMvc.perform(get("/v1/counsel/card/{counselSessionId}", VALID_COUNSEL_SESSION_ID)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }


    @Test
    @DisplayName("실패: 서버 내부 오류")
    void internalServerError() throws Exception {
        Collection<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(RoleType.ROLE_ADMIN.name()));
        mockJwtToken(authorities);

        when(counselCardService.selectCounselCard(VALID_COUNSEL_SESSION_ID))
            .thenThrow(new RuntimeException("내부 서버 오류"));

        mockMvc.perform(get("/v1/counsel/card/{counselSessionId}", VALID_COUNSEL_SESSION_ID)
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value(HttpMessages.INTERNAL_SERVER_ERROR));
    }

    @Test
    @DisplayName("성공: 상담 카드 건강 정보 조회")
    void selectCounselCardHealthInformation_Success() throws Exception {
        Collection<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(RoleType.ROLE_ADMIN.name()));
        mockJwtToken(authorities);

        Counselee counselee = Counselee.builder()
            .id("01HQ8VQXG7RZDQ1234567890AB")
            .name("테스트 상담자")
            .isDisability(false)
            .dateOfBirth(LocalDate.of(1990, 1, 1))
            .build();
        CounselSession counselSession = CounselSession.builder()
            .id(VALID_COUNSEL_SESSION_ID)
            .counselee(counselee)
            .build();

        CounselCardHealthInformationRes mockResponse = new CounselCardHealthInformationRes(
            CounselCard.createFromSession(counselSession));

        when(counselCardService.selectCounselCardHealthInformation(VALID_COUNSEL_SESSION_ID))
            .thenReturn(mockResponse);

        mockMvc.perform(get("/v1/counsel/card/{counselSessionId}/health-information", VALID_COUNSEL_SESSION_ID)
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    private void mockJwtToken(Collection<GrantedAuthority> authorities) {
        Jwt jwt = Jwt.withTokenValue("token")
            .header("alg", "none")
            .claim("preferred_username", "testUser")
            .build();

        when(jwtDecoder.decode(anyString())).thenReturn(jwt);
        when(customJwtRoleConverter.convert(jwt)).thenReturn(authorities);
    }
}