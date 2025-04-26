package com.springboot.api.counselsession.service;

import static com.springboot.api.counselsession.enums.AICounselSummaryStatus.GPT_COMPLETE;
import static com.springboot.api.counselsession.enums.AICounselSummaryStatus.GPT_FAILED;
import static com.springboot.api.counselsession.enums.AICounselSummaryStatus.GPT_PROGRESS;
import static com.springboot.api.counselsession.enums.AICounselSummaryStatus.STT_COMPLETE;
import static com.springboot.api.counselsession.enums.AICounselSummaryStatus.STT_FAILED;
import static com.springboot.api.counselsession.enums.AICounselSummaryStatus.STT_PROGRESS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.common.properties.NaverClovaProperties;
import com.springboot.api.common.properties.SttFileProperties;
import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.common.util.FileUtil;
import com.springboot.api.counselsession.dto.aiCounselSummary.ConvertSpeechToTextReq;
import com.springboot.api.counselsession.dto.aiCounselSummary.DeleteAICounselSummaryReq;
import com.springboot.api.counselsession.dto.aiCounselSummary.STTMessageForPromptDTO;
import com.springboot.api.counselsession.dto.aiCounselSummary.SelectAICounselSummaryPopUpRes;
import com.springboot.api.counselsession.dto.aiCounselSummary.SelectAICounselSummaryStatusRes;
import com.springboot.api.counselsession.dto.aiCounselSummary.SelectAnalysedTextRes;
import com.springboot.api.counselsession.dto.aiCounselSummary.SelectSpeakerListRes;
import com.springboot.api.counselsession.dto.aiCounselSummary.SelectSpeechToTextRes;
import com.springboot.api.counselsession.dto.aiCounselSummary.SpeakerStatsDTO;
import com.springboot.api.counselsession.dto.naverClova.DiarizationDTO;
import com.springboot.api.counselsession.dto.naverClova.SpeechToTextReq;
import com.springboot.api.counselsession.dto.naverClova.SpeechToTextRes;
import com.springboot.api.counselsession.entity.AICounselSummary;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.api.counselsession.entity.PromptTemplate;
import com.springboot.api.counselsession.enums.AICounselSummaryStatus;
import com.springboot.api.counselsession.repository.AICounselSummaryRepository;
import com.springboot.api.counselsession.repository.CounselSessionRepository;
import com.springboot.api.counselsession.repository.PromptTemplateRepository;
import com.springboot.api.counselsession.service.eventlistener.STTCompleteEvent;
import com.springboot.api.infra.external.NaverClovaExternalService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final ApplicationEventPublisher applicationEventPublisher;

    public void convertSpeechToText(MultipartFile multipartFile, ConvertSpeechToTextReq convertSpeechToTextReq)
        throws IOException {

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
            .diarization(DiarizationDTO.builder()
                .speakerCountMin(3)
                .speakerCountMax(6)
                .build())
            .wordAlignment(false)
            .fullText(true)
            .build();

        String originFileName = fileUtil.saveMultipartFile(multipartFile, sttFileProperties.getOrigin());

        callNaverClovaAsync(headers, originFileName, speechToTextReq)
            .thenAcceptAsync(
                speechToTextRes -> {
                    updateAiCounselSummaryStatus(
                        aiCounselSummary,
                        "COMPLETED".equals(speechToTextRes.result()) ? STT_COMPLETE : STT_FAILED,
                        objectMapper.valueToTree(speechToTextRes));
                    applicationEventPublisher.publishEvent(
                        new STTCompleteEvent(convertSpeechToTextReq.getCounselSessionId()));
                }
            )
            .exceptionally(
                ex -> {
                    log.error("Speech-to-text processing error", ex);
                    updateAiCounselSummaryStatus(aiCounselSummary, STT_FAILED, null);
                    return null;
                })
            .whenComplete(
                (result, throwable) -> {
                    // ✅ 성공/실패 여부 상관없이 파일 삭제
                    try {
                        Files.deleteIfExists(Path.of(sttFileProperties.getOrigin() + originFileName));
                        Files.deleteIfExists(
                            Path.of(
                                sttFileProperties.getConvert() + originFileName.replace(".webm", ".mp4")));
                    } catch (IOException e) {
                        log.warn("Failed to delete temp file: {}", originFileName, e);
                    }
                });
    }

    @Async
    public CompletableFuture<SpeechToTextRes> callNaverClovaAsync(Map<String, String> headers, String originFileName,
        SpeechToTextReq request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File sttReqFile = Paths.get(sttFileProperties.getOrigin(), originFileName).toFile();

                if (Objects.requireNonNull(sttReqFile.getName()).contains(".webm")) {
                    sttReqFile = fileUtil.convertWebmToMp4(sttReqFile.getName(), sttFileProperties.getOrigin(),
                        sttFileProperties.getConvert());
                }

                return naverClovaExternalService.convertSpeechToText(headers, new FileSystemResource(sttReqFile),
                        request)
                    .getBody();

            } catch (IOException e) {
                log.error("Error while reading file bytes", e);
                throw new CompletionException(e);
            }
        });
    }

    public void updateAiCounselSummaryStatus(AICounselSummary aiCounselSummary, AICounselSummaryStatus status,
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

        HashMap<String, SpeakerStatsDTO> speakerMap = new HashMap<>();

        speechToTextRes.speakers()
            .forEach(speaker -> speakerMap
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

        return speakerMap
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().isValidSpeaker())
            .sorted(Comparator.comparing((Map.Entry<String, SpeakerStatsDTO> entry)
                -> entry.getValue().getSpeakCount()).reversed())
            .map(map -> SelectSpeakerListRes.of(map.getKey(), map.getValue(), totalSpeakCount.get()))
            .toList();

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

    @Async
    @Transactional
    public void analyseText(String counselSessionId) throws JsonProcessingException {

        counselSessionRepository.findById(counselSessionId)
            .orElseThrow(IllegalArgumentException::new);

        AICounselSummary aiCounselSummary = aiCounselSummaryRepository
            .findByCounselSessionId(counselSessionId)
            .orElseThrow(NoContentException::new);

        aiCounselSummary.setAiCounselSummaryStatus(GPT_PROGRESS);

        JsonNode sttResult = Optional.ofNullable(aiCounselSummary.getSttResult())
            .orElseThrow(NoContentException::new);

        SpeechToTextRes speechToTextRes = objectMapper.treeToValue(sttResult, SpeechToTextRes.class);

        List<SelectSpeakerListRes> selectAnalysedTextRes = this.selectSpeakerList(counselSessionId);
        List<String> speakers = selectAnalysedTextRes
            .stream()
            .limit(3)
            .map(SelectSpeakerListRes::speaker)
            .toList();

        List<STTMessageForPromptDTO> sttMessages = speechToTextRes.segments()
            .stream()
            .filter(segmentDTO -> speakers
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
                    aiCounselSummary.setSpeakers(speakers);
                    aiCounselSummary.setTaResult(
                        objectMapper.valueToTree(chatResponse));
                    aiCounselSummary.setAiCounselSummaryStatus(GPT_COMPLETE);
                    aiCounselSummaryRepository.save(aiCounselSummary);
                })
            .exceptionally(ex -> {
                log.error("error", ex);
                aiCounselSummary.setSpeakers(speakers);
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

    public SelectAnalysedTextRes selectAnalysedText(String counselSessionId) {

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
