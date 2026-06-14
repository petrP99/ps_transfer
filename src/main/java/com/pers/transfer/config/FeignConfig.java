package com.pers.transfer.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pers.transfer.exception.RemoteServiceException;
import feign.RequestInterceptor;
import feign.Response;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {

    private final ObjectMapper objectMapper;

    @Value("${services.core.internal-token}")
    private String internalToken;

    @Bean
    RequestInterceptor bearerTokenRelay() {
        return template -> {
            template.header("X-Internal-Token", internalToken);
            if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
                HttpServletRequest request = attributes.getRequest();
                String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
                if (authorization != null && !authorization.isBlank()) {
                    template.header(HttpHeaders.AUTHORIZATION, authorization);
                }
            }
        };
    }

    @Bean
    ErrorDecoder coreErrorDecoder() {
        return (methodKey, response) -> new RemoteServiceException(
                HttpStatus.resolve(response.status()),
                readDetail(response)
        );
    }

    private String readDetail(Response response) {
        if (response.body() == null) {
            return null;
        }
        try (InputStream input = response.body().asInputStream()) {
            JsonNode problem = objectMapper.readTree(input);
            return problem.path("detail").asText(null);
        } catch (IOException ignored) {
            return null;
        }
    }
}
