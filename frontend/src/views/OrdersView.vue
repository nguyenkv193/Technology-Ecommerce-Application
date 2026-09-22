<template>
  <div class="space-y-8">
    <div class="pb-5 border-b border-zinc-200/80">
      <h1 class="text-2xl sm:text-3xl font-bold text-zinc-900 tracking-tight">Lịch Sử Đơn Hàng</h1>
      <p class="text-xs sm:text-sm text-zinc-500 mt-1">Theo dõi tiến độ vận chuyển và thông tin chi tiết các đơn hàng đã đặt</p>
    </div>

    <div v-if="loading" class="text-center py-16 text-zinc-400 text-xs animate-pulse">
      Đang tải danh sách đơn hàng...
    </div>

    <div v-else-if="orders.length === 0" class="text-center py-16 bg-white rounded-2xl border border-zinc-200/80 p-8 space-y-4">
      <div class="w-14 h-14 mx-auto rounded-2xl bg-zinc-100 flex items-center justify-center text-zinc-500">
        <svg class="w-7 h-7" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/><rect x="9" y="3" width="6" height="4" rx="2"/></svg>
      </div>
      <div>
        <h3 class="text-base font-semibold text-zinc-900">Bạn chưa có đơn hàng nào</h3>
        <p class="text-xs text-zinc-500 max-w-sm mx-auto mt-1">Hãy bắt đầu lựa chọn những thiết bị công nghệ tiên tiến trên TechStore.</p>
      </div>
      <router-link to="/products" class="inline-block px-5 py-2.5 rounded-xl text-xs font-medium bg-zinc-900 text-white hover:bg-zinc-800 transition">
        Khám phá sản phẩm
      </router-link>
    </div>

    <div v-else class="space-y-5">
      <div 
        v-for="order in orders" 
        :key="order.id"
        class="bg-white border border-zinc-200/80 rounded-2xl p-5 sm:p-6 space-y-5 shadow-xs"
      >
        <!-- Order Header -->
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-4 border-b border-zinc-100">
          <div>
            <div class="flex items-center gap-2">
              <span class="text-xs text-zinc-400">Mã đơn:</span>
              <span class="font-mono text-xs font-bold text-zinc-900">{{ order.orderCode }}</span>
            </div>
            <div class="text-[11px] text-zinc-400 mt-0.5">Thời gian: {{ formatDate(order.createdAt) }}</div>
          </div>

          <div class="flex items-center gap-3">
            <span 
              class="px-2.5 py-1 rounded-full text-[11px] font-medium tracking-wide"
              :class="getStatusBadgeClass(order.orderStatus)"
            >
              {{ getStatusLabel(order.orderStatus) }}
            </span>

            <button 
              v-if="order.orderStatus === 'PENDING' || order.orderStatus === 'CONFIRMED'"
              @click="handleCancelOrder(order.id)"
              class="px-2.5 py-1 rounded-lg text-xs font-medium text-red-600 hover:bg-red-50 border border-red-200 transition"
            >
              Hủy đơn
            </button>
          </div>
        </div>

        <!-- Items Preview -->
        <div class="divide-y divide-zinc-100">
          <div 
            v-for="item in order.items" 
            :key="item.id"
            class="py-3 flex items-center justify-between gap-4"
          >
            <div class="flex items-center gap-3 min-w-0">
              <div class="w-12 h-12 rounded-xl bg-[#f6f6f8] p-1.5 shrink-0 border border-zinc-100 flex items-center justify-center">
                <img :src="item.thumbnailUrl || defaultImage" class="max-w-full max-h-full object-contain" />
              </div>
              <div class="truncate">
                <h4 class="text-xs font-semibold text-zinc-900 truncate">{{ item.productName }}</h4>
                <div class="text-[11px] text-zinc-500">{{ item.variantName }} (SL: {{ item.quantity }})</div>
              </div>
            </div>
            <div class="text-xs font-semibold text-zinc-900 shrink-0">
              {{ formatPrice(item.totalPrice) }}
            </div>
          </div>
        </div>

        <!-- Footer Info -->
        <div class="pt-3.5 border-t border-zinc-100 flex flex-col sm:flex-row sm:items-center justify-between text-xs text-zinc-500 gap-2">
          <div>
            <span>Địa chỉ nhận: </span>
            <span class="text-zinc-800 font-medium">{{ order.recipientName }} ({{ order.phoneNumber }}) - {{ order.shippingAddress }}</span>
          </div>
          <div class="flex items-center gap-2">
            <span>Tổng thanh toán:</span>
            <span class="text-base font-bold text-zinc-900">{{ formatPrice(order.finalAmount) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { orderApi } from '@/api/orderApi'
import type { Order } from '@/types'

const orders = ref<Order[]>([])
const loading = ref<boolean>(true)
const defaultImage = 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500&auto=format&fit=crop&q=60'

const formatPrice = (value?: number | null): string => {
  if (!value || isNaN(value)) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}

const formatDate = (isoStr?: string): string => {
  if (!isoStr) return ''
  return new Date(isoStr).toLocaleString('vi-VN')
}

const getStatusLabel = (status: string): string => {
  const map: Record<string, string> = {
    PENDING: 'Chờ xác nhận',
    CONFIRMED: 'Đã xác nhận',
    SHIPPING: 'Đang giao hàng',
    DELIVERED: 'Đã giao thành công',
    CANCELLED: 'Đã hủy'
  }
  return map[status] || status
}

const getStatusBadgeClass = (status: string): string => {
  const map: Record<string, string> = {
    PENDING: 'bg-amber-50 text-amber-700 border border-amber-200',
    CONFIRMED: 'bg-blue-50 text-blue-700 border border-blue-200',
    SHIPPING: 'bg-indigo-50 text-indigo-700 border border-indigo-200',
    DELIVERED: 'bg-emerald-50 text-emerald-700 border border-emerald-200',
    CANCELLED: 'bg-zinc-100 text-zinc-600 border border-zinc-200'
  }
  return map[status] || 'bg-zinc-100 text-zinc-700'
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await orderApi.getMyOrders({ size: 20 })
    if (res.success && res.data) {
      orders.value = res.data.content || []
    }
  } catch (err) {
    console.error('Lỗi tải danh sách đơn hàng:', err)
  } finally {
    loading.value = false
  }
}

const handleCancelOrder = async (orderId: number) => {
  if (!confirm('Bạn có chắc chắn muốn hủy đơn hàng này? Hệ thống sẽ tự động hoàn tồn kho cho sản phẩm.')) {
    return
  }

  try {
    await orderApi.cancelOrder(orderId)
    await fetchOrders()
  } catch (err: any) {
    alert(err.message)
  }
}

onMounted(() => {
  fetchOrders()
})
</script>
