import pytest
import pandas as pd
from fastapi.testclient import TestClient
from app.main import app
from app.routers.recommendations import train_all_models
from app.services.data_loader import MOCK_PRODUCTS, MOCK_INTERACTIONS
from app.services.popularity_recommender import PopularityRecommender
from app.services.content_based_recommender import ContentBasedRecommender
from app.services.collaborative_recommender import CollaborativeRecommender
from app.services.user_clustering import UserClusteringService
from app.services.hybrid_recommender import HybridRecommender
from app.services.evaluator import RecommendationEvaluator

@pytest.fixture(scope="session", autouse=True)
def setup_models():
    """Khởi tạo và huấn luyện mô hình cho toàn bộ phiên test"""
    train_all_models()

@pytest.fixture
def client():
    with TestClient(app) as test_client:
        yield test_client

def test_popularity_recommender():
    recommender = PopularityRecommender()
    recommender.fit(MOCK_INTERACTIONS, [1, 2, 3, 4, 5, 6])
    results = recommender.recommend(limit=3)

    assert len(results) > 0
    assert results[0].score >= results[-1].score
    assert all(0.0 <= r.score <= 1.0 for r in results)

def test_content_based_recommender():
    recommender = ContentBasedRecommender()
    recommender.fit(MOCK_PRODUCTS)

    # Gợi ý cho iPhone 16 Pro Max (id=1)
    similar_to_iphone = recommender.recommend_similar(product_id=1, limit=3)
    assert len(similar_to_iphone) > 0
    assert all(0.0 <= r.score <= 1.0 for r in similar_to_iphone)

    # Gợi ý cho MacBook Pro (id=3)
    similar_to_macbook = recommender.recommend_similar(product_id=3, limit=3)
    assert len(similar_to_macbook) > 0
    laptop_pids = [r.product_id for r in similar_to_macbook]
    assert any(pid in [1, 2, 4, 5, 6] for pid in laptop_pids)

def test_collaborative_recommender():
    recommender = CollaborativeRecommender()
    recommender.fit(MOCK_INTERACTIONS)

    # User 1 đã tương tác với [1, 3, 6]
    results = recommender.recommend(user_id=1, limit=3)
    # Kết quả gợi ý không được chứa các item user 1 đã tương tác
    rec_ids = [r.product_id for r in results]
    assert 1 not in rec_ids
    assert 3 not in rec_ids
    assert 6 not in rec_ids

def test_user_clustering():
    clustering = UserClusteringService(n_clusters=2)
    response = clustering.fit(MOCK_INTERACTIONS)

    assert response.total_clusters >= 1
    assert len(response.user_assignments) == len(MOCK_INTERACTIONS['user_id'].unique())
    assert all(u.persona_name for u in response.user_assignments)

def test_hybrid_recommender_cold_start():
    popularity = PopularityRecommender()
    popularity.fit(MOCK_INTERACTIONS, [1, 2, 3, 4, 5, 6])

    cb = ContentBasedRecommender()
    cb.fit(MOCK_PRODUCTS)

    cf = CollaborativeRecommender()
    cf.fit(MOCK_INTERACTIONS)

    hybrid = HybridRecommender(popularity, cb, cf)

    # User mới hoàn toàn (id=9999) -> Kích hoạt cơ chế Cold Start Fallback
    results = hybrid.recommend(user_id=9999, interactions_df=MOCK_INTERACTIONS, limit=3)
    assert len(results) == 3
    assert results[0].score > 0

def test_evaluator_metrics():
    def mock_rec(user_id, limit):
        popularity = PopularityRecommender()
        popularity.fit(MOCK_INTERACTIONS, [1, 2, 3, 4, 5, 6])
        return popularity.recommend(limit=limit)

    metrics = RecommendationEvaluator.evaluate(
        algorithm_name="POPULARITY",
        recommendation_func=mock_rec,
        test_interactions_df=MOCK_INTERACTIONS,
        k=3
    )

    assert metrics.algorithm == "POPULARITY"
    assert metrics.k == 3
    assert 0.0 <= metrics.precision_at_k <= 1.0
    assert 0.0 <= metrics.recall_at_k <= 1.0
    assert 0.0 <= metrics.ndcg_at_k <= 1.0
    assert 0.0 <= metrics.hit_rate <= 1.0

# ----------------- FASTAPI API TESTS -----------------

def test_api_root(client):
    response = client.get("/")
    assert response.status_code == 200
    assert response.json()["status"] == "UP"

def test_api_recommend_popular(client):
    response = client.get("/api/v1/recommend/popular?limit=4")
    assert response.status_code == 200
    data = response.json()
    assert data["total"] > 0
    assert len(data["recommendations"]) <= 4

def test_api_recommend_similar(client):
    response = client.get("/api/v1/recommend/similar/1?limit=3")
    assert response.status_code == 200
    data = response.json()
    assert data["target_product_id"] == 1
    assert len(data["recommendations"]) > 0

def test_api_recommend_user(client):
    response = client.get("/api/v1/recommend/user/1?limit=3")
    assert response.status_code == 200
    data = response.json()
    assert data["user_id"] == 1
    assert len(data["recommendations"]) > 0

def test_api_analytics_clusters(client):
    response = client.get("/api/v1/analytics/clusters")
    assert response.status_code == 200
    data = response.json()
    assert data["total_clusters"] > 0
    assert len(data["user_assignments"]) > 0

def test_api_evaluate(client):
    response = client.post("/api/v1/recommend/evaluate?algorithm=hybrid&k=3")
    assert response.status_code == 200
    data = response.json()
    assert data["k"] == 3
    assert "precision_at_k" in data
    assert "ndcg_at_k" in data
