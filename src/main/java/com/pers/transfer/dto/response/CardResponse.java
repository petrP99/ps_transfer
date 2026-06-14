package com.pers.transfer.dto.response;

import com.pers.transfer.domain.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CardResponse(UUID id,
                           UUID clientId,
                           UUID accountId,
                           BigDecimal balance,
                           LocalDate createdDate,
                           LocalDate expireDate,
                           String name,
                           Currency currency,
                           String status,
                           String cardNumber
) {
}
