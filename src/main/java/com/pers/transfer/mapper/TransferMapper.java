package com.pers.transfer.mapper;

import com.pers.transfer.domain.AccountTransfer;
import com.pers.transfer.domain.Transfer;
import com.pers.transfer.domain.TransferStatus;
import com.pers.transfer.dto.response.CardOperationContextResponse;
import com.pers.transfer.dto.response.TransferHistoryResponse;
import com.pers.transfer.dto.response.TransferPreparationResponse;
import com.pers.transfer.dto.response.TransferPreviewResponse;
import com.pers.transfer.dto.response.TransferResponse;
import com.pers.transfer.service.TransferCalculationService.Calculation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class TransferMapper {

    public TransferPreviewResponse toPreviewResponse(
            CardOperationContextResponse context,
            BigDecimal amount, //todo для чего передается
            String recipientPhone,
            String message,
            Calculation calculation
    ) {
        return new TransferPreviewResponse(
                context.cardFrom(),
                context.cardTo(),
                calculation.amount(),
                calculation.amountTo(),
                calculation.exchangeRate(),
                calculation.commissionPercent(),
                calculation.commission(),
                calculation.debitAmount(),
                context.currency(),
                context.targetCurrency(),
                context.recipient(),
                recipientPhone,
                message
        );
    }

    public Transfer toEntity(
            TransferPreparationResponse preparation,
            String recipientPhone
    ) {
        TransferPreviewResponse preview = preparation.preview();
        return Transfer.builder()
                .fromClientId(preparation.fromClientId())
                .toClientId(preparation.toClientId())
                .cardFrom(preview.cardFrom())
                .cardTo(preview.cardTo())
                .amount(preview.amount())
                .amountTo(preview.amountTo())
                .exchangeRate(preview.exchangeRate())
                .commission(preview.commission())
                .debitAmount(preview.debitAmount())
                .currency(preview.currency())
                .targetCurrency(preview.targetCurrency())
                .status(TransferStatus.IN_PROGRESS)
                .timeOfTransfer(LocalDateTime.now())
                .sender(preparation.sender())
                .recipient(preview.recipient())
                .recipientPhone(recipientPhone)
                .message(preview.message())
                .build();
    }

    public TransferResponse toResponse(Transfer transfer) {
        return new TransferResponse(
                transfer.getId(),
                transfer.getFromClientId(),
                transfer.getToClientId(),
                transfer.getCardFrom(),
                transfer.getCardTo(),
                transfer.getAmount(),
                transfer.getTimeOfTransfer(),
                transfer.getRecipient(),
                transfer.getRecipientPhone(),
                transfer.getMessage(),
                transfer.getStatus(),
                transfer.getAmountTo(),
                transfer.getExchangeRate(),
                transfer.getCommission(),
                transfer.getDebitAmount(),
                transfer.getCurrency(),
                transfer.getTargetCurrency(),
                transfer.getCurrency() != transfer.getTargetCurrency()
        );
    }

    public TransferHistoryResponse toHistoryResponse(Transfer transfer, UUID clientId) {
        boolean incoming = transfer.getToClientId().equals(clientId);
        return new TransferHistoryResponse(
                transfer.getId(),
                incoming,
                incoming ? transfer.getSender() : transfer.getRecipient(),
                transfer.getCardFrom(),
                transfer.getCardTo(),
                transfer.getAmount(),
                transfer.getAmountTo(),
                transfer.getTimeOfTransfer(),
                transfer.getRecipientPhone(),
                transfer.getMessage(),
                transfer.getStatus(),
                transfer.getExchangeRate(),
                transfer.getCommission(),
                transfer.getDebitAmount(),
                transfer.getCurrency(),
                transfer.getTargetCurrency(),
                "CARD",
                null,
                null,
                null,
                null
        );
    }

    public TransferHistoryResponse toHistoryResponse(AccountTransfer transfer) {
        return new TransferHistoryResponse(
                transfer.getId(),
                false,
                "Между своими счетами",
                null,
                null,
                transfer.getAmount(),
                transfer.getAmountTo(),
                transfer.getTimeOfTransfer(),
                null,
                null,
                TransferStatus.SUCCESS,
                transfer.getExchangeRate(),
                BigDecimal.ZERO,
                transfer.getAmount(),
                transfer.getCurrency(),
                transfer.getTargetCurrency(),
                "ACCOUNT",
                transfer.getAccountFrom(),
                transfer.getAccountFromName(),
                transfer.getAccountTo(),
                transfer.getAccountToName()
        );
    }
}
