<template>
  <div class="space-y-8">
    <!-- Top Welcome Banner -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 p-6 rounded-2xl bg-gradient-to-r from-blue-900/40 via-slate-900 to-indigo-900/30 border border-blue-500/20 shadow-lg shadow-blue-950/40">
      <div>
        <h1 class="text-2xl font-bold tracking-tight text-white flex items-center gap-2">
          <span>Xin chào, {{ authStore.user?.fullName || 'Quản trị viên' }}</span>
          <span class="text-xl">👋</span>
        </h1>
        <p class="text-sm text-slate-300 mt-1">
          Hệ thống đang hoạt động ổn định. Dưới đây là các chỉ số kinh doanh và vận hành hôm nay.
        </p>
      </div>
      <div class="flex items-center gap-3 shrink-0">
        <router-link
          to="/products/create"
          class="px-4 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-500 text-white text-xs font-semibold shadow-md shadow-blue-600/30 flex items-center gap-2 transition-all"
        >
          <Plus class="w-4 h-4" />
          <span>Thêm sản phẩm</span>
        </router-link>
        <router-link
          to="/system-ops"
          class="px-4 py-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold border border-slate-700 flex items-center gap-2 transition-all"
        >
          <RefreshCw class="w-4 h-4" />
          <span>Đồng bộ Search</span>
        </router-link>
      </div>
    </div>

    <!-- Stat Cards Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
      <StatCard
        title="Tổng doanh thu"
        :value="formatCurrency(metrics.totalRevenue)"
        :icon="DollarSign"
        theme="emerald"
        change="12.5%"
        :is-positive="true"
      />
      <StatCard
        title="Tổng đơn hàng"
        :value="metrics.totalOrders"
        :icon="ShoppingBag"
        theme="blue"
        change="8.2%"
        :is-positive="true"
      />
      <StatCard
        title="Đơn chờ xác nhận"
        :value="metrics.pendingOrders"
        :icon="Clock"
        theme="amber"
      />
      <StatCard
        title="Sản phẩm hoạt động"
        :value="metrics.totalProducts"
        :icon="Layers"
        theme="purple"
      />
    </div>

    <!-- Quick Operations & Recent Orders Layout -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- Recent Orders Table (2 Cols) -->
      <div class="lg:col-span-2 bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-sm space-y-4">
        <div class="flex items-center justify-between">
          <div>
            <h2 class="text-base font-semibold text-slate-100">Đơn hàng mới nhất</h2>
            <p class="text-xs text-slate-400 mt-0.5">Các đơn hàng phát sinh gần đây cần theo dõi</p>
          </div>
          <router-link
            to="/orders"
            class="text-xs font-medium text-blue-400 hover:text-blue-300 flex items-center gap-1 transition-colors"
          >
            <span>Xem tất cả</span>
            <ChevronRight class="w-3.5 h-3.5" />
          </router-link>
        </div>

        <!-- Table -->
        <div class="overflow-x-auto">
          <table class="w-full text-left text-sm text-slate-300">
            <thead class="text-xs font-semibold text-slate-400 uppercase bg-slate-950/60 border-b border-slate-800">
              <tr>
                <th class="py-3 px-4 rounded-l-lg">Mã đơn</th>
                <th class="py-3 px-4">Khách hàng</th>
                <th class="py-3 px-4">Tổng tiền</th>
                <th class="py-3 px-4">Trạng thái</th>
                <th class="py-3 px-4 text-right rounded-r-lg">Hành động</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-800/60">
              <tr v-if="isLoading" class="text-center py-8">
                <td colspan="5" class="py-8 text-slate-500">
                  <div class="flex items-center justify-center gap-2">
                    <Loader2 class="w-4 h-4 animate-spin text-blue-500" />
                    <span>Đang tải dữ liệu đơn hàng...</span>
                  </div>
                </td>
              </tr>
              <tr v-else-if="recentOrders.length === 0" class="text-center">
                <td colspan="5" class="py-8 text-slate-500 text-xs">
                  Chưa có đơn hàng nào được tạo.
                </td>
              </tr>
              <tr
                v-for="order in recentOrders"
                :key="order.id"
                class="hover:bg-slate-800/40 transition-colors"
              >
                <td class="py-3.5 px-4 font-mono font-medium text-slate-200">
                  {{ order.orderCode }}
                </td>
                <td class="py-3.5 px-4">
                  <div class="font-medium text-slate-200">{{ order.customerName }}</div>
                  <div class="text-[11px] text-slate-400">{{ order.customerPhone }}</div>
                </td>
                <td class="py-3.5 px-4 font-medium text-slate-100">
                  {{ formatCurrency(order.totalAmount) }}
                </td>
                <td class="py-3.5 px-4">
                  <BadgeStatus :status="order.status" />
                </td>
                <td class="py-3.5 px-4 text-right">
                  <router-link
                    to="/orders"
                    class="text-xs font-medium text-blue-400 hover:text-blue-300 px-2.5 py-1.5 rounded-lg hover:bg-slate-800 transition-colors"
                  >
                    Chi tiết
                  </router-link>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Quick Operations Sidebar (1 Col) -->
      <div class="space-y-6">
        <!-- Quick Action Panel -->
        <div class="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-sm space-y-4">
          <h2 class="text-base font-semibold text-slate-100">Tác vụ nhanh</h2>
          <div class="space-y-2.5">
            <router-link
              to="/products/create"
              class="w-full flex items-center justify-between p-3.5 rounded-xl bg-slate-950/60 border border-slate-800/80 hover:border-blue-500/40 hover:bg-slate-800/40 transition-all group"
            >
              <div class="flex items-center gap-3">
                <div class="w-9 h-9 rounded-lg bg-blue-500/10 text-blue-400 flex items-center justify-center">
                  <PackagePlus class="w-5 h-5" />
                </div>
                <div>
                  <div class="text-xs font-semibold text-slate-200 group-hover:text-blue-400 transition-colors">Tạo sản phẩm mới</div>
                  <div class="text-[11px] text-slate-400">Đăng bán và cấu hình SKU</div>
                </div>
              </div>
              <ChevronRight class="w-4 h-4 text-slate-500 group-hover:translate-x-0.5 transition-transform" />
            </router-link>

            <router-link
              to="/orders"
              class="w-full flex items-center justify-between p-3.5 rounded-xl bg-slate-950/60 border border-slate-800/80 hover:border-amber-500/40 hover:bg-slate-800/40 transition-all group"
            >
              <div class="flex items-center gap-3">
                <div class="w-9 h-9 rounded-lg bg-amber-500/10 text-amber-400 flex items-center justify-center">
                  <Clock class="w-5 h-5" />
                </div>
                <div>
                  <div class="text-xs font-semibold text-slate-200 group-hover:text-amber-400 transition-colors">Duyệt đơn chờ</div>
                  <div class="text-[11px] text-slate-400">Xác nhận đơn và giao vận</div>
                </div>
              </div>
              <ChevronRight class="w-4 h-4 text-slate-500 group-hover:translate-x-0.5 transition-transform" />
            </router-link>

            <router-link
              to="/system-ops"
              class="w-full flex items-center justify-between p-3.5 rounded-xl bg-slate-950/60 border border-slate-800/80 hover:border-emerald-500/40 hover:bg-slate-800/40 transition-all group"
            >
              <div class="flex items-center gap-3">
                <div class="w-9 h-9 rounded-lg bg-emerald-500/10 text-emerald-400 flex items-center justify-center">
                  <Search class="w-5 h-5" />
                </div>
                <div>
                  <div class="text-xs font-semibold text-slate-200 group-hover:text-emerald-400 transition-colors">Đồng bộ Elasticsearch</div>
                  <div class="text-[11px] text-slate-400">Cập nhật chỉ mục tìm kiếm</div>
                </div>
              </div>
              <ChevronRight class="w-4 h-4 text-slate-500 group-hover:translate-x-0.5 transition-transform" />
            </router-link>
          </div>
        </div>

        <!-- Infrastructure Status Card -->
        <div class="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-sm space-y-3">
          <h2 class="text-xs font-semibold uppercase tracking-wider text-slate-400">Trạng thái Hạ tầng</h2>
          <div class="space-y-2 text-xs">
            <div class="flex items-center justify-between py-1.5 border-b border-slate-800/60">
              <span class="text-slate-300">Spring Boot REST API</span>
              <span class="text-emerald-400 font-medium flex items-center gap-1.5">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span> Port 8080
              </span>
            </div>
            <div class="flex items-center justify-between py-1.5 border-b border-slate-800/60">
              <span class="text-slate-300">Elasticsearch Service</span>
              <span class="text-emerald-400 font-medium flex items-center gap-1.5">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span> Ready
              </span>
            </div>
            <div class="flex items-center justify-between py-1.5">
              <span class="text-slate-300">PostgreSQL Database</span>
              <span class="text-emerald-400 font-medium flex items-center gap-1.5">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span> Connected
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  DollarSign,
  ShoppingBag,
  Clock,
  Layers,
  Plus,
  RefreshCw,
  ChevronRight,
  PackagePlus,
  Search,
  Loader2
} from 'lucide-vue-next'
import { useAdminAuthStore } from '@/stores/auth'
import StatCard from '@/components/common/StatCard.vue'
import BadgeStatus from '@/components/common/BadgeStatus.vue'
import { fetchDashboardMetrics, fetchRecentOrders } from '@/api/dashboard'
import type { DashboardMetrics, Order } from '@/types'

const authStore = useAdminAuthStore()

const isLoading = ref(true)
const metrics = ref<DashboardMetrics>({
  totalRevenue: 0,
  totalOrders: 0,
  pendingOrders: 0,
  totalProducts: 0,
  lowStockCount: 0
})
const recentOrders = ref<Order[]>([])

function formatCurrency(val: number): string {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val || 0)
}

onMounted(async () => {
  isLoading.value = true
  try {
    const [metricData, ordersData] = await Promise.all([
      fetchDashboardMetrics(),
      fetchRecentOrders()
    ])
    metrics.value = metricData
    recentOrders.value = ordersData
  } finally {
    isLoading.value = false
  }
})
</script>
