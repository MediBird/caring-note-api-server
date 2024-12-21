package com.springboot.api.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.Counselee;
import com.springboot.api.domain.Counselor;
import com.springboot.api.domain.MedicationCounsel;
import com.springboot.api.dto.medicationcounsel.AddMedicationCounselReq;
import com.springboot.api.dto.medicationcounsel.DeleteMedicationCounselReq;
import com.springboot.api.dto.medicationcounsel.UpdateMedicationCounselReq;
import com.springboot.api.repository.CounselSessionRepository;
import com.springboot.api.repository.MedicationCounselRepository;
import com.springboot.enums.CounselNeedStatus;
import com.springboot.enums.ScheduleStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class MedicationCounselControllerTest {

    private static final Logger log = LoggerFactory.getLogger(MedicationCounselControllerTest.class);
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CounselSessionRepository counselSessionRepository;

    @Autowired
    private MedicationCounselRepository medicationCounselRepository;

    @Autowired
    private EntityManager entityManager;

    private CounselSession testCounselSession;
    private MedicationCounsel testMedicationCounsel;

    @BeforeEach
    void setUp() {

        Counselee counselee = new Counselee();
        counselee.setId("test-counselee");
        counselee.setName("Test Counselee");
        counselee.setDateOfBirth(LocalDate.of(1995,7,19));
        counselee.setPhoneNumber("01090654118");

        entityManager.persist(counselee); // 영속 상태로 추가

        Counselor counselor = new Counselor();
        counselor.setEmail("roung4119@gmail.com");
        counselor.setName("Test Counselor");
        counselor.setPhoneNumber("01083432753");

        entityManager.persist(counselor); // 영속 상태로 추가

        // 2️⃣ CounselSession 등록 (Counselee, Counselor 관계 추가)
        CounselSession counselSession = CounselSession.builder()
                .status(ScheduleStatus.SCHEDULED)
                .counselee(counselee) // 실제 영속 상태의 엔티티 사용
                .counselor(counselor) // 실제 영속 상태의 엔티티 사용
                .scheduledStartDateTime(LocalDateTime.of(2024, 12, 10, 10, 0, 0))
                .build();
        testCounselSession = counselSessionRepository.save(counselSession); // 영속 상태로 추가
        entityManager.flush(); // flush를 통해 DB에 반영

        // 3️⃣ MedicationCounsel 등록 (CounselSession 관계 추가)
        MedicationCounsel medicationCounsel = MedicationCounsel.builder()
                .counselSession(counselSession) // 실제 영속 상태의 CounselSession 사용
                .counselNeedStatus(CounselNeedStatus.ONE)
                .counselRecord("I'm counselor")
                .counselRecordHighlights(List.of("I'm", "counselor"))
                .build();
        testMedicationCounsel = medicationCounselRepository.save(medicationCounsel);
        entityManager.flush(); // flush를 통해 DB에 반영
    }


    @Test
    @WithMockUser(username = "sunpil", roles = {"ADMIN"})
    void testAdd() throws Exception {

        //given

        AddMedicationCounselReq requestBody = AddMedicationCounselReq.builder()
                .counselSessionId(testCounselSession.getId())
                .counselRecord("I'm counselor")
                .counselNeedStatus(CounselNeedStatus.ONE)
                .counselRecordHighlights(List.of("I'm", "counselor"))
                .build();


        mockMvc.perform(post("/v1/counsel/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andDo(handler -> {
                    log.debug("Response Content: " + handler.getResponse().getContentAsString());
                });
    }

    @Test
    @WithMockUser(username = "sunpil", roles = {"ADMIN"})
    void testSelectByCounselSessionId() throws Exception{


        // Then
        mockMvc.perform(get("/v1/counsel/record")
                        .param("counselSessionId", testCounselSession.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.medicationCounselId").value(testMedicationCounsel.getId()))
                .andExpect(jsonPath("$.data.counselRecord").value(testMedicationCounsel.getCounselRecord()))
                .andExpect(jsonPath("$.data.counselRecordHighlights[0]").value(testMedicationCounsel.getCounselRecordHighlights().getFirst()))
                .andExpect(jsonPath("$.data.counselRecordHighlights[1]").value(testMedicationCounsel.getCounselRecordHighlights().get(1)))
                .andExpect(jsonPath("$.data.counselNeedStatus").value(testMedicationCounsel.getCounselNeedStatus().name()))
                .andDo(handler -> {
                    log.debug("Response Content: " + handler.getResponse().getContentAsString());
                });
    }


    @Test
    @WithMockUser(username = "sunpil", roles = {"ADMIN"})
    void testUpdate() throws Exception {

        UpdateMedicationCounselReq requestBody = UpdateMedicationCounselReq.builder()
                .medicationCounselId(testMedicationCounsel.getId())
                .counselRecord("I'm counselors")
                .counselNeedStatus(CounselNeedStatus.ONE)
                .counselRecordHighlights(List.of("I'm", "counselor"))
                .build();


        mockMvc.perform(put("/v1/counsel/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andDo(handler -> {
                    log.debug("Response Content: " + handler.getResponse().getContentAsString());
                });

        mockMvc.perform(get("/v1/counsel/record")
                        .param("counselSessionId", testCounselSession.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.medicationCounselId").value(testMedicationCounsel.getId()))
                .andExpect(jsonPath("$.data.counselRecord").value(requestBody.getCounselRecord()))
                .andExpect(jsonPath("$.data.counselRecordHighlights[0]").value(testMedicationCounsel.getCounselRecordHighlights().getFirst()))
                .andExpect(jsonPath("$.data.counselRecordHighlights[1]").value(testMedicationCounsel.getCounselRecordHighlights().get(1)))
                .andExpect(jsonPath("$.data.counselNeedStatus").value(testMedicationCounsel.getCounselNeedStatus().name()))
                .andDo(handler -> {
                    log.debug("Response Content: " + handler.getResponse().getContentAsString());
                });

    }

    @Test
    @WithMockUser(username = "sunpil", roles = {"ADMIN"})
    void testDelete() throws Exception{

        DeleteMedicationCounselReq requestBody = DeleteMedicationCounselReq.builder()
                .medicationCounselId(testMedicationCounsel.getId())
                .build();


        mockMvc.perform(delete("/v1/counsel/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andDo(handler -> {
                    log.debug("Response Content: " + handler.getResponse().getContentAsString());
                });
    }

}
