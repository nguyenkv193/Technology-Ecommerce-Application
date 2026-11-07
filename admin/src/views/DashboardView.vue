<template>
  <div class="space-y-6">
    <!-- Top Clean Header -->
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-5 border-b border-zinc-800/80">
      <div>
        <h1 class="text-xl font-semibold tracking-tight text-white">
          Xin chào, {{ authStore.user?.fullName || 'Quản trị viên' }}
        </h1>
        <p class="text-xs text-zinc-400 mt-1 font-mono">
          {{ formattedDate }} • Dữ liệu đơn hàng và doanh thu theo thời gian thực
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
        <button
          type="button"
          @click="loadData"
          class="px-3.5 py-2 rounded-lg bg-zinc-900 hover:bg-zinc-800 text-zinc-300 hover:text-white text-xs font-medium border border-zinc-800 hover:border-zinc-700 flex items-center gap-2 transition-all font-mono"
        >
          <RefreshCw class="w-3.5 h-3.5" :class="{ 'animate-spin': isLoading }" />
          <span>Làm mới</span>
        </button>
      </div>
    </div>

    <!-- Stat Cards Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <StatCard
        title="Tổng doanh thu"
        :value="formatCurrency(metrics.totalRevenue)"
        :icon="DollarSign"
        theme="emerald"
        subtitle="Doanh thu đơn thành công"
      />
      <StatCard
        title="Tổng đơn hàng"
        :value="metrics.totalOrders"
        :icon="ShoppingBag"
        theme="blue"
        subtitle="Tất cả đơn đã tạo"
      />
      <StatCard
        title="Đơn chờ xác nhận"
        :value="metrics.pendingOrders"
        :icon="Clock"
        theme="amber"
        :subtitle="metrics.pendingOrders > 0 ? 'Cần duyệt giao vận' : 'Không có đơn tồn đọng'"
      />
      <StatCard
        title="Sản phẩm hoạt động"
        :value="metrics.totalProducts"
        :icon="Layers"
        theme="purple"
        subtitle="Mặt hàng đang mở bán"
      />
    </div>

    <!-- Dynamic Revenue Chart -->
    <div class="p-5 rounded-xl bg-zinc-900/60 border border-zinc-800/80 shadow-subtle relative overflow-hidden">
      <!-- Chart Header & Range Filter -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-5">
        <div>
          <h2 class="text-sm font-semibold text-zinc-100 tracking-tight">Doanh thu & Số lượng đơn</h2>
          <p class="text-xs text-zinc-400 mt-0.5">Biểu đồ tổng hợp từ dữ liệu đơn hàng thực tế</p>
        </div>

        <div class="flex items-center gap-1 p-1 rounded-lg bg-zinc-950 border border-zinc-800/80 text-xs font-mono self-start sm:self-auto">
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

      <!-- Chart Content Area -->
      <div class="relative h-56 w-full pt-2">
        <!-- Horizontal Reference Gridlines -->
        <div class="absolute inset-0 flex flex-col justify-between pointer-events-none text-[10px] font-mono text-zinc-600">
          <div class="border-b border-zinc-800/40 w-full flex justify-between pr-2">
            <span>{{ formatCompactCurrency(chartMax) }}</span>
          </div>
          <div class="border-b border-zinc-800/40 w-full flex justify-between pr-2">
            <span>{{ formatCompactCurrency(chartMax / 2) }}</span>
          </div>
          <div class="border-b border-zinc-800/40 w-full flex justify-between pr-2">
            <span>0 ₫</span>
          </div>
        </div>

        <!-- SVG Line & Fill -->
        <svg
          class="w-full h-full overflow-visible relative z-10"
          preserveAspectRatio="none"
          viewBox="0 0 700 180"
        >
          <defs>
            <linearGradient id="chartGradient" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stop-color="#3b82f6" stop-opacity="0.2" />
              <stop offset="100%" stop-color="#3b82f6" stop-opacity="0.0" />
            </linearGradient>
          </defs>

          <!-- Area under curve -->
          <path
            v-if="chartPoints.length > 0"
            :d="areaPath"
            fill="url(#chartGradient)"
          />

          <!-- Line -->
          <path
            v-if="chartPoints.length > 0"
            :d="linePath"
            fill="none"
            stroke="#60a5fa"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />

          <!-- Interactive Data Points -->
          <g v-for="(pt, idx) in chartPoints" :key="idx">
            <circle
              :cx="pt.x"
              :cy="pt.y"
              r="4"
              class="fill-zinc-950 stroke-blue-400 stroke-2 hover:r-5 hover:fill-blue-400 transition-all cursor-pointer"
            >
              <title>{{ pt.label }}: {{ formatCurrency(pt.amount) }} ({{ pt.count }} đơn hàng)</title>
            </circle>
          </g>
        </svg>

        <!-- X-Axis Labels -->
        <div class="flex justify-between text-[11px] font-mono text-zinc-500 mt-2 px-1">
          <span v-for="pt in chartPoints" :key="pt.label">{{ pt.label }}</span>
        </div>
      </div>
    </div>

    <!-- 2-Column Bottom Layout -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Recent Orders Table (2 Cols) -->
      <div class="lg:col-span-2 bg-zinc-900/60 border border-zinc-800/80 rounded-xl p-5 shadow-subtle space-y-4">
        <div class="flex items-center justify-between pb-3 border-b border-zinc-800/60">
          <div>
            <h2 class="text-sm font-semibold text-zinc-100 tracking-tight">Đơn hàng mới nhất</h2>
            <p class="text-xs text-zinc-400 mt-0.5">Các giao dịch phát sinh gần đây</p>
          </div>
          <router-link
            to="/orders"
            class="text-xs font-medium text-zinc-400 hover:text-white flex items-center gap-1 transition-colors px-2.5 py-1 rounded-md hover:bg-zinc-800"
          >
            <span>Xem tất cả</span>
            <ChevronRight class="w-3.5 h-3.5" />
          </router-link>
        </div>

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
                    <span>Đang tải dữ liệu...</span>
                  </div>
                </td>
              </tr>
              <tr v-else-if="recentOrders.length === 0" class="text-center">
                <td colspan="5" class="py-8 text-zinc-500 text-xs font-sans">
                  Chưa có đơn hàng nào phát sinh.
                </td>
              </tr>
              <tr
                v-for="order in recentOrders"
                :key="order.id"
                class="hover:bg-zinc-800/40 transition-colors group"
              >
                <td class="py-3 px-3 font-semibold text-zinc-200">
                  <span class="px-1.5 py-0.5 rounded bg-zinc-950 border border-zinc-800 text-[11px]">
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
                    Xem đơn
                  </router-link>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Right Column: Status Breakdown & Shortcuts -->
      <div class="space-y-6">
        <!-- Order Status Breakdown (Truly Dynamic) -->
        <div class="bg-zinc-900/60 border border-zinc-800/80 rounded-xl p-5 shadow-subtle space-y-3">
          <div class="flex items-center justify-between pb-2 border-b border-zinc-800/60">
            <h2 class="text-xs font-semibold text-zinc-200 uppercase tracking-wider font-mono">
              Trạng thái đơn hàng
            </h2>
            <span class="text-[10px] font-mono text-zinc-500">Phân loại</span>
          </div>

          <div class="space-y-2 text-xs font-mono">
            <router-link
              to="/orders?status=PENDING"
              class="flex items-center justify-between p-2 rounded-lg hover:bg-zinc-800/60 transition-colors"
            >
              <div class="flex items-center gap-2">
                <span class="w-2 h-2 rounded-full bg-amber-400"></span>
                <span class="text-zinc-300 font-sans">Chờ xác nhận</span>
              </div>
              <span class="font-semibold text-zinc-100 px-2 py-0.5 rounded bg-zinc-950 border border-zinc-800">
                {{ statusCounts.PENDING }}
              </span>
            </router-link>

            <router-link
              to="/orders?status=CONFIRMED"
              class="flex items-center justify-between p-2 rounded-lg hover:bg-zinc-800/60 transition-colors"
            >
              <div class="flex items-center gap-2">
                <span class="w-2 h-2 rounded-full bg-blue-400"></span>
                <span class="text-zinc-300 font-sans">Đã xác nhận</span>
              </div>
              <span class="font-semibold text-zinc-100 px-2 py-0.5 rounded bg-zinc-950 border border-zinc-800">
                {{ statusCounts.CONFIRMED }}
              </span>
            </router-link>

            <router-link
              to="/orders?status=SHIPPING"
              class="flex items-center justify-between p-2 rounded-lg hover:bg-zinc-800/60 transition-colors"
            >
              <div class="flex items-center gap-2">
                <span class="w-2 h-2 rounded-full bg-purple-400"></span>
                <span class="text-zinc-300 font-sans">Đang giao hàng</span>
              </div>
              <span class="font-semibold text-zinc-100 px-2 py-0.5 rounded bg-zinc-950 border border-zinc-800">
                {{ statusCounts.SHIPPING }}
              </span>
            </router-link>

            <router-link
              to="/orders?status=DELIVERED"
              class="flex items-center justify-between p-2 rounded-lg hover:bg-zinc-800/60 transition-colors"
            >
              <div class="flex items-center gap-2">
                <span class="w-2 h-2 rounded-full bg-emerald-400"></span>
                <span class="text-zinc-300 font-sans">Hoàn thành</span>
              </div>
              <span class="font-semibold text-zinc-100 px-2 py-0.5 rounded bg-zinc-950 border border-zinc-800">
                {{ statusCounts.DELIVERED }}
              </span>
            </router-link>

            <router-link
              to="/orders?status=CANCELLED"
              class="flex items-center justify-between p-2 rounded-lg hover:bg-zinc-800/60 transition-colors"
            >
              <div class="flex items-center gap-2">
                <span class="w-2 h-2 rounded-full bg-zinc-500"></span>
                <span class="text-zinc-400 font-sans">Đã hủy</span>
              </div>
              <span class="font-semibold text-zinc-400 px-2 py-0.5 rounded bg-zinc-950 border border-zinc-800">
                {{ statusCounts.CANCELLED }}
              </span>
            </router-link>
          </div>
        </div>

        <!-- Quick Shortcuts -->
        <div class="bg-zinc-900/60 border border-zinc-800/80 rounded-xl p-5 shadow-subtle space-y-3">
          <div class="flex items-center justify-between pb-2 border-b border-zinc-800/60">
            <h2 class="text-xs font-semibold text-zinc-200 uppercase tracking-wider font-mono">
              Phím tắt thao tác
            </h2>
          </div>

          <div class="space-y-2 font-sans">
            <router-link
              to="/products/create"
              class="w-full flex items-center justify-between p-2.5 rounded-lg bg-zinc-950/60 border border-zinc-800/80 hover:border-zinc-700 hover:bg-zinc-800/40 transition-all text-xs"
            >
              <span class="text-zinc-200 font-medium">Tạo sản phẩm & SKU mới</span>
              <ChevronRight class="w-3.5 h-3.5 text-zinc-500" />
            </router-link>
            <router-link
              to="/orders"
              class="w-full flex items-center justify-between p-2.5 rounded-lg bg-zinc-950/60 border border-zinc-800/80 hover:border-zinc-700 hover:bg-zinc-800/40 transition-all text-xs"
            >
              <span class="text-zinc-200 font-medium">Xử lý toàn bộ đơn hàng</span>
              <ChevronRight class="w-3.5 h-3.5 text-zinc-500" />
            </router-link>
            <router-link
              to="/system-ops"
              class="w-full flex items-center justify-between p-2.5 rounded-lg bg-zinc-950/60 border border-zinc-800/80 hover:border-zinc-700 hover:bg-zinc-800/40 transition-all text-xs"
            >
              <span class="text-zinc-200 font-medium">Đồng bộ Elasticsearch & Cache</span>
              <ChevronRight class="w-3.5 h-3.5 text-zinc-500" />
            </router-link>
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
  Loader2
} from 'lucide-vue-next'
import { useAdminAuthStore } from '@/stores/auth'
import StatCard from '@/components/common/StatCard.vue'
import BadgeStatus from '@/components/common/BadgeStatus.vue'
import { fetchDashboardOverview } from '@/api/dashboard'
import type { DashboardMetrics, Order } from '@/types'

const authStore = useAdminAuthStore()
const isLoading = ref(true)

const activeRange = ref<'7d' | '30d' | 'year'>('7d')
const timeRanges = [
  { id: '7d' as const, label: '7 ngày' },
  { id: '30d' as const, label: '30 ngày' },
  { id: 'year' as const, label: 'Năm nay' }
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
const allOrders = ref<Order[]>([])

// Real dynamic status distribution
const statusCounts = computed(() => {
  const counts = {
    PENDING: 0,
    CONFIRMED: 0,
    SHIPPING: 0,
    DELIVERED: 0,
    CANCELLED: 0
  }
  allOrders.value.forEach((o) => {
    if (o.status && counts[o.status] !== undefined) {
      counts[o.status]++
    }
  })
  return counts
})

// Dynamic Chart Calculations based on actual orders
interface ChartPoint {
  label: string
  amount: number
  count: number
  x: number
  y: number
}

const chartPoints = computed<ChartPoint[]>(() => {
  const points: { label: string; amount: number; count: number }[] = []
  const now = new Date()

  if (activeRange.value === '7d') {
    // 7 past days
    for (let i = 6; i >= 0; i--) {
      const d = new Date(now)
      d.setDate(d.getDate() - i)
      const dayStr = d.toISOString().split('T')[0]
      const label = i === 0 ? 'Hôm nay' : `${d.getDate()}/${d.getMonth() + 1}`

      const dayOrders = allOrders.value.filter((o) => {
        if (!o.createdAt) return false
        return o.createdAt.startsWith(dayStr)
      })

      const amount = dayOrders.reduce((sum, o) => {
        if (o.status === 'DELIVERED' || o.paymentStatus === 'PAID') {
          return sum + (o.totalAmount || 0)
        }
        return sum
      }, 0)

      points.push({ label, amount, count: dayOrders.length })
    }
  } else if (activeRange.value === '30d') {
    // 6 intervals of 5 days
    for (let i = 5; i >= 0; i--) {
      const endD = new Date(now)
      endD.setDate(endD.getDate() - i * 5)
      const startD = new Date(endD)
      startD.setDate(startD.getDate() - 4)

      const label = `${startD.getDate()}-${endD.getDate()}/${endD.getMonth() + 1}`

      const bucketOrders = allOrders.value.filter((o) => {
        if (!o.createdAt) return false
        const orderDate = new Date(o.createdAt)
        return orderDate >= startD && orderDate <= endD
      })

      const amount = bucketOrders.reduce((sum, o) => {
        if (o.status === 'DELIVERED' || o.paymentStatus === 'PAID') {
          return sum + (o.totalAmount || 0)
        }
        return sum
      }, 0)

      points.push({ label, amount, count: bucketOrders.length })
    }
  } else {
    // 12 months of current year
    const currentYear = now.getFullYear()
    for (let m = 0; m < 12; m++) {
      const monthPrefix = `${currentYear}-${String(m + 1).padStart(2, '0')}`
      const label = `T${m + 1}`

      const monthOrders = allOrders.value.filter((o) => {
        if (!o.createdAt) return false
        return o.createdAt.startsWith(monthPrefix)
      })

      const amount = monthOrders.reduce((sum, o) => {
        if (o.status === 'DELIVERED' || o.paymentStatus === 'PAID') {
          return sum + (o.totalAmount || 0)
        }
        return sum
      }, 0)

      points.push({ label, amount, count: monthOrders.length })
    }
  }

  // Calculate SVG coordinates
  const svgWidth = 700
  const svgHeight = 180
  const padX = 35
  const padYTop = 20
  const padYBottom = 25

  const maxVal = Math.max(...points.map((p) => p.amount), 5000000)

  return points.map((p, index) => {
    const x = padX + (index / (points.length - 1)) * (svgWidth - 2 * padX)
    const yRatio = p.amount / maxVal
    const y = svgHeight - padYBottom - yRatio * (svgHeight - padYTop - padYBottom)
    return {
      ...p,
      x: Math.round(x),
      y: Math.round(y)
    }
  })
})

const chartMax = computed(() => {
  const max = Math.max(...chartPoints.value.map((p) => p.amount), 5000000)
  return Math.ceil(max / 1000000) * 1000000
})

const linePath = computed(() => {
  if (chartPoints.value.length === 0) return ''
  return chartPoints.value.reduce((acc, pt, idx) => {
    return idx === 0 ? `M ${pt.x} ${pt.y}` : `${acc} L ${pt.x} ${pt.y}`
  }, '')
})

const areaPath = computed(() => {
  if (chartPoints.value.length === 0) return ''
  const first = chartPoints.value[0]
  const last = chartPoints.value[chartPoints.value.length - 1]
  const baseline = 180 - 25
  return `${linePath.value} L ${last.x} ${baseline} L ${first.x} ${baseline} Z`
})

function formatCurrency(val: number): string {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val || 0)
}

function formatCompactCurrency(val: number): string {
  if (val >= 1000000) {
    return `${(val / 1000000).toFixed(val % 1000000 === 0 ? 0 : 1)}M ₫`
  }
  if (val >= 1000) {
    return `${(val / 1000).toFixed(0)}k ₫`
  }
  return `${val} ₫`
}

async function loadData() {
  isLoading.value = true
  try {
    const overview = await fetchDashboardOverview()
    metrics.value = overview.metrics
    recentOrders.value = overview.recentOrders
    allOrders.value = overview.allOrders
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>
