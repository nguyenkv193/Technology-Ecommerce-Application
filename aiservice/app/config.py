import os
from pydantic import BaseModel

class Settings(BaseModel):
    app_name: str = "TechStore AI Recommendation Service"
    version: str = "1.0.0"
    port: int = int(os.getenv("AI_SERVICE_PORT", "8001"))
    backend_url: str = os.getenv("BACKEND_API_URL", "http://localhost:8080/api/v1")

    # Implicit Feedback Weights cho hành vi người dùng
    weight_view: float = 1.0
    weight_search: float = 1.5
    weight_add_to_cart: float = 2.5
    weight_wishlist: float = 3.0
    weight_purchase: float = 5.0
    weight_rating: float = 4.0

    # Trọng số thuật toán gợi ý Hybrid (CF + Content-Based)
    hybrid_alpha_cf: float = 0.6  # Trọng số Collaborative Filtering
    hybrid_beta_cb: float = 0.4   # Trọng số Content-Based

settings = Settings()
