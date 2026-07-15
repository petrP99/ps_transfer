package com.pers.transfer.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pers.transfer.dto.response.TransferResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.pers.transfer.websocket.TransferStatusHandshakeInterceptor.TRANSFER_ID_ATTRIBUTE;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferStatusWebSocketHandler extends TextWebSocketHandler {

    private static final String TRANSFER_STATUS_CHANGED = "TRANSFER_STATUS_CHANGED";

    private final ObjectMapper objectMapper;
    private final Map<UUID, Set<WebSocketSession>> sessionsByTransferId = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UUID transferId = getTransferId(session);
        if (transferId == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Transfer id is missing"));
            return;
        }

        sessionsByTransferId.computeIfAbsent(transferId, ignored -> ConcurrentHashMap.newKeySet()).add(session);
        log.info("WebSocket статуса перевода открыт: transferId={}", transferId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        removeSession(session);
        log.info("WebSocket статуса перевода закрыт: status={}", status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        removeSession(session);
        log.warn("Ошибка WebSocket статуса перевода: {}", exception.getMessage());
    }

    public void sendStatus(TransferResponse transfer) {
        Set<WebSocketSession> sessions = sessionsByTransferId.get(transfer.id());
        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        String payload;
        try {
            payload = objectMapper.writeValueAsString(Map.of(
                    "type", TRANSFER_STATUS_CHANGED,
                    "transfer", transfer
            ));
        } catch (JsonProcessingException exception) {
            log.warn("Не удалось сериализовать WebSocket статус перевода {}", transfer.id(), exception);
            return;
        }

        sessions.removeIf(session -> !send(session, payload));
    }

    private boolean send(WebSocketSession session, String payload) {
        if (!session.isOpen()) {
            return false;
        }

        try {
            synchronized (session) {
                session.sendMessage(new TextMessage(payload));
            }
            return true;
        } catch (IOException exception) {
            log.warn("Не удалось отправить WebSocket статус перевода: {}", exception.getMessage());
            return false;
        }
    }

    private void removeSession(WebSocketSession session) {
        UUID transferId = getTransferId(session);
        if (transferId == null) {
            return;
        }

        Set<WebSocketSession> sessions = sessionsByTransferId.get(transferId);
        if (sessions == null) {
            return;
        }

        sessions.remove(session);
        if (sessions.isEmpty()) {
            sessionsByTransferId.remove(transferId, sessions);
        }
    }

    private UUID getTransferId(WebSocketSession session) {
        Object transferId = session.getAttributes().get(TRANSFER_ID_ATTRIBUTE);
        return transferId instanceof UUID uuid ? uuid : null;
    }
}
