package com.pers.transfer.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final ErrorCode errorCode;
    private final Object[] messageArguments;

    public BusinessException(HttpStatus status, ErrorCode errorCode, Object... messageArguments) {
        super(errorCode.name());
        this.status = status;
        this.errorCode = errorCode;
        this.messageArguments = messageArguments;
    }

    public BusinessException(
            HttpStatus status,
            ErrorCode errorCode,
            Throwable cause,
            Object... messageArguments
    ) {
        super(errorCode.name(), cause);
        this.status = status;
        this.errorCode = errorCode;
        this.messageArguments = messageArguments;
    }
}
