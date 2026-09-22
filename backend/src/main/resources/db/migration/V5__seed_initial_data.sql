-- =========================================================
-- FLYWAY MIGRATION V5: SEED SAMPLE ENTERPRISE TECH DATA
-- Đề tài: Hệ thống TMĐT Sản phẩm Công nghệ & AI RecSys
-- =========================================================

-- 1. SEED USERS (Admin & Khách hàng mẫu)
-- Mật khẩu mặc định: 'Admin@123' (Mã hóa chuẩn BCrypt)
INSERT INTO users (id, email, password, full_name, phone, role, status, created_at, updated_at)
VALUES
(1, 'admin@techstore.com', '$2a$10$KAs2vQWFLNIzoDIc01QgWOcnk1T0ERAJPxA/WPfhL19vn6LHi1rXO', 'Quản Trị Viên Hệ Thống', '0988888888', 'ADMIN', 'ACTIVE', NOW(), NOW()),
(2, 'nguyenvana@gmail.com', '$2a$10$KAs2vQWFLNIzoDIc01QgWOcnk1T0ERAJPxA/WPfhL19vn6LHi1rXO', 'Nguyễn Văn An', '0912345678', 'USER', 'ACTIVE', NOW(), NOW()),
(3, 'tranthib@gmail.com', '$2a$10$KAs2vQWFLNIzoDIc01QgWOcnk1T0ERAJPxA/WPfhL19vn6LHi1rXO', 'Trần Thị Bình', '0923456789', 'USER', 'ACTIVE', NOW(), NOW()),
(4, 'lehoangc@gmail.com', '$2a$10$KAs2vQWFLNIzoDIc01QgWOcnk1T0ERAJPxA/WPfhL19vn6LHi1rXO', 'Lê Hoàng Cường', '0934567890', 'USER', 'ACTIVE', NOW(), NOW()),
(5, 'phamminhd@gmail.com', '$2a$10$KAs2vQWFLNIzoDIc01QgWOcnk1T0ERAJPxA/WPfhL19vn6LHi1rXO', 'Phạm Minh Đức', '0945678901', 'USER', 'ACTIVE', NOW(), NOW())
ON CONFLICT (id) DO UPDATE SET
    role = EXCLUDED.role,
    full_name = EXCLUDED.full_name;

-- 2. SEED CATEGORIES (Danh mục sản phẩm)
INSERT INTO categories (id, name, slug, description, image_url, status, created_at, updated_at)
VALUES
(1, 'Laptop', 'laptop', 'Máy tính xách tay cao cấp, Ultrabook doanh nhân và Laptop Gaming cấu hình khủng', 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&auto=format&fit=crop&q=80', 'ACTIVE', NOW(), NOW()),
(2, 'Điện thoại', 'dien-thoai', 'Smartphone flagship đỉnh cao tích hợp Trí tuệ nhân tạo AI thế hệ mới', 'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=800&auto=format&fit=crop&q=80', 'ACTIVE', NOW(), NOW()),
(3, 'Máy tính bảng', 'may-tinh-bang', 'Tablet chuyên nghiệp màn hình OLED phục vụ sáng tạo nghệ thuật và đồ họa', 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=800&auto=format&fit=crop&q=80', 'ACTIVE', NOW(), NOW()),
(4, 'Âm thanh', 'am-thanh', 'Tai nghe không dây Hi-Res chống ồn chủ động và loa studio chất lượng cao', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&auto=format&fit=crop&q=80', 'ACTIVE', NOW(), NOW()),
(5, 'Linh kiện & Phụ kiện', 'linh-kien-phu-kien', 'Card đồ họa NVIDIA RTX, chuột công thái học và bàn phím cơ cao cấp', 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&auto=format&fit=crop&q=80', 'ACTIVE', NOW(), NOW())
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    slug = EXCLUDED.slug,
    description = EXCLUDED.description,
    image_url = EXCLUDED.image_url;

-- 3. SEED BRANDS (Thương hiệu công nghệ uy tín)
INSERT INTO brands (id, name, slug, description, logo_url, website_url, status, created_at, updated_at)
VALUES
(1, 'Apple', 'apple', 'Tập đoàn công nghệ dẫn đầu thế giới với hệ sinh thái iPhone, MacBook, iPad và chip Apple Silicon', 'https://upload.wikimedia.org/wikipedia/commons/f/fa/Apple_logo_black.svg', 'https://apple.com', 'ACTIVE', NOW(), NOW()),
(2, 'Samsung', 'samsung', 'Gã khổng lồ công nghệ toàn cầu tiên phong với Galaxy AI và màn hình Dynamic AMOLED', 'https://upload.wikimedia.org/wikipedia/commons/2/24/Samsung_Logo.svg', 'https://samsung.com', 'ACTIVE', NOW(), NOW()),
(3, 'Dell', 'dell', 'Thương hiệu máy tính xách tay doanh nhân hàng đầu với dòng XPS và Alienware cao cấp', 'https://upload.wikimedia.org/wikipedia/commons/4/48/Dell_Logo.svg', 'https://dell.com', 'ACTIVE', NOW(), NOW()),
(4, 'ASUS', 'asus', 'Thương hiệu phần cứng hàng đầu thế giới với dòng ROG và Zenbook đột phá', 'https://upload.wikimedia.org/wikipedia/commons/2/2e/ASUS_Logo.svg', 'https://asus.com', 'ACTIVE', NOW(), NOW()),
(5, 'Sony', 'sony', 'Huyền thoại công nghệ âm thanh và quang học với tai nghe chống ồn đỉnh cao', 'https://upload.wikimedia.org/wikipedia/commons/c/ca/Sony_logo.svg', 'https://sony.com', 'ACTIVE', NOW(), NOW()),
(6, 'NVIDIA', 'nvidia', 'Tập đoàn xử lý đồ họa AI số 1 thế giới với kiến trúc vi xử lý GeForce RTX', 'https://upload.wikimedia.org/wikipedia/commons/2/21/Nvidia_logo.svg', 'https://nvidia.com', 'ACTIVE', NOW(), NOW()),
(7, 'Logitech', 'logitech', 'Nhà sản xuất thiết bị ngoại vi văn phòng và công thái học hàng đầu thế giới', 'https://upload.wikimedia.org/wikipedia/commons/0/08/Logitech_logo.svg', 'https://logitech.com', 'ACTIVE', NOW(), NOW())
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    slug = EXCLUDED.slug,
    description = EXCLUDED.description;

-- 4. SEED PRODUCTS (12 Sản phẩm Flagship Công Nghệ)
INSERT INTO products (id, name, slug, description, category_id, brand_id, status, created_at, updated_at)
VALUES
(1, 'iPhone 16 Pro Max 256GB', 'iphone-16-pro-max-256gb', 'Điện thoại cao cấp nhất từ Apple với chip vi xử lý Apple A18 Pro 3nm, màn hình Super Retina XDR 6.9 inch ProMotion 120Hz, hệ thống camera 48MP zoom quang học 5x sắc nét và khung viền Titan chuẩn hàng không vũ trụ.', 2, 1, 'ACTIVE', NOW(), NOW()),
(2, 'Samsung Galaxy S24 Ultra 256GB', 'samsung-galaxy-s24-ultra-256gb', 'Flagship Android đỉnh cao trang bị bộ tính năng Galaxy AI thông minh toàn diện, chip vi xử lý Snapdragon 8 Gen 3 for Galaxy, bút S-Pen quyền năng tích hợp và màn hình phẳng Dynamic AMOLED 2X 120Hz chống chói độc quyền.', 2, 2, 'ACTIVE', NOW(), NOW()),
(3, 'MacBook Pro 14 M3 Pro', 'macbook-pro-14-m3-pro', 'Cỗ máy làm việc chuyên nghiệp cho kỹ sư phần mềm và nhà sáng tạo nội dung với vi xử lý Apple M3 Pro 11-core CPU / 14-core GPU, màn hình Liquid Retina XDR độ sáng 1600 nits và thời lượng pin bền bỉ lên tới 18 giờ liên tục.', 1, 1, 'ACTIVE', NOW(), NOW()),
(4, 'Dell XPS 14 OLED 2024', 'dell-xps-14-oled-2024', 'Tuyệt tác Laptop doanh nhân siêu mỏng nhẹ với khung nhôm CNC nguyên khối, vi xử lý Intel Core Ultra 7 155H tích hợp nhân xử lý AI NPU chuyên biệt, màn hình cảm ứng 3.2K InfinityEdge OLED màu sắc chuẩn điện ảnh.', 1, 3, 'ACTIVE', NOW(), NOW()),
(5, 'ASUS ROG Zephyrus G16 OLED', 'asus-rog-zephyrus-g16-oled', 'Laptop Gaming siêu mỏng nhẹ đỉnh cao trang bị card đồ họa NVIDIA GeForce RTX 4070 8GB GDDR6, CPU Intel Core Ultra 9 185H, hệ thống tản nhiệt buồng hơi ROG Intelligent Cooling và màn hình ROG Nebula OLED 240Hz 0.2ms.', 1, 4, 'ACTIVE', NOW(), NOW()),
(6, 'iPad Pro 11 M4 OLED', 'ipad-pro-11-m4-oled', 'Thiết bị mỏng nhất từ trước tới nay của Apple, đột phá với vi xử lý siêu mạnh Apple M4, màn hình kép Tandem OLED Ultra Retina XDR và hỗ trợ hoàn hảo bút Apple Pencil Pro có cảm biến haptic.', 3, 1, 'ACTIVE', NOW(), NOW()),
(7, 'Sony WH-1000XM5 Wireless', 'sony-wh-1000xm5-wireless', 'Tai nghe chụp tai chống ồn chủ động tốt nhất thế giới với vi xử lý kép V1 và QN1, màng loa 30mm sợi carbon nhẹ cao cấp, hỗ trợ chuẩn âm thanh Hi-Res Audio Wireless LDAC và thời lượng pin tới 30 giờ.', 4, 5, 'ACTIVE', NOW(), NOW()),
(8, 'ASUS ROG Strix GeForce RTX 4090 OC 24GB', 'asus-rog-strix-geforce-rtx-4090-oc-24gb', 'Card đồ họa tối thượng dành cho game thủ 4K và nhà phát triển mô hình Trí tuệ nhân tạo, kiến trúc NVIDIA Ada Lovelace, 24GB GDDR6X, quạt Axial-tech làm mát buồng hơi và khung gia cố kim loại đúc nguyên khối.', 5, 4, 'ACTIVE', NOW(), NOW()),
(9, 'Apple Watch Ultra 2 GPS + Cellular 49mm', 'apple-watch-ultra-2-gps-cellular-49mm', 'Đồng hồ thông minh thể thao chuyên nghiệp bền bỉ nhất của Apple với vỏ Titan 49mm, màn hình sapphire siêu sáng 3000 nits, định vị GPS kép tần số L1/L5 chuẩn xác và khả năng kháng nước chuẩn lặn 100m.', 5, 1, 'ACTIVE', NOW(), NOW()),
(10, 'Samsung Galaxy Z Fold6 512GB', 'samsung-galaxy-z-fold6-512gb', 'Đỉnh cao điện thoại màn hình gập thế hệ mới mỏng nhẹ hơn, trang bị bản lề FlexHinge kép bền bỉ, màn hình trong Dynamic AMOLED 2X 7.6 inch 120Hz đa nhiệm cùng lúc 3 ứng dụng và bộ công cụ Galaxy AI đa ngôn ngữ.', 2, 2, 'ACTIVE', NOW(), NOW()),
(11, 'Chuột không dây Logitech MX Master 3S', 'chuot-khong-day-logitech-mx-master-3s', 'Chuột công thái học cao cấp cho lập trình viên với cảm biến Darkfield 8000 DPI di chuyển trên mọi bề mặt, con lăn điện từ MagSpeed cuộn 1000 dòng/giây và phím bấm Quiet Clicks giảm 90% tiếng ồn.', 5, 7, 'ACTIVE', NOW(), NOW()),
(12, 'Bàn phím cơ Logitech MX Mechanical', 'ban-phim-co-logitech-mx-mechanical', 'Bàn phím cơ không dây Low-profile cao cấp với switch Tactile Quiet êm ái, kết nối đa thiết bị Easy-Switch qua Bluetooth/Bolt, đèn nền thông minh tự động bật sáng khi bàn tay lại gần và thời lượng pin 10 tháng.', 5, 7, 'ACTIVE', NOW(), NOW())
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    slug = EXCLUDED.slug,
    description = EXCLUDED.description;

-- 5. SEED PRODUCT_VARIANTS (Biến thể cấu hình & Giá bán)
INSERT INTO product_variants (id, product_id, sku, name, price, original_price, stock, status, created_at, updated_at)
VALUES
-- iPhone 16 Pro Max
(1, 1, 'IP16PM-256-NAT', '256GB Titan Tự Nhiên', 34990000.00, 37990000.00, 45, 'ACTIVE', NOW(), NOW()),
(2, 1, 'IP16PM-512-DES', '512GB Titan Sa Mạc', 40990000.00, 43990000.00, 25, 'ACTIVE', NOW(), NOW()),
(3, 1, 'IP16PM-1TB-BLK', '1TB Titan Đen', 46990000.00, 49990000.00, 15, 'ACTIVE', NOW(), NOW()),

-- Samsung Galaxy S24 Ultra
(4, 2, 'S24U-256-GRY', '256GB Xám Titan', 29990000.00, 33990000.00, 38, 'ACTIVE', NOW(), NOW()),
(5, 2, 'S24U-512-BLK', '512GB Đen Titan', 33990000.00, 37490000.00, 20, 'ACTIVE', NOW(), NOW()),

-- MacBook Pro 14 M3 Pro
(6, 3, 'MBP14-M3P-18-512', '18GB RAM / 512GB SSD Space Black', 49990000.00, 52990000.00, 22, 'ACTIVE', NOW(), NOW()),
(7, 3, 'MBP14-M3P-36-1TB', '36GB RAM / 1TB SSD Silver', 62990000.00, 66990000.00, 10, 'ACTIVE', NOW(), NOW()),

-- Dell XPS 14 OLED
(8, 4, 'XPS14-U7-16-512', 'Core Ultra 7 / 16GB RAM / 512GB SSD', 46990000.00, 49990000.00, 18, 'ACTIVE', NOW(), NOW()),
(9, 4, 'XPS14-U7-32-1TB', 'Core Ultra 7 / 32GB RAM / 1TB SSD', 55990000.00, 59990000.00, 8, 'ACTIVE', NOW(), NOW()),

-- ASUS ROG Zephyrus G16 OLED
(10, 5, 'G16-U9-RTX4070', 'Core Ultra 9 / RTX 4070 / 32GB / 1TB SSD', 64990000.00, 69990000.00, 14, 'ACTIVE', NOW(), NOW()),
(11, 5, 'G16-U9-RTX4080', 'Core Ultra 9 / RTX 4080 / 32GB / 2TB SSD', 79990000.00, 84990000.00, 6, 'ACTIVE', NOW(), NOW()),

-- iPad Pro 11 M4 OLED
(12, 6, 'IPAD-M4-256-WIFI', '256GB Wi-Fi Space Black', 27990000.00, 29990000.00, 30, 'ACTIVE', NOW(), NOW()),
(13, 6, 'IPAD-M4-512-5G', '512GB 5G Cellular Silver', 36990000.00, 39990000.00, 15, 'ACTIVE', NOW(), NOW()),

-- Sony WH-1000XM5
(14, 7, 'WH1000XM5-BLK', 'Màu Đen Midnight Black', 7990000.00, 8990000.00, 50, 'ACTIVE', NOW(), NOW()),
(15, 7, 'WH1000XM5-SLV', 'Màu Bạc Platinum Silver', 7990000.00, 8990000.00, 35, 'ACTIVE', NOW(), NOW()),

-- ASUS ROG RTX 4090
(16, 8, 'ROG-RTX4090-24G', '24GB GDDR6X OC Edition', 58990000.00, 62990000.00, 8, 'ACTIVE', NOW(), NOW()),

-- Apple Watch Ultra 2
(17, 9, 'AWU2-49-TRAIL', '49mm Titan Dây Trail Loop', 21490000.00, 22990000.00, 25, 'ACTIVE', NOW(), NOW()),

-- Samsung Galaxy Z Fold6
(18, 10, 'ZFOLD6-512-NAVY', '512GB Xanh Navy', 43990000.00, 47990000.00, 18, 'ACTIVE', NOW(), NOW()),

-- Logitech MX Master 3S
(19, 11, 'MX-MASTER-3S-GRY', 'Màu Xám Graphite', 2490000.00, 2890000.00, 60, 'ACTIVE', NOW(), NOW()),

-- Logitech MX Mechanical
(20, 12, 'MX-MECH-TACTILE', 'Bàn phím cơ full-size Switch Tactile Quiet', 3490000.00, 3990000.00, 40, 'ACTIVE', NOW(), NOW())
ON CONFLICT (id) DO UPDATE SET
    price = EXCLUDED.price,
    stock = EXCLUDED.stock,
    name = EXCLUDED.name;

-- 6. SEED PRODUCT_ATTRIBUTES (Thông số kỹ thuật chuẩn phần cứng)
INSERT INTO product_attributes (id, product_id, name, value, created_at, updated_at)
VALUES
-- iPhone 16 Pro Max
(1, 1, 'CPU', 'Apple A18 Pro (6 lõi, 3nm thế hệ 2)', NOW(), NOW()),
(2, 1, 'RAM', '8GB LPDDR5X', NOW(), NOW()),
(3, 1, 'Màn hình', '6.9 inch Super Retina XDR OLED 120Hz ProMotion', NOW(), NOW()),
(4, 1, 'Camera sau', '48MP Fusion + 48MP Ultra Wide + 12MP Telephoto 5x', NOW(), NOW()),
(5, 1, 'Pin / Sạc', '4685 mAh, Sạc nhanh không dây MagSafe 25W', NOW(), NOW()),
(6, 1, 'Hệ điều hành', 'iOS 18', NOW(), NOW()),

-- Samsung Galaxy S24 Ultra
(7, 2, 'CPU', 'Qualcomm Snapdragon 8 Gen 3 for Galaxy (4nm)', NOW(), NOW()),
(8, 2, 'RAM', '12GB LPDDR5X', NOW(), NOW()),
(9, 2, 'Màn hình', '6.8 inch Dynamic AMOLED 2X QHD+ 120Hz 2600 nits', NOW(), NOW()),
(10, 2, 'Camera sau', '200MP + 50MP Zoom 5x + 10MP Zoom 3x + 12MP Ultra-wide', NOW(), NOW()),
(11, 2, 'Pin / Sạc', '5000 mAh, Sạc nhanh có dây 45W', NOW(), NOW()),
(12, 2, 'Tính năng AI', 'Galaxy AI: Dịch trực tiếp cuộc gọi, Circle to Search', NOW(), NOW()),

-- MacBook Pro 14 M3 Pro
(13, 3, 'CPU', 'Apple M3 Pro (11-Core CPU, 14-Core GPU)', NOW(), NOW()),
(14, 3, 'RAM', '18GB Unified Memory băng thông 150GB/s', NOW(), NOW()),
(15, 3, 'Ổ cứng', '512GB SSD NVMe chuẩn PCIe siêu tốc', NOW(), NOW()),
(16, 3, 'Màn hình', '14.2 inch Liquid Retina XDR 3024x1964 120Hz ProMotion', NOW(), NOW()),
(17, 3, 'Pin / Sạc', '70Wh Li-Polymer, Củ sạc 70W USB-C MagSafe 3', NOW(), NOW()),
(18, 3, 'Trọng lượng', '1.61 kg nhôm tái chế nguyên khối', NOW(), NOW()),

-- Dell XPS 14 OLED
(19, 4, 'CPU', 'Intel Core Ultra 7 155H (16 Cores, 22 Threads, NPU AI)', NOW(), NOW()),
(20, 4, 'RAM', '16GB LPDDR5x 7467MHz Dual Channel', NOW(), NOW()),
(21, 4, 'Ổ cứng', '512GB M.2 PCIe NVMe Gen 4 SSD', NOW(), NOW()),
(22, 4, 'GPU', 'Intel Arc Graphics tích hợp', NOW(), NOW()),
(23, 4, 'Màn hình', '14.5 inch 3.2K (3200x2000) OLED Touch 120Hz 100% DCI-P3', NOW(), NOW()),
(24, 4, 'Trọng lượng', '1.68 kg nhôm CNC cao cấp', NOW(), NOW()),

-- ASUS ROG Zephyrus G16 OLED
(25, 5, 'CPU', 'Intel Core Ultra 9 185H (16 Cores, Up to 5.1GHz, NPU AI)', NOW(), NOW()),
(26, 5, 'GPU', 'NVIDIA GeForce RTX 4070 8GB GDDR6 (TGP 105W)', NOW(), NOW()),
(27, 5, 'RAM', '32GB LPDDR5X 7467MHz Onboard', NOW(), NOW()),
(28, 5, 'Ổ cứng', '1TB PCIe 4.0 NVMe M.2 Performance SSD', NOW(), NOW()),
(29, 5, 'Màn hình', '16 inch 2.5K (2560x1600) ROG Nebula OLED 240Hz 0.2ms G-Sync', NOW(), NOW()),
(30, 5, 'Trọng lượng', '1.85 kg siêu mỏng nhẹ hàng đầu phân khúc', NOW(), NOW()),

-- iPad Pro 11 M4 OLED
(31, 6, 'CPU', 'Apple M4 (9-Core CPU, 10-Core GPU, 16-Core Neural Engine)', NOW(), NOW()),
(32, 6, 'RAM', '8GB Unified Memory', NOW(), NOW()),
(33, 6, 'Màn hình', '11 inch Tandem OLED Ultra Retina XDR 120Hz 1000 nits', NOW(), NOW()),
(34, 6, 'Độ mỏng', '5.3 mm siêu mỏng ấn tượng', NOW(), NOW()),

-- Sony WH-1000XM5
(35, 7, 'Màng loa', '30mm sợi carbon nhẹ với vòm TPU độ đàn hồi cao', NOW(), NOW()),
(36, 7, 'Chống ồn', 'Bộ xử lý tích hợp V1 kết hợp bộ xử lý chống ồn HD QN1', NOW(), NOW()),
(37, 7, 'Thời lượng pin', '30 giờ (bật NC) / 40 giờ (tắt NC), sạc 3 phút dùng 3 giờ', NOW(), NOW()),
(38, 7, 'Chuẩn kết nối', 'Bluetooth 5.2, hỗ trợ Codec LDAC, AAC, SBC', NOW(), NOW()),

-- ASUS ROG RTX 4090
(39, 8, 'Kiến trúc GPU', 'NVIDIA Ada Lovelace AD102', NOW(), NOW()),
(40, 8, 'Bộ nhớ VRAM', '24GB GDDR6X băng thông 384-bit 1008 GB/s', NOW(), NOW()),
(41, 8, 'Nhân CUDA', '16384 Cores', NOW(), NOW()),
(42, 8, 'Công suất nguồn', 'Khuyến nghị nguồn 1000W trở lên (Chân cắm 16-pin 12VHPWR)', NOW(), NOW()),

-- Apple Watch Ultra 2
(43, 9, 'Chipset', 'Apple S9 SiP lõi kép 64-bit với Neural Engine 4 lõi', NOW(), NOW()),
(44, 9, 'Màn hình', 'OLED Always-On Retina Sapphire độ sáng tối đa 3000 nits', NOW(), NOW()),
(45, 9, 'Chống nước', 'Kháng nước 100m, chứng nhận lặn giải trí EN13319', NOW(), NOW()),

-- Samsung Galaxy Z Fold6
(46, 10, 'Màn hình chính', '7.6 inch Dynamic AMOLED 2X gập 120Hz (2160 x 1856 pixels)', NOW(), NOW()),
(47, 10, 'Màn hình phụ', '6.3 inch Dynamic AMOLED 2X 120Hz (2376 x 968 pixels)', NOW(), NOW()),
(48, 10, 'CPU', 'Qualcomm Snapdragon 8 Gen 3 for Galaxy', NOW(), NOW()),
(49, 10, 'RAM', '12GB LPDDR5X', NOW(), NOW()),

-- Logitech MX Master 3S
(50, 11, 'Cảm biến', 'Darkfield High Precision (200 - 8000 DPI)', NOW(), NOW()),
(51, 11, 'Con lăn', 'MagSpeed SmartShift điện từ bằng thép gia công', NOW(), NOW()),
(52, 11, 'Thời lượng pin', '70 ngày sau một lần sạc đầy USB-C', NOW(), NOW()),

-- Logitech MX Mechanical
(53, 12, 'Loại switch', 'Low-profile Mechanical Tactile Quiet', NOW(), NOW()),
(54, 12, 'Kết nối', 'Bluetooth Low Energy & Đầu thu Logi Bolt USB', NOW(), NOW()),
(55, 12, 'Thời lượng pin', '15 ngày có đèn nền / Lên tới 10 tháng không đèn nền', NOW(), NOW())
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    value = EXCLUDED.value;

-- 7. SEED PRODUCT_IMAGES (Hình ảnh sắc nét chuẩn Studio)
INSERT INTO product_images (id, product_id, url, sort_order, is_thumbnail, created_at, updated_at)
VALUES
(1, 1, 'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=800&auto=format&fit=crop&q=80', 0, TRUE, NOW(), NOW()),
(2, 1, 'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=800&auto=format&fit=crop&q=80', 1, FALSE, NOW(), NOW()),

(3, 2, 'https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=800&auto=format&fit=crop&q=80', 0, TRUE, NOW(), NOW()),
(4, 2, 'https://images.unsplash.com/photo-1580910051074-3eb694886505?w=800&auto=format&fit=crop&q=80', 1, FALSE, NOW(), NOW()),

(5, 3, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&auto=format&fit=crop&q=80', 0, TRUE, NOW(), NOW()),
(6, 3, 'https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?w=800&auto=format&fit=crop&q=80', 1, FALSE, NOW(), NOW()),

(7, 4, 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=800&auto=format&fit=crop&q=80', 1, FALSE, NOW(), NOW()),
(8, 4, 'https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=800&auto=format&fit=crop&q=80', 0, TRUE, NOW(), NOW()),

(9, 5, 'https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=800&auto=format&fit=crop&q=80', 0, TRUE, NOW(), NOW()),
(10, 5, 'https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=800&auto=format&fit=crop&q=80', 1, FALSE, NOW(), NOW()),

(11, 6, 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=800&auto=format&fit=crop&q=80', 0, TRUE, NOW(), NOW()),

(12, 7, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&auto=format&fit=crop&q=80', 0, TRUE, NOW(), NOW()),

(13, 8, 'https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=800&auto=format&fit=crop&q=80', 0, TRUE, NOW(), NOW()),

(14, 9, 'https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=800&auto=format&fit=crop&q=80', 0, TRUE, NOW(), NOW()),

(15, 10, 'https://images.unsplash.com/photo-1574944985070-8f3ebc6b79d2?w=800&auto=format&fit=crop&q=80', 0, TRUE, NOW(), NOW()),

(16, 11, 'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=800&auto=format&fit=crop&q=80', 0, TRUE, NOW(), NOW()),

(17, 12, 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&auto=format&fit=crop&q=80', 0, TRUE, NOW(), NOW())
ON CONFLICT (id) DO UPDATE SET
    url = EXCLUDED.url,
    is_thumbnail = EXCLUDED.is_thumbnail;

-- 8. SEED WISHLISTS (Danh sách yêu thích)
INSERT INTO wishlists (id, user_id, product_id, created_at, updated_at)
VALUES
(1, 1, 3, NOW(), NOW()),
(2, 1, 5, NOW(), NOW()),
(3, 2, 1, NOW(), NOW()),
(4, 2, 7, NOW(), NOW()),
(5, 3, 2, NOW(), NOW()),
(6, 3, 10, NOW(), NOW())
ON CONFLICT (user_id, product_id) DO NOTHING;

-- 9. SEED PRODUCT_REVIEWS (Đánh giá chất lượng thực tế)
INSERT INTO product_reviews (id, user_id, product_id, rating, comment, created_at, updated_at)
VALUES
(1, 2, 1, 5, 'Thiết kế Titan tự nhiên nhìn rất sang trọng và nhẹ hơn hẳn 15 Pro Max. Màn hình viền siêu mỏng, thời lượng pin dùng thoải mái hơn 1.5 ngày.', NOW(), NOW()),
(2, 3, 1, 5, 'Camera zoom 5x chụp concert cực kỳ nét. Chip A18 Pro chơi Genshin Impact mượt mà không bị hạ sáng.', NOW(), NOW()),
(3, 4, 2, 5, 'Galaxy AI tính năng dịch cabin và khoanh vùng tìm kiếm tiện dụng vô cùng cho công việc thường ngày. Bút S-Pen ký hợp đồng tiện lợi.', NOW(), NOW()),
(4, 2, 3, 5, 'Màn hình Liquid Retina XDR 120Hz đẹp xuất sắc, bàn phím gõ êm ái. Build dự án Spring Boot và Docker chạy cực kỳ mát mẻ, pin dùng được cả ngày không cần cắm sạc.', NOW(), NOW()),
(5, 3, 3, 5, 'Màu Space Black cực kỳ ngầu và ít bám vân tay hơn thế hệ trước. Quạt tản nhiệt gần như không bao giờ quay khi xử lý tác vụ văn phòng.', NOW(), NOW()),
(6, 4, 5, 5, 'ROG Zephyrus G16 OLED 240Hz màu sắc rực rỡ, độ trễ 0.2ms chơi CS2 và Valorant đỉnh chóp. RTX 4070 cân mượt mọi game AAA ở thiết lập Ultra.', NOW(), NOW()),
(7, 5, 7, 5, 'Chống ồn của Sony XM5 vượt trội hoàn toàn khi ngồi quán cafe hay trên máy bay. Đệm tai êm ái đeo liên tục 4 tiếng không bị đau tai.', NOW(), NOW()),
(8, 2, 11, 5, 'Con lăn MagSpeed cuộn siêu tốc, form cầm vừa vặn bàn tay giúp giảm hẳn đau cổ tay khi làm việc lâu.', NOW(), NOW())
ON CONFLICT (user_id, product_id) DO UPDATE SET
    rating = EXCLUDED.rating,
    comment = EXCLUDED.comment;

-- 10. SEED USER_BEHAVIORS (Dữ liệu Clickstream phục vụ huấn luyện AI RecSys)
-- action_type: VIEW, ADD_TO_CART, WISHLIST, PURCHASE, RATING
INSERT INTO user_behaviors (user_id, session_id, product_id, action_type, action_value, created_at)
VALUES
-- User 1: Thích hệ sinh thái Apple & Laptop hiệu năng cao
(1, 'sess_u1_01', 3, 'VIEW', 'slug=macbook-pro-14-m3-pro', NOW() - INTERVAL '5 days'),
(1, 'sess_u1_01', 3, 'WISHLIST', 'added_to_wishlist', NOW() - INTERVAL '5 days'),
(1, 'sess_u1_02', 1, 'VIEW', 'slug=iphone-16-pro-max-256gb', NOW() - INTERVAL '4 days'),
(1, 'sess_u1_02', 3, 'ADD_TO_CART', 'sku=MBP14-M3P-18-512', NOW() - INTERVAL '4 days'),
(1, 'sess_u1_03', 6, 'VIEW', 'slug=ipad-pro-11-m4-oled', NOW() - INTERVAL '3 days'),
(1, 'sess_u1_03', 9, 'VIEW', 'slug=apple-watch-ultra-2-gps-cellular-49mm', NOW() - INTERVAL '3 days'),
(1, 'sess_u1_04', 5, 'VIEW', 'slug=asus-rog-zephyrus-g16-oled', NOW() - INTERVAL '2 days'),
(1, 'sess_u1_04', 3, 'PURCHASE', 'order_code=ORD-DEMO-001', NOW() - INTERVAL '1 days'),

-- User 2: Thích Smartphone cao cấp & Phụ kiện văn phòng
(2, 'sess_u2_01', 1, 'VIEW', 'slug=iphone-16-pro-max-256gb', NOW() - INTERVAL '6 days'),
(2, 'sess_u2_01', 1, 'ADD_TO_CART', 'sku=IP16PM-256-NAT', NOW() - INTERVAL '6 days'),
(2, 'sess_u2_01', 1, 'PURCHASE', 'order_code=ORD-DEMO-002', NOW() - INTERVAL '5 days'),
(2, 'sess_u2_02', 7, 'VIEW', 'slug=sony-wh-1000xm5-wireless', NOW() - INTERVAL '4 days'),
(2, 'sess_u2_02', 7, 'WISHLIST', 'added_to_wishlist', NOW() - INTERVAL '3 days'),
(2, 'sess_u2_03', 11, 'VIEW', 'slug=chuot-khong-day-logitech-mx-master-3s', NOW() - INTERVAL '2 days'),
(2, 'sess_u2_03', 11, 'PURCHASE', 'order_code=ORD-DEMO-003', NOW() - INTERVAL '1 days'),

-- User 3: Thích hệ sinh thái Samsung & Màn hình gập
(3, 'sess_u3_01', 2, 'VIEW', 'slug=samsung-galaxy-s24-ultra-256gb', NOW() - INTERVAL '5 days'),
(3, 'sess_u3_01', 2, 'WISHLIST', 'added_to_wishlist', NOW() - INTERVAL '5 days'),
(3, 'sess_u3_02', 10, 'VIEW', 'slug=samsung-galaxy-z-fold6-512gb', NOW() - INTERVAL '4 days'),
(3, 'sess_u3_02', 10, 'ADD_TO_CART', 'sku=ZFOLD6-512-NAVY', NOW() - INTERVAL '3 days'),
(3, 'sess_u3_03', 2, 'PURCHASE', 'order_code=ORD-DEMO-004', NOW() - INTERVAL '2 days'),

-- User 4: Dân đồ họa, Gaming & Phần cứng PC
(4, 'sess_u4_01', 5, 'VIEW', 'slug=asus-rog-zephyrus-g16-oled', NOW() - INTERVAL '6 days'),
(4, 'sess_u4_01', 8, 'VIEW', 'slug=asus-rog-strix-geforce-rtx-4090-oc-24gb', NOW() - INTERVAL '5 days'),
(4, 'sess_u4_02', 8, 'ADD_TO_CART', 'sku=ROG-RTX4090-24G', NOW() - INTERVAL '4 days'),
(4, 'sess_u4_02', 4, 'VIEW', 'slug=dell-xps-14-oled-2024', NOW() - INTERVAL '3 days'),
(4, 'sess_u4_03', 12, 'VIEW', 'slug=ban-phim-co-logitech-mx-mechanical', NOW() - INTERVAL '2 days'),
(4, 'sess_u4_03', 5, 'PURCHASE', 'order_code=ORD-DEMO-005', NOW() - INTERVAL '1 days');

-- 11. ĐỒNG BỘ SEQUENCE CHO CÁC BẢNG (Để các thao tác INSERT sau không bị trùng ID)
SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 1) FROM users));
SELECT setval('categories_id_seq', (SELECT COALESCE(MAX(id), 1) FROM categories));
SELECT setval('brands_id_seq', (SELECT COALESCE(MAX(id), 1) FROM brands));
SELECT setval('products_id_seq', (SELECT COALESCE(MAX(id), 1) FROM products));
SELECT setval('product_variants_id_seq', (SELECT COALESCE(MAX(id), 1) FROM product_variants));
SELECT setval('product_attributes_id_seq', (SELECT COALESCE(MAX(id), 1) FROM product_attributes));
SELECT setval('product_images_id_seq', (SELECT COALESCE(MAX(id), 1) FROM product_images));
SELECT setval('wishlists_id_seq', (SELECT COALESCE(MAX(id), 1) FROM wishlists));
SELECT setval('product_reviews_id_seq', (SELECT COALESCE(MAX(id), 1) FROM product_reviews));
SELECT setval('user_behaviors_id_seq', (SELECT COALESCE(MAX(id), 1) FROM user_behaviors));
