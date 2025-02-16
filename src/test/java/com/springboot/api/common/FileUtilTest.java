package com.springboot.api.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.springboot.api.common.util.FileUtil;
import com.springboot.api.dto.naverClova.SpeechToTextReq;
import com.springboot.api.dto.naverClova.SpeechToTextRes;
import com.springboot.api.infra.external.NaverClovaExternalService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import java.util.Map;

@SpringBootTest
@Disabled
public class FileUtilTest {

    @Autowired
    FileUtil fileUtil;

    @ParameterizedTest
    @ValueSource(strings = {"test5.webm"})
    void testConvertWebmToM4a(String filename) throws IOException {

        File mp4File = ResourceUtils.getFile("classpath:" + "stt/audio/"+filename);
        FileInputStream inputStream = new FileInputStream(mp4File);
        String originPath = "/Users/choisunpil/Desktop/development/2024/spring-boot-boilerplate/src/test/resources/stt/audio/test/origin/";
        String convertPath = "/Users/choisunpil/Desktop/development/2024/spring-boot-boilerplate/src/test/resources/stt/audio/test/convert/";

        MultipartFile multipartFile = new MockMultipartFile(
                "media",
                mp4File.getName(),
                "audio/webm",
                inputStream);

        MultipartFile convertedMultipartFile = fileUtil.convertWebmToMp4(multipartFile, originPath, convertPath);



        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("https://clovaspeech-gw.ncloud.com/external/v1/10309/338f74c076c81a57b47313f867ab35519c289e3f8dede43066bea9f266708cb1"));
        RestTemplateAdapter adapter = RestTemplateAdapter.create(restTemplate);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        NaverClovaExternalService service = factory.createClient(NaverClovaExternalService.class);


        Map<String, String> headers = new HashMap<>();
        headers.put("Accept","application/json");
        headers.put("X-CLOVASPEECH-API-KEY","796d7a638753441eb241a266f1f10d49");


        SpeechToTextReq speechToTextReq = SpeechToTextReq
                .builder()
                .language("ko-KR")
                .completion("sync")
                .wordAlignment(true)
                .fullText(true)
                .build();


        SpeechToTextRes speechToTextRes = service.convertSpeechToText(headers, convertedMultipartFile, speechToTextReq).getBody();


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        File file = new File("src/test/resources/stt/output/"+mp4File.getName()+".json");
        objectMapper.writeValue(file, speechToTextRes);





    }

}
