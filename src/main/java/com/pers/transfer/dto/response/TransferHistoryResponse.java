package com.pers.transfer.dto.response;

import com.pers.transfer.domain.Currency;
import com.pers.transfer.domain.TransferStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferHistoryResponse(UUID id,
                                      boolean incoming,
                                      String counterparty,
                                      String cardFrom,
                                      String cardTo,
                                      BigDecimal amount,
                                      BigDecimal amountTo,
                                      LocalDateTime timeOfTransfer,
                                      String recipientPhone,
                                      String message,
                                      TransferStatus status,
                                      BigDecimal exchangeRate,
                                      BigDecimal commission,
                                      BigDecimal debitAmount,
                                      Currency currency,
                                      Currency targetCurrency,
                                      String operationType,
                                      UUID accountFrom,
                                      String accountFromName,
                                      UUID accountTo,
                                      String accountToName
) {
}
