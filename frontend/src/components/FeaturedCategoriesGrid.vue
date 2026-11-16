<template>
  <section class="bg-white rounded-2xl border border-zinc-200/80 p-5 sm:p-6 shadow-sm">
    <!-- Header with Badge, Title, Link and Carousel Controls -->
    <div class="flex flex-col sm:flex-row sm:items-end justify-between mb-5 sm:mb-6 gap-3 pb-3 sm:pb-4 border-b border-zinc-200/60">
      <div class="space-y-1 min-w-0">
        <div class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold tracking-wide border shadow-sm bg-blue-50 text-blue-700 border-blue-200/80 mb-1">
          <LayoutGrid class="w-3.5 h-3.5 text-blue-600" />
          <span>Danh Mục Thiết Bị</span>
        </div>
        <h2 class="text-xl sm:text-2xl font-bold tracking-tight text-zinc-900">
          Danh Mục Nổi Bật
        </h2>
        <p class="text-xs sm:text-sm text-zinc-500">
          Khám phá hệ sinh thái sản phẩm công nghệ chính hãng theo từng nhu cầu
        </p>
      </div>

      <!-- Right Actions: Link + Arrow buttons -->
      <div class="flex items-center gap-2 self-start sm:self-auto shrink-0">
        <router-link
          to="/products"
          class="hidden sm:inline-flex items-center gap-1.5 rounded-full border border-zinc-200/90 bg-white px-3.5 py-1.5 text-xs font-semibold text-zinc-700 shadow-sm hover:border-zinc-300 hover:text-red-600 hover:bg-zinc-50 transition-all"
        >
          <span>Tất cả thiết bị</span>
          <ArrowRight class="w-3.5 h-3.5" />
        </router-link>

        <div class="flex items-center gap-1.5">
          <button
            @click="scrollPrev"
            class="w-8 h-8 rounded-full border border-zinc-200 bg-white hover:bg-zinc-50 hover:border-zinc-300 flex items-center justify-center text-zinc-700 hover:text-zinc-950 shadow-sm transition-all active:scale-95 cursor-pointer"
            title="Lùi lại"
            aria-label="Danh mục trước"
          >
            <ChevronLeft class="w-4 h-4" />
          </button>
          <button
            @click="scrollNext"
            class="w-8 h-8 rounded-full border border-zinc-200 bg-white hover:bg-zinc-50 hover:border-zinc-300 flex items-center justify-center text-zinc-700 hover:text-zinc-950 shadow-sm transition-all active:scale-95 cursor-pointer"
            title="Tiếp theo"
            aria-label="Danh mục tiếp theo"
          >
            <ChevronRight class="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>

    <!-- Carousel Wrapper with Left/Right floating arrows -->
    <div class="relative group/carousel">
      <!-- Floating Prev Button (Desktop Hover) -->
      <button
        @click="scrollPrev"
        class="absolute -left-3.5 top-1/2 -translate-y-1/2 z-10 w-9 h-9 rounded-full bg-white/95 hover:bg-white text-zinc-800 border border-zinc-200/80 shadow-md hidden sm:flex items-center justify-center opacity-0 group-hover/carousel:opacity-100 transition-all duration-200 active:scale-90 cursor-pointer"
        aria-label="Lùi lại"
      >
        <ChevronLeft class="w-5 h-5" />
      </button>

      <!-- Floating Next Button (Desktop Hover) -->
      <button
        @click="scrollNext"
        class="absolute -right-3.5 top-1/2 -translate-y-1/2 z-10 w-9 h-9 rounded-full bg-white/95 hover:bg-white text-zinc-800 border border-zinc-200/80 shadow-md hidden sm:flex items-center justify-center opacity-0 group-hover/carousel:opacity-100 transition-all duration-200 active:scale-90 cursor-pointer"
        aria-label="Tiếp theo"
      >
        <ChevronRight class="w-5 h-5" />
      </button>

      <!-- 1 Row Carousel Track (Exact 5 items on desktop) -->
      <div
        ref="sliderRef"
        @mouseenter="stopAutoSlide"
        @mouseleave="startAutoSlide"
        class="flex gap-3 sm:gap-4 overflow-x-auto scrollbar-none scroll-smooth snap-x snap-mandatory py-2 px-1"
      >
        <router-link
          v-for="item in featuredCategories"
          :key="item.name"
          :to="item.link"
          class="w-[calc((100%-12px)/2)] sm:w-[calc((100%-32px)/3)] md:w-[calc((100%-48px)/4)] lg:w-[calc((100%-64px)/5)] shrink-0 snap-start group/card relative flex flex-col items-center justify-center p-4 sm:p-5 rounded-2xl bg-zinc-50/70 border border-zinc-200/60 hover:bg-white hover:border-zinc-300 hover:shadow-md hover:-translate-y-1 transition-all duration-300 text-center cursor-pointer select-none"
        >
          <!-- Thumbnail Image -->
          <div class="relative w-16 h-16 sm:w-20 sm:h-20 flex items-center justify-center mb-3 transition-transform duration-300 group-hover/card:scale-110 drop-shadow-sm">
            <img
              :src="item.img"
              :alt="item.name"
              class="max-w-full max-h-full object-contain"
              loading="lazy"
            />
          </div>

          <!-- Category Name -->
          <span class="text-xs sm:text-sm font-bold text-zinc-800 group-hover/card:text-red-600 transition-colors line-clamp-1">
            {{ item.name }}
          </span>

          <!-- Subtle hint -->
          <span v-if="item.hint" class="text-[11px] text-zinc-400 group-hover/card:text-zinc-600 font-medium transition-colors line-clamp-1 mt-0.5">
            {{ item.hint }}
          </span>
        </router-link>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { LayoutGrid, ChevronLeft, ChevronRight, ArrowRight } from 'lucide-vue-next'

interface FeaturedCategory {
  name: string
  img: string
  link: string
  hint?: string
}

const featuredCategories: FeaturedCategory[] = [
  { name: 'Điện thoại', img: '/categories/phone.png', link: '/products?categoryId=2', hint: 'iPhone, Galaxy...' },
  { name: 'Laptop & MacBook', img: '/categories/laptop.png', link: '/products?categoryId=1', hint: 'Gaming, AI, Office' },
  { name: 'Máy tính bảng', img: '/categories/tablet.png', link: '/products?categoryId=3', hint: 'iPad, Galaxy Tab' },
  { name: 'Màn hình máy tính', img: '/categories/monitor.png', link: '/products?keyword=Màn hình', hint: '2K, 4K, 144Hz' },
  { name: 'PC & Máy tính bàn', img: '/categories/pc.png', link: '/products?keyword=PC', hint: 'Gaming, Đồ họa' },
  { name: 'Phụ kiện công nghệ', img: '/categories/accessories.png', link: '/products?categoryId=5', hint: 'Sạc, Cáp, Hub' },
  { name: 'Đồng hồ thông minh', img: '/categories/smartwatch.png', link: '/products?keyword=Watch', hint: 'Apple Watch, Garmin' },
  { name: 'SIM & Data 4G/5G', img: '/categories/sim.png', link: '/products?keyword=SIM', hint: 'Gói cước data khủng' },
  { name: 'Tivi & Màn chiếu', img: '/categories/tv.png', link: '/products?keyword=Tivi', hint: 'OLED, 4K Smart TV' },
  { name: 'Máy lạnh - Điều hòa', img: '/categories/aircon.png', link: '/products?keyword=Điều hòa', hint: 'Inverter tiết kiệm điện' },
  { name: 'Máy lọc nước', img: '/categories/water_purifier.png', link: '/products?keyword=Máy lọc nước', hint: 'Công nghệ RO/Nano' },
  { name: 'Robot hút bụi', img: '/categories/robot_vacuum.png', link: '/products?keyword=Robot hút bụi', hint: 'Tự động giặt sấy lau' },
  { name: 'Tủ lạnh & Tủ đông', img: '/categories/fridge.png', link: '/products?keyword=Tủ lạnh', hint: 'Side by side, Multidoor' },
  { name: 'Quạt điều hòa', img: '/categories/cooler_fan.png', link: '/products?keyword=Quạt điều hòa', hint: 'Làm mát cấp tốc' },
  { name: 'Máy giặt & Sấy', img: '/categories/washing_machine.png', link: '/products?keyword=Máy giặt', hint: 'Truyền động trực tiếp' },
  { name: 'Phần mềm bản quyền', img: '/categories/software.png', link: '/products?keyword=Phần mềm', hint: 'Windows, Office 365' }
]

const sliderRef = ref<HTMLElement | null>(null)
let autoSlideTimer: ReturnType<typeof setInterval> | null = null

const getScrollStep = (): number => {
  if (!sliderRef.value) return 240
  const firstChild = sliderRef.value.firstElementChild as HTMLElement
  if (firstChild) {
    const gap = 16 // gap-4 = 1rem = 16px
    return firstChild.offsetWidth + gap
  }
  return sliderRef.value.clientWidth / 5
}

const scrollNext = () => {
  if (!sliderRef.value) return
  const { scrollLeft, scrollWidth, clientWidth } = sliderRef.value
  const maxScroll = scrollWidth - clientWidth
  const step = getScrollStep()

  // Loop back smoothly if reached end
  if (scrollLeft >= maxScroll - 10) {
    sliderRef.value.scrollTo({ left: 0, behavior: 'smooth' })
  } else {
    sliderRef.value.scrollBy({ left: step, behavior: 'smooth' })
  }
}

const scrollPrev = () => {
  if (!sliderRef.value) return
  const { scrollLeft, scrollWidth, clientWidth } = sliderRef.value
  const maxScroll = scrollWidth - clientWidth
  const step = getScrollStep()

  if (scrollLeft <= 10) {
    sliderRef.value.scrollTo({ left: maxScroll, behavior: 'smooth' })
  } else {
    sliderRef.value.scrollBy({ left: -step, behavior: 'smooth' })
  }
}

const startAutoSlide = () => {
  stopAutoSlide()
  autoSlideTimer = setInterval(() => {
    scrollNext()
  }, 3500)
}

const stopAutoSlide = () => {
  if (autoSlideTimer) {
    clearInterval(autoSlideTimer)
    autoSlideTimer = null
  }
}

onMounted(() => {
  startAutoSlide()
})

onUnmounted(() => {
  stopAutoSlide()
})
</script>
