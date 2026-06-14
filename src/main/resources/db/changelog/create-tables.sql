--liquibase formatted sql

--changeset ps-transfer:create-tables
CREATE TABLE transfer
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    from_client_id      UUID           NOT NULL,
    to_client_id        UUID           NOT NULL,
    card_from           VARCHAR(16)    NOT NULL,
    card_to             VARCHAR(16)    NOT NULL,
    amount              NUMERIC(19, 2) NOT NULL,
    amount_to           NUMERIC(19, 2) NOT NULL,
    exchange_rate       NUMERIC(19, 6) NOT NULL,
    commission          NUMERIC(19, 2) NOT NULL,
    debit_amount        NUMERIC(19, 2) NOT NULL,
    currency            VARCHAR(10)    NOT NULL,
    target_currency     VARCHAR(10)    NOT NULL,
    status              VARCHAR(20)    NOT NULL,
    time_of_transfer    TIMESTAMP      NOT NULL,
    sender              VARCHAR(120)   NOT NULL,
    recipient           VARCHAR(120)   NOT NULL,
    recipient_phone     VARCHAR(20),
    message             VARCHAR(120),
    failure_code        VARCHAR(80)
);

CREATE INDEX idx_transfer_from_client_time
    ON transfer (from_client_id, time_of_transfer DESC);
CREATE INDEX idx_transfer_to_client_time
    ON transfer (to_client_id, time_of_transfer DESC);

CREATE TABLE account_transfer
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id           UUID           NOT NULL,
    account_from        UUID           NOT NULL,
    account_from_name   VARCHAR(255)   NOT NULL,
    account_to          UUID           NOT NULL,
    account_to_name     VARCHAR(255)   NOT NULL,
    amount              NUMERIC(19, 2) NOT NULL,
    amount_to           NUMERIC(19, 2) NOT NULL,
    exchange_rate       NUMERIC(19, 6) NOT NULL,
    currency            VARCHAR(10)    NOT NULL,
    target_currency     VARCHAR(10)    NOT NULL,
    time_of_transfer    TIMESTAMP      NOT NULL
);

CREATE INDEX idx_account_transfer_client_time
    ON account_transfer (client_id, time_of_transfer DESC);

CREATE TABLE outbox_event
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_id    UUID          NOT NULL,
    event_type      VARCHAR(50)   NOT NULL,
    event_key       VARCHAR(100)  NOT NULL,
    payload         TEXT          NOT NULL,
    status          VARCHAR(20)   NOT NULL,
    attempts        INTEGER       NOT NULL DEFAULT 0,
    created_at      TIMESTAMP     NOT NULL,
    next_attempt_at TIMESTAMP     NOT NULL,
    published_at    TIMESTAMP,
    last_error      VARCHAR(1000)
);

CREATE INDEX idx_transfer_outbox_ready
    ON outbox_event (status, next_attempt_at, created_at);
CREATE INDEX idx_transfer_outbox_aggregate
    ON outbox_event (aggregate_id);

--rollback DROP TABLE outbox_event; DROP TABLE account_transfer; DROP TABLE transfer;
