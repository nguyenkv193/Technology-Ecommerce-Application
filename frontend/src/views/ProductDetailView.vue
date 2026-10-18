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
        <router-link v-if="product.category?.name || product.categoryName" to="/products" class="hover:text-red-600 transition">
          {{ product.category?.name || product.categoryName }}
        </router-link>
        <span v-if="product.category?.name || product.categoryName" class="text-zinc-300">/</span>
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
              Mã SP: {{ product.id }}
            </span>

            <a href="#reviews" class="flex items-center gap-1 hover:text-red-600 transition">
              <span class="flex text-amber-400">
                <svg v-for="s in 5" :key="s" class="w-3.5 h-3.5 fill-current" :class="hasReviews && s <= Math.round(reviewSummary.averageRating) ? 'text-amber-400' : 'text-zinc-300'" viewBox="0 0 24 24"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>
              </span>
              <span class="font-bold text-zinc-900 ml-1">{{ hasReviews ? reviewSummary.averageRating.toFixed(1) : 'Chưa có đánh giá' }}</span>
              <span class="text-zinc-400 underline decoration-zinc-300">({{ reviewSummary.totalReviews }} đánh giá)</span>
            </a>

            <button 
              @click="scrollToSection('specs')" 
              class="flex items-center gap-1 hover:text-red-600 transition cursor-pointer text-zinc-600"
            >
              <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
              <span>Thông số</span>
            </button>

            <button 
              @click="openCompareModal"
              class="flex items-center gap-1 hover:text-red-600 transition cursor-pointer text-zinc-600"
            >
              <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="18" cy="18" r="3"/><circle cx="6" cy="6" r="3"/><path d="M13 6h3a2 2 0 0 1 2 2v7"/><path d="M11 18H8a2 2 0 0 1-2-2V9"/></svg>
              <span>So sánh</span>
            </button>
          </div>
        </div>

        <div v-if="storefront.storePolicy.officialProductText || selectedCityAvailability?.deliveryEstimate" class="hidden md:flex items-center gap-3">
          <div v-if="storefront.storePolicy.officialProductText" class="flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-emerald-50 text-emerald-700 text-xs font-semibold border border-emerald-200">
            <span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
            <span>{{ storefront.storePolicy.officialProductText }}</span>
          </div>
          <div v-if="selectedCityAvailability?.deliveryEstimate" class="flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-amber-50 text-amber-700 text-xs font-semibold border border-amber-200">
            <svg class="w-3.5 h-3.5 text-amber-600 fill-current shrink-0" viewBox="0 0 24 24"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>
            <span>{{ selectedCityAvailability.deliveryEstimate }}</span>
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
                <span class="mr-1.5 inline-flex items-center" v-html="nav.iconSvg"></span>
                <span>{{ nav.label }}</span>
              </button>
            </div>

            <div v-if="storefront.storePolicy.hotline" class="hidden lg:flex items-center gap-3 shrink-0 pl-3 border-l border-zinc-200 text-xs">
              <span class="text-zinc-400">Hotline:</span>
              <a :href="`tel:${storefront.storePolicy.hotline}`" class="font-bold text-red-600 hover:underline">{{ storefront.storePolicy.hotline }}</a>
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
        <div 
          @touchstart="handleTouchStart"
          @touchend="handleTouchEnd"
          class="bg-white border border-zinc-200/90 rounded-3xl p-6 sm:p-8 aspect-[4/3] sm:aspect-square flex items-center justify-center relative overflow-hidden shadow-xs group select-none"
        >
          <!-- Top Left Tag Badges -->
          <div class="absolute top-4 left-4 z-10 flex flex-col gap-2">
            <span v-if="activeInstallmentPlans.length" class="inline-flex items-center gap-1 text-[11px] font-bold uppercase tracking-wider px-2.5 py-1 rounded-lg bg-red-600 text-white shadow-xs">
              <svg class="w-3 h-3" viewBox="0 0 24 24" fill="currentColor"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/></svg>
              <span>Trả góp</span>
            </span>
            <span v-if="primaryPromotion" class="inline-flex items-center gap-1 text-[10px] font-semibold px-2 py-0.5 rounded-md bg-amber-500 text-white shadow-xs">
              <svg class="w-3 h-3 fill-current shrink-0" viewBox="0 0 24 24"><path d="M12 2c-.5 3-3 4-3 7a5 5 0 0 0 10 0c0-3-2.5-4-3-7-1 2-2 3-4 0Z"/></svg>
              <span>{{ primaryPromotion.title }}</span>
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
          <img v-if="activeImage"
            :src="activeImage"
            :alt="product.name"
            class="object-contain max-h-[85%] max-w-[85%] transition-transform duration-500 group-hover:scale-105 select-none"
          />
          <div v-else class="text-xs text-zinc-400">Chưa có hình ảnh sản phẩm</div>

          <!-- Gallery Floating Navigation Arrows -->
          <button 
            v-if="galleryImages.length > 1"
            @click="prevImage"
            class="absolute left-3 top-1/2 -translate-y-1/2 w-9 h-9 rounded-full bg-white/90 border border-zinc-200 text-zinc-700 hover:bg-white hover:text-red-600 shadow-md flex items-center justify-center transition opacity-80 sm:opacity-0 sm:group-hover:opacity-100 cursor-pointer z-10"
          >
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m15 18-6-6 6-6"/></svg>
          </button>
          <button 
            v-if="galleryImages.length > 1"
            @click="nextImage"
            class="absolute right-3 top-1/2 -translate-y-1/2 w-9 h-9 rounded-full bg-white/90 border border-zinc-200 text-zinc-700 hover:bg-white hover:text-red-600 shadow-md flex items-center justify-center transition opacity-80 sm:opacity-0 sm:group-hover:opacity-100 cursor-pointer z-10"
          >
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m9 18 6-6-6-6"/></svg>
          </button>

          <!-- Bottom Image Index Badge -->
          <div class="absolute bottom-4 right-4 px-2.5 py-1 rounded-full bg-zinc-900/70 backdrop-blur-xs text-white text-[11px] font-mono">
            {{ galleryImages.length ? activeImageIndex + 1 : 0 }} / {{ galleryImages.length }}
          </div>
        </div>

        <!-- FPT Interactive Media Thumbnails & Function Tabs -->
        <div class="flex items-center gap-2 overflow-x-auto pb-2 scrollbar-none">
          <!-- Video Review Tab -->
          <button v-if="storefront.media.reviewVideoUrl"
            @click="openVideoModal"
            class="flex flex-col items-center justify-center w-16 h-16 sm:w-18 sm:h-18 rounded-2xl border bg-white text-zinc-700 hover:border-red-500 hover:text-red-600 shrink-0 transition p-2 cursor-pointer group/btn"
          >
            <div class="w-6 h-6 rounded-full bg-red-100 text-red-600 group-hover/btn:bg-red-600 group-hover/btn:text-white flex items-center justify-center transition">
              <svg class="w-3.5 h-3.5 ml-0.5 fill-current" viewBox="0 0 24 24"><polygon points="5 3 19 12 5 21 5 3"/></svg>
            </div>
            <span class="text-[10px] font-bold mt-1">Video</span>
          </button>

          <!-- Highlight Feature Icon Tab -->
          <button v-if="storefront.storySections.length > 0 || product.description"
            @click="scrollToSection('description')"
            class="flex flex-col items-center justify-center w-16 h-16 sm:w-18 sm:h-18 rounded-2xl border bg-white text-zinc-700 hover:border-amber-500 hover:text-amber-600 shrink-0 transition p-2 cursor-pointer"
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
            class="w-16 h-16 sm:w-18 sm:h-18 rounded-2xl bg-white border p-1.5 shrink-0 transition relative cursor-pointer overflow-hidden flex items-center justify-center"
            :class="activeImageIndex === idx ? 'border-red-600 ring-2 ring-red-600/30' : 'border-zinc-200 hover:border-zinc-300'"
          >
            <img :src="img" class="object-contain w-full h-full pointer-events-none select-none" :alt="`Góc ảnh ${idx + 1}`" />
          </button>

          <!-- Unbox Tab -->
          <button v-if="storefront.media.unboxingDescription || storefront.media.unboxingVideoUrl"
            @click="openUnboxModal"
            class="flex flex-col items-center justify-center w-16 h-16 sm:w-18 sm:h-18 rounded-2xl border bg-white text-zinc-700 hover:border-zinc-400 shrink-0 transition p-2 cursor-pointer"
          >
            <div class="w-6 h-6 rounded-full bg-zinc-100 text-zinc-600 flex items-center justify-center">
              <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16Z"/><path d="m3.3 7 8.7 5 8.7-5"/><path d="M12 22V12"/></svg>
            </div>
            <span class="text-[10px] font-bold mt-1">Mở hộp</span>
          </button>

          <!-- 360 View Tab -->
          <button v-if="storefront.media.view360Url"
            @click="open360Modal"
            class="flex flex-col items-center justify-center w-16 h-16 sm:w-18 sm:h-18 rounded-2xl border bg-white text-zinc-700 hover:border-zinc-400 shrink-0 transition p-2 cursor-pointer"
          >
            <div class="w-6 h-6 rounded-full bg-zinc-100 text-zinc-600 flex items-center justify-center">
              <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21.5 2v6h-6M21.34 15.57a10 10 0 1 1-.57-8.38l5.67-5.67"/></svg>
            </div>
            <span class="text-[10px] font-bold mt-1">Xoay 360°</span>
          </button>
        </div>

        <!-- TechStore Policy & Gold Guarantees Bar (FPT Style) -->
        <div class="grid grid-cols-2 sm:grid-cols-4 gap-3 p-4 rounded-2xl bg-zinc-50 border border-zinc-200/80 text-xs">
          <div v-for="(policy, index) in policyTexts" :key="`${index}-${policy}`" class="flex items-center gap-2.5">
            <div class="w-8 h-8 rounded-xl bg-white border border-zinc-200 flex items-center justify-center text-red-600 shrink-0 shadow-2xs">
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><path d="m9 12 2 2 4-4"/></svg>
            </div>
            <span class="text-zinc-700 font-medium leading-tight">{{ policy }}</span>
          </div>
          <div v-if="policyTexts.length === 0" class="col-span-full text-center text-zinc-400">
            Thông tin chính sách chưa được cập nhật.
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
              <div class="text-xs font-bold text-zinc-900 truncate" :title="spec.value || 'Chưa có dữ liệu'">{{ spec.value || 'Chưa có dữ liệu' }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Section: Mô tả - Thông tin hay (Rich Product Story) -->
        <div id="description" class="bg-white rounded-3xl border border-zinc-200/80 p-6 sm:p-8 space-y-5 shadow-xs relative scroll-mt-24">
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
            <p v-if="product.description" class="font-medium text-zinc-800 text-sm sm:text-base">
              {{ product.description }}
            </p>

            <article v-for="(section, index) in storefront.storySections" :key="`${section.heading || 'story'}-${index}`" class="space-y-3">
              <h4 v-if="section.heading" class="text-sm font-bold text-zinc-900 mt-4">{{ section.heading }}</h4>
              <p v-if="section.body">{{ section.body }}</p>
              <div v-if="section.imageUrl" class="rounded-2xl overflow-hidden border border-zinc-200 my-3">
                <img :src="section.imageUrl" class="w-full h-56 sm:h-72 object-cover" :alt="section.heading || product.name" />
                <div v-if="section.imageCaption" class="p-2.5 bg-zinc-50 text-[11px] text-zinc-500 text-center border-t border-zinc-200">
                  {{ section.imageCaption }}
                </div>
              </div>
            </article>

            <div v-if="!product.description && storefront.storySections.length === 0" class="text-xs text-zinc-400">
              Nội dung giới thiệu sản phẩm chưa được cập nhật.
            </div>
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
        <div class="rounded-3xl border p-5 sm:p-6 shadow-sm space-y-4" :class="primaryPromotion ? 'border-red-200/90 bg-gradient-to-br from-red-50/50 via-white to-red-50/30' : 'border-zinc-200/90 bg-white'">
          
          <!-- Flash Sale Header with Animated Fire & Live Countdown -->
          <div class="flex items-center justify-between pb-3 border-b border-red-100">
            <div class="flex items-center gap-2">
              <span class="flex h-6 px-2.5 rounded-lg text-white font-extrabold text-[11px] uppercase items-center gap-1 shadow-xs" :class="primaryPromotion ? 'bg-red-600 animate-pulse' : 'bg-zinc-700'">
                <svg v-if="primaryPromotion" class="w-3.5 h-3.5 fill-current shrink-0" viewBox="0 0 24 24"><path d="M12 2c-.5 3-3 4-3 7a5 5 0 0 0 10 0c0-3-2.5-4-3-7-1 2-2 3-4 0Z"/></svg>
                <span>{{ primaryPromotion?.title || 'Giá bán' }}</span>
              </span>
            </div>

            <!-- Countdown Timer -->
            <div v-if="timer" class="flex items-center gap-1 text-xs">
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
              <span v-if="currentOriginalPrice != null && currentOriginalPrice > (currentPrice ?? 0)" class="text-sm sm:text-base text-zinc-400 line-through">
                {{ formatPrice(currentOriginalPrice) }}
              </span>
              <span v-if="discountPercent != null" class="text-xs font-bold text-red-700 bg-red-100/80 px-2 py-0.5 rounded-md border border-red-200">
                -{{ discountPercent }}%
              </span>
            </div>

            <!-- Loyalty F-Points Earn Badge -->
            <div v-if="loyaltyPoints != null" class="pt-1 flex items-center gap-2 text-xs">
              <div class="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full bg-amber-50 text-amber-800 border border-amber-200 text-[11px] font-semibold">
                <svg class="w-3.5 h-3.5 text-amber-600 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="9"/><path d="M14.8 9A2 2 0 0 0 13 8h-2a2 2 0 0 0 0 4h2a2 2 0 0 1 0 4h-2a2 2 0 0 1-1.8-1"/><path d="M12 6v2m0 8v2"/></svg>
                <span>Tích lũy +{{ loyaltyPoints.toLocaleString('vi-VN') }} điểm thưởng TechStore</span>
              </div>
            </div>

            <!-- Sold quota progress bar -->
            <div v-if="primaryPromotion?.quota != null && primaryPromotion.quota > 0 && primaryPromotion.claimed != null" class="pt-2 space-y-1">
              <div class="flex justify-between text-[11px] text-zinc-500 font-medium">
                <span>Đã dùng {{ primaryPromotion.claimed }}/{{ primaryPromotion.quota }} suất</span>
                <span class="text-red-600 font-semibold">{{ primaryPromotion.quota - primaryPromotion.claimed <= 5 ? 'Sắp hết ưu đãi' : 'Ưu đãi có giới hạn' }}</span>
              </div>
              <div class="h-2 w-full bg-zinc-200 rounded-full overflow-hidden">
                <div class="h-full bg-gradient-to-r from-red-500 to-amber-500 rounded-full" :style="{ width: `${Math.min(100, Math.round(primaryPromotion.claimed * 100 / primaryPromotion.quota))}%` }"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- Thu Cũ Đổi Mới Banner (Trade-In Offer) -->
        <div v-if="activeTradeInOffers.length" class="p-3.5 rounded-2xl bg-gradient-to-r from-blue-50/80 to-indigo-50/80 border border-blue-200/80 flex items-center justify-between shadow-2xs">
          <div class="flex items-center gap-2.5">
            <div class="w-8 h-8 rounded-xl bg-blue-600 text-white flex items-center justify-center shrink-0">
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/></svg>
            </div>
            <div>
              <div class="text-xs font-bold text-zinc-900">Thu cũ đổi mới</div>
              <div class="text-[11px] text-zinc-500">
                {{ activeTradeInOffers[0].terms || activeTradeInOffers[0].condition || activeTradeInOffers[0].deviceModel }}
                <template v-if="!activeTradeInOffers[0].terms && !activeTradeInOffers[0].condition && activeTradeInOffers[0].maximumSubsidy != null"> · Trợ giá đến {{ formatPrice(activeTradeInOffers[0].maximumSubsidy) }}</template>
              </div>
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
        <div v-if="availableVariants.length > 0" class="space-y-2.5">
          <label class="block text-xs font-bold text-zinc-800 uppercase tracking-wider flex items-center justify-between">
            <span>Phiên bản cấu hình / Bộ nhớ:</span>
            <span class="text-zinc-400 font-normal normal-case">Chọn phiên bản phù hợp</span>
          </label>
          <div class="grid grid-cols-2 sm:grid-cols-3 gap-2">
            <button 
              v-for="v in availableVariants"
              :key="v.id"
              @click="selectVariant(v)"
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
        <div v-else-if="product.variants?.length" class="text-xs text-zinc-500">
          Hiện chưa có phiên bản đang kinh doanh.
        </div>

        <!-- Color Selector (Màu sắc sản phẩm) -->
        <div v-if="colorOptions.length" class="space-y-2.5">
          <label class="block text-xs font-bold text-zinc-800 uppercase tracking-wider flex items-center justify-between">
            <span>Màu sắc:</span>
            <span class="text-zinc-600 font-semibold normal-case">{{ selectedColorName || 'Chưa có thông tin màu' }}</span>
          </label>
          <div class="grid grid-cols-2 sm:grid-cols-4 gap-2">
            <button 
              v-for="c in colorOptions" 
              :key="c.name"
              @click="selectColor(c.name)"
              class="p-2.5 rounded-2xl border transition flex items-center gap-2 cursor-pointer relative"
              :class="selectedColorName === c.name ? 'border-red-600 bg-red-50/30 ring-1 ring-red-600' : 'border-zinc-200 bg-white hover:border-zinc-300'"
            >
              <span v-if="c.hex" class="w-4 h-4 rounded-full shrink-0 border border-black/10" :style="{ backgroundColor: c.hex }"></span>
              <span class="text-xs font-medium text-zinc-800 truncate">{{ c.name }}</span>

              <div 
                v-if="selectedColorName === c.name"
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
            <select v-if="activeCityAvailability.length"
              v-model="selectedCity"
              class="bg-white border border-zinc-200 rounded-lg px-2 py-1 text-xs font-semibold text-zinc-800 focus:outline-none"
            >
              <option v-for="city in activeCityAvailability" :key="city.cityCode" :value="city.cityCode">
                {{ city.cityName }}<template v-if="city.storeCount != null"> ({{ city.storeCount }} siêu thị)</template>
              </option>
            </select>
            <span v-else class="text-[11px] text-zinc-400">Chưa có dữ liệu tồn kho theo siêu thị</span>
          </div>

          <div class="flex items-center gap-2 pt-1">
            <span v-if="selectedCityAvailability?.stockCount != null && selectedCityAvailability.stockCount > 0" class="inline-flex items-center gap-1.5 text-emerald-700 font-bold bg-emerald-50 px-2.5 py-1 rounded-lg border border-emerald-200">
              <span class="w-2 h-2 rounded-full bg-emerald-600 animate-ping"></span>
              <span v-if="selectedCityAvailability.storeCount != null">Có hàng tại {{ selectedCityAvailability.storeCount }} siêu thị ({{ selectedCityAvailability.stockCount }} sản phẩm)</span>
              <span v-else>Còn {{ selectedCityAvailability.stockCount }} sản phẩm trong khu vực</span>
            </span>
            <span v-else-if="selectedCityAvailability" class="inline-flex items-center gap-1.5 text-zinc-500 font-medium bg-zinc-100 px-2.5 py-1 rounded-lg border border-zinc-200">
              <span>{{ selectedCityAvailability.stockCount === 0 ? 'Tạm hết hàng tại khu vực này' : selectedCityAvailability.deliveryEstimate || 'Chưa có thông tin tồn kho khu vực' }}</span>
            </span>
            <span v-else class="inline-flex items-center gap-1.5 text-zinc-500 font-medium bg-zinc-100 px-2.5 py-1 rounded-lg border border-zinc-200">
              <span>Tồn kho phiên bản đang chọn: {{ currentStock }}</span>
            </span>
          </div>
        </div>

        <!-- Khuyến mãi chính -->
        <div class="rounded-3xl border border-red-200 bg-white overflow-hidden shadow-xs">
          <div class="bg-gradient-to-r from-red-600 to-rose-600 px-4 py-2.5 flex items-center justify-between text-white">
            <div class="flex items-center gap-2">
              <svg class="w-4 h-4 fill-current" viewBox="0 0 24 24"><path d="M20 12v10H4V12"/><path d="M2 7h20v5H2z"/><path d="M12 22V7"/><path d="M12 7H7.5a2.5 2.5 0 0 1 0-5C11 2 12 7 12 7z"/><path d="M12 7h4.5a2.5 2.5 0 0 0 0-5C13 2 12 7 12 7z"/></svg>
              <h4 class="text-xs font-bold uppercase tracking-wider">Khuyến mãi chính</h4>
            </div>
            <span v-if="primaryPromotion?.endsAt" class="text-[11px] font-semibold bg-white/20 px-2 py-0.5 rounded-full">Đến {{ new Date(primaryPromotion.endsAt).toLocaleDateString('vi-VN') }}</span>
          </div>

          <div class="p-4 sm:p-5 space-y-3">
            <ul class="space-y-2.5 text-xs text-zinc-700">
              <li v-for="(offer, index) in displayedPromotions" :key="offer.id || `${offer.title}-${index}`" class="flex items-start gap-2">
                <span class="w-4 h-4 rounded-full bg-red-100 text-red-600 flex items-center justify-center text-[10px] font-bold shrink-0 mt-0.5">{{ index + 1 }}</span>
                <span>
                  <strong>{{ offer.title }}</strong>
                  <span v-if="offer.description"> — {{ offer.description }}</span>
                  <span v-if="offer.discountAmount != null"> (giảm {{ formatPrice(offer.discountAmount) }})</span>
                  <span v-if="offer.terms">. {{ offer.terms }}</span>
                </span>
              </li>
            </ul>

            <div v-if="activePromotions.length === 0" class="text-xs text-zinc-400">Hiện chưa có ưu đãi nào được cập nhật cho sản phẩm này.</div>

            <button v-if="activePromotions.length > 3"
              @click="showMoreOffers = !showMoreOffers"
              class="text-xs font-bold text-red-600 hover:text-red-700 flex items-center gap-1 cursor-pointer pt-1"
            >
              <span>{{ showMoreOffers ? 'Thu gọn ưu đãi' : `Xem thêm ${activePromotions.length - 3} ưu đãi` }}</span>
              <svg class="w-3.5 h-3.5 transition-transform" :class="{ 'rotate-180': showMoreOffers }" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m6 9 6 6 6-6"/></svg>
            </button>
          </div>
        </div>

        <!-- Mua Kèm Với Giá Ưu Đãi -->
        <div v-if="activeBundleOffers.length" class="rounded-3xl border border-zinc-200/90 bg-white p-4 sm:p-5 space-y-3 shadow-xs">
          <div class="flex items-center justify-between">
            <h4 class="text-xs font-bold text-zinc-900 flex items-center gap-1.5">
              <svg class="w-3.5 h-3.5 text-red-600 fill-current shrink-0" viewBox="0 0 24 24"><path d="M12 2c-.5 3-3 4-3 7a5 5 0 0 0 10 0c0-3-2.5-4-3-7-1 2-2 3-4 0Z"/></svg>
              <span>Mua kèm với giá ưu đãi</span>
            </h4>
            <span class="text-[11px] text-zinc-400">Ưu đãi đang áp dụng</span>
          </div>

          <div class="space-y-2">
            <div v-for="(combo, index) in activeBundleOffers" :key="combo.id || `${combo.title}-${index}`" class="flex items-center justify-between gap-3 p-3 rounded-2xl border bg-zinc-50/50 border-zinc-200/80">
              <div>
                <div class="text-xs font-semibold text-zinc-800">{{ combo.title }}</div>
                <div v-if="combo.description" class="text-[11px] text-zinc-500 mt-0.5">{{ combo.description }}</div>
                <div v-if="combo.originalPrice != null" class="text-[11px] text-zinc-400 line-through">{{ formatPrice(combo.originalPrice) }}</div>
              </div>
              <div v-if="combo.price != null" class="text-right shrink-0">
                <div class="text-xs font-black text-red-600">{{ formatPrice(combo.price) }}</div>
                <div v-if="combo.originalPrice != null && combo.originalPrice > combo.price" class="text-[10px] text-emerald-600 font-semibold">Tiết kiệm {{ formatPrice(combo.originalPrice - combo.price) }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Khuyến mãi thanh toán (Payment Partner Bank Cards) -->
        <div v-if="activePaymentOffers.length" class="rounded-3xl border border-zinc-200/90 bg-white p-4 sm:p-5 space-y-3 shadow-xs">
          <div class="flex items-center justify-between">
            <h4 class="text-xs font-bold text-zinc-900">Ưu đãi thanh toán</h4>
            <span class="text-[11px] text-red-600 font-semibold">Ưu đãi đối tác</span>
          </div>

          <div class="grid grid-cols-2 sm:grid-cols-4 gap-2 text-center text-[10px] font-bold">
            <div v-for="(offer, index) in activePaymentOffers" :key="offer.id || `${offer.partnerName || offer.title}-${index}`" class="p-2 rounded-xl bg-zinc-50 border border-zinc-200 text-zinc-700">
              {{ offer.partnerName || offer.title }}
            </div>
          </div>
          <p v-for="(offer, index) in activePaymentOffers" :key="`${offer.id || offer.title}-${index}-terms`" class="text-[11px] text-zinc-500 leading-normal">
            {{ offer.description || offer.terms || offer.title }}
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
                :disabled="quantity <= 1"
                class="w-7 h-7 rounded-lg text-zinc-700 hover:bg-zinc-100 flex items-center justify-center font-bold text-sm cursor-pointer"
              >
                -
              </button>
              <span class="w-10 text-center text-xs font-black text-zinc-900">{{ quantity }}</span>
              <button 
                @click="quantity = Math.min(currentStock, quantity + 1)"
                :disabled="currentStock <= 0 || quantity >= currentStock"
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
              <span v-if="storefront.storePolicy.shippingText" class="text-[11px] font-normal opacity-90">{{ storefront.storePolicy.shippingText }}</span>
            </button>

            <!-- Dark Installment CTA -->
            <button v-if="activeInstallmentPlans.length"
              @click="openInstallmentModal"
              :disabled="currentStock <= 0"
              class="py-3.5 px-4 rounded-2xl bg-zinc-900 hover:bg-zinc-800 text-white font-extrabold shadow-md transition-all active:scale-98 disabled:opacity-40 disabled:pointer-events-none flex flex-col items-center justify-center cursor-pointer"
            >
              <span class="text-sm uppercase tracking-wide">TRẢ GÓP</span>
              <span class="text-[11px] font-normal opacity-90">{{ activeInstallmentPlans[0].providerName }}</span>
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
          <div v-if="storefront.storePolicy.hotline" class="text-center text-xs text-zinc-500 pt-1">
            <span>Tư vấn mua hàng: </span>
            <a :href="`tel:${storefront.storePolicy.hotline}`" class="font-bold text-red-600 hover:underline">{{ storefront.storePolicy.hotline }}</a>
            <span v-if="storefront.storePolicy.hotlineHours"> ({{ storefront.storePolicy.hotlineHours }})</span>
          </div>
        </div>
      </div>
    </div>

    <!-- ================= HARDWARE SPECS SECTION ================= -->
    <div id="specs" class="pt-4 scroll-mt-24">
      <HardwareSpecsTable :attributes="product.attributes" />
    </div>

    <!-- ================= CUSTOMER REVIEWS & FEEDBACK SECTION ================= -->
    <section id="reviews" class="bg-white border border-zinc-200/80 rounded-3xl p-6 sm:p-8 space-y-6 shadow-xs scroll-mt-24">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between pb-4 border-b border-zinc-100 gap-4">
        <div>
          <h3 class="text-lg font-bold text-zinc-900 flex items-center gap-2">
            <span class="w-2.5 h-2.5 rounded-full bg-red-600"></span>
            <span>Đánh giá & Bình luận khách hàng</span>
          </h3>
          <p class="text-xs text-zinc-500 mt-0.5">Các đánh giá đã được gửi cho sản phẩm này</p>
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
            {{ hasReviews ? reviewSummary.averageRating.toFixed(1) : '—' }}<span class="text-xl text-zinc-400">/5</span>
          </div>
          <div class="flex items-center justify-center text-lg my-1.5">
            <span v-for="s in 5" :key="s" :class="hasReviews && s <= Math.round(reviewSummary.averageRating) ? 'text-amber-400' : 'text-zinc-300'">★</span>
          </div>
          <div class="text-xs text-zinc-500 font-medium">
            {{ reviewSummary.totalReviews }} lượt đánh giá
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
            placeholder="Chia sẻ trải nghiệm thực tế của bạn về sản phẩm..."
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
                </div>
                <div class="text-[11px] text-zinc-400">{{ formatReviewDate(rev.createdAt) }}</div>
              </div>
            </div>

            <div class="text-amber-400 text-xs">
              <span v-for="s in rev.rating" :key="s">★</span>
            </div>
          </div>

          <p class="text-xs sm:text-sm text-zinc-700 leading-relaxed pl-10">{{ rev.comment }}</p>

        </div>
      </div>
    </section>

    <!-- ================= SIMILAR PRODUCTS SECTION ================= -->
    <div id="similar" class="pt-4 scroll-mt-24">
      <RecommendationCarousel
        title="Sản Phẩm Tương Tự & Phù Hợp"
        subtitle="Gợi ý sản phẩm từ hệ thống"
        :items="similarProducts"
        :loading="loadingSimilar"
      />
    </div>

    <!-- ================= FREQUENTLY ASKED QUESTIONS (FAQ) ================= -->
    <section id="faq" class="bg-white border border-zinc-200/80 rounded-3xl p-6 sm:p-8 space-y-4 shadow-xs scroll-mt-24">
      <div class="pb-3 border-b border-zinc-100">
        <h3 class="text-lg font-bold text-zinc-900 flex items-center gap-2">
          <span class="w-2.5 h-2.5 rounded-full bg-red-600"></span>
          <span>Câu hỏi thường gặp</span>
        </h3>
        <p class="text-xs text-zinc-500 mt-0.5">Thông tin giải đáp được cập nhật riêng cho sản phẩm này</p>
      </div>

      <div v-if="storefront.faqs.length" class="space-y-2.5">
        <div 
          v-for="(faq, fIdx) in storefront.faqs"
          :key="fIdx"
          class="rounded-2xl border transition overflow-hidden"
          :class="openFaqIndex === fIdx ? 'border-red-200 bg-red-50/20' : 'border-zinc-200/80 bg-white'"
        >
          <button 
            @click="openFaqIndex = openFaqIndex === fIdx ? -1 : fIdx"
            class="w-full p-4 text-left flex items-center justify-between gap-3 text-xs sm:text-sm font-bold text-zinc-800 cursor-pointer"
          >
            <span>{{ faq.question }}</span>
            <svg class="w-4 h-4 shrink-0 transition-transform text-zinc-400" :class="{ 'rotate-180 text-red-600': openFaqIndex === fIdx }" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m6 9 6 6 6-6"/></svg>
          </button>
          <div v-if="openFaqIndex === fIdx" class="px-4 pb-4 text-xs text-zinc-600 leading-relaxed border-t border-zinc-100/60 pt-2.5">
            {{ faq.answer }}
          </div>
        </div>
      </div>
      <div v-else class="text-center py-8 text-xs text-zinc-400">Câu hỏi thường gặp chưa được cập nhật.</div>
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
          class="px-4 py-2 rounded-xl text-xs font-bold transition cursor-pointer flex items-center gap-1.5"
          :class="feedbackState === 'yes' ? 'bg-red-600 text-white' : 'bg-white border border-zinc-300 text-zinc-800 hover:bg-zinc-50'"
        >
          <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"/></svg>
          <span>Hài lòng</span>
        </button>
        <button 
          @click="handleFeedback(false)"
          class="px-4 py-2 rounded-xl text-xs font-bold transition cursor-pointer flex items-center gap-1.5"
          :class="feedbackState === 'no' ? 'bg-zinc-800 text-white' : 'bg-white border border-zinc-300 text-zinc-800 hover:bg-zinc-50'"
        >
          <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M10 15v4a3 3 0 0 0 3 3l4-9V2H5.72a2 2 0 0 0-2 1.7l-1.38 9a2 2 0 0 0 2 2.3zm7-13h3a2 2 0 0 1 2 2v7a2 2 0 0 1-2 2h-3"/></svg>
          <span>Chưa hài lòng</span>
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
          <img v-if="activeImage"
            :src="activeImage"
            :alt="product.name" 
            class="w-10 h-10 object-contain rounded-lg border border-zinc-200 bg-white p-0.5 shrink-0 hidden sm:block"
          />
          <div class="min-w-0">
            <div class="text-xs font-extrabold text-zinc-900 truncate">{{ product.name }}</div>
            <div class="text-[11px] text-zinc-500 truncate">
              Phân loại: <strong class="text-zinc-800">{{ selectedVariant?.name || 'Chưa chọn phiên bản' }}</strong><span v-if="selectedColorName"> | {{ selectedColorName }}</span>
            </div>
          </div>
        </div>

        <!-- Middle: Price and Points -->
        <div class="hidden md:flex items-center gap-3 shrink-0">
          <div v-if="loyaltyPoints != null" class="flex items-center gap-1 px-2 py-0.5 rounded-md bg-amber-50 text-amber-700 text-[10px] font-bold border border-amber-200">
            <span>🪙</span>
            <span>+{{ loyaltyPoints.toLocaleString('vi-VN') }} điểm</span>
          </div>
          <div class="text-right">
            <div class="text-base font-black text-red-600">{{ formatPrice(currentPrice) }}</div>
            <div v-if="currentOriginalPrice != null && currentOriginalPrice > (currentPrice ?? 0)" class="text-[10px] text-zinc-400 line-through">{{ formatPrice(currentOriginalPrice) }}</div>
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

          <button v-if="activeInstallmentPlans.length"
            @click="openInstallmentModal"
            class="px-4 py-2.5 rounded-xl bg-zinc-900 hover:bg-zinc-800 text-white font-bold text-xs uppercase tracking-wider transition active:scale-95 cursor-pointer hidden sm:block"
          >
            {{ activeInstallmentPlans[0]?.interestRatePercent === 0 ? 'TRẢ GÓP 0%' : 'TRẢ GÓP' }}
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
              :src="storefront.media.reviewVideoUrl"
              title="Product Video Review"
              frameborder="0"
              allowfullscreen
            ></iframe>
          </div>
        </div>
      </div>
    </Teleport>

    <Teleport to="body">
      <div v-if="showUnboxModal" class="fixed inset-0 z-50 bg-black/70 backdrop-blur-xs flex items-center justify-center p-4" @click.self="showUnboxModal = false">
        <div class="bg-white rounded-3xl max-w-2xl w-full overflow-hidden shadow-2xl space-y-3 p-4">
          <div class="flex items-center justify-between px-2">
            <h4 class="text-sm font-bold text-zinc-900">Nội dung mở hộp</h4>
            <button @click="showUnboxModal = false" class="p-1 rounded-full hover:bg-zinc-100 text-zinc-500 cursor-pointer">✕</button>
          </div>
          <iframe v-if="storefront.media.unboxingVideoUrl" :src="storefront.media.unboxingVideoUrl" title="Video mở hộp sản phẩm" class="aspect-video w-full rounded-2xl bg-black" allowfullscreen></iframe>
          <p v-if="storefront.media.unboxingDescription" class="px-2 text-xs text-zinc-600">{{ storefront.media.unboxingDescription }}</p>
        </div>
      </div>
    </Teleport>

    <Teleport to="body">
      <div v-if="show360Modal" class="fixed inset-0 z-50 bg-black/70 backdrop-blur-xs flex items-center justify-center p-4" @click.self="show360Modal = false">
        <div class="bg-white rounded-3xl max-w-3xl w-full overflow-hidden shadow-2xl p-4 space-y-3">
          <div class="flex items-center justify-between px-2">
            <h4 class="text-sm font-bold text-zinc-900">Góc nhìn 360° sản phẩm</h4>
            <button @click="show360Modal = false" class="p-1 rounded-full hover:bg-zinc-100 text-zinc-500 cursor-pointer">✕</button>
          </div>
          <iframe :src="storefront.media.view360Url" title="Góc nhìn 360 độ sản phẩm" class="aspect-video w-full rounded-2xl bg-zinc-100" allowfullscreen></iframe>
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
          <div v-if="activeTradeInOffers.length" class="space-y-3 text-xs">
            <div>
              <label class="block font-semibold text-zinc-700 mb-1">Dòng máy cũ của bạn:</label>
              <select v-model="selectedTradeInIndex" class="w-full border border-zinc-200 rounded-xl p-2.5 bg-zinc-50 font-medium">
                <option v-for="(offer, index) in activeTradeInOffers" :key="`${offer.deviceModel}-${index}`" :value="index">
                  {{ offer.deviceModel }}<template v-if="offer.maximumSubsidy != null"> — trợ giá đến {{ formatPrice(offer.maximumSubsidy) }}</template>
                </option>
              </select>
            </div>
            <p v-if="selectedTradeInOffer?.condition || selectedTradeInOffer?.terms" class="text-zinc-500">
              {{ selectedTradeInOffer?.condition || selectedTradeInOffer?.terms }}
            </p>
            <div class="p-3.5 rounded-2xl bg-emerald-50 border border-emerald-200 text-emerald-800 space-y-1">
              <div class="text-[11px] font-semibold">Thông tin trợ giá do cửa hàng cung cấp:</div>
              <div v-if="selectedTradeInOffer?.minimumTopUp != null || selectedTradeInOffer?.maximumTopUp != null" class="text-base font-black text-emerald-700">
                Bù thêm {{ selectedTradeInOffer?.minimumTopUp != null ? formatPrice(selectedTradeInOffer.minimumTopUp) : '—' }} – {{ selectedTradeInOffer?.maximumTopUp != null ? formatPrice(selectedTradeInOffer.maximumTopUp) : '—' }}
              </div>
              <div v-else-if="selectedTradeInOffer?.maximumSubsidy != null" class="text-base font-black text-emerald-700">Trợ giá đến {{ formatPrice(selectedTradeInOffer.maximumSubsidy) }}</div>
              <div v-else class="text-sm text-emerald-700">Liên hệ cửa hàng để được báo giá theo tình trạng thiết bị.</div>
              <p v-if="selectedTradeInOffer?.terms" class="text-[11px]">{{ selectedTradeInOffer.terms }}</p>
            </div>
          </div>
          <div v-else class="text-xs text-zinc-500">Chưa có chương trình thu cũ đổi mới được cập nhật.</div>
          <a v-if="storefront.storePolicy.hotline" :href="`tel:${storefront.storePolicy.hotline}`" class="block text-center w-full py-2.5 rounded-xl bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs transition">
            Liên hệ tư vấn
          </a>
          <button v-else @click="showTradeInModal = false" class="w-full py-2.5 rounded-xl bg-zinc-100 hover:bg-zinc-200 text-zinc-700 font-bold text-xs transition">Đóng</button>
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
              <span>Chương trình trả góp</span>
            </h4>
            <button @click="showInstallmentModal = false" class="text-zinc-400 hover:text-zinc-600 cursor-pointer">✕</button>
          </div>

          <div class="space-y-3 text-xs">
            <div class="p-3 bg-zinc-50 rounded-2xl border border-zinc-200 flex justify-between items-center">
              <span class="text-zinc-500">Giá trị sản phẩm:</span>
              <span class="font-extrabold text-sm text-red-600">{{ formatPrice(currentPrice) }}</span>
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-3 gap-2 text-center">
              <div v-for="(plan, index) in activeInstallmentPlans" :key="`${plan.providerName}-${plan.months}-${index}`" class="p-3 rounded-2xl border border-zinc-200 bg-white">
                <div class="text-[11px] text-zinc-500 font-medium">{{ plan.providerName }} · {{ plan.months }} tháng</div>
                <div v-if="plan.monthlyPayment != null" class="text-xs font-black text-zinc-900 mt-1">{{ formatPrice(plan.monthlyPayment) }}/tháng</div>
                <div v-if="plan.interestRatePercent != null" class="text-[10px] text-red-600 font-bold mt-0.5">Lãi suất {{ plan.interestRatePercent }}%</div>
                <div v-if="plan.downPayment != null" class="text-[10px] text-zinc-500 mt-0.5">Trả trước {{ formatPrice(plan.downPayment) }}</div>
                <div v-if="plan.terms" class="text-[10px] text-zinc-500 mt-1">{{ plan.terms }}</div>
              </div>
            </div>

            <div v-if="activeInstallmentPlans.length === 0" class="text-[11px] text-zinc-500 leading-normal">Chưa có phương án trả góp được cập nhật.</div>
          </div>

          <a v-if="storefront.storePolicy.hotline" :href="`tel:${storefront.storePolicy.hotline}`" class="block text-center w-full py-2.5 rounded-xl bg-zinc-900 hover:bg-zinc-800 text-white font-bold text-xs transition">Liên hệ tư vấn</a>
          <button v-else @click="showInstallmentModal = false" class="w-full py-2.5 rounded-xl bg-zinc-100 hover:bg-zinc-200 text-zinc-700 font-bold text-xs transition">Đóng</button>
        </div>
      </div>
    </Teleport>

    <!-- 4. Compare Modal -->
    <Teleport to="body">
      <div v-if="showCompareModal" class="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
        <div class="bg-white rounded-3xl max-w-2xl w-full p-6 space-y-4 shadow-2xl max-h-[85vh] overflow-y-auto">
          <div class="flex items-center justify-between pb-2 border-b border-zinc-100">
            <h4 class="text-sm font-bold text-zinc-900">So sánh sản phẩm</h4>
            <button @click="showCompareModal = false" class="text-zinc-400 hover:text-zinc-600 cursor-pointer">✕</button>
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs">
            <div class="p-4 rounded-2xl bg-red-50/30 border border-red-200 space-y-2 text-center">
              <img v-if="activeImage" :src="activeImage" class="w-20 h-20 object-contain mx-auto" :alt="product.name" />
              <div class="font-bold text-zinc-900">{{ product.name }}</div>
              <div class="font-black text-red-600">{{ formatPrice(currentPrice) }}</div>
              <div class="text-[11px] text-zinc-600 pt-2 border-t border-red-100 space-y-1 text-left">
                <div v-for="attribute in (product.attributes || []).slice(0, 4)" :key="attribute.id || attribute.name">• {{ attribute.name }}: {{ attribute.value }}</div>
              </div>
            </div>

            <div v-if="comparisonProduct" class="p-4 rounded-2xl bg-zinc-50 border border-zinc-200 space-y-2 text-center">
              <img v-if="comparisonProduct.thumbnailUrl || comparisonProduct.images?.[0]?.url" :src="comparisonProduct.thumbnailUrl || comparisonProduct.images?.[0]?.url" class="w-20 h-20 object-contain mx-auto" :alt="comparisonProduct.name" />
              <div class="font-bold text-zinc-900">{{ comparisonProduct.name }}</div>
              <div class="font-black text-zinc-900">{{ formatPrice(comparisonProduct.minPrice) }}</div>
              <div class="text-[11px] text-zinc-600 pt-2 border-t border-zinc-200 space-y-1 text-left">
                <div v-for="attribute in (comparisonProduct.attributes || []).slice(0, 4)" :key="attribute.id || attribute.name">• {{ attribute.name }}: {{ attribute.value }}</div>
              </div>
            </div>
            <div v-else class="p-4 rounded-2xl bg-zinc-50 border border-zinc-200 text-center text-xs text-zinc-500">
              {{ loadingComparison ? 'Đang tải sản phẩm so sánh...' : 'Chưa có sản phẩm so sánh được cấu hình.' }}
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
import type { Product, ProductVariant, ProductReview, ProductReviewSummary, ProductStorefrontContent } from '@/types'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const wishlistStore = useWishlistStore()
const authStore = useAuthStore()

type EnrichedProduct = Product & { reason?: string; aiScore?: number }

// Core State
const product = ref<Product | null>(null)
const storefront = ref<ProductStorefrontContent>(emptyStorefrontContent())
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
const selectedCity = ref<string>('')
const feedbackState = ref<'yes' | 'no' | null>(null)

// Dialog Modals
const showVideoModal = ref<boolean>(false)
const showUnboxModal = ref<boolean>(false)
const show360Modal = ref<boolean>(false)
const showTradeInModal = ref<boolean>(false)
const showInstallmentModal = ref<boolean>(false)
const showCompareModal = ref<boolean>(false)
const comparisonProduct = ref<Product | null>(null)
const loadingComparison = ref<boolean>(false)
const selectedTradeInIndex = ref<number>(0)

// Countdown is driven by the active promotion end time returned by the backend.
const currentTime = ref(Date.now())
const timer = ref<{ hours: string; minutes: string; seconds: string } | null>(null)
let timerInterval: any = null
let timeRefreshInterval: any = null

const startCountdown = (endsAt?: string) => {
  if (timerInterval) clearInterval(timerInterval)
  timer.value = null
  if (!endsAt) return

  const updateTimer = () => {
    currentTime.value = Date.now()
    const totalSeconds = Math.max(0, Math.floor((new Date(endsAt).getTime() - currentTime.value) / 1000))
    const h = Math.floor(totalSeconds / 3600)
    const m = Math.floor((totalSeconds % 3600) / 60)
    const s = totalSeconds % 60
    timer.value = {
      hours: String(h).padStart(2, '0'),
      minutes: String(m).padStart(2, '0'),
      seconds: String(s).padStart(2, '0')
    }
    if (totalSeconds === 0 && timerInterval) {
      clearInterval(timerInterval)
      timerInterval = null
    }
  }

  updateTimer()
  timerInterval = setInterval(() => {
    updateTimer()
  }, 1000)
}

// Sub Nav Tabs
const navTabs = [
  { 
    id: 'overview', 
    label: 'Thông tin sản phẩm', 
    iconSvg: '<svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>' 
  },
  { 
    id: 'description', 
    label: 'Mô tả chi tiết', 
    iconSvg: '<svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>' 
  },
  { 
    id: 'specs', 
    label: 'Thông số kỹ thuật', 
    iconSvg: '<svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="4" y="4" width="16" height="16" rx="2"/><rect x="9" y="9" width="6" height="6"/><path d="M9 1v3M15 1v3M9 20v3M15 20v3M20 9h3M20 14h3M1 9h3M1 14h3"/></svg>' 
  },
  { 
    id: 'reviews', 
    label: 'Đánh giá & Bình luận', 
    iconSvg: '<svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>' 
  },
  { 
    id: 'similar', 
    label: 'Có thể bạn quan tâm', 
    iconSvg: '<svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>' 
  },
  { 
    id: 'faq', 
    label: 'Câu hỏi thường gặp', 
    iconSvg: '<svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>' 
  }
]

// Similar Products
const similarProducts = ref<EnrichedProduct[]>([])
const loadingSimilar = ref<boolean>(false)

// Reviews
const reviews = ref<ProductReview[]>([])
const reviewSummary = ref<ProductReviewSummary>({
  productId: 0, averageRating: 0, totalReviews: 0, ratingBreakdown: [],
  reviews: { content: [], page: 0, size: 10, totalElements: 0, totalPages: 0, last: true }
})
const newReview = ref<{ rating: number; comment: string }>({ rating: 5, comment: '' })
const submittingReview = ref<boolean>(false)
const openFaqIndex = ref<number>(0)

// Computed Properties
const isFav = computed(() => product.value ? wishlistStore.isWishlisted(product.value.id) : false)
const currentStock = computed(() => selectedVariant.value?.stock ?? 0)

const currentPrice = computed(() => selectedVariant.value?.price ?? null)

const currentOriginalPrice = computed(() => {
  return selectedVariant.value?.originalPrice ?? null
})

const discountPercent = computed(() => {
  const price = currentPrice.value
  const originalPrice = currentOriginalPrice.value
  if (price == null || originalPrice == null || originalPrice <= price) return null
  return Math.round(((originalPrice - price) / originalPrice) * 100)
})

const loyaltyPoints = computed(() => selectedVariant.value?.rewardPoints ?? null)
const availableVariants = computed(() => (product.value?.variants || []).filter(variant => variant.status?.toUpperCase() === 'ACTIVE'))

const colorOptions = computed(() => {
  const colors = new Map<string, { name: string; hex?: string }>()
  for (const variant of availableVariants.value) {
    const name = variant.colorName?.trim()
    if (name && !colors.has(name)) colors.set(name, { name, hex: variant.colorHex })
  }
  return [...colors.values()]
})

const selectedColorName = computed(() => selectedVariant.value?.colorName || '')
const activePromotions = computed(() => storefront.value.promotions.filter(isOfferActive))
const primaryPromotion = computed(() => activePromotions.value[0] || null)
const activePaymentOffers = computed(() => storefront.value.paymentOffers.filter(isOfferActive))
const activeBundleOffers = computed(() => storefront.value.bundleOffers.filter(offer => isDatedOfferActive(offer.startsAt, offer.endsAt)))
const activeInstallmentPlans = computed(() => storefront.value.installmentPlans.filter(plan => isDatedOfferActive(plan.startsAt, plan.endsAt)))
const activeTradeInOffers = computed(() => storefront.value.tradeInOffers.filter(offer => isDatedOfferActive(offer.startsAt, offer.endsAt)))
const activeCityAvailability = computed(() => storefront.value.cityAvailability)
const selectedCityAvailability = computed(() => activeCityAvailability.value.find(city => city.cityCode === selectedCity.value) || null)
const hasReviews = computed(() => reviewSummary.value.totalReviews > 0)
const selectedTradeInOffer = computed(() => activeTradeInOffers.value[selectedTradeInIndex.value] || activeTradeInOffers.value[0] || null)
const displayedPromotions = computed(() => showMoreOffers.value ? activePromotions.value : activePromotions.value.slice(0, 3))
const policyTexts = computed(() => [
  storefront.value.storePolicy.officialProductText,
  storefront.value.storePolicy.warrantyText,
  storefront.value.storePolicy.exchangeText,
  storefront.value.storePolicy.shippingText
].filter((value): value is string => Boolean(value?.trim())))

const galleryImages = computed(() => {
  const images = [...(product.value?.images || [])]
    .sort((a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0))
    .map(image => image.url)
  const thumbnail = product.value?.thumbnailUrl
  if (thumbnail && !images.includes(thumbnail)) images.unshift(thumbnail)
  return images
})

const highlightSpecs = computed(() => {
  const attrs = product.value?.attributes || []
  const findVal = (...queries: string[]) => attrs.find(a => queries.some(query => a.name.toLowerCase().includes(query)))?.value || ''

  return [
    {
      title: 'Kích thước màn hình',
      value: findVal('màn hình'),
      iconSvg: '<rect x="5" y="2" width="14" height="20" rx="2"/><line x1="12" y1="18" x2="12" y2="18"/>'
    },
    {
      title: 'Hệ thống Camera',
      value: findVal('camera'),
      iconSvg: '<path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/><circle cx="12" cy="13" r="4"/>'
    },
    {
      title: 'Vi xử lý & RAM',
      value: [findVal('cpu', 'chip', 'vi xử lý'), findVal('ram')].filter(Boolean).join(' | '),
      iconSvg: '<rect x="4" y="4" width="16" height="16" rx="2"/><rect x="9" y="9" width="6" height="6"/><path d="M9 1v3M15 1v3M9 20v3M15 20v3M20 9h3M20 14h3M1 9h3M1 14h3"/>'
    },
    {
      title: 'Pin & Công nghệ sạc',
      value: findVal('pin', 'sạc'),
      iconSvg: '<rect x="2" y="7" width="16" height="10" rx="2"/><line x1="22" y1="11" x2="22" y2="13"/>'
    }
  ]
})

const starBreakdown = computed(() => {
  const counts = new Map((reviewSummary.value.ratingBreakdown || []).map(item => [item.stars, item.count]))
  const total = reviewSummary.value.totalReviews
  return [5, 4, 3, 2, 1].map(stars => {
    const count = counts.get(stars) || 0
    return { stars, count, percent: total > 0 ? Math.round(count * 100 / total) : 0 }
  })
})

const displayReviews = computed(() => reviews.value)

const isDatedOfferActive = (startsAt?: string, endsAt?: string) => {
  const now = currentTime.value
  return (!startsAt || new Date(startsAt).getTime() <= now) && (!endsAt || new Date(endsAt).getTime() > now)
}

const isOfferActive = (offer: { startsAt?: string; endsAt?: string }) => isDatedOfferActive(offer.startsAt, offer.endsAt)

watch(primaryPromotion, promotion => startCountdown(promotion?.endsAt))

function emptyStorefrontContent(): ProductStorefrontContent {
  return {
    storySections: [],
    media: {},
    promotions: [],
    paymentOffers: [],
    bundleOffers: [],
    faqs: [],
    cityAvailability: [],
    installmentPlans: [],
    tradeInOffers: [],
    storePolicy: {},
    comparisonProductIds: []
  }
}

const formatPrice = (value?: number | null): string => {
  if (value == null || isNaN(value)) return 'Liên hệ'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}

const formatReviewDate = (value: string) => {
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? 'Không rõ ngày' : new Intl.DateTimeFormat('vi-VN', { dateStyle: 'medium' }).format(date)
}

// Methods & Actions
const selectImage = (idx: number) => {
  if (!galleryImages.value[idx]) return
  activeImageIndex.value = idx
  activeImage.value = galleryImages.value[idx]
}

const prevImage = () => {
  const len = galleryImages.value.length
  if (len < 2) return
  activeImageIndex.value = (activeImageIndex.value - 1 + len) % len
  activeImage.value = galleryImages.value[activeImageIndex.value]
}

const nextImage = () => {
  const len = galleryImages.value.length
  if (len < 2) return
  activeImageIndex.value = (activeImageIndex.value + 1) % len
  activeImage.value = galleryImages.value[activeImageIndex.value]
}

const selectVariant = (variant: ProductVariant) => {
  selectedVariant.value = variant
  quantity.value = Math.min(quantity.value, Math.max(variant.stock, 1))
}

const selectColor = (colorName: string) => {
  const matchingVariant = availableVariants.value.find(variant => variant.colorName === colorName)
  if (matchingVariant) selectVariant(matchingVariant)
}

let touchStartX = 0
let touchEndX = 0

const handleTouchStart = (e: TouchEvent) => {
  if (e.changedTouches && e.changedTouches[0]) {
    touchStartX = e.changedTouches[0].clientX
  }
}

const handleTouchEnd = (e: TouchEvent) => {
  if (e.changedTouches && e.changedTouches[0]) {
    touchEndX = e.changedTouches[0].clientX
    const diff = touchStartX - touchEndX
    if (Math.abs(diff) > 40) {
      if (diff > 0) {
        nextImage()
      } else {
        prevImage()
      }
    }
  }
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
  const variantId = selectedVariant.value?.id
  if (!variantId) {
    toast.error('Sản phẩm chưa có phiên bản để thêm vào giỏ hàng')
    return false
  }
  if (currentStock.value <= 0) {
    toast.error('Phiên bản đang chọn hiện đã hết hàng')
    return false
  }
  addingToCart.value = true
  try {
    await cartStore.addToCart(variantId, quantity.value, product.value.id)
    toastMessage.value = `Đã thêm ${quantity.value} sản phẩm vào giỏ hàng thành công!`
    toast.success(`Đã thêm ${quantity.value} sản phẩm vào giỏ hàng!`)
    setTimeout(() => { toastMessage.value = '' }, 3000)
    return true
  } catch (err: any) {
    toastMessage.value = err.message
    toast.error(err.message || 'Không thể thêm sản phẩm')
    return false
  } finally {
    addingToCart.value = false
  }
}

const handleBuyNow = async () => {
  if (await handleAddToCart()) router.push('/cart')
}

const openVideoModal = () => {
  showVideoModal.value = true
}

const openUnboxModal = () => {
  showUnboxModal.value = true
}

const open360Modal = () => {
  show360Modal.value = true
}

const openCompareModal = async () => {
  showCompareModal.value = true
  comparisonProduct.value = null
  const comparisonProductId = storefront.value.comparisonProductIds.find(id => id !== product.value?.id)
    ?? similarProducts.value.find(item => item.id !== product.value?.id)?.id
  if (!comparisonProductId) return

  loadingComparison.value = true
  try {
    const response = await catalogApi.getProductById(comparisonProductId)
    if (response.success && response.data) comparisonProduct.value = response.data
  } catch (error) {
    console.error('Không thể tải sản phẩm so sánh:', error)
  } finally {
    loadingComparison.value = false
  }
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

  const isBottom = window.innerHeight + window.scrollY >= document.documentElement.scrollHeight - 50
  if (isBottom) {
    activeSection.value = 'faq'
    return
  }

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

let productRequestId = 0
const loadProductData = async (id: number | string) => {
  const requestId = ++productRequestId
  const productId = Number(id)
  currentTime.value = Date.now()
  loading.value = true
  product.value = null
  storefront.value = emptyStorefrontContent()
  selectedVariant.value = null
  activeImage.value = ''
  activeImageIndex.value = 0
  selectedCity.value = ''
  reviewSummary.value = {
    productId, averageRating: 0, totalReviews: 0, ratingBreakdown: [],
    reviews: { content: [], page: 0, size: 10, totalElements: 0, totalPages: 0, last: true }
  }
  reviews.value = []
  comparisonProduct.value = null
  selectedTradeInIndex.value = 0
  showMoreOffers.value = false
  startCountdown()

  try {
    const res = await catalogApi.getProductById(productId)
    if (requestId !== productRequestId) return
    if (res.success && res.data) {
      product.value = res.data
      selectedVariant.value = availableVariants.value[0] || null
      quantity.value = 1
      activeImage.value = galleryImages.value[0] || ''

      behaviorApi.track('VIEW', product.value.id)
      await Promise.all([
        loadStorefrontContent(product.value.id, requestId),
        loadSimilarProducts(product.value.id),
        loadReviews(product.value.id, requestId)
      ])
    }
  } catch (err) {
    if (requestId === productRequestId) console.error('Không thể tải chi tiết sản phẩm:', err)
  } finally {
    if (requestId === productRequestId) loading.value = false
  }
}

const loadStorefrontContent = async (productId: number, requestId: number) => {
  try {
    const response = await catalogApi.getProductStorefront(productId)
    if (requestId !== productRequestId || product.value?.id !== productId) return
    if (response.success && response.data) {
      storefront.value = { ...emptyStorefrontContent(), ...response.data }
      if (activeCityAvailability.value.length) selectedCity.value = activeCityAvailability.value[0].cityCode
    }
  } catch (error) {
    if (requestId === productRequestId) console.error('Không thể tải nội dung trang sản phẩm:', error)
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

const loadReviews = async (productId: number, requestId: number = productRequestId) => {
  try {
    const res = await catalogApi.getReviewSummary(productId)
    if (requestId !== productRequestId || product.value?.id !== productId) return
    if (res.success && res.data) {
      reviewSummary.value = {
        ...res.data,
        averageRating: res.data.averageRating ?? 0,
        totalReviews: res.data.totalReviews ?? 0,
        ratingBreakdown: res.data.ratingBreakdown || []
      }
      reviews.value = res.data.reviews?.content || []
    }
  } catch (err) {
    if (requestId === productRequestId) console.error('Lỗi tải đánh giá:', err)
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
  window.addEventListener('scroll', handleScroll, { passive: true })
  timeRefreshInterval = setInterval(() => { currentTime.value = Date.now() }, 30000)
  if (route.params.id) {
    loadProductData(route.params.id as string)
  }
})

onBeforeUnmount(() => {
  if (timerInterval) clearInterval(timerInterval)
  if (timeRefreshInterval) clearInterval(timeRefreshInterval)
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
