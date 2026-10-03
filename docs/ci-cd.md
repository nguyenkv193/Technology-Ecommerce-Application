# CI và Docker images

Pipeline dùng GitHub Actions, chưa deploy lên server hay thay đổi database.

## Luồng chạy

- Push lên bất kỳ nhánh nào, mở/cập nhật PR hoặc chọn **Actions → CI → Run workflow** sẽ chạy kiểm tra.
- Frontend: Node 20, `npm ci`, TypeScript typecheck và Vite production build. Chưa có bộ frontend unit test trong project.
- Backend: Temurin Java 21, Gradle 8.10, toàn bộ JUnit tests và `bootJar`. Các test hiện tại không cần PostgreSQL.
- AI: Python 3.12, cài dependencies, `pip check` và toàn bộ pytest tests. Backend URL trỏ tới một cổng không chạy để bộ test dùng seed data, không phụ thuộc dữ liệu môi trường ngoài.
- Kiểm tra Compose với `.env.example`; build ba Docker image trên Linux/amd64.
- Lint cú pháp workflow, biểu thức GitHub Actions và shell scripts bằng actionlint/ShellCheck; image linter được pin digest và chỉ đọc repository.
- Job **CI gate** chỉ xanh khi tất cả kiểm tra trên thành công, kể cả Docker build.
- Chỉ push/manual run trên **nhánh mặc định của repository** và CI xanh mới publish lên GHCR. PR và các nhánh khác chỉ build, không publish.

Workflow không phụ thuộc tên `main`/`master`; đọc nhánh mặc định từ GitHub. CI cài Gradle trực tiếp vì `backend/gradle/wrapper/gradle-wrapper.jar` hiện không được Git theo dõi.

## Images được xuất bản

Với repository hiện tại:

```text
ghcr.io/nguyenkv193/technology-ecommerce-application-backend:sha-<full-commit-sha>
ghcr.io/nguyenkv193/technology-ecommerce-application-frontend:sha-<full-commit-sha>
ghcr.io/nguyenkv193/technology-ecommerce-application-ai-service:sha-<full-commit-sha>
```

Mỗi image cũng có tag `latest`. Dùng tag SHA hoặc digest khi deploy/rollback; không dùng `latest` để xác định một bản release cụ thể. Tên image được lấy từ repository đang chạy workflow và chuyển thành chữ thường, không hard-code owner trong pipeline.

Pipeline dùng `GITHUB_TOKEN` tự cấp với `packages: write` cho publish; không cần lưu PAT hoặc Docker Hub password vào repository. Cách publish và quyền này dựa trên [hướng dẫn GHCR của GitHub](https://docs.github.com/en/actions/tutorials/publish-packages/publish-docker-images).

Ba image publish độc lập. Nếu một image lỗi, workflow sẽ đỏ dù image khác đã được publish; chỉ sử dụng SHA của **run hoàn tất thành công** làm release. Pipeline không tự deploy hoặc tự rollback database.

## Bật trên GitHub

1. Push các file workflow lên nhánh này, mở PR để chạy CI; merge vào nhánh mặc định để publish images.
2. Trong **Settings → Actions → General**, cho phép GitHub Actions và các actions chính thức dùng trong workflow. Workflow khai báo quyền token theo từng job. Nếu policy tổ chức chặn GHCR/package write, cần quản trị viên cho phép.
3. Nếu tên package đã tồn tại, đảm bảo package liên kết với repository và repository có quyền ghi trong **Package settings → Manage Actions access**. Xem [quyền truy cập Container registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry).
4. Có thể đặt branch protection/ruleset yêu cầu status check **CI gate** sau lần chạy đầu tiên. Pipeline không tự thay đổi cấu hình bảo vệ nhánh.
5. Tải `frontend-dist`, `backend-jar`, `backend-test-reports`, `ai-test-reports` trong trang run; artifacts giữ 7 ngày. Các step test/build lỗi sẽ chặn publish, không được bỏ qua.

Packages mới có thể là private. Muốn pull từ máy ngoài GitHub Actions, dùng tài khoản/token chỉ có quyền đọc package (đăng nhập qua `docker login ghcr.io --password-stdin`), hoặc chủ động cấu hình visibility của package. Không đưa token vào source code hay `.env.example`.

## Bảo trì và giới hạn

- Actions được pin bằng commit SHA; Dependabot kiểm tra cập nhật actions hằng tuần.
- Dùng cache npm/pip/Gradle/BuildKit để giảm thời gian; không cancel run trên nhánh mặc định giữa lúc publish.
- Frontend dùng lockfile. Python requirements và Docker base tags hiện chưa khóa tuyệt đối; một commit có thể resolve dependency mới khi rebuild. Digest trong run summary xác định chính xác image đã build.
- Kiểm tra Compose hiện là validation cấu hình, không phải end-to-end test với PostgreSQL/Flyway. Không chạy test lên database thật.
- Chưa thiết lập server, domain, TLS, môi trường production hoặc deploy credentials. Bổ sung CD deploy sau khi xác định hạ tầng đích.

## Kiểm tra local

```powershell
cd frontend
npm ci --no-audit --no-fund
npm run build

cd ../backend
# Cần Java 21 và Gradle 8.10; dùng wrapper khi wrapper JAR có sẵn local.
./gradlew.bat clean test bootJar --no-daemon

cd ../aiservice
$env:BACKEND_API_URL = 'http://127.0.0.1:9/api/v1'
python -m pytest tests -q

cd ..
docker compose --env-file .env.example config --quiet
docker compose build
```
