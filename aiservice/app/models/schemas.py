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


class PersonalizedRequest(BaseModel):
    interactions: list[UserInteractionItem]
    limit: int = Field(default=10, ge=1, le=50)


class ProductRecommendationItem(BaseModel):
    product_id: int
    score: float
    reason: str


class RecommendationResponse(BaseModel):
    source: str = "content_based"
    target_product_id: int | None = None
    total: int
    recommendations: list[ProductRecommendationItem]
