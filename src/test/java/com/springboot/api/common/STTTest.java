package com.springboot.api.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.springboot.api.counselsession.dto.naverClova.DiarizationDTO;
import com.springboot.api.counselsession.dto.naverClova.SpeechToTextReq;
import com.springboot.api.counselsession.dto.naverClova.SpeechToTextRes;
import com.springboot.api.infra.external.NaverClovaExternalService;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestTemplateAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;

@SpringBootTest
@Disabled
public class STTTest {

        @Autowired
        ChatModel chatModel;

        private static final Logger log = LoggerFactory.getLogger(STTTest.class);

        record SttMessage(String speaker, String text) {

        }

        @ParameterizedTest
        @ValueSource(strings = { "test1.m4a", "test2.m4a", "test3.m4a", "test4.m4a" })
        public void testTransformSTT(String filename) throws IOException, SecurityException {

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(
                                "https://clovaspeech-gw.ncloud.com/external/v1/10310/3ae57a3d3c79f41de8bb6429f954bcc3cb78905e2fb7e41b2116f2213e449197"));
                RestTemplateAdapter adapter = RestTemplateAdapter.create(restTemplate);
                HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

                NaverClovaExternalService service = factory.createClient(NaverClovaExternalService.class);

                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("X-CLOVASPEECH-API-KEY", "6c294a231c7d42989a5ef003fd09c3d4");

                File mp4File = ResourceUtils.getFile("classpath:" + "stt/audio/" + filename);
                
                SpeechToTextReq speechToTextReq = SpeechToTextReq.builder()
                                .language("ko-KR")
                                .completion("sync")
                                .wordAlignment(false)
                                .fullText(true)
                                .diarization(DiarizationDTO.builder()
                                                .speakerCountMin(3)
                                                .speakerCountMax(7)
                                                .build())
                                .build();

                SpeechToTextRes speechToTextRes = service.convertSpeechToText(headers, new FileSystemResource(mp4File), speechToTextReq)
                                .getBody();

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

                File file = new File("src/test/resources/stt/output/" + mp4File.getName() + LocalTime.now() + ".json");
                objectMapper.writeValue(file, speechToTextRes);

        }

        @ParameterizedTest
        @ValueSource(strings = { "test1.m4a.json" })
        public void testAnalyzeText(String filename) throws IOException, SecurityException {

                ChatClient chatClient = ChatClient.builder(this.chatModel).build();

                SystemMessage systemMessage = new SystemMessage(
                                """
                                                복약상담 내용을 요약하는 시스템이야.
                                                내가 여러명의 대화를 Speech To Text 한 결과 json을 전달해줄테니
                                                내용을 요약해줘. 참고로 해당 요약정보를 보는 사람은 10년차 약사야.
                                                그리고 요약 정보는 아래 sample 과 동일한 양식의 markdown 으로 만들어줘
                                                """);
                SystemMessage systemMessage2 = new SystemMessage(
                                """
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

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());

                Resource resource = new ClassPathResource("stt/output/" + filename);

                SpeechToTextRes speechToTextRes = objectMapper.readValue(resource.getFile(), SpeechToTextRes.class);

                List<SttMessage> sttMessages = speechToTextRes.segments()
                                .stream()
                                .filter(segmentDTO -> List.of("A", "E").contains(segmentDTO.speaker().name()))
                                .map(segmentDTO -> new SttMessage(segmentDTO.speaker().name(), segmentDTO.text()))
                                .toList();

                String sttMessagesJson = objectMapper.writeValueAsString(sttMessages);

                UserMessage userMessage = new UserMessage(sttMessagesJson);

                List<Message> messages = List.of(systemMessage, systemMessage2, userMessage);

                Prompt prompt = new Prompt(messages);
                ChatResponse chatResponse = chatClient.prompt(prompt)
                                .call()
                                .chatResponse();

                log.info(chatClient.prompt(prompt)
                                .call().content());

                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

                File file = new File("src/test/resources/ta/output/" + filename + ".md");
                objectMapper.writeValue(file, Objects.requireNonNull(chatResponse).getResult().getOutput().getText());

                log.info(Objects.requireNonNull(chatResponse).getResult().getOutput().getText());

        }
}