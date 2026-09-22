# Technology-Ecommerce-Application

> **Đề tài Đồ án Tốt nghiệp**: Nghiên cứu và xây dựng hệ thống thương mại điện tử về sản phẩm công nghệ tích hợp AI trong phân tích hành vi người dùng và gợi ý sản phẩm.

---

## 1. Cấu trúc Monorepo

```text
Technology-Ecommerce-Application/
├── backend/                  # Spring Boot 3.4 (Java 21, Gradle, Modular Monolith)
├── frontend/                 # Vue 3 (TypeScript, Vite, Pinia, Tailwind CSS)
├── ai-service/               # Python AI Service (FastAPI, Scikit-learn, Pandas)
├── infrastructure/           # Docker, Nginx, Flyway, Monitoring configs
├── docker-compose.yml        # Chạy PostgreSQL 17 cục bộ
├── .env.example              # Mẫu biến môi trường
├── .gitignore
└── README.md
```

---

## 2. Công nghệ Cốt lõi (Tech Stack)

- **Backend**: Java 21, Spring Boot 3.4.x, Gradle, Spring Data JPA, Hibernate, Spring Security, JWT, Flyway, MapStruct, Lombok, Springdoc OpenAPI 3.
- **Frontend**: Vue 3, TypeScript, Vite, Pinia, Vue Router, Tailwind CSS, Axios.
- **Database**: PostgreSQL 17.
- **AI Service**: Python, FastAPI, Pandas, NumPy, Scikit-learn (Content-Based, Collaborative Filtering, K-Means, Hybrid Recommendation).
- **Hạ tầng**: Docker, Docker Compose, Nginx.

---

## 3. Khởi động Nhanh (Quick Start)

### Bước 1: Khởi động Cơ sở dữ liệu PostgreSQL 17

```powershell
docker compose up -d postgres
```
- Port: `5433` (Ánh xạ vào cổng container `5432` để tránh xung đột cổng 5432 có sẵn).
- Database: `techstore_db`
- Username: `techstore_user`
- Password: `techstore_secure_password_2026`

### Bước 2: Chạy Backend (Spring Boot)

```powershell
cd backend
./gradlew bootRun
```
- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### Bước 3: Chạy Frontend (Vue 3)

```powershell
cd frontend
npm install
npm run dev
```
