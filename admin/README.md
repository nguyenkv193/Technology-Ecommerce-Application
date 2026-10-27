# TechStore Backoffice Admin Portal

Dự án giao diện quản trị viên (Backoffice Admin Portal) độc lập dành cho nền tảng thương mại điện tử **TechStore**.

---

## 🛠 Công nghệ sử dụng
- **Framework:** Vue 3.5 (Composition API + `<script setup>`)
- **Build tool:** Vite 5.4 + TypeScript 5.9
- **State Management:** Pinia 2.2
- **Router:** Vue Router 4.4 (Strict Role-Based Navigation Guard `ADMIN`)
- **Styling:** TailwindCSS 3.4 (Modern Dark Enterprise Theme)
- **Icons:** Lucide Icons (`lucide-vue-next`)
- **HTTP Client:** Axios 1.7 (JWT Interceptor & Automatic 401 Session Handling)
- **Testing:** Vitest 2.1 + `@vue/test-utils` + `happy-dom`

---

## 🚀 Chạy ứng dụng

### 1. Cài đặt dependencies
```bash
cd admin
npm install
```

### 2. Chạy môi trường Development (Port 5174)
```bash
npm run dev
```
> Truy cập: `http://localhost:5174`  
> *Lưu ý: Mọi request `/api` tự động được proxy sang Spring Boot Backend tại `http://localhost:8080`.*

### 3. Chạy Unit Tests
```bash
npm run test
```

### 4. Build Production
```bash
npm run build
```

---

## 📋 Chức năng chính
1. **Bảo mật & Phân quyền:**
   - Chặn toàn bộ người dùng thường (`USER`), chỉ cho phép tài khoản có `role: 'ADMIN'` đăng nhập vào portal.
   - Quản lý token JWT qua `localStorage` và Axios request interceptor.
2. **Dashboard Tổng quan:**
   - Thống kê doanh thu, tổng đơn hàng, đơn chờ xác nhận, tổng sản phẩm.
   - Bảng đơn hàng mới nhất và các phím tắt thao tác nhanh.
3. **Quản lý Sản phẩm (Product Management):**
   - Danh sách phân trang, tìm kiếm từ khóa, lọc danh mục / thương hiệu.
   - Thêm & cập nhật sản phẩm kèm trình chỉnh sửa thông số kỹ thuật động (RAM, CPU, Ổ cứng...).
   - Xóa sản phẩm có modal xác nhận an toàn.
4. **Quản lý Danh mục & Thương hiệu:**
   - Quản lý cây danh mục ngành hàng.
   - Quản lý danh sách thương hiệu, logo và xuất xứ.
5. **Quản lý Đơn hàng (Order Management):**
   - Lọc đơn hàng theo từng tab trạng thái (`PENDING`, `CONFIRMED`, `SHIPPING`, `DELIVERED`, `CANCELLED`).
   - Modal chi tiết đơn hàng: Xem danh sách sản phẩm, địa chỉ, thanh toán.
   - Cập nhật trạng thái từng bước hoặc hủy đơn có cơ chế tự động hoàn lại tồn kho.
6. **Vận hành Hệ thống (System Operations):**
   - Kích hoạt đồng bộ hóa chỉ mục toàn bộ sản phẩm sang **Elasticsearch** (`/api/v1/admin/search/reindex-products`).
   - Giám sát trạng thái các dịch vụ nền tảng (Spring Boot, Elasticsearch, PostgreSQL, Redis, AI Service).
