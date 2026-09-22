import math
import pandas as pd
from typing import List, Set, Dict
from app.models.schemas import EvaluationMetricsResponse

class RecommendationEvaluator:
    """
    Module đánh giá hiệu năng thuật toán gợi ý (Recommendation Evaluation Metrics).
    Phục vụ trực tiếp cho Đồ án Tốt nghiệp với các chỉ số chuẩn khoa học:
    - Precision@K
    - Recall@K
    - NDCG@K (Normalized Discounted Cumulative Gain)
    - Hit Rate@K
    """

    @staticmethod
    def dcg_at_k(recommended_ids: List[int], ground_truth: Set[int], k: int) -> float:
        dcg = 0.0
        for i, item_id in enumerate(recommended_ids[:k]):
            if item_id in ground_truth:
                dcg += 1.0 / math.log2(i + 2)  # i=0 -> rank=1 -> log2(2)=1
        return dcg

    @staticmethod
    def idcg_at_k(num_relevant: int, k: int) -> float:
        idcg = 0.0
        for i in range(min(num_relevant, k)):
            idcg += 1.0 / math.log2(i + 2)
        return idcg

    @classmethod
    def evaluate(
        cls,
        algorithm_name: str,
        recommendation_func,
        test_interactions_df: pd.DataFrame,
        k: int = 5
    ) -> EvaluationMetricsResponse:
        """
        Đánh giá bằng phương pháp Leave-One-Out hoặc Train/Test Split:
        Duyệt qua từng người dùng trong tập test, so sánh danh sách Top-K dự đoán với sản phẩm thực tế người dùng quan tâm.
        """
        if test_interactions_df is None or test_interactions_df.empty:
            return EvaluationMetricsResponse(
                algorithm=algorithm_name,
                k=k,
                precision_at_k=0.0,
                recall_at_k=0.0,
                ndcg_at_k=0.0,
                hit_rate=0.0,
                total_users_evaluated=0
            )

        users = test_interactions_df['user_id'].unique()
        total_precision = 0.0
        total_recall = 0.0
        total_ndcg = 0.0
        total_hits = 0.0
        evaluated_users = 0

        for user_id in users:
            ground_truth = set(test_interactions_df[test_interactions_df['user_id'] == user_id]['product_id'].tolist())
            if not ground_truth:
                continue

            recommended_items = recommendation_func(user_id=user_id, limit=k)
            rec_ids = [item.product_id for item in recommended_items]

            hits = len(set(rec_ids).intersection(ground_truth))

            # Precision@K
            precision = hits / k if k > 0 else 0.0
            total_precision += precision

            # Recall@K
            recall = hits / len(ground_truth) if len(ground_truth) > 0 else 0.0
            total_recall += recall

            # Hit Rate
            hit_rate = 1.0 if hits > 0 else 0.0
            total_hits += hit_rate

            # NDCG@K
            dcg = cls.dcg_at_k(rec_ids, ground_truth, k)
            idcg = cls.idcg_at_k(len(ground_truth), k)
            ndcg = (dcg / idcg) if idcg > 0 else 0.0
            total_ndcg += ndcg

            evaluated_users += 1

        n = evaluated_users if evaluated_users > 0 else 1
        return EvaluationMetricsResponse(
            algorithm=algorithm_name,
            k=k,
            precision_at_k=round(total_precision / n, 4),
            recall_at_k=round(total_recall / n, 4),
            ndcg_at_k=round(total_ndcg / n, 4),
            hit_rate=round(total_hits / n, 4),
            total_users_evaluated=evaluated_users
        )
