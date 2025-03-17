package com.springboot.api.controller;

import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.common.config.security.SecurityConfig;
import com.springboot.api.common.converter.CustomJwtRoleConverter;
import com.springboot.api.config.TestSecurityConfig;
import com.springboot.api.counselsession.controller.CounseleeConsentController;
import com.springboot.api.counselsession.dto.counseleeconsent.AddCounseleeConsentReq;
import com.springboot.api.counselsession.dto.counseleeconsent.AddCounseleeConsentRes;
import com.springboot.api.counselsession.dto.counseleeconsent.DeleteCounseleeConsentRes;
import com.springboot.api.counselsession.dto.counseleeconsent.SelectCounseleeConsentByCounseleeIdRes;
import com.springboot.api.counselsession.dto.counseleeconsent.UpdateCounseleeConsentReq;
import com.springboot.api.counselsession.dto.counseleeconsent.UpdateCounseleeConsentRes;
import com.springboot.api.counselsession.service.CounseleeConsentService;

@WebMvcTest(CounseleeConsentController.class)
@Import({ SecurityConfig.class, TestSecurityConfig.class })
public class CounseleeConsentControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private CounseleeConsentService counseleeConsentService;

        @MockBean
        private JwtDecoder jwtDecoder;

        @MockBean
        private CustomJwtRoleConverter customJwtRoleConverter;

        private final String VALID_COUNSELEE_ID = "01HQ7YXHG8ZYXM5T2Q3X4KDJPJ";
        private final String VALID_COUNSEL_SESSION_ID = "01HQ7YXHG8ZYXM5T2Q3X4KDJPL";
        private final String VALID_COUNSELEE_CONSENT_ID = "01HQ7YXHG8ZYXM5T2Q3X4KDJPJ";

        private Jwt jwt;

        @BeforeEach
        public void setUp() {
                jwt = Jwt.withTokenValue("token")
                                .header("alg", "none")
                                .claim("preferred_username", "testUser")
                                .build();

                when(jwtDecoder.decode(anyString())).thenReturn(jwt);
        }

        @Test
        public void testSelectConsent_WithAdminRole_Success() throws Exception {
                // Given
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));

                when(customJwtRoleConverter.convert(jwt)).thenReturn(authorities);

                SelectCounseleeConsentByCounseleeIdRes mockResponse = SelectCounseleeConsentByCounseleeIdRes.builder()
                                .counseleeId(VALID_COUNSELEE_ID)
                                .isConsent(true)
                                .build();

                when(counseleeConsentService.selectCounseleeConsentByCounseleeId(anyString(), anyString()))
                                .thenReturn(mockResponse);

                // When & Then
                mockMvc.perform(get("/v1/counselee/consent/{counselSessionId}", VALID_COUNSEL_SESSION_ID)
                                .param("counseleeId", VALID_COUNSELEE_ID)
                                .header("Authorization", "Bearer token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.counseleeId").value(VALID_COUNSELEE_ID))
                                .andExpect(jsonPath("$.data.isConsent").value(true));
        }

        @Test
        public void testSelectConsent_WithUserRole_Success() throws Exception {
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_USER"));

                when(customJwtRoleConverter.convert(jwt)).thenReturn(authorities);

                SelectCounseleeConsentByCounseleeIdRes mockResponse = SelectCounseleeConsentByCounseleeIdRes.builder()
                                .counseleeId(VALID_COUNSELEE_ID)
                                .isConsent(true)
                                .build();

                when(counseleeConsentService.selectCounseleeConsentByCounseleeId(anyString(), anyString()))
                                .thenReturn(mockResponse);

                // When & Then
                mockMvc.perform(get("/v1/counselee/consent/{counselSessionId}", VALID_COUNSEL_SESSION_ID)
                                .param("counseleeId", VALID_COUNSELEE_ID)
                                .header("Authorization", "Bearer token"))
                                .andExpect(status().isOk());
        }

        @Test
        public void testSelectConsent_WithInvalidRole_Failure() throws Exception {
                // Given
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_NONE"));
                when(customJwtRoleConverter.convert(jwt)).thenReturn(authorities);

                // When & Then
                mockMvc.perform(get("/v1/counselee/consent/{counselSessionId}", VALID_COUNSEL_SESSION_ID)
                                .param("counseleeId", VALID_COUNSELEE_ID)
                                .header("Authorization", "Bearer token"))
                                .andExpect(status().isForbidden());
        }

        @Test
        public void testSelectConsent_WithInvalidCounselSessionId_Failure() throws Exception {
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));
                when(customJwtRoleConverter.convert(jwt)).thenReturn(authorities);

                // When & Then
                mockMvc.perform(get("/v1/counselee/consent/{counselSessionId}", "invalid-id")
                                .param("counseleeId", VALID_COUNSELEE_ID)
                                .header("Authorization", "Bearer token"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        public void testSelectConsent_WithInvalidCounseleeId_Failure() throws Exception {
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));

                when(customJwtRoleConverter.convert(jwt)).thenReturn(authorities);

                // When & Then
                mockMvc.perform(get("/v1/counselee/consent/{counselSessionId}", VALID_COUNSEL_SESSION_ID)
                                .param("counseleeId", "invalid-id")
                                .header("Authorization", "Bearer token"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        public void testSelectConsent_WithoutToken_Failure() throws Exception {
                // When & Then
                mockMvc.perform(get("/v1/counselee/consent/{counselSessionId}", VALID_COUNSEL_SESSION_ID)
                                .param("counseleeId", VALID_COUNSELEE_ID))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        public void testAddConsent_WithAdminRole_Success() throws Exception {
                // Given
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));
                when(customJwtRoleConverter.convert(jwt)).thenReturn(authorities);

                AddCounseleeConsentReq request = AddCounseleeConsentReq.builder()
                                .counselSessionId(VALID_COUNSEL_SESSION_ID)
                                .counseleeId(VALID_COUNSELEE_ID)
                                .isConsent(true)
                                .build();

                AddCounseleeConsentRes mockResponse = AddCounseleeConsentRes.builder()
                                .counseleeConsentId(VALID_COUNSELEE_ID)
                                .build();

                when(counseleeConsentService.addCounseleeConsent(any(AddCounseleeConsentReq.class)))
                                .thenReturn(mockResponse);

                // When & Then
                mockMvc.perform(post("/v1/counselee/consent")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(request))
                                .header("Authorization", "Bearer token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.counseleeConsentId").value(VALID_COUNSELEE_ID));
        }

        @Test
        public void testAddConsent_WithInvalidRole_Failure() throws Exception {
                // Given
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_NONE"));
                when(customJwtRoleConverter.convert(jwt)).thenReturn(authorities);

                AddCounseleeConsentReq request = AddCounseleeConsentReq.builder()
                                .counselSessionId(VALID_COUNSEL_SESSION_ID)
                                .counseleeId(VALID_COUNSELEE_ID)
                                .isConsent(true)
                                .build();

                // When & Then
                mockMvc.perform(post("/v1/counselee/consent")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(request))
                                .header("Authorization", "Bearer token"))
                                .andExpect(status().isForbidden());
        }

        @Test
        public void testUpdateConsent_WithUserRole_Success() throws Exception {
                // Given
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_USER"));
                when(customJwtRoleConverter.convert(jwt)).thenReturn(authorities);

                UpdateCounseleeConsentReq request = UpdateCounseleeConsentReq.builder()
                                .counseleeConsentId(VALID_COUNSELEE_ID)
                                .isConsent(false)
                                .build();

                UpdateCounseleeConsentRes mockResponse = UpdateCounseleeConsentRes.builder()
                                .updatedCounseleeConsentId(VALID_COUNSELEE_CONSENT_ID)
                                .build();

                when(counseleeConsentService.updateCounseleeConsent(any(UpdateCounseleeConsentReq.class)))
                                .thenReturn(mockResponse);

                // When & Then
                mockMvc.perform(put("/v1/counselee/consent")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(request))
                                .header("Authorization", "Bearer token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.updatedCounseleeConsentId")
                                                .value(VALID_COUNSELEE_CONSENT_ID));
        }

        @Test
        public void testDeleteConsent_WithAssistantRole_Success() throws Exception {
                // Given
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ASSISTANT"));
                when(customJwtRoleConverter.convert(jwt)).thenReturn(authorities);

                DeleteCounseleeConsentRes mockResponse = DeleteCounseleeConsentRes.builder()
                                .deletedCounseleeConsentId(VALID_COUNSELEE_CONSENT_ID)
                                .build();

                when(counseleeConsentService.deleteCounseleeConsent(anyString()))
                                .thenReturn(mockResponse);

                // When & Then
                mockMvc.perform(delete("/v1/counselee/consent/{counseleeConsentId}", VALID_COUNSELEE_CONSENT_ID)
                                .header("Authorization", "Bearer token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.deletedCounseleeConsentId")
                                                .value(VALID_COUNSELEE_CONSENT_ID));
        }

        @Test
        public void testDeleteConsent_WithoutToken_Failure() throws Exception {
                // When & Then
                mockMvc.perform(delete("/v1/counselee/consent/{counseleeConsentId}", VALID_COUNSELEE_CONSENT_ID))
                                .andExpect(status().isUnauthorized());
        }

}