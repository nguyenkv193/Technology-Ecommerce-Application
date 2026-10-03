<template>
  <section class="py-6 sm:py-8">
    <div class="flex flex-col sm:flex-row sm:items-end justify-between mb-6 gap-3 pb-3 border-b border-zinc-200/60">
      <div>
        <div v-if="badge" class="flex items-center gap-2 mb-1.5">
          <span class="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-md text-[10px] font-semibold uppercase tracking-wider bg-zinc-100 text-zinc-600 border border-zinc-200">
            <svg class="w-3 h-3 text-zinc-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
            {{ badge }}
          </span>
        </div>
        <h2 class="text-xl sm:text-2xl font-bold tracking-tight text-zinc-900">
          {{ title }}
        </h2>
        <p v-if="subtitle" class="text-xs sm:text-sm text-zinc-500 mt-1">
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

    <!-- Empty State -->
    <div v-else-if="!items || items.length === 0" class="p-8 text-center bg-white rounded-2xl border border-zinc-200/80 text-zinc-400 text-xs">
      Đang cập nhật các gợi ý sản phẩm công nghệ phù hợp...
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
import type { Product } from '@/types'

withDefaults(defineProps<{
  title: string
  subtitle?: string
  badge?: string
  items?: (Product & { reason?: string })[]
  loading?: boolean
}>(), {
  subtitle: '',
  badge: '',
  items: () => [],
  loading: false
})
</script>
