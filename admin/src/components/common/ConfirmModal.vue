<template>
  <div
    v-if="isOpen"
    class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-zinc-950/80 backdrop-blur-sm animate-fade-in"
    @click.self="emit('cancel')"
  >
    <div class="w-full max-w-md bg-zinc-900 border border-zinc-800 rounded-xl p-5 shadow-2xl relative">
      <div class="flex items-start gap-3.5">
        <div
          class="w-9 h-9 rounded-lg flex items-center justify-center shrink-0"
          :class="isDanger ? 'bg-rose-500/10 text-rose-400 border border-rose-500/20' : 'bg-zinc-800 text-zinc-300 border border-zinc-700/60'"
        >
          <AlertTriangle v-if="isDanger" class="w-4 h-4" />
          <Info v-else class="w-4 h-4" />
        </div>
        <div class="flex-1">
          <h3 class="text-sm font-semibold text-zinc-100">{{ title }}</h3>
          <p class="text-xs text-zinc-400 mt-1 leading-relaxed">{{ message }}</p>
        </div>
      </div>

      <div class="mt-5 flex items-center justify-end gap-2.5 pt-4 border-t border-zinc-800/80">
        <button
          type="button"
          @click="emit('cancel')"
          class="px-3.5 py-1.5 rounded-lg text-xs font-medium text-zinc-300 hover:text-white bg-zinc-800 hover:bg-zinc-750 border border-zinc-700/60 transition-colors"
        >
          Hủy bỏ
        </button>
        <button
          type="button"
          @click="emit('confirm')"
          :disabled="isLoading"
          class="px-3.5 py-1.5 rounded-lg text-xs font-semibold transition-all shadow-sm flex items-center gap-2 disabled:opacity-50"
          :class="isDanger ? 'bg-rose-500 hover:bg-rose-600 text-white' : 'bg-white hover:bg-zinc-200 text-zinc-950'"
        >
          <Loader2 v-if="isLoading" class="w-3.5 h-3.5 animate-spin" />
          <span>{{ confirmText }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { AlertTriangle, Info, Loader2 } from 'lucide-vue-next'

withDefaults(
  defineProps<{
    isOpen: boolean
    title: string
    message: string
    confirmText?: string
    isDanger?: boolean
    isLoading?: boolean
  }>(),
  {
    confirmText: 'Xác nhận',
    isDanger: true,
    isLoading: false
  }
)

const emit = defineEmits<{
  (e: 'confirm'): void
  (e: 'cancel'): void
}>()
</script>
