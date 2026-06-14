package com.pers.transfer.exception;

import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ProblemDetail> handleBusiness(BusinessException exception) {
        String detail = messageSource.getMessage(
                exception.getErrorCode().getKey(),
                exception.getMessageArguments(),
                exception.getErrorCode().getKey(),
                LocaleContextHolder.getLocale()
        );
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(exception.getStatus(), detail);
        problem.setProperty("code", exception.getErrorCode().name());
        return ResponseEntity.status(exception.getStatus()).body(problem);
    }

    @ExceptionHandler(RemoteServiceException.class)
    ResponseEntity<ProblemDetail> handleRemote(RemoteServiceException exception) {
        String detail = exception.getDetail();
        if (detail == null || detail.isBlank()) {
            detail = messageSource.getMessage(
                    ErrorCode.REMOTE_REQUEST_FAILED.getKey(),
                    null,
                    LocaleContextHolder.getLocale()
            );
        }
        return ResponseEntity.status(exception.getStatus())
                .body(ProblemDetail.forStatusAndDetail(exception.getStatus(), detail));
    }

    @ExceptionHandler(RetryableException.class)
    ResponseEntity<ProblemDetail> handleUnavailable(RetryableException exception) {
        String detail = messageSource.getMessage(
                ErrorCode.REMOTE_SERVICE_UNAVAILABLE.getKey(),
                null,
                LocaleContextHolder.getLocale()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, detail));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException exception) {
        List<String> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage() == null
                        ? error.getField()
                        : error.getDefaultMessage())
                .distinct()
                .toList();
        String detail = errors.isEmpty()
                ? messageSource.getMessage(
                        "validation.failed",
                        null,
                        LocaleContextHolder.getLocale()
                )
                : errors.get(0);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problem.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(problem);
    }
}
