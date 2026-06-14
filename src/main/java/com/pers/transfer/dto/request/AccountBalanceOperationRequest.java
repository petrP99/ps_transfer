package com.pers.transfer.dto.request;

import com.pers.transfer.domain.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountBalanceOperationRequest(UUID accountFrom,
                                             UUID accountTo,
                                             BigDecimal debitAmount,
                                             BigDecimal creditAmount,
                                             Currency currency,
                                             Currency targetCurrency
) {
}
