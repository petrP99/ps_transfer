package com.pers.transfer.http;

import com.pers.transfer.dto.request.AccountTransferRequest;
import com.pers.transfer.dto.response.AccountTransferResponse;
import com.pers.transfer.service.AccountTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
        return ResponseEntity.ok(service.transfer(request));
    }
}
