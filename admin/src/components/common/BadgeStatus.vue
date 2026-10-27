<template>
  <span
    class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium border"
    :class="statusClass"
  >
    <span class="w-1.5 h-1.5 rounded-full" :class="dotClass"></span>
    <span>{{ statusLabel }}</span>
  </span>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  status: string
}>()

const statusConfig: Record<string, { label: string; bg: string; text: string; border: string; dot: string }> = {
  PENDING: {
    label: 'Chờ xác nhận',
    bg: 'bg-amber-500/10',
    text: 'text-amber-400',
    border: 'border-amber-500/20',
    dot: 'bg-amber-400'
  },
  CONFIRMED: {
    label: 'Đã xác nhận',
    bg: 'bg-blue-500/10',
    text: 'text-blue-400',
    border: 'border-blue-500/20',
    dot: 'bg-blue-400'
  },
  SHIPPING: {
    label: 'Đang giao hàng',
    bg: 'bg-purple-500/10',
    text: 'text-purple-400',
    border: 'border-purple-500/20',
    dot: 'bg-purple-400'
  },
  DELIVERED: {
    label: 'Giao thành công',
    bg: 'bg-emerald-500/10',
    text: 'text-emerald-400',
    border: 'border-emerald-500/20',
    dot: 'bg-emerald-400'
  },
  CANCELLED: {
    label: 'Đã hủy',
    bg: 'bg-rose-500/10',
    text: 'text-rose-400',
    border: 'border-rose-500/20',
    dot: 'bg-rose-400'
  },
  ACTIVE: {
    label: 'Hoạt động',
    bg: 'bg-emerald-500/10',
    text: 'text-emerald-400',
    border: 'border-emerald-500/20',
    dot: 'bg-emerald-400'
  },
  INACTIVE: {
    label: 'Tạm ẩn',
    bg: 'bg-slate-500/10',
    text: 'text-slate-400',
    border: 'border-slate-500/20',
    dot: 'bg-slate-400'
  }
}

const current = computed(() => {
  return statusConfig[props.status] || {
    label: props.status,
    bg: 'bg-slate-500/10',
    text: 'text-slate-400',
    border: 'border-slate-500/20',
    dot: 'bg-slate-400'
  }
})

const statusClass = computed(() => `${current.value.bg} ${current.value.text} ${current.value.border}`)
const dotClass = computed(() => current.value.dot)
const statusLabel = computed(() => current.value.label)
</script>
