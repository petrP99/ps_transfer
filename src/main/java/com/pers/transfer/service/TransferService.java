package com.pers.transfer.service;

import com.pers.transfer.client.CoreBankingClient;
import com.pers.transfer.domain.Transfer;
import com.pers.transfer.domain.TransferStatus;
import com.pers.transfer.dto.request.CardOperationContextRequest;
import com.pers.transfer.dto.response.CardOperationContextResponse;
import com.pers.transfer.dto.response.PageResponse;
import com.pers.transfer.dto.request.PhoneTransferPreviewRequest;
import com.pers.transfer.dto.request.PhoneTransferRequest;
import com.pers.transfer.dto.request.PhoneOperationContextRequest;
import com.pers.transfer.dto.response.TransferHistoryResponse;
import com.pers.transfer.dto.response.TransferPreparationResponse;
import com.pers.transfer.dto.request.TransferPreviewRequest;
import com.pers.transfer.dto.response.TransferPreviewResponse;
import com.pers.transfer.dto.request.TransferRequest;
import com.pers.transfer.dto.response.TransferResponse;
import com.pers.transfer.event.BalanceOperationCommand;
import com.pers.transfer.event.BalanceOperationResult;
import com.pers.transfer.exception.ErrorCode;
import com.pers.transfer.exception.TransferException;
import com.pers.transfer.mapper.TransferMapper;
import com.pers.transfer.repository.AccountTransferRepository;
import com.pers.transfer.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CONFLICT;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final CoreBankingClient coreClient;
    private final TransferRepository transferRepository;
    private final AccountTransferRepository accountTransferRepository;
    private final OutboxService outboxService;
    private final TransferMapper transferMapper;
    private final TransferCalculationService calculationService;

    public TransferPreviewResponse preview(TransferPreviewRequest request) {
        return prepare(request).preview();
    }

    public TransferPreviewResponse previewPhone(PhoneTransferPreviewRequest request) {
        return preparePhone(request).preview();
    }

    @Transactional
    public TransferResponse create(TransferRequest request) {
        return create(prepare(new TransferPreviewRequest(
                request.cardFrom(),
                request.cardTo(),
                request.amount(),
                request.message()
        )));
    }

    @Transactional
    public TransferResponse createPhone(PhoneTransferRequest request) {
        return create(preparePhone(new PhoneTransferPreviewRequest(
                request.cardFrom(),
                request.phone(),
                request.amount(),
                request.message()
        )));
    }

    private TransferResponse create(TransferPreparationResponse preparation) {
        Transfer transfer = transferRepository.save(
                transferMapper.toEntity(
                        preparation,
                        preparation.preview().recipientPhone()
                )
        );

        outboxService.saveExecutionRequested(new BalanceOperationCommand(
                transfer.getId(),
                transfer.getFromClientId(),
                transfer.getToClientId(),
                transfer.getCardFrom(),
                transfer.getCardTo(),
                transfer.getDebitAmount(),
                transfer.getAmountTo(),
                transfer.getCurrency(),
                transfer.getTargetCurrency()
        ));
        return transferMapper.toResponse(transfer);
    }

    private TransferPreparationResponse prepare(TransferPreviewRequest request) {
        CardOperationContextResponse context = coreClient.getCardContext(
                new CardOperationContextRequest(request.cardFrom(), request.cardTo())
        );
        return prepare(context, request.amount(), null, request.message());
    }

    private TransferPreparationResponse preparePhone(PhoneTransferPreviewRequest request) {
        CardOperationContextResponse context = coreClient.getPhoneContext(
                new PhoneOperationContextRequest(request.cardFrom(), request.phone())
        );
        return prepare(context, request.amount(), request.phone(), request.message());
    }

    private TransferPreparationResponse prepare(
            CardOperationContextResponse context,
            java.math.BigDecimal amount,
            String recipientPhone,
            String message
    ) {
        TransferCalculationService.Calculation calculation = calculationService.calculate(
                amount,
                context.currency(),
                context.targetCurrency(),
                context.sourceRate(),
                context.targetRate(),
                true
        );
        validateFunds(context.sourceBalance(), calculation.debitAmount());
        TransferPreviewResponse preview = transferMapper.toPreviewResponse(
                context,
                amount,
                recipientPhone,
                message,
                calculation
        );
        return new TransferPreparationResponse(
                context.fromClientId(),
                context.toClientId(),
                context.sender(),
                preview
        );
    }

    private void validateFunds(
            java.math.BigDecimal balance,
            java.math.BigDecimal debitAmount
    ) {
        if (balance.compareTo(debitAmount) < 0) {
            throw new TransferException(CONFLICT, ErrorCode.ACCOUNT_INSUFFICIENT_FUNDS);
        }
    }

    @Transactional
    public void applyExecutionResult(BalanceOperationResult result) {
        Transfer transfer = transferRepository.findByIdForUpdate(result.operationId()).orElse(null);
        if (transfer == null || transfer.getStatus() != TransferStatus.IN_PROGRESS) {
            return;
        }
        transfer.setStatus(result.successful() ? TransferStatus.SUCCESS : TransferStatus.FAILED);
        transfer.setFailureCode(result.failureCode());
    }

    @Transactional(readOnly = true)
    public TransferResponse findById(UUID id) {
        UUID clientId = coreClient.getProfile().id();
        return transferRepository.findByIdAndFromClientId(id, clientId)
                .map(transferMapper::toResponse)
                .orElseThrow(() -> notFound(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<TransferHistoryResponse> history(Pageable pageable) {
        UUID clientId = coreClient.getProfile().id();
        List<TransferHistoryResponse> history = new ArrayList<>();

        transferRepository.findHistory(clientId).stream()
                .map(transfer -> transferMapper.toHistoryResponse(transfer, clientId))
                .forEach(history::add);

        accountTransferRepository.findAllByClientIdOrderByTimeOfTransferDesc(clientId).stream()
                .map(transferMapper::toHistoryResponse)
                .forEach(history::add);

        history.sort(Comparator.comparing(
                TransferHistoryResponse::timeOfTransfer,
                Comparator.nullsLast(Comparator.reverseOrder())));

        int start = Math.min((int) pageable.getOffset(), history.size());
        int end = Math.min(start + pageable.getPageSize(), history.size());
        return PageResponse.from(new PageImpl<>(history.subList(start, end), pageable, history.size()));
    }

    @Transactional(readOnly = true)
    public TransferHistoryResponse historyDetails(UUID id) {
        UUID clientId = coreClient.getProfile().id();

        Optional<TransferHistoryResponse> cardTransfer = transferRepository
                .findByIdAndParticipant(id, clientId)
                .map(transfer -> transferMapper.toHistoryResponse(transfer, clientId));

        return cardTransfer.orElseGet(() -> accountTransferRepository
                .findByIdAndClientId(id, clientId)
                .map(transferMapper::toHistoryResponse)
                .orElseThrow(() -> notFound(id)));
    }

    private TransferException notFound(UUID id) {
        return new TransferException(HttpStatus.NOT_FOUND, ErrorCode.TRANSFER_NOT_FOUND, id);
    }
}
