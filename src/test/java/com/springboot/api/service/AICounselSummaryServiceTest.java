package com.springboot.api.service;

import static com.springboot.api.counselsession.enums.AICounselSummaryStatus.STT_COMPLETE;
import static com.springboot.api.counselsession.enums.AICounselSummaryStatus.STT_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.counselsession.dto.aiCounselSummary.*;
import com.springboot.api.counselsession.dto.naverClova.SpeechToTextRes;
import com.springboot.api.counselsession.entity.AICounselSummary;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.api.counselsession.repository.AICounselSummaryRepository;
import com.springboot.api.counselsession.repository.CounselSessionRepository;
import com.springboot.api.counselsession.service.AICounselSummaryService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
        @ValueSource(strings = { "test1.m4a.json" })
        public void selectSpeakerList(String filename) throws IOException {

                ObjectMapper objectMapper = new ObjectMapper();
                Resource resource = new ClassPathResource("stt/output/" + filename);
                SpeechToTextRes speechToTextRes = objectMapper.readValue(resource.getFile(), SpeechToTextRes.class);

                HashMap<String, SpeakerStatsDTO> speakerMap = new HashMap<>();

                speechToTextRes.speakers()
                        .forEach(speaker ->  speakerMap
                                .putIfAbsent(speaker.name()
                                        , new SpeakerStatsDTO()
                                )
                        );

                AtomicInteger totalSpeakCount = new AtomicInteger();

                speechToTextRes.segments().forEach(segment -> {
                      SpeakerStatsDTO speakerStats = speakerMap.get(segment.speaker().name());
                      speakerStats.updateSpeakerStats(segment.text());
                      totalSpeakCount.getAndIncrement();
                });
                
                log.info("speakerTextMap: " + speakerMap);

                List<SelectSpeakerListRes> selectSpeakerListResList = speakerMap
                        .entrySet()
                        .stream()
                        .filter(entry ->  entry.getValue().isValidSpeaker())
                        .sorted(Comparator.comparing((Map.Entry<String, SpeakerStatsDTO> entry)
                                -> entry.getValue().getSpeakCount()).reversed())
                        .map(map -> SelectSpeakerListRes.of(map.getKey(), map.getValue(), totalSpeakCount.get()))
                        .toList();


                log.info("selectSpeakerListResList: " + selectSpeakerListResList);

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

        @Test
        public void testSelectSpeakerList() throws Exception {
                String testCounselSessionId = "TEST-COUNSEL-SESSION-01";

                CounselSession mockCounselSession = new CounselSession();
                mockCounselSession.setId(testCounselSessionId);

                //A는 발화 수 >= 10, Max Length >= 5
                //B는 발화 수 >= 10, Max Length < 5
                //C는 발화 수 < 10, Max Length >= 5
                AICounselSummary mockAiCounselSummary = AICounselSummary.builder()
                                .counselSession(mockCounselSession)
                                .aiCounselSummaryStatus(STT_COMPLETE)
                                .sttResult(new ObjectMapper().readTree("""
                                        {
                                            "segments": [
                                                { "speaker": { "name": "A" }, "text": "Hello there!", "start": 0, "end": 1 },
                                                { "speaker": { "name": "A" }, "text": "Hello there!", "start": 0, "end": 1 },
                                                { "speaker": { "name": "A" }, "text": "Hello there!", "start": 0, "end": 1 },
                                                { "speaker": { "name": "A" }, "text": "Hello there!", "start": 0, "end": 1 },
                                                { "speaker": { "name": "A" }, "text": "Hello there!", "start": 0, "end": 1 },
                                                { "speaker": { "name": "A" }, "text": "Hello there!", "start": 0, "end": 1 },
                                                { "speaker": { "name": "A" }, "text": "Hello there!", "start": 0, "end": 1 },
                                                { "speaker": { "name": "A" }, "text": "Hello there!", "start": 0, "end": 1 },
                                                { "speaker": { "name": "A" }, "text": "Hello there!", "start": 0, "end": 1 },
                                                { "speaker": { "name": "A" }, "text": "Hello there!", "start": 0, "end": 1 },
                                                { "speaker": { "name": "B" }, "text": "Hi!", "start": 2, "end": 3 },
                                                { "speaker": { "name": "B" }, "text": "Hi!", "start": 2, "end": 3 },
                                                { "speaker": { "name": "A" }, "text": "How are you?", "start": 4, "end": 5 },
                                                { "speaker": { "name": "A" }, "text": "This is a longer statement from A.", "start": 6, "end": 7 },
                                                { "speaker": { "name": "B" }, "text": "Great!", "start": 8, "end": 9 },
                                                { "speaker": { "name": "B" }, "text": "Great!", "start": 8, "end": 9 },
                                                { "speaker": { "name": "B" }, "text": "Great!", "start": 8, "end": 9 },
                                                { "speaker": { "name": "B" }, "text": "Great!", "start": 8, "end": 9 },
                                                { "speaker": { "name": "B" }, "text": "Great!", "start": 8, "end": 9 },
                                                { "speaker": { "name": "B" }, "text": "Great!", "start": 8, "end": 9 },
                                                { "speaker": { "name": "B" }, "text": "Great!", "start": 8, "end": 9 },
                                                { "speaker": { "name": "B" }, "text": "Great!", "start": 8, "end": 9 },
                                                { "speaker": { "name": "B" }, "text": "Great!", "start": 8, "end": 9 },
                                                { "speaker": { "name": "C" }, "text": "Great! Great! Great! Great! Great! ", "start": 8, "end": 9 },
                                                { "speaker": { "name": "C" }, "text": "Great! Great! Great! Great! Great! ", "start": 8, "end": 9 },
                                                { "speaker": { "name": "C" }, "text": "Great! Great! Great! Great! Great! ", "start": 8, "end": 9 },
                                                { "speaker": { "name": "C" }, "text": "Great! Great! Great! Great! Great! ", "start": 8, "end": 9 },
                                                { "speaker": { "name": "C" }, "text": "Great! Great! Great! Great! Great! ", "start": 8, "end": 9 },
                                                { "speaker": { "name": "C" }, "text": "Great! Great! Great! Great! Great! ", "start": 8, "end": 9 },
                                                { "speaker": { "name": "C" }, "text": "Great! Great! Great! Great! Great! ", "start": 8, "end": 9 },
                                                { "speaker": { "name": "C" }, "text": "Great! Great! Great! Great! Great! ", "start": 8, "end": 9 },
                                                { "speaker": { "name": "C" }, "text": "Great! Great! Great! Great! Great! ", "start": 8, "end": 9 }
                                            ],
                                           "speakers" : [ {
                                                            "label" : "1",
                                                            "name" : "A",
                                                            "edited" : false
                                                            },
                                                           {
                                                            "label" : "2",
                                                            "name" : "B",
                                                            "edited" : false
                                                            },
                                                           {
                                                            "label" : "3",
                                                            "name" : "C",
                                                            "edited" : false
                                                            } ]
                                        }
                                """))
                                .build();

                when(counselSessionRepository.findById(testCounselSessionId)).thenReturn(Optional.of(mockCounselSession));
                when(aiCounselSummaryRepository.findByCounselSessionId(testCounselSessionId))
                                .thenReturn(Optional.of(mockAiCounselSummary));

                List<SelectSpeakerListRes> result = aiCounselSummaryService.selectSpeakerList(testCounselSessionId);

                assertEquals(2, result.size());
        }
}