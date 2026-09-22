# =========================================================
# Multi-stage Dockerfile cho Spring Boot 3.4 (Java 21)
# Hệ thống: TechStore E-Commerce & AI Recommendation
# =========================================================

# Stage 1: Build JAR bằng Official Gradle 8.10 Image (Tránh lỗi SSL PKIX khi tải wrapper trong Alpine)
FROM gradle:8.10-jdk21 AS builder

WORKDIR /home/gradle/project

# Copy cấu hình Gradle và source code
COPY build.gradle settings.gradle ./
COPY src ./src

# Đóng gói file JAR thực thi (bỏ qua bước test trong container build)
RUN gradle bootJar --no-daemon -x test

# Stage 2: Runtime Image gọn nhẹ chỉ chứa JRE 21 Alpine
FROM eclipse-temurin:21-jre-alpine AS runner

WORKDIR /app

# Tạo non-root user nhằm đảm bảo an toàn bảo mật hệ thống
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy file jar đã build từ Stage 1
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 8080

# Cấu hình tối ưu hóa bộ nhớ JVM trong môi trường container
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dfile.encoding=UTF-8"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
