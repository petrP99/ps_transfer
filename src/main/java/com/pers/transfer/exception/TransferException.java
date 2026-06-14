package com.pers.transfer.exception;

import org.springframework.http.HttpStatus;

public class TransferException extends BusinessException {

    public TransferException(HttpStatus status, ErrorCode errorCode, Object... arguments) {
        super(status, errorCode, arguments);
    }
}
