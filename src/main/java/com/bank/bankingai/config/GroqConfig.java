
package com.bank.bankingai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GroqConfig {

    @Value("${groq.api.url}")
    private String groqApiUrl;

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Bean
    public RestClient groqRestClient() {

        return RestClient.builder()
                .baseUrl(groqApiUrl)
                .defaultHeader(
                        "Authorization",
                        "Bearer " + groqApiKey
                )
                .defaultHeader(
                        "Content-Type",
                        "application/json"
                )
                .build();
    }
}
