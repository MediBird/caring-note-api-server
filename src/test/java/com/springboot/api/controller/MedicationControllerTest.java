package com.springboot.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.dto.medication.SearchMedicationByKeywordRes;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql("/sql/test-medication.sql")
public class MedicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "sanghoon", roles = {"ADMIN"})
    void testSearchMedication() throws Exception {
        String keyword = "ㅈㄴㄹㅇ";
        String itemName = "제니로우캡슐120밀리그램(오르리스타트)";
        SearchMedicationByKeywordRes searchMedicationByKeywordRes = SearchMedicationByKeywordRes.builder()
            .itemName(itemName)
            .id("AAAA26XK48EDA")
            .itemImage("https://nedrug.mfds.go.kr/pbp/cmn/itemImageDownload/1PEPKBUcgZk")
            .build();

        mockMvc.perform(get("/v1/medication?keyword=" + keyword)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(handler -> {
                    String responseContent = handler.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    System.out.println(responseContent);
                    JsonNode root = objectMapper.readTree(responseContent);
                    List<SearchMedicationByKeywordRes> responseDTOs = objectMapper.convertValue(
                        root.get("data"), 
                        new TypeReference<List<SearchMedicationByKeywordRes>>() {}
                    );
                    assertEquals(searchMedicationByKeywordRes, responseDTOs.get(0));
                });
    }
}
