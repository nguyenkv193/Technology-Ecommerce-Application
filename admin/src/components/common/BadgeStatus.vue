<template>
  <span
    class="inline-flex items-center gap-1.5 px-2 py-0.5 rounded-md text-[11px] font-mono font-medium border"
    :class="statusClass"
  >
    <span class="w-1.5 h-1.5 rounded-full shrink-0" :class="dotClass"></span>
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
    bg: 'bg-amber-50',
    text: 'text-amber-700',
    border: 'border-amber-200/80',
    dot: 'bg-amber-500'
  },
  CONFIRMED: {
    label: 'Đã xác nhận',
    bg: 'bg-blue-50',
    text: 'text-blue-700',
    border: 'border-blue-200/80',
    dot: 'bg-blue-500'
  },
  SHIPPING: {
    label: 'Đang giao hàng',
    bg: 'bg-purple-50',
    text: 'text-purple-700',
    border: 'border-purple-200/80',
    dot: 'bg-purple-500'
  },
  DELIVERED: {
    label: 'Giao thành công',
    bg: 'bg-emerald-50',
    text: 'text-emerald-700',
    border: 'border-emerald-200/80',
    dot: 'bg-emerald-500'
  },
  CANCELLED: {
    label: 'Đã hủy',
    bg: 'bg-rose-50',
    text: 'text-rose-700',
    border: 'border-rose-200/80',
    dot: 'bg-rose-500'
  },
  ACTIVE: {
    label: 'Hoạt động',
    bg: 'bg-emerald-50',
    text: 'text-emerald-700',
    border: 'border-emerald-200/80',
    dot: 'bg-emerald-500'
  },
  INACTIVE: {
    label: 'Tạm ẩn',
    bg: 'bg-zinc-100',
    text: 'text-zinc-600',
    border: 'border-zinc-200',
    dot: 'bg-zinc-400'
  }
}

const current = computed(() => {
  return statusConfig[props.status] || {
    label: props.status,
    bg: 'bg-zinc-100',
    text: 'text-zinc-600',
    border: 'border-zinc-200',
    dot: 'bg-zinc-400'
  }
})

const statusClass = computed(() => `${current.value.bg} ${current.value.text} ${current.value.border}`)
const dotClass = computed(() => current.value.dot)
const statusLabel = computed(() => current.value.label)
</script>
