import logging
import requests
import pandas as pd
from typing import List, Tuple
from app.config import settings
from app.models.schemas import ProductFeatureItem

logger = logging.getLogger(__name__)

# Dữ liệu hạt giống mặc định (Seed Data) phòng trường hợp Backend Spring Boot chưa chạy hoặc cơ sở dữ liệu trống
MOCK_PRODUCTS: List[ProductFeatureItem] = [
    ProductFeatureItem(
        id=1,
        name="iPhone 16 Pro Max 256GB",
        slug="iphone-16-pro-max-256gb",
        category="Điện thoại",
        brand="Apple",
        description="Điện thoại Apple cao cấp nhất với chip Apple A18 Pro, màn hình Super Retina XDR 6.9 inch, camera 48MP Zoom 5x, khung viền Titan cao cấp.",
        attributes=[
            {"name": "RAM", "value": "8GB"},
            {"name": "Bộ nhớ", "value": "256GB"},
            {"name": "Chip", "value": "Apple A18 Pro"},
            {"name": "Màn hình", "value": "6.9 inch 120Hz OLED"}
        ],
        min_price=34990000.0
    ),
    ProductFeatureItem(
        id=2,
        name="Samsung Galaxy S24 Ultra 256GB",
        slug="samsung-galaxy-s24-ultra-256gb",
        category="Điện thoại",
        brand="Samsung",
        description="Flagship tích hợp Galaxy AI thông minh, chip Snapdragon 8 Gen 3 for Galaxy, bút S-Pen tiện ích, màn hình phẳng Dynamic AMOLED 2X 120Hz.",
        attributes=[
            {"name": "RAM", "value": "12GB"},
            {"name": "Bộ nhớ", "value": "256GB"},
            {"name": "Chip", "value": "Snapdragon 8 Gen 3"},
            {"name": "Màn hình", "value": "6.8 inch 120Hz Dynamic AMOLED"}
        ],
        min_price=29990000.0
    ),
    ProductFeatureItem(
        id=3,
        name="MacBook Pro 14 M3 Pro",
        slug="macbook-pro-14-m3-pro",
        category="Laptop",
        brand="Apple",
        description="Laptop chuyên nghiệp mạnh mẽ nhất cho lập trình viên và sáng tạo nội dung với vi xử lý Apple M3 Pro 11 Core, màn hình Liquid Retina XDR 120Hz.",
        attributes=[
            {"name": "RAM", "value": "18GB"},
            {"name": "Bộ nhớ", "value": "512GB SSD"},
            {"name": "Chip", "value": "Apple M3 Pro"},
            {"name": "Màn hình", "value": "14.2 inch Liquid Retina XDR"}
        ],
        min_price=49990000.0
    ),
    ProductFeatureItem(
        id=4,
        name="Dell XPS 14 OLED 2024",
        slug="dell-xps-14-oled-2024",
        category="Laptop",
        brand="Dell",
        description="Laptop doanh nhân siêu mỏng nhẹ cao cấp của Dell, vi xử lý Intel Core Ultra 7 155H tích hợp NPU AI, màn hình 3.2K OLED 120Hz cảm ứng.",
        attributes=[
            {"name": "RAM", "value": "16GB LPDDR5x"},
            {"name": "Bộ nhớ", "value": "512GB SSD PCIe 4.0"},
            {"name": "Chip", "value": "Intel Core Ultra 7 155H"},
            {"name": "Màn hình", "value": "14.5 inch 3.2K OLED"}
        ],
        min_price=46990000.0
    ),
    ProductFeatureItem(
        id=5,
        name="ASUS ROG Zephyrus G16 OLED",
        slug="asus-rog-zephyrus-g16-oled",
        category="Laptop",
        brand="ASUS",
        description="Laptop Gaming siêu phẩm mỏng nhẹ hiệu năng khủng với card đồ họa NVIDIA GeForce RTX 4070, CPU Intel Core Ultra 9, màn hình ROG Nebula OLED 240Hz.",
        attributes=[
            {"name": "RAM", "value": "32GB LPDDR5x"},
            {"name": "Bộ nhớ", "value": "1TB SSD PCIe 4.0"},
            {"name": "Chip", "value": "Intel Core Ultra 9 185H"},
            {"name": "GPU", "value": "NVIDIA GeForce RTX 4070 8GB"},
            {"name": "Màn hình", "value": "16 inch 2.5K OLED 240Hz"}
        ],
        min_price=64990000.0
    ),
    ProductFeatureItem(
        id=6,
        name="iPad Pro 11 M4 OLED",
        slug="ipad-pro-11-m4-oled",
        category="Máy tính bảng",
        brand="Apple",
        description="Máy tính bảng mỏng nhất từ trước tới nay của Apple, trang bị chip siêu mạnh Apple M4, công nghệ màn hình đột phá Ultra Retina XDR Tandem OLED.",
        attributes=[
            {"name": "RAM", "value": "8GB"},
            {"name": "Bộ nhớ", "value": "256GB"},
            {"name": "Chip", "value": "Apple M4"},
            {"name": "Màn hình", "value": "11 inch Ultra Retina XDR OLED 120Hz"}
        ],
        min_price=27990000.0
    )
]

MOCK_INTERACTIONS = pd.DataFrame([
    {"user_id": 1, "product_id": 1, "score": 8.0, "interaction_count": 4},
    {"user_id": 1, "product_id": 3, "score": 10.0, "interaction_count": 5},
    {"user_id": 1, "product_id": 6, "score": 5.0, "interaction_count": 2},
    {"user_id": 2, "product_id": 2, "score": 7.0, "interaction_count": 3},
    {"user_id": 2, "product_id": 4, "score": 9.0, "interaction_count": 4},
    {"user_id": 3, "product_id": 3, "score": 9.5, "interaction_count": 5},
    {"user_id": 3, "product_id": 5, "score": 12.0, "interaction_count": 6},
    {"user_id": 4, "product_id": 1, "score": 4.0, "interaction_count": 2},
    {"user_id": 4, "product_id": 2, "score": 8.0, "interaction_count": 4},
])

class DataLoader:
    """
    Tải dữ liệu từ Spring Boot backend REST API:
    - Danh mục sản phẩm & thuộc tính kỹ thuật (/api/v1/products)
    - Ma trận hành vi tương tác User-Item (/api/v1/behaviors/export-interactions)
    """

    @classmethod
    def load_products(cls) -> List[ProductFeatureItem]:
        url = f"{settings.backend_url}/products?size=1000"
        try:
            response = requests.get(url, timeout=3)
            if response.status_code == 200:
                data = response.json().get('data', {}).get('content', [])
                if data:
                    items = []
                    for item in data:
                        items.append(ProductFeatureItem(
                            id=item.get('id'),
                            name=item.get('name', ''),
                            slug=item.get('slug', ''),
                            category=item.get('categoryName', ''),
                            brand=item.get('brandName', ''),
                            description=item.get('name', ''),
                            attributes=[],
                            min_price=item.get('minPrice', 0.0)
                        ))
                    logger.info("Đã tải thành công %d sản phẩm từ Backend Spring Boot", len(items))
                    return items
        except Exception as e:
            logger.warning("Không thể kết nối Backend Spring Boot (%s), sử dụng dữ liệu mặc định", e)

        return MOCK_PRODUCTS

    @classmethod
    def load_interactions(cls) -> pd.DataFrame:
        url = f"{settings.backend_url}/behaviors/export-interactions"
        try:
            response = requests.get(url, timeout=3)
            if response.status_code == 200:
                data = response.json().get('data', [])
                if data:
                    df = pd.DataFrame(data)
                    df = df.rename(columns={
                        'userId': 'user_id',
                        'productId': 'product_id',
                        'interactionCount': 'interaction_count',
                        'lastInteractedAt': 'last_interacted_at'
                    })
                    logger.info("Đã tải thành công ma trận tương tác (%d bản ghi) từ Backend Spring Boot", len(df))
                    return df
        except Exception as e:
            logger.warning("Không thể tải ma trận tương tác từ Backend (%s), sử dụng dữ liệu giả lập", e)

        return MOCK_INTERACTIONS
