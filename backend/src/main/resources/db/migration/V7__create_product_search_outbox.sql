CREATE TABLE product_search_outbox (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    event_type VARCHAR(16) NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    next_attempt_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    processed_at TIMESTAMPTZ,
    last_error VARCHAR(2000),
    CONSTRAINT chk_product_search_outbox_event_type
        CHECK (event_type IN ('UPSERT', 'DELETE'))
);

CREATE INDEX idx_product_search_outbox_pending
    ON product_search_outbox (next_attempt_at, id)
    WHERE processed_at IS NULL;
