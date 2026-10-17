CREATE INDEX IF NOT EXISTS idx_behaviors_user_product_created
    ON user_behaviors (user_id, product_id, created_at DESC);
