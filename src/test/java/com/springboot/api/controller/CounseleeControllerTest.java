package com.springboot.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.common.config.security.SecurityConfig;
import com.springboot.api.common.converter.CustomJwtRoleConverter;
import com.springboot.api.config.TestSecurityConfig;
import com.springboot.api.dto.counselee.AddCounseleeReq;
import com.springboot.api.dto.counselee.DeleteCounseleeBatchRes;
import com.springboot.api.dto.counselee.SelectCounseleeBaseInformationByCounseleeIdRes;
import com.springboot.api.dto.counselee.SelectCounseleePageRes;
import com.springboot.api.dto.counselee.SelectCounseleeRes;
import com.springboot.api.dto.counselee.UpdateCounseleeReq;
import com.springboot.api.service.CounseleeService;
import com.springboot.enums.GenderType;

@WebMvcTest(CounseleeController.class)
@Import({ SecurityConfig.class, TestSecurityConfig.class })
public class CounseleeControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private CounseleeService counseleeService;

        @MockBean
        private JwtDecoder jwtDecoder;

        @MockBean
        private CustomJwtRoleConverter customJwtRoleConverter;

        @Autowired
        private ObjectMapper objectMapper;

        private final String VALID_COUNSEL_SESSION_ID = "01HQ7YXHG8ZYXM5T2Q3X4KDJPL";
        private final String INVALID_COUNSEL_SESSION_ID = "invalid-id";

        @Test
        public void testAddCounseleeSuccess() throws Exception {
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));
                mockJwtToken(authorities);

                String requestBody = "{ \"name\": \"John Doe\", \"phoneNumber\": \"010-1234-5678\", \"dateOfBirth\": \"1990-01-01\", \"genderType\": \"MALE\" }";

                when(counseleeService.addCounselee(any(AddCounseleeReq.class))).thenReturn("Success");

                mockMvc.perform(post("/v1/counsel/counselee/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer token")
                                .content(requestBody))
                                .andExpect(status().isOk());
        }

        @Test
        public void testAddCounseleeWithMissingFields() throws Exception {
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));
                mockJwtToken(authorities);
                String requestBody = "{ \"name\": \"\", \"phoneNumber\": \"\", \"dateOfBirth\": \"\" }";

                mockMvc.perform(post("/v1/counsel/counselee/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer mock-token")
                                .content(requestBody))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.data.errors").isArray());
        }

        @Test
        public void testUpdateCounseleeSuccess() throws Exception {
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));
                mockJwtToken(authorities);
                String requestBody = "{ \"counseleeId\": \"01HQ7YXHG8ZYXM5T2Q3X4KDJPJ\", \"name\": \"John Doe\", \"phoneNumber\": \"010-1234-5678\", \"dateOfBirth\": \"1990-01-01\" , \"genderType\": \"MALE\"}";

                when(counseleeService.updateCounselee(any(UpdateCounseleeReq.class))).thenReturn("Success");

                mockMvc.perform(put("/v1/counsel/counselee/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer token")
                                .content(requestBody))
                                .andExpect(status().isOk());
        }

        @Test
        public void testSelectCounseleeSuccess() throws Exception {
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));
                mockJwtToken(authorities);

                SelectCounseleeRes mockResponse = SelectCounseleeRes.builder()
                                .id("01HQ7YXHG8ZYXM5T2Q3X4KDJPJ")
                                .name("John Doe")
                                .phoneNumber("010-1234-5678")
                                .build();

                when(counseleeService.selectCounselee(anyString())).thenReturn(mockResponse);

                mockMvc.perform(get("/v1/counsel/counselee/{counseleeId}", "01HQ7YXHG8ZYXM5T2Q3X4KDJPJ")
                                .header("Authorization", "Bearer token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.id").value("01HQ7YXHG8ZYXM5T2Q3X4KDJPJ"))
                                .andExpect(jsonPath("$.data.name").value("John Doe"))
                                .andExpect(jsonPath("$.data.phoneNumber").value("010-1234-5678"));
        }

        @Test
        public void testSelectCounseleeListSuccess() throws Exception {
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));
                mockJwtToken(authorities);
                List<SelectCounseleeRes> mockList = Arrays.asList(
                                SelectCounseleeRes.builder()
                                                .id("01HQ7YXHG8ZYXM5T2Q3X4KDJPJ")
                                                .name("John Doe")
                                                .build(),
                                SelectCounseleeRes.builder()
                                                .id("01HQ7YXHG8ZYXM5T2Q3X4KDJPK")
                                                .name("Jane Doe")
                                                .build());
                SelectCounseleePageRes mockPageRes = SelectCounseleePageRes.builder()
                                .content(mockList)
                                .totalPages(1)
                                .totalElements(2)
                                .currentPage(0)
                                .hasNext(false)
                                .hasPrevious(false)
                                .build();
                when(counseleeService.selectCounselees(
                                eq(0),
                                eq(10),
                                isNull(),
                                isNull(),
                                isNull())).thenReturn(mockPageRes);

                mockMvc.perform(get("/v1/counsel/counselee/")
                                .param("page", "0")
                                .param("size", "10")
                                .header("Authorization", "Bearer token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.content").isArray())
                                .andExpect(jsonPath("$.data.totalPages").value(1))
                                .andExpect(jsonPath("$.data.totalElements").value(2))
                                .andExpect(jsonPath("$.data.currentPage").value(0))
                                .andExpect(jsonPath("$.data.hasNext").value(false))
                                .andExpect(jsonPath("$.data.hasPrevious").value(false));
        }

        @Test
        public void testDeleteCounseleeSuccess() throws Exception {
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));
                mockJwtToken(authorities);

                mockMvc.perform(delete("/v1/counsel/counselee/{counseleeId}", "01HQ7YXHG8ZYXM5T2Q3X4KDJPJ")
                                .header("Authorization", "Bearer token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data").value("delete the counselee success"));
        }

        @Test
        public void testDeleteCounseleeBatchSuccess() throws Exception {
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));
                mockJwtToken(authorities);

                String requestBody = "[{\"counseleeId\": \"01HQ7YXHG8ZYXM5T2Q3X4KDJPJ\"}, {\"counseleeId\": \"01HQ7YXHG8ZYXM5T2Q3X4KDJPK\"}]";

                List<DeleteCounseleeBatchRes> mockResponse = Arrays.asList(
                                DeleteCounseleeBatchRes.builder()
                                                .deletedCounseleeId("01HQ7YXHG8ZYXM5T2Q3X4KDJPJ")
                                                .build(),
                                DeleteCounseleeBatchRes.builder()
                                                .deletedCounseleeId("01HQ7YXHG8ZYXM5T2Q3X4KDJPK")
                                                .build());

                when(counseleeService.deleteCounseleeBatch(anyList())).thenReturn(mockResponse);

                mockMvc.perform(delete("/v1/counsel/counselee/batch")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer token")
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data").isArray())
                                .andExpect(jsonPath("$.data.length()").value(2));
        }

        @Test
        public void testSelectCounseleeBaseInformationSuccess() throws Exception {
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));
                mockJwtToken(authorities);

                SelectCounseleeBaseInformationByCounseleeIdRes mockResponse = SelectCounseleeBaseInformationByCounseleeIdRes
                                .builder()
                                .counseleeId("01HQ7YXHG8ZYXM5T2Q3X4KDJPJ")
                                .name("John Doe")
                                .build();

                when(counseleeService.selectCounseleeBaseInformation(anyString())).thenReturn(mockResponse);

                mockMvc.perform(get("/v1/counsel/counselee/{counselSessionId}/base/information",
                                "01HQ7YXHG8ZYXM5T2Q3X4KDJPJ")
                                .header("Authorization", "Bearer token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.counseleeId").value("01HQ7YXHG8ZYXM5T2Q3X4KDJPJ"))
                                .andExpect(jsonPath("$.data.name").value("John Doe"));
        }

        @Test
        public void testSelectBaseInfo_WithAdminRole_Success() throws Exception {
                // Given
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));
                mockJwtToken(authorities);

                SelectCounseleeBaseInformationByCounseleeIdRes mockResponse = SelectCounseleeBaseInformationByCounseleeIdRes
                                .builder()
                                .counseleeId("testId")
                                .name("홍길동")
                                .build();

                when(counseleeService.selectCounseleeBaseInformation(VALID_COUNSEL_SESSION_ID))
                                .thenReturn(mockResponse);

                // When & Then
                mockMvc.perform(get("/v1/counsel/counselee/{counselSessionId}/base/information",
                                VALID_COUNSEL_SESSION_ID)
                                .header("Authorization", "Bearer token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.name").value("홍길동"));
        }

        @Test
        public void testSelectBaseInfo_WithInvalidRole_Forbidden() throws Exception {
                // Given
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_NONE"));
                mockJwtToken(authorities);

                // When & Then
                mockMvc.perform(get("/v1/counsel/counselee/{counselSessionId}/base/information",
                                VALID_COUNSEL_SESSION_ID)
                                .header("Authorization", "Bearer token"))
                                .andExpect(status().isForbidden());
        }

        @Test
        public void testSelectBaseInfo_WithInvalidSessionId_BadRequest() throws Exception {
                // Given
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));
                mockJwtToken(authorities);

                // When & Then
                mockMvc.perform(get("/v1/counsel/counselee/{counselSessionId}/base/information",
                                INVALID_COUNSEL_SESSION_ID)
                                .header("Authorization", "Bearer token"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        public void testSelectBaseInfo_WithoutToken_Unauthorized() throws Exception {
                // When & Then
                mockMvc.perform(get("/v1/counsel/counselee/{counselSessionId}/base/information",
                                VALID_COUNSEL_SESSION_ID))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        public void testAddCounselee_WithAdminRole_Success() throws Exception {
                // Given
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));
                mockJwtToken(authorities);

                AddCounseleeReq request = AddCounseleeReq.builder()
                                .name("홍길동")
                                .phoneNumber("010-1234-5678")
                                .genderType(GenderType.MALE)
                                .dateOfBirth(LocalDate.of(1990, 1, 1))
                                .build();

                when(counseleeService.addCounselee(any(AddCounseleeReq.class)))
                                .thenReturn("success");

                // When & Then
                mockMvc.perform(post("/v1/counsel/counselee/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("Authorization", "Bearer token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data").value("success"));
        }

        @Test
        public void testAddCounselee_WithUserRole_Forbidden() throws Exception {
                // Given
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_USER"));
                mockJwtToken(authorities);

                AddCounseleeReq request = AddCounseleeReq.builder()
                                .name("홍길동")
                                .phoneNumber("010-1234-5678")
                                .genderType(GenderType.MALE)
                                .dateOfBirth(LocalDate.of(1990, 1, 1))
                                .build();

                // When & Then
                mockMvc.perform(post("/v1/counsel/counselee/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("Authorization", "Bearer token"))
                                .andExpect(status().isForbidden());
        }

        @Test
        public void testAddCounselee_WithInvalidRequest_BadRequest() throws Exception {
                // Given
                Collection<GrantedAuthority> authorities = Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_ADMIN"));
                mockJwtToken(authorities);

                AddCounseleeReq request = AddCounseleeReq.builder()
                                .name("") // 빈 이름
                                .phoneNumber("invalid") // 잘못된 전화번호 형식
                                .build();

                // When & Then
                mockMvc.perform(post("/v1/counsel/counselee/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("Authorization", "Bearer token"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        public void testAddCounselee_WithoutToken_Unauthorized() throws Exception {
                // Given
                AddCounseleeReq request = AddCounseleeReq.builder()
                                .name("홍길동")
                                .phoneNumber("01012345678")
                                .build();

                // When & Then
                mockMvc.perform(post("/v1/counsel/counselee/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isUnauthorized());
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
