package com.pers.transfer.config;

import com.pers.transfer.websocket.TransferStatusHandshakeInterceptor;
import com.pers.transfer.websocket.TransferStatusWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class TransferWebSocketConfig implements WebSocketConfigurer {

    private final TransferStatusWebSocketHandler transferStatusWebSocketHandler;
    private final TransferStatusHandshakeInterceptor transferStatusHandshakeInterceptor;

    @Value("${app.websocket.allowed-origin-patterns:http://localhost:5173,http://127.0.0.1:5173}")
    private String[] allowedOriginPatterns;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(transferStatusWebSocketHandler, "/ws/transfers/{id}")
                .addInterceptors(transferStatusHandshakeInterceptor)
                .setAllowedOriginPatterns(allowedOriginPatterns);
    }
}
