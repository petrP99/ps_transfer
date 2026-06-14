package com.pers.transfer.client;

import com.pers.transfer.config.FeignConfig;
import com.pers.transfer.dto.request.AccountBalanceOperationRequest;
import com.pers.transfer.dto.request.AccountOperationContextRequest;
import com.pers.transfer.dto.response.AccountOperationContextResponse;
import com.pers.transfer.dto.response.CardResponse;
import com.pers.transfer.dto.request.CardOperationContextRequest;
import com.pers.transfer.dto.response.CardOperationContextResponse;
import com.pers.transfer.dto.response.ClientResponse;
import com.pers.transfer.dto.request.PhoneOperationContextRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "core-service",
        url = "${services.core.url}",
        configuration = FeignConfig.class)
public interface CoreBankingClient {

    @GetMapping("/api/v1/client/profile")
    ClientResponse getProfile();

    @GetMapping("/api/v1/banking-operation-support/cards")
    List<CardResponse> getCards();

    @PostMapping("/api/v1/banking-operation-support/card-context")
    CardOperationContextResponse getCardContext(@RequestBody CardOperationContextRequest request);

    @PostMapping("/api/v1/banking-operation-support/phone-context")
    CardOperationContextResponse getPhoneContext(@RequestBody PhoneOperationContextRequest request);

    @PostMapping("/api/v1/banking-operation-support/account-context")
    AccountOperationContextResponse getAccountContext(@RequestBody AccountOperationContextRequest request);

    @PostMapping("/api/v1/banking-operation-support/account-execute")
    void executeAccountOperation(@RequestBody AccountBalanceOperationRequest request);
}
