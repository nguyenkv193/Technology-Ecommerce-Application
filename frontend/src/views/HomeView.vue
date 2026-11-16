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


      <!-- 2. Main Full-Width Hero Banner Slider (Apple / Flagship Cinema Style) -->
      <div 
        class="w-full relative overflow-hidden rounded-2xl border border-zinc-200/80 bg-zinc-950 shadow-sm flex flex-col justify-between h-[320px] sm:h-[400px] lg:h-[450px] group select-none"
        @mouseenter="stopSlideTimer"
        @mouseleave="startSlideTimer"
      >
        <!-- Full-bleed Image Banner Slide with Smooth Cross-Fade Transition -->
        <div class="relative w-full h-full flex-1 overflow-hidden">
          <Transition name="hero-slide-fade" mode="out-in">
            <router-link
              :key="currentSlide.id"
              :to="currentSlide.link"
              class="absolute inset-0 block w-full h-full cursor-pointer group/slide"
            >
              <img 
                :src="currentSlide.image" 
                :alt="currentSlide.title"
                class="w-full h-full object-cover object-center group-hover/slide:scale-[1.02] transition-transform duration-700 ease-out"
                loading="eager"
              />
              
              <!-- Bottom subtle gradient overlay for readability -->
              <div class="absolute inset-x-0 bottom-0 h-32 bg-gradient-to-t from-black/80 via-black/25 to-transparent pointer-events-none"></div>

              <!-- Minimalist Caption Pill (Tiêu đề tinh tế góc dưới) -->
              <div class="absolute bottom-5 left-5 sm:left-7 right-28 sm:right-36 z-10 pointer-events-none">
                <div class="inline-flex items-center gap-2.5 px-3.5 py-1.5 rounded-full bg-black/60 backdrop-blur-md border border-white/20 text-white text-xs font-semibold max-w-full shadow-md">
                  <span class="w-2 h-2 rounded-full bg-red-500 animate-pulse shrink-0"></span>
                  <span class="truncate font-sans">{{ currentSlide.title }}</span>
                </div>
              </div>
            </router-link>
          </Transition>
        </div>

        <!-- Prev / Next Navigation Arrows (Smooth hover reveal) -->
        <button 
          @click.stop="prevSlide"
          class="absolute left-3.5 top-1/2 -translate-y-1/2 z-20 w-11 h-11 rounded-full bg-black/40 hover:bg-black/75 backdrop-blur-md text-white border border-white/20 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all duration-200 active:scale-95 shadow-xl cursor-pointer"
          aria-label="Slide trước"
        >
          <svg class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="15 18 9 12 15 6"/></svg>
        </button>
        <button 
          @click.stop="nextSlide"
          class="absolute right-3.5 top-1/2 -translate-y-1/2 z-20 w-11 h-11 rounded-full bg-black/40 hover:bg-black/75 backdrop-blur-md text-white border border-white/20 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all duration-200 active:scale-95 shadow-xl cursor-pointer"
          aria-label="Slide tiếp theo"
        >
          <svg class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="9 18 15 12 9 6"/></svg>
        </button>

        <!-- Bottom Navigation Indicators (Dots & Active Pill) -->
        <div class="absolute bottom-5 right-5 sm:right-7 z-20 flex items-center gap-2 px-3 py-1.5 rounded-full bg-black/60 backdrop-blur-md border border-white/20 shadow-md">
          <button
            v-for="(slide, sIdx) in featuredSlides"
            :key="slide.id"
            @click.stop="goToSlide(sIdx)"
            class="h-2 rounded-full transition-all duration-300 cursor-pointer"
            :class="currentSlideIndex === sIdx ? 'w-7 bg-red-500' : 'w-2 bg-white/40 hover:bg-white/75'"
            :title="slide.title"
          ></button>
        </div>
      </div>

      <!-- 3. Minimalist Brand Trust & Service Guarantee Bar (Apple / High-End Tech Style) -->
      <div class="rounded-2xl border border-zinc-200/80 bg-white/95 backdrop-blur-xs p-3.5 sm:p-4 shadow-xs">
        <div class="grid grid-cols-2 lg:grid-cols-4 gap-3 sm:gap-4 divide-y sm:divide-y-0 divide-zinc-100">
          <!-- Item 1: 100% Chính Hãng -->
          <div class="flex items-center gap-3 p-2 rounded-xl transition-all duration-200 hover:bg-zinc-50 group">
            <div class="w-10 h-10 rounded-xl bg-zinc-100 text-zinc-800 border border-zinc-200/60 flex items-center justify-center shrink-0 group-hover:bg-red-50 group-hover:text-red-600 group-hover:border-red-200 transition-all duration-200">
              <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                <polyline points="9 12 11 14 15 10"/>
              </svg>
            </div>
            <div class="min-w-0">
              <div class="text-xs sm:text-sm font-bold text-zinc-900 tracking-tight group-hover:text-red-600 transition-colors">100% Chính Hãng</div>
              <div class="text-[11px] text-zinc-500 truncate">Ủy quyền & bảo đảm nguồn gốc</div>
            </div>
          </div>

          <!-- Item 2: Giao Hỏa Tốc 2H -->
          <div class="flex items-center gap-3 p-2 rounded-xl transition-all duration-200 hover:bg-zinc-50 group pt-3 sm:pt-2">
            <div class="w-10 h-10 rounded-xl bg-zinc-100 text-zinc-800 border border-zinc-200/60 flex items-center justify-center shrink-0 group-hover:bg-red-50 group-hover:text-red-600 group-hover:border-red-200 transition-all duration-200">
              <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
              </svg>
            </div>
            <div class="min-w-0">
              <div class="text-xs sm:text-sm font-bold text-zinc-900 tracking-tight group-hover:text-red-600 transition-colors">Giao Hỏa Tốc 2H</div>
              <div class="text-[11px] text-zinc-500 truncate">Nội thành miễn phí từ 500K</div>
            </div>
          </div>

          <!-- Item 3: Bảo Hành 12 Tháng -->
          <div class="flex items-center gap-3 p-2 rounded-xl transition-all duration-200 hover:bg-zinc-50 group pt-3 sm:pt-2">
            <div class="w-10 h-10 rounded-xl bg-zinc-100 text-zinc-800 border border-zinc-200/60 flex items-center justify-center shrink-0 group-hover:bg-red-50 group-hover:text-red-600 group-hover:border-red-200 transition-all duration-200">
              <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
              </svg>
            </div>
            <div class="min-w-0">
              <div class="text-xs sm:text-sm font-bold text-zinc-900 tracking-tight group-hover:text-red-600 transition-colors">Bảo Hành 12 Tháng</div>
              <div class="text-[11px] text-zinc-500 truncate">Toàn diện tại TTBH ủy quyền</div>
            </div>
          </div>

          <!-- Item 4: Đổi Mới 30 Ngày -->
          <div class="flex items-center gap-3 p-2 rounded-xl transition-all duration-200 hover:bg-zinc-50 group pt-3 sm:pt-2">
            <div class="w-10 h-10 rounded-xl bg-zinc-100 text-zinc-800 border border-zinc-200/60 flex items-center justify-center shrink-0 group-hover:bg-red-50 group-hover:text-red-600 group-hover:border-red-200 transition-all duration-200">
              <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="23 4 23 10 17 10"/>
                <polyline points="1 20 1 14 7 14"/>
                <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/>
              </svg>
            </div>
            <div class="min-w-0">
              <div class="text-xs sm:text-sm font-bold text-zinc-900 tracking-tight group-hover:text-red-600 transition-colors">Đổi Mới 30 Ngày</div>
              <div class="text-[11px] text-zinc-500 truncate">Lỗi phần cứng 1-đổi-1 an tâm</div>
            </div>
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
import { bannerApi } from '@/api/bannerApi'
import { behaviorApi } from '@/api/behaviorApi'
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
// Top Trending Keyword Tags (Tự động tải từ hành vi người dùng)
interface TrendingTag {
  label: string
  link: string
  count?: number
}

const trendingTags = ref<TrendingTag[]>([
  { label: 'iPhone 16 Pro Max', link: '/products?keyword=iPhone+16' },
  { label: 'Laptop AI 2026', link: '/products?keyword=AI' },
  { label: 'MacBook Air M3', link: '/products?keyword=MacBook' },
  { label: 'Màn Hình 144Hz', link: '/products?keyword=Màn+hình' },
  { label: 'PC Gaming ROG', link: '/products?keyword=ROG' },
  { label: 'Bàn Phím Cơ', link: '/products?keyword=Bàn+phím' },
  { label: 'Tai Nghe ANC', link: '/products?keyword=Tai+nghe' },
  { label: 'Back to School 50%', link: '/products?keyword=Khuyen+mai' }
])

const loadTrendingKeywords = async () => {
  try {
    const res = await behaviorApi.getTrendingKeywords(8, 7)
    if (res.success && res.data && res.data.length > 0) {
      trendingTags.value = res.data.map(item => ({
        label: item.keyword,
        link: item.link,
        count: item.searchCount
      }))
    }
  } catch (err) {
    // Keep defaults on network/api error
  }
}

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



const loadHeroBanners = async () => {
  try {
    const res = await bannerApi.getActiveBanners('HERO_SLIDER')
    if (res.success && res.data && res.data.length > 0) {
      featuredSlides.value = res.data.map(b => ({
        id: b.id,
        title: b.title,
        image: b.imageUrl,
        link: b.linkUrl
      }))
    }
  } catch (err) {
    // Keep fallback featuredSlides
  }
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
  void loadHeroBanners()
  void loadTrendingKeywords()
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

/* Hero Image Slide Fade Transition */
.hero-slide-fade-enter-active,
.hero-slide-fade-leave-active {
  transition: opacity 0.4s ease;
}
.hero-slide-fade-enter-from,
.hero-slide-fade-leave-to {
  opacity: 0;
}

</style>
