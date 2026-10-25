<template>
  <div class="bg-white rounded-xl border border-zinc-200/80 p-6 sm:p-8 shadow-xs">
    <div class="flex items-center justify-between mb-6 pb-4 border-b border-zinc-100">
      <div>
        <div class="flex items-center gap-2">
          <span class="w-2.5 h-2.5 rounded-full bg-red-600"></span>
          <h3 class="text-lg font-bold text-zinc-900">Thông số kỹ thuật</h3>
        </div>
        <p class="text-xs text-zinc-400 mt-0.5">Chi tiết cấu hình và đặc tính kỹ thuật tiêu chuẩn từ nhà sản xuất</p>
      </div>
      <div class="p-2 rounded-lg bg-zinc-50 text-zinc-500 border border-zinc-200/60 hidden sm:block">
        <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="4" y="4" width="16" height="16" rx="2"/><rect x="9" y="9" width="6" height="6"/><path d="M9 1v3M15 1v3M9 20v3M15 20v3M20 9h3M20 14h3M1 9h3M1 14h3"/>
        </svg>
      </div>
    </div>

    <div v-if="!attributes || attributes.length === 0" class="text-zinc-400 text-xs py-8 text-center bg-zinc-50 rounded-lg border border-dashed border-zinc-200">
      Thông số kỹ thuật đang được cập nhật.
    </div>

    <div v-else class="space-y-1">
      <div 
        v-for="(attr, idx) in attributes" 
        :key="idx"
        class="py-3 px-4 flex justify-between items-center text-xs sm:text-sm rounded-lg transition-colors"
        :class="idx % 2 === 0 ? 'bg-zinc-50/70' : 'bg-white'"
      >
        <span class="text-zinc-500 font-medium flex items-center gap-2">
          <span class="w-1.5 h-1.5 rounded-full bg-zinc-300"></span>
          {{ attr.name }}
        </span>
        <span class="text-zinc-900 font-semibold text-right max-w-[60%]">{{ attr.value }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ProductAttribute } from '@/types'

withDefaults(defineProps<{
  attributes?: ProductAttribute[]
}>(), {
  attributes: () => []
})
</script>
