import logging
from datetime import datetime, timezone
from threading import Lock
from time import monotonic
from fastapi import APIRouter, HTTPException, Path, Query
from app.config import settings
from app.models.schemas import PersonalizedRequest, RecommendationResponse
from app.services.content_based_recommender import ContentBasedRecommender
from app.services.data_loader import DataLoader

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/api/v1", tags=["Content-Based Recommendations"])


class ModelState:
    def __init__(self):
        self.lock = Lock()
        self.model = None
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
                self.model = model
                self.updated_at = datetime.now(timezone.utc).isoformat()
                self.updated_monotonic = monotonic()
                self.last_error = None
            logger.info("Updated Content-Based model with %d real products", len(products))
            return True
        except Exception as exc:
            with self.lock:
                self.last_error = str(exc)
            logger.warning("Catalog refresh failed: %s", exc)
            return False
        finally:
            with self.lock:
                self.refreshing = False

    def _ready(self):
        return self.model is not None and self.updated_monotonic is not None and (
            monotonic() - self.updated_monotonic <= settings.max_model_age_seconds
        )

    def get_model(self):
        with self.lock:
            if not self._ready():
                raise HTTPException(status_code=503, detail="Content-Based model is not ready")
            return self.model

    def status(self):
        with self.lock:
            ready = self._ready()
            return {
                "status": "HEALTHY" if ready and self.last_error is None else "DEGRADED",
                "ready": ready,
                "data_source": "backend",
                "product_count": len(self.model.products) if self.model is not None else 0,
                "last_updated_at": self.updated_at,
                "refresh_failed": self.last_error is not None,
            }


model_state = ModelState()


@router.get("/recommend/similar/{product_id}", response_model=RecommendationResponse)
def similar(product_id: int = Path(gt=0), limit: int = Query(10, ge=1, le=50)):
    results = model_state.get_model().recommend_similar(product_id, limit)
    return RecommendationResponse(target_product_id=product_id, total=len(results), recommendations=results)


@router.post("/recommend/user", response_model=RecommendationResponse)
def personalized(request: PersonalizedRequest):
    results = model_state.get_model().recommend_for_user(request.interactions, request.limit)
    return RecommendationResponse(total=len(results), recommendations=results)
