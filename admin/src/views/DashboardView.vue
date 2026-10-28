<template>
  <div class="space-y-6">
    <!-- Top Minimalist Welcome Header -->
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-6 border-b border-zinc-800/80">
      <div>
        <div class="flex items-center gap-2">
          <h1 class="text-xl font-semibold tracking-tight text-white flex items-center gap-2">
            <span>Xin chào, {{ authStore.user?.fullName || 'Quản trị viên' }}</span>
          </h1>
          <span class="px-2 py-0.5 rounded-full text-[10px] font-mono font-medium bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
            Hệ thống ổn định
          </span>
        </div>
        <p class="text-xs text-zinc-400 mt-1 font-mono">
          {{ formattedDate }} • Tổng quan tình hình kinh doanh và vận hành sàn
        </p>
      </div>

      <div class="flex items-center gap-2.5 shrink-0">
        <router-link
          to="/products/create"
          class="px-3.5 py-2 rounded-lg bg-white hover:bg-zinc-200 text-zinc-950 text-xs font-medium shadow-sm flex items-center gap-2 transition-all"
        >
          <Plus class="w-3.5 h-3.5" />
          <span>Thêm sản phẩm</span>
        </router-link>
        <router-link
          to="/system-ops"
          class="px-3.5 py-2 rounded-lg bg-zinc-900 hover:bg-zinc-800 text-zinc-300 hover:text-white text-xs font-medium border border-zinc-800 hover:border-zinc-700 flex items-center gap-2 transition-all"
        >
          <RefreshCw class="w-3.5 h-3.5" />
          <span>Đồng bộ Search</span>
        </router-link>
      </div>
    </div>

    <!-- Stat Cards Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
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

    <!-- Interactive Analytics Chart (Vercel Minimalist Style) -->
    <div class="p-6 rounded-xl bg-zinc-900/60 border border-zinc-800/80 shadow-subtle relative overflow-hidden">
      <!-- Top Chart Controls -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div>
          <div class="flex items-center gap-2">
            <h2 class="text-sm font-semibold text-zinc-100 tracking-tight">Doanh thu & Lưu lượng đơn</h2>
            <span class="text-[11px] font-mono text-zinc-500">Live Analytics</span>
          </div>
          <p class="text-xs text-zinc-400 mt-0.5">Biểu đồ tổng hợp doanh thu theo chu kỳ thời gian</p>
        </div>

        <div class="flex items-center gap-1.5 p-1 rounded-lg bg-zinc-950 border border-zinc-800/80 text-xs font-mono self-start sm:self-auto">
          <button
            v-for="range in timeRanges"
            :key="range.id"
            @click="activeRange = range.id"
            class="px-2.5 py-1 rounded-md transition-all text-[11px]"
            :class="activeRange === range.id ? 'bg-zinc-800 text-white font-medium shadow-sm' : 'text-zinc-400 hover:text-zinc-200'"
          >
            {{ range.label }}
          </button>
        </div>
      </div>

      <!-- Key Metrics Header Ribbon -->
      <div class="grid grid-cols-2 sm:grid-cols-4 gap-4 py-3 px-4 mb-6 rounded-lg bg-zinc-950/70 border border-zinc-800/60 font-mono text-xs">
        <div>
          <span class="text-zinc-500 text-[10px] uppercase block">Doanh thu trung bình / ngày</span>
          <span class="text-zinc-200 font-semibold tabular-nums">17.850.000 ₫</span>
        </div>
        <div>
          <span class="text-zinc-500 text-[10px] uppercase block">Tỷ lệ hoàn thành đơn</span>
          <span class="text-emerald-400 font-semibold tabular-nums">96.4%</span>
        </div>
        <div>
          <span class="text-zinc-500 text-[10px] uppercase block">Thời gian xác nhận</span>
          <span class="text-zinc-200 font-semibold tabular-nums">~12 phút</span>
        </div>
        <div>
          <span class="text-zinc-500 text-[10px] uppercase block">Giá trị TB / đơn hàng</span>
          <span class="text-zinc-200 font-semibold tabular-nums">2.604.000 ₫</span>
        </div>
      </div>

      <!-- SVG Chart Area -->
      <div class="relative h-60 w-full pt-4">
        <!-- SVG Grid Lines -->
        <div class="absolute inset-0 flex flex-col justify-between pointer-events-none text-[10px] font-mono text-zinc-600">
          <div class="border-b border-zinc-800/40 w-full flex justify-between pr-2"><span>30M</span></div>
          <div class="border-b border-zinc-800/40 w-full flex justify-between pr-2"><span>20M</span></div>
          <div class="border-b border-zinc-800/40 w-full flex justify-between pr-2"><span>10M</span></div>
          <div class="border-b border-zinc-800/40 w-full flex justify-between pr-2"><span>0M</span></div>
        </div>

        <!-- Interactive SVG Curve -->
        <svg
          class="w-full h-full overflow-visible relative z-10"
          preserveAspectRatio="none"
          viewBox="0 0 700 200"
        >
          <defs>
            <linearGradient id="areaGradient" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stop-color="#3b82f6" stop-opacity="0.25" />
              <stop offset="100%" stop-color="#3b82f6" stop-opacity="0" />
            </linearGradient>
            <linearGradient id="lineGradient" x1="0" y1="0" x2="1" y2="0">
              <stop offset="0%" stop-color="#60a5fa" />
              <stop offset="100%" stop-color="#a855f7" />
            </linearGradient>
          </defs>

          <!-- Area fill under line -->
          <path
            d="M 20 160 C 100 130, 150 150, 220 90 C 290 30, 360 80, 440 50 C 520 20, 600 70, 680 30 L 680 200 L 20 200 Z"
            fill="url(#areaGradient)"
          />

          <!-- Glowing main trend line -->
          <path
            d="M 20 160 C 100 130, 150 150, 220 90 C 290 30, 360 80, 440 50 C 520 20, 600 70, 680 30"
            fill="none"
            stroke="url(#lineGradient)"
            stroke-width="2.5"
            stroke-linecap="round"
          />

          <!-- Interactive data points -->
          <g v-for="(point, idx) in chartPoints" :key="idx">
            <circle
              :cx="point.x"
              :cy="point.y"
              r="4"
              class="fill-zinc-950 stroke-blue-400 stroke-2 hover:r-6 hover:fill-blue-400 transition-all cursor-pointer"
            >
              <title>{{ point.day }}: {{ point.amount }} ({{ point.orders }} đơn)</title>
            </circle>
          </g>
        </svg>

        <!-- X Axis Labels -->
        <div class="flex justify-between text-[11px] font-mono text-zinc-500 mt-2 px-2">
          <span v-for="point in chartPoints" :key="point.day">{{ point.day }}</span>
        </div>
      </div>
    </div>

    <!-- Quick Operations & Recent Orders Layout -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Recent Orders Table (2 Cols) -->
      <div class="lg:col-span-2 bg-zinc-900/60 border border-zinc-800/80 rounded-xl p-5 shadow-subtle space-y-4">
        <div class="flex items-center justify-between pb-3 border-b border-zinc-800/60">
          <div>
            <h2 class="text-sm font-semibold text-zinc-100 tracking-tight">Đơn hàng mới nhất</h2>
            <p class="text-xs text-zinc-400 mt-0.5">Các giao dịch phát sinh gần đây trên sàn</p>
          </div>
          <router-link
            to="/orders"
            class="text-xs font-medium text-zinc-400 hover:text-white flex items-center gap-1 transition-colors px-2 py-1 rounded hover:bg-zinc-800"
          >
            <span>Xem tất cả</span>
            <ChevronRight class="w-3.5 h-3.5" />
          </router-link>
        </div>

        <!-- Table -->
        <div class="overflow-x-auto">
          <table class="w-full text-left text-xs text-zinc-300">
            <thead class="text-[11px] font-mono font-medium text-zinc-400 uppercase bg-zinc-950/60 border-b border-zinc-800/80">
              <tr>
                <th class="py-2.5 px-3 rounded-l-lg">Mã đơn</th>
                <th class="py-2.5 px-3">Khách hàng</th>
                <th class="py-2.5 px-3">Tổng tiền</th>
                <th class="py-2.5 px-3">Trạng thái</th>
                <th class="py-2.5 px-3 text-right rounded-r-lg">Hành động</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-zinc-800/60 font-mono">
              <tr v-if="isLoading" class="text-center py-8">
                <td colspan="5" class="py-8 text-zinc-500">
                  <div class="flex items-center justify-center gap-2">
                    <Loader2 class="w-4 h-4 animate-spin text-zinc-400" />
                    <span>Đang tải dữ liệu đơn hàng...</span>
                  </div>
                </td>
              </tr>
              <tr v-else-if="recentOrders.length === 0" class="text-center">
                <td colspan="5" class="py-8 text-zinc-500 text-xs">
                  Chưa có đơn hàng nào được tạo.
                </td>
              </tr>
              <tr
                v-for="order in recentOrders"
                :key="order.id"
                class="hover:bg-zinc-800/40 transition-colors group"
              >
                <td class="py-3 px-3 font-semibold text-zinc-200">
                  <span class="px-1.5 py-0.5 rounded bg-zinc-800/80 border border-zinc-700/50 text-[11px]">
                    {{ order.orderCode }}
                  </span>
                </td>
                <td class="py-3 px-3 font-sans">
                  <div class="font-medium text-zinc-200">{{ order.customerName }}</div>
                  <div class="text-[11px] font-mono text-zinc-500">{{ order.customerPhone }}</div>
                </td>
                <td class="py-3 px-3 font-semibold text-zinc-100 tabular-nums">
                  {{ formatCurrency(order.totalAmount) }}
                </td>
                <td class="py-3 px-3 font-sans">
                  <BadgeStatus :status="order.status" />
                </td>
                <td class="py-3 px-3 text-right font-sans">
                  <router-link
                    to="/orders"
                    class="text-xs font-medium text-zinc-400 hover:text-white px-2.5 py-1 rounded-md hover:bg-zinc-800 transition-colors inline-block"
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
        <div class="bg-zinc-900/60 border border-zinc-800/80 rounded-xl p-5 shadow-subtle space-y-3">
          <div class="flex items-center justify-between pb-2 border-b border-zinc-800/60">
            <h2 class="text-xs font-mono font-medium text-zinc-300 uppercase tracking-wider">Tác vụ nhanh</h2>
            <span class="text-[10px] font-mono text-zinc-500">Shortcuts</span>
          </div>

          <div class="space-y-2">
            <router-link
              to="/products/create"
              class="w-full flex items-center justify-between p-3 rounded-lg bg-zinc-950/70 border border-zinc-800/80 hover:border-zinc-700 hover:bg-zinc-900/90 transition-all group"
            >
              <div class="flex items-center gap-3">
                <div class="w-8 h-8 rounded-md bg-zinc-900 border border-zinc-800 text-zinc-300 flex items-center justify-center group-hover:text-white group-hover:border-zinc-700">
                  <PackagePlus class="w-4 h-4" />
                </div>
                <div>
                  <div class="text-xs font-medium text-zinc-200 group-hover:text-white transition-colors">Tạo sản phẩm mới</div>
                  <div class="text-[11px] text-zinc-500">Đăng bán và cấu hình SKU</div>
                </div>
              </div>
              <ChevronRight class="w-3.5 h-3.5 text-zinc-600 group-hover:text-zinc-400 group-hover:translate-x-0.5 transition-all" />
            </router-link>

            <router-link
              to="/orders"
              class="w-full flex items-center justify-between p-3 rounded-lg bg-zinc-950/70 border border-zinc-800/80 hover:border-zinc-700 hover:bg-zinc-900/90 transition-all group"
            >
              <div class="flex items-center gap-3">
                <div class="w-8 h-8 rounded-md bg-zinc-900 border border-zinc-800 text-zinc-300 flex items-center justify-center group-hover:text-white group-hover:border-zinc-700">
                  <Clock class="w-4 h-4" />
                </div>
                <div>
                  <div class="text-xs font-medium text-zinc-200 group-hover:text-white transition-colors">Duyệt đơn chờ</div>
                  <div class="text-[11px] text-zinc-500">Xác nhận đơn và giao vận</div>
                </div>
              </div>
              <ChevronRight class="w-3.5 h-3.5 text-zinc-600 group-hover:text-zinc-400 group-hover:translate-x-0.5 transition-all" />
            </router-link>

            <router-link
              to="/system-ops"
              class="w-full flex items-center justify-between p-3 rounded-lg bg-zinc-950/70 border border-zinc-800/80 hover:border-zinc-700 hover:bg-zinc-900/90 transition-all group"
            >
              <div class="flex items-center gap-3">
                <div class="w-8 h-8 rounded-md bg-zinc-900 border border-zinc-800 text-zinc-300 flex items-center justify-center group-hover:text-white group-hover:border-zinc-700">
                  <Search class="w-4 h-4" />
                </div>
                <div>
                  <div class="text-xs font-medium text-zinc-200 group-hover:text-white transition-colors">Đồng bộ Elasticsearch</div>
                  <div class="text-[11px] text-zinc-500">Cập nhật chỉ mục tìm kiếm</div>
                </div>
              </div>
              <ChevronRight class="w-3.5 h-3.5 text-zinc-600 group-hover:text-zinc-400 group-hover:translate-x-0.5 transition-all" />
            </router-link>
          </div>
        </div>

        <!-- Infrastructure Status Card (Linear style) -->
        <div class="bg-zinc-900/60 border border-zinc-800/80 rounded-xl p-5 shadow-subtle space-y-3 font-mono">
          <div class="flex items-center justify-between pb-2 border-b border-zinc-800/60">
            <h2 class="text-xs font-medium uppercase tracking-wider text-zinc-400">Trạng thái Dịch vụ</h2>
            <span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span>
          </div>

          <div class="space-y-2.5 text-xs">
            <div class="flex items-center justify-between py-1 border-b border-zinc-800/40">
              <span class="text-zinc-400">Spring Boot REST API</span>
              <span class="text-emerald-400 font-medium flex items-center gap-1.5 text-[11px]">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span> :8080 (0.4ms)
              </span>
            </div>
            <div class="flex items-center justify-between py-1 border-b border-zinc-800/40">
              <span class="text-zinc-400">Elasticsearch Service</span>
              <span class="text-emerald-400 font-medium flex items-center gap-1.5 text-[11px]">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span> :9200 (Synced)
              </span>
            </div>
            <div class="flex items-center justify-between py-1 border-b border-zinc-800/40">
              <span class="text-zinc-400">PostgreSQL 16 DB</span>
              <span class="text-emerald-400 font-medium flex items-center gap-1.5 text-[11px]">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span> Pool OK
              </span>
            </div>
            <div class="flex items-center justify-between py-1">
              <span class="text-zinc-400">Redis Cache Layer</span>
              <span class="text-emerald-400 font-medium flex items-center gap-1.5 text-[11px]">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span> 98.2% Hit
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
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
const activeRange = ref('7d')
const timeRanges = [
  { id: '7d', label: '7 ngày' },
  { id: '30d', label: '30 ngày' },
  { id: 'year', label: 'Năm nay' }
]

const chartPoints = [
  { day: 'T2', x: 20, y: 160, amount: '12.4M', orders: 6 },
  { day: 'T3', x: 130, y: 130, amount: '15.2M', orders: 8 },
  { day: 'T4', x: 220, y: 90, amount: '22.8M', orders: 11 },
  { day: 'T5', x: 330, y: 60, amount: '26.1M', orders: 14 },
  { day: 'T6', x: 440, y: 50, amount: '28.5M', orders: 15 },
  { day: 'T7', x: 550, y: 35, amount: '32.0M', orders: 18 },
  { day: 'CN', x: 680, y: 30, amount: '34.5M', orders: 20 },
]

const formattedDate = computed(() => {
  return new Intl.DateTimeFormat('vi-VN', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    year: 'numeric'
  }).format(new Date())
})

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
