<template>
  <footer class="bg-zinc-950 border-t border-zinc-800/90 text-zinc-400 mt-20 transition-colors">
    <!-- 1. Top Bar: Store Locator & Urgent Hotlines (Dark Mode FPT Shop Style) -->
    <div class="border-b border-zinc-800/80 bg-zinc-900/50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
        <div class="flex flex-col lg:flex-row items-center justify-between gap-4 text-xs">
          <!-- Store Locator -->
          <div class="flex items-center gap-3">
            <div class="w-9 h-9 rounded-xl bg-red-500/10 text-red-400 flex items-center justify-center shrink-0 border border-red-500/20">
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z" />
                <circle cx="12" cy="10" r="3" />
              </svg>
            </div>
            <div>
              <div class="font-bold text-white text-xs sm:text-sm flex items-center gap-2">
                Hệ thống hơn 120 cửa hàng TechStore trên toàn quốc
                <span class="inline-flex items-center px-1.5 py-0.5 rounded text-[10px] font-medium bg-red-500/20 text-red-300 border border-red-500/30">Miễn phí giữ hàng</span>
              </div>
              <p class="text-[11px] text-zinc-400">Tìm siêu thị gần nhất để trải nghiệm và mua sắm trực tiếp</p>
            </div>
          </div>

          <!-- Hotlines Quick Access -->
          <div class="flex flex-wrap items-center gap-3 sm:gap-6 text-[11px]">
            <a href="tel:19008888" class="flex items-center gap-2 group hover:text-red-400 transition-colors">
              <span class="w-7 h-7 rounded-lg bg-emerald-500/10 text-emerald-400 flex items-center justify-center shrink-0 border border-emerald-500/20 group-hover:scale-105 transition-transform">
                <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/></svg>
              </span>
              <div>
                <span class="text-zinc-400 block text-[10px]">Tư vấn mua hàng</span>
                <span class="font-bold text-white group-hover:text-red-400">1900 8888 <span class="font-normal text-zinc-500 text-[10px]">(Nhánh 1)</span></span>
              </div>
            </a>

            <div class="h-6 w-px bg-zinc-800 hidden sm:block"></div>

            <a href="tel:19008888" class="flex items-center gap-2 group hover:text-red-400 transition-colors">
              <span class="w-7 h-7 rounded-lg bg-blue-500/10 text-blue-400 flex items-center justify-center shrink-0 border border-blue-500/20 group-hover:scale-105 transition-transform">
                <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/></svg>
              </span>
              <div>
                <span class="text-zinc-400 block text-[10px]">Hỗ trợ kỹ thuật</span>
                <span class="font-bold text-white group-hover:text-red-400">1900 8888 <span class="font-normal text-zinc-500 text-[10px]">(Nhánh 2)</span></span>
              </div>
            </a>

            <div class="h-6 w-px bg-zinc-800 hidden sm:block"></div>

            <a href="tel:18008889" class="flex items-center gap-2 group hover:text-red-400 transition-colors">
              <span class="w-7 h-7 rounded-lg bg-amber-500/10 text-amber-400 flex items-center justify-center shrink-0 border border-amber-500/20 group-hover:scale-105 transition-transform">
                <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
              </span>
              <div>
                <span class="text-zinc-400 block text-[10px]">Góp ý, khiếu nại</span>
                <span class="font-bold text-white group-hover:text-red-400">1800 8889 <span class="font-normal text-zinc-500 text-[10px]">(Miễn phí)</span></span>
              </div>
            </a>
          </div>
        </div>
      </div>
    </div>

    <!-- 2. Main Footer Navigation: 5-Column Grid -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 lg:py-12">
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-8">
        
        <!-- Column 1: Brand & Hotlines -->
        <div class="space-y-4">
          <div class="inline-flex items-center bg-white px-2.5 py-1.5 rounded-xl shadow-xs">
            <img src="/logo.png" alt="TechStore Logo" class="h-8 w-auto object-contain" />
          </div>
          <p class="text-xs leading-relaxed text-zinc-400">
            Hệ sinh thái bán lẻ công nghệ chính hãng hàng đầu: Flagship Smartphone, Laptop & Máy trạm, Máy tính bảng và Thiết bị thông minh.
          </p>

          <div class="pt-1 space-y-2 text-xs">
            <div class="text-[11px] font-semibold uppercase tracking-wider text-zinc-200">Tổng đài hỗ trợ (8:00 - 21:30)</div>
            <div class="space-y-1.5 text-[11px]">
              <div class="flex items-center justify-between py-1 border-b border-zinc-800/80">
                <span class="text-zinc-400">Mua hàng:</span>
                <a href="tel:19008888" class="font-bold text-red-400 hover:underline">1900 8888</a>
              </div>
              <div class="flex items-center justify-between py-1 border-b border-zinc-800/80">
                <span class="text-zinc-400">Kỹ thuật & BH:</span>
                <span class="font-medium text-zinc-200">1900 8888 (Phím 2)</span>
              </div>
              <div class="flex items-center justify-between py-1 border-b border-zinc-800/80">
                <span class="text-zinc-400">Email hỗ trợ:</span>
                <a href="mailto:support@techstore.vn" class="font-medium text-zinc-200 hover:text-red-400 transition">support@techstore.vn</a>
              </div>
            </div>
          </div>

          <!-- Social Media Icons -->
          <div class="pt-2">
            <div class="text-[11px] font-semibold text-zinc-200 mb-2">Kết nối với TechStore</div>
            <div class="flex items-center gap-2">
              <a href="#" aria-label="Facebook" class="w-8 h-8 rounded-lg bg-zinc-900 border border-zinc-800 hover:bg-[#1877F2] hover:border-[#1877F2] text-zinc-300 hover:text-white flex items-center justify-center transition-colors">
                <svg class="w-4 h-4 fill-current" viewBox="0 0 24 24"><path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/></svg>
              </a>
              <a href="#" aria-label="YouTube" class="w-8 h-8 rounded-lg bg-zinc-900 border border-zinc-800 hover:bg-[#FF0000] hover:border-[#FF0000] text-zinc-300 hover:text-white flex items-center justify-center transition-colors">
                <svg class="w-4 h-4 fill-current" viewBox="0 0 24 24"><path d="M23.498 6.186a3.016 3.016 0 0 0-2.122-2.136C19.505 3.545 12 3.545 12 3.545s-7.505 0-9.377.505A3.017 3.017 0 0 0 .502 6.186C0 8.07 0 12 0 12s0 3.93.502 5.814a3.016 3.016 0 0 0 2.122 2.136c1.871.505 9.376.505 9.376.505s7.505 0 9.377-.505a3.015 3.015 0 0 0 2.122-2.136C24 15.93 24 12 24 12s0-3.93-.502-5.814zM9.545 15.568V8.432L15.818 12l-6.273 3.568z"/></svg>
              </a>
              <a href="#" aria-label="TikTok" class="w-8 h-8 rounded-lg bg-zinc-900 border border-zinc-800 hover:bg-zinc-800 text-zinc-300 hover:text-white flex items-center justify-center transition-colors">
                <svg class="w-4 h-4 fill-current" viewBox="0 0 24 24"><path d="M12.525.02c1.31-.02 2.61-.01 3.91-.02.08 1.53.63 3.09 1.75 4.17 1.12 1.11 2.7 1.62 4.24 1.79v4.03c-1.44-.05-2.89-.35-4.2-.97-.57-.26-1.1-.59-1.62-.97v7.55a6.52 6.52 0 0 1-1.33 3.97 6.568 6.568 0 0 1-4.75 2.58 6.536 6.536 0 0 1-5.18-1.92 6.516 6.516 0 0 1-1.89-4.87c.07-3.58 2.97-6.52 6.55-6.57.48-.01.96.04 1.43.14v4.06c-.44-.14-.9-.2-1.36-.18-1.42.06-2.65 1.13-2.81 2.55a2.536 2.536 0 0 0 2.14 2.79c1.38.2 2.74-.75 2.89-2.14.03-.43.03-.86.03-1.29V.02h.15z"/></svg>
              </a>
              <a href="#" aria-label="Zalo" class="w-8 h-8 rounded-lg bg-zinc-900 border border-zinc-800 hover:bg-[#0068FF] hover:border-[#0068FF] text-zinc-300 hover:text-white flex items-center justify-center transition-colors font-bold text-[11px]">
                Zalo
              </a>
            </div>
          </div>
        </div>

        <!-- Column 2: Về TechStore -->
        <div>
          <h4 class="text-xs font-bold uppercase tracking-wider text-zinc-200 mb-3.5">Về TechStore</h4>
          <ul class="space-y-2 text-xs">
            <li><router-link to="/about" class="hover:text-white transition-colors">Giới thiệu về công ty</router-link></li>
            <li><a href="#" class="hover:text-white transition-colors">Hệ thống 120 cửa hàng</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Quy chế hoạt động sàn TMĐT</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Tuyển dụng nhân tài mới</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Tin tức công nghệ & Khuyến mãi</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Dự án Khách hàng Doanh nghiệp (B2B)</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Tra cứu hóa đơn điện tử</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Tra cứu thông tin bảo hành</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Câu hỏi thường gặp (FAQ)</a></li>
          </ul>
        </div>

        <!-- Column 3: Chính sách & Dịch vụ -->
        <div>
          <h4 class="text-xs font-bold uppercase tracking-wider text-zinc-200 mb-3.5">Chính sách & Hỗ trợ</h4>
          <ul class="space-y-2 text-xs">
            <li><a href="#" class="hover:text-white transition-colors">Chính sách bảo hành chính hãng</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Chính sách đổi trả 1-đổi-1 trong 30 ngày</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Chính sách giao hàng hỏa tốc trong 2 giờ</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Hướng dẫn mua hàng & Thanh toán online</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Chính sách trả góp 0% lãi suất</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Chính sách kiểm hàng & Khui hộp</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Chính sách bảo vệ dữ liệu cá nhân</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Hỗ trợ kỹ thuật chuyên sâu</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Quy trình giải quyết phản ánh, khiếu nại</a></li>
          </ul>
        </div>

        <!-- Column 4: Danh mục thiết bị -->
        <div>
          <h4 class="text-xs font-bold uppercase tracking-wider text-zinc-200 mb-3.5">Danh mục thiết bị</h4>
          <ul class="space-y-2 text-xs">
            <li>
              <router-link to="/products?categoryId=2" class="hover:text-white transition-colors flex items-center justify-between group">
                <span>Điện thoại thông minh</span>
                <span class="text-[10px] text-zinc-500 group-hover:text-red-400">Apple, Samsung</span>
              </router-link>
            </li>
            <li>
              <router-link to="/products?categoryId=1" class="hover:text-white transition-colors flex items-center justify-between group">
                <span>Laptop & Máy trạm</span>
                <span class="text-[10px] text-zinc-500 group-hover:text-red-400">MacBook, Dell</span>
              </router-link>
            </li>
            <li>
              <router-link to="/products?categoryId=3" class="hover:text-white transition-colors flex items-center justify-between group">
                <span>Máy tính bảng</span>
                <span class="text-[10px] text-zinc-500 group-hover:text-red-400">iPad, Tab</span>
              </router-link>
            </li>
            <li>
              <router-link to="/products?categoryId=5" class="hover:text-white transition-colors flex items-center justify-between group">
                <span>Linh kiện & Phụ kiện</span>
                <span class="text-[10px] text-zinc-500 group-hover:text-red-400">Sạc, Cáp, Tai nghe</span>
              </router-link>
            </li>
            <li>
              <router-link to="/products?categoryId=4" class="hover:text-white transition-colors flex items-center justify-between group">
                <span>Đồng hồ thông minh</span>
                <span class="text-[10px] text-zinc-500 group-hover:text-red-400">Apple Watch, Garmin</span>
              </router-link>
            </li>
            <li>
              <router-link to="/products?categoryId=6" class="hover:text-white transition-colors flex items-center justify-between group">
                <span>Màn hình & PC Gaming</span>
                <span class="text-[10px] text-zinc-500 group-hover:text-red-400">LG, Asus, Dell</span>
              </router-link>
            </li>
          </ul>
        </div>

        <!-- Column 5: Thanh toán & Chứng nhận (Enterprise Trust Badges) -->
        <div class="space-y-4">
          <!-- Hỗ trợ thanh toán -->
          <div>
            <h4 class="text-xs font-bold uppercase tracking-wider text-zinc-200 mb-2.5">Hỗ trợ thanh toán</h4>
            <div class="grid grid-cols-4 gap-1.5">
              <!-- Visa -->
              <div class="h-7 rounded-lg bg-zinc-900 border border-zinc-800 flex items-center justify-center p-1" title="Visa">
                <span class="font-extrabold italic text-blue-400 text-[11px] tracking-tight">VISA</span>
              </div>
              <!-- Mastercard -->
              <div class="h-7 rounded-lg bg-zinc-900 border border-zinc-800 flex items-center justify-center p-1" title="Mastercard">
                <div class="flex -space-x-1">
                  <div class="w-3.5 h-3.5 rounded-full bg-red-500"></div>
                  <div class="w-3.5 h-3.5 rounded-full bg-amber-400 opacity-90"></div>
                </div>
              </div>
              <!-- JCB -->
              <div class="h-7 rounded-lg bg-zinc-900 border border-zinc-800 flex items-center justify-center p-1" title="JCB">
                <span class="font-bold text-emerald-400 text-[10px]">JCB</span>
              </div>
              <!-- VNPAY -->
              <div class="h-7 rounded-lg bg-zinc-900 border border-zinc-800 flex items-center justify-center p-1" title="VNPAY">
                <span class="font-bold text-blue-400 text-[9px]">VNPAY</span>
              </div>
              <!-- MoMo -->
              <div class="h-7 rounded-lg bg-zinc-900 border border-zinc-800 flex items-center justify-center p-1" title="MoMo">
                <span class="font-extrabold text-pink-400 text-[9px]">MoMo</span>
              </div>
              <!-- Apple Pay -->
              <div class="h-7 rounded-lg bg-zinc-900 border border-zinc-800 flex items-center justify-center p-1" title="Apple Pay">
                <span class="font-semibold text-zinc-200 text-[9px]">Pay</span>
              </div>
              <!-- Trả góp 0% -->
              <div class="col-span-2 h-7 rounded-lg bg-red-500/15 border border-red-500/30 flex items-center justify-center p-1 text-[10px] font-bold text-red-400" title="Trả góp 0%">
                Trả góp 0%
              </div>
            </div>
          </div>

          <!-- Chứng nhận uy tín -->
          <div>
            <h4 class="text-xs font-bold uppercase tracking-wider text-zinc-200 mb-2.5">Chứng nhận uy tín</h4>
            <div class="flex flex-wrap items-center gap-2">
              <!-- Bộ Công Thương Seal Badge -->
              <div class="inline-flex items-center gap-1.5 px-2 py-1 rounded-lg bg-blue-950/60 border border-blue-800/60 text-[10px] font-bold text-blue-300 shadow-2xs">
                <svg class="w-3.5 h-3.5 text-red-500 fill-current" viewBox="0 0 24 24">
                  <path d="M12 2L4 5v6.09c0 5.05 3.41 9.76 8 10.91 4.59-1.15 8-5.86 8-10.91V5l-8-3zm0 4.5c1.38 0 2.5 1.12 2.5 2.5S13.38 11.5 12 11.5 9.5 10.38 9.5 9s1.12-2.5 2.5-2.5zM7 16c0-2.21 2.24-4 5-4s5 1.79 5 4H7z"/>
                </svg>
                <span>ĐÃ THÔNG BÁO BCT</span>
              </div>
              <!-- DMCA Badge -->
              <div class="inline-flex items-center gap-1 px-2 py-1 rounded-lg bg-zinc-900 border border-zinc-800 text-[10px] font-bold text-zinc-300">
                <svg class="w-3 h-3 text-emerald-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><rect width="18" height="11" x="3" y="11" rx="2" ry="2"/><path d="m7 11V7a5 5 0 0 1 10 0v4"/></svg>
                <span>DMCA PROTECTED</span>
              </div>
            </div>
          </div>

          <!-- Tải ứng dụng TechStore -->
          <div class="pt-1">
            <h4 class="text-xs font-bold uppercase tracking-wider text-zinc-200 mb-2">Tải ứng dụng TechStore</h4>
            <div class="flex items-center gap-2.5">
              <!-- Mini QR mockup -->
              <div class="w-14 h-14 rounded-lg bg-white p-1 flex items-center justify-center shrink-0 shadow-2xs">
                <svg class="w-full h-full text-zinc-900" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect width="6" height="6" x="3" y="3" rx="1"/>
                  <rect width="6" height="6" x="15" y="3" rx="1"/>
                  <rect width="6" height="6" x="3" y="15" rx="1"/>
                  <path d="M15 15h.01M19 15h.01M15 19h.01M19 19h.01"/>
                </svg>
              </div>
              <!-- Store Badges -->
              <div class="space-y-1.5 flex-1">
                <a href="#" class="flex items-center gap-1.5 px-2 py-1 rounded-lg bg-zinc-900 border border-zinc-800 text-zinc-200 hover:bg-zinc-800 hover:text-white transition text-[10px] font-medium">
                  <svg class="w-3 h-3 fill-current" viewBox="0 0 24 24"><path d="M18.71 19.5c-.83 1.24-1.71 2.45-3.05 2.47-1.34.03-1.77-.79-3.29-.79-1.53 0-2 .77-3.27.82-1.31.05-2.3-1.32-3.14-2.53C4.25 17 2.94 12.45 4.7 9.39c.87-1.52 2.43-2.48 4.12-2.51 1.28-.02 2.5.87 3.29.87.78 0 2.26-1.07 3.81-.91.65.03 2.47.26 3.64 1.98-.09.06-2.17 1.28-2.15 3.81.03 3.02 2.65 4.03 2.68 4.04-.03.07-.42 1.44-1.38 2.83M15.97 6.37c.61-.75 1.04-1.8 0.92-2.85-.92.04-2.07.62-2.73 1.39-.58.67-1.09 1.74-.96 2.77 1.03.08 2.11-.53 2.77-1.31z"/></svg>
                  <span>App Store</span>
                </a>
                <a href="#" class="flex items-center gap-1.5 px-2 py-1 rounded-lg bg-zinc-900 border border-zinc-800 text-zinc-200 hover:bg-zinc-800 hover:text-white transition text-[10px] font-medium">
                  <svg class="w-3 h-3 fill-current" viewBox="0 0 24 24"><path d="M3.609 1.814L13.793 12 3.61 22.186c-.347-.365-.56-.879-.56-1.464V3.278c0-.585.213-1.099.559-1.464zM15.207 13.414l2.707 2.707-11.83 6.76c-.287.164-.606.26-.941.26a1.98 1.98 0 0 1-.777-.156l10.841-9.571zm0-2.828L4.366 1.015a1.98 1.98 0 0 1 .777-.156c.335 0 .654.096.941.26l11.83 6.76-2.707 2.707zm1.414 1.414l3.828 2.187c.801.458.801 1.205 0 1.663l-3.828 2.187-2.121-2.121 2.121-2.121z"/></svg>
                  <span>Google Play</span>
                </a>
              </div>
            </div>
          </div>
        </div>

      </div>

      <!-- 3. TechStore Ecosystem Strip (Dark Tone) -->
      <div class="mt-10 pt-6 border-t border-zinc-800/80">
        <div class="flex flex-col md:flex-row items-start md:items-center justify-between gap-4 text-xs">
          <div class="text-[11px] font-semibold text-zinc-200 shrink-0">
            HỆ SINH THÁI TECHSTORE GROUP:
          </div>
          <div class="flex flex-wrap items-center gap-4 sm:gap-6 text-zinc-400 text-[11px]">
            <a href="#" class="hover:text-red-400 transition flex items-center gap-1.5 font-medium text-zinc-300">
              <span class="w-1.5 h-1.5 rounded-full bg-red-500"></span>
              TechStore Flagship: <span class="text-zinc-400 font-normal">Trải nghiệm công nghệ cao cấp</span>
            </a>
            <a href="#" class="hover:text-blue-400 transition flex items-center gap-1.5 font-medium text-zinc-300">
              <span class="w-1.5 h-1.5 rounded-full bg-blue-500"></span>
              TechCare: <span class="text-zinc-400 font-normal">Trung tâm sửa chữa & bảo hành ủy quyền</span>
            </a>
            <a href="#" class="hover:text-emerald-400 transition flex items-center gap-1.5 font-medium text-zinc-300">
              <span class="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
              TechBiz: <span class="text-zinc-400 font-normal">Giải pháp chuyển đổi số cho Doanh nghiệp</span>
            </a>
          </div>
        </div>
      </div>

      <!-- 4. Bottom Corporate & Legal Information (Dark Minimalist) -->
      <div class="mt-8 pt-6 border-t border-zinc-800/80 space-y-3 text-[11px] text-zinc-400 leading-relaxed">
        <div class="flex flex-col md:flex-row items-start md:items-center justify-between gap-3">
          <div class="font-medium text-zinc-300">
            © 2026 TechStore System - Graduation Thesis. All rights reserved.
          </div>
          <div class="flex flex-wrap items-center gap-4 text-[11px] text-zinc-400">
            <a href="#" class="hover:text-white transition">Chính sách bảo mật</a>
            <span>•</span>
            <a href="#" class="hover:text-white transition">Quy chế hoạt động</a>
            <span>•</span>
            <a href="#" class="hover:text-white transition">Điều khoản dịch vụ</a>
            <span>•</span>
            <a href="#" class="hover:text-white transition">Sơ đồ trang web</a>
            <span>•</span>
            <a href="#" class="hover:text-white transition">API Docs</a>
          </div>
        </div>

        <div class="text-[10px] text-zinc-500 space-y-1">
          <p>
            Công Ty Cổ Phần Công Nghệ & Thương Mại Điện Tử TechStore • Địa chỉ: Tòa nhà TechStore Innovation Tower, Lô E2a-7, Đường D1, Khu Công Nghệ Cao, TP. Thủ Đức, TP. Hồ Chí Minh.
          </p>
          <p>
            GPĐKKD số 0311609355 do Sở Kế hoạch và Đầu tư TP. Hồ Chí Minh cấp • Giấy phép thiết lập mạng thông tin điện tử số 47/GP-TTĐT do Sở Thông tin & Truyền thông cấp.
          </p>
          <p>
            Điện thoại liên hệ: (028) 7300 8888 • Email: support@techstore.vn • Chịu trách nhiệm nội dung & đề tài: Nhóm Nghiên Cứu Hệ Thống Thương Mại Điện Tử.
          </p>
        </div>
      </div>
    </div>
  </footer>
</template>
