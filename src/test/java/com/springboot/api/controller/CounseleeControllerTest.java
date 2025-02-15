package com.springboot.api.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;

import com.springboot.api.dto.counselee.AddCounseleeReq;
import com.springboot.api.service.CounseleeService;
import com.springboot.api.repository.CounselorRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import com.springboot.api.dto.counselee.DeleteCounseleeBatchRes;
import com.springboot.api.dto.counselee.SelectCounseleeBaseInformationByCounseleeIdRes;
import com.springboot.api.dto.counselee.SelectCounseleeRes;
import com.springboot.api.dto.counselee.UpdateCounseleeReq;

@WebMvcTest(CounseleeController.class)
public class CounseleeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CounseleeService counseleeService;

    @MockBean
    private CounselorRepository counselorRepository;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    public void setUp() {
        when(userDetailsService.loadUserByUsername(any(String.class)))
                .thenReturn(new UserDetails() {
                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        return Collections.emptyList();
                    }

                    @Override
                    public String getPassword() {
                        return "password";
                    }

                    @Override
                    public String getUsername() {
                        return "sanghoon";
                    }

                });
        Map<String, Object> claims = new HashMap<>();
        claims.put("Granted Authorities", Collections.singletonList("ROLE_ADMIN"));

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claims(claimsMap -> claimsMap.putAll(claims))
                .build();
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);
    }

    @Test
    public void testAddCounseleeSuccess() throws Exception {

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

        String requestBody = "{ \"counseleeId\": \"123\", \"name\": \"John Doe\", \"phoneNumber\": \"010-1234-5678\", \"dateOfBirth\": \"1990-01-01\" }";

        when(counseleeService.updateCounselee(any(UpdateCounseleeReq.class))).thenReturn("Success");

        mockMvc.perform(put("/v1/counsel/counselee/")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    public void testSelectCounseleeSuccess() throws Exception {

        SelectCounseleeRes mockResponse = SelectCounseleeRes.builder()
                .id("123")
                .name("John Doe")
                .phoneNumber("010-1234-5678")
                .build();

        when(counseleeService.selectCounselee(anyString())).thenReturn(mockResponse);

        mockMvc.perform(get("/v1/counsel/counselee/{counseleeId}", "123")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("123"))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.phoneNumber").value("010-1234-5678"));
    }

    @Test
    public void testSelectCounseleeListSuccess() throws Exception {

        List<SelectCounseleeRes> mockList = Arrays.asList(
                SelectCounseleeRes.builder()
                        .id("123")
                        .name("John Doe")
                        .build(),
                SelectCounseleeRes.builder()
                        .id("456")
                        .name("Jane Doe")
                        .build());

        when(counseleeService.selectCounselees(anyInt(), anyInt())).thenReturn(mockList);

        mockMvc.perform(get("/v1/counsel/counselee/")
                .param("page", "0")
                .param("size", "10")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    public void testDeleteCounseleeSuccess() throws Exception {

        mockMvc.perform(delete("/v1/counsel/counselee/{counseleeId}", "123")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("delete the counselee success"));
    }

    @Test
    public void testDeleteCounseleeBatchSuccess() throws Exception {
        Map<String, Object> claims = new HashMap<>();
        claims.put("Granted Authorities", Collections.singletonList("ROLE_ADMIN"));

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claims(claimsMap -> claimsMap.putAll(claims))
                .build();
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);

        String requestBody = "[{\"counseleeId\": \"123\"}, {\"counseleeId\": \"456\"}]";

        List<DeleteCounseleeBatchRes> mockResponse = Arrays.asList(
                DeleteCounseleeBatchRes.builder()
                        .deletedCounseleeId("123")
                        .build(),
                DeleteCounseleeBatchRes.builder()
                        .deletedCounseleeId("456")
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
        Map<String, Object> claims = new HashMap<>();
        claims.put("Granted Authorities", Collections.singletonList("ROLE_ADMIN"));

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claims(claimsMap -> claimsMap.putAll(claims))
                .build();
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);

        SelectCounseleeBaseInformationByCounseleeIdRes mockResponse = SelectCounseleeBaseInformationByCounseleeIdRes
                .builder()
                .counseleeId("123")
                .name("John Doe")
                .build();

        when(counseleeService.selectCounseleeBaseInformation(anyString())).thenReturn(mockResponse);

        mockMvc.perform(get("/v1/counsel/counselee/{counselSessionId}/base/information", "123")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.counseleeId").value("123"))
                .andExpect(jsonPath("$.data.name").value("John Doe"));
    }
}
