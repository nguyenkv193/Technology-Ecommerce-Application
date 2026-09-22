<template>
  <div class="space-y-12 sm:space-y-16">
    <!-- Hero Banner (Enterprise Minimalist Style với Flagship Showcase Slide) -->
    <section class="relative isolate overflow-hidden rounded-3xl border border-zinc-200/80 p-8 sm:p-10 lg:p-14 shadow-xs min-h-[520px] flex items-center">
      <!-- Dynamic Product Background with Frosted Glass Blur Overlay -->
      <div class="absolute inset-0 z-0 overflow-hidden pointer-events-none rounded-3xl">
        <Transition name="hero-bg-crossfade">
          <img 
            :key="currentSlide.id"
            :src="currentSlide.image" 
            :alt="currentSlide.title"
            class="absolute inset-0 w-full h-full object-cover object-center filter blur-[8px] scale-110"
          />
        </Transition>

        <!-- Frosted Glass Protective Layer ("Hiệu ứng phủ mờ") -->
        <div class="absolute inset-0 bg-white/40 backdrop-blur-sm"></div>
        <div class="absolute inset-0 bg-gradient-to-r from-white/80 via-white/50 to-transparent"></div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 lg:gap-12 items-center relative z-10 w-full">
        <!-- Left: Dynamic Copy & Actions (7 cols) -->
        <div class="lg:col-span-7 space-y-6">
          <Transition name="hero-text-fade" mode="out-in">
            <div :key="currentSlide.id" class="space-y-6">
              <!-- Kicker Pill Badge -->
              <div class="inline-flex items-center gap-2 px-3.5 py-1 rounded-full bg-white/90 backdrop-blur-md border border-zinc-200/80 text-zinc-800 text-xs font-semibold shadow-2xs">
                <span class="w-1.5 h-1.5 rounded-full bg-zinc-900 animate-pulse"></span>
                <span>{{ currentSlide.kicker }}</span>
              </div>

              <!-- Main Dynamic H1 -->
              <h1 class="text-3xl sm:text-4xl lg:text-5xl font-extrabold text-zinc-950 tracking-tight leading-[1.15]">
                {{ currentSlide.headingLine1 }} <br/>
                <span class="text-zinc-500">{{ currentSlide.headingLine2 }}</span>
              </h1>

              <!-- Dynamic Description -->
              <p class="text-zinc-600 text-sm sm:text-base leading-relaxed max-w-xl">
                {{ currentSlide.description }}
              </p>

              <!-- Dynamic Action Buttons -->
              <div class="flex flex-wrap gap-3 pt-1">
                <router-link 
                  :to="currentSlide.link"
                  class="px-5 py-2.5 rounded-xl font-medium text-xs sm:text-sm bg-zinc-900 hover:bg-black text-white shadow-xs transition-all active:scale-95 flex items-center gap-2"
                >
                  <span>{{ currentSlide.primaryCtaText }}</span>
                  <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
                </router-link>

                <router-link 
                  :to="currentSlide.secondaryCtaLink"
                  class="px-5 py-2.5 rounded-xl font-medium text-xs sm:text-sm bg-white/90 hover:bg-white text-zinc-800 border border-zinc-200/80 shadow-2xs transition flex items-center gap-2"
                >
                  <span>{{ currentSlide.secondaryCtaText }}</span>
                </router-link>
              </div>
            </div>
          </Transition>

          <!-- Redesigned Trust Badges (Modern Luxury Pill Badges) -->
          <div class="pt-5 border-t border-zinc-200/70 flex flex-wrap items-center gap-2.5 sm:gap-3">
            <!-- 100% Chính Hãng -->
            <div class="inline-flex items-center gap-2 px-3 py-1.5 rounded-xl bg-white/90 backdrop-blur-md border border-zinc-200/80 shadow-2xs hover:border-emerald-300 hover:shadow-xs transition-all duration-200 group/b">
              <div class="w-5 h-5 rounded-lg bg-emerald-50 text-emerald-600 flex items-center justify-center shrink-0 group-hover/b:scale-110 transition-transform">
                <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
              </div>
              <span class="text-xs font-semibold text-zinc-800">100% Chính hãng</span>
            </div>

            <!-- Giao hỏa tốc 2H -->
            <div class="inline-flex items-center gap-2 px-3 py-1.5 rounded-xl bg-white/90 backdrop-blur-md border border-zinc-200/80 shadow-2xs hover:border-blue-300 hover:shadow-xs transition-all duration-200 group/b">
              <div class="w-5 h-5 rounded-lg bg-blue-50 text-blue-600 flex items-center justify-center shrink-0 group-hover/b:scale-110 transition-transform">
                <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
              </div>
              <span class="text-xs font-semibold text-zinc-800">Giao hỏa tốc 2H</span>
            </div>

            <!-- Bảo hành 12 tháng -->
            <div class="inline-flex items-center gap-2 px-3 py-1.5 rounded-xl bg-white/90 backdrop-blur-md border border-zinc-200/80 shadow-2xs hover:border-violet-300 hover:shadow-xs transition-all duration-200 group/b">
              <div class="w-5 h-5 rounded-lg bg-violet-50 text-violet-600 flex items-center justify-center shrink-0 group-hover/b:scale-110 transition-transform">
                <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/><polyline points="22,6 12,13 2,6"/></svg>
              </div>
              <span class="text-xs font-semibold text-zinc-800">Bảo hành 12 tháng</span>
            </div>

            <!-- Đổi trả 1-đổi-1 30 ngày -->
            <div class="inline-flex items-center gap-2 px-3 py-1.5 rounded-xl bg-white/90 backdrop-blur-md border border-zinc-200/80 shadow-2xs hover:border-amber-300 hover:shadow-xs transition-all duration-200 group/b">
              <div class="w-5 h-5 rounded-lg bg-amber-50 text-amber-600 flex items-center justify-center shrink-0 group-hover/b:scale-110 transition-transform">
                <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M20 12V8H6a2 2 0 0 1-2-2c0-1.1.9-2 2-2h12v4"/><path d="M4 6v12c0 1.1.9 2 2 2h14v-4"/><circle cx="18" cy="12" r="1"/></svg>
              </div>
              <span class="text-xs font-semibold text-zinc-800">Đổi mới 30 ngày</span>
            </div>
          </div>
        </div>

        <!-- Right: Interactive Flagship Showcase (5 cols) -->
        <div 
          class="lg:col-span-5 relative flex flex-col items-center justify-center pt-2 lg:pt-0"
          @mouseenter="stopSlideTimer"
          @mouseleave="startSlideTimer"
        >
          <!-- Soft Ambient Glow -->
          <div class="absolute -top-6 -right-6 w-64 h-64 rounded-full bg-gradient-to-br from-zinc-200 to-zinc-100/20 blur-3xl pointer-events-none -z-0"></div>

          <!-- Showcase Card with Fade-Slide Transition -->
          <div class="relative z-10 w-full max-w-sm">
            <Transition name="fade-slide" mode="out-in">
              <div 
                :key="currentSlide.id"
                class="rounded-2xl bg-white/95 backdrop-blur-md border border-zinc-200/80 p-5 shadow-lg hover:shadow-xl transition-all duration-300 flex flex-col justify-between"
              >
                <!-- Top Header on Card -->
                <div class="flex items-center justify-between gap-2 mb-3">
                  <span class="text-[10px] font-semibold uppercase tracking-wider px-2 py-0.5 rounded-md bg-zinc-100 text-zinc-700 border border-zinc-200/80">
                    {{ currentSlide.tag }}
                  </span>
                  <span class="inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-[10px] font-semibold bg-zinc-900 text-white shadow-2xs">
                    ★ {{ currentSlide.badge }}
                  </span>
                </div>

                <!-- Product Showcase Image Canvas -->
                <router-link 
                  :to="currentSlide.link"
                  class="relative aspect-[4/3] rounded-xl bg-[#f6f6f8] flex items-center justify-center p-4 overflow-hidden group cursor-pointer"
                >
                  <img 
                    :src="currentSlide.image" 
                    :alt="currentSlide.title"
                    class="object-contain max-h-full max-w-full group-hover:scale-105 transition-transform duration-500 drop-shadow-sm"
                  />
                </router-link>

                <!-- Spec Chips -->
                <div class="flex flex-wrap gap-1.5 mt-3 mb-2">
                  <span 
                    v-for="(spec, sIdx) in currentSlide.specs" 
                    :key="sIdx"
                    class="text-[10px] text-zinc-600 bg-zinc-100/90 px-2 py-0.5 rounded-md font-medium border border-zinc-200/60"
                  >
                    {{ spec }}
                  </span>
                </div>

                <!-- Product Info & Direct CTA -->
                <div class="mt-2 pt-3 border-t border-zinc-100 flex items-center justify-between gap-3">
                  <div class="min-w-0">
                    <h3 class="font-bold text-zinc-900 text-sm truncate">
                      {{ currentSlide.title }}
                    </h3>
                    <div class="text-xs font-bold text-zinc-900 mt-0.5">
                      {{ formatCurrency(currentSlide.price) }}
                    </div>
                  </div>

                  <router-link 
                    :to="currentSlide.link"
                    class="shrink-0 px-3.5 py-1.5 rounded-lg text-xs font-medium bg-zinc-900 hover:bg-black text-white transition-all active:scale-95 flex items-center gap-1 shadow-2xs"
                  >
                    <span>Xem ngay</span>
                    <svg class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
                  </router-link>
                </div>
              </div>
            </Transition>

            <!-- Slide Navigation Controls -->
            <div class="flex items-center justify-between mt-3 px-1">
              <!-- Dots -->
              <div class="flex items-center gap-1.5">
                <button 
                  v-for="(_, idx) in featuredSlides" 
                  :key="idx"
                  @click="goToSlide(idx)"
                  class="h-1.5 rounded-full transition-all duration-300"
                  :class="currentSlideIndex === idx ? 'w-6 bg-zinc-900' : 'w-2 bg-zinc-300 hover:bg-zinc-400'"
                  :title="`Slide ${idx + 1}`"
                ></button>
              </div>

              <!-- Previous / Next Arrows -->
              <div class="flex items-center gap-1">
                <button 
                  @click="prevSlide"
                  class="p-1 rounded-lg bg-white/80 hover:bg-white border border-zinc-200 text-zinc-600 hover:text-zinc-900 transition shadow-2xs"
                  title="Trước"
                >
                  <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M15 18l-6-6 6-6"/></svg>
                </button>
                <button 
                  @click="nextSlide"
                  class="p-1 rounded-lg bg-white/80 hover:bg-white border border-zinc-200 text-zinc-600 hover:text-zinc-900 transition shadow-2xs"
                  title="Tiếp theo"
                >
                  <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M9 18l6-6-6-6"/></svg>
                </button>
              </div>
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
      subtitle="Các sản phẩm được quan tâm và đánh giá cao nhất"
      badge="Nổi bật"
      :items="popularProducts"
      :loading="loadingPopular"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import RecommendationCarousel from '@/components/RecommendationCarousel.vue'
import FeaturedCategoriesGrid from '@/components/FeaturedCategoriesGrid.vue'
import { aiApi } from '@/api/aiApi'
import { catalogApi } from '@/api/catalogApi'
import { useAuthStore } from '@/stores/auth'
import type { Product, AIRecommendationItem } from '@/types'

const authStore = useAuthStore()

type EnrichedProduct = Product & { reason?: string; aiScore?: number }

const personalizedProducts = ref<EnrichedProduct[]>([])
const popularProducts = ref<EnrichedProduct[]>([])
const loadingPersonalized = ref<boolean>(false)
const loadingPopular = ref<boolean>(false)

// Flagship Showcase Slides
interface FeaturedSlide {
  id: number
  tag: string
  badge: string
  title: string
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
}

const featuredSlides = ref<FeaturedSlide[]>([
  {
    id: 3,
    tag: 'Apple Silicon M3',
    badge: 'Flagship Laptop',
    title: 'MacBook Pro 14 M3 Pro',
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
    secondaryCtaLink: '/products?categoryId=1'
  },
  {
    id: 1,
    tag: 'Titanium Design',
    badge: 'Smartphone Đỉnh Cao',
    title: 'iPhone 16 Pro Max 256GB',
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
    secondaryCtaLink: '/products?categoryId=2'
  },
  {
    id: 5,
    tag: 'AI Workstation & Gaming',
    badge: 'Siêu Cấu Hình',
    title: 'ASUS ROG Zephyrus G16 OLED',
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
    secondaryCtaLink: '/products?categoryId=1'
  },
  {
    id: 7,
    tag: 'Chống Ồn Huyền Thoại',
    badge: 'Hi-Res Audio',
    title: 'Sony WH-1000XM5 Wireless',
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
    secondaryCtaLink: '/products?categoryId=4'
  }
])

const currentSlideIndex = ref<number>(0)
let slideTimer: number | null = null

const currentSlide = computed(() => {
  return featuredSlides.value[currentSlideIndex.value] || featuredSlides.value[0]
})

const nextSlide = () => {
  currentSlideIndex.value = (currentSlideIndex.value + 1) % featuredSlides.value.length
}

const prevSlide = () => {
  currentSlideIndex.value = (currentSlideIndex.value - 1 + featuredSlides.value.length) % featuredSlides.value.length
}

const goToSlide = (idx: number) => {
  currentSlideIndex.value = idx
}

const startSlideTimer = () => {
  stopSlideTimer()
  slideTimer = window.setInterval(() => {
    nextSlide()
  }, 4500)
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


const enrichRecommendations = async (recList: AIRecommendationItem[]): Promise<EnrichedProduct[]> => {
  const enriched: EnrichedProduct[] = []
  for (const item of recList) {
    try {
      const res = await catalogApi.getProductById(item.product_id)
      if (res.success && res.data) {
        const pData = res.data
        const thumb = pData.thumbnailUrl || 
          pData.images?.find((img: any) => img.isThumbnail)?.url || 
          pData.images?.[0]?.url || 
          ''
        enriched.push({
          ...pData,
          thumbnailUrl: thumb,
          brandName: pData.brandName || pData.brand?.name,
          categoryName: pData.categoryName || pData.category?.name,
          aiScore: item.score
        })
      }
    } catch {
      enriched.push({
        id: item.product_id,
        name: `Thiết bị công nghệ #${item.product_id}`,
        slug: `product-${item.product_id}`,
        description: '',
        shortDescription: '',
        thumbnailUrl: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500&auto=format&fit=crop&q=60',
        active: true,
        minPrice: 24990000,
        brandName: 'Tech Brand',
        categoryName: 'Sản phẩm nổi bật',
        aiScore: item.score
      })
    }
  }
  return enriched
}

const loadPopular = async () => {
  loadingPopular.value = true
  try {
    const res = await aiApi.getPopular(4)
    if (res.recommendations) {
      popularProducts.value = await enrichRecommendations(res.recommendations)
    }
  } catch (e) {
    console.error('Không thể tải sản phẩm thịnh hành:', e)
  } finally {
    loadingPopular.value = false
  }
}

const loadPersonalized = async () => {
  loadingPersonalized.value = true
  try {
    const userId = authStore.user?.id || 1
    const res = await aiApi.getUserRecommendations(userId, 4)
    if (res.recommendations) {
      personalizedProducts.value = await enrichRecommendations(res.recommendations)
    }
  } catch (e) {
    console.error('Không thể tải gợi ý cá nhân hóa:', e)
  } finally {
    loadingPersonalized.value = false
  }
}

onMounted(() => {
  loadPopular()
  loadPersonalized()
  startSlideTimer()
})

onUnmounted(() => {
  stopSlideTimer()
})
</script>

<style scoped>
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
</style>
