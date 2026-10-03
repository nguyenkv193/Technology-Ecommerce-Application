import os
from pydantic import BaseModel, Field


class Settings(BaseModel):
    app_name: str = "TechStore AI Recommendation Service"
    version: str = "2.0.0"
    port: int = int(os.getenv("AI_SERVICE_PORT", "8001"))
    backend_url: str = os.getenv("BACKEND_API_URL", "http://localhost:8080/api/v1")
    request_timeout_seconds: float = Field(default=float(os.getenv("AI_BACKEND_TIMEOUT_SECONDS", "3")), gt=0)
    refresh_seconds: float = Field(default=float(os.getenv("AI_REFRESH_SECONDS", "300")), gt=0)
    retry_seconds: float = Field(default=float(os.getenv("AI_RETRY_SECONDS", "10")), gt=0)
    max_model_age_seconds: float = Field(default=float(os.getenv("AI_MAX_MODEL_AGE_SECONDS", "900")), gt=0)


settings = Settings()
