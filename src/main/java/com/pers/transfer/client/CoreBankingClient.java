package com.pers.transfer.client;

import com.pers.transfer.config.FeignConfig;
import com.pers.transfer.dto.request.AccountBalanceOperationRequest;
import com.pers.transfer.dto.request.AccountOperationContextRequest;
import com.pers.transfer.dto.response.AccountOperationContextResponse;
import com.pers.transfer.dto.request.CardOperationContextRequest;
import com.pers.transfer.dto.response.CardOperationContextResponse;
import com.pers.transfer.dto.response.ClientResponse;
import com.pers.transfer.dto.request.PhoneOperationContextRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "core-service",
        url = "${services.core.url}",
        configuration = FeignConfig.class)
public interface CoreBankingClient {

    @GetMapping("/api/v1/client/profile")
    ClientResponse getProfile();

    @PostMapping("/api/v1/internal/transfers/card-context")
    CardOperationContextResponse getCardContext(@RequestBody CardOperationContextRequest request);

    @PostMapping("/api/v1/internal/transfers/phone-context")
    CardOperationContextResponse getPhoneContext(@RequestBody PhoneOperationContextRequest request);

    @PostMapping("/api/v1/internal/transfers/account-context")
    AccountOperationContextResponse getAccountContext(@RequestBody AccountOperationContextRequest request);

    @PostMapping("/api/v1/internal/transfers/account-execute")
    void executeAccountOperation(@RequestBody AccountBalanceOperationRequest request);
}
