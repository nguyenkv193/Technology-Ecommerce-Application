-- =========================================================
-- FLYWAY MIGRATION V14: AVATAR NGƯỜI DÙNG
-- Lưu URL ảnh hoặc Data URL (ảnh đã nén 256x256 phía client)
-- =========================================================
ALTER TABLE users ADD COLUMN IF NOT EXISTS avatar_url TEXT;
