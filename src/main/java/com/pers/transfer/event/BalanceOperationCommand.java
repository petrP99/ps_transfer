package com.pers.transfer.event;

import com.pers.transfer.domain.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceOperationCommand(UUID operationId,
                                      UUID fromClientId,
                                      UUID toClientId,
                                      String cardFrom,
                                      String cardTo,
                                      BigDecimal debitAmount,
                                      BigDecimal amountTo,
                                      Currency currency,
                                      Currency targetCurrency
) {
}
