import logging
from contextlib import asynccontextmanager
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.config import settings
from app.routers import recommendations
from app.routers.recommendations import train_all_models

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s"
)
logger = logging.getLogger("techstore-ai")

@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("Khởi động TechStore AI Recommendation Service...")
    train_all_models()
    yield
    logger.info("Dừng TechStore AI Recommendation Service.")

app = FastAPI(
    title=settings.app_name,
    version=settings.version,
    description="Dịch vụ AI Phân tích Hành vi Người dùng & Gợi ý Sản phẩm Công nghệ (Cosine Similarity, Collaborative Filtering, K-Means Clustering, Hybrid Recommender)",
    lifespan=lifespan
)

# Cấu hình CORS mở rộng cho Vue.js Frontend và Spring Boot Backend
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Đăng ký các router
app.include_router(recommendations.router)

@app.get("/", tags=["Health Check"])
def root():
    return {
        "service": settings.app_name,
        "version": settings.version,
        "status": "UP",
        "docs_url": "/docs"
    }

@app.get("/health", tags=["Health Check"])
def health():
    return {"status": "HEALTHY"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app.main:app", host="0.0.0.0", port=settings.port, reload=True)
