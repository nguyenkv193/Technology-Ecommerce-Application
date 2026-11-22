-- =========================================================
-- FLYWAY MIGRATION V13: BANNER MANAGEMENT SCHEMA
-- Hệ thống: TechStore E-Commerce & Admin CMS
-- =========================================================

-- 1. BẢNG BANNERS (Quản lý Banner quảng cáo / Hero Slider / Khuyến mãi)
CREATE TABLE IF NOT EXISTS banners (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,                          -- Tiêu đề banner (quản trị viên theo dõi & làm alt text cho SEO)
    image_url VARCHAR(500) NOT NULL,                      -- URL ảnh banner Desktop
    mobile_image_url VARCHAR(500),                        -- URL ảnh cho Mobile (tùy chọn)
    link_url VARCHAR(500) NOT NULL,                       -- Đường dẫn khi click (vd: /products/1, /products?category=Laptop)
    target_type VARCHAR(20) NOT NULL DEFAULT '_self',     -- '_self' hoặc '_blank'
    position VARCHAR(50) NOT NULL DEFAULT 'HERO_SLIDER',  -- HERO_SLIDER, SIDE_PROMO, BOTTOM_CARD, POPUP
    display_order INT NOT NULL DEFAULT 0,                 -- Thứ tự hiển thị (số nhỏ xếp trước)
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',         -- 'ACTIVE' hoặc 'INACTIVE'
    start_date TIMESTAMP WITH TIME ZONE,                  -- Hẹn giờ bắt đầu hiển thị
    end_date TIMESTAMP WITH TIME ZONE,                    -- Hẹn giờ kết thúc hiển thị
    click_count BIGINT NOT NULL DEFAULT 0,                -- Thống kê số lượt click
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index tối ưu truy vấn storefront cho banner theo vị trí và trạng thái
CREATE INDEX IF NOT EXISTS idx_banners_pos_status_order 
    ON banners(position, status, display_order);

CREATE INDEX IF NOT EXISTS idx_banners_schedule 
    ON banners(start_date, end_date);

-- 2. SEED DỮ LIỆU BANNER MẪU CHO HERO SLIDER & SIDE PROMO
INSERT INTO banners (title, image_url, mobile_image_url, link_url, target_type, position, display_order, status)
VALUES
(
    'MacBook Pro 14 M3 Pro - Thiết Kế Đỉnh Cao Hiệu Năng Vượt Bậc',
    'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=1200&auto=format&fit=crop&q=80',
    'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600&auto=format&fit=crop&q=80',
    '/products/3',
    '_self',
    'HERO_SLIDER',
    1,
    'ACTIVE'
),
(
    'iPhone 16 Pro Max - Quyền Năng Bứt Phá Chuẩn Mực Tương Lai',
    'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=1200&auto=format&fit=crop&q=80',
    'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=600&auto=format&fit=crop&q=80',
    '/products/1',
    '_self',
    'HERO_SLIDER',
    2,
    'ACTIVE'
),
(
    'ASUS ROG Zephyrus G16 - Cỗ Máy Chiến Binh Đồ Họa & AI Tối Thượng',
    'https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=1200&auto=format&fit=crop&q=80',
    'https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=600&auto=format&fit=crop&q=80',
    '/products/5',
    '_self',
    'HERO_SLIDER',
    3,
    'ACTIVE'
),
(
    'Sony WH-1000XM5 Wireless - Tĩnh Lặng Tuyệt Đối Âm Thanh Chuẩn Studio',
    'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=1200&auto=format&fit=crop&q=80',
    'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80',
    '/products/7',
    '_self',
    'HERO_SLIDER',
    4,
    'ACTIVE'
),
(
    'Ưu Đãi Học Sinh - Sinh Viên Giảm Thêm 5%',
    'https://images.unsplash.com/photo-1523240795612-9a054b0db644?w=600&auto=format&fit=crop&q=80',
    NULL,
    '/products?keyword=Hoc+sinh+sinh+vien',
    '_self',
    'SIDE_PROMO',
    1,
    'ACTIVE'
),
(
    'Khuyến Mãi Tháng Giảm Đến 50% Xả Kho Phụ Kiện',
    'https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=600&auto=format&fit=crop&q=80',
    NULL,
    '/products?keyword=Khuyen+mai',
    '_self',
    'SIDE_PROMO',
    2,
    'ACTIVE'
);
