package com.springboot.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.common.dto.ByteArrayMultipartFile;
import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.domain.AICounselSummary;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.dto.aiCounselSummary.*;
import com.springboot.api.dto.naverClova.SegmentDTO;
import com.springboot.api.dto.naverClova.SpeechToTextReq;
import com.springboot.api.dto.naverClova.SpeechToTextRes;
import com.springboot.api.infra.external.NaverClovaExternalService;
import com.springboot.api.repository.AICounselSummaryRepository;
import com.springboot.api.repository.CounselSessionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    @Value("${naver.clova.api-key}")
    private String clovaApiKey;
    private final ChatModel chatModel;

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
        headers.put("X-CLOVASPEECH-API-KEY",clovaApiKey); //암호화 및 Property 추후에 뺄 예정

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

        counselSessionRepository.findById(counselSessionId)
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

        counselSessionRepository.findById(counselSessionId)
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

    public void analyseText(AnalyseTextReq analyseTextReq) throws JsonProcessingException {

        counselSessionRepository.findById(analyseTextReq.getCounselSessionId())
                .orElseThrow(IllegalArgumentException::new);

        AICounselSummary aiCounselSummary = aiCounselSummaryRepository.findByCounselSessionId(analyseTextReq.getCounselSessionId())
                .orElseThrow(NoContentException::new);

        JsonNode sttResult = Optional.ofNullable(aiCounselSummary.getSttResult())
                .orElseThrow(NoContentException::new);

        SpeechToTextRes speechToTextRes = objectMapper.treeToValue(sttResult,SpeechToTextRes.class);



        List<STTMessageForPromptDTO> sttMessages = speechToTextRes.segments()
                .stream()
                .filter(segmentDTO -> analyseTextReq.getSpeakers().contains(segmentDTO.speaker().name()))
                .map(segmentDTO -> STTMessageForPromptDTO
                        .builder()
                        .speaker(segmentDTO.speaker().name())
                        .text(segmentDTO.text())
                        .build())
                .toList();

        String sttMessagesJson = objectMapper.writeValueAsString(sttMessages);

        SystemMessage systemMessage = new SystemMessage("""
        복약상담 내용을 요약하는 시스템이야.
        내가 여러명의 대화를 Speech To Text 한 결과 json을 전달해줄테니
        내용을 요약해줘. 참고로 해당 요약정보를 보는 사람은 10년차 약사야.
        그리고 요약 정보는 아래 sample 과 동일한 양식의 markdown 으로 만들어줘
        """);

        SystemMessage  oneShotMessage = new SystemMessage("""
                        ## 👨‍⚕️ 약사
                                                
                        - 현재 복용 중인 약이 **뇌졸중 예방**에 중요하며, 지속적인 복용이 필요하다고 언급했어요.
                        - **당뇨약 복용 조정 가능성**에 대한 의견을 제시하고, 혈당이 잘 조절되고 있다고 안내했어요.
                        - **약 복용 유지 및 건강한 생활습관**을 위해 주의할 점을 강조했어요.
                                                
                        ### 📌 안내가 더 필요해요
                                                
                        - 해당 약을 복용해야 하는 이유
                                                
                        ### ✅ 다음 상담 때 체크하면 좋아요
                                                
                        - 최근 혈압
                        - 혈당 변화치
                        - 복용 약 변화
                                                
                        ---
                                                
                        ## 💊 내담자
                                                
                        - **혈압이 상황에 따라 변한다**고 언급했어요.
                        - 과거 **뇌경색 발병 경험**이 있다고 언급했으며, 현재는 큰 문제 없이 생활 중이에요.
                        - **약물을 계속 복용할 것**이라고 했어요.
                        - **당뇨약과 혈압약 복용 중**이며, **보건소에서 약 복용 여부에 대한 의견**을 들었다고 해요.
                        - 현재 혈압 수치(130~140)와 관련하여 **약을 줄일 수 있는지 질문**했어요.
                                                
                        ---
                                                
                        ## 📌 주요 키워드
                                                
                        - **혈압, 뇌경색, 당뇨**
                      
                        """
        );


        UserMessage userMessage = new UserMessage(sttMessagesJson);





        List<Message> messages =  List.of(systemMessage, oneShotMessage, userMessage);

        callGpt(messages)
                .thenAcceptAsync(
                        chatResponse -> {
                            aiCounselSummary.setTaResult(objectMapper.valueToTree(chatResponse));
                            aiCounselSummary.setAiCounselSummaryStatus(GPT_COMPLETE);
                            aiCounselSummaryRepository.save(aiCounselSummary);
                        }
                )
                .exceptionally(ex ->{
                            log.error("error",ex);
                            aiCounselSummary.setAiCounselSummaryStatus(GPT_FAILED);
                            aiCounselSummaryRepository.save(aiCounselSummary);
                            return null;
                        }
                );
    }

    @Async
    public CompletableFuture<ChatResponse> callGpt(List<Message> messages)  {
        ChatClient chatClient = ChatClient.builder(this.chatModel).build();
        Prompt prompt = new Prompt(messages);
        return CompletableFuture.supplyAsync(() -> chatClient.prompt(prompt)
                .call()
                .chatResponse());
    }

    public SelectAnalysedTextRes selectAnalysedText(String counselSessionId) throws JsonProcessingException {

        counselSessionRepository.findById(counselSessionId)
                .orElseThrow(IllegalArgumentException::new);

        AICounselSummary aiCounselSummary = aiCounselSummaryRepository.findByCounselSessionId(counselSessionId)
                .orElseThrow(NoContentException::new);

        JsonNode taResult = Optional.ofNullable(aiCounselSummary.getTaResult())
                .orElseThrow(NoContentException::new);

        String taResultText = taResult.get("result").get("output").get("text").asText();

        return new SelectAnalysedTextRes(taResultText);
    }

    @Transactional
    public void deleteAICounselSummary(DeleteAICounselSummaryReq deleteAICounselSummaryReq) {

        counselSessionRepository.findById(deleteAICounselSummaryReq.counselSessionId())
                .orElseThrow(IllegalArgumentException::new);

        aiCounselSummaryRepository.deleteByCounselSessionId(deleteAICounselSummaryReq.counselSessionId());

    }


}
