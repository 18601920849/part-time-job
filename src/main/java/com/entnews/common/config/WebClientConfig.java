package com.entnews.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("api.url")
    public String BASE_URL ;

    @Bean
    public WebClient webClient() {
        // 创建WebClient实例，指定基础URL
        return WebClient.create(BASE_URL);
    }

}
