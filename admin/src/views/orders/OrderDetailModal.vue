<template>
  <div
    v-if="isOpen && order"
    class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm animate-fade-in"
    @click.self="emit('close')"
  >
    <div class="w-full max-w-2xl bg-slate-900 border border-slate-800 rounded-2xl shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
      <!-- Header -->
      <div class="p-6 border-b border-slate-800 flex items-center justify-between shrink-0">
        <div>
          <div class="flex items-center gap-3">
            <h3 class="text-lg font-bold text-slate-100 font-mono">{{ order.orderCode }}</h3>
            <BadgeStatus :status="order.status" />
          </div>
          <p class="text-xs text-slate-400 mt-1">
            Đặt lúc: {{ formatDate(order.createdAt) }}
          </p>
        </div>
        <button
          @click="emit('close')"
          class="p-2 rounded-xl text-slate-400 hover:text-slate-200 hover:bg-slate-800 transition-colors"
        >
          <X class="w-5 h-5" />
        </button>
      </div>

      <!-- Scrollable Body -->
      <div class="p-6 overflow-y-auto space-y-6 flex-1">
        <!-- Customer & Delivery Information -->
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 p-4 rounded-xl bg-slate-950/60 border border-slate-800/80 text-xs">
          <div class="space-y-1">
            <span class="font-semibold text-slate-400 uppercase tracking-wider text-[10px]">Thông tin người nhận</span>
            <p class="font-semibold text-slate-200 text-sm">{{ order.customerName }}</p>
            <p class="text-slate-300">SĐT: {{ order.customerPhone }}</p>
            <p class="text-slate-400">Email: {{ order.customerEmail }}</p>
          </div>
          <div class="space-y-1">
            <span class="font-semibold text-slate-400 uppercase tracking-wider text-[10px]">Địa chỉ giao hàng</span>
            <p class="text-slate-200 leading-relaxed">{{ order.shippingAddress }}</p>
            <div class="pt-2 flex items-center gap-2">
              <span class="px-2 py-0.5 rounded bg-slate-800 text-slate-300 font-medium">
                {{ order.paymentMethod }}
              </span>
              <span class="text-[11px] text-slate-400">({{ order.paymentStatus }})</span>
            </div>
          </div>
        </div>

        <!-- Items Table -->
        <div class="space-y-3">
          <h4 class="text-xs font-semibold uppercase tracking-wider text-slate-400">Danh sách sản phẩm</h4>
          <div class="border border-slate-800 rounded-xl overflow-hidden">
            <table class="w-full text-left text-xs">
              <thead class="bg-slate-950/80 text-slate-400 border-b border-slate-800 uppercase font-semibold">
                <tr>
                  <th class="py-2.5 px-3">Sản phẩm</th>
                  <th class="py-2.5 px-3 text-right">Đơn giá</th>
                  <th class="py-2.5 px-3 text-center">SL</th>
                  <th class="py-2.5 px-3 text-right">Thành tiền</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-800/60">
                <tr v-for="item in (order.items || [])" :key="item.id" class="text-slate-300">
                  <td class="py-3 px-3">
                    <div class="font-medium text-slate-100">{{ item.productName }}</div>
                    <div v-if="item.skuCode" class="font-mono text-[10px] text-slate-500">SKU: {{ item.skuCode }}</div>
                  </td>
                  <td class="py-3 px-3 text-right font-mono">{{ formatCurrency(item.unitPrice) }}</td>
                  <td class="py-3 px-3 text-center font-semibold">{{ item.quantity }}</td>
                  <td class="py-3 px-3 text-right font-mono font-medium text-slate-100">{{ formatCurrency(item.totalPrice) }}</td>
                </tr>
                <tr v-if="!order.items || order.items.length === 0">
                  <td colspan="4" class="py-4 text-center text-slate-500">
                    Chi tiết sản phẩm đang được cập nhật.
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Payment Breakdown -->
        <div class="p-4 rounded-xl bg-slate-950/60 border border-slate-800/80 space-y-2 text-xs">
          <div class="flex justify-between text-slate-400">
            <span>Tạm tính hàng hóa:</span>
            <span class="font-mono text-slate-200">{{ formatCurrency(order.subtotal) }}</span>
          </div>
          <div class="flex justify-between text-slate-400">
            <span>Phí vận chuyển:</span>
            <span class="font-mono text-slate-200">{{ formatCurrency(order.shippingFee) }}</span>
          </div>
          <div v-if="order.discountAmount > 0" class="flex justify-between text-emerald-400">
            <span>Khuyến mãi / Giảm giá:</span>
            <span class="font-mono">-{{ formatCurrency(order.discountAmount) }}</span>
          </div>
          <div class="pt-2 border-t border-slate-800 flex justify-between text-sm font-bold text-slate-100">
            <span>Tổng thanh toán:</span>
            <span class="font-mono text-emerald-400 text-base">{{ formatCurrency(order.totalAmount) }}</span>
          </div>
        </div>
      </div>

      <!-- Footer Workflow Action Bar -->
      <div class="p-5 border-t border-slate-800 bg-slate-950/40 flex flex-wrap items-center justify-between gap-3 shrink-0">
        <div>
          <button
            v-if="canCancel"
            type="button"
            @click="handleCancel"
            :disabled="isProcessing"
            class="px-3.5 py-2 rounded-xl text-xs font-semibold text-rose-400 hover:bg-rose-500/10 border border-rose-500/20 transition-colors disabled:opacity-50"
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
            class="px-4 py-2 rounded-xl bg-blue-600 hover:bg-blue-500 text-white text-xs font-semibold shadow-md shadow-blue-600/30 flex items-center gap-1.5 transition-all"
          >
            <Check class="w-4 h-4" />
            <span>Xác nhận đơn</span>
          </button>

          <button
            v-if="order.status === 'CONFIRMED'"
            type="button"
            @click="advanceStatus('SHIPPING')"
            :disabled="isProcessing"
            class="px-4 py-2 rounded-xl bg-purple-600 hover:bg-purple-500 text-white text-xs font-semibold shadow-md shadow-purple-600/30 flex items-center gap-1.5 transition-all"
          >
            <Truck class="w-4 h-4" />
            <span>Giao vận chuyển</span>
          </button>

          <button
            v-if="order.status === 'SHIPPING'"
            type="button"
            @click="advanceStatus('DELIVERED')"
            :disabled="isProcessing"
            class="px-4 py-2 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-semibold shadow-md shadow-emerald-600/30 flex items-center gap-1.5 transition-all"
          >
            <CheckCircle class="w-4 h-4" />
            <span>Giao thành công</span>
          </button>

          <button
            type="button"
            @click="emit('close')"
            class="px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-semibold transition-colors"
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
