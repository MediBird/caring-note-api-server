package com.springboot.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.dto.counselor.AddCounselorReq;
import com.springboot.api.dto.counselor.LoginCounselorReq;
import com.springboot.enums.RoleType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CounselorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;



    @Test
    @Transactional
    void shouldAddCounselorAndLoginSuccessfully() throws Exception {
        // Given: 요청 데이터 준비
        AddCounselorReq request = AddCounselorReq.builder()
                .name("sunpil")
                .email("roung4119@gmail.com")
                .phoneNumber("01090654118")
                .password("12341234")
                .roleType(RoleType.ADMIN)
                .build();

        LoginCounselorReq loginRequest = LoginCounselorReq.builder()
                .email("roung4119@gmail.com")
                .password("12341234")
                .build();

        // When: 요청 수행
        mockMvc.perform(post("/api/v1/counselor/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then: 응답 검증
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, startsWith("Bearer ")));


        mockMvc.perform(post("/api/v1/counselor/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                // Then: 응답 검증
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, startsWith("Bearer ")));
    }

}