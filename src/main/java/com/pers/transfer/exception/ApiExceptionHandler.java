package com.pers.transfer.exception;

import feign.RetryableException;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
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
    private final Tracer tracer;

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
        addTraceId(problem);
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
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(exception.getStatus(), detail);
        addTraceId(problem);
        return ResponseEntity.status(exception.getStatus()).body(problem);
    }

    @ExceptionHandler(RetryableException.class)
    ResponseEntity<ProblemDetail> handleUnavailable(RetryableException exception) {
        String detail = messageSource.getMessage(
                ErrorCode.REMOTE_SERVICE_UNAVAILABLE.getKey(),
                null,
                LocaleContextHolder.getLocale()
        );
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, detail);
        addTraceId(problem);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(problem);
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
        addTraceId(problem);
        return ResponseEntity.badRequest().body(problem);
    }

    private void addTraceId(ProblemDetail problem) {
        Span span = tracer.currentSpan();
        if (span != null) {
            problem.setProperty("traceId", span.context().traceId());
        }
    }
}
