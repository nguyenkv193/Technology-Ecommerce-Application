<template>
  <div class="space-y-12 sm:space-y-16">
    <!-- Hero Banner (Flagship Cyber-Glass Stage Showcase) -->
    <section 
      class="relative isolate overflow-hidden rounded-3xl border border-zinc-800/90 bg-[#07090e] p-6 sm:p-10 lg:p-12 shadow-2xl min-h-[560px] flex flex-col justify-between"
      @mouseenter="stopSlideTimer"
      @mouseleave="startSlideTimer"
    >
      <!-- Dynamic Ambient Aura Mesh & Grid Overlay -->
      <div class="absolute inset-0 z-0 overflow-hidden pointer-events-none rounded-3xl">
        <!-- Tech Subtle Grid Pattern -->
        <div class="absolute inset-0 bg-[radial-gradient(#ffffff0d_1px,transparent_1px)] [background-size:24px_24px] opacity-40"></div>

        <!-- Dynamic Ambient Aura Glow in top right / center -->
        <div 
          class="absolute -top-24 right-0 lg:right-12 w-[420px] sm:w-[560px] h-[420px] sm:h-[560px] rounded-full blur-[110px] opacity-40 transition-all duration-1000 pointer-events-none"
          :class="currentSlide.themeGlow"
        ></div>

        <!-- Secondary subtle ambient glow in bottom left -->
        <div class="absolute -bottom-24 -left-24 w-80 h-80 rounded-full bg-blue-600/10 blur-[100px] pointer-events-none"></div>

        <!-- Soft Vignette & Border Light -->
        <div class="absolute inset-0 bg-gradient-to-t from-[#07090e] via-transparent to-transparent opacity-60"></div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-10 items-center relative z-10 w-full flex-1">
        <!-- Left: Dynamic Copy & Actions (7 cols) -->
        <div class="lg:col-span-7 space-y-6">
          <Transition name="hero-text-fade" mode="out-in">
            <div :key="currentSlide.id" class="space-y-5">
              <!-- Kicker Pill Badge with Pulsing LED Dot -->
              <div class="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-white/5 backdrop-blur-md border border-white/10 text-white text-xs font-semibold shadow-inner">
                <span class="w-2 h-2 rounded-full animate-ping" :class="currentSlide.accentDot"></span>
                <span>{{ currentSlide.kicker }}</span>
              </div>

              <!-- Main Dynamic H1 with Metallic Gradient Text -->
              <h1 class="text-3xl sm:text-4xl lg:text-5xl font-black text-white tracking-tight leading-[1.12]">
                <span>{{ currentSlide.headingLine1 }}</span> <br/>
                <span class="bg-clip-text text-transparent bg-gradient-to-r from-zinc-100 via-zinc-300 to-zinc-500 font-extrabold">
                  {{ currentSlide.headingLine2 }}
                </span>
              </h1>

              <!-- Dynamic Description -->
              <p class="text-zinc-300 text-xs sm:text-sm lg:text-base leading-relaxed max-w-xl font-normal">
                {{ currentSlide.description }}
              </p>

              <!-- Deal Highlight Pill & Price -->
              <div class="flex items-center flex-wrap gap-3 pt-1">
                <div class="text-2xl sm:text-3xl font-black text-white tracking-tight">
                  {{ formatCurrency(currentSlide.price) }}
                </div>
                <div class="flex items-center gap-2">
                  <span class="px-2.5 py-1 rounded-lg text-[11px] font-bold uppercase tracking-wider bg-red-600/90 text-white shadow-xs">
                    Trả góp 0%
                  </span>
                  <span class="px-2.5 py-1 rounded-lg text-[11px] font-semibold bg-white/10 border border-white/10 text-amber-300">
                    🔥 Giảm sốc online
                  </span>
                </div>
              </div>

              <!-- Dynamic Action Buttons -->
              <div class="flex flex-wrap items-center gap-3 pt-2">
                <router-link 
                  :to="currentSlide.link"
                  class="px-6 py-3 rounded-2xl font-bold text-xs sm:text-sm bg-white hover:bg-zinc-100 text-zinc-950 shadow-lg hover:shadow-white/20 transition-all active:scale-95 flex items-center gap-2 cursor-pointer group/btn"
                >
                  <span>{{ currentSlide.primaryCtaText }}</span>
                  <svg class="w-4 h-4 group-hover/btn:translate-x-1 transition-transform" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
                </router-link>

                <router-link 
                  :to="currentSlide.secondaryCtaLink"
                  class="px-5 py-3 rounded-2xl font-semibold text-xs sm:text-sm bg-white/5 hover:bg-white/10 text-zinc-200 border border-white/15 backdrop-blur-md transition-all active:scale-95 flex items-center gap-2 cursor-pointer"
                >
                  <span>{{ currentSlide.secondaryCtaText }}</span>
                </router-link>
              </div>
            </div>
          </Transition>

          <!-- Modern Glassmorphism Trust Badges -->
          <div class="pt-5 border-t border-white/10 flex flex-wrap items-center gap-2.5 sm:gap-3">
            <!-- 100% Chính Hãng -->
            <div class="inline-flex items-center gap-2 px-3 py-1.5 rounded-xl bg-white/5 border border-white/10 text-zinc-300 text-xs font-medium hover:bg-white/10 hover:border-emerald-500/40 transition-all duration-200">
              <span class="text-emerald-400">✓</span>
              <span>100% Chính hãng</span>
            </div>

            <!-- Giao hỏa tốc 2H -->
            <div class="inline-flex items-center gap-2 px-3 py-1.5 rounded-xl bg-white/5 border border-white/10 text-zinc-300 text-xs font-medium hover:bg-white/10 hover:border-blue-500/40 transition-all duration-200">
              <span class="text-blue-400">⚡</span>
              <span>Giao hỏa tốc 2H</span>
            </div>

            <!-- Bảo hành 12 tháng -->
            <div class="inline-flex items-center gap-2 px-3 py-1.5 rounded-xl bg-white/5 border border-white/10 text-zinc-300 text-xs font-medium hover:bg-white/10 hover:border-violet-500/40 transition-all duration-200">
              <span class="text-violet-400">🛡️</span>
              <span>Bảo hành 12 tháng</span>
            </div>

            <!-- Đổi trả 1-đổi-1 30 ngày -->
            <div class="inline-flex items-center gap-2 px-3 py-1.5 rounded-xl bg-white/5 border border-white/10 text-zinc-300 text-xs font-medium hover:bg-white/10 hover:border-amber-500/40 transition-all duration-200">
              <span class="text-amber-400">🔄</span>
              <span>Đổi mới 30 ngày</span>
            </div>
          </div>
        </div>

        <!-- Right: Flagship Floating Stage & Floating HUD Spec Cards (5 cols) -->
        <div class="lg:col-span-5 relative flex items-center justify-center min-h-[340px] sm:min-h-[400px]">
          <!-- Floating Pedestal Light Glow underneath product -->
          <div class="absolute bottom-6 w-60 sm:w-80 h-10 rounded-[100%] bg-white/10 blur-xl pointer-events-none"></div>

          <!-- Product Floating Image (Free-standing, borderless & majestic) -->
          <router-link 
            :to="currentSlide.link"
            class="relative z-10 block group cursor-pointer"
          >
            <Transition name="hero-product-zoom" mode="out-in">
              <div :key="currentSlide.id" class="relative flex items-center justify-center p-2">
                <img 
                  :src="currentSlide.image" 
                  :alt="currentSlide.title"
                  class="w-72 sm:w-88 lg:w-96 max-h-[320px] sm:max-h-[380px] object-contain drop-shadow-[0_20px_45px_rgba(0,0,0,0.85)] group-hover:scale-105 transition-transform duration-500 select-none animate-float-slow"
                />
              </div>
            </Transition>
          </router-link>

          <!-- Floating Spec HUD 1 (Top Left) -->
          <div 
            v-if="currentSlide.hudSpecs?.[0]"
            class="absolute top-2 left-0 sm:-left-3 z-20 px-3.5 py-2 rounded-2xl bg-zinc-900/85 backdrop-blur-md border border-white/15 shadow-xl flex items-center gap-3 transition-all duration-500 animate-hover-gentle pointer-events-none select-none"
          >
            <div class="w-8 h-8 rounded-xl bg-white/10 flex items-center justify-center text-white shrink-0">
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="4" y="4" width="16" height="16" rx="2"/><rect x="9" y="9" width="6" height="6"/><path d="M9 1v3M15 1v3M9 20v3M15 20v3M20 9h3M20 14h3M1 9h3M1 14h3"/></svg>
            </div>
            <div>
              <div class="text-[10px] text-zinc-400 font-medium">{{ currentSlide.hudSpecs[0].label }}</div>
              <div class="text-xs font-bold text-white">{{ currentSlide.hudSpecs[0].value }}</div>
            </div>
          </div>

          <!-- Floating Spec HUD 2 (Bottom Right) -->
          <div 
            v-if="currentSlide.hudSpecs?.[1]"
            class="absolute bottom-6 right-0 sm:-right-3 z-20 px-3.5 py-2 rounded-2xl bg-zinc-900/85 backdrop-blur-md border border-white/15 shadow-xl flex items-center gap-3 transition-all duration-500 animate-hover-gentle-delay pointer-events-none select-none"
          >
            <div class="w-8 h-8 rounded-xl bg-white/10 flex items-center justify-center text-white shrink-0">
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
            </div>
            <div>
              <div class="text-[10px] text-zinc-400 font-medium">{{ currentSlide.hudSpecs[1].label }}</div>
              <div class="text-xs font-bold text-white">{{ currentSlide.hudSpecs[1].value }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Bottom Controls: Interactive Product Tabs with Progress Timer & Arrows -->
      <div class="relative z-20 pt-6 mt-6 border-t border-white/10 flex flex-col sm:flex-row items-center justify-between gap-4">
        <!-- Tab Switcher with Progress Indicator -->
        <div class="flex items-center gap-2 overflow-x-auto w-full sm:w-auto pb-1 sm:pb-0 scrollbar-none">
          <button 
            v-for="(slide, idx) in featuredSlides" 
            :key="slide.id"
            @click="goToSlide(idx)"
            class="relative px-3.5 py-2 rounded-xl text-xs font-semibold transition-all shrink-0 cursor-pointer overflow-hidden group"
            :class="currentSlideIndex === idx 
              ? 'text-white bg-white/15 border border-white/25 shadow-sm' 
              : 'text-zinc-400 hover:text-white hover:bg-white/5 border border-transparent'"
          >
            <span>{{ slide.shortName || slide.title }}</span>

            <!-- Animated Progress Bar on Active Tab -->
            <div 
              v-if="currentSlideIndex === idx"
              class="absolute bottom-0 left-0 h-[2px] bg-white rounded-full transition-all duration-100 ease-linear"
              :style="{ width: `${slideProgress}%` }"
            ></div>
          </button>
        </div>

        <!-- Arrows & Slide Counter -->
        <div class="flex items-center gap-3 shrink-0 self-end sm:self-auto">
          <span class="text-xs font-mono font-medium text-zinc-400">
            0{{ currentSlideIndex + 1 }} / 0{{ featuredSlides.length }}
          </span>
          <div class="flex items-center gap-1.5">
            <button 
              @click="prevSlide"
              class="w-8 h-8 rounded-xl bg-white/10 hover:bg-white/20 border border-white/15 text-white flex items-center justify-center transition active:scale-95 cursor-pointer"
              title="Slide trước"
            >
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M15 18l-6-6 6-6"/></svg>
            </button>
            <button 
              @click="nextSlide"
              class="w-8 h-8 rounded-xl bg-white/10 hover:bg-white/20 border border-white/15 text-white flex items-center justify-center transition active:scale-95 cursor-pointer"
              title="Slide tiếp theo"
            >
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M9 18l6-6-6-6"/></svg>
            </button>
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
