package com.pers.transfer.dto.response;

import com.pers.transfer.domain.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountOperationContextResponse(UUID clientId,
                                              UUID accountFrom,
                                              String accountFromName,
                                              UUID accountTo,
                                              String accountToName,
                                              BigDecimal sourceBalance,
                                              Currency currency,
                                              Currency targetCurrency,
                                              BigDecimal sourceRate,
                                              BigDecimal targetRate
) {
}
