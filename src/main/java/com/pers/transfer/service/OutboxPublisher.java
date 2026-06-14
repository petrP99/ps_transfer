package com.pers.transfer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pers.transfer.domain.OutboxEvent;
import com.pers.transfer.domain.OutboxStatus;
import com.pers.transfer.event.BalanceOperationCommand;
import com.pers.transfer.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxPublisher {

    private static final int MAX_ERROR_LENGTH = 1000;
    private static final String UNKNOWN_PUBLISHING_ERROR = "Unknown publishing error";

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.transfer-execute}")
    private String executionTopic;

    @Value("${outbox.publisher.batch-size:50}")
    private int batchSize;

    @Value("${outbox.publisher.retry-delay-seconds:10}")
    private long retryDelaySeconds;

    @Value("${outbox.publisher.max-attempts:20}")
    private int maxAttempts;

    @Value("${outbox.publisher.max-retry-delay-seconds:3600}")
    private long maxRetryDelaySeconds;

    @Value("${outbox.publisher.retention-days:7}")
    private long retentionDays;

    @Scheduled(
            fixedDelayString = "${outbox.publisher.fixed-delay-ms:1000}",
            initialDelayString = "${outbox.publisher.initial-delay-ms:1000}"
    )
    @Transactional
    public void publishBatch() {
        repository.findReadyForPublishing(batchSize).forEach(this::publish);
    }

    @Scheduled(cron = "${outbox.publisher.cleanup-cron:0 0 3 * * *}")
    @Transactional
    public void cleanupPublishedEvents() {
        int deleted = repository.deletePublishedBefore(
                LocalDateTime.now().minusDays(retentionDays)
        );
        if (deleted > 0) {
            log.info("Deleted {} published outbox events", deleted);
        }
    }

    private void publish(OutboxEvent event) {
        try {
            BalanceOperationCommand command =
                    objectMapper.readValue(event.getPayload(), BalanceOperationCommand.class);
            kafkaTemplate.send(executionTopic, event.getEventKey(), command).get(5, TimeUnit.SECONDS);
            event.setStatus(OutboxStatus.PUBLISHED);
            event.setPublishedAt(LocalDateTime.now());
            event.setLastError(null);
        } catch (Exception exception) {
            int attempts = event.getAttempts() + 1;
            event.setAttempts(attempts);
            event.setLastError(limit(exception.getMessage()));
            if (attempts >= maxAttempts) {
                event.setStatus(OutboxStatus.FAILED);
                log.error("Outbox event {} permanently failed", event.getId(), exception);
                return;
            }
            long delay = Math.min(
                    retryDelaySeconds * (1L << Math.min(attempts - 1, 6)),
                    maxRetryDelaySeconds
            );
            event.setNextAttemptAt(LocalDateTime.now().plusSeconds(delay));
            log.warn("Outbox event {} publish attempt {} failed", event.getId(), attempts);
        }
    }

    private String limit(String message) {
        String value = message == null || message.isBlank()
                ? UNKNOWN_PUBLISHING_ERROR
                : message;
        return value.substring(0, Math.min(value.length(), MAX_ERROR_LENGTH));
    }
}
