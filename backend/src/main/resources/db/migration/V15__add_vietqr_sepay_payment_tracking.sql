-- =========================================================
-- FLYWAY MIGRATION V15: VIETQR & SEPAY WEBHOOK TRACKING
-- =========================================================

-- 1. Thêm thời hạn thanh toán và thời điểm thanh toán vào bảng orders
ALTER TABLE orders
    ADD COLUMN IF NOT EXISTS payment_expires_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN IF NOT EXISTS paid_at TIMESTAMP WITH TIME ZONE;

CREATE INDEX IF NOT EXISTS idx_orders_payment_expires_at
    ON orders(payment_expires_at)
    WHERE payment_status = 'PENDING' AND order_status = 'PENDING';

-- 2. Bảng lưu lịch sử webhook thanh toán (chống xử lý trùng idempotency & phục vụ đối soát)
CREATE TABLE IF NOT EXISTS payment_transactions (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT,
    gateway VARCHAR(30) NOT NULL DEFAULT 'SEPAY',
    gateway_transaction_id VARCHAR(100) NOT NULL UNIQUE,
    transfer_type VARCHAR(20),
    transfer_amount NUMERIC(15, 2) NOT NULL DEFAULT 0,
    content TEXT,
    account_number VARCHAR(50),
    reference_code VARCHAR(100),
    status VARCHAR(50) NOT NULL,
    raw_payload TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_transactions_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_payment_transactions_order_id ON payment_transactions(order_id);
CREATE INDEX IF NOT EXISTS idx_payment_transactions_status ON payment_transactions(status);
