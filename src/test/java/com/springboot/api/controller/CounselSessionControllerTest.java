package com.springboot.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.dto.counselsession.AddCounselSessionReq;
import com.springboot.api.dto.counselsession.AddCounselSessionRes;
import com.springboot.api.dto.counselsession.SelectCounselSessionListByBaseDateAndCursorAndSizeRes;
import com.springboot.api.dto.counselsession.SelectCounselSessionListItem;
import com.springboot.api.service.CounselSessionService;
import com.springboot.enums.CardRecordStatus;
import com.springboot.enums.ScheduleStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        when(counselSessionService.addCounselSession(any(AddCounselSessionReq.class))).thenReturn(addCounselSessionRes); // POST 요청 실행
        mockMvc.perform(post("/v1/counsel/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andDo(handler -> {
                    System.out.println(handler.getResponse().getContentAsString());
                });
    }


    @Test
    @WithMockUser(username = "sanghoon", roles = {"ADMIN"})
    void shouldSelectCounselSessionListByBaseDateAndCursorAndSizeSuccessfully() throws Exception {

        SelectCounselSessionListItem selectCounselSessionListItem = new SelectCounselSessionListItem(
                "CS12345",          // counselSessionId
                        "14:30",            // scheduledTime
                        "2024-11-23",       // scheduledDate
                        "CSE123",           // counseleeId
                        "John Doe",         // counseleeName
                        "CNR123",           // counselorId
                        "Jane Smith",       // counselorName
                        ScheduleStatus.SCHEDULED,  // status
                        CardRecordStatus.UNRECORDED, // cardRecordStatus
                        true                // isCounselorAssign
                );

        List<SelectCounselSessionListItem> sessionListItems = List.of(selectCounselSessionListItem);

        SelectCounselSessionListByBaseDateAndCursorAndSizeRes selectCounselSessionListByBaseDateAndCursorAndSizeRes
                = new SelectCounselSessionListByBaseDateAndCursorAndSizeRes(sessionListItems,null,false);


        when(counselSessionService.selectCounselSessionListByBaseDateAndCursorAndSize(any()))
                .thenReturn(selectCounselSessionListByBaseDateAndCursorAndSizeRes);


        mockMvc.perform(get("/v1/counsel/session/list")
                        .param("baseDate","2024-11-23")
                        .param("size" , "1")
                )
                .andExpect(status().isOk())
                .andDo(handler -> {
                    System.out.println(handler.getResponse().getContentAsString());
                });
    }
}
