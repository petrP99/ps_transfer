package com.pers.transfer.dto.response;

import com.pers.transfer.domain.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountTransferResponse(UUID id,
                                      UUID accountFrom,
                                      String accountFromName,
                                      UUID accountTo,
                                      String accountToName,
                                      BigDecimal amount,
                                      BigDecimal amountTo,
                                      BigDecimal exchangeRate,
                                      Currency currency,
                                      Currency targetCurrency,
                                      LocalDateTime timeOfTransfer
) {
}
