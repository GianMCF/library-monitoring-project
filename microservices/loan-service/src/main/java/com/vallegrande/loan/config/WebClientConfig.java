package com.vallegrande.loan.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${catalog.url}")
    private String catalogUrl;

    @Bean
    public WebClient catalogClient() {

        return WebClient.builder()
                .baseUrl(catalogUrl)
                .build();
    }
}