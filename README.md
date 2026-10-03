# Technology-Ecommerce-Application

Ứng dụng thương mại điện tử sản phẩm công nghệ, tích hợp gợi ý bằng Content-Based Filtering.

## Cấu trúc

- `backend/`: Spring Boot 3.4, Java 21, PostgreSQL, JWT.
- `frontend/`: Vue 3, TypeScript, Vite, Pinia, Tailwind CSS.
- `aiservice/`: FastAPI, NumPy, Scikit-learn; một model TF-IDF + Cosine Similarity.
- `docker-compose.yml`: PostgreSQL, backend, AI service và frontend.

## Luồng gợi ý

Frontend luôn giữ mục **Gợi Ý Dành Riêng Cho Bạn** và gọi backend:

- Có lịch sử: backend lấy tương tác của tài khoản từ JWT, gửi lịch sử mới nhất cho AI.
- Khách hoặc tài khoản chưa có lịch sử: backend truy vấn sản phẩm bán chạy.
- AI timeout, lỗi hoặc chưa sẵn sàng: backend trả sản phẩm bán chạy.
- AI trả chưa đủ sản phẩm: bổ sung best seller, rồi sản phẩm có sẵn trong catalog nếu cần.
- Loại sản phẩm đã tương tác, sản phẩm trùng, ngừng bán hoặc không còn biến thể ACTIVE còn hàng.

Best seller được xếp theo tổng `order_items.quantity`, gộp các biến thể của cùng sản phẩm,
chỉ tính đơn `DELIVERED` và `PAID`. Khi chưa có doanh số hợp lệ, fallback dùng sản phẩm thật
đang bán và còn hàng với nguồn `catalog`; không gắn nhãn bán chạy hoặc tạo điểm AI giả.
Mục **Sản Phẩm Bán Chạy** riêng chỉ lấy sản phẩm có doanh số hợp lệ; khi chưa có dữ liệu,
vẫn giữ mục trên trang chủ và hiển thị thông báo rõ ràng.
Trang chủ còn có các danh sách **Sản Phẩm Mới Lên Kệ**, điện thoại, laptop, máy tính bảng,
linh kiện/phụ kiện và tai nghe/âm thanh. Các danh sách lấy catalog ACTIVE có phân trang,
xác định danh mục từ slug và có liên kết **Xem tất cả**, trạng thái tải và nút thử lại khi lỗi.

Trang chi tiết ưu tiên sản phẩm tương tự cùng danh mục; fallback cũng giới hạn trong danh mục đó
và loại sản phẩm đang xem. AI chỉ tính mức tương đồng; backend kiểm tra dữ liệu bán hàng và bổ sung fallback.

### API dành cho frontend

- `GET /api/v1/recommendations/me?limit=4`: xác định người dùng từ JWT; khách được phép gọi.
- `GET /api/v1/recommendations/similar/{productId}?limit=4`: sản phẩm tương tự.
- `GET /api/v1/products/best-sellers?limit=4`: sản phẩm bán chạy thực tế.

API gợi ý trả envelope `ApiResponse` với `data.source`, `data.fallbackReason` và `data.recommendations`.
Mỗi phần tử chứa `product` đầy đủ để hiển thị card, `source` (`content_based`, `best_seller`, `catalog`),
`score` (chỉ có khi tính bằng AI) và `reason`. Có thể có nhiều nguồn trong một danh sách;
`data.source` phản ánh nguồn ưu tiên của danh sách.

### API và vòng đời AI

- Backend xuất catalog có phân trang tại `GET /api/v1/products/recommendation-features`,
  gồm tên, danh mục, thương hiệu, mô tả và thông số kỹ thuật.
- AI tự refresh catalog mỗi 300 giây; khi backend chưa sẵn sàng, retry mỗi 10 giây.
- Model được xây dựng riêng rồi thay thế nguyên tử. Refresh lỗi giữ snapshot thật trước đó;
  quá 900 giây chưa cập nhật thành công thì AI trả 503 để backend dùng fallback.
- Lịch sử được gửi theo mỗi request `POST /api/v1/recommend/user`, không lưu ma trận người dùng chung
  hoặc retrain model cho mỗi lượt thao tác.
- `GET /health`: trạng thái, số sản phẩm, nguồn dữ liệu, thời gian cập nhật.
- `GET /ready`: trả 503 nếu model chưa sẵn sàng hoặc quá hạn.
- Mock chỉ nằm trong test. Collaborative, Hybrid, Popularity, K-Means, Evaluation và AI Analytics UI đã được bỏ.

## Chạy bằng Docker

```powershell
docker compose up -d --build
```

- Frontend: http://localhost:3000
- Backend Swagger: http://localhost:8080/swagger-ui/index.html
- AI readiness: http://localhost:8001/ready
- PostgreSQL: localhost:5433

AI đợi backend healthy. Frontend chỉ phụ thuộc backend nên vẫn hoạt động khi AI không khả dụng.

## Chạy development

Cần Java 21, Node.js và Python. Khởi động PostgreSQL:

```powershell
docker compose up -d postgres
```

Backend:

```powershell
cd backend
.\gradlew.bat bootRun
```

AI:

```powershell
cd aiservice
python -m venv .venv
.\.venv\Scripts\python.exe -m pip install -r requirements.txt
.\.venv\Scripts\python.exe -m uvicorn app.main:app --host 0.0.0.0 --port 8001
```

Frontend:

```powershell
cd frontend
npm ci
npm run dev
```

Frontend proxy toàn bộ API qua backend; không còn proxy `/ai-api`.
Các giá trị development mặc định dùng localhost; Docker Compose thiết lập địa chỉ service nội bộ.

## Cấu hình

Xem `.env.example`:

- `AI_SERVICE_URL`: URL AI dùng bởi backend.
- `AI_TIMEOUT_MS`: giới hạn chờ mỗi request AI (bao gồm kết nối/DNS), mặc định 1500 ms.
- `BACKEND_API_URL`: URL backend dùng để AI đọc catalog.
- `AI_REFRESH_SECONDS`, `AI_RETRY_SECONDS`, `AI_MAX_MODEL_AGE_SECONDS`: chu kỳ refresh, retry và giới hạn tuổi model.

## Kiểm tra

```powershell
cd backend
.\gradlew.bat test --no-daemon
```

Test query bán chạy chạy trên H2 trong bộ nhớ, không thay đổi database ứng dụng.

```powershell
cd aiservice
.\.venv\Scripts\python.exe -m pytest -q -p no:cacheprovider
```

```powershell
cd frontend
npm run build
```

Build frontend kiểm tra cả TypeScript trong các file Vue bằng `vue-tsc`.
