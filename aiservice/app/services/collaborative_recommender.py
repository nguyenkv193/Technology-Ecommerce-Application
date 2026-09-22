import numpy as np
import pandas as pd
from typing import List, Dict
from sklearn.metrics.pairwise import cosine_similarity
from app.models.schemas import ProductRecommendationItem

class CollaborativeRecommender:
    """
    Thuật toán lọc cộng tác (Item-Based Collaborative Filtering).
    Dựa trên sự tương đồng về hành vi giữa các sản phẩm được người dùng cùng tương tác.
    Công thức: Dự đoán điểm = Tổng(Độ tương đồng giữa item i và các item j user đã tương tác * điểm của item j).
    """

    def __init__(self):
        self.item_similarity_df: pd.DataFrame = pd.DataFrame()
        self.user_item_matrix: pd.DataFrame = pd.DataFrame()

    def fit(self, interactions_df: pd.DataFrame):
        if interactions_df is None or interactions_df.empty:
            self.item_similarity_df = pd.DataFrame()
            self.user_item_matrix = pd.DataFrame()
            return

        # Tạo ma trận tương tác User-Item
        self.user_item_matrix = interactions_df.pivot_table(
            index='user_id',
            columns='product_id',
            values='score',
            aggfunc='sum',
            fill_value=0
        )

        if self.user_item_matrix.shape[1] <= 1:
            return

        # Tính ma trận tương đồng Cosine Similarity giữa các Item (Cột)
        sim_matrix = cosine_similarity(self.user_item_matrix.T)
        items = self.user_item_matrix.columns

        self.item_similarity_df = pd.DataFrame(sim_matrix, index=items, columns=items)

    def recommend(self, user_id: int, limit: int = 10) -> List[ProductRecommendationItem]:
        if self.item_similarity_df.empty or user_id not in self.user_item_matrix.index:
            return []

        user_ratings = self.user_item_matrix.loc[user_id]
        interacted_items = user_ratings[user_ratings > 0].index.tolist()

        if not interacted_items:
            return []

        # Tính điểm dự đoán cho tất cả các item chưa được user tương tác
        scores = {}
        for item in self.item_similarity_df.columns:
            if item in interacted_items:
                continue

            sim_series = self.item_similarity_df.loc[item, interacted_items]
            ratings_series = user_ratings[interacted_items]

            sim_sum = sim_series.abs().sum()
            if sim_sum > 0:
                predicted_score = (sim_series * ratings_series).sum() / sim_sum
                if predicted_score > 0:
                    scores[item] = float(predicted_score)

        sorted_items = sorted(scores.items(), key=lambda x: x[1], reverse=True)[:limit]
        max_val = sorted_items[0][1] if sorted_items and sorted_items[0][1] > 0 else 1.0

        return [
            ProductRecommendationItem(
                product_id=int(pid),
                score=round(float(score / max_val), 4),
                reason="Người dùng có sở thích tương tự bạn cũng rất quan tâm sản phẩm này"
            ) for pid, score in sorted_items
        ]
