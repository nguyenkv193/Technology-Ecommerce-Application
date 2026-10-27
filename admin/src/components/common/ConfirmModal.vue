<template>
  <div
    v-if="isOpen"
    class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm animate-fade-in"
    @click.self="emit('cancel')"
  >
    <div class="w-full max-w-md bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-2xl relative">
      <div class="flex items-start gap-4">
        <div class="w-10 h-10 rounded-xl flex items-center justify-center shrink-0" :class="isDanger ? 'bg-rose-500/10 text-rose-400 border border-rose-500/20' : 'bg-blue-500/10 text-blue-400 border border-blue-500/20'">
          <AlertTriangle v-if="isDanger" class="w-5 h-5" />
          <Info v-else class="w-5 h-5" />
        </div>
        <div class="flex-1">
          <h3 class="text-base font-semibold text-slate-100">{{ title }}</h3>
          <p class="text-sm text-slate-400 mt-1.5 leading-relaxed">{{ message }}</p>
        </div>
      </div>

      <div class="mt-6 flex items-center justify-end gap-3 pt-4 border-t border-slate-800/80">
        <button
          type="button"
          @click="emit('cancel')"
          class="px-4 py-2 rounded-xl text-xs font-semibold text-slate-300 hover:bg-slate-800 transition-colors"
        >
          Hủy bỏ
        </button>
        <button
          type="button"
          @click="emit('confirm')"
          :disabled="isLoading"
          class="px-4 py-2 rounded-xl text-xs font-semibold text-white transition-all shadow-md flex items-center gap-2"
          :class="isDanger ? 'bg-rose-600 hover:bg-rose-500 shadow-rose-600/20' : 'bg-blue-600 hover:bg-blue-500 shadow-blue-600/20'"
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
