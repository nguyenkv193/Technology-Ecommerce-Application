ALTER TABLE product_variants
    ADD COLUMN color_name VARCHAR(100),
    ADD COLUMN color_hex VARCHAR(7),
    ADD COLUMN reward_points BIGINT;

CREATE TABLE product_storefront_contents (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE REFERENCES products(id) ON DELETE CASCADE,
    content JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
