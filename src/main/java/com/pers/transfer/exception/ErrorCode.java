package com.pers.transfer.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    TRANSFER_NOT_FOUND("transfer.not.found"),
    ACCOUNT_INSUFFICIENT_FUNDS("account.insufficient.funds"),
    REMOTE_SERVICE_UNAVAILABLE("transfer.remote.service.unavailable"),
    REMOTE_REQUEST_FAILED("transfer.remote.request.failed"),
    OUTBOX_SERIALIZE_FAILED("outbox.serialize.failed");

    private final String key;

    ErrorCode(String key) {
        this.key = key;
    }
}
