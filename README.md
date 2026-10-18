# Technology-Ecommerce-Application

## CI / Docker images

GitHub Actions kiểm tra frontend, backend, AI và Docker images trên push/PR. Khi nhánh mặc định vượt qua toàn bộ CI, pipeline publish ba images lên GHCR với tag commit SHA và `latest`; chưa tự deploy lên server.

Xem [hướng dẫn CI/CD](docs/ci-cd.md) để bật workflow, xem artifacts và cấu hình quyền GHCR.

> **Đề tài Đồ án Tốt nghiệp**: Nghiên cứu và xây dựng hệ thống thương mại điện tử về sản phẩm công nghệ tích hợp AI trong phân tích hành vi người dùng và gợi ý sản phẩm.
Ứng dụng thương mại điện tử sản phẩm công nghệ, tích hợp gợi ý Content-Based, Item-Item Collaborative Filtering và Hybrid.

## Cấu trúc

- `backend/`: Spring Boot 3.4, Java 21, PostgreSQL, JWT.
- `frontend/`: Vue 3, TypeScript, Vite, Pinia, Tailwind CSS.
- `aiservice/`: FastAPI, Scikit-learn và ma trận user-item sparse; Content-Based, Item-Item Collaborative và Hybrid.
- `docker-compose.yml`: PostgreSQL, Elasticsearch, backend, AI service và frontend.

## Tìm kiếm sản phẩm

- PostgreSQL vẫn là nguồn dữ liệu chính; Elasticsearch giữ index tìm kiếm `products-v1`.
- Keyword search tìm theo tên, mô tả, thương hiệu, danh mục, SKU và thông số; hỗ trợ lọc kết hợp theo danh mục, thương hiệu, trạng thái và khoảng giá.
- Tìm kiếm keyword mặc định xếp theo độ liên quan; người dùng vẫn có thể chọn sắp xếp theo ngày tạo hoặc tên.
- Tạo/sửa/xóa sản phẩm ghi sự kiện vào `product_search_outbox` trong cùng transaction với PostgreSQL. Backend xử lý và retry đồng bộ index; endpoint tìm kiếm quay về JPA/PostgreSQL khi Elasticsearch không sẵn sàng.
- Khi index chưa tồn tại, backend tạo mapping và nạp catalog hiện có. Admin có thể dựng lại index bằng `POST /api/v1/admin/search/reindex-products`.
- Elasticsearch trong Compose hiện được cấu hình cho local development và tắt security; không expose cấu hình này trực tiếp ra môi trường production.

## Luồng gợi ý

Frontend luôn giữ mục **Gợi Ý Dành Riêng Cho Bạn** và gọi backend:

- Có lịch sử: backend lấy tương tác của tài khoản từ JWT, gửi lịch sử mới nhất cho AI.
- AI kết hợp độ giống nội dung sản phẩm với sản phẩm được người dùng có hành vi tương tự quan tâm; khi chưa đủ dữ liệu CF, tự giảm về Content-Based.
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
Mỗi phần tử chứa `product` đầy đủ để hiển thị card, `source` (`hybrid`, `collaborative`, `content_based`, `best_seller`, `catalog`),
`score` (chỉ có khi tính bằng AI) và `reason`. Có thể có nhiều nguồn trong một danh sách;
`data.source` phản ánh nguồn ưu tiên của danh sách.

### API và vòng đời AI

- Backend xuất catalog có phân trang tại `GET /api/v1/products/recommendation-features`,
  gồm tên, danh mục, thương hiệu, mô tả và thông số kỹ thuật.
- Backend tổng hợp log thành ma trận user-item tại `GET /api/v1/behaviors/recommendation-training-data`;
  endpoint chỉ chấp nhận khóa nội bộ `X-AI-Service-Key`, không gửi tên/email người dùng sang AI.
- Điểm implicit preference: xem 1, thêm giỏ 3, wishlist 4, mua 5; rating 4/5 đóng góp dương,
  rating thấp hơn không được xem là thích. Điểm được nén logarit và giảm một nửa sau mỗi 30 ngày.
- Item-Item CF dùng cosine trên ma trận user-item sparse và chỉ giữ cặp sản phẩm có ít nhất hai người dùng chung;
  Hybrid tăng trọng số CF theo số sản phẩm trong lịch sử người dùng được mô hình hỗ trợ.
- AI tự refresh catalog mỗi 300 giây; khi backend chưa sẵn sàng, retry mỗi 10 giây.
- Model được xây dựng riêng rồi thay thế nguyên tử. Refresh lỗi giữ snapshot thật trước đó;
  quá 900 giây chưa cập nhật thành công thì AI trả 503 để backend dùng fallback.
- Hành vi được xuất theo trang và chụp tại một mốc thời gian; AI giới hạn mặc định 200.000 cặp user-item gần nhất.
  Lịch sử riêng của người đang xem vẫn được gửi theo từng request, không retrain cho mỗi lượt thao tác.
- `GET /health`: trạng thái, số sản phẩm, số cặp user-item, trạng thái CF và thời gian cập nhật.
- `GET /ready`: trả 503 nếu model chưa sẵn sàng hoặc quá hạn.
- `POST /api/v1/recommend/user?strategy=hybrid|content_based|collaborative` chọn phương pháp để so sánh.
- Đánh giá offline: `cd aiservice; python evaluate_recommenders.py --k 10` tạo Precision@K, Recall@K,
  NDCG@K cho Popularity baseline, Content-Based, Collaborative và Hybrid bằng global temporal cutoff;
  cặp user-item kiểm tra được loại khỏi train để tránh rò rỉ lịch sử.
  Chỉ có kết quả khi log thật có đủ người dùng tương tác với ít nhất hai sản phẩm; không dùng seed giả để báo cáo chất lượng.

## Chạy bằng Docker

```powershell
docker compose up -d --build
```

- Frontend: http://localhost:3000
- Backend Swagger: http://localhost:8080/swagger-ui/index.html
- AI readiness: http://localhost:8001/ready
- PostgreSQL: localhost:5433
- Elasticsearch: http://localhost:9200

AI đợi backend healthy. Frontend chỉ phụ thuộc backend nên vẫn hoạt động khi AI không khả dụng.

## Chạy development

Cần Java 21, Node.js và Python. Khởi động PostgreSQL và Elasticsearch:

```powershell
docker compose up -d postgres elasticsearch
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

Để bật Collaborative/Hybrid từ log thật, đặt cùng một `AI_INTERNAL_API_KEY` cho backend và AI Service.
Khi chạy Docker Compose, lưu khóa ngẫu nhiên trong `.env`; khi chạy development trực tiếp, export biến môi trường
cho cả hai process. Nếu khóa chưa được cấu hình, AI vẫn phục vụ Content-Based và backend fallback bình thường,
nhưng CF sẽ ở trạng thái disabled.

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
- `AI_INTERNAL_API_KEY`: khóa chia sẻ giữa backend và AI để xuất log tương tác nội bộ; để trống sẽ tắt CF.
- `AI_MAX_TRAINING_INTERACTIONS`: giới hạn snapshot train, mặc định 200.000 cặp user-item.
- `AI_REFRESH_SECONDS`, `AI_RETRY_SECONDS`, `AI_MAX_MODEL_AGE_SECONDS`: chu kỳ refresh, retry và giới hạn tuổi model.
- `ELASTICSEARCH_PORT`, `ELASTICSEARCH_URIS`: cổng host và địa chỉ Elasticsearch; Compose tự dùng hostname nội bộ `elasticsearch:9200`.
- `SEARCH_INDEX_CHECK_INTERVAL_MS`: chu kỳ kiểm tra index và nạp catalog nếu index chưa tồn tại, mặc định 10000 ms.
- `SEARCH_OUTBOX_POLL_INTERVAL_MS`: chu kỳ xử lý sự kiện cập nhật search index, mặc định 2000 ms.

### Đăng nhập Google OAuth2 (tùy chọn)

Backend hỗ trợ Google OIDC và vẫn phát hành JWT của TechStore sau khi callback thành công.
Để bật tính năng, tạo OAuth Client trên Google Cloud Console rồi thêm các biến sau vào `.env`:

```env
OAUTH2_GOOGLE_ENABLED=true
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
OAUTH2_GOOGLE_REDIRECT_URI=http://localhost:8080/login/oauth2/code/google
OAUTH2_FRONTEND_SUCCESS_URL=http://localhost:5173/oauth2/callback
```

Thêm `http://localhost:8080/login/oauth2/code/google` vào Authorized redirect URIs
của Google OAuth Client. Khi chạy Docker Compose, dùng callback
`http://localhost:3000/login/oauth2/code/google` và frontend callback
`http://localhost:3000/oauth2/callback`.

Khi chạy frontend bằng Vite, đặt `VITE_GOOGLE_OAUTH_ENABLED=true` trong
`frontend/.env`. Khi chạy Docker Compose, đặt biến này trong `.env` ở thư mục gốc
để truyền vào frontend image lúc build.

Luồng callback dùng one-time code có thời hạn hai phút, sau đó frontend đổi code lấy
JWT qua `POST /api/v1/auth/oauth2/exchange`. Code hiện được lưu trong memory của backend;
môi trường nhiều instance nên thay bằng Redis hoặc storage dùng chung.

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
