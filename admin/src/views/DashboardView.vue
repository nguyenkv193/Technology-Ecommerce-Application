<template>
  <div class="space-y-6">
    <!-- Top Clean Header -->
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-5 border-b border-zinc-200">
      <div>
        <h1 class="text-xl font-semibold tracking-tight text-zinc-900">
          Xin chào, {{ authStore.user?.fullName || 'Quản trị viên' }}
        </h1>
        <p class="text-xs text-zinc-500 mt-1 font-mono">
          {{ formattedDate }} • Dữ liệu đơn hàng và doanh thu theo thời gian thực
        </p>
      </div>

      <div class="flex flex-wrap items-center gap-2.5 shrink-0">
        <CustomSelect
          v-model="activeSection"
          :options="sectionOptions"
          align="right"
          menu-width-class="w-64"
          trigger-test-id="dashboard-section-select"
          option-test-id-prefix="dashboard-section-option"
        />
        <router-link
          to="/products/create"
          class="px-3.5 py-2 rounded-lg bg-zinc-900 hover:bg-zinc-800 text-white text-xs font-medium shadow-sm flex items-center gap-2 transition-all"
        >
          <Plus class="w-3.5 h-3.5" />
          <span>Thêm sản phẩm</span>
        </router-link>
        <button
          type="button"
          @click="loadData"
          class="px-3.5 py-2 rounded-lg bg-white hover:bg-zinc-50 text-zinc-700 hover:text-zinc-900 text-xs font-medium border border-zinc-200 shadow-xs flex items-center gap-2 transition-all font-mono"
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
    <div v-if="isSectionVisible('revenue')" class="p-5 rounded-xl bg-white border border-zinc-200 shadow-sm relative overflow-hidden">
      <!-- Chart Header & Range Filter -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-5">
        <div>
          <h2 class="text-sm font-semibold text-zinc-900 tracking-tight">Doanh thu & Số lượng đơn</h2>
          <p class="text-xs text-zinc-500 mt-0.5">Biểu đồ tổng hợp từ dữ liệu đơn hàng thực tế</p>
        </div>

        <div class="flex items-center gap-1 p-1 rounded-lg bg-zinc-100 border border-zinc-200 text-xs font-mono self-start sm:self-auto">
          <button
            v-for="range in timeRanges"
            :key="range.id"
            @click="activeRange = range.id"
            class="px-2.5 py-1 rounded-md transition-all text-[11px]"
            :class="activeRange === range.id ? 'bg-white text-zinc-900 font-semibold shadow-xs' : 'text-zinc-600 hover:text-zinc-900'"
          >
            {{ range.label }}
          </button>
        </div>
      </div>

      <!-- Chart Content Area -->
      <RevenueChart :points="revenuePoints" />
    </div>

    <!-- User Interaction Analytics & Top Products -->
    <div v-if="isSectionVisible('interactions')" class="grid grid-cols-1 xl:grid-cols-12 gap-6">
      <!-- Interaction Trend Chart (7 cols) -->
      <div class="xl:col-span-7 p-5 rounded-xl bg-white border border-zinc-200 shadow-sm space-y-4">
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          <div>
            <h2 class="text-sm font-semibold text-zinc-900 tracking-tight">Thống kê tương tác người dùng</h2>
            <p class="text-xs text-zinc-500 mt-0.5">Theo dõi lượt xem (click), lượt tym, thêm giỏ hàng và mua hàng</p>
          </div>

          <div class="flex items-center gap-1 p-1 rounded-lg bg-zinc-100 border border-zinc-200 text-xs font-mono self-start sm:self-auto">
            <button
              v-for="opt in interactionDayOptions"
              :key="opt.days"
              @click="changeInteractionDays(opt.days)"
              class="px-2.5 py-1 rounded-md transition-all text-[11px]"
              :class="interactionDays === opt.days ? 'bg-white text-zinc-900 font-semibold shadow-xs' : 'text-zinc-600 hover:text-zinc-900'"
            >
              {{ opt.label }}
            </button>
          </div>
        </div>

        <!-- Mini KPI Pills -->
        <div class="grid grid-cols-2 sm:grid-cols-4 gap-2.5">
          <div class="p-2.5 rounded-lg bg-blue-50/60 border border-blue-100">
            <div class="flex items-center gap-1.5 text-[11px] text-blue-700 font-medium">
              <Eye class="w-3.5 h-3.5" />
              <span>Lượt xem</span>
            </div>
            <div class="text-base font-bold font-mono text-zinc-900 mt-1 tabular-nums">
              {{ behaviorAnalytics.summary.totalViews.toLocaleString('vi-VN') }}
            </div>
          </div>

          <div class="p-2.5 rounded-lg bg-rose-50/60 border border-rose-100">
            <div class="flex items-center gap-1.5 text-[11px] text-rose-700 font-medium">
              <Heart class="w-3.5 h-3.5" />
              <span>Lượt tym</span>
            </div>
            <div class="text-base font-bold font-mono text-zinc-900 mt-1 tabular-nums">
              {{ behaviorAnalytics.summary.totalWishlists.toLocaleString('vi-VN') }}
            </div>
          </div>

          <div class="p-2.5 rounded-lg bg-amber-50/60 border border-amber-100">
            <div class="flex items-center gap-1.5 text-[11px] text-amber-700 font-medium">
              <ShoppingCart class="w-3.5 h-3.5" />
              <span>Thêm giỏ</span>
            </div>
            <div class="text-base font-bold font-mono text-zinc-900 mt-1 tabular-nums">
              {{ behaviorAnalytics.summary.totalAddToCart.toLocaleString('vi-VN') }}
            </div>
          </div>

          <div class="p-2.5 rounded-lg bg-emerald-50/60 border border-emerald-100">
            <div class="flex items-center gap-1.5 text-[11px] text-emerald-700 font-medium">
              <CheckCircle2 class="w-3.5 h-3.5" />
              <span>Mua hàng</span>
            </div>
            <div class="text-base font-bold font-mono text-zinc-900 mt-1 tabular-nums">
              {{ behaviorAnalytics.summary.totalPurchases.toLocaleString('vi-VN') }}
            </div>
          </div>
        </div>

        <InteractionChart :series="behaviorAnalytics.dailySeries" />
      </div>

      <!-- Top Interacted Products Ranking (5 cols) -->
      <div class="xl:col-span-5 p-5 rounded-xl bg-white border border-zinc-200 shadow-sm flex flex-col space-y-4">
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2 pb-3 border-b border-zinc-100">
          <div>
            <h2 class="text-sm font-semibold text-zinc-900 tracking-tight">Xếp hạng sản phẩm tương tác</h2>
            <p class="text-xs text-zinc-500 mt-0.5">Top sản phẩm nổi bật trong {{ interactionDays }} ngày qua</p>
          </div>

          <CustomSelect
            v-model="rankingSortKey"
            :options="rankingSortOptions"
            align="right"
            size="sm"
          />
        </div>

        <div v-if="isLoadingAnalytics" class="py-12 text-center text-zinc-500 text-xs">
          <Loader2 class="w-4 h-4 animate-spin mx-auto mb-2 text-zinc-500" />
          <span>Đang tải bảng xếp hạng...</span>
        </div>

        <div v-else-if="sortedTopProducts.length === 0" class="py-12 text-center text-zinc-400 text-xs">
          Chưa có dữ liệu tương tác trong khoảng thời gian này.
        </div>

        <div v-else class="space-y-2.5 overflow-y-auto max-h-[340px] pr-1">
          <div
            v-for="(item, idx) in sortedTopProducts"
            :key="item.productId"
            class="flex items-center gap-3 p-2.5 rounded-xl border border-zinc-100 hover:border-zinc-200 hover:bg-zinc-50/70 transition-colors"
          >
            <!-- Rank Badge -->
            <div
              class="w-6 h-6 rounded-full flex items-center justify-center text-[11px] font-mono font-bold shrink-0"
              :class="rankBadgeClass(idx)"
            >
              {{ idx + 1 }}
            </div>

            <!-- Thumbnail -->
            <div class="w-10 h-10 rounded-lg bg-zinc-50 border border-zinc-200 overflow-hidden flex items-center justify-center shrink-0">
              <img
                v-if="item.thumbnailUrl"
                :src="item.thumbnailUrl"
                :alt="item.productName"
                class="w-full h-full object-cover"
              />
              <Package v-else class="w-4 h-4 text-zinc-400" />
            </div>

            <!-- Product Info -->
            <div class="min-w-0 flex-1">
              <router-link
                :to="`/products/${item.productId}/edit`"
                class="text-xs font-semibold text-zinc-900 hover:text-blue-600 truncate block"
                :title="item.productName"
              >
                {{ item.productName }}
              </router-link>
              <div class="flex flex-wrap items-center gap-x-2.5 gap-y-0.5 text-[10px] font-mono text-zinc-500 mt-1">
                <span class="text-blue-600 font-medium">{{ item.viewCount }} xem</span>
                <span class="text-rose-600 font-medium">{{ item.wishlistCount }} tym</span>
                <span class="text-amber-600 font-medium">{{ item.addToCartCount }} giỏ</span>
                <span class="text-emerald-600 font-medium">{{ item.purchaseCount }} mua</span>
              </div>
            </div>

            <!-- Score -->
            <div class="text-right shrink-0">
              <div class="text-xs font-bold font-mono text-zinc-900 tabular-nums">
                {{ Math.round(item.interactionScore) }}đ
              </div>
              <div v-if="item.minPrice" class="text-[10px] font-mono text-zinc-400 tabular-nums">
                {{ formatCompactCurrency(item.minPrice) }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 2-Column Bottom Layout -->
    <div v-if="isSectionVisible('orders')" class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Recent Orders Table (2 Cols) -->
      <div class="lg:col-span-2 bg-white border border-zinc-200 rounded-xl p-5 shadow-sm space-y-4">
        <div class="flex items-center justify-between pb-3 border-b border-zinc-100">
          <div>
            <h2 class="text-sm font-semibold text-zinc-900 tracking-tight">Đơn hàng mới nhất</h2>
            <p class="text-xs text-zinc-500 mt-0.5">Các giao dịch phát sinh gần đây</p>
          </div>
          <router-link
            to="/orders"
            class="text-xs font-medium text-zinc-600 hover:text-zinc-900 flex items-center gap-1 transition-colors px-2.5 py-1 rounded-md hover:bg-zinc-100"
          >
            <span>Xem tất cả</span>
            <ChevronRight class="w-3.5 h-3.5" />
          </router-link>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full text-left text-xs text-zinc-700">
            <thead class="text-[11px] font-mono font-medium text-zinc-500 uppercase bg-zinc-50/80 border-b border-zinc-200">
              <tr>
                <th class="py-2.5 px-3 rounded-l-lg">Mã đơn</th>
                <th class="py-2.5 px-3">Khách hàng</th>
                <th class="py-2.5 px-3">Tổng tiền</th>
                <th class="py-2.5 px-3">Trạng thái</th>
                <th class="py-2.5 px-3 text-right rounded-r-lg">Hành động</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-zinc-100 font-mono">
              <tr v-if="isLoading" class="text-center py-8">
                <td colspan="5" class="py-8 text-zinc-500">
                  <div class="flex items-center justify-center gap-2">
                    <Loader2 class="w-4 h-4 animate-spin text-zinc-500" />
                    <span>Đang tải dữ liệu...</span>
                  </div>
                </td>
              </tr>
              <tr v-else-if="recentOrders.length === 0" class="text-center">
                <td colspan="5" class="py-8 text-zinc-400 text-xs font-sans">
                  Chưa có đơn hàng nào phát sinh.
                </td>
              </tr>
              <tr
                v-for="order in recentOrders"
                :key="order.id"
                class="hover:bg-zinc-50/70 transition-colors group"
              >
                <td class="py-3 px-3 font-semibold text-zinc-900">
                  <span class="px-1.5 py-0.5 rounded bg-zinc-100 border border-zinc-200 text-zinc-800 text-[11px]">
                    {{ order.orderCode }}
                  </span>
                </td>
                <td class="py-3 px-3 font-sans">
                  <div class="font-medium text-zinc-900">{{ order.recipientName }}</div>
                  <div class="text-[11px] font-mono text-zinc-500">{{ order.phoneNumber }}</div>
                </td>
                <td class="py-3 px-3 font-semibold text-zinc-900 tabular-nums">
                  {{ formatCurrency(order.finalAmount ?? order.totalAmount) }}
                </td>
                <td class="py-3 px-3 font-sans">
                  <BadgeStatus :status="order.orderStatus" />
                </td>
                <td class="py-3 px-3 text-right font-sans">
                  <router-link
                    to="/orders"
                    class="text-xs font-medium text-zinc-600 hover:text-zinc-900 px-2.5 py-1 rounded-md hover:bg-zinc-100 border border-transparent hover:border-zinc-200 transition-colors inline-block"
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
        <div class="bg-white border border-zinc-200 rounded-xl p-5 shadow-sm space-y-3">
          <div class="flex items-center justify-between pb-2 border-b border-zinc-100">
            <h2 class="text-xs font-semibold text-zinc-900 uppercase tracking-wider font-mono">
              Trạng thái đơn hàng
            </h2>
            <span class="text-[10px] font-mono text-zinc-400">Phân loại</span>
          </div>

          <div class="space-y-2 text-xs font-mono">
            <router-link
              to="/orders?status=PENDING"
              class="flex items-center justify-between p-2 rounded-lg hover:bg-zinc-50 border border-transparent hover:border-zinc-200 transition-colors"
            >
              <div class="flex items-center gap-2">
                <span class="w-2 h-2 rounded-full bg-amber-500"></span>
                <span class="text-zinc-700 font-sans">Chờ xác nhận</span>
              </div>
              <span class="font-semibold text-zinc-900 px-2 py-0.5 rounded bg-zinc-100 border border-zinc-200">
                {{ statusCounts.PENDING }}
              </span>
            </router-link>

            <router-link
              to="/orders?status=CONFIRMED"
              class="flex items-center justify-between p-2 rounded-lg hover:bg-zinc-50 border border-transparent hover:border-zinc-200 transition-colors"
            >
              <div class="flex items-center gap-2">
                <span class="w-2 h-2 rounded-full bg-blue-500"></span>
                <span class="text-zinc-700 font-sans">Đã xác nhận</span>
              </div>
              <span class="font-semibold text-zinc-900 px-2 py-0.5 rounded bg-zinc-100 border border-zinc-200">
                {{ statusCounts.CONFIRMED }}
              </span>
            </router-link>

            <router-link
              to="/orders?status=SHIPPING"
              class="flex items-center justify-between p-2 rounded-lg hover:bg-zinc-50 border border-transparent hover:border-zinc-200 transition-colors"
            >
              <div class="flex items-center gap-2">
                <span class="w-2 h-2 rounded-full bg-purple-500"></span>
                <span class="text-zinc-700 font-sans">Đang giao hàng</span>
              </div>
              <span class="font-semibold text-zinc-900 px-2 py-0.5 rounded bg-zinc-100 border border-zinc-200">
                {{ statusCounts.SHIPPING }}
              </span>
            </router-link>

            <router-link
              to="/orders?status=DELIVERED"
              class="flex items-center justify-between p-2 rounded-lg hover:bg-zinc-50 border border-transparent hover:border-zinc-200 transition-colors"
            >
              <div class="flex items-center gap-2">
                <span class="w-2 h-2 rounded-full bg-emerald-500"></span>
                <span class="text-zinc-700 font-sans">Hoàn thành</span>
              </div>
              <span class="font-semibold text-zinc-900 px-2 py-0.5 rounded bg-zinc-100 border border-zinc-200">
                {{ statusCounts.DELIVERED }}
              </span>
            </router-link>

            <router-link
              to="/orders?status=CANCELLED"
              class="flex items-center justify-between p-2 rounded-lg hover:bg-zinc-50 border border-transparent hover:border-zinc-200 transition-colors"
            >
              <div class="flex items-center gap-2">
                <span class="w-2 h-2 rounded-full bg-zinc-400"></span>
                <span class="text-zinc-500 font-sans">Đã hủy</span>
              </div>
              <span class="font-semibold text-zinc-500 px-2 py-0.5 rounded bg-zinc-100 border border-zinc-200">
                {{ statusCounts.CANCELLED }}
              </span>
            </router-link>
          </div>
        </div>

        <!-- Quick Shortcuts -->
        <div class="bg-white border border-zinc-200 rounded-xl p-5 shadow-sm space-y-3">
          <div class="flex items-center justify-between pb-2 border-b border-zinc-100">
            <h2 class="text-xs font-semibold text-zinc-900 uppercase tracking-wider font-mono">
              Phím tắt thao tác
            </h2>
          </div>

          <div class="space-y-2 font-sans">
            <router-link
              to="/products/create"
              class="w-full flex items-center justify-between p-2.5 rounded-lg bg-zinc-50 border border-zinc-200/80 hover:border-zinc-300 hover:bg-zinc-100/70 transition-all text-xs"
            >
              <span class="text-zinc-800 font-medium">Tạo sản phẩm & SKU mới</span>
              <ChevronRight class="w-3.5 h-3.5 text-zinc-400" />
            </router-link>
            <router-link
              to="/orders"
              class="w-full flex items-center justify-between p-2.5 rounded-lg bg-zinc-50 border border-zinc-200/80 hover:border-zinc-300 hover:bg-zinc-100/70 transition-all text-xs"
            >
              <span class="text-zinc-800 font-medium">Xử lý toàn bộ đơn hàng</span>
              <ChevronRight class="w-3.5 h-3.5 text-zinc-400" />
            </router-link>
            <router-link
              to="/users"
              class="w-full flex items-center justify-between p-2.5 rounded-lg bg-zinc-50 border border-zinc-200/80 hover:border-zinc-300 hover:bg-zinc-100/70 transition-all text-xs"
            >
              <span class="text-zinc-800 font-medium">Quản lý tài khoản người dùng</span>
              <ChevronRight class="w-3.5 h-3.5 text-zinc-400" />
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import {
  DollarSign,
  ShoppingBag,
  Clock,
  Layers,
  Plus,
  RefreshCw,
  ChevronRight,
  BarChart3,
  LayoutGrid,
  Loader2,
  Eye,
  Heart,
  ShoppingCart,
  CheckCircle2,
  Package
} from 'lucide-vue-next'
import { useAdminAuthStore } from '@/stores/auth'
import StatCard from '@/components/common/StatCard.vue'
import BadgeStatus from '@/components/common/BadgeStatus.vue'
import CustomSelect, { type CustomSelectOption } from '@/components/common/CustomSelect.vue'
import RevenueChart, { type RevenuePoint } from '@/components/dashboard/RevenueChart.vue'
import InteractionChart from '@/components/dashboard/InteractionChart.vue'
import { fetchDashboardOverview, fetchBehaviorAnalytics } from '@/api/dashboard'
import type { BehaviorAnalytics, DashboardMetrics, Order } from '@/types'

const authStore = useAdminAuthStore()
const isLoading = ref(true)

type DashboardSection = 'revenue' | 'interactions' | 'orders' | 'all'
const SECTION_STORAGE_KEY = 'techstore_admin_dashboard_section'
const sectionOptions: (CustomSelectOption & { value: DashboardSection })[] = [
  { value: 'revenue', label: 'Doanh thu & Số lượng đơn', description: 'Biểu đồ doanh thu theo thời gian', icon: BarChart3 },
  { value: 'interactions', label: 'Tương tác & Top sản phẩm', description: 'Hành vi người dùng & xếp hạng', icon: Eye },
  { value: 'orders', label: 'Đơn hàng & Phím tắt', description: 'Đơn mới nhất & trạng thái xử lý', icon: ShoppingBag },
  { value: 'all', label: 'Hiển thị tất cả', description: 'Xem toàn bộ các khối thống kê', icon: LayoutGrid }
]

const rankingSortOptions: CustomSelectOption[] = [
  { value: 'interactionScore', label: 'Theo điểm tương tác' },
  { value: 'viewCount', label: 'Theo lượt xem' },
  { value: 'wishlistCount', label: 'Theo lượt tym' },
  { value: 'addToCartCount', label: 'Theo thêm giỏ' },
  { value: 'purchaseCount', label: 'Theo lượt mua' }
]

function loadSavedSection(): DashboardSection {
  const saved = localStorage.getItem(SECTION_STORAGE_KEY)
  return sectionOptions.some(opt => opt.value === saved) ? (saved as DashboardSection) : 'revenue'
}

const activeSection = ref<DashboardSection>(loadSavedSection())

watch(activeSection, value => localStorage.setItem(SECTION_STORAGE_KEY, value))

function isSectionVisible(section: Exclude<DashboardSection, 'all'>): boolean {
  return activeSection.value === 'all' || activeSection.value === section
}

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

const interactionDays = ref<7 | 14 | 30>(14)
const interactionDayOptions = [
  { days: 7 as const, label: '7 ngày' },
  { days: 14 as const, label: '14 ngày' },
  { days: 30 as const, label: '30 ngày' }
]
const isLoadingAnalytics = ref(false)
const rankingSortKey = ref<'interactionScore' | 'viewCount' | 'wishlistCount' | 'addToCartCount' | 'purchaseCount'>('interactionScore')
const behaviorAnalytics = ref<BehaviorAnalytics>({
  days: 14,
  summary: {
    totalViews: 0,
    totalWishlists: 0,
    totalAddToCart: 0,
    totalPurchases: 0,
    totalSearches: 0
  },
  dailySeries: [],
  topProducts: []
})

const sortedTopProducts = computed(() => {
  const key = rankingSortKey.value
  return [...behaviorAnalytics.value.topProducts].sort((a, b) => Number(b[key]) - Number(a[key]))
})

function rankBadgeClass(index: number): string {
  if (index === 0) return 'bg-amber-100 text-amber-800 border border-amber-300'
  if (index === 1) return 'bg-zinc-200 text-zinc-800 border border-zinc-300'
  if (index === 2) return 'bg-orange-100 text-orange-800 border border-orange-300'
  return 'bg-zinc-100 text-zinc-500 border border-zinc-200'
}

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
    if (o.orderStatus && counts[o.orderStatus] !== undefined) {
      counts[o.orderStatus]++
    }
  })
  return counts
})

const revenuePoints = computed<RevenuePoint[]>(() => {
  const points: RevenuePoint[] = []
  const now = new Date()

  if (activeRange.value === '7d') {
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
        if (o.orderStatus === 'DELIVERED' || o.paymentStatus === 'PAID') {
          return sum + (o.finalAmount ?? o.totalAmount ?? 0)
        }
        return sum
      }, 0)

      points.push({ label, amount, count: dayOrders.length })
    }
  } else if (activeRange.value === '30d') {
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
        if (o.orderStatus === 'DELIVERED' || o.paymentStatus === 'PAID') {
          return sum + (o.finalAmount ?? o.totalAmount ?? 0)
        }
        return sum
      }, 0)

      points.push({ label, amount, count: bucketOrders.length })
    }
  } else {
    const currentYear = now.getFullYear()
    for (let m = 0; m < 12; m++) {
      const monthPrefix = `${currentYear}-${String(m + 1).padStart(2, '0')}`
      const label = `T${m + 1}`

      const monthOrders = allOrders.value.filter((o) => {
        if (!o.createdAt) return false
        return o.createdAt.startsWith(monthPrefix)
      })

      const amount = monthOrders.reduce((sum, o) => {
        if (o.orderStatus === 'DELIVERED' || o.paymentStatus === 'PAID') {
          return sum + (o.finalAmount ?? o.totalAmount ?? 0)
        }
        return sum
      }, 0)

      points.push({ label, amount, count: monthOrders.length })
    }
  }

  return points
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

async function loadInteractionAnalytics() {
  isLoadingAnalytics.value = true
  try {
    behaviorAnalytics.value = await fetchBehaviorAnalytics(interactionDays.value, 10)
  } finally {
    isLoadingAnalytics.value = false
  }
}

function changeInteractionDays(days: 7 | 14 | 30) {
  if (interactionDays.value === days) return
  interactionDays.value = days
  loadInteractionAnalytics()
}

async function loadData() {
  isLoading.value = true
  try {
    const [overview] = await Promise.all([fetchDashboardOverview(), loadInteractionAnalytics()])
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
