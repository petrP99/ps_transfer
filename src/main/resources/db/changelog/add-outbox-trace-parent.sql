--liquibase formatted sql

--changeset ps-transfer:add-outbox-trace-parent
ALTER TABLE outbox_event
    ADD COLUMN IF NOT EXISTS trace_parent VARCHAR(128);

--rollback ALTER TABLE outbox_event DROP COLUMN IF EXISTS trace_parent;
