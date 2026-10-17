# TÀI LIỆU MA TRẬN TEST CASE: TÍNH NĂNG SEARCH SUGGESTION DROPDOWN

> **Dự án**: TechStore E-Commerce & AI Recommendation  
> **Chức năng**: Dropdown gợi ý tìm kiếm thông minh (Search Suggestion Dropdown)  
> **Component liên quan**: `SearchBox.vue`, `Navbar.vue`, `catalogApi.ts`  
> **Framework kiểm thử tự động**: Vitest + Vue Test Utils + Happy-DOM  

---

## 1. TỔNG QUAN PHẠM VI KIỂM THỬ (TEST SCOPE)

1. **Giao diện & Trạng thái mặc định**:
   - Thanh input tìm kiếm bo tròn (`rounded-full`), nút kính lúp đỏ, placeholder.
   - Dropdown hiển thị khi focus/click vào ô tìm kiếm.
   - Banner khuyến mãi "TÍCH ĐIỂM ĐỔI ƯU ĐÃI" & Modal TechRewards.
2. **Lịch sử tìm kiếm (Search History)**:
   - Hiển thị danh sách từ khóa đã tìm kiếm từ `localStorage`.
   - Nút xóa từng mục (`X`), nút "Xem thêm" / "Thu gọn".
   - Cơ chế lưu trữ không trùng lặp (deduplication) và giới hạn tối đa 10 mục.
3. **Xu hướng tìm kiếm (Trending Searches)**:
   - Danh sách tag từ khóa thịnh hành (iPhone 18, iPhone Duo, Laptop, Macbook...).
   - Điều hướng và cập nhật lịch sử khi chọn tag.
4. **Gợi ý cho bạn (Recommendations)**:
   - Lưới 2 cột x 2 dòng các bài viết/sản phẩm công nghệ nổi bật.
   - Nút "Gợi ý khác ↻" xoay vòng các bộ gợi ý tiếp theo.
5. **Chế độ Autocomplete khi gõ từ khóa (`>= 1 ký tự`)**:
   - Lọc từ khóa khớp trong lịch sử.
   - Gọi API `catalogApi.getSearchSuggestions` kèm cơ chế debounce 220ms.
   - Làm nổi bật (highlight `<mark>`) từ khóa trong tên sản phẩm.
   - Trạng thái loading, trạng thái không có kết quả (empty state).
6. **Tương tác bàn phím & Trợ năng (Accessibility)**:
   - Phím `ArrowDown` / `ArrowUp` di chuyển highlight qua các gợi ý.
   - Phím `Enter` chọn mục hoặc tìm kiếm toàn bộ danh mục.
   - Phím `Escape` đóng dropdown.
   - Click bên ngoài (Click Outside) đóng dropdown.
7. **Responsive & Mobile**:
   - Nút kính lúp trên mobile mở drawer modal tìm kiếm riêng biệt.

---

## 2. MA TRẬN TEST CASE CHI TIẾT (TEST CASES MATRIX)

| Mã TC | Phân loại | Tên Test Case | Tiền điều kiện | Các bước thực hiện (Steps) | Dữ liệu đầu vào | Kết quả mong đợi (Expected Result) | Mức độ | Trạng thái |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-SEARCH-01** | UI | Hiển thị ban đầu của thanh tìm kiếm | Vào trang chủ | Quan sát Navbar ở desktop | N/A | Input dạng viên thuốc, placeholder chuẩn, nút kính lúp màu đỏ, dropdown đóng | High | **PASS** |
| **TC-SEARCH-02** | Functional | Mở Dropdown khi focus vào input | Dropdown đang đóng | Click hoặc focus vào input tìm kiếm | N/A | Dropdown mở với hiệu ứng chuyển cảnh mượt mà, lớp phủ backdrop mờ xuất hiện | High | **PASS** |
| **TC-SEARCH-03** | Functional | Hiển thị Banner tích điểm | Dropdown mở | Quan sát phần đầu tiên của dropdown | N/A | Banner nền tối, chữ vàng/đỏ "TÍCH ĐIỂM ĐỔI ƯU ĐÃI", nút "Tìm hiểu ngay" | Medium | **PASS** |
| **TC-SEARCH-04** | Functional | Mở Modal chi tiết TechRewards | Dropdown mở | Click vào Banner hoặc nút "Tìm hiểu ngay" | N/A | Modal popup hiển thị các quyền lợi: Tích lũy 1-3%, đổi voucher 500k, hội viên VIP | Medium | **PASS** |
| **TC-SEARCH-05** | Functional | Khởi tạo lịch sử tìm kiếm mặc định | `localStorage` rỗng | Focus vào input tìm kiếm lần đầu | N/A | Khởi tạo tự động các từ khóa mẫu (`iphone 18`, `samsung`...) vào `localStorage` | High | **PASS** |
| **TC-SEARCH-06** | Functional | Mở rộng danh sách lịch sử | History có > 2 mục | Click vào liên kết "Xem thêm" | N/A | Danh sách mở rộng hiển thị toàn bộ lịch sử, nút đổi thành "Thu gọn" | High | **PASS** |
| **TC-SEARCH-07** | Functional | Thu gọn danh sách lịch sử | History đang mở rộng | Click vào liên kết "Thu gọn" | N/A | Danh sách thu gọn lại còn 2 mục, nút đổi thành "Xem thêm" | High | **PASS** |
| **TC-SEARCH-08** | Functional | Xóa một mục khỏi lịch sử | History có từ khóa | Click nút `X` bên phải mục "iphone 18" | Click `X` | Mục "iphone 18" bị xóa khỏi danh sách và cập nhật trong `localStorage`, không kích hoạt tìm kiếm | High | **PASS** |
| **TC-SEARCH-09** | Functional | Chọn từ khóa từ lịch sử | History có từ khóa | Click vào dòng chữ "iphone 18" | Click dòng | Điều hướng sang `/products?keyword=iphone 18`, đóng dropdown, đưa mục lên đầu lịch sử | High | **PASS** |
| **TC-SEARCH-10** | Functional | Hiển thị và chọn Xu hướng tìm kiếm | Dropdown mở | Click vào chip "Laptop" trong Xu hướng | Click chip | Điều hướng sang `/products?keyword=Laptop`, lưu "Laptop" vào lịch sử, đóng dropdown | High | **PASS** |
| **TC-SEARCH-11** | Functional | Hiển thị lưới Gợi ý cho bạn | Dropdown mở | Quan sát phần Gợi ý cho bạn | N/A | Lưới 2 cột x 2 dòng gồm 4 thẻ có thumbnail bo góc, tiêu đề 2 dòng có dấu ba chấm `...` | High | **PASS** |
| **TC-SEARCH-12** | Functional | Xoay đổi gợi ý ("Gợi ý khác") | Dropdown mở | Click vào nút "Gợi ý khác ↻" | Click nút | Icon xoay 180 độ, 4 thẻ gợi ý được thay thế bằng bộ gợi ý tiếp theo | Medium | **PASS** |
| **TC-SEARCH-13** | Functional | Click vào thẻ gợi ý | Dropdown mở | Click vào bài viết gợi ý | Click thẻ | Điều hướng sang trang tìm kiếm sản phẩm theo từ khóa của bài viết | Medium | **PASS** |
| **TC-SEARCH-14** | Functional | Chuyển chế độ Autocomplete khi gõ | Input đang trống | Gõ từ khóa vào ô tìm kiếm | "iphone" | Chuyển sang giao diện kết quả live, hiển thị lịch sử trùng khớp và danh sách sản phẩm | High | **PASS** |
| **TC-SEARCH-15** | Functional | Debounce gọi API gợi ý | Input đang gõ | Gõ liên tục các ký tự | "macb" -> "macbook" | Chỉ gọi API 1 lần sau khi dừng gõ 220ms, hủy bỏ các request dở dang trước đó | High | **PASS** |
| **TC-SEARCH-16** | Functional | Highlight từ khóa khớp trong tên | API trả về sản phẩm | Gõ từ khóa | "pro" | Các chữ "pro" trong tên sản phẩm được bọc thẻ `<mark>` màu nền vàng nhạt, in đậm | Medium | **PASS** |
| **TC-SEARCH-17** | Functional | Định dạng giá tiền VND gợi ý | API trả về sản phẩm | Quan sát cột giá sản phẩm | Giá số | Giá hiển thị đúng định dạng tiền tệ Việt Nam (VD: `34.990.000 ₫`) | Medium | **PASS** |
| **TC-SEARCH-18** | Functional | Click chọn sản phẩm gợi ý | Danh sách sản phẩm hiện | Click vào sản phẩm có `id = 101` | Click | Điều hướng sang `/products/101`, lưu tên sản phẩm vào lịch sử, đóng dropdown | High | **PASS** |
| **TC-SEARCH-19** | Functional | Nút Xem tất cả kết quả | Chế độ gợi ý đang mở | Click "Xem tất cả kết quả cho '...'" | Click | Điều hướng sang `/products?keyword=...`, đóng dropdown | High | **PASS** |
| **TC-SEARCH-20** | Functional | Xóa nhanh từ khóa (Nút X input) | Input có ký tự | Click nút `X` trong thanh input | Click `X` | Input được xóa rỗng, dropdown tự động quay lại trạng thái mặc định (Banner + History) | Medium | **PASS** |
| **TC-SEARCH-21** | UI/UX | Điều hướng bằng phím mũi tên | Danh sách gợi ý hiện | Nhấn phím `ArrowDown` và `ArrowUp` | Phím mũi tên | Item được highlight với nền `bg-zinc-100`, xoay vòng danh sách | High | **PASS** |
| **TC-SEARCH-22** | UI/UX | Chọn bằng phím Enter | Đang highlight item | Nhấn phím `Enter` | Phím `Enter` | Chọn sản phẩm đang được highlight và điều hướng ngay lập tức | High | **PASS** |
| **TC-SEARCH-23** | UI/UX | Đóng Dropdown bằng phím Escape | Dropdown đang mở | Nhấn phím `Escape` | Phím `Escape` | Dropdown đóng lại ngay lập tức | High | **PASS** |
| **TC-SEARCH-24** | UI/UX | Đóng Dropdown khi Click Outside | Dropdown đang mở | Click chuột vào bất kỳ vị trí ngoài dropdown | Click ngoài | Dropdown đóng lại | High | **PASS** |
| **TC-SEARCH-25** | Edge Case | Ký tự đặc biệt & XSS Injection | Dropdown mở | Nhập các ký tự đặc biệt, mã HTML/JS | `<script>alert(1)</script>` | Ký tự được escape an toàn, không sinh lỗi render hay XSS script | Critical | **PASS** |
| **TC-SEARCH-26** | Edge Case | Backend API trả về lỗi hoặc timeout | Đang nhập từ khóa | Mô phỏng API trả về lỗi 500 hoặc mạng ngắt | "laptop" | Giao diện hiển thị thông báo "Không tìm thấy sản phẩm phù hợp" kèm tùy chọn tìm kiếm thủ công | Medium | **PASS** |
| **TC-SEARCH-27** | Edge Case | Fallback khi ảnh Thumbnail lỗi | Dropdown mở | URL ảnh bị hỏng 404 | Ảnh lỗi | Tự động kích hoạt sự kiện `onerror` và thay bằng ảnh placeholder thiết bị mặc định | Low | **PASS** |
| **TC-SEARCH-28** | Edge Case | Giới hạn tối đa 10 mục lịch sử | Đã có 10 mục | Tìm kiếm từ khóa thứ 11 | Từ khóa mới | Mục mới nhất đứng đầu, mục cũ nhất thứ 11 bị loại bỏ, tổng số luôn <= 10 | Medium | **PASS** |
| **TC-SEARCH-29** | Edge Case | Loại bỏ trùng lặp trong lịch sử | Đã có từ khóa trong history | Tìm kiếm lại từ khóa đã có | Từ khóa cũ | Đưa từ khóa đó lên vị trí đầu tiên, không tạo dòng trùng lặp | Medium | **PASS** |
| **TC-SEARCH-30** | Responsive | Tìm kiếm trên thiết bị Mobile | Màn hình < 768px | Nhấp icon kính lúp trên mobile Navbar | Click icon | Drawer modal tìm kiếm mở toàn màn hình với đầy đủ chức năng và nút "Đóng" | High | **PASS** |

---

## 3. BÁO CÁO THỰC THI KIỂM THỬ TỰ ĐỘNG (AUTOMATED TEST RESULTS)

- **Công cụ chạy**: `npm test` (`vitest run`)
- **Tổng số Test Suites**: 2 (`SearchBox.spec.ts`, `Navbar.spec.ts`)
- **Tổng số Test Cases tự động**: 17 tests
- **Kết quả**: **17/17 PASSED (100%)**
- **Thời gian thực thi**: ~3.25 giây
