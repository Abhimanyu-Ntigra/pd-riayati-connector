package com.ntigra.riayati_middleware.config;

import com.ntigra.riayati_middleware.util.RiayatiResponseErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class RiayatiRestConfig {

    private final RiayatiProperties properties;

    @Bean("riayatiRestTemplate")
    public RestTemplate riayatiRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000);  // 30 seconds
        factory.setReadTimeout(60000);     // 60 seconds

        RestTemplate restTemplate = new RestTemplate(factory);

        // Add error handler
        restTemplate.setErrorHandler(new RiayatiResponseErrorHandler());

        return restTemplate;
    }
}
