package com.springboot.api.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.common.config.security.SecurityConfig;
import com.springboot.api.common.converter.CustomJwtRoleConverter;
import com.springboot.api.common.dto.PageRes;
import com.springboot.api.config.TestSecurityConfig;
import com.springboot.api.counselee.controller.CounseleeController;
import com.springboot.api.counselee.dto.AddCounseleeReq;
import com.springboot.api.counselee.dto.DeleteCounseleeBatchRes;
import com.springboot.api.counselee.dto.SelectCounseleeBaseInformationByCounseleeIdRes;
import com.springboot.api.counselee.dto.SelectCounseleeRes;
import com.springboot.api.counselee.dto.UpdateCounseleeReq;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselee.service.CounseleeService;
import com.springboot.api.fixture.CounseleeFixture;
import com.springboot.enums.GenderType;

@WebMvcTest(CounseleeController.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
public class CounseleeControllerTest {

    private final String VALID_COUNSEL_SESSION_ID = "01HQ7YXHG8ZYXM5T2Q3X4KDJPL";
    private final String INVALID_COUNSEL_SESSION_ID = "invalid-id";
    // TODO: MockMvcTester로 수정
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CounseleeService counseleeService;
    @MockitoBean
    private JwtDecoder jwtDecoder;
    @MockitoBean
    private CustomJwtRoleConverter customJwtRoleConverter;
    @Autowired
    private ObjectMapper objectMapper;

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
        String requestBody = "{ \"counseleeId\": \"01HQ7YXHG8ZYXM5T2Q3X4KDJPJ\", \"name\": \"John Doe\", \"phoneNumber\": \"010-1234-5678\", \"dateOfBirth\": \"1990-01-01\" , \"genderType\": \"MALE\", \"healthInsuranceType\": \"HEALTH_INSURANCE\"}";

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

        Page<Counselee> mockPage = new PageImpl<>(CounseleeFixture.createList(2),
            PageRequest.of(0, 10), 2);

        PageRes<SelectCounseleeRes> mockPageRes = new PageRes<>(mockPage).map(SelectCounseleeRes::from);
        when(counseleeService.selectCounselees(
            any(),
            isNull(),
            isNull(),
            isNull())).thenReturn(mockPageRes);

        mockMvc.perform(get("/v1/counsel/counselee/")
                .param("page", "0")
                .param("size", "10")
                .header("Authorization", "Bearer token"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.totalPages").value(1))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.hasNext").value(false))
            .andExpect(jsonPath("$.hasPrevious").value(false));
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
