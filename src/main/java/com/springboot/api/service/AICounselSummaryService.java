package com.springboot.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.common.dto.ByteArrayMultipartFile;
import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.common.util.FileUtil;
import com.springboot.api.domain.AICounselSummary;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.dto.aiCounselSummary.SelectSpeakerListRes;
import com.springboot.api.dto.aiCounselSummary.SelectSpeechToTextRes;
import com.springboot.api.dto.medicationcounsel.ConvertSpeechToTextReq;
import com.springboot.api.dto.naverClova.SegmentDTO;
import com.springboot.api.dto.naverClova.SpeechToTextReq;
import com.springboot.api.dto.naverClova.SpeechToTextRes;
import com.springboot.api.infra.external.NaverClovaExternalService;
import com.springboot.api.repository.AICounselSummaryRepository;
import com.springboot.api.repository.CounselSessionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.springboot.enums.AICounselSummaryStatus.*;

@Service
@RequiredArgsConstructor
public class AICounselSummaryService {

    private static final Logger log = LoggerFactory.getLogger(AICounselSummaryService.class);
    private final AICounselSummaryRepository aiCounselSummaryRepository;
    private final CounselSessionRepository counselSessionRepository;
    private final ObjectMapper objectMapper;
    private final NaverClovaExternalService naverClovaExternalService;
    private final DateTimeUtil dateTimeUtil;
    private final FileUtil fileUtil;

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
        headers.put("X-CLOVASPEECH-API-KEY","6c294a231c7d42989a5ef003fd09c3d4"); //암호화 및 Property 추후에 뺄 예정

        SpeechToTextReq speechToTextReq = SpeechToTextReq
                .builder()
                .language("ko-KR")
                .completion("sync")
                .wordAlignment(true)
                .fullText(true)
                .build();

        callNaverClovaAsync(headers, file, speechToTextReq)
                .thenAcceptAsync(
                        speechToTextRes -> {
                            aiCounselSummary.setSttResult(objectMapper.valueToTree(speechToTextRes));
                            aiCounselSummary.setAiCounselSummaryStatus(STT_COMPLETE);
                            aiCounselSummaryRepository.save(aiCounselSummary);
                        }
                )
                .exceptionally(ex ->{
                            log.error("error",ex);
                            aiCounselSummary.setAiCounselSummaryStatus(STT_FAILED);
                            aiCounselSummaryRepository.save(aiCounselSummary);
                            return null;
                        }
                );

    }

    @Async
    public CompletableFuture<SpeechToTextRes> callNaverClovaAsync(Map<String, String> headers, MultipartFile file, SpeechToTextReq request)  {

        try {

            byte[] fileBytes = file.getBytes();
            MultipartFile newMultipartFile = new ByteArrayMultipartFile(file.getName(), file.getOriginalFilename(), file.getContentType(), fileBytes);

            return CompletableFuture.supplyAsync(() -> naverClovaExternalService.convertSpeechToText(headers, newMultipartFile, request).getBody());

        } catch (IOException e) {
            log.error("Error while reading file bytes", e);
            return CompletableFuture.failedFuture(e);
        }

    }

    public List<SelectSpeakerListRes> selectSpeakerList(String counselSessionId) throws JsonProcessingException {

        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
                .orElseThrow(IllegalArgumentException::new);

        AICounselSummary aiCounselSummary = aiCounselSummaryRepository.findByCounselSessionId(counselSessionId)
                .orElseThrow(NoContentException::new);

        if(aiCounselSummary.getAiCounselSummaryStatus().equals(STT_PROGRESS)){
            throw new NoContentException();
        }

        SpeechToTextRes speechToTextRes = objectMapper.treeToValue(aiCounselSummary.getSttResult(),SpeechToTextRes.class);

        Map<String, String> longestTexts = speechToTextRes.segments().stream()
                .collect(Collectors.toMap(
                        // `name`을 키로 사용
                        segmentDTO -> segmentDTO.speaker().name(),
                        SegmentDTO::text,
                        // 기존 값과 새로운 값 중 더 긴 문자열을 선택
                        (existing, replacement) -> existing.length() >= replacement.length() ? existing : replacement
                ));

        return   longestTexts.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(map -> new SelectSpeakerListRes(map.getKey(), map.getValue())).toList();

    }

    public List<SelectSpeechToTextRes> selectSpeechToText(String counselSessionId) throws JsonProcessingException {

        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
                .orElseThrow(IllegalArgumentException::new);

        AICounselSummary aiCounselSummary = aiCounselSummaryRepository.findByCounselSessionId(counselSessionId)
                .orElseThrow(NoContentException::new);

        if(aiCounselSummary.getAiCounselSummaryStatus().equals(STT_PROGRESS)){
            throw new NoContentException();
        }

        List<String> speakers = aiCounselSummary.getSpeakers();

        SpeechToTextRes speechToTextRes = objectMapper.treeToValue(aiCounselSummary.getSttResult(),SpeechToTextRes.class);

        return  speechToTextRes.segments().stream()
                .filter(segmentDTO -> speakers.contains(segmentDTO.speaker().name()))
                .map(segmentDTO -> new SelectSpeechToTextRes(segmentDTO.speaker().name()
                        ,segmentDTO.text()
                        ,dateTimeUtil.msToHMS(segmentDTO.start())
                        ,dateTimeUtil.msToHMS(segmentDTO.end())
                )).toList();



    }


}
