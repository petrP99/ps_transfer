package com.pers.transfer.kafka;

import com.pers.transfer.event.BalanceOperationResult;
import com.pers.transfer.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransferResultListener {

    private final TransferService transferService;

    @KafkaListener(
            topics = "${spring.kafka.topics.transfer-result}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void onResult(BalanceOperationResult result) {
        transferService.applyExecutionResult(result);
    }
}
