<template>
  <div class="space-y-6">
    <!-- Top Bar -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-5 border-b border-zinc-200">
      <div>
        <div class="flex items-center gap-2">
          <h1 class="text-xl font-semibold tracking-tight text-zinc-900">Quản lý Đơn hàng</h1>
          <span class="px-2 py-0.5 rounded-full text-[10px] font-mono font-medium bg-zinc-100 text-zinc-700 border border-zinc-200">
            {{ totalElements }} đơn
          </span>
        </div>
        <p class="text-xs text-zinc-500 mt-1 font-mono">Theo dõi, duyệt đơn thanh toán và điều phối giao vận toàn sàn</p>
      </div>

      <button
        @click="loadOrders"
        class="px-3.5 py-2 rounded-lg bg-white hover:bg-zinc-50 text-zinc-700 hover:text-zinc-900 text-xs font-medium border border-zinc-200 shadow-xs flex items-center gap-2 transition-all self-start sm:self-auto font-mono"
      >
        <RefreshCw class="w-3.5 h-3.5" :class="{ 'animate-spin': isLoading }" />
        <span>Làm mới</span>
      </button>
    </div>

    <!-- Status Tabs (Linear Style) -->
      <div class="flex items-center gap-1.5 overflow-x-auto pb-2 border-b border-zinc-200 font-mono">
      <button
        v-for="tab in statusTabs"
        :key="tab.value"
        @click="selectTab(tab.value)"
        class="px-3 py-1.5 rounded-lg text-xs transition-all shrink-0 flex items-center gap-1.5"
        :class="[
          currentStatusTab === tab.value
            ? 'bg-zinc-900 text-white font-medium shadow-xs border border-zinc-900'
            : 'text-zinc-600 hover:text-zinc-900 hover:bg-zinc-100'
        ]"
      >
        <span>{{ tab.label }}</span>
      </button>
    </div>

    <!-- Search & Payment Filters -->
    <div class="bg-white border border-zinc-200 rounded-xl p-3 flex flex-col lg:flex-row gap-3 items-stretch lg:items-center justify-between shadow-xs">
      <div class="relative w-full lg:w-80">
        <Search class="w-3.5 h-3.5 text-zinc-400 absolute left-3 top-1/2 -translate-y-1/2" />
        <input
          v-model="keyword"
          type="text"
          placeholder="Tìm theo mã đơn, tên khách, SĐT..."
          class="w-full bg-white border border-zinc-200 rounded-lg pl-9 pr-8 py-2 text-xs text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900"
        />
        <button
          v-if="keyword"
          type="button"
          @click="keyword = ''"
          class="absolute right-2.5 top-1/2 -translate-y-1/2 text-zinc-400 hover:text-zinc-600 text-xs"
        >
          ✕
        </button>
      </div>

      <div class="flex flex-wrap items-center gap-2 justify-end">
        <CustomSelect v-model="selectedPaymentMethod" :options="paymentMethodOptions" />
        <CustomSelect v-model="selectedPaymentStatus" :options="paymentStatusOptions" align="right" />
        <button
          v-if="keyword || selectedPaymentMethod !== 'ALL' || selectedPaymentStatus !== 'ALL'"
          type="button"
          @click="resetOrderFilters"
          class="h-9 px-2.5 rounded-lg text-zinc-600 hover:text-zinc-900 hover:bg-zinc-100 text-xs transition-colors cursor-pointer"
        >
          Đặt lại
        </button>
      </div>
    </div>

    <!-- Data Table (Linear High-Density) -->
    <div class="bg-white border border-zinc-200 rounded-xl overflow-hidden shadow-sm">
      <div class="overflow-x-auto">
        <table class="w-full text-left text-xs text-zinc-700">
          <thead class="text-[11px] font-mono font-medium text-zinc-500 uppercase bg-zinc-50/80 border-b border-zinc-200">
            <tr>
              <th class="py-3 px-5 font-mono">Mã đơn hàng</th>
              <th class="py-3 px-4">Khách hàng</th>
              <th class="py-3 px-4">Thanh toán</th>
              <th class="py-3 px-4">Tổng tiền</th>
              <th class="py-3 px-4">Trạng thái</th>
              <th class="py-3 px-4">Ngày đặt</th>
              <th class="py-3 px-5 text-right">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-zinc-100 font-mono">
            <tr v-if="isLoading">
              <td colspan="7" class="py-12 text-center text-zinc-500 font-sans">
                <div class="flex items-center justify-center gap-2">
                  <Loader2 class="w-4 h-4 animate-spin text-zinc-500" />
                  <span>Đang tải danh sách đơn hàng...</span>
                </div>
              </td>
            </tr>
            <tr v-else-if="filteredOrders.length === 0">
              <td colspan="7" class="py-12 text-center text-zinc-400 text-xs font-sans">
                Không có đơn hàng nào phù hợp trong mục này.
              </td>
            </tr>
            <tr
              v-for="order in filteredOrders"
              :key="order.id"
              class="hover:bg-zinc-50/70 transition-colors group"
            >
              <td class="py-3 px-5 font-semibold text-zinc-900">
                <span class="px-2 py-0.5 rounded bg-zinc-100 border border-zinc-200 text-[11px]">
                  {{ order.orderCode }}
                </span>
              </td>
              <td class="py-3 px-4 font-sans">
                <div class="font-medium text-zinc-900 text-xs">{{ order.recipientName }}</div>
                <div class="text-[11px] font-mono text-zinc-500">{{ order.phoneNumber }}</div>
              </td>
              <td class="py-3 px-4 text-xs">
                <span class="px-2 py-0.5 rounded bg-zinc-100 border border-zinc-200 text-zinc-700 font-mono text-[11px]">
                  {{ order.paymentMethod }}
                </span>
              </td>
              <td class="py-3 px-4 font-semibold text-zinc-900 tabular-nums">
                {{ formatCurrency(order.finalAmount ?? order.totalAmount) }}
              </td>
              <td class="py-3 px-4 font-sans">
                <BadgeStatus :status="order.orderStatus" />
              </td>
              <td class="py-3 px-4 text-xs text-zinc-500 tabular-nums">
                {{ formatDate(order.createdAt) }}
              </td>
              <td class="py-3 px-5 text-right font-sans">
                <button
                  @click="openDetail(order)"
                  class="px-2.5 py-1 rounded-md bg-white hover:bg-zinc-50 text-zinc-700 hover:text-zinc-900 border border-zinc-200 text-xs font-medium shadow-xs transition-colors"
                >
                  Xử lý
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination Footer -->
      <div class="p-3.5 border-t border-zinc-200 flex items-center justify-between text-xs text-zinc-500 font-mono">
        <div>
          Tổng số <span class="font-semibold text-zinc-900">{{ totalElements }}</span> đơn hàng
        </div>
        <div class="flex items-center gap-1.5">
          <button
            @click="changePage(currentPage - 1)"
            :disabled="currentPage === 0"
            class="px-2.5 py-1 rounded-md bg-white border border-zinc-200 hover:bg-zinc-50 text-zinc-700 hover:text-zinc-900 disabled:opacity-40 disabled:cursor-not-allowed transition-colors text-xs shadow-xs"
          >
            Trước
          </button>
          <span class="text-zinc-500 px-2">Trang {{ currentPage + 1 }} / {{ totalPages || 1 }}</span>
          <button
            @click="changePage(currentPage + 1)"
            :disabled="currentPage >= totalPages - 1"
            class="px-2.5 py-1 rounded-md bg-white border border-zinc-200 hover:bg-zinc-50 text-zinc-700 hover:text-zinc-900 disabled:opacity-40 disabled:cursor-not-allowed transition-colors text-xs shadow-xs"
          >
            Sau
          </button>
        </div>
      </div>
    </div>

    <!-- Order Detail Modal -->
    <OrderDetailModal
      :is-open="isDetailOpen"
      :order="selectedOrder"
      @close="isDetailOpen = false"
      @updated="handleOrderUpdated"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { RefreshCw, Search, Loader2 } from 'lucide-vue-next'
import BadgeStatus from '@/components/common/BadgeStatus.vue'
import CustomSelect, { type CustomSelectOption } from '@/components/common/CustomSelect.vue'
import OrderDetailModal from './OrderDetailModal.vue'
import { fetchOrders } from '@/api/orders'
import type { Order } from '@/types'

const orders = ref<Order[]>([])
const isLoading = ref(false)
const currentPage = ref(0)
const totalPages = ref(1)
const totalElements = ref(0)
const currentStatusTab = ref('ALL')
const keyword = ref('')
const selectedPaymentMethod = ref('ALL')
const selectedPaymentStatus = ref('ALL')

const paymentMethodOptions: CustomSelectOption[] = [
  { value: 'ALL', label: 'Tất cả thanh toán' },
  { value: 'COD', label: 'COD (Tiền mặt)' },
  { value: 'BANK_TRANSFER', label: 'VietQR (Chuyển khoản)' }
]

const paymentStatusOptions: CustomSelectOption[] = [
  { value: 'ALL', label: 'Tất cả TT thanh toán' },
  { value: 'PENDING', label: 'Chờ thanh toán' },
  { value: 'PAID', label: 'Đã thanh toán' },
  { value: 'FAILED', label: 'Thất bại / Hết hạn' },
  { value: 'REFUNDED', label: 'Đã hoàn tiền' }
]

const filteredOrders = computed(() => {
  const kw = keyword.value.trim().toLowerCase()
  return orders.value.filter((o) => {
    const matchesKw =
      !kw ||
      o.orderCode.toLowerCase().includes(kw) ||
      o.recipientName.toLowerCase().includes(kw) ||
      (o.phoneNumber || '').toLowerCase().includes(kw)
    const matchesMethod =
      selectedPaymentMethod.value === 'ALL' || o.paymentMethod === selectedPaymentMethod.value
    const matchesPayStatus =
      selectedPaymentStatus.value === 'ALL' || o.paymentStatus === selectedPaymentStatus.value
    return matchesKw && matchesMethod && matchesPayStatus
  })
})

function resetOrderFilters() {
  keyword.value = ''
  selectedPaymentMethod.value = 'ALL'
  selectedPaymentStatus.value = 'ALL'
}

const isDetailOpen = ref(false)
const selectedOrder = ref<Order | null>(null)

const statusTabs = [
  { label: 'Tất cả', value: 'ALL' },
  { label: 'Chờ xác nhận', value: 'PENDING' },
  { label: 'Đã xác nhận', value: 'CONFIRMED' },
  { label: 'Đang giao', value: 'SHIPPING' },
  { label: 'Đã giao', value: 'DELIVERED' },
  { label: 'Đã hủy', value: 'CANCELLED' }
]

function formatCurrency(val: number): string {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val || 0)
}

function formatDate(dateStr: string): string {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleDateString('vi-VN')
}

async function loadOrders() {
  isLoading.value = true
  try {
    const res = await fetchOrders(currentPage.value, 10, currentStatusTab.value)
    orders.value = res.content || []
    totalPages.value = res.totalPages || 1
    totalElements.value = res.totalElements || 0
  } finally {
    isLoading.value = false
  }
}

function selectTab(status: string) {
  currentStatusTab.value = status
  currentPage.value = 0
  loadOrders()
}

function changePage(page: number) {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
    loadOrders()
  }
}

function openDetail(order: Order) {
  selectedOrder.value = order
  isDetailOpen.value = true
}

function handleOrderUpdated() {
  isDetailOpen.value = false
  loadOrders()
}

onMounted(() => {
  loadOrders()
})
</script>
