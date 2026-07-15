package com.pers.transfer.websocket;

import com.pers.transfer.client.CoreBankingClient;
import com.pers.transfer.dto.response.ClientResponse;
import com.pers.transfer.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriTemplate;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferStatusHandshakeInterceptor implements HandshakeInterceptor {

    public static final String TRANSFER_ID_ATTRIBUTE = "transferId";
    public static final String CLIENT_ID_ATTRIBUTE = "clientId";

    private static final UriTemplate TRANSFER_STATUS_PATH = new UriTemplate("/ws/transfers/{id}");

    private final CoreBankingClient coreBankingClient;
    private final TransferRepository transferRepository;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        UUID transferId = extractTransferId(request);
        if (transferId == null) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        ClientResponse profile = coreBankingClient.getProfile();
        UUID clientId = profile.id();
        if (transferRepository.findByIdAndFromClientId(transferId, clientId).isEmpty()) {
            response.setStatusCode(HttpStatus.NOT_FOUND);
            return false;
        }

        attributes.put(TRANSFER_ID_ATTRIBUTE, transferId);
        attributes.put(CLIENT_ID_ATTRIBUTE, clientId);
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
        if (exception != null) {
            log.warn("Ошибка WebSocket handshake статуса перевода: {}", exception.getMessage());
        }
    }

    private UUID extractTransferId(ServerHttpRequest request) {
        try {
            String path = request.getURI().getPath();
            String id = TRANSFER_STATUS_PATH.match(path).get("id");
            return UUID.fromString(id);
        } catch (RuntimeException exception) {
            return null;
        }
    }
}
