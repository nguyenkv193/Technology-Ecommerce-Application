from datetime import datetime
from pydantic import BaseModel, ConfigDict, Field


class ProductFeatureItem(BaseModel):
    model_config = ConfigDict(populate_by_name=True)
    id: int = Field(gt=0)
    name: str
    slug: str
    category_id: int | None = Field(default=None, alias="categoryId")
    category: str = ""
    brand: str = ""
    description: str = ""
    attributes: list[dict[str, str]] = Field(default_factory=list)


class UserInteractionItem(BaseModel):
    model_config = ConfigDict(allow_inf_nan=False)
    product_id: int = Field(gt=0)
    score: float = Field(gt=0)


class TrainingInteractionItem(BaseModel):
    model_config = ConfigDict(populate_by_name=True, allow_inf_nan=False)
    user_id: int = Field(gt=0, alias="userId")
    product_id: int = Field(gt=0, alias="productId")
    score: float = Field(gt=0)
    last_interacted_at: datetime = Field(alias="lastInteractedAt")


class PersonalizedRequest(BaseModel):
    interactions: list[UserInteractionItem]
    limit: int = Field(default=10, ge=1, le=50)


class ProductRecommendationItem(BaseModel):
    product_id: int
    score: float
    reason: str
    source: str = "content_based"


class RecommendationResponse(BaseModel):
    source: str = "content_based"
    target_product_id: int | None = None
    total: int
    recommendations: list[ProductRecommendationItem]
