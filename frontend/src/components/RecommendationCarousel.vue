<template>
  <section :aria-label="title" :aria-busy="loading" class="py-6 sm:py-8">
    <div :class="featuredHeader ? 'collection-header' : 'flex flex-col sm:flex-row sm:items-end justify-between mb-6 gap-3 pb-3 border-b border-zinc-200/60'">
      <div class="min-w-0">
        <div v-if="badge" :class="featuredHeader ? 'mb-3' : 'flex items-center gap-2 mb-1.5'">
          <span :class="featuredHeader ? 'collection-kicker' : 'inline-flex items-center gap-1.5 px-2 py-0.5 rounded-md text-[10px] font-semibold uppercase tracking-wider bg-zinc-100 text-zinc-600 border border-zinc-200'">
            <PackagePlus v-if="featuredHeader" class="h-3.5 w-3.5" :stroke-width="1.8" aria-hidden="true" />
            <svg v-else aria-hidden="true" class="w-3 h-3 text-zinc-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
            {{ badge }}
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
      <div v-for="i in 4" :key="i" class="bg-white border border-zinc-200/80 rounded-2xl p-4 animate-pulse space-y-3">
        <div class="bg-zinc-100 rounded-xl aspect-square"></div>
        <div class="h-3.5 bg-zinc-100 rounded w-3/4"></div>
        <div class="h-3 bg-zinc-100 rounded w-1/2"></div>
      </div>
    </div>

    <div v-else-if="errorMessage" role="alert" class="p-6 sm:p-8 bg-white rounded-2xl border border-zinc-200/80 text-center space-y-3">
      <p class="text-sm text-zinc-600">{{ errorMessage }}</p>
      <button type="button" @click="$emit('retry')" class="px-4 py-2 rounded-lg border border-zinc-300 text-xs font-semibold text-zinc-800 hover:bg-zinc-50 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-zinc-900 focus-visible:ring-offset-2">
        Thử lại
      </button>
    </div>

    <!-- Empty State -->
    <div v-else-if="!items || items.length === 0" class="p-8 text-center bg-white rounded-2xl border border-zinc-200/80 text-zinc-400 text-xs">
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
import ProductCard from './ProductCard.vue'
import { PackagePlus } from 'lucide-vue-next'
import type { Product } from '@/types'

defineEmits<{ retry: [] }>()

withDefaults(defineProps<{
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
</script>

<style scoped>
.collection-header {
  @apply relative mb-6 flex flex-col justify-between gap-5 rounded-2xl border border-zinc-200/80 bg-white p-5 sm:flex-row sm:items-center sm:gap-8 sm:p-7;
}

.collection-header::before {
  content: '';
  @apply absolute left-0 top-7 bottom-7 w-[3px] rounded-r-full bg-emerald-600;
}

.collection-kicker {
  @apply inline-flex items-center gap-2 rounded-full bg-emerald-50 px-3 py-1.5 text-[10px] font-semibold uppercase tracking-widest text-emerald-800;
}
</style>
