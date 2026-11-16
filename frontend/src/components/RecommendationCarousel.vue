<template>
  <section :aria-label="title" :aria-busy="loading" class="py-6 sm:py-8">
    <div :class="featuredHeader ? 'collection-header' : 'flex flex-col sm:flex-row sm:items-end justify-between mb-6 gap-3 pb-3 sm:pb-4 border-b border-zinc-200/60'">
      <div class="min-w-0">
        <div v-if="badge" :class="featuredHeader ? 'mb-3' : 'flex items-center gap-2 mb-2'">
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
        <h2 :class="featuredHeader ? 'text-2xl sm:text-3xl font-bold tracking-tight leading-tight text-zinc-950' : 'text-xl sm:text-2xl font-bold tracking-tight text-zinc-900'">
          {{ title }}
        </h2>
        <p v-if="subtitle" :class="featuredHeader ? 'mt-2 max-w-xl text-sm leading-relaxed text-zinc-600' : 'text-xs sm:text-sm text-zinc-500 mt-1'">
          {{ subtitle }}
        </p>
      </div>

      <slot name="action"></slot>
    </div>

    <!-- Skeleton Loading -->
    <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-5">
      <div v-for="i in 4" :key="i" class="bg-white border border-zinc-200/80 rounded-xl p-4 animate-pulse space-y-3">
        <div class="bg-zinc-100 rounded-lg aspect-square"></div>
        <div class="h-3.5 bg-zinc-100 rounded w-3/4"></div>
        <div class="h-3 bg-zinc-100 rounded w-1/2"></div>
      </div>
    </div>

    <div v-else-if="errorMessage" role="alert" class="p-6 sm:p-8 bg-white rounded-xl border border-zinc-200/80 text-center space-y-3">
      <p class="text-sm text-zinc-600">{{ errorMessage }}</p>
      <button type="button" @click="$emit('retry')" class="px-4 py-2 rounded-lg border border-zinc-300 text-xs font-semibold text-zinc-800 hover:bg-zinc-50 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-zinc-900 focus-visible:ring-offset-2">
        Thử lại
      </button>
    </div>

    <!-- Empty State -->
    <div v-else-if="!items || items.length === 0" class="p-8 text-center bg-white rounded-xl border border-zinc-200/80 text-zinc-400 text-xs">
      {{ emptyMessage }}
    </div>

    <!-- Products Grid -->
    <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-5">
      <ProductCard 
        v-for="item in items" 
        :key="item.id"
        :product="item"
        :reason="item.reason"
      />
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import ProductCard from './ProductCard.vue'
import { PackagePlus, Sparkles, Flame, Zap, TrendingUp } from 'lucide-vue-next'
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
