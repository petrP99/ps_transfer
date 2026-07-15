package com.pers.transfer.http;

import com.pers.transfer.dto.request.AccountTransferRequest;
import com.pers.transfer.dto.response.AccountTransferResponse;
import com.pers.transfer.service.AccountTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/account-transfers")
public class AccountTransferController {

    private final AccountTransferService service;

    @PostMapping("/preview")
    ResponseEntity<AccountTransferResponse> preview(@Valid @RequestBody AccountTransferRequest request) {
        return ResponseEntity.ok(service.preview(request));
    }

    @PostMapping
    ResponseEntity<AccountTransferResponse> transfer(@Valid @RequestBody AccountTransferRequest request) {
        log.info(
                "Получен запрос на перевод между счетами: accountFrom={}, accountTo={}, amount={}",
                request.accountFrom(),
                request.accountTo(),
                request.amount()
        );
        return ResponseEntity.ok(service.transfer(request));
    }
}
