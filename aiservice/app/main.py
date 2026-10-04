import asyncio
import logging
from contextlib import asynccontextmanager, suppress
from fastapi import FastAPI
from fastapi.responses import JSONResponse
from app.config import settings
from app.routers.recommendations import model_state, router

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(name)s: %(message)s")


async def refresh_catalog():
    while True:
        success = await asyncio.to_thread(model_state.refresh)
        await asyncio.sleep(settings.refresh_seconds if success else settings.retry_seconds)


@asynccontextmanager
async def lifespan(app: FastAPI):
    refresh_task = asyncio.create_task(refresh_catalog())
    try:
        yield
    finally:
        refresh_task.cancel()
        with suppress(asyncio.CancelledError):
            await refresh_task


app = FastAPI(
    title=settings.app_name,
    version=settings.version,
    description="Gợi ý sản phẩm bằng Content-Based Filtering (TF-IDF & Cosine Similarity)",
    lifespan=lifespan,
)
app.include_router(router)


@app.get("/")
def root():
    return {"service": settings.app_name, "version": settings.version, **model_state.status()}


@app.get("/health")
def health():
    return model_state.status()


@app.get("/ready")
def readiness():
    state = model_state.status()
    return JSONResponse(state, status_code=200 if state["ready"] else 503)


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app.main:app", host="0.0.0.0", port=settings.port)
