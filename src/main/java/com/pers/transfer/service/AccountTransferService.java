package com.pers.transfer.service;

import com.pers.transfer.client.CoreBankingClient;
import com.pers.transfer.domain.AccountTransfer;
import com.pers.transfer.dto.request.AccountBalanceOperationRequest;
import com.pers.transfer.dto.request.AccountOperationContextRequest;
import com.pers.transfer.dto.response.AccountOperationContextResponse;
import com.pers.transfer.dto.request.AccountTransferRequest;
import com.pers.transfer.dto.response.AccountTransferResponse;
import com.pers.transfer.exception.ErrorCode;
import com.pers.transfer.exception.TransferException;
import com.pers.transfer.mapper.AccountTransferMapper;
import com.pers.transfer.repository.AccountTransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.http.HttpStatus.CONFLICT;

@Service
@RequiredArgsConstructor
public class AccountTransferService {

    private final CoreBankingClient coreClient;
    private final AccountTransferRepository repository;
    private final AccountTransferMapper accountTransferMapper;
    private final TransferCalculationService calculationService;

    public AccountTransferResponse preview(AccountTransferRequest request) {
        return prepare(request).response();
    }

    @Transactional
    public AccountTransferResponse transfer(AccountTransferRequest request) {
        PreparedAccountTransfer prepared = prepare(request);
        AccountTransferResponse response = prepared.response();
        coreClient.executeAccountOperation(new AccountBalanceOperationRequest(
                response.accountFrom(),
                response.accountTo(),
                response.amount(),
                response.amountTo(),
                response.currency(),
                response.targetCurrency()
        ));
        AccountTransfer transfer = repository.save(
                accountTransferMapper.toEntity(response, prepared.clientId())
        );
        return accountTransferMapper.toResponse(transfer);
    }

    private PreparedAccountTransfer prepare(AccountTransferRequest request) {
        AccountOperationContextResponse context = coreClient.getAccountContext(
                new AccountOperationContextRequest(request.accountFrom(), request.accountTo())
        );
        TransferCalculationService.Calculation calculation = calculationService.calculate(
                request.amount(),
                context.currency(),
                context.targetCurrency(),
                context.sourceRate(),
                context.targetRate(),
                false
        );
        if (context.sourceBalance().compareTo(calculation.debitAmount()) < 0) {
            throw new TransferException(CONFLICT, ErrorCode.ACCOUNT_INSUFFICIENT_FUNDS);
        }
        return new PreparedAccountTransfer(
                context.clientId(),
                accountTransferMapper.toPreviewResponse(request, context, calculation)
        );
    }

    private record PreparedAccountTransfer(
            java.util.UUID clientId,
            AccountTransferResponse response
    ) {
    }
}
