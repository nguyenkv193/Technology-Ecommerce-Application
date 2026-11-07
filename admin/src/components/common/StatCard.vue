<template>
  <div class="p-5 rounded-xl bg-white border border-zinc-200 hover:border-zinc-300 transition-colors shadow-sm group">
    <div class="flex items-center justify-between pb-3">
      <span class="text-xs font-medium text-zinc-500">{{ title }}</span>
      <div
        class="w-8 h-8 rounded-lg flex items-center justify-center shrink-0 border transition-colors shadow-xs"
        :class="iconClasses"
      >
        <component :is="icon" class="w-4 h-4" />
      </div>
    </div>

    <div class="space-y-1">
      <div class="text-2xl font-semibold text-zinc-900 tracking-tight font-mono tabular-nums">
        {{ value }}
      </div>
      <div v-if="change" class="flex items-center gap-1.5 text-xs font-mono">
        <span
          class="font-medium inline-flex items-center gap-0.5"
          :class="isPositive ? 'text-emerald-600' : 'text-rose-600'"
        >
          <span>{{ isPositive ? '↑' : '↓' }}</span>
          <span>{{ change }}</span>
        </span>
        <span class="text-zinc-500">so với tháng trước</span>
      </div>
      <div v-else-if="subtitle" class="text-xs text-zinc-500 font-mono">
        {{ subtitle }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, type Component } from 'vue'

const props = withDefaults(
  defineProps<{
    title: string
    value: string | number
    icon: Component
    change?: string
    isPositive?: boolean
    subtitle?: string
    theme?: 'blue' | 'emerald' | 'amber' | 'purple'
  }>(),
  {
    theme: 'blue',
    isPositive: true
  }
)

const iconClasses = computed(() => {
  switch (props.theme) {
    case 'emerald':
      return 'bg-emerald-50 text-emerald-600 border-emerald-200/80'
    case 'amber':
      return 'bg-amber-50 text-amber-600 border-amber-200/80'
    case 'purple':
      return 'bg-purple-50 text-purple-600 border-purple-200/80'
    case 'blue':
    default:
      return 'bg-blue-50 text-blue-600 border-blue-200/80'
  }
})
</script>
