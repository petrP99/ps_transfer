package com.pers.transfer.dto.response;

import com.pers.transfer.domain.Currency;
import com.pers.transfer.domain.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferResponse(UUID id,
                               UUID fromClientId,
                               UUID toClientId,
                               String cardFrom,
                               String cardTo,
                               BigDecimal amount,
                               LocalDateTime timeOfTransfer,
                               String recipient,
                               String recipientPhone,
                               String message,
                               TransferStatus status,
                               BigDecimal amountTo,
                               BigDecimal exchangeRate,
                               BigDecimal commission,
                               BigDecimal debitAmount,
                               Currency currency,
                               Currency targetCurrency,
                               boolean isExchange
) {
}
