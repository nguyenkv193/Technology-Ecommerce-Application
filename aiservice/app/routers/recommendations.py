import logging
from datetime import datetime, timezone
from threading import Lock
from time import monotonic
from typing import Literal
from fastapi import APIRouter, HTTPException, Path, Query
from app.config import settings
from app.models.schemas import (
    PersonalizedRequest,
    ProductRecommendationItem,
    RecommendationResponse,
    TrainingInteractionItem,
    UserInteractionItem,
)
from app.services.content_based_recommender import ContentBasedRecommender
from app.services.data_loader import DataLoader
from app.services.hybrid_recommender import HybridRecommender
from app.services.item_based_collaborative_recommender import ItemBasedCollaborativeRecommender

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/api/v1", tags=["Product Recommendations"])


class ModelState:
    def __init__(self) -> None:
        self.lock = Lock()
        self.model = None
        self.collaborative_model = ItemBasedCollaborativeRecommender()
        self.training_interactions: list[TrainingInteractionItem] = []
        self.training_interaction_count = 0
        self.updated_at = None
        self.updated_monotonic = None
        self.last_error = None
        self.refreshing = False

    def refresh(self) -> bool:
        with self.lock:
            if self.refreshing:
                return False
            self.refreshing = True
        try:
            products = DataLoader.load_products()
            model = ContentBasedRecommender()
            model.fit(products)

            with self.lock:
                previous_interactions = self.training_interactions
            training_interactions: list[TrainingInteractionItem] = []
            interaction_error = None
            if settings.backend_api_key:
                try:
                    training_interactions = DataLoader.load_interactions()
                except Exception as exc:
                    interaction_error = exc
                    training_interactions = previous_interactions
                    logger.warning("Interaction refresh failed; retaining previous CF snapshot: %s", exc)

            collaborative_model = ItemBasedCollaborativeRecommender()
            collaborative_model.fit(products, training_interactions)
            with self.lock:
                self.model = model
                self.collaborative_model = collaborative_model
                self.training_interactions = training_interactions
                self.training_interaction_count = collaborative_model.training_interaction_count
                self.updated_at = datetime.now(timezone.utc).isoformat()
                self.updated_monotonic = monotonic()
                self.last_error = str(interaction_error) if interaction_error else None
            logger.info(
                "Updated recommendation snapshot: products=%d, user-item interactions=%d, users=%d, item-pairs=%d",
                len(products), collaborative_model.training_interaction_count,
                collaborative_model.training_user_count, collaborative_model.similarity.nnz,
            )
            # Catalog training succeeded; interaction failures should not trigger a tight retry loop.
            return True
        except Exception as exc:
            with self.lock:
                self.last_error = str(exc)
            logger.warning("Catalog refresh failed: %s", exc)
            return False
        finally:
            with self.lock:
                self.refreshing = False

    def _ready(self) -> bool:
        return self.model is not None and self.updated_monotonic is not None and (
            monotonic() - self.updated_monotonic <= settings.max_model_age_seconds
        )

    def get_model(self) -> ContentBasedRecommender:
        with self.lock:
            if not self._ready():
                raise HTTPException(status_code=503, detail="Content-Based model is not ready")
            return self.model

    def recommend_for_user(
        self,
        interactions: list[UserInteractionItem],
        limit: int,
        strategy: Literal["hybrid", "content_based", "collaborative"],
    ) -> tuple[str, list[ProductRecommendationItem]]:
        with self.lock:
            if not self._ready():
                raise HTTPException(status_code=503, detail="Recommendation models are not ready")
            recommender = HybridRecommender(self.model, self.collaborative_model)
        return recommender.recommend_for_user(interactions, limit, strategy)

    def status(self) -> dict[str, object]:
        with self.lock:
            ready = self._ready()
            return {
                "status": "HEALTHY" if ready and self.last_error is None else "DEGRADED",
                "ready": ready,
                "data_source": "backend",
                "product_count": len(self.model.products) if self.model is not None else 0,
                "training_interaction_count": self.training_interaction_count,
                "collaborative_ready": bool(self.collaborative_model.similarity.nnz),
                "enabled_strategies": ["content_based", "collaborative", "hybrid"],
                "collaborative_enabled": bool(settings.backend_api_key),
                "last_updated_at": self.updated_at,
                "refresh_failed": self.last_error is not None,
            }


model_state = ModelState()


@router.get("/recommend/similar/{product_id}", response_model=RecommendationResponse)
def similar(product_id: int = Path(gt=0), limit: int = Query(10, ge=1, le=50)) -> RecommendationResponse:
    results = model_state.get_model().recommend_similar(product_id, limit)
    return RecommendationResponse(target_product_id=product_id, total=len(results), recommendations=results)


@router.post("/recommend/user", response_model=RecommendationResponse)
def personalized(
    request: PersonalizedRequest,
    strategy: Literal["hybrid", "content_based", "collaborative"] = Query(default="hybrid"),
) -> RecommendationResponse:
    source, results = model_state.recommend_for_user(request.interactions, request.limit, strategy)
    return RecommendationResponse(source=source, total=len(results), recommendations=results)
