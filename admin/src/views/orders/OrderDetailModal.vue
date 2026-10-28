<template>
  <div
    v-if="isOpen && order"
    class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-zinc-950/80 backdrop-blur-sm animate-fade-in"
    @click.self="emit('close')"
  >
    <div class="w-full max-w-2xl bg-zinc-900 border border-zinc-800 rounded-xl shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
      <!-- Header -->
      <div class="px-6 py-4 border-b border-zinc-800/80 flex items-center justify-between shrink-0 bg-zinc-900/50">
        <div>
          <div class="flex items-center gap-3">
            <h3 class="text-base font-bold text-zinc-100 font-mono tracking-tight">{{ order.orderCode }}</h3>
            <BadgeStatus :status="order.status" />
          </div>
          <p class="text-[11px] text-zinc-500 font-mono mt-0.5">
            Thời gian tạo: {{ formatDate(order.createdAt) }}
          </p>
        </div>
        <button
          @click="emit('close')"
          class="p-1.5 rounded-lg text-zinc-400 hover:text-zinc-200 hover:bg-zinc-800/80 transition-colors"
        >
          <X class="w-4 h-4" />
        </button>
      </div>

      <!-- Scrollable Body -->
      <div class="p-6 overflow-y-auto space-y-5 flex-1">
        <!-- Customer & Delivery Information -->
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 p-4 rounded-xl bg-zinc-950/60 border border-zinc-800/80 text-xs">
          <div class="space-y-1.5">
            <span class="font-mono text-[10px] uppercase tracking-wider text-zinc-500 font-semibold">Khách hàng nhận</span>
            <p class="font-semibold text-zinc-200 text-sm">{{ order.customerName }}</p>
            <p class="text-zinc-400 font-mono text-[11px]">SĐT: <span class="text-zinc-300">{{ order.customerPhone }}</span></p>
            <p class="text-zinc-400 font-mono text-[11px]">Email: <span class="text-zinc-300">{{ order.customerEmail }}</span></p>
          </div>
          <div class="space-y-1.5">
            <span class="font-mono text-[10px] uppercase tracking-wider text-zinc-500 font-semibold">Giao hàng & Thanh toán</span>
            <p class="text-zinc-300 leading-relaxed text-[11px]">{{ order.shippingAddress }}</p>
            <div class="pt-1.5 flex items-center gap-2">
              <span class="px-2 py-0.5 rounded font-mono text-[10px] bg-zinc-800 text-zinc-300 border border-zinc-700/60 font-medium">
                {{ order.paymentMethod }}
              </span>
              <span class="text-[10px] font-mono text-zinc-500">({{ order.paymentStatus }})</span>
            </div>
          </div>
        </div>

        <!-- Items Table -->
        <div class="space-y-2.5">
          <div class="flex items-center justify-between">
            <h4 class="text-xs font-mono font-semibold uppercase tracking-wider text-zinc-400">Danh mục sản phẩm</h4>
            <span class="text-[10px] font-mono text-zinc-500">{{ (order.items || []).length }} mặt hàng</span>
          </div>
          <div class="border border-zinc-800/80 rounded-xl overflow-hidden">
            <table class="w-full text-left text-xs">
              <thead class="bg-zinc-950/80 text-zinc-400 border-b border-zinc-800 uppercase font-mono text-[10px] tracking-wider">
                <tr>
                  <th class="py-2.5 px-3.5 font-medium">Sản phẩm</th>
                  <th class="py-2.5 px-3.5 text-right font-medium">Đơn giá</th>
                  <th class="py-2.5 px-3.5 text-center font-medium">SL</th>
                  <th class="py-2.5 px-3.5 text-right font-medium">Thành tiền</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-zinc-800/60 bg-zinc-900/30">
                <tr v-for="item in (order.items || [])" :key="item.id" class="text-zinc-300 hover:bg-zinc-800/20 transition-colors">
                  <td class="py-3 px-3.5">
                    <div class="font-medium text-zinc-200">{{ item.productName }}</div>
                    <div v-if="item.skuCode" class="font-mono text-[10px] text-zinc-500">SKU: {{ item.skuCode }}</div>
                  </td>
                  <td class="py-3 px-3.5 text-right font-mono text-zinc-400 tabular-nums">{{ formatCurrency(item.unitPrice) }}</td>
                  <td class="py-3 px-3.5 text-center font-mono font-semibold text-zinc-300">{{ item.quantity }}</td>
                  <td class="py-3 px-3.5 text-right font-mono font-medium text-zinc-100 tabular-nums">{{ formatCurrency(item.totalPrice) }}</td>
                </tr>
                <tr v-if="!order.items || order.items.length === 0">
                  <td colspan="4" class="py-6 text-center text-zinc-500 text-xs">
                    Chi tiết sản phẩm đang được cập nhật.
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Payment Breakdown -->
        <div class="p-4 rounded-xl bg-zinc-950/60 border border-zinc-800/80 space-y-2 text-xs">
          <div class="flex justify-between text-zinc-400">
            <span>Tạm tính tiền hàng:</span>
            <span class="font-mono text-zinc-200 tabular-nums">{{ formatCurrency(order.subtotal) }}</span>
          </div>
          <div class="flex justify-between text-zinc-400">
            <span>Phí vận chuyển:</span>
            <span class="font-mono text-zinc-200 tabular-nums">{{ formatCurrency(order.shippingFee) }}</span>
          </div>
          <div v-if="order.discountAmount > 0" class="flex justify-between text-emerald-400">
            <span>Khuyến mãi / Giảm giá:</span>
            <span class="font-mono tabular-nums">-{{ formatCurrency(order.discountAmount) }}</span>
          </div>
          <div class="pt-2 border-t border-zinc-800 flex justify-between text-xs font-semibold text-zinc-100">
            <span>Tổng thanh toán:</span>
            <span class="font-mono text-emerald-400 text-sm font-bold tabular-nums">{{ formatCurrency(order.totalAmount) }}</span>
          </div>
        </div>
      </div>

      <!-- Footer Workflow Action Bar -->
      <div class="px-6 py-3.5 border-t border-zinc-800/80 bg-zinc-950/60 flex flex-wrap items-center justify-between gap-3 shrink-0">
        <div>
          <button
            v-if="canCancel"
            type="button"
            @click="handleCancel"
            :disabled="isProcessing"
            class="px-3.5 py-1.5 rounded-lg text-xs font-semibold text-rose-400 hover:bg-rose-500/10 border border-rose-500/25 transition-colors disabled:opacity-50"
          >
            Hủy đơn hàng
          </button>
        </div>

        <div class="flex items-center gap-2">
          <!-- Next status actions -->
          <button
            v-if="order.status === 'PENDING'"
            type="button"
            @click="advanceStatus('CONFIRMED')"
            :disabled="isProcessing"
            class="px-3.5 py-1.5 rounded-lg bg-white hover:bg-zinc-200 text-zinc-950 text-xs font-semibold shadow-sm flex items-center gap-1.5 transition-all"
          >
            <Check class="w-3.5 h-3.5" />
            <span>Xác nhận đơn</span>
          </button>

          <button
            v-if="order.status === 'CONFIRMED'"
            type="button"
            @click="advanceStatus('SHIPPING')"
            :disabled="isProcessing"
            class="px-3.5 py-1.5 rounded-lg bg-white hover:bg-zinc-200 text-zinc-950 text-xs font-semibold shadow-sm flex items-center gap-1.5 transition-all"
          >
            <Truck class="w-3.5 h-3.5" />
            <span>Giao vận chuyển</span>
          </button>

          <button
            v-if="order.status === 'SHIPPING'"
            type="button"
            @click="advanceStatus('DELIVERED')"
            :disabled="isProcessing"
            class="px-3.5 py-1.5 rounded-lg bg-emerald-500 hover:bg-emerald-400 text-zinc-950 text-xs font-semibold shadow-sm flex items-center gap-1.5 transition-all"
          >
            <CheckCircle class="w-3.5 h-3.5" />
            <span>Giao thành công</span>
          </button>

          <button
            type="button"
            @click="emit('close')"
            class="px-3.5 py-1.5 rounded-lg bg-zinc-800 hover:bg-zinc-750 text-zinc-300 text-xs font-medium transition-colors border border-zinc-700/60"
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { X, Check, Truck, CheckCircle } from 'lucide-vue-next'
import BadgeStatus from '@/components/common/BadgeStatus.vue'
import { updateOrderStatus, cancelOrder } from '@/api/orders'
import type { Order, OrderStatus } from '@/types'

const props = defineProps<{
  isOpen: boolean
  order: Order | null
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'updated'): void
}>()

const isProcessing = ref(false)

const canCancel = computed(() => {
  return props.order && (props.order.status === 'PENDING' || props.order.status === 'CONFIRMED')
})

function formatCurrency(val: number): string {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val || 0)
}

function formatDate(dateStr: string): string {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleString('vi-VN')
}

async function advanceStatus(newStatus: OrderStatus) {
  if (!props.order) return
  isProcessing.value = true
  try {
    await updateOrderStatus(props.order.id, newStatus)
    emit('updated')
  } catch (err: any) {
    alert(err.response?.data?.message || 'Không thể cập nhật trạng thái đơn hàng.')
  } finally {
    isProcessing.value = false
  }
}

async function handleCancel() {
  if (!props.order) return
  if (!confirm(`Xác nhận hủy đơn hàng ${props.order.orderCode}? Hệ thống sẽ tự động hoàn lại tồn kho.`)) {
    return
  }
  isProcessing.value = true
  try {
    await cancelOrder(props.order.id)
    emit('updated')
  } catch (err: any) {
    alert(err.response?.data?.message || 'Không thể hủy đơn hàng.')
  } finally {
    isProcessing.value = false
  }
}
</script>
