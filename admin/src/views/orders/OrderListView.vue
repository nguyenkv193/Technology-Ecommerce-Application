<template>
  <div class="space-y-6">
    <!-- Top Bar -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-5 border-b border-zinc-800/80">
      <div>
        <div class="flex items-center gap-2">
          <h1 class="text-xl font-semibold tracking-tight text-white">Quản lý Đơn hàng</h1>
          <span class="px-2 py-0.5 rounded-full text-[10px] font-mono font-medium bg-zinc-900 text-zinc-400 border border-zinc-800">
            {{ totalElements }} đơn
          </span>
        </div>
        <p class="text-xs text-zinc-400 mt-1 font-mono">Theo dõi, duyệt đơn thanh toán và điều phối giao vận toàn sàn</p>
      </div>

      <button
        @click="loadOrders"
        class="px-3.5 py-2 rounded-lg bg-zinc-900 hover:bg-zinc-800 text-zinc-300 hover:text-white text-xs font-medium border border-zinc-800 hover:border-zinc-700 flex items-center gap-2 transition-all self-start sm:self-auto font-mono"
      >
        <RefreshCw class="w-3.5 h-3.5" :class="{ 'animate-spin': isLoading }" />
        <span>Làm mới</span>
      </button>
    </div>

    <!-- Status Tabs (Linear Style) -->
    <div class="flex items-center gap-1.5 overflow-x-auto pb-2 border-b border-zinc-800/60 font-mono">
      <button
        v-for="tab in statusTabs"
        :key="tab.value"
        @click="selectTab(tab.value)"
        class="px-3 py-1.5 rounded-lg text-xs transition-all shrink-0 flex items-center gap-1.5"
        :class="[
          currentStatusTab === tab.value
            ? 'bg-zinc-800 text-white font-medium shadow-sm border border-zinc-700/60'
            : 'text-zinc-400 hover:text-zinc-200 hover:bg-zinc-900/60'
        ]"
      >
        <span>{{ tab.label }}</span>
      </button>
    </div>

    <!-- Data Table (Linear High-Density) -->
    <div class="bg-zinc-900/60 border border-zinc-800/80 rounded-xl overflow-hidden shadow-subtle">
      <div class="overflow-x-auto">
        <table class="w-full text-left text-xs text-zinc-300">
          <thead class="text-[11px] font-mono font-medium text-zinc-400 uppercase bg-zinc-950/70 border-b border-zinc-800/80">
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
          <tbody class="divide-y divide-zinc-800/60 font-mono">
            <tr v-if="isLoading">
              <td colspan="7" class="py-12 text-center text-zinc-500 font-sans">
                <div class="flex items-center justify-center gap-2">
                  <Loader2 class="w-4 h-4 animate-spin text-zinc-400" />
                  <span>Đang tải danh sách đơn hàng...</span>
                </div>
              </td>
            </tr>
            <tr v-else-if="orders.length === 0">
              <td colspan="7" class="py-12 text-center text-zinc-500 text-xs font-sans">
                Không có đơn hàng nào trong mục này.
              </td>
            </tr>
            <tr
              v-for="order in orders"
              :key="order.id"
              class="hover:bg-zinc-800/40 transition-colors group"
            >
              <td class="py-3 px-5 font-semibold text-zinc-200">
                <span class="px-2 py-0.5 rounded bg-zinc-950 border border-zinc-800 text-[11px]">
                  {{ order.orderCode }}
                </span>
              </td>
              <td class="py-3 px-4 font-sans">
                <div class="font-medium text-zinc-200 text-xs">{{ order.customerName }}</div>
                <div class="text-[11px] font-mono text-zinc-500">{{ order.customerPhone }}</div>
              </td>
              <td class="py-3 px-4 text-xs">
                <span class="px-2 py-0.5 rounded bg-zinc-950 border border-zinc-800 text-zinc-300 font-mono text-[11px]">
                  {{ order.paymentMethod }}
                </span>
              </td>
              <td class="py-3 px-4 font-semibold text-zinc-100 tabular-nums">
                {{ formatCurrency(order.totalAmount) }}
              </td>
              <td class="py-3 px-4 font-sans">
                <BadgeStatus :status="order.status" />
              </td>
              <td class="py-3 px-4 text-xs text-zinc-400 tabular-nums">
                {{ formatDate(order.createdAt) }}
              </td>
              <td class="py-3 px-5 text-right font-sans">
                <button
                  @click="openDetail(order)"
                  class="px-2.5 py-1 rounded-md bg-zinc-800 hover:bg-zinc-700 text-zinc-200 border border-zinc-700/60 text-xs font-medium transition-colors"
                >
                  Xử lý
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination Footer -->
      <div class="p-3.5 border-t border-zinc-800/80 flex items-center justify-between text-xs text-zinc-400 font-mono">
        <div>
          Tổng số <span class="font-semibold text-zinc-200">{{ totalElements }}</span> đơn hàng
        </div>
        <div class="flex items-center gap-1.5">
          <button
            @click="changePage(currentPage - 1)"
            :disabled="currentPage === 0"
            class="px-2.5 py-1 rounded-md bg-zinc-900 border border-zinc-800 hover:bg-zinc-800 text-zinc-300 hover:text-white disabled:opacity-40 disabled:cursor-not-allowed transition-colors text-xs"
          >
            Trước
          </button>
          <span class="text-zinc-500 px-2">Trang {{ currentPage + 1 }} / {{ totalPages || 1 }}</span>
          <button
            @click="changePage(currentPage + 1)"
            :disabled="currentPage >= totalPages - 1"
            class="px-2.5 py-1 rounded-md bg-zinc-900 border border-zinc-800 hover:bg-zinc-800 text-zinc-300 hover:text-white disabled:opacity-40 disabled:cursor-not-allowed transition-colors text-xs"
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
