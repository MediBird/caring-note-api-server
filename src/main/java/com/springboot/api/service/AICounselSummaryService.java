package com.springboot.api.service;

import static com.springboot.enums.AICounselSummaryStatus.GPT_COMPLETE;
import static com.springboot.enums.AICounselSummaryStatus.GPT_FAILED;
import static com.springboot.enums.AICounselSummaryStatus.GPT_PROGRESS;
import static com.springboot.enums.AICounselSummaryStatus.STT_COMPLETE;
import static com.springboot.enums.AICounselSummaryStatus.STT_FAILED;
import static com.springboot.enums.AICounselSummaryStatus.STT_PROGRESS;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.common.dto.ByteArrayMultipartFile;
import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.common.properties.NaverClovaProperties;
import com.springboot.api.common.properties.SttFileProperties;
import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.common.util.FileUtil;
import com.springboot.api.domain.AICounselSummary;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.PromptTemplate;
import com.springboot.api.dto.aiCounselSummary.AnalyseTextReq;
import com.springboot.api.dto.aiCounselSummary.ConvertSpeechToTextReq;
import com.springboot.api.dto.aiCounselSummary.DeleteAICounselSummaryReq;
import com.springboot.api.dto.aiCounselSummary.STTMessageForPromptDTO;
import com.springboot.api.dto.aiCounselSummary.SelectAICounselSummaryPopUpRes;
import com.springboot.api.dto.aiCounselSummary.SelectAICounselSummaryStatusRes;
import com.springboot.api.dto.aiCounselSummary.SelectAnalysedTextRes;
import com.springboot.api.dto.aiCounselSummary.SelectSpeakerListRes;
import com.springboot.api.dto.aiCounselSummary.SelectSpeechToTextRes;
import com.springboot.api.dto.naverClova.SegmentDTO;
import com.springboot.api.dto.naverClova.SpeechToTextReq;
import com.springboot.api.dto.naverClova.SpeechToTextRes;
import com.springboot.api.infra.external.NaverClovaExternalService;
import com.springboot.api.repository.AICounselSummaryRepository;
import com.springboot.api.repository.CounselSessionRepository;
import com.springboot.api.repository.PromptTemplateRepository;
import com.springboot.enums.AICounselSummaryStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AICounselSummaryService {

        private static final Logger log = LoggerFactory.getLogger(AICounselSummaryService.class);
        private final AICounselSummaryRepository aiCounselSummaryRepository;
        private final CounselSessionRepository counselSessionRepository;
        private final ObjectMapper objectMapper;
        private final NaverClovaExternalService naverClovaExternalService;
        private final DateTimeUtil dateTimeUtil;
        private final NaverClovaProperties naverClovaProperties;
        private final ChatModel chatModel;
        private final SttFileProperties sttFileProperties;
        private final PromptTemplateRepository promptTemplateRepository;

        private final FileUtil fileUtil;

        public void convertSpeechToText(MultipartFile file, ConvertSpeechToTextReq convertSpeechToTextReq) {

                CounselSession counselSession = counselSessionRepository
                                .findById(convertSpeechToTextReq.getCounselSessionId())
                                .orElseThrow(IllegalArgumentException::new);

                AICounselSummary aiCounselSummary = aiCounselSummaryRepository
                                .findByCounselSessionId(convertSpeechToTextReq.getCounselSessionId())
                                .orElse(AICounselSummary
                                                .builder()
                                                .counselSession(counselSession)
                                                .build());

                aiCounselSummary.setAiCounselSummaryStatus(STT_PROGRESS);
                aiCounselSummary.setSpeakers(null);
                aiCounselSummary.setTaResult(null);
                aiCounselSummary.setSttResult(null);
                aiCounselSummaryRepository.save(aiCounselSummary);

                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("X-CLOVASPEECH-API-KEY", naverClovaProperties.getApiKey());

                SpeechToTextReq speechToTextReq = SpeechToTextReq
                                .builder()
                                .language("ko-KR")
                                .completion("sync")
                                .wordAlignment(true)
                                .fullText(true)
                                .build();

                callNaverClovaAsync(headers, file, speechToTextReq)
                                .thenAcceptAsync(speechToTextRes -> updateAiCounselSummaryStatus(aiCounselSummary,
                                                "COMPLETED".equals(speechToTextRes.result()) ? STT_COMPLETE
                                                                : STT_FAILED,
                                                objectMapper.valueToTree(speechToTextRes))

                                )
                                .exceptionally(ex -> {
                                        log.error("Speech-to-text processing error", ex);
                                        updateAiCounselSummaryStatus(aiCounselSummary, STT_FAILED, null);
                                        return null;
                                });

        }

        @Async
        public CompletableFuture<SpeechToTextRes> callNaverClovaAsync(Map<String, String> headers, MultipartFile file,
                        SpeechToTextReq request) {
                return CompletableFuture.supplyAsync(() -> {
                        try {
                                MultipartFile multipartFile;

                                if (Objects.requireNonNull(file.getContentType()).contains("webm")) {
                                        multipartFile = fileUtil.convertWebmToMp4(file, sttFileProperties.getOrigin(),
                                                        sttFileProperties.getCovert());
                                } else {
                                        byte[] fileBytes = file.getBytes();
                                        multipartFile = new ByteArrayMultipartFile(file.getName(),
                                                        file.getOriginalFilename(), file.getContentType(), fileBytes);
                                }
                                return naverClovaExternalService.convertSpeechToText(headers, multipartFile, request)
                                                .getBody();
                        } catch (IOException e) {
                                log.error("Error while reading file bytes", e);
                                throw new CompletionException(e);
                        }
                });
        }

        private void updateAiCounselSummaryStatus(AICounselSummary aiCounselSummary, AICounselSummaryStatus status,
                        JsonNode sttResult) {
                aiCounselSummary.setAiCounselSummaryStatus(status);
                aiCounselSummary.setSttResult(sttResult);
                aiCounselSummaryRepository.save(aiCounselSummary);
        }

        public List<SelectSpeakerListRes> selectSpeakerList(String counselSessionId) throws JsonProcessingException {

                counselSessionRepository.findById(counselSessionId)
                                .orElseThrow(IllegalArgumentException::new);

                AICounselSummary aiCounselSummary = aiCounselSummaryRepository.findByCounselSessionId(counselSessionId)
                                .orElseThrow(NoContentException::new);

                if (aiCounselSummary.getAiCounselSummaryStatus().equals(STT_PROGRESS)) {
                        throw new NoContentException();
                }

                SpeechToTextRes speechToTextRes = objectMapper.treeToValue(aiCounselSummary.getSttResult(),
                                SpeechToTextRes.class);

                Map<String, String> longestTexts = speechToTextRes.segments().stream()
                                .collect(Collectors.toMap(
                                                // `name`을 키로 사용
                                                segmentDTO -> segmentDTO.speaker().name(),
                                                SegmentDTO::text,
                                                // 기존 값과 새로운 값 중 더 긴 문자열을 선택
                                                (existing, replacement) -> existing.length() >= replacement.length()
                                                                ? existing
                                                                : replacement));

                return longestTexts.entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .map(map -> new SelectSpeakerListRes(map.getKey(), map.getValue())).toList();

        }

        public List<SelectSpeechToTextRes> selectSpeechToText(String counselSessionId) throws JsonProcessingException {

                counselSessionRepository.findById(counselSessionId)
                                .orElseThrow(IllegalArgumentException::new);

                AICounselSummary aiCounselSummary = aiCounselSummaryRepository.findByCounselSessionId(counselSessionId)
                                .orElseThrow(NoContentException::new);

                if (aiCounselSummary.getAiCounselSummaryStatus().equals(STT_PROGRESS)) {
                        throw new NoContentException();
                }

                List<String> speakers = aiCounselSummary.getSpeakers();

                SpeechToTextRes speechToTextRes = objectMapper.treeToValue(aiCounselSummary.getSttResult(),
                                SpeechToTextRes.class);

                return speechToTextRes.segments().stream()
                                .filter(segmentDTO -> speakers.contains(segmentDTO.speaker().name()))
                                .map(segmentDTO -> new SelectSpeechToTextRes(segmentDTO.speaker().name(),
                                                segmentDTO.text(), dateTimeUtil.msToHMS(segmentDTO.start()),
                                                dateTimeUtil.msToHMS(segmentDTO.end())))
                                .toList();

        }

        @Transactional
        public void analyseText(AnalyseTextReq analyseTextReq) throws JsonProcessingException {

                counselSessionRepository.findById(analyseTextReq.getCounselSessionId())
                                .orElseThrow(IllegalArgumentException::new);

                AICounselSummary aiCounselSummary = aiCounselSummaryRepository
                                .findByCounselSessionId(analyseTextReq.getCounselSessionId())
                                .orElseThrow(NoContentException::new);

                aiCounselSummary.setAiCounselSummaryStatus(GPT_PROGRESS);

                JsonNode sttResult = Optional.ofNullable(aiCounselSummary.getSttResult())
                                .orElseThrow(NoContentException::new);

                SpeechToTextRes speechToTextRes = objectMapper.treeToValue(sttResult, SpeechToTextRes.class);

                List<STTMessageForPromptDTO> sttMessages = speechToTextRes.segments()
                                .stream()
                                .filter(segmentDTO -> analyseTextReq.getSpeakers()
                                                .contains(segmentDTO.speaker().name()))
                                .map(segmentDTO -> STTMessageForPromptDTO
                                                .builder()
                                                .speaker(segmentDTO.speaker().name())
                                                .text(segmentDTO.text())
                                                .build())
                                .toList();

                String sttMessagesJson = objectMapper.writeValueAsString(sttMessages);

                PromptTemplate promptTemplate = promptTemplateRepository.findById("ta_prompt")
                                .orElseThrow(NoContentException::new);

                callGpt(promptTemplate.generatePromptMessages(new UserMessage(sttMessagesJson)))
                                .thenAcceptAsync(
                                                chatResponse -> {
                                                        aiCounselSummary.setSpeakers(analyseTextReq.getSpeakers());
                                                        aiCounselSummary.setTaResult(
                                                                        objectMapper.valueToTree(chatResponse));
                                                        aiCounselSummary.setAiCounselSummaryStatus(GPT_COMPLETE);
                                                        aiCounselSummaryRepository.save(aiCounselSummary);
                                                })
                                .exceptionally(ex -> {
                                        log.error("error", ex);
                                        aiCounselSummary.setSpeakers(analyseTextReq.getSpeakers());
                                        aiCounselSummary.setAiCounselSummaryStatus(GPT_FAILED);
                                        aiCounselSummaryRepository.save(aiCounselSummary);
                                        return null;
                                });
        }

        @Async
        public CompletableFuture<ChatResponse> callGpt(List<Message> messages) {
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

        public SelectAICounselSummaryPopUpRes selectAICounselSummaryPopUp(String counselSessionId,
                        LocalDate localDate) {

                CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
                                .orElseThrow(IllegalArgumentException::new);

                Optional<AICounselSummary> aiCounselSummary = aiCounselSummaryRepository
                                .findByCounselSessionId(counselSessionId);

                LocalDate currentDate = Optional.ofNullable(localDate)
                                .orElse(LocalDate.now());

                boolean isPopup = aiCounselSummary.isEmpty()
                                && counselSession.getScheduledStartDateTime().toLocalDate().isEqual(currentDate);

                return new SelectAICounselSummaryPopUpRes(isPopup);

        }

        public SelectAICounselSummaryStatusRes selectAICounselSummaryStatus(String counselSessionId) {

                counselSessionRepository.findById(counselSessionId)
                                .orElseThrow(IllegalArgumentException::new);

                AICounselSummary aiCounselSummary = aiCounselSummaryRepository.findByCounselSessionId(counselSessionId)
                                .orElseThrow(NoContentException::new);

                return new SelectAICounselSummaryStatusRes(aiCounselSummary.getAiCounselSummaryStatus());
        }

}
