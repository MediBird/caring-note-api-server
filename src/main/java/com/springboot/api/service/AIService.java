package com.springboot.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.domain.AICounselSummary;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.dto.medicationcounsel.ConvertSpeechToTextReq;
import com.springboot.api.dto.naverClova.SpeechToTextReq;
import com.springboot.api.dto.naverClova.SpeechToTextRes;
import com.springboot.api.infra.external.NaverClovaExternalService;
import com.springboot.api.repository.AICounselSummaryRepository;
import com.springboot.api.repository.CounselSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.springboot.enums.AICounselSummaryStatus.STT_COMPLETE;
import static com.springboot.enums.AICounselSummaryStatus.STT_PROGRESS;

@Service
@RequiredArgsConstructor
public class AIService {

    private final AICounselSummaryRepository aiCounselSummaryRepository;
    private final CounselSessionRepository counselSessionRepository;
    private final ObjectMapper objectMapper;
    private final NaverClovaExternalService naverClovaExternalService;

    public void convertSpeechToText(MultipartFile file, ConvertSpeechToTextReq convertSpeechToTextReq){

        CounselSession counselSession = counselSessionRepository.findById(convertSpeechToTextReq.getCounselSessionId())
                .orElseThrow(IllegalArgumentException::new);

        AICounselSummary aiCounselSummary = AICounselSummary
                .builder()
                .counselSession(counselSession)
                .aiCounselSummaryStatus(STT_PROGRESS)
                .build();

        aiCounselSummaryRepository.save(aiCounselSummary);


        Map<String, String> headers = new HashMap<>();
        headers.put("Accept","application/json");
        headers.put("X-CLOVASPEECH-API-KEY","6c294a231c7d42989a5ef003fd09c3d4");

        SpeechToTextReq speechToTextReq = SpeechToTextReq
                .builder()
                .language("ko-KR")
                .completion("sync")
                .wordAlignment(true)
                .fullText(true)
                .build();

        CompletableFuture<SpeechToTextRes> resCompletableFuture = callNaverClovaAsync(headers, file, speechToTextReq);

        resCompletableFuture.thenAcceptAsync(speechToTextRes -> {
            aiCounselSummary.setSttResult(objectMapper.valueToTree(speechToTextRes));
            aiCounselSummary.setAiCounselSummaryStatus(STT_COMPLETE);
            aiCounselSummaryRepository.save(aiCounselSummary);
        });

    }

    @Async
    public CompletableFuture<SpeechToTextRes> callNaverClovaAsync(Map<String, String> headers, MultipartFile file, SpeechToTextReq request) {


        SpeechToTextRes response = naverClovaExternalService.convertSpeechToText(headers, file, request).getBody();
        return CompletableFuture.completedFuture(response);
    }


}
