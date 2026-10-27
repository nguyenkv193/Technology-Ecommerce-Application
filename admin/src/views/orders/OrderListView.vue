<template>
  <div class="space-y-6">
    <!-- Top Bar -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold tracking-tight text-slate-100">Quản lý Đơn hàng</h1>
        <p class="text-xs text-slate-400 mt-1">Theo dõi, duyệt đơn và điều phối giao vận toàn sàn</p>
      </div>
      <button
        @click="loadOrders"
        class="px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold border border-slate-700 flex items-center gap-2 transition-colors"
      >
        <RefreshCw class="w-3.5 h-3.5" :class="{ 'animate-spin': isLoading }" />
        <span>Làm mới</span>
      </button>
    </div>

    <!-- Status Tabs -->
    <div class="flex items-center gap-2 overflow-x-auto pb-2 border-b border-slate-800/80">
      <button
        v-for="tab in statusTabs"
        :key="tab.value"
        @click="selectTab(tab.value)"
        class="px-4 py-2 rounded-xl text-xs font-semibold transition-all shrink-0 flex items-center gap-2"
        :class="[
          currentStatusTab === tab.value
            ? 'bg-blue-600 text-white shadow-md shadow-blue-600/20'
            : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
        ]"
      >
        <span>{{ tab.label }}</span>
      </button>
    </div>

    <!-- Data Table -->
    <div class="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-sm">
      <div class="overflow-x-auto">
        <table class="w-full text-left text-sm text-slate-300">
          <thead class="text-xs font-semibold text-slate-400 uppercase bg-slate-950/60 border-b border-slate-800">
            <tr>
              <th class="py-3.5 px-6 font-mono">Mã đơn hàng</th>
              <th class="py-3.5 px-4">Khách hàng</th>
              <th class="py-3.5 px-4">Thanh toán</th>
              <th class="py-3.5 px-4">Tổng tiền</th>
              <th class="py-3.5 px-4">Trạng thái</th>
              <th class="py-3.5 px-4">Ngày đặt</th>
              <th class="py-3.5 px-6 text-right">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-800/60">
            <tr v-if="isLoading">
              <td colspan="7" class="py-12 text-center text-slate-500">
                <div class="flex items-center justify-center gap-2">
                  <Loader2 class="w-5 h-5 animate-spin text-blue-500" />
                  <span>Đang tải danh sách đơn hàng...</span>
                </div>
              </td>
            </tr>
            <tr v-else-if="orders.length === 0">
              <td colspan="7" class="py-12 text-center text-slate-500 text-xs">
                Không có đơn hàng nào trong mục này.
              </td>
            </tr>
            <tr
              v-for="order in orders"
              :key="order.id"
              class="hover:bg-slate-800/40 transition-colors"
            >
              <td class="py-3.5 px-6 font-mono font-semibold text-slate-100">
                {{ order.orderCode }}
              </td>
              <td class="py-3.5 px-4">
                <div class="font-medium text-slate-100 text-xs">{{ order.customerName }}</div>
                <div class="text-[11px] text-slate-400">{{ order.customerPhone }}</div>
              </td>
              <td class="py-3.5 px-4 text-xs">
                <span class="px-2 py-0.5 rounded bg-slate-950 border border-slate-800 text-slate-300 font-mono text-[11px]">
                  {{ order.paymentMethod }}
                </span>
              </td>
              <td class="py-3.5 px-4 font-semibold text-emerald-400 text-xs">
                {{ formatCurrency(order.totalAmount) }}
              </td>
              <td class="py-3.5 px-4">
                <BadgeStatus :status="order.status" />
              </td>
              <td class="py-3.5 px-4 text-xs text-slate-400">
                {{ formatDate(order.createdAt) }}
              </td>
              <td class="py-3.5 px-6 text-right">
                <button
                  @click="openDetail(order)"
                  class="px-3 py-1.5 rounded-lg bg-blue-600/10 border border-blue-500/20 hover:bg-blue-600/20 text-blue-400 text-xs font-semibold transition-colors"
                >
                  Xử lý
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination Footer -->
      <div class="p-4 border-t border-slate-800/80 flex items-center justify-between text-xs text-slate-400">
        <div>
          Tổng số <span class="font-semibold text-slate-200">{{ totalElements }}</span> đơn hàng
        </div>
        <div class="flex items-center gap-2">
          <button
            @click="changePage(currentPage - 1)"
            :disabled="currentPage === 0"
            class="px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-200 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
          >
            Trước
          </button>
          <span class="text-slate-400 px-2">Trang {{ currentPage + 1 }} / {{ totalPages || 1 }}</span>
          <button
            @click="changePage(currentPage + 1)"
            :disabled="currentPage >= totalPages - 1"
            class="px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-200 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
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
import { ref, onMounted } from 'vue'
import { RefreshCw, Loader2 } from 'lucide-vue-next'
import BadgeStatus from '@/components/common/BadgeStatus.vue'
import OrderDetailModal from './OrderDetailModal.vue'
import { fetchOrders } from '@/api/orders'
import type { Order } from '@/types'

const orders = ref<Order[]>([])
const isLoading = ref(false)
const currentPage = ref(0)
const totalPages = ref(1)
const totalElements = ref(0)
const currentStatusTab = ref('ALL')

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
