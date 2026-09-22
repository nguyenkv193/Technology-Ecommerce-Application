import logging
from fastapi import APIRouter, Query, HTTPException
from typing import Optional
from app.config import settings
from app.models.schemas import (
    RecommendationResponse,
    EvaluationMetricsResponse,
    ClusterAnalysisResponse
)
from app.services.data_loader import DataLoader
from app.services.popularity_recommender import PopularityRecommender
from app.services.content_based_recommender import ContentBasedRecommender
from app.services.collaborative_recommender import CollaborativeRecommender
from app.services.user_clustering import UserClusteringService
from app.services.hybrid_recommender import HybridRecommender
from app.services.evaluator import RecommendationEvaluator

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/api/v1", tags=["AI Recommendations & Analytics"])

# Khởi tạo các Recommender Models Singleton
popularity_engine = PopularityRecommender()
content_engine = ContentBasedRecommender()
collaborative_engine = CollaborativeRecommender()
clustering_service = UserClusteringService(n_clusters=3)
hybrid_engine = HybridRecommender(
    popularity=popularity_engine,
    content_based=content_engine,
    collaborative=collaborative_engine,
    alpha_cf=settings.hybrid_alpha_cf,
    beta_cb=settings.hybrid_beta_cb
)

current_interactions_df = None

def train_all_models():
    global current_interactions_df
    logger.info("Bắt đầu huấn luyện và cập nhật các mô hình gợi ý AI...")

    products = DataLoader.load_products()
    interactions_df = DataLoader.load_interactions()
    current_interactions_df = interactions_df

    all_pids = [p.id for p in products]

    # 1. Huấn luyện Popularity
    popularity_engine.fit(interactions_df, all_pids)

    # 2. Huấn luyện Content-Based (TF-IDF & Cosine Similarity)
    content_engine.fit(products)

    # 3. Huấn luyện Collaborative Filtering (Item-Based)
    collaborative_engine.fit(interactions_df)

    # 4. Huấn luyện Phân cụm người dùng K-Means
    clustering_service.fit(interactions_df)

    logger.info("Huấn luyện hoàn tất thành công cho toàn bộ hệ thống gợi ý.")

@router.get("/recommend/popular", response_model=RecommendationResponse)
def get_popular_recommendations(limit: int = Query(10, ge=1, le=50)):
    """
    Gợi ý sản phẩm nổi bật & thịnh hành nhất (Popularity-Based).
    Dành cho khách truy cập mới hoặc chưa có dữ liệu lịch sử (Cold Start).
    """
    results = popularity_engine.recommend(limit=limit)
    return RecommendationResponse(
        algorithm="Popularity-Based (Cold-Start)",
        total=len(results),
        recommendations=results
    )

@router.get("/recommend/similar/{product_id}", response_model=RecommendationResponse)
def get_similar_products(product_id: int, limit: int = Query(10, ge=1, le=50)):
    """
    Gợi ý sản phẩm tương tự về cấu hình, thông số kỹ thuật (Content-Based Cosine Similarity).
    Áp dụng khi người dùng đang xem một sản phẩm cụ thể.
    """
    results = content_engine.recommend_similar(product_id=product_id, limit=limit)
    return RecommendationResponse(
        algorithm="Content-Based Filtering (Cosine Similarity on Dynamic Specs)",
        target_product_id=product_id,
        total=len(results),
        recommendations=results
    )

@router.get("/recommend/user/{user_id}", response_model=RecommendationResponse)
def get_personalized_recommendations(user_id: int, limit: int = Query(10, ge=1, le=50)):
    """
    Gợi ý cá nhân hóa đa tiêu chí (Hybrid Recommendation: 60% Collaborative + 40% Content-Based + Fallback Popularity).
    Tối ưu hóa tỉ lệ chuyển đổi dựa trên hành vi cụ thể của từng người dùng.
    """
    results = hybrid_engine.recommend(user_id=user_id, interactions_df=current_interactions_df, limit=limit)
    return RecommendationResponse(
        algorithm="Hybrid Recommender (Collaborative Filtering + Content-Based)",
        user_id=user_id,
        total=len(results),
        recommendations=results
    )

@router.get("/analytics/clusters", response_model=ClusterAnalysisResponse)
def get_user_clusters():
    """
    Phân tích phân cụm khách hàng (K-Means Clustering).
    Cung cấp thông tin chân dung khách hàng cho Quản trị viên (Admin Dashboard).
    """
    return clustering_service.fit(current_interactions_df)

@router.post("/recommend/evaluate", response_model=EvaluationMetricsResponse)
def evaluate_algorithm(
    algorithm: str = Query("hybrid", pattern="^(hybrid|collaborative|content_based|popularity)$"),
    k: int = Query(5, ge=1, le=20)
):
    """
    Đánh giá độ chính xác của thuật toán gợi ý (Precision@K, Recall@K, NDCG@K, Hit Rate).
    Phục vụ đánh giá thực nghiệm cho Đồ án Tốt nghiệp.
    """
    if current_interactions_df is None or current_interactions_df.empty:
        raise HTTPException(status_code=400, detail="Chưa có dữ liệu ma trận tương tác để đánh giá")

    if algorithm == "collaborative":
        rec_func = lambda user_id, limit: collaborative_engine.recommend(user_id=user_id, limit=limit)
    elif algorithm == "content_based":
        rec_func = lambda user_id, limit: content_engine.recommend_for_user(user_id=user_id, interactions_df=current_interactions_df, limit=limit)
    elif algorithm == "popularity":
        rec_func = lambda user_id, limit: popularity_engine.recommend(limit=limit)
    else:
        rec_func = lambda user_id, limit: hybrid_engine.recommend(user_id=user_id, interactions_df=current_interactions_df, limit=limit)

    return RecommendationEvaluator.evaluate(
        algorithm_name=algorithm.upper(),
        recommendation_func=rec_func,
        test_interactions_df=current_interactions_df,
        k=k
    )

@router.post("/recommend/retrain")
def retrain_models():
    """
    Kích hoạt tái huấn luyện toàn bộ mô hình AI từ dữ liệu mới nhất trong cơ sở dữ liệu.
    """
    train_all_models()
    return {"status": "success", "message": "Đã tái huấn luyện thành công toàn bộ mô hình gợi ý AI"}
