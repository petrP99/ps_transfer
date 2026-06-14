package com.pers.transfer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transfer")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "from_client_id", nullable = false)
    private UUID fromClientId;

    @Column(name = "to_client_id", nullable = false)
    private UUID toClientId;

    @Column(name = "card_from", nullable = false, length = 16)
    private String cardFrom;

    @Column(name = "card_to", nullable = false, length = 16)
    private String cardTo;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "amount_to", nullable = false, precision = 19, scale = 2)
    private BigDecimal amountTo;

    @Column(name = "exchange_rate", nullable = false, precision = 19, scale = 6)
    private BigDecimal exchangeRate;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal commission;

    @Column(name = "debit_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal debitAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_currency", nullable = false)
    private Currency targetCurrency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status;

    @Column(name = "time_of_transfer", nullable = false)
    private LocalDateTime timeOfTransfer;

    @Column(nullable = false, length = 120)
    private String sender;

    @Column(nullable = false, length = 120)
    private String recipient;

    @Column(name = "recipient_phone", length = 20)
    private String recipientPhone;

    @Column(length = 120)
    private String message;

    @Column(name = "failure_code", length = 80)
    private String failureCode;
}
