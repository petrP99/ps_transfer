package com.pers.transfer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pers.transfer.domain.OutboxEvent;
import com.pers.transfer.domain.OutboxEventType;
import com.pers.transfer.domain.OutboxStatus;
import com.pers.transfer.event.BalanceOperationCommand;
import com.pers.transfer.exception.BusinessException;
import com.pers.transfer.exception.ErrorCode;
import com.pers.transfer.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveExecutionRequested(BalanceOperationCommand command) {
        LocalDateTime now = LocalDateTime.now();
        repository.save(OutboxEvent.builder()
                .aggregateId(command.operationId())
                .eventType(OutboxEventType.TRANSFER_EXECUTION_REQUESTED)
                .eventKey(command.operationId().toString())
                .payload(write(command))
                .status(OutboxStatus.PENDING)
                .attempts(0)
                .createdAt(now)
                .nextAttemptAt(now)
                .build());
    }

    private String write(BalanceOperationCommand command) {
        try {
            return objectMapper.writeValueAsString(command);
        } catch (JsonProcessingException exception) {
            throw new BusinessException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorCode.OUTBOX_SERIALIZE_FAILED,
                    exception
            );
        }
    }
}
