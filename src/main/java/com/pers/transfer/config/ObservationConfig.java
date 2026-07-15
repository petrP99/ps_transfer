package com.pers.transfer.config;

import io.micrometer.observation.ObservationPredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.DefaultServerRequestObservationConvention;
import org.springframework.http.server.observation.ServerRequestObservationContext;
import org.springframework.http.server.observation.ServerRequestObservationConvention;

@Configuration
public class ObservationConfig {

    @Bean
    ObservationPredicate skipSpringSecurityObservations() {
        return (name, context) ->
                !name.startsWith("spring.security")
                        && !context.getClass().getName().startsWith("org.springframework.security");
    }

    @Bean
    ServerRequestObservationConvention serverRequestObservationConvention() {
        return new DefaultServerRequestObservationConvention() {
            @Override
            public String getContextualName(ServerRequestObservationContext context) {
                return context.getCarrier().getMethod() + " " + requestPath(context);
            }
        };
    }

    private static String requestPath(ServerRequestObservationContext context) {
        String pathPattern = context.getPathPattern();
        if (hasText(pathPattern)) {
            return pathPattern;
        }
        return normalizePath(context.getCarrier().getRequestURI());
    }

    private static String normalizePath(String path) {
        if (!hasText(path)) {
            return "/unknown";
        }
        return path
                .replaceAll("/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}(?=/|$)", "/{id}")
                .replaceAll("/\\d+(?=/|$)", "/{id}");
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
