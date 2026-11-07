<template>
  <div class="p-5 rounded-xl bg-zinc-900/60 border border-zinc-800/80 hover:border-zinc-700/80 transition-colors shadow-subtle group">
    <div class="flex items-center justify-between pb-3">
      <span class="text-xs font-medium text-zinc-400">{{ title }}</span>
      <div
        class="w-8 h-8 rounded-lg flex items-center justify-center shrink-0 border transition-colors"
        :class="iconClasses"
      >
        <component :is="icon" class="w-4 h-4" />
      </div>
    </div>

    <div class="space-y-1">
      <div class="text-2xl font-semibold text-white tracking-tight font-mono tabular-nums">
        {{ value }}
      </div>
      <div v-if="change" class="flex items-center gap-1.5 text-xs font-mono">
        <span
          class="font-medium inline-flex items-center gap-0.5"
          :class="isPositive ? 'text-emerald-400' : 'text-rose-400'"
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
      return 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20'
    case 'amber':
      return 'bg-amber-500/10 text-amber-400 border-amber-500/20'
    case 'purple':
      return 'bg-purple-500/10 text-purple-400 border-purple-500/20'
    case 'blue':
    default:
      return 'bg-zinc-800 text-zinc-300 border-zinc-700/60'
  }
})
</script>
