<template>
  <div class="p-6 rounded-2xl bg-slate-900 border border-slate-800 relative overflow-hidden transition-all hover:border-slate-700 shadow-sm">
    <div class="flex items-center justify-between">
      <div>
        <p class="text-xs font-medium text-slate-400 uppercase tracking-wider">{{ title }}</p>
        <p class="text-2xl font-bold text-slate-100 mt-2 tracking-tight">{{ value }}</p>
        <div v-if="change" class="mt-2 flex items-center gap-1.5 text-xs font-medium" :class="isPositive ? 'text-emerald-400' : 'text-rose-400'">
          <span>{{ isPositive ? '↑' : '↓' }} {{ change }}</span>
          <span class="text-slate-500 font-normal">so với tháng trước</span>
        </div>
      </div>
      <div class="w-12 h-12 rounded-2xl flex items-center justify-center shrink-0 border" :class="colorClasses">
        <component :is="icon" class="w-6 h-6" />
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
    theme?: 'blue' | 'emerald' | 'amber' | 'purple'
  }>(),
  {
    theme: 'blue',
    isPositive: true
  }
)

const colorClasses = computed(() => {
  switch (props.theme) {
    case 'emerald':
      return 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20 shadow-emerald-500/5'
    case 'amber':
      return 'bg-amber-500/10 text-amber-400 border-amber-500/20 shadow-amber-500/5'
    case 'purple':
      return 'bg-purple-500/10 text-purple-400 border-purple-500/20 shadow-purple-500/5'
    case 'blue':
    default:
      return 'bg-blue-500/10 text-blue-400 border-blue-500/20 shadow-blue-500/5'
  }
})
</script>
