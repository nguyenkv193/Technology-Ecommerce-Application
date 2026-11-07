<template>
  <div class="space-y-6">
    <!-- Top Bar -->
    <div class="flex items-center justify-between pb-5 border-b border-zinc-200">
      <div>
        <div class="flex items-center gap-2">
          <h1 class="text-xl font-semibold tracking-tight text-zinc-900">Vận hành Hệ thống</h1>
          <span class="px-2 py-0.5 rounded-full text-[10px] font-mono font-medium bg-emerald-50 text-emerald-700 border border-emerald-200">
            Hạ tầng ổn định
          </span>
        </div>
        <p class="text-xs text-zinc-500 mt-1 font-mono">Giám sát hạ tầng PostgreSQL Database, Backend Core và dịch vụ AI</p>
      </div>
    </div>

    <!-- Operations Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Left 2 Cols: PostgreSQL Engine & Health Check -->
      <div class="lg:col-span-2 space-y-6">
        <div class="bg-white border border-zinc-200 rounded-xl p-5 shadow-sm space-y-4">
          <div class="flex items-start justify-between pb-3 border-b border-zinc-100">
            <div class="flex items-center gap-3">
              <div class="w-8 h-8 rounded-lg bg-zinc-100 border border-zinc-200 text-zinc-700 flex items-center justify-center shrink-0">
                <Database class="w-4 h-4" />
              </div>
              <div>
                <h2 class="text-sm font-semibold text-zinc-900">PostgreSQL 17 Database & Catalog Engine</h2>
                <p class="text-xs text-zinc-500 font-mono">Tìm kiếm, lọc danh mục & lưu trữ dữ liệu tập trung (ACID)</p>
              </div>
            </div>
            <span class="px-2 py-0.5 rounded-md text-[11px] font-mono font-medium bg-emerald-50 border border-emerald-200 text-emerald-700">
              Hoạt động
            </span>
          </div>

          <p class="text-xs text-zinc-600 leading-relaxed font-sans">
            Hệ thống sử dụng cơ sở dữ liệu quan hệ PostgreSQL 17 để phục vụ toàn bộ tác vụ lưu trữ, tìm kiếm từ khóa, lọc theo thương hiệu, khoảng giá và phân trang.
            Mọi thay đổi dữ liệu sản phẩm đều có hiệu lực ngay lập tức, đảm bảo tính nhất quán cao (Strong Consistency) và tối ưu chi phí tài nguyên máy chủ.
          </p>

          <!-- Result banner if done -->
          <div
            v-if="checkResult"
            class="p-3.5 rounded-lg border flex items-center gap-2.5 text-xs font-mono"
            :class="checkResult.success ? 'bg-emerald-50 border-emerald-200 text-emerald-800' : 'bg-rose-50 border-rose-200 text-rose-800'"
          >
            <CheckCircle v-if="checkResult.success" class="w-4 h-4 shrink-0 text-emerald-600" />
            <AlertCircle v-else class="w-4 h-4 shrink-0 text-rose-600" />
            <div>
              <p class="font-semibold">{{ checkResult.message }}</p>
              <p v-if="checkResult.details" class="text-[11px] opacity-90 mt-0.5">
                Trạng thái: <span class="font-bold">{{ checkResult.details.status }}</span> | Dịch vụ: {{ checkResult.details.service }}
              </p>
            </div>
          </div>

          <div class="pt-2 flex flex-col sm:flex-row sm:items-center gap-3 font-mono">
            <button
              data-test="health-check-btn"
              type="button"
              @click="handleHealthCheck"
              :disabled="isChecking"
              class="px-4 py-2 rounded-lg bg-zinc-900 hover:bg-zinc-800 text-white text-xs font-medium shadow-sm flex items-center gap-2 transition-all disabled:opacity-50 self-start"
            >
              <RefreshCw class="w-3.5 h-3.5" :class="{ 'animate-spin': isChecking }" />
              <span>{{ isChecking ? 'Đang kiểm tra kết nối...' : 'Kiểm tra sức khỏe hệ thống' }}</span>
            </button>
            <span class="text-[11px] text-zinc-500">
              API endpoint: <code class="text-zinc-700 font-mono font-medium">/api/v1/health</code>
            </span>
          </div>
        </div>

        <!-- Activity Log -->
        <div class="bg-white border border-zinc-200 rounded-xl p-5 shadow-sm space-y-3 font-mono">
          <div class="flex items-center justify-between pb-2 border-b border-zinc-100">
            <h3 class="text-xs font-semibold uppercase tracking-wider text-zinc-900">Lịch sử tác vụ</h3>
            <span class="text-[10px] text-zinc-400">Session logs</span>
          </div>

          <div class="space-y-1.5">
            <div
              v-for="(log, idx) in activityLogs"
              :key="idx"
              class="p-2.5 rounded-lg bg-zinc-50 border border-zinc-200/80 flex items-center justify-between text-xs"
            >
              <div class="flex items-center gap-2">
                <span class="w-1.5 h-1.5 rounded-full" :class="log.success ? 'bg-emerald-500' : 'bg-rose-500'"></span>
                <span class="text-zinc-900 font-medium">{{ log.action }}</span>
                <span class="text-zinc-500 text-[11px]">({{ log.detail }})</span>
              </div>
              <span class="text-[11px] text-zinc-500 tabular-nums">{{ log.time }}</span>
            </div>
            <div v-if="activityLogs.length === 0" class="py-4 text-center text-xs text-zinc-400 font-sans">
              Chưa có thao tác nào trong phiên làm việc này.
            </div>
          </div>
        </div>
      </div>

      <!-- Right 1 Col: Service Architecture Status -->
      <div class="space-y-6">
        <div class="bg-white border border-zinc-200 rounded-xl p-5 shadow-sm space-y-3 font-mono">
          <div class="flex items-center justify-between pb-2 border-b border-zinc-100">
            <h3 class="text-xs font-semibold uppercase tracking-wider text-zinc-900 flex items-center gap-2">
              <Server class="w-3.5 h-3.5 text-zinc-500" />
              <span>Dịch vụ nền tảng</span>
            </h3>
            <span class="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
          </div>

          <div class="space-y-2">
            <div
              v-for="svc in services"
              :key="svc.name"
              class="p-3 rounded-lg bg-zinc-50 border border-zinc-200/80 flex items-center justify-between"
            >
              <div>
                <p class="text-xs font-medium text-zinc-900 font-sans">{{ svc.name }}</p>
                <p class="text-[10px] text-zinc-500 font-mono mt-0.5">{{ svc.endpoint }}</p>
              </div>
              <span class="px-2 py-0.5 rounded text-[10px] font-medium bg-emerald-50 border border-emerald-200 text-emerald-700">
                {{ svc.status }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Database, RefreshCw, Server, CheckCircle, AlertCircle } from 'lucide-vue-next'
import { checkSystemHealth, type SystemHealthData } from '@/api/system'

interface ActivityLog {
  action: string
  detail: string
  time: string
  success: boolean
}

const isChecking = ref(false)
const checkResult = ref<{ success: boolean; message: string; details?: SystemHealthData } | null>(null)

const activityLogs = ref<ActivityLog[]>([])

const services = [
  { name: 'Spring Boot Backend', endpoint: 'localhost:8080/api/v1', status: 'Online' },
  { name: 'PostgreSQL 17 Database', endpoint: 'localhost:5433/techstore_db', status: 'Connected' },
  { name: 'AI Recommendation Service', endpoint: 'localhost:8001/api/v1', status: 'Online' }
]

async function handleHealthCheck() {
  isChecking.value = true
  checkResult.value = null
  try {
    const res = await checkSystemHealth()
    checkResult.value = {
      success: true,
      message: res.message || 'Hệ thống TechStore đang hoạt động ổn định',
      details: res.data
    }
    activityLogs.value.unshift({
      action: 'Kiểm tra sức khỏe hệ thống',
      detail: `Trạng thái: ${res.data?.status ?? 'UP'}`,
      time: new Date().toLocaleTimeString('vi-VN'),
      success: true
    })
  } catch (err: any) {
    const msg = err.response?.data?.message || err.message || 'Lỗi khi kiểm tra sức khỏe hệ thống'
    checkResult.value = {
      success: false,
      message: msg
    }
    activityLogs.value.unshift({
      action: 'Kiểm tra sức khỏe hệ thống',
      detail: 'Không thể kết nối đến máy chủ',
      time: new Date().toLocaleTimeString('vi-VN'),
      success: false
    })
  } finally {
    isChecking.value = false
  }
}
</script>
