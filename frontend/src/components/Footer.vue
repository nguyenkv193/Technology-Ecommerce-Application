<template>
  <footer class="bg-white border-t border-zinc-200/80 text-zinc-500 py-12 mt-20 transition-colors">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
        <div class="space-y-3">
          <div class="flex items-center gap-2">
            <img src="/logo.png" alt="TechStore Logo" class="h-12 w-auto object-contain" />
          </div>
          <p class="text-xs leading-relaxed text-zinc-500">
            Hệ sinh thái thương mại điện tử chuyên cung cấp thiết bị công nghệ chính hãng: Flagship smartphone, máy trạm sáng tạo, laptop và phụ kiện cao cấp.
          </p>
        </div>

        <div>
          <h4 class="text-xs font-semibold uppercase tracking-wider text-zinc-900 mb-3">Danh mục thiết bị</h4>
          <ul class="space-y-2 text-xs">
            <li><router-link to="/products?categoryId=2" class="hover:text-zinc-900 transition">Điện thoại thông minh</router-link></li>
            <li><router-link to="/products?categoryId=1" class="hover:text-zinc-900 transition">Laptop & Máy trạm</router-link></li>
            <li><router-link to="/products?categoryId=3" class="hover:text-zinc-900 transition">Máy tính bảng</router-link></li>
            <li><router-link to="/products?categoryId=5" class="hover:text-zinc-900 transition">Linh kiện & Phụ kiện</router-link></li>
          </ul>
        </div>

        <div>
          <h4 class="text-xs font-semibold uppercase tracking-wider text-zinc-900 mb-3">Chính sách & Dịch vụ</h4>
          <ul class="space-y-2 text-xs">
            <li class="flex items-center gap-2">
              <span class="w-1.5 h-1.5 rounded-full bg-zinc-400"></span>
              <span>Giao hàng hỏa tốc trong 2 giờ</span>
            </li>
            <li class="flex items-center gap-2">
              <span class="w-1.5 h-1.5 rounded-full bg-zinc-400"></span>
              <span>Bảo hành chính hãng 12 tháng</span>
            </li>
            <li class="flex items-center gap-2">
              <span class="w-1.5 h-1.5 rounded-full bg-zinc-400"></span>
              <span>Đổi trả 1-đổi-1 trong 30 ngày</span>
            </li>
            <li class="flex items-center gap-2">
              <span class="w-1.5 h-1.5 rounded-full bg-zinc-900"></span>
              <span class="font-medium text-zinc-800">Hỗ trợ kỹ thuật chuyên sâu</span>
            </li>
          </ul>
        </div>

        <div>
          <h4 class="text-xs font-semibold uppercase tracking-wider text-zinc-900 mb-3">Thông tin liên hệ</h4>
          <div class="p-3.5 bg-zinc-50 rounded-xl border border-zinc-200/80 text-[11px] space-y-2">
            <div class="flex justify-between">
              <span class="text-zinc-500">Hotline tư vấn:</span>
              <span class="text-zinc-800 font-medium">1900 8888</span>
            </div>
            <div class="flex justify-between">
              <span class="text-zinc-500">Email hỗ trợ:</span>
              <span class="text-zinc-800 font-medium">support@techstore.vn</span>
            </div>
            <div class="flex justify-between">
              <span class="text-zinc-500">Thời gian làm việc:</span>
              <span class="text-zinc-800 font-medium">8:00 - 21:30</span>
            </div>
          </div>
        </div>
      </div>

      <div class="mt-12 pt-6 border-t border-zinc-100 flex flex-col sm:flex-row items-center justify-between text-xs text-zinc-400 gap-2">
        <div>© 2026 TechStore System - Graduation Thesis. All rights reserved.</div>
        <div class="flex items-center gap-4 text-[11px]">
          <span class="hover:text-zinc-600 transition cursor-pointer">Bảo mật</span>
          <span class="hover:text-zinc-600 transition cursor-pointer">Điều khoản</span>
          <span class="hover:text-zinc-600 transition cursor-pointer">API Docs</span>
        </div>
      </div>
    </div>
  </footer>
</template>
