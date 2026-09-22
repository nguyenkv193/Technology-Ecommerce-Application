import pandas as pd
from typing import List, Dict
from app.models.schemas import ProductRecommendationItem
from app.services.popularity_recommender import PopularityRecommender
from app.services.content_based_recommender import ContentBasedRecommender
from app.services.collaborative_recommender import CollaborativeRecommender

class HybridRecommender:
    """
    Hệ thống gợi ý lai kết hợp (Hybrid Recommendation System).
    Kết hợp điểm số từ Collaborative Filtering (60%) và Content-Based Filtering (40%),
    đồng thời tự động Fallback về Popularity Recommender khi xảy ra hiện tượng Khởi động nguội (Cold Start).
    """

    def __init__(
        self,
        popularity: PopularityRecommender,
        content_based: ContentBasedRecommender,
        collaborative: CollaborativeRecommender,
        alpha_cf: float = 0.6,
        beta_cb: float = 0.4
    ):
        self.popularity = popularity
        self.content_based = content_based
        self.collaborative = collaborative
        self.alpha_cf = alpha_cf
        self.beta_cb = beta_cb

    def recommend(self, user_id: int, interactions_df: pd.DataFrame, limit: int = 10) -> List[ProductRecommendationItem]:
        # 1. Kiểm tra Cold Start: nếu user chưa có tương tác -> Dùng Popularity
        user_has_history = (
            interactions_df is not None and
            not interactions_df.empty and
            user_id in interactions_df['user_id'].values
        )

        if not user_has_history:
            return self.popularity.recommend(limit=limit)

        # 2. Lấy kết quả từ Collaborative Filtering và Content-Based
        cf_results = self.collaborative.recommend(user_id=user_id, limit=limit * 2)
        cb_results = self.content_based.recommend_for_user(user_id=user_id, interactions_df=interactions_df, limit=limit * 2)

        cf_dict: Dict[int, float] = {item.product_id: item.score for item in cf_results}
        cb_dict: Dict[int, float] = {item.product_id: item.score for item in cb_results}

        # 3. Trộn điểm kết hợp: Score = alpha * CF + beta * CB
        all_candidate_pids = set(cf_dict.keys()).union(set(cb_dict.keys()))

        if not all_candidate_pids:
            # Fallback nếu cả CF và CB không tìm ra
            return self.popularity.recommend(limit=limit)

        hybrid_scores = []
        for pid in all_candidate_pids:
            score_cf = cf_dict.get(pid, 0.0)
            score_cb = cb_dict.get(pid, 0.0)

            # Tính điểm trung bình có trọng số
            final_score = (self.alpha_cf * score_cf) + (self.beta_cb * score_cb)

            if score_cf > 0 and score_cb > 0:
                reason = "Gợi ý tối ưu kết hợp từ thói quen của bạn và sở thích từ cộng đồng (Hybrid)"
            elif score_cf > 0:
                reason = "Người dùng có cùng sở thích cũng đã chọn sản phẩm này"
            else:
                reason = "Phù hợp với thông số kỹ thuật các sản phẩm bạn quan tâm"

            hybrid_scores.append(ProductRecommendationItem(
                product_id=pid,
                score=round(final_score, 4),
                reason=reason
            ))

        hybrid_scores.sort(key=lambda x: x.score, reverse=True)

        # 4. Nếu chưa đủ số lượng limit, bù thêm các sản phẩm Popularity
        if len(hybrid_scores) < limit:
            existing_pids = {item.product_id for item in hybrid_scores}
            fillers = self.popularity.recommend(limit=limit - len(hybrid_scores), exclude_product_ids=list(existing_pids))
            hybrid_scores.extend(fillers)

        return hybrid_scores[:limit]
