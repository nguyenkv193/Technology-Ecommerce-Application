-- =========================================================
-- FLYWAY MIGRATION V4: REVIEWS & USER BEHAVIORS LOG SCHEMA
-- Hệ thống: TechStore E-Commerce & AI Recommendation
-- =========================================================

-- 1. BẢNG PRODUCT_REVIEWS (Đánh giá & Nhận xét sản phẩm)
CREATE TABLE IF NOT EXISTS product_reviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT uq_review_user_product UNIQUE (user_id, product_id)
);

CREATE INDEX IF NOT EXISTS idx_reviews_product_id ON product_reviews(product_id);
CREATE INDEX IF NOT EXISTS idx_reviews_user_id ON product_reviews(user_id);
CREATE INDEX IF NOT EXISTS idx_reviews_rating ON product_reviews(rating);

-- 2. BẢNG USER_BEHAVIORS (Thu thập dữ liệu hành vi người dùng cho AI Recommendation)
-- Các loại hành vi: VIEW (xem sản phẩm), SEARCH (tìm kiếm), ADD_TO_CART, WISHLIST, PURCHASE, RATING
CREATE TABLE IF NOT EXISTS user_behaviors (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    session_id VARCHAR(100),
    product_id BIGINT,
    action_type VARCHAR(50) NOT NULL,
    action_value VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_behaviors_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_behaviors_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_behaviors_user_action ON user_behaviors(user_id, action_type);
CREATE INDEX IF NOT EXISTS idx_behaviors_product_action ON user_behaviors(product_id, action_type);
CREATE INDEX IF NOT EXISTS idx_behaviors_created_at ON user_behaviors(created_at);
CREATE INDEX IF NOT EXISTS idx_behaviors_session ON user_behaviors(session_id);
