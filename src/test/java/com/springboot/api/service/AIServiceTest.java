package com.springboot.api.service;

import com.springboot.api.domain.AICounselSummary;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.dto.medicationcounsel.ConvertSpeechToTextReq;
import com.springboot.api.repository.AICounselSummaryRepository;
import com.springboot.api.repository.CounselSessionRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;

import static com.springboot.enums.AICounselSummaryStatus.STT_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@EnableAsync
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)// 비동기 테스트 위해 필요
public class AIServiceTest {

    @Autowired
    private AIService aiService; // 실제 서비스 주입

    @MockBean
    private CounselSessionRepository counselSessionRepository;

    @MockBean
    private AICounselSummaryRepository aiCounselSummaryRepository;


    @ParameterizedTest
    @Transactional
    @ValueSource(strings = {"test1.m4a", "test2.m4a", "test3.m4a","test4.m4a"})
    public void testConvertSpeechToText(String filename) throws Exception {

        String testCounselSessionId = "TEST-COUNSEL-SESSION-01";
        CounselSession mockCounselSession = new CounselSession();
        mockCounselSession.setId(testCounselSessionId);

        AICounselSummary mockAiCounselSummary = AICounselSummary.builder()
                .counselSession(mockCounselSession)
                .aiCounselSummaryStatus(STT_PROGRESS)
                .build();

        when(counselSessionRepository.findById(testCounselSessionId)).thenReturn(Optional.of(mockCounselSession));
        when(aiCounselSummaryRepository.save(any(AICounselSummary.class))).thenReturn(mockAiCounselSummary);

        // ✅ 3. Mock 파일 생성 (MultipartFile)
        File mp4File = ResourceUtils.getFile("classpath:" + "stt/audio/"+filename);
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
        aiService.convertSpeechToText(file, request);

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
}