package com.pers.transfer.service;

import com.pers.transfer.domain.Currency;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TransferCalculationService {

    @Value("${transfer.commission-percent}")
    private BigDecimal commissionPercent;

    public Calculation calculate(
            BigDecimal amount,
            Currency sourceCurrency,
            Currency targetCurrency,
            BigDecimal sourceRate,
            BigDecimal targetRate,
            boolean applyExchangeCommission
    ) {
        BigDecimal normalizedAmount = amount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal exchangeRate = sourceRate.divide(targetRate, 6, RoundingMode.HALF_UP);
        BigDecimal amountTo = normalizedAmount.multiply(exchangeRate)
                .setScale(2, RoundingMode.HALF_UP);
        boolean exchange = sourceCurrency != targetCurrency;
        BigDecimal appliedPercent = exchange && applyExchangeCommission
                ? commissionPercent
                : BigDecimal.ZERO;
        BigDecimal commission = normalizedAmount.multiply(appliedPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal debitAmount = normalizedAmount.add(commission)
                .setScale(2, RoundingMode.HALF_UP);
        return new Calculation(
                normalizedAmount,
                amountTo,
                exchangeRate,
                appliedPercent,
                commission,
                debitAmount
        );
    }

    public record Calculation(
            BigDecimal amount,
            BigDecimal amountTo,
            BigDecimal exchangeRate,
            BigDecimal commissionPercent,
            BigDecimal commission,
            BigDecimal debitAmount
    ) {
    }
}
