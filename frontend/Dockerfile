# =========================================================
# Dockerfile cho Vue 3 Frontend (Multi-stage Nginx)
# Hệ thống: TechStore E-Commerce & AI Recommendation
# =========================================================

# Stage 1: Build static assets bằng Node Alpine
FROM node:20-alpine AS builder

WORKDIR /app

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
