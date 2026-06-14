package com.pers.transfer.mapper;

import com.pers.transfer.domain.AccountTransfer;
import com.pers.transfer.dto.request.AccountTransferRequest;
import com.pers.transfer.dto.response.AccountOperationContextResponse;
import com.pers.transfer.dto.response.AccountTransferResponse;
import com.pers.transfer.service.TransferCalculationService.Calculation;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class AccountTransferMapper {

    public AccountTransferResponse toPreviewResponse(AccountTransferRequest request,
                                                     AccountOperationContextResponse context,
                                                     Calculation calculation) {
        return new AccountTransferResponse(
                null,
                request.accountFrom(),
                context.accountFromName(),
                request.accountTo(),
                context.accountToName(),
                calculation.amount(),
                calculation.amountTo(),
                calculation.exchangeRate(),
                context.currency(),
                context.targetCurrency(),
                null
        );
    }

    public AccountTransfer toEntity(AccountTransferResponse response, UUID clientId) {
        return AccountTransfer.builder()
                .clientId(clientId)
                .accountFrom(response.accountFrom())
                .accountFromName(response.accountFromName())
                .accountTo(response.accountTo())
                .accountToName(response.accountToName())
                .amount(response.amount())
                .amountTo(response.amountTo())
                .exchangeRate(response.exchangeRate())
                .currency(response.currency())
                .targetCurrency(response.targetCurrency())
                .timeOfTransfer(LocalDateTime.now())
                .build();
    }

    public AccountTransferResponse toResponse(AccountTransfer transfer) {
        return new AccountTransferResponse(
                transfer.getId(),
                transfer.getAccountFrom(),
                transfer.getAccountFromName(),
                transfer.getAccountTo(),
                transfer.getAccountToName(),
                transfer.getAmount(),
                transfer.getAmountTo(),
                transfer.getExchangeRate(),
                transfer.getCurrency(),
                transfer.getTargetCurrency(),
                transfer.getTimeOfTransfer()
        );
    }
}
