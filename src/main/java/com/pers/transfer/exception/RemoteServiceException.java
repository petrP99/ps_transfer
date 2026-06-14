package com.pers.transfer.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RemoteServiceException extends RuntimeException {

    private final HttpStatus status;
    private final String detail;

    public RemoteServiceException(HttpStatus status, String detail) {
        this.status = status == null ? HttpStatus.BAD_GATEWAY : status;
        this.detail = detail;
    }
}
