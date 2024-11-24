package com.springboot.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.dto.counselor.LoginCounselorReq;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CounselorSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeAll
    void loginAndGetToken() throws Exception {
        // 로그인 요청 데이터 준비
        LoginCounselorReq loginRequest = LoginCounselorReq.builder()
                .email("roung4119@gmail.com")
                .password("12341234")
                .build();

        // 로그인 요청 및 헤더에서 토큰 추출
        MvcResult result = mockMvc.perform(post("/api/v1/counselor/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        token = result.getResponse().getHeader("Authorization");

        // 토큰 검증
        assertThat(token).isNotBlank();
    }

    @Transactional
    @Test
    void shouldAddCounselSessionSuccessfully() throws Exception {
        // Given: 요청 데이터 준비
        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("counseleeId", "123");
        requestBody.put("scheduledStartDateTime", "2024-11-25 17:30");
        requestBody.put("status", "SCHEDULED");

        // POST 요청 실행
         mockMvc.perform(post("/api/v1/counsel/session")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andDo(handler ->{
                    System.out.println(handler.getResponse().getContentAsString());
                });

    }

}
