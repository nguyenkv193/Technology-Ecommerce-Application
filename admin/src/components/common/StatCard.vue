<template>
  <div class="p-5 rounded-xl bg-zinc-900/60 border border-zinc-800/80 hover:border-zinc-700/80 relative overflow-hidden transition-all duration-200 group shadow-subtle">
    <!-- Top hairline glow -->
    <div class="absolute inset-x-0 top-0 h-px bg-gradient-to-r from-transparent via-zinc-600/30 to-transparent"></div>

    <div class="flex items-start justify-between">
      <div class="space-y-1">
        <p class="text-[11px] font-medium text-zinc-400 uppercase tracking-wider font-mono">{{ title }}</p>
        <p class="text-2xl font-semibold text-zinc-100 tracking-tight font-mono tabular-nums">{{ value }}</p>
        
        <div v-if="change" class="pt-1 flex items-center gap-1.5 text-[11px] font-mono">
          <span
            class="px-1.5 py-0.5 rounded inline-flex items-center gap-0.5 text-[10px] font-semibold"
            :class="isPositive ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20' : 'bg-rose-500/10 text-rose-400 border border-rose-500/20'"
          >
            <span>{{ isPositive ? '↑' : '↓' }}</span>
            <span>{{ change }}</span>
          </span>
          <span class="text-zinc-500">so với tháng trước</span>
        </div>
      </div>

      <!-- Icon container -->
      <div
        class="w-9 h-9 rounded-lg flex items-center justify-center shrink-0 border transition-colors"
        :class="iconClasses"
      >
        <component :is="icon" class="w-4 h-4" />
      </div>
    </div>

    <!-- Decorative mini sparkline curve -->
    <div class="mt-3 pt-2 border-t border-zinc-800/40 flex items-center justify-between text-[11px] text-zinc-500 font-mono">
      <span>Xu hướng 30 ngày</span>
      <svg class="w-20 h-5 text-emerald-400/60 overflow-visible" viewBox="0 0 80 20" fill="none">
        <path
          :d="sparklinePath"
          stroke="currentColor"
          stroke-width="1.5"
          stroke-linecap="round"
          stroke-linejoin="round"
        />
      </svg>
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

const sparklinePath = computed(() => {
  if (props.isPositive) {
    return 'M 0 16 Q 20 18, 35 10 T 60 7 T 80 3'
  }
  return 'M 0 4 Q 25 6, 45 12 T 65 14 T 80 18'
})

const iconClasses = computed(() => {
  switch (props.theme) {
    case 'emerald':
      return 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20 group-hover:bg-emerald-500/20'
    case 'amber':
      return 'bg-amber-500/10 text-amber-400 border-amber-500/20 group-hover:bg-amber-500/20'
    case 'purple':
      return 'bg-purple-500/10 text-purple-400 border-purple-500/20 group-hover:bg-purple-500/20'
    case 'blue':
    default:
      return 'bg-zinc-800 text-zinc-300 border-zinc-700/80 group-hover:bg-zinc-700/80 group-hover:text-white'
  }
})
</script>
