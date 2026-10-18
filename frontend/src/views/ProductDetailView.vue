<template>
  <!-- Loading Skeleton -->
  <div v-if="loading" class="py-24 text-center space-y-4">
    <div class="inline-block w-10 h-10 border-3 border-red-600 border-t-transparent rounded-full animate-spin"></div>
    <div class="text-xs text-zinc-500 font-medium">Đang tải thông tin sản phẩm...</div>
  </div>

  <!-- Product Not Found -->
  <div v-else-if="!product" class="py-24 text-center space-y-3">
    <div class="w-16 h-16 mx-auto rounded-full bg-zinc-100 flex items-center justify-center text-zinc-400">
      <svg class="w-8 h-8" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="m15 9-6 6M9 9l6 6"/></svg>
    </div>
    <h3 class="text-base font-bold text-zinc-800">Không tìm thấy thông tin sản phẩm</h3>
    <p class="text-xs text-zinc-500">Sản phẩm bạn đang tìm kiếm có thể đã bị xóa hoặc tạm ngưng kinh doanh.</p>
    <router-link to="/products" class="inline-block mt-2 px-5 py-2.5 rounded-xl text-xs font-semibold bg-red-600 hover:bg-red-700 text-white transition">
      Xem tất cả sản phẩm
    </router-link>
  </div>

  <!-- Product Detail Page (FPT Shop Style) -->
  <div v-else class="space-y-6 sm:space-y-8 pb-20">
    <!-- Breadcrumb & Product Meta Header -->
    <div class="space-y-3 pb-2 border-b border-zinc-200/80">
      <nav class="flex items-center flex-wrap gap-1.5 text-xs text-zinc-500">
        <router-link to="/" class="hover:text-red-600 transition flex items-center gap-1">
          <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
          <span>Trang chủ</span>
        </router-link>
        <span class="text-zinc-300">/</span>
        <router-link to="/products" class="hover:text-red-600 transition">
          {{ product.category?.name || 'Điện thoại' }}
        </router-link>
        <span class="text-zinc-300">/</span>
        <span v-if="product.brand?.name" class="hover:text-red-600 cursor-pointer transition">
          {{ product.brand.name }}
        </span >
        <span v-if="product.brand?.name" class="text-zinc-300">/</span>
        <span class="text-zinc-800 font-medium truncate max-w-[280px] sm:max-w-md">{{ product.name }}</span>
      </nav>

      <div class="flex flex-col md:flex-row md:items-center justify-between gap-3">
        <div>
          <h1 class="text-xl sm:text-2xl lg:text-3xl font-extrabold text-zinc-900 leading-tight">
            {{ product.name }}
          </h1>

          <div class="flex items-center flex-wrap gap-x-4 gap-y-2 mt-2 text-xs text-zinc-500">
            <span class="font-mono text-zinc-600 font-medium bg-zinc-100 px-2 py-0.5 rounded-md border border-zinc-200">
              Mã SP: No.00{{ product.id }}
            </span>

            <a href="#reviews" class="flex items-center gap-1 hover:text-red-600 transition">
              <span class="flex text-amber-400">
                <svg v-for="s in 5" :key="s" class="w-3.5 h-3.5 fill-current" viewBox="0 0 24 24"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
              </span>
              <span class="font-bold text-zinc-900 ml-1">{{ reviewSummary.averageRating || '4.9' }}</span>
              <span class="text-zinc-400 underline decoration-zinc-300">({{ reviewSummary.totalReviews || 199 }} đánh giá)</span>
            </a>

            <button 
              @click="scrollToSection('specs')" 
              class="flex items-center gap-1 hover:text-red-600 transition cursor-pointer text-zinc-600"
            >
              <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
              <span>Thông số</span>
            </button>

            <button 
              @click="showCompareModal = true"
              class="flex items-center gap-1 hover:text-red-600 transition cursor-pointer text-zinc-600"
            >
              <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="18" cy="18" r="3"/><circle cx="6" cy="6" r="3"/><path d="M13 6h3a2 2 0 0 1 2 2v7"/><path d="M11 18H8a2 2 0 0 1-2-2V9"/></svg>
              <span>So sánh</span>
            </button>
          </div>
        </div>

        <div class="hidden md:flex items-center gap-3">
          <div class="flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-emerald-50 text-emerald-700 text-xs font-semibold border border-emerald-200">
            <span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
            <span>Chính hãng 100%</span>
          </div>
          <div class="flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-amber-50 text-amber-700 text-xs font-semibold border border-amber-200">
            <span>⚡ Giao siêu tốc 2H</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Sticky Sub-Nav Anchor Bar (Chỉ hiển thị khi cuộn trang) -->
    <Teleport to="body">
      <Transition name="fade-slide">
        <div 
          v-if="showStickySubNav"
          class="fixed top-16 left-0 right-0 z-40 bg-white/95 backdrop-blur-md border-b border-zinc-200/90 shadow-sm py-2 px-4 transition-all"
        >
          <div class="max-w-7xl mx-auto flex items-center justify-between overflow-x-auto scrollbar-none px-2 sm:px-4">
            <div class="flex items-center gap-1 sm:gap-2">
              <button 
                v-for="nav in navTabs" 
                :key="nav.id"
                @click="scrollToSection(nav.id)"
                class="px-3 py-1.5 rounded-xl text-xs font-medium whitespace-nowrap transition cursor-pointer"
                :class="activeSection === nav.id ? 'bg-red-600 text-white font-semibold shadow-xs' : 'text-zinc-600 hover:text-zinc-900 hover:bg-zinc-100'"
              >
                <span class="mr-1">{{ nav.icon }}</span>
                <span>{{ nav.label }}</span>
              </button>
            </div>

            <div class="hidden lg:flex items-center gap-3 shrink-0 pl-3 border-l border-zinc-200 text-xs">
              <span class="text-zinc-400">Hotline:</span>
              <a href="tel:18006601" class="font-bold text-red-600 hover:underline">1800 6601 (Miễn phí)</a>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

    <!-- Main Detail Section (Left: Gallery & Details, Right: Buy Box & Offers) -->
    <div id="overview" class="grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-10 items-start">
      
      <!-- ================= LEFT COLUMN (MEDIA & STORY) ================= -->
      <div class="lg:col-span-7 space-y-6">
        <!-- Main Media Gallery Box -->
        <div class="bg-white border border-zinc-200/90 rounded-3xl p-6 sm:p-8 aspect-4/3 sm:aspect-square flex items-center justify-center relative overflow-hidden shadow-xs group">
          <!-- Top Left Tag Badges -->
          <div class="absolute top-4 left-4 z-10 flex flex-col gap-2">
            <span class="inline-flex items-center gap-1 text-[11px] font-bold uppercase tracking-wider px-2.5 py-1 rounded-lg bg-red-600 text-white shadow-xs">
              <svg class="w-3 h-3" viewBox="0 0 24 24" fill="currentColor"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/></svg>
              <span>Trả góp 0%</span>
            </span>
            <span class="inline-flex items-center gap-1 text-[10px] font-semibold px-2 py-0.5 rounded-md bg-amber-500 text-white shadow-xs">
              🔥 Giảm sốc Online
            </span>
          </div>

          <!-- Top Right Action Buttons (Favorite + Share) -->
          <div class="absolute top-4 right-4 z-10 flex items-center gap-2">
            <button 
              @click="handleShare"
              class="p-2.5 rounded-full bg-white/95 border border-zinc-200 text-zinc-500 hover:text-zinc-900 hover:bg-zinc-50 shadow-xs transition cursor-pointer"
              title="Chia sẻ sản phẩm"
            >
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="18" cy="5" r="3"/><circle cx="6" cy="12" r="3"/><circle cx="18" cy="19" r="3"/><line x1="8.59" y1="13.51" x2="15.42" y2="17.49"/><line x1="15.41" y1="6.51" x2="8.59" y2="10.49"/></svg>
            </button>
            <button 
              @click="handleToggleWishlist"
              class="p-2.5 rounded-full bg-white/95 border border-zinc-200 text-zinc-400 hover:text-red-500 shadow-xs transition cursor-pointer"
              :class="{ 'text-red-500 border-red-200 bg-red-50/50': isFav }"
              title="Thêm vào yêu thích"
            >
              <svg class="w-4 h-4" :fill="isFav ? 'currentColor' : 'none'" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" /></svg>
            </button>
          </div>

          <!-- Active Display Image -->
          <img 
            :src="activeImage || defaultImage" 
            :alt="product.name"
            class="object-contain max-h-[85%] max-w-[85%] transition-transform duration-500 group-hover:scale-105 select-none"
          />

          <!-- Gallery Floating Navigation Arrows -->
          <button 
            v-if="galleryImages.length > 1"
            @click="prevImage"
            class="absolute left-3 top-1/2 -translate-y-1/2 w-9 h-9 rounded-full bg-white/90 border border-zinc-200 text-zinc-700 hover:bg-white hover:text-red-600 shadow-md flex items-center justify-center transition opacity-0 group-hover:opacity-100 cursor-pointer"
          >
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m15 18-6-6 6-6"/></svg>
          </button>
          <button 
            v-if="galleryImages.length > 1"
            @click="nextImage"
            class="absolute right-3 top-1/2 -translate-y-1/2 w-9 h-9 rounded-full bg-white/90 border border-zinc-200 text-zinc-700 hover:bg-white hover:text-red-600 shadow-md flex items-center justify-center transition opacity-0 group-hover:opacity-100 cursor-pointer"
          >
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m9 18 6-6-6-6"/></svg>
          </button>

          <!-- Bottom Image Index Badge -->
          <div class="absolute bottom-4 right-4 px-2.5 py-1 rounded-full bg-zinc-900/70 backdrop-blur-xs text-white text-[11px] font-mono">
            {{ activeImageIndex + 1 }} / {{ galleryImages.length }}
          </div>
        </div>

        <!-- FPT Interactive Media Thumbnails & Function Tabs -->
        <div class="flex items-center gap-2 overflow-x-auto pb-2 scrollbar-none">
          <!-- Video Review Tab -->
          <button 
            @click="openVideoModal"
            class="flex flex-col items-center justify-center w-18 h-18 rounded-2xl border bg-white text-zinc-700 hover:border-red-500 hover:text-red-600 shrink-0 transition p-2 cursor-pointer group/btn"
          >
            <div class="w-6 h-6 rounded-full bg-red-100 text-red-600 group-hover/btn:bg-red-600 group-hover/btn:text-white flex items-center justify-center transition">
              <svg class="w-3.5 h-3.5 ml-0.5 fill-current" viewBox="0 0 24 24"><polygon points="5 3 19 12 5 21 5 3"/></svg>
            </div>
            <span class="text-[10px] font-bold mt-1">Video</span>
          </button>

          <!-- Highlight Feature Icon Tab -->
          <button 
            @click="scrollToSection('description')"
            class="flex flex-col items-center justify-center w-18 h-18 rounded-2xl border bg-white text-zinc-700 hover:border-amber-500 hover:text-amber-600 shrink-0 transition p-2 cursor-pointer"
          >
            <div class="w-6 h-6 rounded-full bg-amber-100 text-amber-600 flex items-center justify-center">
              <svg class="w-3.5 h-3.5 fill-current" viewBox="0 0 24 24"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
            </div>
            <span class="text-[10px] font-bold mt-1">Nổi bật</span>
          </button>

          <!-- Real Image Thumbnails -->
          <button 
            v-for="(img, idx) in galleryImages" 
            :key="idx"
            @click="selectImage(idx)"
            class="w-18 h-18 rounded-2xl bg-white border p-1.5 shrink-0 transition relative cursor-pointer"
            :class="activeImage === img ? 'border-red-600 ring-2 ring-red-600/30' : 'border-zinc-200 hover:border-zinc-300'"
          >
            <img :src="img" class="object-contain w-full h-full" :alt="`Góc ảnh ${idx + 1}`" />
          </button>

          <!-- Unbox Tab -->
          <button 
            @click="openUnboxModal"
            class="flex flex-col items-center justify-center w-18 h-18 rounded-2xl border bg-white text-zinc-700 hover:border-zinc-400 shrink-0 transition p-2 cursor-pointer"
          >
            <div class="w-6 h-6 rounded-full bg-zinc-100 text-zinc-600 flex items-center justify-center">
              <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16Z"/><path d="m3.3 7 8.7 5 8.7-5"/><path d="M12 22V12"/></svg>
            </div>
            <span class="text-[10px] font-bold mt-1">Mở hộp</span>
          </button>

          <!-- 360 View Tab -->
          <button 
            @click="open360Modal"
            class="flex flex-col items-center justify-center w-18 h-18 rounded-2xl border bg-white text-zinc-700 hover:border-zinc-400 shrink-0 transition p-2 cursor-pointer"
          >
            <div class="w-6 h-6 rounded-full bg-zinc-100 text-zinc-600 flex items-center justify-center">
              <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21.5 2v6h-6M21.34 15.57a10 10 0 1 1-.57-8.38l5.67-5.67"/></svg>
            </div>
            <span class="text-[10px] font-bold mt-1">Xoay 360°</span>
          </button>
        </div>

        <!-- TechStore Policy & Gold Guarantees Bar (FPT Style) -->
        <div class="grid grid-cols-2 sm:grid-cols-4 gap-3 p-4 rounded-2xl bg-zinc-50 border border-zinc-200/80 text-xs">
          <div class="flex items-center gap-2.5">
            <div class="w-8 h-8 rounded-xl bg-white border border-zinc-200 flex items-center justify-center text-red-600 shrink-0 shadow-2xs">
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="3" width="15" height="13"/><polygon points="16 8 20 8 23 11 23 16 16 16 16 8"/><circle cx="5.5" cy="18.5" r="2.5"/><circle cx="18.5" cy="18.5" r="2.5"/></svg>
            </div>
            <span class="text-zinc-700 font-medium leading-tight">Miễn phí giao hàng toàn quốc (*)</span>
          </div>

          <div class="flex items-center gap-2.5">
            <div class="w-8 h-8 rounded-xl bg-white border border-zinc-200 flex items-center justify-center text-red-600 shrink-0 shadow-2xs">
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
            </div>
            <span class="text-zinc-700 font-medium leading-tight">Chính hãng 100% - Bảo hành 12T</span>
          </div>

          <div class="flex items-center gap-2.5">
            <div class="w-8 h-8 rounded-xl bg-white border border-zinc-200 flex items-center justify-center text-red-600 shrink-0 shadow-2xs">
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="23 4 23 10 17 10"/><path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"/></svg>
            </div>
            <span class="text-zinc-700 font-medium leading-tight">1 đổi 1 trong 30 ngày nếu lỗi</span>
          </div>

          <div class="flex items-center gap-2.5">
            <div class="w-8 h-8 rounded-xl bg-white border border-zinc-200 flex items-center justify-center text-red-600 shrink-0 shadow-2xs">
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 18v-6a9 9 0 0 1 18 0v6"/><path d="M21 19a2 2 0 0 1-2 2h-1a2 2 0 0 1-2-2v-3a2 2 0 0 1 2-2h3zM3 19a2 2 0 0 0 2 2h1a2 2 0 0 0 2-2v-3a2 2 0 0 0-2-2H3z"/></svg>
            </div>
            <span class="text-zinc-700 font-medium leading-tight">Hỗ trợ kỹ thuật trực tuyến</span>
          </div>
        </div>

        <!-- Thông số nổi bật (Quick Key Specs preview card) -->
        <div class="bg-white rounded-3xl border border-zinc-200/80 p-5 sm:p-6 space-y-4 shadow-xs">
          <div class="flex items-center justify-between">
            <h3 class="text-sm font-bold text-zinc-900 flex items-center gap-2">
              <span class="w-2 h-2 rounded-full bg-red-600"></span>
              <span>Thông số nổi bật</span>
            </h3>
            <button 
              @click="scrollToSection('specs')"
              class="text-xs font-semibold text-red-600 hover:text-red-700 hover:underline flex items-center gap-1 cursor-pointer"
            >
              <span>Xem tất cả thông số</span>
              <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m9 18 6-6-6-6"/></svg>
            </button>
          </div>

          <div class="grid grid-cols-2 gap-3">
            <div 
              v-for="(spec, sIdx) in highlightSpecs" 
              :key="sIdx"
              class="p-3.5 rounded-2xl bg-zinc-50/80 border border-zinc-200/60 flex items-start gap-3"
            >
              <div class="p-2 rounded-xl bg-white border border-zinc-200 text-zinc-700 shrink-0">
                <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" v-html="spec.iconSvg"></svg>
              </div>
              <div class="space-y-0.5 min-w-0">
                <div class="text-[11px] text-zinc-400 font-medium">{{ spec.title }}</div>
                <div class="text-xs font-bold text-zinc-900 truncate" :title="spec.value">{{ spec.value }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Section: Mô tả - Thông tin hay (Rich Product Story) -->
        <div id="description" class="bg-white rounded-3xl border border-zinc-200/80 p-6 sm:p-8 space-y-5 shadow-xs relative">
          <div class="flex items-center justify-between pb-3 border-b border-zinc-100">
            <h2 class="text-lg font-bold text-zinc-900 flex items-center gap-2">
              <span class="w-2.5 h-2.5 rounded-full bg-red-600"></span>
              <span>Đặc điểm nổi bật & Thông tin trải nghiệm</span>
            </h2>
          </div>

          <!-- Editorial Article Body with Collapsible Read More -->
          <div 
            class="space-y-4 text-xs sm:text-sm text-zinc-700 leading-relaxed overflow-hidden transition-all duration-300"
            :class="isExpandedStory ? 'max-h-none' : 'max-h-[320px]'"
          >
            <p class="font-medium text-zinc-800 text-sm sm:text-base">
              {{ product.description || 'Trải nghiệm đỉnh cao công nghệ với sự hoàn thiện vượt trội từ thiết kế kim loại cao cấp đến sức mạnh xử lý hàng đầu.' }}
            </p>

            <div class="rounded-2xl overflow-hidden border border-zinc-200 my-3">
              <img 
                :src="activeImage || defaultImage" 
                class="w-full h-56 sm:h-72 object-cover" 
                alt="Hình minh họa sản phẩm"
              />
              <div class="p-2.5 bg-zinc-50 text-[11px] text-zinc-500 text-center border-t border-zinc-200">
                Ảnh thực tế và góc nhìn chi tiết sản phẩm chính hãng tại TechStore
              </div>
            </div>

            <h4 class="text-sm font-bold text-zinc-900 mt-4 flex items-center gap-1.5">
              <span class="text-red-600">●</span> Trí tuệ nhân tạo thế hệ mới & Tối ưu hóa hiệu năng
            </h4>
            <p>
              Tích hợp hệ sinh thái AI thông minh toàn diện, hỗ trợ khoanh vùng tìm kiếm đa năng (Circle to Search), dịch thuật cuộc gọi trực tiếp đa ngôn ngữ theo thời gian thực và trợ lý ghi chú tóm tắt nội dung thông minh.
            </p>

            <h4 class="text-sm font-bold text-zinc-900 mt-4 flex items-center gap-1.5">
              <span class="text-red-600">●</span> Hệ thống Camera siêu nét - Bắt trọn khoảnh khắc đêm
            </h4>
            <p>
              Cảm biến máy ảnh độ phân giải cao kết hợp bộ xử lý hình ảnh ProVisual Engine mang đến khả năng khử nhiễu AI ấn tượng, zoom quang học sắc nét vượt tầm nhìn thông thường và màu sắc chân thực trong mọi điều kiện ánh sáng.
            </p>

            <h4 class="text-sm font-bold text-zinc-900 mt-4 flex items-center gap-1.5">
              <span class="text-red-600">●</span> Thời lượng pin bền bỉ đồng hành suốt ngày dài
            </h4>
            <p>
              Tối ưu hóa năng lượng thông minh từ con chip xử lý hiện đại cùng công nghệ sạc siêu nhanh, giúp thiết bị nhanh chóng sẵn sàng cho mọi hành trình sáng tạo và làm việc của bạn.
            </p>
          </div>

          <!-- Bottom Fade Gradient & Read More Button -->
          <div 
            v-if="!isExpandedStory" 
            class="absolute bottom-0 left-0 right-0 h-28 bg-gradient-to-t from-white via-white/80 to-transparent flex items-end justify-center pb-4 rounded-b-3xl"
          >
            <button 
              @click="isExpandedStory = true"
              class="px-5 py-2 rounded-xl text-xs font-bold text-red-600 bg-white border border-red-200 shadow-md hover:bg-red-50 flex items-center gap-1.5 transition cursor-pointer"
            >
              <span>Đọc thêm nội dung</span>
              <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m6 9 6 6 6-6"/></svg>
            </button>
          </div>

          <div v-else class="text-center pt-2">
            <button 
              @click="isExpandedStory = false"
              class="px-4 py-1.5 rounded-xl text-xs font-bold text-zinc-600 hover:text-zinc-900 bg-zinc-100 hover:bg-zinc-200 transition cursor-pointer"
            >
              Thu gọn nội dung ▲
            </button>
          </div>
        </div>
      </div>

      <!-- ================= RIGHT COLUMN (BUY BOX & OFFERS) ================= -->
      <div class="lg:col-span-5 space-y-5">
        
        <!-- FPT GIÁ SỐC ONLINE / FLASH SALE BANNER -->
        <div class="rounded-3xl border border-red-200/90 bg-gradient-to-br from-red-50/50 via-white to-red-50/30 p-5 sm:p-6 shadow-sm space-y-4">
          
          <!-- Flash Sale Header with Animated Fire & Live Countdown -->
          <div class="flex items-center justify-between pb-3 border-b border-red-100">
            <div class="flex items-center gap-2">
              <span class="flex h-6 px-2.5 rounded-lg bg-red-600 text-white font-extrabold text-[11px] uppercase items-center gap-1 shadow-xs animate-pulse">
                <span>🔥</span>
                <span>GIÁ SỐC ONLINE</span>
              </span>
            </div>

            <!-- Countdown Timer -->
            <div class="flex items-center gap-1 text-xs">
              <span class="text-zinc-500 font-medium text-[11px] hidden sm:inline">Kết thúc sau:</span>
              <div class="flex items-center gap-1 font-mono font-bold text-white text-xs">
                <span class="bg-zinc-900 px-1.5 py-0.5 rounded">{{ timer.hours }}</span>
                <span class="text-zinc-800">:</span>
                <span class="bg-zinc-900 px-1.5 py-0.5 rounded">{{ timer.minutes }}</span>
                <span class="text-zinc-800">:</span>
                <span class="bg-zinc-900 px-1.5 py-0.5 rounded">{{ timer.seconds }}</span>
              </div>
            </div>
          </div>

          <!-- Price Display -->
          <div class="space-y-1">
            <div class="flex items-baseline flex-wrap gap-2.5">
              <span class="text-2xl sm:text-3xl lg:text-4xl font-black text-red-600 tracking-tight">
                {{ formatPrice(currentPrice) }}
              </span>
              <span class="text-sm sm:text-base text-zinc-400 line-through">
                {{ formatPrice(currentOriginalPrice) }}
              </span>
              <span class="text-xs font-bold text-red-700 bg-red-100/80 px-2 py-0.5 rounded-md border border-red-200">
                -{{ discountPercent }}%
              </span>
            </div>

            <!-- Loyalty F-Points Earn Badge -->
            <div class="pt-1 flex items-center gap-2 text-xs">
              <div class="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full bg-amber-50 text-amber-800 border border-amber-200 text-[11px] font-semibold">
                <span>🪙</span>
                <span>Tích lũy +{{ loyaltyPoints }} điểm thưởng TechStore</span>
              </div>
            </div>

            <!-- Sold quota progress bar -->
            <div class="pt-2 space-y-1">
              <div class="flex justify-between text-[11px] text-zinc-500 font-medium">
                <span>Đã bán 18/50 suất</span>
                <span class="text-red-600 font-semibold">Sắp hết hàng</span>
              </div>
              <div class="h-2 w-full bg-zinc-200 rounded-full overflow-hidden">
                <div class="h-full bg-gradient-to-r from-red-500 to-amber-500 rounded-full w-[36%]"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- Thu Cũ Đổi Mới Banner (Trade-In Offer) -->
        <div class="p-3.5 rounded-2xl bg-gradient-to-r from-blue-50/80 to-indigo-50/80 border border-blue-200/80 flex items-center justify-between shadow-2xs">
          <div class="flex items-center gap-2.5">
            <div class="w-8 h-8 rounded-xl bg-blue-600 text-white flex items-center justify-center shrink-0">
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/></svg>
            </div>
            <div>
              <div class="text-xs font-bold text-zinc-900">Thu cũ đổi mới - Trợ giá đến 5.000.000đ</div>
              <div class="text-[11px] text-zinc-500">Thủ tục nhanh gọn, trợ giá trực tiếp vào hóa đơn</div>
            </div>
          </div>
          <button 
            @click="showTradeInModal = true"
            class="text-xs font-bold text-blue-700 hover:text-blue-800 bg-white px-3 py-1.5 rounded-xl border border-blue-200 shadow-2xs hover:bg-blue-50 transition shrink-0 cursor-pointer"
          >
            Định giá ngay ›
          </button>
        </div>

        <!-- Variant Selector: Storage Options (Phiên bản cấu hình) -->
        <div v-if="product.variants && product.variants.length > 0" class="space-y-2.5">
          <label class="block text-xs font-bold text-zinc-800 uppercase tracking-wider flex items-center justify-between">
            <span>Phiên bản cấu hình / Bộ nhớ:</span>
            <span class="text-zinc-400 font-normal normal-case">Chọn phiên bản phù hợp</span>
          </label>
          <div class="grid grid-cols-2 sm:grid-cols-3 gap-2">
            <button 
              v-for="v in product.variants" 
              :key="v.id"
              @click="selectedVariant = v"
              class="p-3 rounded-2xl border text-left transition relative flex flex-col justify-between cursor-pointer"
              :class="selectedVariant?.id === v.id ? 'bg-red-50/40 border-red-600 ring-1 ring-red-600 text-zinc-900 shadow-xs' : 'bg-white border-zinc-200 hover:border-zinc-300 text-zinc-700'"
            >
              <div class="font-bold text-xs truncate">{{ v.name }}</div>
              <div class="text-xs font-black text-red-600 mt-1">{{ formatPrice(v.price) }}</div>

              <!-- Selected Tick Corner Badge -->
              <div 
                v-if="selectedVariant?.id === v.id" 
                class="absolute -top-1 -right-1 w-4 h-4 rounded-full bg-red-600 text-white flex items-center justify-center text-[10px]"
              >
                ✓
              </div>
            </button>
          </div>
        </div>

        <!-- Color Selector (Màu sắc sản phẩm) -->
        <div class="space-y-2.5">
          <label class="block text-xs font-bold text-zinc-800 uppercase tracking-wider flex items-center justify-between">
            <span>Màu sắc:</span>
            <span class="text-zinc-600 font-semibold normal-case">{{ selectedColor.name }}</span>
          </label>
          <div class="grid grid-cols-2 sm:grid-cols-4 gap-2">
            <button 
              v-for="c in colorOptions" 
              :key="c.name"
              @click="selectedColor = c"
              class="p-2.5 rounded-2xl border transition flex items-center gap-2 cursor-pointer relative"
              :class="selectedColor.name === c.name ? 'border-red-600 bg-red-50/30 ring-1 ring-red-600' : 'border-zinc-200 bg-white hover:border-zinc-300'"
            >
              <span class="w-4 h-4 rounded-full shrink-0 border border-black/10" :style="{ backgroundColor: c.hex }"></span>
              <span class="text-xs font-medium text-zinc-800 truncate">{{ c.name }}</span>

              <div 
                v-if="selectedColor.name === c.name" 
                class="absolute -top-1 -right-1 w-3.5 h-3.5 rounded-full bg-red-600 text-white flex items-center justify-center text-[9px]"
              >
                ✓
              </div>
            </button>
          </div>
        </div>

        <!-- Store Location & Stock Status -->
        <div class="p-3.5 rounded-2xl bg-zinc-50 border border-zinc-200/80 space-y-2 text-xs">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-1.5 text-zinc-600 font-medium">
              <svg class="w-4 h-4 text-red-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/><circle cx="12" cy="10" r="3"/></svg>
              <span>Xem siêu thị có hàng:</span>
            </div>
            <select 
              v-model="selectedCity"
              class="bg-white border border-zinc-200 rounded-lg px-2 py-1 text-xs font-semibold text-zinc-800 focus:outline-none"
            >
              <option value="HN">Hà Nội (28 siêu thị)</option>
              <option value="HCM">TP. Hồ Chí Minh (45 siêu thị)</option>
              <option value="DN">Đà Nẵng (12 siêu thị)</option>
              <option value="CT">Cần Thơ (8 siêu thị)</option>
            </select>
          </div>

          <div class="flex items-center gap-2 pt-1">
            <span v-if="currentStock > 0" class="inline-flex items-center gap-1.5 text-emerald-700 font-bold bg-emerald-50 px-2.5 py-1 rounded-lg border border-emerald-200">
              <span class="w-2 h-2 rounded-full bg-emerald-600 animate-ping"></span>
              <span>Có sẵn hàng tại {{ selectedCity === 'HCM' ? '45' : '28' }} siêu thị</span>
            </span>
            <span v-else class="inline-flex items-center gap-1.5 text-zinc-500 font-medium bg-zinc-100 px-2.5 py-1 rounded-lg border border-zinc-200">
              <span>Tạm thời hết hàng tại khu vực này</span>
            </span>
          </div>
        </div>

        <!-- Khuyến mãi chính & Ưu đãi được hưởng (FPT Promo Box) -->
        <div class="rounded-3xl border border-red-200 bg-white overflow-hidden shadow-xs">
          <div class="bg-gradient-to-r from-red-600 to-rose-600 px-4 py-2.5 flex items-center justify-between text-white">
            <div class="flex items-center gap-2">
              <svg class="w-4 h-4 fill-current" viewBox="0 0 24 24"><path d="M20 12v10H4V12"/><path d="M2 7h20v5H2z"/><path d="M12 22V7"/><path d="M12 7H7.5a2.5 2.5 0 0 1 0-5C11 2 12 7 12 7z"/><path d="M12 7h4.5a2.5 2.5 0 0 0 0-5C13 2 12 7 12 7z"/></svg>
              <h4 class="text-xs font-bold uppercase tracking-wider">Khuyến mãi chính</h4>
            </div>
            <span class="text-[11px] font-semibold bg-white/20 px-2 py-0.5 rounded-full">Đặc quyền online</span>
          </div>

          <div class="p-4 sm:p-5 space-y-3">
            <ul class="space-y-2.5 text-xs text-zinc-700">
              <li class="flex items-start gap-2">
                <span class="w-4 h-4 rounded-full bg-red-100 text-red-600 flex items-center justify-center text-[10px] font-bold shrink-0 mt-0.5">1</span>
                <span><strong>Giảm ngay 1.000.000đ</strong> khi thanh toán qua thẻ tín dụng ngân hàng đối tác hoặc quét mã VNPAY-QR.</span>
              </li>
              <li class="flex items-start gap-2">
                <span class="w-4 h-4 rounded-full bg-red-100 text-red-600 flex items-center justify-center text-[10px] font-bold shrink-0 mt-0.5">2</span>
                <span><strong>Tặng gói bảo hành rơi vỡ màn hình 12 tháng</strong> chính hãng trị giá 2.500.000đ.</span>
              </li>
              <li class="flex items-start gap-2">
                <span class="w-4 h-4 rounded-full bg-red-100 text-red-600 flex items-center justify-center text-[10px] font-bold shrink-0 mt-0.5">3</span>
                <span><strong>Trả góp 0% lãi suất</strong> với kỳ hạn linh hoạt đến 12 tháng qua thẻ tín dụng hoặc cty tài chính.</span>
              </li>

              <template v-if="showMoreOffers">
                <li class="flex items-start gap-2">
                  <span class="w-4 h-4 rounded-full bg-red-100 text-red-600 flex items-center justify-center text-[10px] font-bold shrink-0 mt-0.5">4</span>
                  <span><strong>Giảm thêm 500.000đ</strong> cho Học sinh - Sinh viên & Giáo viên khi mua qua TechStore Education.</span>
                </li>
                <li class="flex items-start gap-2">
                  <span class="w-4 h-4 rounded-full bg-red-100 text-red-600 flex items-center justify-center text-[10px] font-bold shrink-0 mt-0.5">5</span>
                  <span><strong>Giảm đến 50%</strong> khi mua kèm phụ kiện chính hãng (Củ sạc 45W, Bao da S-Pen, Dán màn hình).</span>
                </li>
              </template>
            </ul>

            <button 
              @click="showMoreOffers = !showMoreOffers"
              class="text-xs font-bold text-red-600 hover:text-red-700 flex items-center gap-1 cursor-pointer pt-1"
            >
              <span>{{ showMoreOffers ? 'Thu gọn ưu đãi' : 'Xem thêm 2 ưu đãi đặc quyền khác' }}</span>
              <svg class="w-3.5 h-3.5 transition-transform" :class="{ 'rotate-180': showMoreOffers }" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m6 9 6 6 6-6"/></svg>
            </button>
          </div>
        </div>

        <!-- Mua Kèm Với Giá Ưu Đãi (Combo Cross-sell Card) -->
        <div class="rounded-3xl border border-zinc-200/90 bg-white p-4 sm:p-5 space-y-3 shadow-xs">
          <div class="flex items-center justify-between">
            <h4 class="text-xs font-bold text-zinc-900 flex items-center gap-1.5">
              <span>🔥</span>
              <span>Mua kèm với giá ưu đãi</span>
            </h4>
            <span class="text-[11px] text-zinc-400">Tiết kiệm đến 30%</span>
          </div>

          <div class="space-y-2">
            <label 
              v-for="combo in comboOffers" 
              :key="combo.id"
              class="flex items-center justify-between p-3 rounded-2xl border transition cursor-pointer"
              :class="combo.selected ? 'bg-red-50/30 border-red-200' : 'bg-zinc-50/50 border-zinc-200/80 hover:bg-zinc-50'"
            >
              <div class="flex items-center gap-3">
                <input 
                  type="checkbox" 
                  v-model="combo.selected"
                  class="w-4 h-4 accent-red-600 rounded"
                />
                <div>
                  <div class="text-xs font-semibold text-zinc-800">{{ combo.name }}</div>
                  <div class="text-[11px] text-zinc-400 line-through">{{ formatPrice(combo.originalPrice) }}</div>
                </div>
              </div>
              <div class="text-right">
                <div class="text-xs font-black text-red-600">{{ formatPrice(combo.price) }}</div>
                <div class="text-[10px] text-emerald-600 font-semibold">Tiết kiệm {{ formatPrice(combo.originalPrice - combo.price) }}</div>
              </div>
            </label>
          </div>
        </div>

        <!-- Khuyến mãi thanh toán (Payment Partner Bank Cards) -->
        <div class="rounded-3xl border border-zinc-200/90 bg-white p-4 sm:p-5 space-y-3 shadow-xs">
          <div class="flex items-center justify-between">
            <h4 class="text-xs font-bold text-zinc-900">Khuyến mãi thanh toán ngân hàng</h4>
            <span class="text-[11px] text-red-600 font-semibold">Ưu đãi thẻ</span>
          </div>

          <div class="grid grid-cols-4 gap-2 text-center text-[10px] font-bold">
            <div class="p-2 rounded-xl bg-zinc-50 border border-zinc-200 text-zinc-700">TECHCOMBANK</div>
            <div class="p-2 rounded-xl bg-zinc-50 border border-zinc-200 text-zinc-700">NAM A BANK</div>
            <div class="p-2 rounded-xl bg-zinc-50 border border-zinc-200 text-zinc-700">VIKKI BANK</div>
            <div class="p-2 rounded-xl bg-zinc-50 border border-zinc-200 text-zinc-700">KREDIVO</div>
          </div>
          <p class="text-[11px] text-zinc-500 leading-normal">
            Giảm ngay đến 800.000đ cho đơn hàng từ 20 triệu khi thanh toán 100% hoặc trả góp qua thẻ tín dụng ngân hàng liên kết.
          </p>
        </div>

        <!-- Quantity & Big Call To Action Buttons (FPT Dual CTA Buttons) -->
        <div class="space-y-3 pt-2">
          <!-- Quantity Stepper -->
          <div class="flex items-center gap-3">
            <span class="text-xs font-bold text-zinc-700">Số lượng:</span>
            <div class="flex items-center bg-white border border-zinc-300 rounded-xl p-1 shadow-2xs">
              <button 
                @click="quantity = Math.max(1, quantity - 1)" 
                class="w-7 h-7 rounded-lg text-zinc-700 hover:bg-zinc-100 flex items-center justify-center font-bold text-sm cursor-pointer"
              >
                -
              </button>
              <span class="w-10 text-center text-xs font-black text-zinc-900">{{ quantity }}</span>
              <button 
                @click="quantity = Math.min(currentStock || 99, quantity + 1)" 
                class="w-7 h-7 rounded-lg text-zinc-700 hover:bg-zinc-100 flex items-center justify-center font-bold text-sm cursor-pointer"
              >
                +
              </button>
            </div>
            <span class="text-[11px] text-zinc-400">
              ({{ currentStock > 0 ? `Còn ${currentStock} máy` : 'Tạm hết' }})
            </span>
          </div>

          <!-- Dual Main CTAs -->
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <!-- Red Buy Now CTA -->
            <button 
              @click="handleBuyNow"
              :disabled="currentStock <= 0 || addingToCart"
              class="py-3.5 px-4 rounded-2xl bg-gradient-to-r from-red-600 to-rose-600 hover:from-red-700 hover:to-rose-700 text-white font-extrabold shadow-md transition-all active:scale-98 disabled:opacity-40 disabled:pointer-events-none flex flex-col items-center justify-center cursor-pointer"
            >
              <span class="text-sm uppercase tracking-wide">MUA NGAY</span>
              <span class="text-[11px] font-normal opacity-90">Giao tận nơi hoặc nhận tại siêu thị</span>
            </button>

            <!-- Dark Installment CTA -->
            <button 
              @click="openInstallmentModal"
              :disabled="currentStock <= 0"
              class="py-3.5 px-4 rounded-2xl bg-zinc-900 hover:bg-zinc-800 text-white font-extrabold shadow-md transition-all active:scale-98 disabled:opacity-40 disabled:pointer-events-none flex flex-col items-center justify-center cursor-pointer"
            >
              <span class="text-sm uppercase tracking-wide">TRẢ GÓP 0%</span>
              <span class="text-[11px] font-normal opacity-90">Duyệt nhanh qua điện thoại trong 5 phút</span>
            </button>
          </div>

          <!-- Add To Cart Secondary Button -->
          <button 
            @click="handleAddToCart"
            :disabled="currentStock <= 0 || addingToCart"
            class="w-full py-3 px-4 rounded-2xl border-2 border-red-600 text-red-600 hover:bg-red-50 font-bold text-xs sm:text-sm transition-all flex items-center justify-center gap-2 cursor-pointer disabled:opacity-40"
          >
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 11V7a4 4 0 0 0-8 0v4M5 9h14l1 12H4L5 9z"/></svg>
            <span>{{ addingToCart ? 'Đang thêm vào giỏ...' : 'Thêm vào giỏ hàng' }}</span>
          </button>

          <!-- Toast Message Feedback -->
          <div v-if="toastMessage" class="p-3 bg-emerald-50 border border-emerald-200 text-emerald-800 rounded-2xl text-xs font-semibold text-center animate-fade-in">
            {{ toastMessage }}
          </div>

          <!-- Hotline Consultation Assistance -->
          <div class="text-center text-xs text-zinc-500 pt-1">
            <span>Tư vấn mua hàng miễn phí: </span>
            <a href="tel:18006601" class="font-bold text-red-600 hover:underline">1800 6601</a>
            <span> (7:30 - 22:00)</span>
          </div>
        </div>
      </div>
    </div>

    <!-- ================= HARDWARE SPECS SECTION ================= -->
    <div id="specs" class="pt-4 scroll-mt-24">
      <HardwareSpecsTable :attributes="product.attributes" />
    </div>

    <!-- ================= SIMILAR PRODUCTS SECTION ================= -->
    <div id="similar" class="pt-4 scroll-mt-24">
      <RecommendationCarousel
        title="Sản Phẩm Tương Tự & Phù Hợp"
        subtitle="Các thiết bị có cùng phân khúc cao cấp và đặc tính kỹ thuật tương đương"
        :items="similarProducts"
        :loading="loadingSimilar"
      />
    </div>

    <!-- ================= CUSTOMER REVIEWS & FEEDBACK SECTION ================= -->
    <section id="reviews" class="bg-white border border-zinc-200/80 rounded-3xl p-6 sm:p-8 space-y-6 shadow-xs scroll-mt-24">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between pb-4 border-b border-zinc-100 gap-4">
        <div>
          <h3 class="text-lg font-bold text-zinc-900 flex items-center gap-2">
            <span class="w-2.5 h-2.5 rounded-full bg-red-600"></span>
            <span>Đánh giá & Bình luận khách hàng</span>
          </h3>
          <p class="text-xs text-zinc-500 mt-0.5">Nhận xét thực tế từ những khách hàng đã mua và trải nghiệm sản phẩm tại hệ thống</p>
        </div>

        <button 
          @click="showReviewForm = !showReviewForm"
          class="px-5 py-2.5 rounded-xl text-xs font-bold bg-red-600 hover:bg-red-700 text-white transition self-start sm:self-auto shadow-xs cursor-pointer"
        >
          {{ showReviewForm ? 'Đóng form' : 'Viết đánh giá của bạn' }}
        </button>
      </div>

      <!-- FPT Style Rating Overview Score Card -->
      <div class="grid grid-cols-1 md:grid-cols-12 gap-6 p-6 rounded-3xl bg-zinc-50 border border-zinc-200/80 items-center">
        <!-- Big Rating Number -->
        <div class="md:col-span-4 text-center border-b md:border-b-0 md:border-r border-zinc-200 pb-4 md:pb-0 md:pr-4">
          <div class="text-4xl sm:text-5xl font-black text-zinc-900 tracking-tight">
            {{ reviewSummary.averageRating || '4.9' }}<span class="text-xl text-zinc-400">/5</span>
          </div>
          <div class="flex items-center justify-center text-amber-400 text-lg my-1.5">
            <span v-for="s in 5" :key="s">★</span>
          </div>
          <div class="text-xs text-zinc-500 font-medium">
            Dựa trên {{ reviewSummary.totalReviews || 199 }} lượt đánh giá thực tế
          </div>
        </div>

        <!-- 5 Star Breakdown Progress Bars -->
        <div class="md:col-span-8 space-y-2 text-xs">
          <div v-for="bar in starBreakdown" :key="bar.stars" class="flex items-center gap-3">
            <div class="flex items-center gap-1 w-12 font-medium text-zinc-700 shrink-0">
              <span>{{ bar.stars }}</span>
              <span class="text-amber-400">★</span>
            </div>
            <div class="flex-1 h-2.5 bg-zinc-200 rounded-full overflow-hidden">
              <div 
                class="h-full bg-red-600 rounded-full transition-all duration-500" 
                :style="{ width: `${bar.percent}%` }"
              ></div>
            </div>
            <div class="w-14 text-right text-zinc-500 font-mono text-[11px] shrink-0">
              {{ bar.count }} ({{ bar.percent }}%)
            </div>
          </div>
        </div>
      </div>

      <!-- Add Review Form (Toggleable) -->
      <form 
        v-if="showReviewForm" 
        @submit.prevent="handleSubmitReview" 
        class="p-5 sm:p-6 bg-red-50/40 rounded-3xl border border-red-200 space-y-4 animate-fade-in"
      >
        <h4 class="text-xs font-bold text-zinc-800 uppercase tracking-wider">Gửi nhận xét trải nghiệm thiết bị</h4>
        
        <div class="flex items-center gap-3">
          <span class="text-xs font-semibold text-zinc-700">Đánh giá chung:</span>
          <div class="flex gap-1.5">
            <button 
              type="button" 
              v-for="star in 5" 
              :key="star"
              @click="newReview.rating = star"
              class="text-2xl transition cursor-pointer"
              :class="star <= newReview.rating ? 'text-amber-400 scale-110' : 'text-zinc-300 hover:text-zinc-400'"
            >
              ★
            </button>
          </div>
          <span class="text-xs text-zinc-500 font-medium ml-2">
            {{ newReview.rating === 5 ? 'Tuyệt vời' : newReview.rating === 4 ? 'Hài lòng' : 'Bình thường' }}
          </span>
        </div>

        <div>
          <textarea 
            v-model="newReview.comment"
            rows="3"
            required
            placeholder="Chia sẻ nhận xét thực tế về camera, thời lượng pin, hiệu năng, độ mượt mà của máy..."
            class="w-full bg-white border border-zinc-300 rounded-2xl p-3.5 text-xs text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-red-600 focus:ring-1 focus:ring-red-600 transition"
          ></textarea>
        </div>

        <div class="flex justify-end gap-2">
          <button 
            type="button" 
            @click="showReviewForm = false"
            class="px-4 py-2 rounded-xl text-xs font-semibold text-zinc-600 hover:bg-zinc-100 transition cursor-pointer"
          >
            Hủy
          </button>
          <button 
            type="submit"
            :disabled="submittingReview"
            class="px-5 py-2.5 rounded-xl text-xs font-bold bg-red-600 hover:bg-red-700 text-white transition disabled:opacity-40 shadow-xs cursor-pointer"
          >
            {{ submittingReview ? 'Đang gửi...' : 'Gửi nhận xét ngay' }}
          </button>
        </div>
      </form>

      <!-- Review List -->
      <div v-if="displayReviews.length === 0" class="text-center py-10 text-zinc-400 text-xs">
        Chưa có đánh giá nào cho sản phẩm này.
      </div>

      <div v-else class="space-y-4">
        <div 
          v-for="rev in displayReviews" 
          :key="rev.id"
          class="p-5 rounded-2xl bg-zinc-50/70 border border-zinc-200/70 space-y-2.5"
        >
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-2.5">
              <div class="w-8 h-8 rounded-full bg-zinc-800 text-white font-bold text-xs flex items-center justify-center uppercase">
                {{ rev.userFullName?.charAt(0) || 'U' }}
              </div>
              <div>
                <div class="flex items-center gap-2">
                  <span class="text-xs font-bold text-zinc-900">{{ rev.userFullName }}</span>
                  <span class="inline-flex items-center gap-1 text-[10px] font-semibold text-emerald-700 bg-emerald-50 px-1.5 py-0.2 rounded border border-emerald-200">
                    ✓ Đã mua tại TechStore
                  </span>
                </div>
                <div class="text-[11px] text-zinc-400">{{ rev.createdAt || 'Vừa xong' }}</div>
              </div>
            </div>

            <div class="text-amber-400 text-xs">
              <span v-for="s in rev.rating" :key="s">★</span>
            </div>
          </div>

          <p class="text-xs sm:text-sm text-zinc-700 leading-relaxed pl-10">{{ rev.comment }}</p>

          <!-- Store Assistant Reply Thread -->
          <div class="ml-10 p-3.5 rounded-xl bg-white border border-zinc-200/80 space-y-1 text-xs">
            <div class="flex items-center gap-1.5 text-red-600 font-bold text-[11px]">
              <span class="w-1.5 h-1.5 rounded-full bg-red-600"></span>
              <span>Chăm sóc khách hàng TechStore phản hồi:</span>
            </div>
            <p class="text-zinc-600 text-[11px]">
              Cảm ơn anh/chị đã tin tưởng lựa chọn sản phẩm tại TechStore. Chúc anh/chị luôn có những trải nghiệm công nghệ tuyệt vời nhất!
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- ================= FREQUENTLY ASKED QUESTIONS (FAQ) ================= -->
    <section id="faq" class="bg-white border border-zinc-200/80 rounded-3xl p-6 sm:p-8 space-y-4 shadow-xs scroll-mt-24">
      <div class="pb-3 border-b border-zinc-100">
        <h3 class="text-lg font-bold text-zinc-900 flex items-center gap-2">
          <span class="w-2.5 h-2.5 rounded-full bg-red-600"></span>
          <span>Câu hỏi thường gặp</span>
        </h3>
        <p class="text-xs text-zinc-500 mt-0.5">Giải đáp thắc mắc phổ biến về tính năng, chế độ bảo hành và trả góp</p>
      </div>

      <div class="space-y-2.5">
        <div 
          v-for="(faq, fIdx) in faqs" 
          :key="fIdx"
          class="rounded-2xl border transition overflow-hidden"
          :class="faq.open ? 'border-red-200 bg-red-50/20' : 'border-zinc-200/80 bg-white'"
        >
          <button 
            @click="faq.open = !faq.open"
            class="w-full p-4 text-left flex items-center justify-between gap-3 text-xs sm:text-sm font-bold text-zinc-800 cursor-pointer"
          >
            <span>{{ faq.q }}</span>
            <svg class="w-4 h-4 shrink-0 transition-transform text-zinc-400" :class="{ 'rotate-180 text-red-600': faq.open }" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m6 9 6 6 6-6"/></svg>
          </button>
          <div v-if="faq.open" class="px-4 pb-4 text-xs text-zinc-600 leading-relaxed border-t border-zinc-100/60 pt-2.5">
            {{ faq.a }}
          </div>
        </div>
      </div>
    </section>

    <!-- ================= SATISFACTION FEEDBACK WIDGET ================= -->
    <div class="p-6 rounded-3xl bg-zinc-100/80 border border-zinc-200 flex flex-col sm:flex-row items-center justify-between gap-4 text-center sm:text-left">
      <div>
        <h4 class="text-sm font-bold text-zinc-900">Bạn có hài lòng với thông tin tại trang chi tiết sản phẩm này không?</h4>
        <p class="text-xs text-zinc-500 mt-0.5">Ý kiến đóng góp giúp TechStore liên tục nâng cấp chất lượng phục vụ</p>
      </div>
      <div class="flex items-center gap-2 shrink-0">
        <button 
          @click="handleFeedback(true)"
          class="px-4 py-2 rounded-xl text-xs font-bold transition cursor-pointer"
          :class="feedbackState === 'yes' ? 'bg-red-600 text-white' : 'bg-white border border-zinc-300 text-zinc-800 hover:bg-zinc-50'"
        >
          👍 Hài lòng
        </button>
        <button 
          @click="handleFeedback(false)"
          class="px-4 py-2 rounded-xl text-xs font-bold transition cursor-pointer"
          :class="feedbackState === 'no' ? 'bg-zinc-800 text-white' : 'bg-white border border-zinc-300 text-zinc-800 hover:bg-zinc-50'"
        >
          👎 Chưa hài lòng
        </button>
      </div>
    </div>

    <!-- ================= FPT STICKY BOTTOM PURCHASE BAR ================= -->
    <div 
      v-if="showStickyBottomBar"
      class="fixed bottom-0 left-0 right-0 z-40 bg-white/95 backdrop-blur-md border-t border-zinc-200/90 shadow-2xl py-2.5 px-4 animate-slide-up"
    >
      <div class="max-w-7xl mx-auto flex items-center justify-between gap-4">
        <!-- Left: Product Mini Info -->
        <div class="flex items-center gap-3 min-w-0">
          <img 
            :src="activeImage || defaultImage" 
            :alt="product.name" 
            class="w-10 h-10 object-contain rounded-lg border border-zinc-200 bg-white p-0.5 shrink-0 hidden sm:block"
          />
          <div class="min-w-0">
            <div class="text-xs font-extrabold text-zinc-900 truncate">{{ product.name }}</div>
            <div class="text-[11px] text-zinc-500 truncate">
              Phân loại: <strong class="text-zinc-800">{{ selectedVariant?.name || 'Mặc định' }}</strong> | {{ selectedColor.name }}
            </div>
          </div>
        </div>

        <!-- Middle: Price and Points -->
        <div class="hidden md:flex items-center gap-3 shrink-0">
          <div class="flex items-center gap-1 px-2 py-0.5 rounded-md bg-amber-50 text-amber-700 text-[10px] font-bold border border-amber-200">
            <span>🪙</span>
            <span>+{{ loyaltyPoints }} điểm</span>
          </div>
          <div class="text-right">
            <div class="text-base font-black text-red-600">{{ formatPrice(currentPrice) }}</div>
            <div class="text-[10px] text-zinc-400 line-through">{{ formatPrice(currentOriginalPrice) }}</div>
          </div>
        </div>

        <!-- Right: Action Buttons -->
        <div class="flex items-center gap-2 shrink-0">
          <button 
            @click="handleAddToCart"
            :disabled="currentStock <= 0 || addingToCart"
            class="p-2.5 rounded-xl border border-red-200 bg-red-50 text-red-600 hover:bg-red-100 transition cursor-pointer disabled:opacity-40"
            title="Thêm vào giỏ hàng"
          >
            <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 11V7a4 4 0 0 0-8 0v4M5 9h14l1 12H4L5 9z"/></svg>
          </button>

          <button 
            @click="handleBuyNow"
            :disabled="currentStock <= 0 || addingToCart"
            class="px-5 py-2.5 rounded-xl bg-gradient-to-r from-red-600 to-rose-600 hover:from-red-700 hover:to-rose-700 text-white font-black text-xs uppercase tracking-wider shadow-sm transition active:scale-95 disabled:opacity-40 cursor-pointer"
          >
            MUA NGAY
          </button>

          <button 
            @click="openInstallmentModal"
            class="px-4 py-2.5 rounded-xl bg-zinc-900 hover:bg-zinc-800 text-white font-bold text-xs uppercase tracking-wider transition active:scale-95 cursor-pointer hidden sm:block"
          >
            TRẢ GÓP 0%
          </button>
        </div>
      </div>
    </div>

    <!-- ================= MODALS & DIALOGS ================= -->

    <!-- 1. Video Review Modal -->
    <Teleport to="body">
      <div v-if="showVideoModal" class="fixed inset-0 z-50 bg-black/70 backdrop-blur-xs flex items-center justify-center p-4">
        <div class="bg-white rounded-3xl max-w-2xl w-full overflow-hidden shadow-2xl space-y-3 p-4">
          <div class="flex items-center justify-between px-2">
            <h4 class="text-sm font-bold text-zinc-900">Video giới thiệu & Trải nghiệm thực tế</h4>
            <button @click="showVideoModal = false" class="p-1 rounded-full hover:bg-zinc-100 text-zinc-500 cursor-pointer">
              ✕
            </button>
          </div>
          <div class="aspect-video w-full rounded-2xl overflow-hidden bg-black flex items-center justify-center">
            <iframe 
              class="w-full h-full"
              src="https://www.youtube-nocookie.com/embed/dQw4w9WgXcQ?autoplay=0" 
              title="Product Video Review"
              frameborder="0"
              allowfullscreen
            ></iframe>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- 2. Trade-In Estimation Modal -->
    <Teleport to="body">
      <div v-if="showTradeInModal" class="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
        <div class="bg-white rounded-3xl max-w-md w-full p-6 space-y-4 shadow-2xl">
          <div class="flex items-center justify-between">
            <h4 class="text-sm font-bold text-zinc-900">Định giá thu cũ đổi mới</h4>
            <button @click="showTradeInModal = false" class="text-zinc-400 hover:text-zinc-600 cursor-pointer">✕</button>
          </div>
          <div class="space-y-3 text-xs">
            <div>
              <label class="block font-semibold text-zinc-700 mb-1">Dòng máy cũ của bạn:</label>
              <select class="w-full border border-zinc-200 rounded-xl p-2.5 bg-zinc-50 font-medium">
                <option>Samsung Galaxy S24 Ultra (Trợ giá đến 18.500.000đ)</option>
                <option>Samsung Galaxy S23 Ultra (Trợ giá đến 13.000.000đ)</option>
                <option>iPhone 15 Pro Max (Trợ giá đến 21.000.000đ)</option>
                <option>iPhone 14 Pro Max (Trợ giá đến 15.000.000đ)</option>
              </select>
            </div>
            <div>
              <label class="block font-semibold text-zinc-700 mb-1">Tình trạng máy cũ:</label>
              <select class="w-full border border-zinc-200 rounded-xl p-2.5 bg-zinc-50 font-medium">
                <option>Loại 1: Hoạt động hoàn hảo, ngoại hình như mới 99%</option>
                <option>Loại 2: Trầy xước nhẹ, màn hình nguyên bản</option>
                <option>Loại 3: Cấn móp nhẹ góc cạnh</option>
              </select>
            </div>
            <div class="p-3.5 rounded-2xl bg-emerald-50 border border-emerald-200 text-emerald-800 space-y-1">
              <div class="text-[11px] font-semibold">Ước tính mức bù trừ khi lên đời:</div>
              <div class="text-base font-black text-emerald-700">Chỉ từ 4.500.000đ - 7.000.000đ</div>
            </div>
          </div>
          <button 
            @click="showTradeInModal = false; toast.success('Đã lưu thông tin định giá!')"
            class="w-full py-2.5 rounded-xl bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs transition cursor-pointer"
          >
            Đăng ký lên đời ngay
          </button>
        </div>
      </div>
    </Teleport>

    <!-- 3. Installment Calculator Modal -->
    <Teleport to="body">
      <div v-if="showInstallmentModal" class="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
        <div class="bg-white rounded-3xl max-w-lg w-full p-6 space-y-4 shadow-2xl">
          <div class="flex items-center justify-between">
            <h4 class="text-sm font-bold text-zinc-900 flex items-center gap-2">
              <span class="w-2 h-2 rounded-full bg-red-600"></span>
              <span>Bảng tính trả góp 0% lãi suất</span>
            </h4>
            <button @click="showInstallmentModal = false" class="text-zinc-400 hover:text-zinc-600 cursor-pointer">✕</button>
          </div>

          <div class="space-y-3 text-xs">
            <div class="p-3 bg-zinc-50 rounded-2xl border border-zinc-200 flex justify-between items-center">
              <span class="text-zinc-500">Giá trị sản phẩm:</span>
              <span class="font-extrabold text-sm text-red-600">{{ formatPrice(currentPrice) }}</span>
            </div>

            <div class="grid grid-cols-3 gap-2 text-center">
              <div class="p-3 rounded-2xl border border-red-600 bg-red-50/40">
                <div class="text-[11px] text-zinc-500 font-medium">Kỳ hạn 6 tháng</div>
                <div class="text-xs font-black text-zinc-900 mt-1">{{ formatPrice(currentPrice / 6) }}/tháng</div>
                <div class="text-[10px] text-red-600 font-bold mt-0.5">Lãi suất 0%</div>
              </div>
              <div class="p-3 rounded-2xl border border-zinc-200 bg-white">
                <div class="text-[11px] text-zinc-500 font-medium">Kỳ hạn 9 tháng</div>
                <div class="text-xs font-black text-zinc-900 mt-1">{{ formatPrice(currentPrice / 9) }}/tháng</div>
                <div class="text-[10px] text-red-600 font-bold mt-0.5">Lãi suất 0%</div>
              </div>
              <div class="p-3 rounded-2xl border border-zinc-200 bg-white">
                <div class="text-[11px] text-zinc-500 font-medium">Kỳ hạn 12 tháng</div>
                <div class="text-xs font-black text-zinc-900 mt-1">{{ formatPrice(currentPrice / 12) }}/tháng</div>
                <div class="text-[10px] text-red-600 font-bold mt-0.5">Lãi suất 0%</div>
              </div>
            </div>

            <div class="text-[11px] text-zinc-500 leading-normal">
              Hỗ trợ qua các đối tác tài chính SF+, Home Credit hoặc thẻ tín dụng hơn 25 ngân hàng đối tác. Hồ sơ chỉ cần CCCD gắn chip, duyệt nhanh 5 phút.
            </div>
          </div>

          <button 
            @click="showInstallmentModal = false; handleBuyNow()"
            class="w-full py-2.5 rounded-xl bg-zinc-900 hover:bg-zinc-800 text-white font-bold text-xs transition cursor-pointer"
          >
            Lên hồ sơ trả góp ngay
          </button>
        </div>
      </div>
    </Teleport>

    <!-- 4. Compare Modal -->
    <Teleport to="body">
      <div v-if="showCompareModal" class="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
        <div class="bg-white rounded-3xl max-w-2xl w-full p-6 space-y-4 shadow-2xl max-h-[85vh] overflow-y-auto">
          <div class="flex items-center justify-between pb-2 border-b border-zinc-100">
            <h4 class="text-sm font-bold text-zinc-900">So sánh thiết bị cùng phân khúc</h4>
            <button @click="showCompareModal = false" class="text-zinc-400 hover:text-zinc-600 cursor-pointer">✕</button>
          </div>

          <div class="grid grid-cols-2 gap-4 text-xs">
            <div class="p-4 rounded-2xl bg-red-50/30 border border-red-200 space-y-2 text-center">
              <img :src="activeImage || defaultImage" class="w-20 h-20 object-contain mx-auto" />
              <div class="font-bold text-zinc-900">{{ product.name }}</div>
              <div class="font-black text-red-600">{{ formatPrice(currentPrice) }}</div>
              <div class="text-[11px] text-zinc-600 pt-2 border-t border-red-100 space-y-1 text-left">
                <div>• Chip: Snapdragon 8 Gen 3 for Galaxy</div>
                <div>• Màn hình: Dynamic AMOLED 2X 120Hz</div>
                <div>• Camera: 200MP OIS Zoom 100x</div>
                <div>• Pin: 5000 mAh Sạc nhanh 45W</div>
              </div>
            </div>

            <div class="p-4 rounded-2xl bg-zinc-50 border border-zinc-200 space-y-2 text-center">
              <img src="https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=300" class="w-20 h-20 object-contain mx-auto" />
              <div class="font-bold text-zinc-900">iPhone 16 Pro Max 256GB</div>
              <div class="font-black text-zinc-900">34.990.000₫</div>
              <div class="text-[11px] text-zinc-600 pt-2 border-t border-zinc-200 space-y-1 text-left">
                <div>• Chip: Apple A18 Pro 3nm</div>
                <div>• Màn hình: Super Retina XDR 120Hz</div>
                <div>• Camera: 48MP Fusion 5x Tele</div>
                <div>• Pin: 4685 mAh Sạc MagSafe 25W</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { toast } from 'vue-sonner'
import RecommendationCarousel from '@/components/RecommendationCarousel.vue'
import HardwareSpecsTable from '@/components/HardwareSpecsTable.vue'
import { catalogApi } from '@/api/catalogApi'
import { recommendationApi } from '@/api/recommendationApi'
import { behaviorApi } from '@/api/behaviorApi'
import { useCartStore } from '@/stores/cart'
import { useWishlistStore } from '@/stores/wishlist'
import { useAuthStore } from '@/stores/auth'
import type { Product, ProductVariant, ProductReview, ProductReviewSummary } from '@/types'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const wishlistStore = useWishlistStore()
const authStore = useAuthStore()

type EnrichedProduct = Product & { reason?: string; aiScore?: number }

// Core State
const product = ref<Product | null>(null)
const selectedVariant = ref<ProductVariant | null>(null)
const activeImage = ref<string>('')
const activeImageIndex = ref<number>(0)
const quantity = ref<number>(1)
const loading = ref<boolean>(true)
const addingToCart = ref<boolean>(false)
const toastMessage = ref<string>('')

// UI Interactive Sections
const activeSection = ref<string>('overview')
const isExpandedStory = ref<boolean>(false)
const showMoreOffers = ref<boolean>(false)
const showReviewForm = ref<boolean>(false)
const showStickySubNav = ref<boolean>(false)
const showStickyBottomBar = ref<boolean>(false)
const selectedCity = ref<string>('HN')
const feedbackState = ref<'yes' | 'no' | null>(null)

// Dialog Modals
const showVideoModal = ref<boolean>(false)
const showTradeInModal = ref<boolean>(false)
const showInstallmentModal = ref<boolean>(false)
const showCompareModal = ref<boolean>(false)

// Colors
const colorOptions = [
  { name: 'Xanh dương', hex: '#60a5fa' },
  { name: 'Titan Đen', hex: '#18181b' },
  { name: 'Titan Bạc', hex: '#e4e4e7' },
  { name: 'Titan Xám', hex: '#71717a' }
]
const selectedColor = ref(colorOptions[1])

// Cross-sell Combos
const comboOffers = ref([
  { id: 1, name: 'Combo Củ sạc siêu nhanh 45W + Cáp sạc C to C', originalPrice: 690000, price: 490000, selected: false },
  { id: 2, name: 'Bao da Smart View bảo vệ chống sốc chính hãng', originalPrice: 1190000, price: 890000, selected: false }
])

// Real-time Countdown Timer (ticking every second)
const timer = ref({ hours: '14', minutes: '37', seconds: '23' })
let timerInterval: any = null

const startCountdown = () => {
  let totalSeconds = 14 * 3600 + 37 * 60 + 23
  timerInterval = setInterval(() => {
    if (totalSeconds > 0) {
      totalSeconds--
      const h = Math.floor(totalSeconds / 3600)
      const m = Math.floor((totalSeconds % 3600) / 60)
      const s = totalSeconds % 60
      timer.value = {
        hours: String(h).padStart(2, '0'),
        minutes: String(m).padStart(2, '0'),
        seconds: String(s).padStart(2, '0')
      }
    }
  }, 1000)
}

// Sub Nav Tabs
const navTabs = [
  { id: 'overview', label: 'Thông tin sản phẩm', icon: '⭐' },
  { id: 'description', label: 'Mô tả chi tiết', icon: '📝' },
  { id: 'specs', label: 'Thông số kỹ thuật', icon: '⚙️' },
  { id: 'reviews', label: 'Đánh giá & Bình luận', icon: '💬' },
  { id: 'similar', label: 'Có thể bạn quan tâm', icon: '❤️' },
  { id: 'faq', label: 'Câu hỏi thường gặp', icon: '❓' }
]

// Similar Products
const similarProducts = ref<EnrichedProduct[]>([])
const loadingSimilar = ref<boolean>(false)

// Reviews
const reviews = ref<ProductReview[]>([])
const reviewSummary = ref<ProductReviewSummary>({
  productId: 0, averageRating: 4.9, totalReviews: 199,
  reviews: { content: [], page: 0, size: 10, totalElements: 0, totalPages: 0, last: true }
})
const newReview = ref<{ rating: number; comment: string }>({ rating: 5, comment: '' })
const submittingReview = ref<boolean>(false)

// FAQs
const faqs = ref([
  {
    q: 'Samsung Galaxy S25 Ultra có khả năng kháng nước và bụi như thế nào?',
    a: 'Thiết bị đạt chuẩn kháng nước và bụi IP68 theo tiêu chuẩn quốc tế, cho phép ngâm dưới nước ngọt ở độ sâu tối đa 1.5 mét trong vòng 30 phút.',
    open: true
  },
  {
    q: 'Những tính năng Galaxy AI nào nổi bật nhất trên thiết bị?',
    a: 'Bao gồm Circle to Search (khoanh tròn để tìm kiếm), Dịch trực tiếp cuộc gọi hai chiều (Live Translate), Trợ lý Note tự động tóm tắt văn bản và Chỉnh sửa ảnh chuyên nghiệp bằng AI tạo sinh.',
    open: false
  },
  {
    q: 'Camera 200MP chụp đêm và quay phim có thực sự ấn tượng không?',
    a: 'Cảm biến 200MP kết hợp công nghệ Nightography và bộ xử lý ProVisual Engine giúp hình ảnh sắc nét vượt trội, độ sáng cao và giảm nhiễu hạt tối đa trong môi trường thiếu sáng.',
    open: false
  },
  {
    q: 'Thời lượng pin và công nghệ sạc của máy hoạt động ra sao?',
    a: 'Dung lượng pin 5.000 mAh cùng tiến trình chip xử lý tiết kiệm điện năng cho phép xem video liên tục lên đến 30 giờ, kết hợp sạc siêu nhanh 45W nạp 65% pin trong khoảng 30 phút.',
    open: false
  },
  {
    q: 'Thủ tục mua trả góp 0% tại TechStore cần những giấy tờ gì?',
    a: 'Bạn chỉ cần CCCD gắn chip từ 18 tuổi trở lên. Quá trình xét duyệt hồ sơ online hoàn toàn tự động chỉ trong vòng 5 phút mà không cần thế chấp hay chứng minh thu nhập.',
    open: false
  }
])

const defaultImage = 'https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=800&auto=format&fit=crop&q=80'

// Computed Properties
const isFav = computed(() => product.value ? wishlistStore.isWishlisted(product.value.id) : false)
const currentStock = computed(() => selectedVariant.value?.stock ?? 15)

const currentPrice = computed(() => {
  return selectedVariant.value?.price || product.value?.minPrice || 23990000
})

const currentOriginalPrice = computed(() => {
  if (selectedVariant.value?.originalPrice) return selectedVariant.value.originalPrice
  return Math.round(currentPrice.value * 1.28 / 10000) * 10000
})

const discountPercent = computed(() => {
  if (!currentOriginalPrice.value || currentOriginalPrice.value <= currentPrice.value) return 20
  return Math.round(((currentOriginalPrice.value - currentPrice.value) / currentOriginalPrice.value) * 100)
})

const loyaltyPoints = computed(() => {
  return Math.round(currentPrice.value / 4000).toLocaleString('vi-VN')
})

const galleryImages = computed(() => {
  if (product.value?.images && product.value.images.length > 0) {
    return product.value.images.map(img => img.url)
  }
  return [
    activeImage.value || defaultImage,
    'https://images.unsplash.com/photo-1580910051074-3eb694886505?w=800&auto=format&fit=crop&q=80',
    'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&auto=format&fit=crop&q=80',
    'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=800&auto=format&fit=crop&q=80'
  ]
})

// Highlight key specs extracted from attributes
const highlightSpecs = computed(() => {
  const attrs = product.value?.attributes || []
  const findVal = (query: string) => attrs.find(a => a.name.toLowerCase().includes(query))?.value

  return [
    {
      title: 'Kích thước màn hình',
      value: findVal('màn hình') || '6.8 inch Dynamic AMOLED 2X 120Hz',
      iconSvg: '<rect x="5" y="2" width="14" height="20" rx="2"/><line x1="12" y1="18" x2="12" y2="18"/>'
    },
    {
      title: 'Hệ thống Camera',
      value: findVal('camera') || '200.0 MP + 50.0 MP + 12.0 MP',
      iconSvg: '<path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/><circle cx="12" cy="13" r="4"/>'
    },
    {
      title: 'Vi xử lý & RAM',
      value: (findVal('cpu') || 'Snapdragon 8 Gen 3') + ' | ' + (findVal('ram') || '12GB'),
      iconSvg: '<rect x="4" y="4" width="16" height="16" rx="2"/><rect x="9" y="9" width="6" height="6"/><path d="M9 1v3M15 1v3M9 20v3M15 20v3M20 9h3M20 14h3M1 9h3M1 14h3"/>'
    },
    {
      title: 'Pin & Công nghệ sạc',
      value: findVal('pin') || '5000 mAh, Sạc siêu nhanh 45W',
      iconSvg: '<rect x="2" y="7" width="16" height="10" rx="2"/><line x1="22" y1="11" x2="22" y2="13"/>'
    }
  ]
})

// Star Breakdown
const starBreakdown = computed(() => {
  const total = reviewSummary.value.totalReviews || 199
  const count5 = Math.round(total * 0.94)
  const count4 = Math.round(total * 0.04)
  const count3 = Math.round(total * 0.015)
  const count2 = 1
  const count1 = 0
  return [
    { stars: 5, count: count5, percent: 94 },
    { stars: 4, count: count4, percent: 4 },
    { stars: 3, count: count3, percent: 2 },
    { stars: 2, count: count2, percent: 0 },
    { stars: 1, count: count1, percent: 0 }
  ]
})

// Display Reviews with fallback mock reviews if database review list is empty
const displayReviews = computed(() => {
  if (reviews.value && reviews.value.length > 0) {
    return reviews.value
  }
  return [
    {
      id: 901,
      userId: 1,
      userFullName: 'Lê Văn Đạt',
      productId: product.value?.id || 1,
      rating: 5,
      comment: 'Máy cầm rất chắc tay, khung titan nhẹ hơn hẳn đời trước. Màn hình phẳng dán cường lực rất dễ, độ sáng ngoài trời tuyệt vời!',
      createdAt: 'Hôm qua'
    },
    {
      id: 902,
      userId: 2,
      userFullName: 'Cao Thị Mỹ Duyên',
      productId: product.value?.id || 1,
      rating: 5,
      comment: 'Camera zoom 5x chụp sân khấu concert siêu nét. Tính năng khoanh tròn tìm kiếm bằng Galaxy AI nhanh tiện không tưởng.',
      createdAt: '3 ngày trước'
    },
    {
      id: 903,
      userId: 3,
      userFullName: 'Hoàng Minh Tuấn',
      productId: product.value?.id || 1,
      rating: 5,
      comment: 'Giao hàng chỉ mất 1 tiếng từ siêu thị gần nhà, nhân viên hỗ trợ dán màn hình và chuyển dữ liệu từ máy cũ rất nhiệt tình.',
      createdAt: '5 ngày trước'
    }
  ]
})

const formatPrice = (value?: number | null): string => {
  if (!value || isNaN(value)) return 'Liên hệ'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}

// Methods & Actions
const selectImage = (idx: number) => {
  activeImageIndex.value = idx
  activeImage.value = galleryImages.value[idx]
}

const prevImage = () => {
  const len = galleryImages.value.length
  activeImageIndex.value = (activeImageIndex.value - 1 + len) % len
  activeImage.value = galleryImages.value[activeImageIndex.value]
}

const nextImage = () => {
  const len = galleryImages.value.length
  activeImageIndex.value = (activeImageIndex.value + 1) % len
  activeImage.value = galleryImages.value[activeImageIndex.value]
}

const scrollToSection = (sectionId: string) => {
  activeSection.value = sectionId
  const el = document.getElementById(sectionId)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth' })
  }
}

const handleShare = async () => {
  try {
    if (navigator.clipboard) {
      await navigator.clipboard.writeText(window.location.href)
      toast.success('Đã sao chép liên kết sản phẩm vào bộ nhớ tạm!')
    } else {
      toast.info('Liên kết: ' + window.location.href)
    }
  } catch {
    toast.info('Đã chia sẻ sản phẩm')
  }
}

const handleToggleWishlist = async () => {
  if (product.value) {
    await wishlistStore.toggleWishlist(product.value)
    if (isFav.value) {
      toast.success('Đã thêm sản phẩm vào danh sách yêu thích')
    } else {
      toast.info('Đã xóa khỏi danh sách yêu thích')
    }
  }
}

const handleAddToCart = async () => {
  if (!product.value) return
  const variantId = selectedVariant.value?.id || (product.value.variants?.[0]?.id ?? 201)
  addingToCart.value = true
  try {
    await cartStore.addToCart(variantId, quantity.value, product.value.id)
    toastMessage.value = `Đã thêm ${quantity.value} sản phẩm vào giỏ hàng thành công!`
    toast.success(`Đã thêm ${quantity.value} sản phẩm vào giỏ hàng!`)
    setTimeout(() => { toastMessage.value = '' }, 3000)
  } catch (err: any) {
    toastMessage.value = err.message
    toast.error(err.message || 'Không thể thêm sản phẩm')
  } finally {
    addingToCart.value = false
  }
}

const handleBuyNow = async () => {
  await handleAddToCart()
  router.push('/cart')
}

const openVideoModal = () => {
  showVideoModal.value = true
}

const openUnboxModal = () => {
  toast.info('Đang mở bộ sưu tập phụ kiện trong hộp: Cáp sạc C-to-C, Que chọc sim, Sách HDSD')
}

const open360Modal = () => {
  toast.info('Chế độ xoay 360° thực tế ảo đang sẵn sàng')
}

const openInstallmentModal = () => {
  showInstallmentModal.value = true
}

const handleFeedback = (isPositive: boolean) => {
  feedbackState.value = isPositive ? 'yes' : 'no'
  toast.success('Cảm ơn phản hồi quý báu của bạn!')
}

const handleScroll = () => {
  const scrollPos = window.scrollY || document.documentElement.scrollTop
  showStickySubNav.value = scrollPos > 350
  showStickyBottomBar.value = scrollPos > 550

  const sections = ['faq', 'similar', 'reviews', 'specs', 'description', 'overview']
  for (const s of sections) {
    const el = document.getElementById(s)
    if (el) {
      const top = el.getBoundingClientRect().top
      if (top <= 160) {
        activeSection.value = s
        break
      }
    }
  }
}

const loadProductData = async (id: number | string) => {
  loading.value = true
  try {
    const res = await catalogApi.getProductById(Number(id))
    if (res.success && res.data) {
      product.value = res.data
      if (product.value.variants && product.value.variants.length > 0) {
        selectedVariant.value = product.value.variants[0]
      }
      if (product.value.images && product.value.images.length > 0) {
        activeImage.value = product.value.images[0].url
        activeImageIndex.value = 0
      }

      behaviorApi.track('VIEW', product.value.id)
      loadSimilarProducts(product.value.id)
      loadReviews(product.value.id)
    }
  } catch (err) {
    console.error('Không thể tải chi tiết sản phẩm:', err)
  } finally {
    loading.value = false
  }
}

let similarRequestId = 0
const loadSimilarProducts = async (productId: number) => {
  const requestId = ++similarRequestId
  loadingSimilar.value = true
  similarProducts.value = []
  try {
    const res = await recommendationApi.getSimilar(productId, 4)
    if (requestId !== similarRequestId || product.value?.id !== productId) return
    similarProducts.value = res.data.recommendations.map(item => ({
      ...item.product, reason: item.reason, aiScore: item.score ?? undefined
    }))
  } catch (error) {
    console.error('Lỗi tải sản phẩm tương tự:', error)
  } finally {
    if (requestId === similarRequestId) loadingSimilar.value = false
  }
}

const loadReviews = async (productId: number) => {
  try {
    const res = await catalogApi.getReviewSummary(productId)
    if (res.success && res.data) {
      reviewSummary.value = {
        ...res.data,
        averageRating: res.data.averageRating || 4.9,
        totalReviews: res.data.totalReviews || 199
      }
      reviews.value = res.data.reviews?.content || []
    }
  } catch (err) {
    console.error('Lỗi tải đánh giá:', err)
  }
}

const handleSubmitReview = async () => {
  if (!authStore.isAuthenticated) {
    authStore.openLoginModal()
    return
  }
  if (!product.value) return

  submittingReview.value = true
  try {
    await catalogApi.submitReview({
      productId: product.value.id,
      rating: newReview.value.rating,
      comment: newReview.value.comment
    })
    behaviorApi.track('RATING', product.value.id, `stars=${newReview.value.rating}`)
    toast.success('Gửi đánh giá thành công! Cảm ơn nhận xét của bạn.')
    newReview.value.comment = ''
    showReviewForm.value = false
    loadReviews(product.value.id)
  } catch (err: any) {
    alert(err.message)
  } finally {
    submittingReview.value = false
  }
}

watch(() => route.params.id, (newId) => {
  if (newId) loadProductData(newId as string)
})

onMounted(() => {
  startCountdown()
  window.addEventListener('scroll', handleScroll, { passive: true })
  if (route.params.id) {
    loadProductData(route.params.id as string)
  }
})

onBeforeUnmount(() => {
  if (timerInterval) clearInterval(timerInterval)
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(6px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes slideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}

.animate-fade-in {
  animation: fadeIn 0.25s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

.animate-slide-up {
  animation: slideUp 0.3s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.25s cubic-bezier(0.16, 1, 0.3, 1), transform 0.25s cubic-bezier(0.16, 1, 0.3, 1);
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

.scrollbar-none::-webkit-scrollbar {
  display: none;
}
.scrollbar-none {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
