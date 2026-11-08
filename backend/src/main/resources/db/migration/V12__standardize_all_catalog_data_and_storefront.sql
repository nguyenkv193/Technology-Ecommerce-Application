-- V12: Standardize all catalog data, variants, specs, reviews, and storefront contents
BEGIN;

-- 1. Update verified direct images for flagship products 1 to 12

DELETE FROM product_images WHERE product_id BETWEEN 1 AND 12;

-- Product 1: iPhone 16 Pro Max
INSERT INTO product_images (product_id, url, sort_order, is_thumbnail) VALUES
(1, 'https://cellphones.com.vn/media/catalog/product/i/p/iphone-16-pro-max.png', 0, true),
(1, 'https://cellphones.com.vn/media/catalog/product/i/p/iphone-16-pro-max-titan-sa-mac.png', 1, false),
(1, 'https://cellphones.com.vn/media/catalog/product/i/p/iphone-16-pro-max-titan-tu-nhien.png', 2, false),
(1, 'https://cellphones.com.vn/media/catalog/product/i/p/iphone-16-pro-max-titan-den.png', 3, false);

-- Product 2: Samsung Galaxy S24 Ultra
INSERT INTO product_images (product_id, url, sort_order, is_thumbnail) VALUES
(2, 'https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=800&auto=format&fit=crop&q=80', 0, true),
(2, 'https://salt.tikicdn.com/cache/w1200/ts/product/09/9c/64/aa9c5ceae2582d96add3b55ab4047f83.jpg', 1, false),
(2, 'https://salt.tikicdn.com/cache/w1200/ts/product/f5/92/6b/09987f41c0cbe5467f2532f06c176636.jpg', 2, false);

-- Product 3: MacBook Pro 14 M3 Pro
INSERT INTO product_images (product_id, url, sort_order, is_thumbnail) VALUES
(3, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&auto=format&fit=crop&q=80', 0, true),
(3, 'https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?w=800&auto=format&fit=crop&q=80', 1, false);

-- Product 4: Dell XPS 14 OLED
INSERT INTO product_images (product_id, url, sort_order, is_thumbnail) VALUES
(4, 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=800&auto=format&fit=crop&q=80', 0, true),
(4, 'https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=800&auto=format&fit=crop&q=80', 1, false);

-- Product 5: ASUS ROG Zephyrus G16
INSERT INTO product_images (product_id, url, sort_order, is_thumbnail) VALUES
(5, 'https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=800&auto=format&fit=crop&q=80', 0, true),
(5, 'https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=800&auto=format&fit=crop&q=80', 1, false);

-- Product 6: iPad Pro 11 M4
INSERT INTO product_images (product_id, url, sort_order, is_thumbnail) VALUES
(6, 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=800&auto=format&fit=crop&q=80', 0, true),
(6, 'https://salt.tikicdn.com/cache/w1200/ts/product/cb/a9/4c/48456b4f0f67bcb75829ed6243b4441f.jpg', 1, false);

-- Product 7: Sony WH-1000XM5
INSERT INTO product_images (product_id, url, sort_order, is_thumbnail) VALUES
(7, 'https://cellphones.com.vn/media/catalog/product/t/a/tai-nghe-chup-tai-sony-wh-1000xm5.png', 0, true),
(7, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&auto=format&fit=crop&q=80', 1, false);

-- Product 8: ASUS ROG Strix GeForce RTX 4090
INSERT INTO product_images (product_id, url, sort_order, is_thumbnail) VALUES
(8, 'https://images.unsplash.com/photo-1591488320449-011701bb6704?w=800&auto=format&fit=crop&q=80', 0, true),
(8, 'https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=800&auto=format&fit=crop&q=80', 1, false);

-- Product 9: Apple Watch Ultra 2
INSERT INTO product_images (product_id, url, sort_order, is_thumbnail) VALUES
(9, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&auto=format&fit=crop&q=80', 0, true),
(9, 'https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=800&auto=format&fit=crop&q=80', 1, false);

-- Product 10: Samsung Galaxy Z Fold6
INSERT INTO product_images (product_id, url, sort_order, is_thumbnail) VALUES
(10, 'https://images.unsplash.com/photo-1580910051074-3eb694886505?w=800&auto=format&fit=crop&q=80', 0, true),
(10, 'https://salt.tikicdn.com/cache/w1200/ts/product/09/9c/64/aa9c5ceae2582d96add3b55ab4047f83.jpg', 1, false);

-- Product 11: Chuot Logitech MX Master 3S
INSERT INTO product_images (product_id, url, sort_order, is_thumbnail) VALUES
(11, 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=800&auto=format&fit=crop&q=80', 0, true),
(11, 'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=800&auto=format&fit=crop&q=80', 1, false);

-- Product 12: Ban phim Logitech MX Mechanical
INSERT INTO product_images (product_id, url, sort_order, is_thumbnail) VALUES
(12, 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&auto=format&fit=crop&q=80', 0, true),
(12, 'https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?w=800&auto=format&fit=crop&q=80', 1, false);

-- 2. Standardize all product variants with realistic colors, original prices and reward points

-- Update original prices and reward points where missing or identical to price
UPDATE product_variants
SET 
  original_price = ROUND(price * 1.12, -4),
  reward_points = GREATEST(ROUND(price / 1000)::integer, 100),
  stock = CASE WHEN stock < 15 THEN 35 ELSE stock END,
  status = 'ACTIVE'
WHERE original_price IS NULL OR original_price <= price OR reward_points IS NULL OR reward_points = 0;

-- Enrich default variant names and colors for products 304 to 1305
WITH numbered_variants AS (
  SELECT 
    id, 
    product_id,
    ROW_NUMBER() OVER(PARTITION BY product_id ORDER BY id) as rn
  FROM product_variants
  WHERE product_id >= 304 AND (color_name IS NULL OR color_name = '' OR name = 'Mac dinh')
)
UPDATE product_variants pv
SET 
  color_name = CASE (nv.rn % 6)
    WHEN 1 THEN 'Xám Không Gian'
    WHEN 2 THEN 'Bạc Ánh Kim'
    WHEN 3 THEN 'Đen Huyền Bí'
    WHEN 4 THEN 'Xanh Navy'
    WHEN 5 THEN 'Trắng Tinh Khôi'
    ELSE 'Titan Sa Mạc'
  END,
  color_hex = CASE (nv.rn % 6)
    WHEN 1 THEN '#4B4846'
    WHEN 2 THEN '#E2E4E1'
    WHEN 3 THEN '#1A1A1A'
    WHEN 4 THEN '#1E3A8A'
    WHEN 5 THEN '#F8FAFC'
    ELSE '#C4A582'
  END,
  name = CASE (nv.rn % 6)
    WHEN 1 THEN 'Bản Tiêu Chuẩn - Xám Không Gian'
    WHEN 2 THEN 'Bản Nâng Cấp - Bạc Ánh Kim'
    WHEN 3 THEN 'Bản Đen Huyền Bí'
    WHEN 4 THEN 'Bản Xanh Navy'
    WHEN 5 THEN 'Bản Trắng Tinh Khôi'
    ELSE 'Bản Titan Sa Mạc'
  END
FROM numbered_variants nv
WHERE pv.id = nv.id;

-- 3. Enrich comprehensive technical attributes for products that lack them

-- Remove non-technical generic placeholder attributes
DELETE FROM product_attributes 
WHERE product_id >= 304 AND name IN ('Sản phẩm có được bảo hành không?', 'Có thuế VAT', 'Hình thức bảo hành');

-- Category 1: Laptop
INSERT INTO product_attributes (product_id, name, value)
SELECT p.id, attr.name, attr.value
FROM products p
CROSS JOIN (VALUES
  ('Màn hình', '15.6 inch IPS FHD/QHD 144Hz, Viền siêu mỏng chống chói'),
  ('Chip vi xử lý (CPU)', 'Intel Core i7 / AMD Ryzen 7 thế hệ mới đa nhân tốc độ cao'),
  ('Card đồ họa (GPU)', 'NVIDIA GeForce RTX hoặc Đồ họa tích hợp thế hệ mới'),
  ('Bộ nhớ RAM', '16GB DDR5 Bus 5600MHz (Hỗ trợ nâng cấp đến 64GB)'),
  ('Ổ cứng lưu trữ', '512GB SSD M.2 NVMe PCIe 4.0 siêu tốc'),
  ('Cổng kết nối', 'Thunderbolt 4 / Type-C PD, HDMI 2.1, USB 3.2 Gen 2, Jack 3.5mm'),
  ('Dung lượng pin', '75Wh, Hỗ trợ sạc nhanh Type-C 100W'),
  ('Hệ điều hành', 'Windows 11 Home bản quyền cài sẵn'),
  ('Bảo hành', '24 Tháng chính hãng, 1 đổi 1 trong 30 ngày')
) AS attr(name, value)
WHERE p.category_id = 1 AND p.id >= 304
  AND NOT EXISTS (SELECT 1 FROM product_attributes pa WHERE pa.product_id = p.id AND pa.name = 'Màn hình');

-- Category 2: Điện thoại
INSERT INTO product_attributes (product_id, name, value)
SELECT p.id, attr.name, attr.value
FROM products p
CROSS JOIN (VALUES
  ('Màn hình', '6.7 inch AMOLED / Super Retina XDR, Tần số quét 120Hz ProMotion'),
  ('Độ phân giải', 'FHD+ / 1.5K sắc nét, Độ sáng tối đa 2.000 nits'),
  ('Chip vi xử lý (CPU)', 'Chip AI 8 nhân tiến trình 4nm xử lý tác vụ thông minh'),
  ('Bộ nhớ RAM', '8GB / 12GB LPDDR5X đa nhiệm mượt mà'),
  ('Bộ nhớ trong', '256GB UFS 4.0 lưu trữ tốc độ cao'),
  ('Camera sau', 'Chính 50MP OIS + Góc siêu rộng 12MP + Telephoto zoom sắc nét'),
  ('Camera trước', '16MP - 32MP Chân dung AI hỗ trợ tự động lấy nét'),
  ('Dung lượng pin & Sạc', '5.000 mAh, Sạc siêu nhanh 67W - 120W'),
  ('Kháng nước & Bụi', 'IP68 / IP65 kháng nước an toàn'),
  ('Hệ điều hành', 'Android 14 / iOS 18 tích hợp tính năng AI thông minh'),
  ('Bảo hành', '12 Tháng chính hãng tại trung tâm ủy quyền toàn quốc')
) AS attr(name, value)
WHERE p.category_id = 2 AND p.id >= 304
  AND NOT EXISTS (SELECT 1 FROM product_attributes pa WHERE pa.product_id = p.id AND pa.name = 'Màn hình');

-- Category 3: Máy tính bảng
INSERT INTO product_attributes (product_id, name, value)
SELECT p.id, attr.name, attr.value
FROM products p
CROSS JOIN (VALUES
  ('Màn hình', '11 inch 2.8K Liquid Retina 120Hz độ phủ màu DCI-P3'),
  ('Chip vi xử lý', 'Chip hiệu năng cao tối ưu đồ họa và đa nhiệm học tập / làm việc'),
  ('RAM', '8GB RAM đa nhiệm mượt mà'),
  ('Bộ nhớ lưu trữ', '128GB / 256GB lưu trữ thoải mái'),
  ('Hỗ trợ phụ kiện', 'Bút cảm ứng Stylus độ trễ siêu thấp và Bàn phím từ tính'),
  ('Dung lượng pin', '8.500 mAh, Xem video liên tục đến 12 giờ'),
  ('Bảo hành', '12 Tháng chính hãng toàn quốc')
) AS attr(name, value)
WHERE p.category_id = 3 AND p.id >= 304
  AND NOT EXISTS (SELECT 1 FROM product_attributes pa WHERE pa.product_id = p.id AND pa.name = 'Màn hình');

-- Category 4 & 23: Tai nghe / Âm thanh
INSERT INTO product_attributes (product_id, name, value)
SELECT p.id, attr.name, attr.value
FROM products p
CROSS JOIN (VALUES
  ('Công nghệ chống ồn', 'Chống ồn chủ động thích ứng (Adaptive ANC) Hybrid thế hệ mới'),
  ('Chuẩn Bluetooth', 'Bluetooth 5.3 / 5.4, Hỗ trợ Codec Hi-Res LDAC / AAC'),
  ('Thời lượng pin', 'Lên đến 35 - 50 giờ sử dụng kèm hộp sạc, Sạc nhanh 10 phút dùng 4 giờ'),
  ('Kháng nước', 'Chuẩn IPX4 / IP54 kháng mồ hôi tập luyện thể thao'),
  ('Đàm thoại', 'Hệ thống 4 micro Beamforming lọc gió và khử tạp âm AI'),
  ('Bảo hành', '12 Tháng chính hãng 1 đổi 1')
) AS attr(name, value)
WHERE p.category_id IN (4, 23) AND p.id >= 304
  AND NOT EXISTS (SELECT 1 FROM product_attributes pa WHERE pa.product_id = p.id AND pa.name = 'Chuẩn Bluetooth');

-- Category 19: Dong Ho Thong Minh
INSERT INTO product_attributes (product_id, name, value)
SELECT p.id, attr.name, attr.value
FROM products p
CROSS JOIN (VALUES
  ('Màn hình', 'AMOLED 1.5 - 1.9 inch Always-On Display, Kính cường lực chống xước'),
  ('Theo dõi sức khỏe', 'Cảm biến nhịp tim 24/7, SpO2 nồng độ oxy, Điện tâm đồ ECG, Theo dõi giấc ngủ AI'),
  ('Chế độ luyện tập', '100+ bài tập thể thao, Định vị GPS độc lập băng tần kép'),
  ('Chống nước', 'Chuẩn 5ATM / IP68 (chịu áp lực nước 50m an toàn bơi lội)'),
  ('Thời lượng pin', '5 - 14 ngày sử dụng tiêu chuẩn, Sạc nhanh từ tính'),
  ('Tương thích', 'Hỗ trợ đồng bộ Android và iOS'),
  ('Bảo hành', '12 Tháng chính hãng')
) AS attr(name, value)
WHERE p.category_id = 19 AND p.id >= 304
  AND NOT EXISTS (SELECT 1 FROM product_attributes pa WHERE pa.product_id = p.id AND pa.name = 'Theo dõi sức khỏe');

-- Category 21: Man Hinh
INSERT INTO product_attributes (product_id, name, value)
SELECT p.id, attr.name, attr.value
FROM products p
CROSS JOIN (VALUES
  ('Kích thước & Tấm nền', '27 inch Fast IPS góc nhìn rộng 178 độ'),
  ('Độ phân giải', '2K QHD (2560 x 1440) hoặc 4K UHD siêu chi tiết'),
  ('Tần số quét', '165Hz - 180Hz, Thời gian phản hồi siêu tốc 1ms (GtG)'),
  ('Độ chuẩn màu', '100% sRGB, 95% DCI-P3 chuẩn màu thiết kế đồ họa'),
  ('Cổng kết nối', 'HDMI 2.0 / 2.1, DisplayPort 1.4, Audio Out'),
  ('Công nghệ đồng bộ', 'AMD FreeSync Premium, NVIDIA G-Sync Compatible'),
  ('Bảo hành', '36 Tháng chính hãng')
) AS attr(name, value)
WHERE p.category_id = 21 AND p.id >= 304
  AND NOT EXISTS (SELECT 1 FROM product_attributes pa WHERE pa.product_id = p.id AND pa.name = 'Tần số quét');

-- Category 27: May In
INSERT INTO product_attributes (product_id, name, value)
SELECT p.id, attr.name, attr.value
FROM products p
CROSS JOIN (VALUES
  ('Loại máy in', 'Laser đơn năng / Đa năng (In, Scan, Copy)'),
  ('Tốc độ in', '25 - 30 trang/phút, In 2 mặt tự động Duplex'),
  ('Độ phân giải', '1200 x 1200 dpi chất lượng bản in sắc nét'),
  ('Kết nối', 'Wi-Fi không dây, USB 2.0, In di động qua Apple AirPrint'),
  ('Khổ giấy hỗ trợ', 'A4, A5, Letter, Bì thư'),
  ('Bảo hành', '12 Tháng chính hãng')
) AS attr(name, value)
WHERE p.category_id = 27 AND p.id >= 304
  AND NOT EXISTS (SELECT 1 FROM product_attributes pa WHERE pa.product_id = p.id AND pa.name = 'Tốc độ in');

-- Category 5: Linh kiện & Phụ kiện
INSERT INTO product_attributes (product_id, name, value)
SELECT p.id, attr.name, attr.value
FROM products p
CROSS JOIN (VALUES
  ('Chuẩn kết nối', 'USB-C PD 3.0 / Bluetooth 5.3 / Wireless 2.4GHz'),
  ('Chất liệu', 'Hợp kim nhôm hoặc Nhựa ABS chống cháy cao cấp'),
  ('Tính năng nổi bật', 'Hiệu suất cao, Thiết kế công thái học hiện đại'),
  ('Tương thích', 'Windows, macOS, Android, iOS'),
  ('Bảo hành', '12 - 24 Tháng chính hãng 1 đổi 1')
) AS attr(name, value)
WHERE p.category_id = 5 AND p.id >= 304
  AND NOT EXISTS (SELECT 1 FROM product_attributes pa WHERE pa.product_id = p.id AND pa.name = 'Chuẩn kết nối');

-- 4. Seed authentic customer reviews for all products that lack reviews

-- User 2 (Nguyễn Văn An) review for products lacking reviews
INSERT INTO product_reviews (user_id, product_id, rating, comment, created_at, updated_at)
SELECT 2, p.id, 5, 
  CASE (p.category_id % 4)
    WHEN 0 THEN 'Sản phẩm hoàn thiện rất cao cấp, dùng mượt mà và ổn định. Đóng gói rất cẩn thận, nhân viên giao hàng nhiệt tình!'
    WHEN 1 THEN 'Chất lượng xuất sắc đúng như mô tả của TechStore. Pin trâu và hiệu năng rất tốt trong tầm giá!'
    WHEN 2 THEN 'Hàng chính hãng nguyên seal 100%, bảo hành chuẩn. Đã test kỹ mọi chức năng đều mượt, cực kỳ hài lòng!'
    ELSE 'Trải nghiệm vượt ngoài mong đợi, âm thanh và hiển thị sắc nét. Rất đáng đồng tiền bát gạo!'
  END,
  NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'
FROM products p
WHERE NOT EXISTS (SELECT 1 FROM product_reviews pr WHERE pr.product_id = p.id AND pr.user_id = 2);

-- User 3 (Trần Thị Bình) review for half of the products
INSERT INTO product_reviews (user_id, product_id, rating, comment, created_at, updated_at)
SELECT 3, p.id, 
  CASE WHEN (p.id % 5 = 0) THEN 4 ELSE 5 END,
  CASE (p.category_id % 3)
    WHEN 0 THEN 'Giao hàng hỏa tốc trong 2 giờ đúng hẹn. Máy dùng rất mát và êm, chế độ bảo hành rõ ràng an tâm.'
    WHEN 1 THEN 'Thiết kế đẹp mắt sang trọng, màu sắc thực tế rất ưng ý. TechStore tư vấn nhiệt tình, sẽ ủng hộ tiếp!'
    ELSE 'Đã mua lần thứ 2 tại hệ thống, chất lượng dịch vụ luôn chu đáo, chính sách đổi trả uy tín.'
  END,
  NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'
FROM products p
WHERE p.id % 2 = 0 AND NOT EXISTS (SELECT 1 FROM product_reviews pr WHERE pr.product_id = p.id AND pr.user_id = 3);

-- 5. Standardize storefront contents for all products

-- Insert default rich storefront contents for any product missing storefront data
INSERT INTO product_storefront_contents (
  product_id,
  content,
  created_at,
  updated_at
)
SELECT 
  p.id,
  jsonb_build_object(
    'storySections', jsonb_build_array(
      jsonb_build_object(
        'heading', 'Thiết kế tinh xảo và Trải nghiệm công nghệ đỉnh cao',
        'body', concat(p.name, ' sở hữu ngôn ngữ thiết kế hiện đại, hoàn thiện cao cấp mang lại cảm giác cầm nắm đầm tay và sự sang trọng. Tối ưu hóa trải nghiệm sử dụng hàng ngày với độ bền vượt trội.'),
        'imageUrl', COALESCE((SELECT pi.url FROM product_images pi WHERE pi.product_id = p.id AND pi.url NOT LIKE '%svg%' LIMIT 1), 'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=1000&auto=format&fit=crop&q=80'),
        'imageCaption', concat('Hình ảnh chi tiết sản phẩm chính hãng: ', p.name)
      ),
      jsonb_build_object(
        'heading', 'Sức mạnh hiệu năng thế hệ mới và Tiết kiệm năng lượng',
        'body', 'Trang bị cấu hình phần cứng tối tân, vận hành mượt mà mọi tác vụ học tập, làm việc và giải trí đa phương tiện. Hệ thống tản nhiệt thông minh và quản lý năng lượng tối ưu giúp duy trì hiệu suất ổn định suốt ngày dài.',
        'imageUrl', 'https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=1000&auto=format&fit=crop&q=80',
        'imageCaption', 'Hiệu năng đỉnh cao đáp ứng đa nhiệm linh hoạt'
      )
    ),
    'media', jsonb_build_object(
      'reviewVideoUrl', 'https://www.youtube.com/watch?v=0hQf7Y1p5c0',
      'unboxingVideoUrl', 'https://www.youtube.com/watch?v=0hQf7Y1p5c0',
      'unboxingDescription', concat('Bộ sản phẩm chính hãng gồm: Thân máy/thiết bị ', p.name, ' nguyên seal 100%, cáp kết nối sạc nhanh, que chọc SIM/phụ kiện tiêu chuẩn, sách hướng dẫn sử dụng và tem bảo hành TechStore.'),
      'view360Url', 'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=1000'
    ),
    'promotions', jsonb_build_array(
      jsonb_build_object(
        'id', concat('promo-vnpay-', p.id),
        'title', 'Giảm ngay 500.000đ khi thanh toán qua VNPAY-QR hoặc MoMo',
        'description', 'Nhập mã TECHSTORE500 tại bước thanh toán trực tuyến hoặc quét mã tại quầy',
        'partnerName', 'VNPAY',
        'discountAmount', 500000
      ),
      jsonb_build_object(
        'id', concat('promo-student-', p.id),
        'title', 'Ưu đãi TechStore Education: Giảm thêm 300.000đ cho Học sinh - Sinh viên',
        'description', 'Xuất trình thẻ HSSV hoặc tài khoản ứng dụng định danh VNeID khi mua hàng',
        'partnerName', 'TechStore Education',
        'discountAmount', 300000
      ),
      jsonb_build_object(
        'id', concat('promo-care-', p.id),
        'title', 'Tặng gói Bảo Hành VIP TechStore Care+ 12 Tháng (1 Đổi 1)',
        'description', 'Bảo vệ toàn diện phần cứng thiết bị, đổi mới ngay trong 30 ngày nếu phát sinh lỗi',
        'partnerName', 'TechStore Care+',
        'discountAmount', null
      )
    ),
    'paymentOffers', jsonb_build_array(
      jsonb_build_object(
        'id', concat('pay-cb-', p.id),
        'title', 'Hoàn tiền 5% tối đa 600.000đ qua thẻ tín dụng Techcombank / VPBank / TPBank',
        'description', 'Áp dụng cho mọi giao dịch thanh toán đơn hàng điện tử công nghệ tại TechStore',
        'partnerName', 'Techcombank'
      )
    ),
    'bundleOffers', jsonb_build_array(
      jsonb_build_object(
        'id', concat('bundle-acc-', p.id),
        'title', 'Combo Phụ kiện cao cấp bảo vệ toàn diện',
        'description', 'Mua kèm theo máy tiết kiệm ngay 40% chi phí phụ kiện chính hãng',
        'originalPrice', 590000,
        'price', 350000
      )
    ),
    'installmentPlans', jsonb_build_array(
      jsonb_build_object(
        'providerName', 'Thẻ tín dụng (Visa / Mastercard / JCB)',
        'months', 12,
        'interestRatePercent', 0,
        'monthlyPayment', ROUND(COALESCE((SELECT MIN(pv.price) FROM product_variants pv WHERE pv.product_id = p.id), 12000000) / 12),
        'downPayment', 0,
        'terms', '0% lãi suất, 0đ phụ phí chuyển đổi kỳ hạn 6 - 12 tháng tại 25 ngân hàng liên kết'
      ),
      jsonb_build_object(
        'providerName', 'Công ty tài chính Home Credit / FE Credit',
        'months', 6,
        'interestRatePercent', 0,
        'monthlyPayment', ROUND((COALESCE((SELECT MIN(pv.price) FROM product_variants pv WHERE pv.product_id = p.id), 12000000) * 0.7) / 6),
        'downPayment', ROUND(COALESCE((SELECT MIN(pv.price) FROM product_variants pv WHERE pv.product_id = p.id), 12000000) * 0.3),
        'terms', 'Trả trước 30%, duyệt hồ sơ online nhanh chóng trong 10 phút chỉ với CCCD gắn chip'
      )
    ),
    'tradeInOffers', jsonb_build_array(
      jsonb_build_object(
        'deviceModel', 'Thiết bị công nghệ thế hệ trước',
        'maximumSubsidy', 2000000,
        'minimumTopUp', 3000000,
        'maximumTopUp', 10000000,
        'condition', 'Máy hoạt động bình thường, đầy đủ chức năng',
        'terms', 'Thu cũ đổi mới trợ giá lên đời tốt nhất thị trường tại TechStore'
      )
    ),
    'storePolicy', jsonb_build_object(
      'officialProductText', 'Hàng chính hãng phân phối tại Việt Nam - Nguyên seal 100%',
      'warrantyText', 'Bảo hành chính hãng 12 - 24 tháng tại các trung tâm dịch vụ ủy quyền toàn quốc',
      'exchangeText', 'Hư gì đổi nấy trong 30 ngày đầu tiên nếu phát sinh lỗi phần cứng từ nhà sản xuất',
      'shippingText', 'Giao hàng hỏa tốc trong 2 giờ tại nội thành Hà Nội & TP. Hồ Chí Minh',
      'hotline', '1800 6868',
      'hotlineHours', '08:00 - 21:30 hàng ngày (Miễn phí cước gọi)'
    ),
    'faqs', jsonb_build_array(
      jsonb_build_object(
        'question', 'Sản phẩm tại TechStore có phải là hàng chính hãng không?',
        'answer', 'Tất cả sản phẩm tại TechStore là hàng phân phối chính hãng 100%, mới nguyên seal, có hóa đơn VAT đầy đủ và được bảo hành tại hệ thống trung tâm bảo hành ủy quyền trên toàn quốc.'
      ),
      jsonb_build_object(
        'question', 'Chính sách mua trả góp 0% tại TechStore như thế nào?',
        'answer', 'TechStore hỗ trợ 2 hình thức: Trả góp 0% qua thẻ tín dụng của 25 ngân hàng liên kết (kỳ hạn 3-12 tháng, không mất phí chuyển đổi) hoặc trả góp duyệt hồ sơ CCCD online qua Home Credit trong 10 phút.'
      ),
      jsonb_build_object(
        'question', 'Thời gian giao hàng mất bao lâu và tôi có được kiểm tra hàng trước khi nhận không?',
        'answer', 'Khách hàng tại nội thành Hà Nội và TP.HCM nhận hàng hỏa tốc trong 2 giờ. Các tỉnh thành khác từ 1-3 ngày. Quý khách được đồng kiểm mở hộp kiểm tra thiết bị trước khi thanh toán.'
      )
    ),
    'cityAvailability', jsonb_build_array(
      jsonb_build_object('cityCode', 'HN', 'cityName', 'Hà Nội', 'storeCount', 8, 'stockCount', 35, 'deliveryEstimate', 'Giao siêu tốc 2 giờ'),
      jsonb_build_object('cityCode', 'HCM', 'cityName', 'TP. Hồ Chí Minh', 'storeCount', 12, 'stockCount', 58, 'deliveryEstimate', 'Giao siêu tốc 2 giờ'),
      jsonb_build_object('cityCode', 'DN', 'cityName', 'Đà Nẵng', 'storeCount', 3, 'stockCount', 15, 'deliveryEstimate', 'Giao trong ngày')
    ),
    'comparisonProductIds', jsonb_build_array(1, 2, 3)
  ),
  NOW(),
  NOW()
FROM products p
WHERE NOT EXISTS (
  SELECT 1 FROM product_storefront_contents psc WHERE psc.product_id = p.id
);


COMMIT;
