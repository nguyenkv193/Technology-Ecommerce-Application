from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any

class ProductFeatureItem(BaseModel):
    id: int
    name: str
    slug: str
    category: str = ""
    brand: str = ""
    description: str = ""
    attributes: List[Dict[str, str]] = []
    min_price: Optional[float] = 0.0

class UserInteractionItem(BaseModel):
    user_id: int
    product_id: int
    score: float
    interaction_count: int = 1

class ProductRecommendationItem(BaseModel):
    product_id: int
    score: float = Field(..., description="Điểm số phù hợp được thuật toán tính toán")
    reason: str = Field(..., description="Giải thích lý do gợi ý (Explainability)")

class RecommendationResponse(BaseModel):
    algorithm: str
    user_id: Optional[int] = None
    target_product_id: Optional[int] = None
    total: int
    recommendations: List[ProductRecommendationItem]

class EvaluationMetricsResponse(BaseModel):
    algorithm: str
    k: int
    precision_at_k: float
    recall_at_k: float
    ndcg_at_k: float
    hit_rate: float
    total_users_evaluated: int

class UserClusterItem(BaseModel):
    user_id: int
    cluster_id: int
    persona_name: str
    avg_score: float
    total_interactions: int

class ClusterAnalysisResponse(BaseModel):
    total_clusters: int
    clusters: List[Dict[str, Any]]
    user_assignments: List[UserClusterItem]
