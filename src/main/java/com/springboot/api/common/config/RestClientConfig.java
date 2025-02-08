package com.springboot.api.common.config;

import com.springboot.api.infra.external.NaverClovaExternalService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestTemplateAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestClientConfig {

    @Bean
    public NaverClovaExternalService naverClovaExternalService() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("https://clovaspeech-gw.ncloud.com/external/v1/10310/3ae57a3d3c79f41de8bb6429f954bcc3cb78905e2fb7e41b2116f2213e449197"));
        RestTemplateAdapter adapter = RestTemplateAdapter.create(restTemplate);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(NaverClovaExternalService.class);
    }

}
