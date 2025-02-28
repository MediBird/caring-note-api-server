package com.springboot.api.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import com.springboot.api.common.config.security.SecurityConfig;
import com.springboot.api.common.converter.CustomJwtRoleConverter;
import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.common.message.HttpMessages;
import com.springboot.api.config.TestSecurityConfig;
import com.springboot.api.counselcard.controller.CounselCardController;
import com.springboot.api.counselcard.dto.SelectCounselCardRes;
import com.springboot.api.counselcard.dto.SelectPreviousCounselCardRes;
import com.springboot.api.counselcard.dto.information.base.BaseInformationDTO;
import com.springboot.api.counselcard.dto.information.health.HealthInformationDTO;
import com.springboot.api.counselcard.dto.information.independentlife.IndependentLifeInformationDTO;
import com.springboot.api.counselcard.dto.information.living.LivingInformationDTO;
import com.springboot.api.counselsession.service.CounselCardService;
import com.springboot.enums.CardRecordStatus;

@WebMvcTest(CounselCardController.class)
@Import({ SecurityConfig.class, TestSecurityConfig.class })
class CounselCardControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private CounselCardService counselCardService;

        @MockBean
        private JwtDecoder jwtDecoder;

        @MockBean
        private CustomJwtRoleConverter customJwtRoleConverter;

        private static final String VALID_COUNSEL_SESSION_ID = "01HQ8VQXG7RZDQ1234567890AB";
        private static final String VALID_COUNSEL_CARD_ID = "01HQ8VQXG7RZDQ1234567890AB";
        private static final String INVALID_COUNSEL_SESSION_ID = "invalid";

        @Test
        @DisplayName("성공: 유효한 상담 세션 ID로 상담 카드 조회")
        void selectCounselCard_Success() throws Exception {
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));
                mockJwtToken(authorities);
                // given
                SelectCounselCardRes mockResponse = SelectCounselCardRes.builder()
                                .counselCardId(VALID_COUNSEL_CARD_ID)
                                .baseInformation(BaseInformationDTO.builder().build())
                                .healthInformation(HealthInformationDTO.builder().build())
                                .livingInformation(LivingInformationDTO.builder().build())
                                .independentLifeInformation(IndependentLifeInformationDTO.builder().build())
                                .cardRecordStatus(CardRecordStatus.RECORDED)
                                .build();

                when(counselCardService.selectCounselCard(VALID_COUNSEL_SESSION_ID))
                                .thenReturn(mockResponse);

                // when & then
                mockMvc.perform(get("/v1/counsel/card/{counselSessionId}", VALID_COUNSEL_SESSION_ID)
                                .header("Authorization", "Bearer token")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.counselCardId").value(VALID_COUNSEL_CARD_ID))
                                .andExpect(jsonPath("$.data.cardRecordStatus").value(CardRecordStatus.RECORDED.name()));
        }

        @Test
        @DisplayName("실패: 잘못된 길이의 상담 세션 ID")
        void selectCounselCard_InvalidLength() throws Exception {
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));
                mockJwtToken(authorities);
                // when & then
                mockMvc.perform(get("/v1/counsel/card/{counselSessionId}", INVALID_COUNSEL_SESSION_ID)
                                .header("Authorization", "Bearer token")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 상담 카드")
        void selectCounselCard_NotFound() throws Exception {
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));
                mockJwtToken(authorities);
                // given
                when(counselCardService.selectCounselCard(VALID_COUNSEL_SESSION_ID))
                                .thenThrow(new NoContentException("상담 카드를 찾을 수 없습니다"));

                // when & then
                mockMvc.perform(get("/v1/counsel/card/{counselSessionId}", VALID_COUNSEL_SESSION_ID)
                                .header("Authorization", "Bearer token")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("성공: 유효한 상담 세션 ID로 이전 상담 카드 조회")
        void selectPreviousCounselCard_Success() throws Exception {
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));
                mockJwtToken(authorities);
                // given
                SelectPreviousCounselCardRes mockResponse = SelectPreviousCounselCardRes.builder()
                                .baseInformation(BaseInformationDTO.builder().build())
                                .healthInformation(HealthInformationDTO.builder().build())
                                .livingInformation(LivingInformationDTO.builder().build())
                                .independentLifeInformation(IndependentLifeInformationDTO.builder().build())
                                .build();

                when(counselCardService.selectPreviousCounselCard(VALID_COUNSEL_SESSION_ID))
                                .thenReturn(mockResponse);

                // when & then
                mockMvc.perform(get("/v1/counsel/card/{counselSessionId}/previous", VALID_COUNSEL_SESSION_ID)
                                .header("Authorization", "Bearer token")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("실패: 잘못된 길이의 상담 세션 ID")
        void selectPreviousCounselCard_InvalidLength() throws Exception {
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));
                mockJwtToken(authorities);
                // when & then
                mockMvc.perform(get("/v1/counsel/card/{counselSessionId}/previous", INVALID_COUNSEL_SESSION_ID)
                                .header("Authorization", "Bearer token")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("실패: 이전 상담 카드가 존재하지 않음")
        void selectPreviousCounselCard_NotFound() throws Exception {
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));
                mockJwtToken(authorities);
                // given
                when(counselCardService.selectPreviousCounselCard(VALID_COUNSEL_SESSION_ID))
                                .thenThrow(new NoContentException("이전 상담 카드를 찾을 수 없습니다"));

                // when & then
                mockMvc.perform(get("/v1/counsel/card/{counselSessionId}/previous", VALID_COUNSEL_SESSION_ID)
                                .header("Authorization", "Bearer token")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("실패: 인증되지 않은 사용자")
        void selectPreviousCounselCard_Unauthorized() throws Exception {

                // when & then
                mockMvc.perform(get("/v1/counsel/card/{counselSessionId}/previous", VALID_COUNSEL_SESSION_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("실패: 권한 없는 역할")
        void selectPreviousCounselCard_Forbidden() throws Exception {
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_NONE"));
                mockJwtToken(authorities);
                // when & then
                mockMvc.perform(get("/v1/counsel/card/{counselSessionId}/previous", VALID_COUNSEL_SESSION_ID)
                                .header("Authorization", "Bearer token")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("실패: 서버 내부 오류")
        void selectPreviousCounselCard_InternalServerError() throws Exception {
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));
                mockJwtToken(authorities);
                // given
                when(counselCardService.selectPreviousCounselCard(VALID_COUNSEL_SESSION_ID))
                                .thenThrow(new RuntimeException("내부 서버 오류"));

                // when & then
                mockMvc.perform(get("/v1/counsel/card/{counselSessionId}/previous", VALID_COUNSEL_SESSION_ID)
                                .header("Authorization", "Bearer token")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.message").value(HttpMessages.INTERNAL_SERVER_ERROR));
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