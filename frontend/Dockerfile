# =========================================================
# Dockerfile cho Vue 3 Frontend (Multi-stage Nginx)
# Hệ thống: TechStore E-Commerce & AI Recommendation
# =========================================================

# Stage 1: Build static assets bằng Node Alpine
FROM node:20-alpine AS builder

WORKDIR /app

ARG VITE_GOOGLE_OAUTH_ENABLED=false
ENV VITE_GOOGLE_OAUTH_ENABLED=$VITE_GOOGLE_OAUTH_ENABLED

ARG VITE_VIETQR_BANK_ID=MB
ARG VITE_VIETQR_BANK_NAME="MB Bank (Ngân hàng Quân Đội)"
ARG VITE_VIETQR_ACCOUNT_NO=0388889999
ARG VITE_VIETQR_ACCOUNT_NAME="CONG TY TECHSTORE VIET NAM"
ENV VITE_VIETQR_BANK_ID="$VITE_VIETQR_BANK_ID"
ENV VITE_VIETQR_BANK_NAME="$VITE_VIETQR_BANK_NAME"
ENV VITE_VIETQR_ACCOUNT_NO="$VITE_VIETQR_ACCOUNT_NO"
ENV VITE_VIETQR_ACCOUNT_NAME="$VITE_VIETQR_ACCOUNT_NAME"

COPY package*.json ./
RUN npm ci --no-audit --no-fund

COPY . .
RUN npm run build

# Stage 2: Serve bằng Nginx Alpine siêu nhẹ
FROM nginx:alpine AS runner

COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
