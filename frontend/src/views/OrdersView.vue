<template>
  <div class="space-y-8">
    <div class="pb-4 border-b border-zinc-200/80">
      <h1 class="text-lg sm:text-xl font-bold text-zinc-900 tracking-tight">Lịch Sử Đơn Hàng</h1>
      <p class="text-xs text-zinc-500 mt-1">Theo dõi tiến độ vận chuyển và thông tin chi tiết các đơn hàng đã đặt</p>
    </div>

    <div class="flex flex-col md:flex-row gap-6 items-start">
      <!-- LEFT: avatar + tabs -->
      <AccountSidebar
        active-tab="orders"
        :order-count="totalElements"
        @select-tab="handleSidebarTab"
      />

      <!-- RIGHT: order history content -->
      <section class="flex-1 min-w-0 w-full space-y-4">
        <h2 class="text-base font-semibold text-zinc-900" data-testid="active-tab-title">Lịch sử đơn hàng</h2>

        <!-- Status Filter Tabs -->
        <div class="flex items-center gap-1.5 overflow-x-auto pb-1">
          <button
            v-for="tab in statusTabs"
            :key="tab.value"
            type="button"
            :data-testid="`order-status-tab-${tab.value}`"
            @click="selectStatus(tab.value)"
            :class="[
              'px-3 py-1.5 rounded-lg text-xs font-medium transition shrink-0 cursor-pointer border',
              selectedStatus === tab.value
                ? 'bg-zinc-900 text-white border-zinc-900 shadow-xs'
                : 'bg-white text-zinc-600 border-zinc-200 hover:text-zinc-900 hover:bg-zinc-50'
            ]"
          >
            {{ tab.label }}
          </button>
        </div>

        <!-- Search Filter Bar -->
        <div class="bg-white border border-zinc-200/80 rounded-2xl p-2.5 flex flex-wrap items-center gap-2 shadow-xs">
          <div class="relative flex-1 min-w-[200px]">
            <Search class="w-3.5 h-3.5 text-zinc-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              v-model="keyword"
              type="text"
              data-testid="order-search-input"
              @keyup.enter="handleSearch"
              placeholder="Tìm theo mã đơn, người nhận, SĐT..."
              class="w-full h-9 bg-zinc-50/70 focus:bg-white border border-zinc-200 rounded-lg pl-9 pr-8 text-xs text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 transition-colors"
            />
            <button
              v-if="keyword"
              type="button"
              @click="keyword = ''; handleSearch()"
              class="absolute right-2.5 top-1/2 -translate-y-1/2 text-zinc-400 hover:text-zinc-600 text-xs cursor-pointer"
            >
              ✕
            </button>
          </div>
          <button
            type="button"
            data-testid="order-search-btn"
            @click="handleSearch"
            class="h-9 px-3.5 inline-flex items-center justify-center rounded-lg bg-zinc-900 hover:bg-zinc-800 border border-zinc-900 text-white text-xs font-medium shadow-xs transition cursor-pointer shrink-0"
          >
            Tìm kiếm
          </button>
          <button
            type="button"
            data-testid="order-reset-btn"
            @click="resetFilters"
            class="h-9 px-3 inline-flex items-center justify-center rounded-lg bg-white hover:bg-zinc-50 border border-zinc-200 text-zinc-700 hover:text-zinc-900 text-xs font-medium shadow-xs transition cursor-pointer shrink-0"
          >
            Đặt lại
          </button>
        </div>

        <div v-if="loading" class="text-center py-16 text-zinc-400 text-xs animate-pulse bg-white rounded-2xl border border-zinc-200/80">
          Đang tải danh sách đơn hàng...
        </div>

        <div v-else-if="orders.length === 0" class="text-center py-16 bg-white rounded-2xl border border-zinc-200/80 p-8 space-y-4">
          <div class="w-14 h-14 mx-auto rounded-2xl bg-zinc-100 flex items-center justify-center text-zinc-500">
            <svg class="w-7 h-7" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/><rect x="9" y="3" width="6" height="4" rx="2"/></svg>
          </div>
          <div>
            <h3 class="text-base font-semibold text-zinc-900">
              {{ hasActiveFilters ? 'Không tìm thấy đơn hàng phù hợp' : 'Bạn chưa có đơn hàng nào' }}
            </h3>
            <p class="text-xs text-zinc-500 max-w-sm mx-auto mt-1">
              {{ hasActiveFilters ? 'Thử thay đổi trạng thái hoặc từ khóa tìm kiếm khác.' : 'Hãy bắt đầu lựa chọn những thiết bị công nghệ tiên tiến trên TechStore.' }}
            </p>
          </div>
          <button
            v-if="hasActiveFilters"
            type="button"
            @click="resetFilters"
            class="inline-block px-5 py-2.5 rounded-xl text-xs font-medium bg-zinc-900 text-white hover:bg-zinc-800 transition cursor-pointer"
          >
            Đặt lại bộ lọc
          </button>
          <router-link v-else to="/products" class="inline-block px-5 py-2.5 rounded-xl text-xs font-medium bg-zinc-900 text-white hover:bg-zinc-800 transition">
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

              <div class="flex flex-wrap items-center gap-2 sm:gap-2.5">
                <span
                  class="px-2.5 py-1 rounded-full text-[11px] font-medium"
                  :class="getPaymentBadgeClass(order)"
                >
                  {{ getPaymentLabel(order) }}
                </span>

                <span 
                  class="px-2.5 py-1 rounded-full text-[11px] font-medium tracking-wide"
                  :class="getStatusBadgeClass(order.orderStatus)"
                >
                  {{ getStatusLabel(order.orderStatus) }}
                </span>

                <button
                  v-if="canPayWithVietQr(order)"
                  type="button"
                  data-testid="open-vietqr-btn"
                  @click="qrOrder = order"
                  class="px-2.5 py-1 rounded-lg text-xs font-semibold bg-zinc-900 text-white hover:bg-zinc-800 transition cursor-pointer"
                >
                  Mã VietQR
                </button>

                <button 
                  v-if="order.orderStatus === 'PENDING' || order.orderStatus === 'CONFIRMED'"
                  @click="handleCancelOrder(order.id)"
                  class="px-2.5 py-1 rounded-lg text-xs font-medium text-red-600 hover:bg-red-50 border border-red-200 transition cursor-pointer"
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

          <!-- Pagination Bar -->
          <div
            v-if="totalPages > 1"
            data-testid="orders-pagination"
            class="bg-white border border-zinc-200/80 rounded-2xl px-4 py-3 flex flex-col sm:flex-row items-center justify-between gap-3 text-xs text-zinc-500 shadow-xs"
          >
            <div>
              Trang <span class="font-semibold text-zinc-900">{{ currentPage + 1 }}</span> / {{ totalPages }}
              <span class="text-zinc-400">({{ totalElements }} đơn hàng)</span>
            </div>
            <div class="flex items-center gap-1.5">
              <button
                type="button"
                data-testid="orders-prev-page"
                :disabled="currentPage === 0"
                @click="changePage(currentPage - 1)"
                class="px-3 py-1.5 rounded-lg bg-white border border-zinc-200 hover:bg-zinc-50 text-zinc-700 hover:text-zinc-900 disabled:opacity-40 disabled:cursor-not-allowed transition cursor-pointer"
              >
                Trước
              </button>
              <button
                v-for="pageIdx in visiblePages"
                :key="pageIdx"
                type="button"
                @click="changePage(pageIdx)"
                :class="[
                  'w-8 h-8 rounded-lg text-xs font-medium border transition cursor-pointer',
                  currentPage === pageIdx
                    ? 'bg-zinc-900 text-white border-zinc-900'
                    : 'bg-white text-zinc-700 border-zinc-200 hover:bg-zinc-50'
                ]"
              >
                {{ pageIdx + 1 }}
              </button>
              <button
                type="button"
                data-testid="orders-next-page"
                :disabled="currentPage >= totalPages - 1"
                @click="changePage(currentPage + 1)"
                class="px-3 py-1.5 rounded-lg bg-white border border-zinc-200 hover:bg-zinc-50 text-zinc-700 hover:text-zinc-900 disabled:opacity-40 disabled:cursor-not-allowed transition cursor-pointer"
              >
                Sau
              </button>
            </div>
          </div>
        </div>
      </section>
    </div>

    <VietQrPaymentModal
      :order="qrOrder"
      :show-orders-link="false"
      @close="qrOrder = null"
      @confirmed="handleQrConfirmed"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, getCurrentInstance } from 'vue'
import { Search } from 'lucide-vue-next'
import { orderApi } from '@/api/orderApi'
import AccountSidebar, { type AccountTabKey } from '@/components/AccountSidebar.vue'
import VietQrPaymentModal from '@/components/VietQrPaymentModal.vue'
import type { Order } from '@/types'

const instance = getCurrentInstance()
const router = instance?.appContext.config.globalProperties.$router

const handleSidebarTab = (tab: AccountTabKey) => {
  if (tab === 'orders') return
  router?.push({ path: '/profile', query: tab === 'profile' ? {} : { tab } })
}

const orders = ref<Order[]>([])
const loading = ref<boolean>(true)
const qrOrder = ref<Order | null>(null)
const defaultImage = 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500&auto=format&fit=crop&q=60'

const pageSize = 5
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const selectedStatus = ref<string>('ALL')
const keyword = ref('')

const statusTabs = [
  { value: 'ALL', label: 'Tất cả' },
  { value: 'PENDING', label: 'Chờ xác nhận' },
  { value: 'CONFIRMED', label: 'Đã xác nhận' },
  { value: 'SHIPPING', label: 'Đang giao' },
  { value: 'DELIVERED', label: 'Đã giao' },
  { value: 'CANCELLED', label: 'Đã hủy' }
]

const hasActiveFilters = computed(() => selectedStatus.value !== 'ALL' || keyword.value.trim().length > 0)

const visiblePages = computed(() => {
  const total = totalPages.value
  if (total <= 5) return Array.from({ length: total }, (_, i) => i)
  const start = Math.max(0, Math.min(currentPage.value - 2, total - 5))
  return Array.from({ length: 5 }, (_, i) => start + i)
})

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

const isQrPaymentMethod = (method?: string) => method === 'BANK_TRANSFER' || method === 'VNPAY'

const canPayWithVietQr = (order: Order): boolean =>
  isQrPaymentMethod(order.paymentMethod) &&
  order.paymentStatus === 'PENDING' &&
  order.orderStatus !== 'CANCELLED'

const getPaymentLabel = (order: Order): string => {
  const methodLabel = isQrPaymentMethod(order.paymentMethod) ? 'VietQR' : order.paymentMethod || 'COD'
  if (order.paymentStatus === 'PAID') return `${methodLabel} • Đã thanh toán`
  if (order.paymentStatus === 'REFUNDED') return `${methodLabel} • Đã hoàn tiền`
  if (isQrPaymentMethod(order.paymentMethod)) return `${methodLabel} • Chờ thanh toán`
  return `${methodLabel} • Khi nhận hàng`
}

const getPaymentBadgeClass = (order: Order): string => {
  if (order.paymentStatus === 'PAID') return 'bg-emerald-50 text-emerald-700 border border-emerald-200'
  if (isQrPaymentMethod(order.paymentMethod) && order.paymentStatus === 'PENDING') {
    return 'bg-amber-50 text-amber-700 border border-amber-200'
  }
  return 'bg-zinc-100 text-zinc-700 border border-zinc-200'
}

const handleQrConfirmed = (updated: Order) => {
  qrOrder.value = updated
  const idx = orders.value.findIndex(o => o.id === updated.id)
  if (idx !== -1) orders.value[idx] = updated
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await orderApi.getMyOrders({
      page: currentPage.value,
      size: pageSize,
      status: selectedStatus.value !== 'ALL' ? selectedStatus.value : undefined,
      keyword: keyword.value.trim() || undefined
    })
    if (res.success && res.data) {
      orders.value = res.data.content || []
      totalElements.value = res.data.totalElements ?? orders.value.length
      totalPages.value = res.data.totalPages ?? (orders.value.length > 0 ? 1 : 0)
    }
  } catch (err) {
    console.error('Lỗi tải danh sách đơn hàng:', err)
  } finally {
    loading.value = false
  }
}

const selectStatus = (status: string) => {
  selectedStatus.value = status
  currentPage.value = 0
  fetchOrders()
}

const handleSearch = () => {
  currentPage.value = 0
  fetchOrders()
}

const resetFilters = () => {
  selectedStatus.value = 'ALL'
  keyword.value = ''
  currentPage.value = 0
  fetchOrders()
}

const changePage = (page: number) => {
  if (page < 0 || page >= totalPages.value) return
  currentPage.value = page
  fetchOrders()
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
