package com.pers.transfer.dto;

import com.pers.transfer.dto.request.AccountTransferRequest;
import com.pers.transfer.dto.request.TransferRequest;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationMessagesTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");

        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.setValidationMessageSource(messageSource);
        factory.afterPropertiesSet();
        validator = factory;
    }

    @Test
    void resolvesRequiredAmountMessageFromProperties() {
        TransferRequest request = new TransferRequest(
                "1111222233334444",
                "5555666677778888",
                null,
                null
        );

        assertThat(validator.validate(request))
                .extracting(violation -> violation.getMessage())
                .contains("Укажите сумму перевода");
    }

    @Test
    void resolvesPositiveAmountMessageFromProperties() {
        AccountTransferRequest request = new AccountTransferRequest(
                java.util.UUID.randomUUID(),
                java.util.UUID.randomUUID(),
                BigDecimal.ZERO
        );

        assertThat(validator.validate(request))
                .extracting(violation -> violation.getMessage())
                .contains("Сумма перевода должна быть больше нуля");
    }
}
