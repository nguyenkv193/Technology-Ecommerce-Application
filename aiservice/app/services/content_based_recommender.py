import numpy as np
import pandas as pd
from typing import List, Dict
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from app.models.schemas import ProductRecommendationItem, ProductFeatureItem

class ContentBasedRecommender:
    """
    Thuật toán lọc dựa trên nội dung (Content-Based Filtering với Cosine Similarity).
    Sử dụng TF-IDF để vector hóa các thuộc tính kỹ thuật động (CPU, RAM, GPU, Màn hình),
    tên sản phẩm, thương hiệu và mô tả để tìm kiếm sản phẩm tương đồng về cấu hình và tính năng.
    """

    def __init__(self):
        self.vectorizer = TfidfVectorizer(token_pattern=r'(?u)\b\w+\b')
        self.tfidf_matrix = None
        self.similarity_matrix = None
        self.product_id_to_idx: Dict[int, int] = {}
        self.idx_to_product_id: Dict[int, int] = {}
        self.products: List[ProductFeatureItem] = []

    def _extract_content_text(self, item: ProductFeatureItem) -> str:
        tokens = [item.name, item.brand, item.category, item.description]
        for attr in item.attributes:
            name = attr.get('name', '')
            val = attr.get('value', '')
            tokens.append(f"{name} {val} {name}_{val}")
        return " ".join(filter(None, tokens))

    def fit(self, products: List[ProductFeatureItem]):
        if not products:
            return

        self.products = products
        self.product_id_to_idx = {p.id: idx for idx, p in enumerate(products)}
        self.idx_to_product_id = {idx: p.id for idx, p in enumerate(products)}

        corpus = [self._extract_content_text(p) for p in products]
        self.tfidf_matrix = self.vectorizer.fit_transform(corpus)
        self.similarity_matrix = cosine_similarity(self.tfidf_matrix, self.tfidf_matrix)

    def recommend_similar(self, product_id: int, limit: int = 10) -> List[ProductRecommendationItem]:
        """
        Gợi ý sản phẩm tương tự khi người dùng đang xem trang chi tiết một sản phẩm công nghệ.
        """
        if self.similarity_matrix is None or product_id not in self.product_id_to_idx:
            return []

        target_idx = self.product_id_to_idx[product_id]
        scores = self.similarity_matrix[target_idx]

        # Sắp xếp giảm dần theo điểm Cosine Similarity
        ranked_indices = np.argsort(scores)[::-1]

        results = []
        for idx in ranked_indices:
            pid = self.idx_to_product_id[idx]
            if pid == product_id:
                continue
            sim_score = float(scores[idx])
            if sim_score <= 0.001:
                continue

            percent = int(sim_score * 100)
            results.append(ProductRecommendationItem(
                product_id=pid,
                score=round(sim_score, 4),
                reason=f"Độ tương đồng thông số & thương hiệu đạt {percent}% (Cosine Similarity)"
            ))

            if len(results) >= limit:
                break

        return results

    def recommend_for_user(self, user_id: int, interactions_df: pd.DataFrame, limit: int = 10) -> List[ProductRecommendationItem]:
        """
        Gợi ý cho người dùng dựa trên hồ sơ sở thích (User Profile Vector)
        tính bằng trung bình có trọng số các sản phẩm họ đã xem/thêm giỏ/mua.
        """
        if self.tfidf_matrix is None or interactions_df is None or interactions_df.empty:
            return []

        user_actions = interactions_df[interactions_df['user_id'] == user_id]
        if user_actions.empty:
            return []

        # Tạo vector sở thích của User bằng trung bình có trọng số
        user_vector = np.zeros((1, self.tfidf_matrix.shape[1]))
        total_weight = 0.0
        interacted_pids = set(user_actions['product_id'].tolist())

        for _, row in user_actions.iterrows():
            pid = int(row['product_id'])
            if pid in self.product_id_to_idx:
                idx = self.product_id_to_idx[pid]
                weight = float(row['score'])
                user_vector += self.tfidf_matrix[idx].toarray() * weight
                total_weight += weight

        if total_weight > 0:
            user_vector /= total_weight

        # Tính Cosine Similarity giữa User Profile Vector và toàn bộ sản phẩm
        user_scores = cosine_similarity(user_vector, self.tfidf_matrix)[0]
        ranked_indices = np.argsort(user_scores)[::-1]

        results = []
        for idx in ranked_indices:
            pid = self.idx_to_product_id[idx]
            if pid in interacted_pids:
                continue
            sim_score = float(user_scores[idx])
            if sim_score <= 0.001:
                continue

            results.append(ProductRecommendationItem(
                product_id=pid,
                score=round(sim_score, 4),
                reason=f"Phù hợp với thông số và danh mục bạn hay quan tâm ({int(sim_score * 100)}%)"
            ))

            if len(results) >= limit:
                break

        return results
