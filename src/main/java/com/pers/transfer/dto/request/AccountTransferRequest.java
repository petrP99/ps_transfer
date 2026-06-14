package com.pers.transfer.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountTransferRequest(
        @NotNull(message = "{validation.account.sender.required}")
        UUID accountFrom,
        @NotNull(message = "{validation.account.recipient.required}")
        UUID accountTo,
        @NotNull(message = "{validation.transfer.amount.required}")
        @Positive(message = "{validation.transfer.amount.positive}")
        @Digits(
                integer = 17,
                fraction = 2,
                message = "{validation.transfer.amount.digits}"
        )
        BigDecimal amount
) {
}
