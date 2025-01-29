package com.duoc.dsy2206equipments.config;

import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Configuration
public class FeignBasicAuthConfig {

    @Bean
    public RequestInterceptor basicAuthInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                String username = "admin";
                String password = "admin123";
                String authHeader = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
                requestTemplate.header("Authorization", authHeader);
            }
        };
    }
    
}
