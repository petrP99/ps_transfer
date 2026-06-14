package com.pers.transfer.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TransferPreviewRequest(
        @NotBlank(message = "{validation.card.sender.required}")
        @Pattern(regexp = "\\d{16}", message = "{validation.card.sender.number}")
        String cardFrom,
        @NotBlank(message = "{validation.card.recipient.required}")
        @Pattern(regexp = "\\d{16}", message = "{validation.card.recipient.number}")
        String cardTo,
        @NotNull(message = "{validation.transfer.amount.required}")
        @Positive(message = "{validation.transfer.amount.positive}")
        @Digits(
                integer = 17,
                fraction = 2,
                message = "{validation.transfer.amount.digits}"
        )
        BigDecimal amount,
        @Size(max = 120, message = "{validation.transfer.message.size}")
        String message
) {
}
