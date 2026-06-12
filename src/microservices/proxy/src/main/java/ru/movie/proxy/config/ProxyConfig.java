package ru.movie.proxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

public class ProxyConfig {

        @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
}
