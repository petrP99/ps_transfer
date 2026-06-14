package com.pers.transfer.dto.response;

import com.pers.transfer.domain.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public record CardOperationContextResponse(UUID fromClientId,
                                           UUID toClientId,
                                           String cardFrom,
                                           String cardTo,
                                           BigDecimal sourceBalance,
                                           Currency currency,
                                           Currency targetCurrency,
                                           BigDecimal sourceRate,
                                           BigDecimal targetRate,
                                           String sender,
                                           String recipient
) {
}
