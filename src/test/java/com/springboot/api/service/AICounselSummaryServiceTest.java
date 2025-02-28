package com.springboot.api.service;

import static com.springboot.enums.AICounselSummaryStatus.STT_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.domain.AICounselSummary;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.dto.aiCounselSummary.AnalyseTextReq;
import com.springboot.api.dto.aiCounselSummary.ConvertSpeechToTextReq;
import com.springboot.api.dto.aiCounselSummary.DeleteAICounselSummaryReq;
import com.springboot.api.dto.aiCounselSummary.SelectSpeakerListRes;
import com.springboot.api.dto.naverClova.SegmentDTO;
import com.springboot.api.dto.naverClova.SpeechToTextRes;
import com.springboot.api.repository.AICounselSummaryRepository;
import com.springboot.api.repository.CounselSessionRepository;

@SpringBootTest
@EnableAsync
@Disabled
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // 비동기 테스트 위해 필요
public class AICounselSummaryServiceTest {

        private static final Logger log = LoggerFactory.getLogger(AICounselSummaryServiceTest.class);
        @Autowired
        private AICounselSummaryService aiCounselSummaryService; // 실제 서비스 주입

        @MockBean
        private CounselSessionRepository counselSessionRepository;

        @MockBean
        private AICounselSummaryRepository aiCounselSummaryRepository;

        @ParameterizedTest
        @Transactional
        @ValueSource(strings = { "test1.m4a", "test2.m4a", "test3.m4a", "test4.m4a" })
        public void testConvertSpeechToText(String filename) throws Exception {

                String testCounselSessionId = "TEST-COUNSEL-SESSION-01";
                CounselSession mockCounselSession = new CounselSession();
                mockCounselSession.setId(testCounselSessionId);

                AICounselSummary mockAiCounselSummary = AICounselSummary.builder()
                                .counselSession(mockCounselSession)
                                .aiCounselSummaryStatus(STT_PROGRESS)
                                .build();

                when(counselSessionRepository.findById(testCounselSessionId))
                                .thenReturn(Optional.of(mockCounselSession));
                when(aiCounselSummaryRepository.save(any(AICounselSummary.class))).thenReturn(mockAiCounselSummary);

                // ✅ 3. Mock 파일 생성 (MultipartFile)
                File mp4File = ResourceUtils.getFile("classpath:" + "stt/audio/" + filename);
                FileInputStream inputStream = new FileInputStream(mp4File);

                MultipartFile file = new MockMultipartFile(
                                "media",
                                mp4File.getName(),
                                "audio/m4a",
                                inputStream);

                ConvertSpeechToTextReq request = ConvertSpeechToTextReq.builder()
                                .counselSessionId(testCounselSessionId)
                                .build();

                // ✅ 4. 메서드 실행
                aiCounselSummaryService.convertSpeechToText(file, request);

                // ✅ 5. Assertions
                // AICounselSummary 저장 확인 (STT_PROGRESS)
                ArgumentCaptor<AICounselSummary> captor = ArgumentCaptor.forClass(AICounselSummary.class);
                verify(aiCounselSummaryRepository, times(1)).save(captor.capture());
                assertEquals(STT_PROGRESS, captor.getValue().getAiCounselSummaryStatus());

                // ✅ 비동기 처리 후 결과 저장 확인 (STT_COMPLETE)
                Thread.sleep(2000); // 비동기 실행 대기
                verify(aiCounselSummaryRepository, atLeast(1)).save(any(AICounselSummary.class));

                // 로그 출력 (디버깅용)
                System.out.println("✅ AICounselSummary 상태: " + captor.getValue().getAiCounselSummaryStatus());
                System.out.println("✅ STT 결과: " + captor.getValue().getSttResult());
        }

        @ParameterizedTest
        @ValueSource(strings = { "test1.m4a.json", "test2.m4a.json", "test3.m4a.json", "test4.m4a.json" })
        public void selectSpeakerList(String filename) throws IOException {

                ObjectMapper objectMapper = new ObjectMapper();
                Resource resource = new ClassPathResource("stt/output/" + filename);
                SpeechToTextRes speechToTextRes = objectMapper.readValue(resource.getFile(), SpeechToTextRes.class);

                Map<String, String> longestTexts = speechToTextRes.segments().stream()
                                .collect(Collectors.toMap(
                                                // `name`을 키로 사용
                                                segmentDTO -> segmentDTO.speaker().name(),
                                                SegmentDTO::text,
                                                // 기존 값과 새로운 값 중 더 긴 문자열을 선택
                                                (existing, replacement) -> existing.length() >= replacement.length()
                                                                ? existing
                                                                : replacement));

                List<SelectSpeakerListRes> selectSpeakerListResList = longestTexts.entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .map(map -> new SelectSpeakerListRes(map.getKey(), map.getValue())).toList();

                log.debug(selectSpeakerListResList.toString());
        }

        @ParameterizedTest
        @ValueSource(strings = { "test1.m4a.json", "test2.m4a.json", "test3.m4a.json", "test4.m4a.json" })
        public void selectSpeakCount(String filename) throws IOException {

                ObjectMapper objectMapper = new ObjectMapper();
                Resource resource = new ClassPathResource("stt/output/" + filename);
                SpeechToTextRes speechToTextRes = objectMapper.readValue(resource.getFile(), SpeechToTextRes.class);

                Map<String, Long> speakerTextCount = speechToTextRes.segments().stream()
                                .collect(Collectors.groupingBy(segment -> segment.speaker().label(),
                                                Collectors.counting()));

                log.debug(speakerTextCount.toString());
        }

        @Test
        void testAnalyseText() throws JsonProcessingException {

                AnalyseTextReq analyseTextReq = AnalyseTextReq
                                .builder()
                                .counselSessionId("TEST-COUNSEL-SESSION-02")
                                .speakers(List.of("A", "E"))
                                .build();

                aiCounselSummaryService.analyseText(analyseTextReq);

        }

        @Test
        void deleteAISummary() {

                String testCounselSessionId = "TEST-COUNSEL-SESSION-02";
                CounselSession mockCounselSession = new CounselSession();
                mockCounselSession.setId(testCounselSessionId);

                when(counselSessionRepository.findById(testCounselSessionId))
                                .thenReturn(Optional.of(mockCounselSession));

                DeleteAICounselSummaryReq deleteAICounselSummaryReq = new DeleteAICounselSummaryReq(
                                testCounselSessionId);

                aiCounselSummaryService.deleteAICounselSummary(deleteAICounselSummaryReq);
        }

}