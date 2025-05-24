package com.springboot.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.config.TestSecurityConfig;
import com.springboot.api.medication.controller.MedicationController;
import com.springboot.api.medication.model.SearchMedicationByKeywordRes;
import com.springboot.api.medication.service.MedicationService;
import com.springboot.api.role.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicationController.class)
@Import(TestSecurityConfig.class)
class MedicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicationService medicationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("키워드로 약물 검색 - 성공")
    @WithMockUser(roles = "USER")
    void searchMedicationByKeyword_Success() throws Exception {
        // given
        String keyword = "aspirin";
        List<SearchMedicationByKeywordRes> mockResponse = List.of(
                SearchMedicationByKeywordRes.builder().medicationId(1L).medicationName("Aspirin 100mg").manufacturer("Bayer").build(),
                SearchMedicationByKeywordRes.builder().medicationId(2L).medicationName("Aspirin 500mg").manufacturer("Bayer").build()
        );
        when(medicationService.searchMedicationByKeyword(keyword)).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/v1/medication")
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].medicationName").value("Aspirin 100mg"))
                .andExpect(jsonPath("$.data[1].medicationName").value("Aspirin 500mg"));
    }

    @Test
    @DisplayName("키워드로 약물 검색 - 결과 없음")
    @WithMockUser(roles = "ASSISTANT")
    void searchMedicationByKeyword_EmptyResult() throws Exception {
        // given
        String keyword = "unknown";
        when(medicationService.searchMedicationByKeyword(keyword)).thenReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/v1/medication")
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("키워드로 약물 검색 - 키워드 누락")
    @WithMockUser(roles = "ADMIN")
    void searchMedicationByKeyword_MissingKeyword() throws Exception {
        // when & then
        mockMvc.perform(get("/v1/medication") // No keyword parameter
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Spring's default for missing required param
    }

    @Test
    @DisplayName("키워드로 약물 검색 - 키워드 빈 문자열")
    @WithMockUser(roles = "USER")
    void searchMedicationByKeyword_EmptyKeyword() throws Exception {
        // given
        String keyword = "";
        // Assuming the service might return an empty list or the controller/validation handles it.
        // If service-level validation throws an exception, this test would need adjustment.
        when(medicationService.searchMedicationByKeyword(keyword)).thenReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/v1/medication")
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Or isBadRequest() if @RequestParam(required=true) and not blank/empty
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }
    
    @Test
    @DisplayName("키워드로 약물 검색 - 인증되지 않은 사용자")
    void searchMedicationByKeyword_Unauthenticated() throws Exception {
        // when & then
        mockMvc.perform(get("/v1/medication")
                        .param("keyword", "test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()); // RoleSecuredAspect usually leads to 403 Forbidden
    }
}
