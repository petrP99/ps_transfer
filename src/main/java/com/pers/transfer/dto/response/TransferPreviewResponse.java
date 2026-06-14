package com.pers.transfer.dto.response;

import com.pers.transfer.domain.Currency;

import java.math.BigDecimal;

public record TransferPreviewResponse(String cardFrom,
                                      String cardTo,
                                      BigDecimal amount,
                                      BigDecimal amountTo,
                                      BigDecimal exchangeRate,
                                      BigDecimal commissionPercent,
                                      BigDecimal commission,
                                      BigDecimal debitAmount,
                                      Currency currency,
                                      Currency targetCurrency,
                                      String recipient,
                                      String recipientPhone,
                                      String message
) {
}
