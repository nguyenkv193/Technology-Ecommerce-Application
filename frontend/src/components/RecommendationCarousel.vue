<template>
  <section :aria-label="title" :aria-busy="loading" class="py-6 sm:py-8">
    <!-- Header with Badge, Title, Subtitle, Action and Carousel Controls -->
    <div :class="featuredHeader ? 'collection-header' : 'flex flex-col sm:flex-row sm:items-end justify-between mb-5 sm:mb-6 gap-3 pb-3 sm:pb-4 border-b border-zinc-200/60'">
      <div class="space-y-1 min-w-0">
        <!-- Themed Badge -->
        <div v-if="badge" :class="featuredHeader ? 'mb-3' : 'flex items-center gap-2 mb-1.5'">
          <span
            :class="featuredHeader 
              ? 'collection-kicker' 
              : ['inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold tracking-wide border shadow-sm transition-colors', badgeTheme.badgeClass]"
          >
            <PackagePlus v-if="featuredHeader" class="h-3.5 w-3.5" :stroke-width="1.8" aria-hidden="true" />
            <component :is="badgeTheme.icon" v-else :class="badgeTheme.iconClass" :stroke-width="2" aria-hidden="true" />
            <span>{{ badge }}</span>
          </span>
        </div>

        <!-- Section Title -->
        <h2 :class="featuredHeader ? 'text-2xl sm:text-3xl font-bold tracking-tight leading-tight text-zinc-950' : 'text-xl sm:text-2xl font-bold tracking-tight text-zinc-900'">
          {{ title }}
        </h2>

        <!-- Subtitle -->
        <p v-if="subtitle" :class="featuredHeader ? 'mt-2 max-w-xl text-sm leading-relaxed text-zinc-600' : 'text-xs sm:text-sm text-zinc-500 mt-0.5'">
          {{ subtitle }}
        </p>
      </div>

      <!-- Right Actions: Slot Action (e.g. Xem tất cả) + Arrow Controls -->
      <div class="flex items-center gap-2.5 self-start sm:self-auto shrink-0">
        <slot name="action"></slot>

        <!-- Header Next/Prev buttons -->
        <div v-if="items && items.length > 5" class="flex items-center gap-1.5">
          <button
            @click="scrollPrev"
            class="w-8 h-8 rounded-full border border-zinc-200 bg-white hover:bg-zinc-50 hover:border-zinc-300 flex items-center justify-center text-zinc-700 hover:text-zinc-950 shadow-sm transition-all active:scale-95 cursor-pointer disabled:opacity-30 disabled:pointer-events-none"
            title="Lùi lại"
            aria-label="Sản phẩm trước"
          >
            <ChevronLeft class="w-4 h-4" />
          </button>
          <button
            @click="scrollNext"
            class="w-8 h-8 rounded-full border border-zinc-200 bg-white hover:bg-zinc-50 hover:border-zinc-300 flex items-center justify-center text-zinc-700 hover:text-zinc-950 shadow-sm transition-all active:scale-95 cursor-pointer disabled:opacity-30 disabled:pointer-events-none"
            title="Tiếp theo"
            aria-label="Sản phẩm tiếp theo"
          >
            <ChevronRight class="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>

    <!-- Skeleton Loading (1 Row of 5 cards) -->
    <div v-if="loading" class="flex gap-4 overflow-hidden py-2 px-1">
      <div
        v-for="i in 5"
        :key="i"
        class="w-[calc((100%-16px)/2)] sm:w-[calc((100%-32px)/3)] md:w-[calc((100%-48px)/4)] lg:w-[calc((100%-64px)/5)] shrink-0 bg-white border border-zinc-200/80 rounded-xl p-4 animate-pulse space-y-3"
      >
        <div class="bg-zinc-100 rounded-lg aspect-square"></div>
        <div class="h-3.5 bg-zinc-100 rounded w-3/4"></div>
        <div class="h-3 bg-zinc-100 rounded w-1/2"></div>
      </div>
    </div>

    <!-- Error Message -->
    <div v-else-if="errorMessage" role="alert" class="p-6 sm:p-8 bg-white rounded-xl border border-zinc-200/80 text-center space-y-3">
      <p class="text-sm text-zinc-600">{{ errorMessage }}</p>
      <button
        type="button"
        @click="$emit('retry')"
        class="px-4 py-2 rounded-lg border border-zinc-300 text-xs font-semibold text-zinc-800 hover:bg-zinc-50 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-zinc-900 focus-visible:ring-offset-2 cursor-pointer"
      >
        Thử lại
      </button>
    </div>

    <!-- Empty State -->
    <div v-else-if="!items || items.length === 0" class="p-8 text-center bg-white rounded-xl border border-zinc-200/80 text-zinc-400 text-xs">
      {{ emptyMessage }}
    </div>

    <!-- Products Carousel (1 Row, Exact 5 columns on desktop) -->
    <div v-else class="relative group/shelf">
      <!-- Floating Prev Button (Left) -->
      <button
        v-if="items.length > 5"
        @click="scrollPrev"
        class="absolute -left-3.5 sm:-left-4 top-1/2 -translate-y-1/2 z-20 w-9 h-9 sm:w-10 sm:h-10 rounded-full bg-white/95 hover:bg-white text-zinc-800 border border-zinc-200/90 shadow-md hidden sm:flex items-center justify-center opacity-0 group-hover/shelf:opacity-100 transition-all duration-200 active:scale-90 cursor-pointer"
        aria-label="Sản phẩm trước"
      >
        <ChevronLeft class="w-5 h-5" />
      </button>

      <!-- Floating Next Button (Right) -->
      <button
        v-if="items.length > 5"
        @click="scrollNext"
        class="absolute -right-3.5 sm:-right-4 top-1/2 -translate-y-1/2 z-20 w-9 h-9 sm:w-10 sm:h-10 rounded-full bg-white/95 hover:bg-white text-zinc-800 border border-zinc-200/90 shadow-md hidden sm:flex items-center justify-center opacity-0 group-hover/shelf:opacity-100 transition-all duration-200 active:scale-90 cursor-pointer"
        aria-label="Sản phẩm tiếp theo"
      >
        <ChevronRight class="w-5 h-5" />
      </button>

      <!-- Scrollable Track: 1 row, 5 items on lg, with smooth snapping -->
      <div
        ref="sliderRef"
        @mouseenter="stopAutoSlide"
        @mouseleave="startAutoSlide"
        class="flex gap-4 overflow-x-auto scrollbar-none scroll-smooth snap-x snap-mandatory py-2 px-1"
      >
        <div
          v-for="item in items"
          :key="item.id"
          class="w-[calc((100%-16px)/2)] sm:w-[calc((100%-32px)/3)] md:w-[calc((100%-48px)/4)] lg:w-[calc((100%-64px)/5)] shrink-0 snap-start flex flex-col"
        >
          <ProductCard 
            class="h-full"
            :product="item"
            :reason="item.reason"
          />
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick, onMounted, onUnmounted } from 'vue'
import ProductCard from './ProductCard.vue'
import { PackagePlus, Sparkles, Flame, Zap, TrendingUp, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import type { Product } from '@/types'

defineEmits<{ retry: [] }>()

const props = withDefaults(defineProps<{
  title: string
  subtitle?: string
  badge?: string
  featuredHeader?: boolean
  items?: (Product & { reason?: string })[]
  loading?: boolean
  emptyMessage?: string
  errorMessage?: string
}>(), {
  subtitle: '',
  badge: '',
  featuredHeader: false,
  items: () => [],
  loading: false,
  emptyMessage: 'Đang cập nhật các gợi ý sản phẩm công nghệ phù hợp...',
  errorMessage: ''
})

const badgeTheme = computed(() => {
  const text = (props.badge || '').toLowerCase().trim()
  if (text.includes('dành cho bạn') || text.includes('ai') || text.includes('gợi ý')) {
    return {
      icon: Sparkles,
      iconClass: 'w-3.5 h-3.5 text-violet-600',
      badgeClass: 'bg-violet-50 text-violet-700 border-violet-200/80 hover:bg-violet-100/60'
    }
  }
  if (text.includes('nổi bật') || text.includes('bán chạy') || text.includes('hot')) {
    return {
      icon: Flame,
      iconClass: 'w-3.5 h-3.5 text-red-500 fill-red-500/20',
      badgeClass: 'bg-red-50 text-red-600 border-red-200/80 hover:bg-red-100/60'
    }
  }
  if (text.includes('mới') || text.includes('new')) {
    return {
      icon: Zap,
      iconClass: 'w-3.5 h-3.5 text-amber-600 fill-amber-500/20',
      badgeClass: 'bg-amber-50 text-amber-700 border-amber-200/80 hover:bg-amber-100/60'
    }
  }
  return {
    icon: TrendingUp,
    iconClass: 'w-3.5 h-3.5 text-zinc-600',
    badgeClass: 'bg-zinc-100 text-zinc-700 border-zinc-200/80 hover:bg-zinc-200/60'
  }
})

const sliderRef = ref<HTMLElement | null>(null)
let autoSlideTimer: ReturnType<typeof setInterval> | null = null

const getScrollStep = (): number => {
  if (!sliderRef.value) return 260
  const firstChild = sliderRef.value.firstElementChild as HTMLElement
  if (firstChild) {
    const gap = 16 // gap-4 = 16px
    return firstChild.offsetWidth + gap
  }
  return sliderRef.value.clientWidth / 5
}

const scrollNext = () => {
  if (!sliderRef.value) return
  const { scrollLeft, scrollWidth, clientWidth } = sliderRef.value
  const maxScroll = scrollWidth - clientWidth
  if (maxScroll <= 5) return

  const step = getScrollStep()
  // Loop back smoothly if reached end
  if (scrollLeft >= maxScroll - 15) {
    sliderRef.value.scrollTo({ left: 0, behavior: 'smooth' })
  } else {
    sliderRef.value.scrollBy({ left: step, behavior: 'smooth' })
  }
}

const scrollPrev = () => {
  if (!sliderRef.value) return
  const { scrollLeft, scrollWidth, clientWidth } = sliderRef.value
  const maxScroll = scrollWidth - clientWidth
  if (maxScroll <= 5) return

  const step = getScrollStep()
  if (scrollLeft <= 15) {
    sliderRef.value.scrollTo({ left: maxScroll, behavior: 'smooth' })
  } else {
    sliderRef.value.scrollBy({ left: -step, behavior: 'smooth' })
  }
}

const startAutoSlide = () => {
  stopAutoSlide()
  if (props.items && props.items.length > 4) {
    autoSlideTimer = setInterval(() => {
      scrollNext()
    }, 3500)
  }
}

const stopAutoSlide = () => {
  if (autoSlideTimer) {
    clearInterval(autoSlideTimer)
    autoSlideTimer = null
  }
}

watch(
  [() => props.loading, () => props.items],
  async ([isLoading, items]) => {
    if (!isLoading && items && items.length > 4) {
      await nextTick()
      startAutoSlide()
    }
  },
  { immediate: true, deep: true }
)

onMounted(() => {
  startAutoSlide()
})

onUnmounted(() => {
  stopAutoSlide()
})
</script>

<style scoped>
.collection-header {
  @apply relative mb-6 flex flex-col justify-between gap-5 rounded-xl border border-zinc-200/80 bg-white p-5 sm:flex-row sm:items-center sm:gap-8 sm:p-7;
}

.collection-header::before {
  content: '';
  @apply absolute left-0 top-7 bottom-7 w-[3px] rounded-r-full bg-emerald-600;
}

.collection-kicker {
  @apply inline-flex items-center gap-2 rounded-full bg-emerald-50 px-3 py-1.5 text-[10px] font-semibold uppercase tracking-widest text-emerald-800;
}
</style>
