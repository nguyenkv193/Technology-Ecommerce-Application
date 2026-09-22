import pandas as pd
from typing import List
from app.models.schemas import ProductRecommendationItem

class PopularityRecommender:
    """
    Thuật toán gợi ý dựa trên độ phổ biến (Popularity-Based Recommendation).
    Được áp dụng chủ yếu cho bài toán Khởi động nguội (Cold Start) khi người dùng
    mới chưa có lịch sử tương tác nào trong hệ thống.
    """

    def __init__(self):
        self.popular_products: List[ProductRecommendationItem] = []

    def fit(self, interactions_df: pd.DataFrame, all_product_ids: List[int] = None):
        """
        Huấn luyện tính điểm phổ biến:
        Score = tổng trọng số hành vi (View, Cart, Wishlist, Purchase, Rating) * log(1 + số lần tương tác).
        """
        if interactions_df is None or interactions_df.empty:
            # Nếu chưa có tương tác, fallback gợi ý danh sách product ID có sẵn
            if all_product_ids:
                self.popular_products = [
                    ProductRecommendationItem(
                        product_id=pid,
                        score=1.0,
                        reason="Sản phẩm nổi bật trong danh mục công nghệ"
                    ) for pid in all_product_ids
                ]
            else:
                self.popular_products = []
            return

        # Nhóm theo product_id để tính tổng điểm và số lượt tương tác
        grouped = interactions_df.groupby('product_id').agg(
            total_score=('score', 'sum'),
            total_count=('interaction_count', 'sum')
        ).reset_index()

        # Chuẩn hóa điểm phổ biến kết hợp tổng điểm và tần suất
        grouped['final_score'] = grouped['total_score'] * (1 + grouped['total_count'].apply(lambda x: x ** 0.5))
        grouped = grouped.sort_values(by='final_score', ascending=False)

        max_score = grouped['final_score'].max() if not grouped.empty and grouped['final_score'].max() > 0 else 1.0

        self.popular_products = [
            ProductRecommendationItem(
                product_id=int(row['product_id']),
                score=round(float(row['final_score'] / max_score), 4),
                reason="Sản phẩm công nghệ thịnh hành & được quan tâm nhiều nhất"
            ) for _, row in grouped.iterrows()
        ]

    def recommend(self, limit: int = 10, exclude_product_ids: List[int] = None) -> List[ProductRecommendationItem]:
        excludes = set(exclude_product_ids or [])
        results = [p for p in self.popular_products if p.product_id not in excludes]
        return results[:limit]
