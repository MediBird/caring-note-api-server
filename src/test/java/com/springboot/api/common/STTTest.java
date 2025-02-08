package com.springboot.api.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.springboot.api.dto.naverClova.SpeechToTextReq;
import com.springboot.api.dto.naverClova.SpeechToTextRes;
import com.springboot.api.service.NaverClovaService;
import lombok.Builder;
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
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestTemplateAdapter;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SpringBootTest
public class STTTest {

    @Autowired
    ChatModel chatModel;

    private static final Logger log = LoggerFactory.getLogger(STTTest.class);

    @ParameterizedTest
    @ValueSource(strings = {"test1.m4a", "test2.m4a", "test3.m4a","test4.m4a"})
    public void testTransformSTT(String filename) throws IOException, SecurityException {

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("https://clovaspeech-gw.ncloud.com/external/v1/10309/338f74c076c81a57b47313f867ab35519c289e3f8dede43066bea9f266708cb1"));
        RestTemplateAdapter adapter = RestTemplateAdapter.create(restTemplate);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        NaverClovaService service = factory.createClient(NaverClovaService.class);


        Map<String, String> headers = new HashMap<>();
        headers.put("Accept","application/json");
        headers.put("X-CLOVASPEECH-API-KEY","796d7a638753441eb241a266f1f10d49");


        File mp4File = ResourceUtils.getFile("classpath:" + "stt/audio/"+filename);
        FileInputStream inputStream = new FileInputStream(mp4File);

        MultipartFile multipartFile = new MockMultipartFile(
                "media",
                mp4File.getName(),
                "audio/m4a",
                inputStream);

        SpeechToTextReq speechToTextReq = SpeechToTextReq
                .builder()
                .language("ko-KR")
                .completion("sync")
                .wordAlignment(true)
                .fullText(true)
                .build();


        SpeechToTextRes speechToTextRes = service.transformSpeechToText(headers, multipartFile, speechToTextReq).getBody();


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        File file = new File("src/test/resources/stt/output/"+mp4File.getName()+".json");
        objectMapper.writeValue(file, speechToTextRes);

    }

    @ParameterizedTest
    @ValueSource(strings = {"test2.m4a.json"})
    public void testAnalyzeText(String filename) throws IOException, SecurityException {

        ChatClient chatClient = ChatClient.builder(this.chatModel).build();

        SystemMessage systemMessage = new SystemMessage(
        """
        복약상담 내용을 요약하는 시스템이야.
        내가 여러명의 대화를 Speech To Text 한 결과 json을 전달해줄테니
        내용을 요약해줘. 참고로 해당 요약정보를 보는 사람은 10년차 약사야.
        """);

        ObjectMapper objectMapper = new ObjectMapper();

        Resource resource = new ClassPathResource("stt/output/"+filename);

        SpeechToTextRes speechToTextRes = objectMapper.readValue(resource.getFile(), SpeechToTextRes.class);



        List<SttMessage> sttMessages = speechToTextRes.segments()
                .stream()
                .filter(segmentDTO -> List.of("A","E").contains(segmentDTO.speaker().name()))
                .map(segmentDTO -> SttMessage
                        .builder()
                        .speaker(segmentDTO.speaker().name())
                        .text(segmentDTO.text())
                        .build())
                .toList();

        String sttMessagesJson = objectMapper.writeValueAsString(sttMessages);

        UserMessage userMessage = new UserMessage(sttMessagesJson);



        List<Message> messages = List.of(systemMessage, userMessage);

        Prompt prompt = new Prompt(messages);


        ChatResponse chatResponse = chatClient.prompt(prompt)
                .call()
                .chatResponse();

        log.info(Objects.requireNonNull(chatResponse).toString());

    }

    @Builder
    record SttMessage(String speaker, String text){};


}
