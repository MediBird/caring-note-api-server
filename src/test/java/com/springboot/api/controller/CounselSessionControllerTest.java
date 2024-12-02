package com.springboot.api.controller;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.dto.counselsession.AddCounselSessionReq;
import com.springboot.api.dto.counselsession.AddCounselSessionRes;
import com.springboot.api.service.CounselSessionService;
import com.springboot.enums.ScheduleStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CounselSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CounselSessionService counselSessionService;

    @Test
    @WithMockUser(username = "sanghoon", roles = {"ADMIN"})
    void shouldAddCounselSessionSuccessfully() throws Exception {
        AddCounselSessionReq requestBody = AddCounselSessionReq.builder()
                .counseleeId("01HXBK6P4NCZXCSNAG3FY7YZDT")
                .counselorId("01JE435Z6MR8G2KAT6DCF8KR7N")
                .scheduledStartDateTime("2024-11-23 14:30")
                .status(ScheduleStatus.SCHEDULED)
                .build();
        AddCounselSessionRes addCounselSessionRes = new AddCounselSessionRes("01JE43K3J5NQ50DVS6CGZFGN8S");
        when(counselSessionService.addCounselSession(anyString(), any(AddCounselSessionReq.class))).thenReturn(addCounselSessionRes); // POST 요청 실행
        mockMvc.perform(post("/v1/counsel/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andDo(handler -> {
                    System.out.println(handler.getResponse().getContentAsString());
                });
    }
}
