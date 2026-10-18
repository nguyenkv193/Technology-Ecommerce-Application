<template>
  <div class="space-y-12 sm:space-y-16">
    <!-- ================= MODERN E-COMMERCE HERO GRID (PHONG VŨ / CELLPHONES STYLE) ================= -->
    <section class="space-y-3.5">
      <!-- 1. Top Trending Keyword Tags Bar -->
      <div class="flex items-center gap-2 overflow-x-auto scrollbar-none py-1 px-1">
        <span class="text-[11px] font-bold text-red-600 uppercase tracking-wider flex items-center gap-1.5 shrink-0">
          <svg class="w-3.5 h-3.5 text-red-600 fill-red-500 animate-pulse" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
            <path stroke-linecap="round" stroke-linejoin="round" d="M15.362 5.214A8.252 8.252 0 0 1 12 21 8.25 8.25 0 0 1 6.038 7.047 8.287 8.287 0 0 0 9 9.601a8.983 8.983 0 0 1 3.361-6.867 8.21 8.21 0 0 0 3 2.48Z" />
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 18a3.75 3.75 0 0 0 .495-7.468 5.99 5.99 0 0 0-1.925 3.547 5.975 5.975 0 0 1-2.133-1.001A3.75 3.75 0 0 0 12 18Z" />
          </svg>
          <span>Xu hướng:</span>
        </span>
        <div class="flex items-center gap-1.5 flex-nowrap shrink-0">
          <router-link
            v-for="tag in trendingTags"
            :key="tag.label"
            :to="tag.link"
            class="px-2.5 py-1 rounded-full text-xs font-medium text-zinc-600 bg-zinc-100 hover:bg-red-50 hover:text-red-600 border border-zinc-200/60 transition-colors whitespace-nowrap cursor-pointer"
          >
            {{ tag.label }}
          </router-link>
        </div>
      </div>

      <!-- Mobile Horizontal Category Pills (Hiển thị khi màn hình nhỏ < lg) -->
      <div class="flex lg:hidden items-center gap-2 overflow-x-auto scrollbar-none py-1 pb-2">
        <router-link
          v-for="cat in sidebarCategories"
          :key="cat.name"
          :to="cat.link"
          class="flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold bg-white border border-zinc-200 shadow-2xs whitespace-nowrap text-zinc-700 hover:text-red-600 shrink-0"
        >
          <span class="w-4 h-4 flex items-center justify-center shrink-0" v-html="cat.iconSvg"></span>
          <span>{{ cat.name }}</span>
        </router-link>
      </div>

      <!-- 2. Main 3-Column Hero Grid: Left Menu (3 cols) + Center Slider (6 cols) + Right Banners (3 cols) -->
      <div class="grid grid-cols-1 lg:grid-cols-12 gap-3.5 sm:gap-4 items-stretch">
        
        <!-- COLUMN 1: Vertical Category Navigation Sidebar (lg:col-span-3) -->
        <div class="hidden lg:flex lg:col-span-3 rounded-3xl border border-zinc-200/90 bg-white p-3 shadow-xs flex-col justify-between select-none">
          <div>
            <div class="px-2.5 py-1.5 text-[11px] font-bold uppercase tracking-wider text-zinc-400 flex items-center justify-between">
              <span>Danh mục sản phẩm</span>
              <span class="text-[10px] text-red-600 font-semibold lowercase">chính hãng</span>
            </div>

            <div class="space-y-0.5 mt-1">
              <router-link
                v-for="cat in sidebarCategories"
                :key="cat.name"
                :to="cat.link"
                class="flex items-center justify-between px-2.5 py-2 rounded-xl text-xs font-medium text-zinc-700 hover:text-red-600 hover:bg-red-50/60 transition-all duration-150 group cursor-pointer"
              >
                <div class="flex items-center gap-2.5 truncate">
                  <span 
                    class="w-7 h-7 rounded-lg bg-zinc-100 group-hover:bg-red-100/70 text-zinc-600 group-hover:text-red-600 flex items-center justify-center shrink-0 transition-colors"
                    v-html="cat.iconSvg"
                  ></span>
                  <span class="truncate font-semibold">{{ cat.name }}</span>
                </div>
                <svg class="w-3.5 h-3.5 text-zinc-300 group-hover:text-red-500 group-hover:translate-x-0.5 transition-all shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="9 18 15 12 9 6"/></svg>
              </router-link>
            </div>
          </div>

          <!-- Bottom Banner in Sidebar -->
          <div class="pt-2 border-t border-zinc-100 px-1">
            <router-link
              to="/products"
              class="flex items-center justify-between text-xs font-bold text-red-600 hover:text-red-700 py-1.5 px-2 rounded-xl hover:bg-red-50/50 transition-colors"
            >
              <span class="flex items-center gap-1.5">
                <svg class="w-3.5 h-3.5 text-red-600 fill-current" viewBox="0 0 24 24"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>
                <span>Xem tất cả sản phẩm</span>
              </span>
              <span>→</span>
            </router-link>
          </div>
        </div>

        <!-- COLUMN 2: Center Main Carousel Banner (lg:col-span-6) -->
        <div 
          class="lg:col-span-6 relative isolate overflow-hidden rounded-3xl border border-zinc-900 bg-[#07090e] shadow-md flex flex-col justify-between min-h-[420px] sm:min-h-[440px]"
          @mouseenter="stopSlideTimer"
          @mouseleave="startSlideTimer"
        >
          <!-- Dynamic Ambient Glow behind product in slider -->
          <div class="absolute inset-0 z-0 overflow-hidden pointer-events-none">
            <div class="absolute inset-0 bg-[radial-gradient(#ffffff0a_1px,transparent_1px)] [background-size:20px_20px] opacity-40"></div>
            <div 
              class="absolute -top-16 -right-16 w-80 h-80 rounded-full blur-[90px] opacity-40 transition-all duration-700 pointer-events-none"
              :class="currentSlide.themeGlow"
            ></div>
            <div class="absolute inset-0 bg-gradient-to-t from-[#07090e] via-transparent to-transparent opacity-50"></div>
          </div>

          <!-- Slider Content Body -->
          <div class="relative z-10 p-6 sm:p-7 flex flex-col justify-between flex-1">
            <Transition name="hero-text-fade" mode="out-in">
              <div :key="currentSlide.id" class="grid grid-cols-1 sm:grid-cols-12 gap-5 items-center flex-1">
                <!-- Text Left (7 cols) -->
                <div class="sm:col-span-7 space-y-3.5">
                  <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-white/10 backdrop-blur-md border border-white/15 text-white text-[11px] font-semibold">
                    <span class="w-1.5 h-1.5 rounded-full animate-ping" :class="currentSlide.accentDot"></span>
                    <span>{{ currentSlide.kicker }}</span>
                  </div>

                  <h1 class="text-2xl sm:text-3xl font-black text-white tracking-tight leading-snug">
                    <span>{{ currentSlide.headingLine1 }}</span> <br/>
                    <span class="bg-clip-text text-transparent bg-gradient-to-r from-zinc-100 via-zinc-200 to-zinc-400 font-extrabold">
                      {{ currentSlide.headingLine2 }}
                    </span>
                  </h1>

                  <p class="text-zinc-300 text-xs sm:text-sm line-clamp-2 leading-relaxed">
                    {{ currentSlide.description }}
                  </p>

                  <div class="flex items-center gap-2 pt-1">
                    <div class="text-xl sm:text-2xl font-black text-white tracking-tight">
                      {{ formatCurrency(currentSlide.price) }}
                    </div>
                    <span class="px-2 py-0.5 rounded-md text-[10px] font-bold uppercase bg-red-600 text-white shadow-2xs">
                      Trả góp 0%
                    </span>
                    <span class="inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-[10px] font-semibold bg-white/10 text-amber-300">
                      <svg class="w-3 h-3 fill-amber-400 text-amber-400" viewBox="0 0 24 24"><path d="M12 2c-.5 3-3 4-3 7a5 5 0 0 0 10 0c0-3-2.5-4-3-7-1 2-2 3-4 0Z"/></svg>
                      <span>Giảm sốc</span>
                    </span>
                  </div>

                  <div class="flex items-center gap-2.5 pt-2">
                    <router-link
                      :to="currentSlide.link"
                      class="px-4 py-2.5 rounded-xl font-bold text-xs bg-white hover:bg-zinc-100 text-zinc-950 transition-all active:scale-95 flex items-center gap-1.5 shadow-sm cursor-pointer group/cta"
                    >
                      <span>{{ currentSlide.primaryCtaText }}</span>
                      <svg class="w-3.5 h-3.5 group-hover/cta:translate-x-0.5 transition-transform" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
                    </router-link>

                    <router-link
                      :to="currentSlide.secondaryCtaLink"
                      class="px-3.5 py-2.5 rounded-xl font-semibold text-xs bg-white/10 hover:bg-white/15 text-zinc-200 border border-white/15 transition-all active:scale-95 cursor-pointer"
                    >
                      <span>{{ currentSlide.secondaryCtaText }}</span>
                    </router-link>
                  </div>
                </div>

                <!-- Product Floating Image Right (5 cols) -->
                <div class="sm:col-span-5 relative flex items-center justify-center min-h-[190px] sm:min-h-[220px]">
                  <router-link :to="currentSlide.link" class="block group cursor-pointer">
                    <img 
                      :src="currentSlide.image" 
                      :alt="currentSlide.headingLine1"
                      class="w-52 sm:w-60 max-h-[210px] object-contain drop-shadow-[0_16px_30px_rgba(0,0,0,0.85)] group-hover:scale-105 transition-transform duration-500 animate-float-slow"
                    />
                  </router-link>
                </div>
              </div>
            </Transition>

            <!-- Bottom Progress Indicators & Arrows -->
            <div class="pt-4 border-t border-white/10 flex items-center justify-between">
              <!-- Dots / Progress Tabs -->
              <div class="flex items-center gap-2">
                <button
                  v-for="(slide, sIdx) in featuredSlides"
                  :key="slide.id"
                  @click="goToSlide(sIdx)"
                  class="h-2 rounded-full transition-all cursor-pointer"
                  :class="currentSlideIndex === sIdx ? 'w-8 bg-red-500' : 'w-2 bg-white/20 hover:bg-white/40'"
                  :title="slide.shortName || slide.headingLine1"
                ></button>
              </div>

              <!-- Arrows -->
              <div class="flex items-center gap-1.5">
                <button 
                  @click="prevSlide"
                  class="w-7 h-7 rounded-lg bg-white/10 hover:bg-white/20 text-white flex items-center justify-center transition active:scale-95 cursor-pointer"
                  title="Slide trước"
                >
                  <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M15 18l-6-6 6-6"/></svg>
                </button>
                <button 
                  @click="nextSlide"
                  class="w-7 h-7 rounded-lg bg-white/10 hover:bg-white/20 text-white flex items-center justify-center transition active:scale-95 cursor-pointer"
                  title="Slide tiếp theo"
                >
                  <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M9 18l6-6-6-6"/></svg>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- COLUMN 3: Right Side Banners (lg:col-span-3 - 2 Stacked Cards) -->
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-1 gap-3.5 lg:col-span-3">
          <router-link
            v-for="b in sidePromoBanners"
            :key="b.id"
            :to="b.link"
            class="relative rounded-3xl overflow-hidden p-4 sm:p-5 flex flex-col justify-between group shadow-xs transition-all duration-300 hover:shadow-md cursor-pointer border border-zinc-200/80"
            :class="b.bgClass"
          >
            <!-- Background Image overlay with blend -->
            <div class="absolute inset-0 opacity-15 group-hover:opacity-25 transition-opacity pointer-events-none bg-cover bg-center" :style="{ backgroundImage: `url(${b.image})` }"></div>
            
            <div class="relative z-10 space-y-1">
              <span class="inline-block px-2.5 py-0.5 rounded-full text-[10px] font-extrabold uppercase tracking-wider" :class="b.badgeClass">
                {{ b.badge }}
              </span>
              <h3 class="text-base sm:text-lg font-black tracking-tight leading-tight pt-1" :class="b.titleClass">
                {{ b.title }}
              </h3>
              <p class="text-xs font-semibold" :class="b.subtitleClass">
                {{ b.subtitle }}
              </p>
            </div>

            <div class="relative z-10 pt-4 flex items-center justify-between">
              <span class="text-xs font-bold underline decoration-2 underline-offset-4 flex items-center gap-1" :class="b.ctaClass">
                <span>{{ b.ctaText }}</span>
                <span class="group-hover:translate-x-1 transition-transform">›</span>
              </span>
              <div class="w-9 h-9 rounded-xl bg-white/90 shadow-2xs border border-zinc-200/80 flex items-center justify-center shrink-0" v-html="b.iconSvg"></div>
            </div>
          </router-link>
        </div>

      </div>

      <!-- 3. Bottom Row: 4 Mini Promo Cards (Phong Vũ Style) -->
      <div class="grid grid-cols-2 lg:grid-cols-4 gap-3 sm:gap-4">
        <router-link
          v-for="card in bottomMiniCards"
          :key="card.id"
          :to="card.link"
          class="rounded-2xl border border-zinc-200/90 bg-white p-3.5 sm:p-4 hover:border-zinc-300 hover:shadow-xs transition-all flex items-center justify-between gap-3 group cursor-pointer"
        >
          <div class="space-y-1 min-w-0">
            <span class="inline-block text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded-md" :class="card.badgeClass">
              {{ card.badge }}
            </span>
            <div class="text-xs sm:text-sm font-extrabold text-zinc-900 truncate group-hover:text-red-600 transition-colors">
              {{ card.title }}
            </div>
            <div class="text-[11px] font-semibold text-red-600">
              {{ card.highlight }}
            </div>
          </div>
          <div class="w-12 h-12 sm:w-14 sm:h-14 rounded-xl bg-zinc-50 border border-zinc-100 flex items-center justify-center p-1 shrink-0 group-hover:scale-105 transition-transform">
            <img :src="card.image" :alt="card.title" class="max-h-full max-w-full object-contain" />
          </div>
        </router-link>
      </div>

      <!-- 4. Trust Badges Row (Enterprise Brand Guarantees) -->
      <div class="grid grid-cols-2 sm:grid-cols-4 gap-3 p-4 rounded-2xl bg-white border border-zinc-200/80 text-xs shadow-2xs">
        <div class="flex items-center gap-2.5">
          <div class="w-8 h-8 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center shrink-0">
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><polyline points="9 12 11 14 15 10"/></svg>
          </div>
          <div>
            <div class="font-bold text-zinc-900">100% Chính hãng</div>
            <div class="text-[10px] text-zinc-500">Bảo đảm nguồn gốc</div>
          </div>
        </div>

        <div class="flex items-center gap-2.5">
          <div class="w-8 h-8 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center shrink-0">
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>
          </div>
          <div>
            <div class="font-bold text-zinc-900">Giao hỏa tốc 2H</div>
            <div class="text-[10px] text-zinc-500">Nội thành miễn phí</div>
          </div>
        </div>

        <div class="flex items-center gap-2.5">
          <div class="w-8 h-8 rounded-xl bg-violet-50 text-violet-600 flex items-center justify-center shrink-0">
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
          </div>
          <div>
            <div class="font-bold text-zinc-900">Bảo hành 12 tháng</div>
            <div class="text-[10px] text-zinc-500">Toàn diện chính hãng</div>
          </div>
        </div>

        <div class="flex items-center gap-2.5">
          <div class="w-8 h-8 rounded-xl bg-amber-50 text-amber-600 flex items-center justify-center shrink-0">
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="23 4 23 10 17 10"/><polyline points="1 20 1 14 7 14"/><path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/></svg>
          </div>
          <div>
            <div class="font-bold text-zinc-900">Đổi mới 30 ngày</div>
            <div class="text-[10px] text-zinc-500">Lỗi 1-đổi-1 an tâm</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Danh Mục Nổi Bật (FPT Shop Style) -->
    <FeaturedCategoriesGrid />

    <!-- Gợi ý Dành Cho Bạn -->
    <RecommendationCarousel
      title="Gợi Ý Dành Riêng Cho Bạn"
      subtitle="Những thiết bị được chọn lọc phù hợp với sở thích của bạn"
      badge="Dành cho bạn"
      :items="personalizedProducts"
      :loading="loadingPersonalized"
    />

    <!-- Sản phẩm Nổi Bật -->
    <RecommendationCarousel
      title="Sản Phẩm Bán Chạy"
      subtitle="Các sản phẩm bán nhiều nhất từ đơn hàng đã giao và thanh toán"
      badge="Nổi bật"
      :items="bestSellerProducts"
      :loading="loadingBestSellers"
      empty-message="Chưa có dữ liệu bán chạy từ đơn hàng đã giao và thanh toán. Bạn có thể khám phá các sản phẩm bên dưới."
      :error-message="bestSellerError"
      @retry="loadBestSellers"
    >
      <template #action>
        <router-link to="/products" class="catalog-shelf-link">Khám phá sản phẩm <span aria-hidden="true">→</span></router-link>
      </template>
    </RecommendationCarousel>

    <template v-for="shelf in catalogShelves" :key="shelf.key">
      <RecommendationCarousel
        v-if="shelf.key === 'newest' || shelf.loading || shelf.error || shelf.products.length > 0"
        :title="shelf.title"
        :subtitle="shelf.subtitle"
        :badge="shelf.key === 'newest' ? 'Mới lên kệ' : 'Khám phá'"
        :items="shelf.products"
        :loading="shelf.loading"
        :error-message="shelf.error"
        empty-message="Chưa có sản phẩm trong danh sách này. Vui lòng quay lại sau."
        @retry="retryCatalogShelf(shelf)"
      >
        <template #action>
          <router-link :to="shelf.href" :aria-label="`Xem tất cả: ${shelf.title}`" class="catalog-shelf-link">
            Xem tất cả <span aria-hidden="true">→</span>
          </router-link>
        </template>
      </RecommendationCarousel>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import RecommendationCarousel from '@/components/RecommendationCarousel.vue'
import FeaturedCategoriesGrid from '@/components/FeaturedCategoriesGrid.vue'
import { recommendationApi } from '@/api/recommendationApi'
import { catalogApi } from '@/api/catalogApi'
import { useAuthStore } from '@/stores/auth'
import type { Product, Category } from '@/types'

const authStore = useAuthStore()

type EnrichedProduct = Product & { reason?: string; aiScore?: number }

const personalizedProducts = ref<EnrichedProduct[]>([])
const bestSellerProducts = ref<EnrichedProduct[]>([])
const loadingPersonalized = ref<boolean>(false)
const loadingBestSellers = ref<boolean>(true)
const bestSellerError = ref('')

interface CatalogShelf {
  key: string
  title: string
  subtitle: string
  categorySlugs: string[]
  categoryId?: number
  href: string
  products: Product[]
  loading: boolean
  error: string
}

const catalogShelves = ref<CatalogShelf[]>([
  { key: 'newest', title: 'Sản Phẩm Mới Lên Kệ', subtitle: 'Những sản phẩm vừa được cập nhật trong cửa hàng', categorySlugs: [] },
  { key: 'phones', title: 'Điện Thoại', subtitle: 'Khám phá các dòng điện thoại và cấu hình đang có tại TechStore', categorySlugs: ['dien-thoai'] },
  { key: 'laptops', title: 'Laptop', subtitle: 'Tìm chiếc laptop phù hợp cho công việc, học tập và giải trí', categorySlugs: ['laptop'] },
  { key: 'tablets', title: 'Máy Tính Bảng', subtitle: 'Thiết bị gọn nhẹ cho học tập, sáng tạo và sử dụng hằng ngày', categorySlugs: ['may-tinh-bang'] },
  { key: 'accessories', title: 'Linh Kiện & Phụ Kiện', subtitle: 'Hoàn thiện góc làm việc và nâng cấp trải nghiệm của bạn', categorySlugs: ['linh-kien-phu-kien'] },
  { key: 'audio', title: 'Tai Nghe & Âm Thanh', subtitle: 'Khám phá các thiết bị nghe nhạc và giải trí', categorySlugs: ['tai-nghe', 'am-thanh'] }
].map(shelf => ({ ...shelf, href: '/products', products: [], loading: true, error: '' })))

const flattenCategories = (categories: Category[]): Category[] =>
  categories.flatMap(category => [category, ...flattenCategories(category.children ?? [])])

const loadCatalogShelf = async (shelf: CatalogShelf) => {
  shelf.loading = true
  shelf.error = ''
  try {
    const res = await catalogApi.getProducts({
      page: 0, size: 8, status: 'ACTIVE', sortBy: 'createdAt', sortDirection: 'DESC',
      ...(shelf.categoryId !== undefined ? { categoryId: shelf.categoryId } : {})
    })
    if (!res.success) throw new Error('Catalog request failed')
    shelf.products = res.data.content
  } catch {
    shelf.error = 'Chưa thể tải danh sách sản phẩm. Vui lòng thử lại.'
  } finally {
    shelf.loading = false
  }
}

const loadCategoryShelves = async () => {
  const shelves = catalogShelves.value.filter(shelf => shelf.categorySlugs.length > 0)
  shelves.forEach(shelf => { shelf.loading = true; shelf.error = '' })
  try {
    const res = await catalogApi.getCategories()
    if (!res.success) throw new Error('Categories request failed')
    const categories = flattenCategories(res.data)
    await Promise.allSettled(shelves.map(async shelf => {
      const category = shelf.categorySlugs.map(slug => categories.find(item => item.slug === slug)).find(Boolean)
      if (!category) {
        shelf.products = []
        shelf.loading = false
        return
      }
      shelf.categoryId = category.id
      shelf.href = `/products?categoryId=${category.id}`
      await loadCatalogShelf(shelf)
    }))
  } catch {
    shelves.forEach(shelf => {
      shelf.loading = false
      shelf.error = 'Chưa thể tải danh mục sản phẩm. Vui lòng thử lại.'
    })
  }
}

const retryCatalogShelf = (shelf: CatalogShelf) => {
  if (shelf.categorySlugs.length > 0 && shelf.categoryId === undefined) {
    void loadCategoryShelves()
  } else {
    void loadCatalogShelf(shelf)
  }
}

// ================= E-COMMERCE HERO GRID DATA (ADMIN-READY) =================
// Top Trending Keyword Tags
const trendingTags = [
  { label: 'iPhone 16 Pro Max', link: '/products?keyword=iPhone+16' },
  { label: 'Laptop AI 2026', link: '/products?keyword=AI' },
  { label: 'MacBook Air M3', link: '/products?keyword=MacBook' },
  { label: 'Màn Hình 144Hz', link: '/products?keyword=Màn+hình' },
  { label: 'PC Gaming ROG', link: '/products?keyword=ROG' },
  { label: 'Bàn Phím Cơ', link: '/products?keyword=Bàn+phím' },
  { label: 'Tai Nghe ANC', link: '/products?keyword=Tai+nghe' },
  { label: 'Back to School 50%', link: '/products?keyword=Khuyen+mai' }
]

// Sidebar Categories
const sidebarCategories = [
  {
    name: 'Laptop & MacBook',
    link: '/products?category=Laptop',
    iconSvg: '<svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="12" rx="2"/><path d="M2 20h20"/></svg>'
  },
  {
    name: 'Apple Authorised',
    link: '/products?brandId=1',
    iconSvg: '<svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20.94c1.5 0 2.75 1.06 4 1.06 3 0 6-8 6-12.22A4.91 4.91 0 0 0 17 5c-2.22 0-4 1.44-5 2-1-.56-2.78-2-5-2a4.9 4.9 0 0 0-5 4.78C2 14 5 22 8 22c1.25 0 2.5-1.06 4-1.06Z"/><path d="M10 2c1 .5 2 2 2 5"/></svg>'
  },
  {
    name: 'PC & Máy tính bàn',
    link: '/products?keyword=PC',
    iconSvg: '<svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="3" width="20" height="14" rx="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/></svg>'
  },
  {
    name: 'Linh kiện máy tính',
    link: '/products?keyword=Linh+kiện',
    iconSvg: '<svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="4" y="4" width="16" height="16" rx="2"/><rect x="9" y="9" width="6" height="6"/><path d="M9 1v3M15 1v3M9 20v3M15 20v3M20 9h3M20 14h3M1 9h3M1 14h3"/></svg>'
  },
  {
    name: 'Màn hình máy tính',
    link: '/products?keyword=Màn+hình',
    iconSvg: '<svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="3" width="20" height="14" rx="2"/><line x1="12" y1="17" x2="12" y2="21"/></svg>'
  },
  {
    name: 'Điện thoại & Tablet',
    link: '/products?category=Điện+thoại',
    iconSvg: '<svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="5" y="2" width="14" height="20" rx="2"/><line x1="12" y1="18" x2="12.01" y2="18"/></svg>'
  },
  {
    name: 'Bàn phím & Chuột',
    link: '/products?keyword=Bàn+phím',
    iconSvg: '<svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="4" width="20" height="16" rx="2"/><line x1="6" y1="8" x2="6" y2="8"/><line x1="10" y1="8" x2="10" y2="8"/><line x1="14" y1="8" x2="14" y2="8"/><line x1="18" y1="8" x2="18" y2="8"/></svg>'
  },
  {
    name: 'Tai nghe & Âm thanh',
    link: '/products?category=Âm+thanh',
    iconSvg: '<svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 18v-6a9 9 0 0 1 18 0v6"/><path d="M21 19a2 2 0 0 1-2 2h-1a2 2 0 0 1-2-2v-3a2 2 0 0 1 2-2h3zM3 19a2 2 0 0 0 2 2h1a2 2 0 0 0 2-2v-3a2 2 0 0 0-2-2H3z"/></svg>'
  },
  {
    name: 'Phụ kiện công nghệ',
    link: '/products?keyword=Phụ+kiện',
    iconSvg: '<svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2v6M12 18v4M4.93 4.93l4.24 4.24M14.83 14.83l4.24 4.24M2 12h6M18 12h4M4.93 19.07l4.24-4.24M14.83 9.17l4.24-4.24"/></svg>'
  },
  {
    name: 'TechNews & Tin tức',
    link: '/products',
    iconSvg: '<svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 22h16a2 2 0 0 0 2-2V4a2 2 0 0 0-2-2H8a2 2 0 0 0-2 2v16a2 2 0 0 1-2 2Zm0 0a2 2 0 0 1-2-2v-9c0-1.1.9-2 2-2h2"/><path d="M18 14h-8M15 18h-5M10 6h8v4h-8V6Z"/></svg>'
  }
]

// Right Side Promotional Banners (Stacked)
const sidePromoBanners = [
  {
    id: 1,
    badge: 'ƯU ĐÃI ĐỘC QUYỀN',
    badgeClass: 'bg-blue-600 text-white',
    title: 'HỌC SINH - SINH VIÊN',
    titleClass: 'text-zinc-900',
    subtitle: 'Giảm thêm 5% + Quà tặng balo TechStore',
    subtitleClass: 'text-blue-600',
    ctaText: 'Đăng ký ngay',
    ctaClass: 'text-blue-700',
    iconSvg: '<svg class="w-5 h-5 text-blue-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>',
    bgClass: 'bg-gradient-to-br from-blue-50/90 via-indigo-50/60 to-white',
    image: 'https://images.unsplash.com/photo-1523240795612-9a054b0db644?w=500&auto=format&fit=crop&q=80',
    link: '/products?keyword=Hoc+sinh+sinh+vien'
  },
  {
    id: 2,
    badge: 'KHUYẾN MÃI THÁNG',
    badgeClass: 'bg-red-600 text-white',
    title: 'GIẢM ĐẾN 50%',
    titleClass: 'text-zinc-900',
    subtitle: 'Xả kho thiết bị công nghệ & Phụ kiện',
    subtitleClass: 'text-red-600',
    ctaText: 'Săn deal ngay',
    ctaClass: 'text-red-700',
    iconSvg: '<svg class="w-5 h-5 text-red-600 fill-red-500" viewBox="0 0 24 24"><path d="M12 2c-.5 3-3 4-3 7a5 5 0 0 0 10 0c0-3-2.5-4-3-7-1 2-2 3-4 0Z"/></svg>',
    bgClass: 'bg-gradient-to-br from-red-50/90 via-rose-50/60 to-white',
    image: 'https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=500&auto=format&fit=crop&q=80',
    link: '/products?keyword=Khuyen+mai'
  }
]

// Bottom 4 Mini Promo Cards (Phong Vũ Style)
const bottomMiniCards = [
  {
    id: 1,
    badge: 'FLASH SALE',
    badgeClass: 'bg-amber-100 text-amber-800 border border-amber-200',
    title: 'Laptop Gaming & AI',
    highlight: 'Giảm đến 7 Triệu',
    image: 'https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=300&auto=format&fit=crop&q=80',
    link: '/products?category=Laptop'
  },
  {
    id: 2,
    badge: 'APPLE STORE',
    badgeClass: 'bg-zinc-100 text-zinc-800 border border-zinc-200',
    title: 'Mac mini M4 & Studio',
    highlight: 'Trả góp 0% duyệt 5P',
    image: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=300&auto=format&fit=crop&q=80',
    link: '/products?brandId=1'
  },
  {
    id: 3,
    badge: 'BẢO HÀNH VÀNG',
    badgeClass: 'bg-blue-100 text-blue-800 border border-blue-200',
    title: 'Gói Bảo Hành 1 Đổi 1',
    highlight: 'Giá chỉ từ 199K',
    image: 'https://images.unsplash.com/photo-1563770660941-20978e870e26?w=300&auto=format&fit=crop&q=80',
    link: '/products'
  },
  {
    id: 4,
    badge: 'BUILD PC',
    badgeClass: 'bg-emerald-100 text-emerald-800 border border-emerald-200',
    title: 'Tự Ráp PC Theo Ý',
    highlight: 'Tặng tản nước 3 Triệu',
    image: 'https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=300&auto=format&fit=crop&q=80',
    link: '/products?keyword=PC'
  }
]

// Flagship Showcase Slides
interface HudSpec {
  label: string
  value: string
  icon: string
}

interface FeaturedSlide {
  id: number
  tag: string
  badge: string
  title: string
  shortName: string
  specs: string[]
  price: number
  image: string
  bgImage: string
  link: string
  kicker: string
  headingLine1: string
  headingLine2: string
  description: string
  primaryCtaText: string
  secondaryCtaText: string
  secondaryCtaLink: string
  themeGlow: string
  accentDot: string
  hudSpecs: HudSpec[]
}

const featuredSlides = ref<FeaturedSlide[]>([
  {
    id: 3,
    tag: 'Apple Silicon M3',
    badge: 'Flagship Laptop',
    title: 'MacBook Pro 14 M3 Pro',
    shortName: 'MacBook Pro M3',
    specs: ['Apple M3 Pro 11-Core', '18GB Unified RAM', 'Liquid Retina XDR', 'Pin 18h'],
    price: 49990000,
    image: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&auto=format&fit=crop&q=80',
    bgImage: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=1600&auto=format&fit=crop&q=85',
    link: '/products/3',
    kicker: 'Apple Silicon M3 Pro Siêu Phẩm',
    headingLine1: 'Thiết Kế Đỉnh Cao.',
    headingLine2: 'Hiệu Năng Vượt Bậc.',
    description: 'Trang bị vi xử lý Apple M3 Pro thế hệ mới với 18GB RAM Unified, màn hình Liquid Retina XDR 120Hz rực rỡ và thời lượng pin 18 giờ liên tục, đáp ứng hoàn hảo mọi tác vụ đồ họa và lập trình chuyên sâu.',
    primaryCtaText: 'Khám phá MacBook Pro',
    secondaryCtaText: 'Xem tất cả Laptop',
    secondaryCtaLink: '/products?categoryId=1',
    themeGlow: 'bg-gradient-to-br from-violet-600/35 via-indigo-600/20 to-transparent',
    accentDot: 'bg-violet-400',
    hudSpecs: [
      { label: 'Chipset Siêu Mạnh', value: 'Apple M3 Pro 11-Core', icon: 'cpu' },
      { label: 'Màn hình Chuẩn Studio', value: 'Liquid Retina XDR 120Hz', icon: 'display' }
    ]
  },
  {
    id: 1,
    tag: 'Titanium Design',
    badge: 'Smartphone Đỉnh Cao',
    title: 'iPhone 16 Pro Max 256GB',
    shortName: 'iPhone 16 Pro',
    specs: ['Apple A18 Pro 3nm', 'Camera 48MP Zoom 5x', '6.9" 120Hz ProMotion', 'Titan Tự Nhiên'],
    price: 34990000,
    image: 'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=800&auto=format&fit=crop&q=80',
    bgImage: 'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=1600&auto=format&fit=crop&q=85',
    link: '/products/1',
    kicker: 'Khung Titan & Apple Intelligence',
    headingLine1: 'Quyền Năng Bứt Phá.',
    headingLine2: 'Chuẩn Mực Tương Lai.',
    description: 'Tuyệt tác Flagship hàng đầu với chip vi xử lý Apple A18 Pro 3nm, cụm camera 48MP Fusion zoom quang học 5x sắc nét và màn hình viền siêu mỏng cùng khung titan chuẩn hàng không vũ trụ.',
    primaryCtaText: 'Khám phá iPhone 16 Pro',
    secondaryCtaText: 'Xem Flagship Phone',
    secondaryCtaLink: '/products?categoryId=2',
    themeGlow: 'bg-gradient-to-br from-amber-500/30 via-orange-600/20 to-transparent',
    accentDot: 'bg-amber-400',
    hudSpecs: [
      { label: 'Vi xử lý Tối Tân', value: 'Apple A18 Pro 3nm', icon: 'cpu' },
      { label: 'Ống kính Đỉnh Cao', value: '48MP Fusion 5x Tele', icon: 'camera' }
    ]
  },
  {
    id: 5,
    tag: 'AI Workstation & Gaming',
    badge: 'Siêu Cấu Hình',
    title: 'ASUS ROG Zephyrus G16 OLED',
    shortName: 'ROG Zephyrus G16',
    specs: ['Core Ultra 9 185H', 'RTX 4070 8GB', 'Nebula OLED 240Hz', '32GB LPDDR5X'],
    price: 64990000,
    image: 'https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=800&auto=format&fit=crop&q=80',
    bgImage: 'https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=1600&auto=format&fit=crop&q=85',
    link: '/products/5',
    kicker: 'AI Workstation & Gaming Đỉnh Cao',
    headingLine1: 'Cỗ Máy Chiến Binh.',
    headingLine2: 'Đồ Họa & AI Tối Thượng.',
    description: 'Định nghĩa lại laptop gaming siêu mỏng nhẹ với vi xử lý Intel Core Ultra 9 tích hợp AI NPU, card đồ họa NVIDIA GeForce RTX 4070 và màn hình ROG Nebula OLED 240Hz siêu mượt mà.',
    primaryCtaText: 'Khám phá ROG Zephyrus',
    secondaryCtaText: 'Xem Máy trạm Gaming',
    secondaryCtaLink: '/products?categoryId=1',
    themeGlow: 'bg-gradient-to-br from-red-600/35 via-rose-600/20 to-transparent',
    accentDot: 'bg-red-400',
    hudSpecs: [
      { label: 'Card Đồ Họa', value: 'GeForce RTX 4070 8GB', icon: 'gpu' },
      { label: 'Màn hình Gaming', value: 'ROG Nebula OLED 240Hz', icon: 'display' }
    ]
  },
  {
    id: 7,
    tag: 'Chống Ồn Huyền Thoại',
    badge: 'Hi-Res Audio',
    title: 'Sony WH-1000XM5 Wireless',
    shortName: 'Sony WH-1000XM5',
    specs: ['Bộ xử lý kép V1 + QN1', 'Âm thanh LDAC Hi-Res', 'Pin 30 giờ', 'Màng loa sợi carbon'],
    price: 7990000,
    image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&auto=format&fit=crop&q=80',
    bgImage: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=1600&auto=format&fit=crop&q=85',
    link: '/products/7',
    kicker: 'Chống Ồn Chủ Động & Hi-Res Wireless',
    headingLine1: 'Tĩnh Lặng Tuyệt Đối.',
    headingLine2: 'Âm Thanh Chuẩn Studio.',
    description: 'Đắm chìm vào thế giới âm thanh chân thực đỉnh cao với vi xử lý kép V1 và QN1, màng loa sợi carbon 30mm siêu nhẹ hỗ trợ giải mã LDAC và thời lượng pin vượt bậc lên đến 30 giờ.',
    primaryCtaText: 'Trải nghiệm Sony XM5',
    secondaryCtaText: 'Xem Thiết bị Âm thanh',
    secondaryCtaLink: '/products?categoryId=4',
    themeGlow: 'bg-gradient-to-br from-cyan-500/35 via-blue-600/20 to-transparent',
    accentDot: 'bg-cyan-400',
    hudSpecs: [
      { label: 'Chipset Chống Ồn', value: 'Kép V1 + HD QN1', icon: 'audio' },
      { label: 'Thời Lượng Pin', value: '30h Nghe Nhạc Liên Tục', icon: 'battery' }
    ]
  }
])

const currentSlideIndex = ref<number>(0)
const slideProgress = ref<number>(0)
let slideTimer: any = null
const SLIDE_DURATION = 5000 // 5 seconds per slide
const TICK_INTERVAL = 50

const currentSlide = computed(() => {
  return featuredSlides.value[currentSlideIndex.value] || featuredSlides.value[0]
})

const nextSlide = () => {
  currentSlideIndex.value = (currentSlideIndex.value + 1) % featuredSlides.value.length
  slideProgress.value = 0
}

const prevSlide = () => {
  currentSlideIndex.value = (currentSlideIndex.value - 1 + featuredSlides.value.length) % featuredSlides.value.length
  slideProgress.value = 0
}

const goToSlide = (idx: number) => {
  currentSlideIndex.value = idx
  slideProgress.value = 0
}

const startSlideTimer = () => {
  stopSlideTimer()
  slideTimer = setInterval(() => {
    slideProgress.value += (TICK_INTERVAL / SLIDE_DURATION) * 100
    if (slideProgress.value >= 100) {
      slideProgress.value = 0
      nextSlide()
    }
  }, TICK_INTERVAL)
}

const stopSlideTimer = () => {
  if (slideTimer !== null) {
    clearInterval(slideTimer)
    slideTimer = null
  }
}

const formatCurrency = (value?: number | null): string => {
  if (!value || isNaN(value)) return 'Liên hệ'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}


const loadBestSellers = async () => {
  loadingBestSellers.value = true
  bestSellerError.value = ''
  try {
    const res = await catalogApi.getBestSellers(4)
    bestSellerProducts.value = res.data
  } catch (error) {
    bestSellerProducts.value = []
    bestSellerError.value = 'Chưa thể tải sản phẩm bán chạy. Vui lòng thử lại.'
    console.error('Không thể tải sản phẩm bán chạy:', error)
  } finally {
    loadingBestSellers.value = false
  }
}

let personalizedRequestId = 0
const loadPersonalized = async () => {
  const requestId = ++personalizedRequestId
  loadingPersonalized.value = true
  personalizedProducts.value = []
  try {
    const res = await recommendationApi.getPersonalized(4)
    if (requestId !== personalizedRequestId) return
    personalizedProducts.value = res.data.recommendations.map(item => ({
      ...item.product,
      reason: item.reason,
      aiScore: item.score ?? undefined
    }))
  } catch (error) {
    if (requestId === personalizedRequestId) console.error('Không thể tải gợi ý sản phẩm:', error)
  } finally {
    if (requestId === personalizedRequestId) loadingPersonalized.value = false
  }
}

watch(() => authStore.token, loadPersonalized, { immediate: true })

onMounted(() => {
  loadBestSellers()
  const newest = catalogShelves.value.find(shelf => shelf.key === 'newest')
  if (newest) void loadCatalogShelf(newest)
  void loadCategoryShelves()
  startSlideTimer()
})

onUnmounted(() => {
  personalizedRequestId++
  stopSlideTimer()
})
</script>

<style scoped>
.catalog-shelf-link {
  @apply inline-flex items-center justify-center gap-2 self-start sm:self-auto shrink-0 rounded-lg border border-zinc-200 bg-white px-4 py-2 text-xs font-semibold text-zinc-700 transition-colors hover:border-zinc-400 hover:text-zinc-950 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-zinc-900 focus-visible:ring-offset-2;
}

/* Card Slide Transition */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.35s ease, transform 0.35s cubic-bezier(0.16, 1, 0.3, 1);
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(10px) scale(0.98);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.98);
}

/* Hero Background Dynamic Crossfade */
.hero-bg-crossfade-enter-active,
.hero-bg-crossfade-leave-active {
  transition: opacity 0.9s cubic-bezier(0.4, 0, 0.2, 1);
}

.hero-bg-crossfade-enter-from,
.hero-bg-crossfade-leave-to {
  opacity: 0;
}

/* Hero Left Text Smooth Transition */
.hero-text-fade-enter-active,
.hero-text-fade-leave-active {
  transition: opacity 0.35s ease, transform 0.35s cubic-bezier(0.16, 1, 0.3, 1);
}

.hero-text-fade-enter-from {
  opacity: 0;
  transform: translateY(8px);
}

.hero-text-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* Floating Animations for Product & Spec HUDs */
@keyframes float-slow {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-8px) rotate(0.5deg);
  }
}

.animate-float-slow {
  animation: float-slow 4.5s ease-in-out infinite;
}

@keyframes hover-gentle {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-5px);
  }
}

.animate-hover-gentle {
  animation: hover-gentle 3.5s ease-in-out infinite;
}

.animate-hover-gentle-delay {
  animation: hover-gentle 4s ease-in-out infinite 1.8s;
}

/* Hero Product Zoom Transition */
.hero-product-zoom-enter-active,
.hero-product-zoom-leave-active {
  transition: opacity 0.4s ease, transform 0.45s cubic-bezier(0.16, 1, 0.3, 1);
}

.hero-product-zoom-enter-from {
  opacity: 0;
  transform: scale(0.93) translateY(12px);
}

.hero-product-zoom-leave-to {
  opacity: 0;
  transform: scale(0.96) translateY(-12px);
}
</style>
