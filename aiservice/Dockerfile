# =========================================================
# Dockerfile cho AI Recommendation Service (Python FastAPI)
# Hệ thống: TechStore E-Commerce & AI Recommendation
# =========================================================

FROM python:3.12-slim

WORKDIR /app

# Cài đặt biến môi trường Python
ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    AI_SERVICE_PORT=8001

# Cài đặt curl để hỗ trợ docker healthcheck nếu cần
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Copy và cài đặt dependencies trước để tận dụng Docker cache layer
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

# Copy mã nguồn ứng dụng
COPY app ./app

# Tạo non-root user đảm bảo tính an toàn
RUN useradd -m -u 1000 appuser && chown -R appuser:appuser /app
USER appuser

EXPOSE 8001

HEALTHCHECK --interval=15s --timeout=5s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:8001/ready || exit 1

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8001"]
