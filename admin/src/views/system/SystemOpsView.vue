<template>
  <div class="space-y-6">
    <!-- Top Bar -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold tracking-tight text-slate-100">Vận hành Hệ thống</h1>
        <p class="text-xs text-slate-400 mt-1">Quản lý đồng bộ chỉ mục Elasticsearch, bộ nhớ đệm và giám sát dịch vụ</p>
      </div>
    </div>

    <!-- Operations Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Left 2 Cols: Elasticsearch Reindexing Engine -->
      <div class="lg:col-span-2 space-y-6">
        <div class="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-sm space-y-5">
          <div class="flex items-start justify-between">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-xl bg-blue-500/10 text-blue-400 border border-blue-500/20 flex items-center justify-center shrink-0">
                <Search class="w-5 h-5" />
              </div>
              <div>
                <h2 class="text-base font-semibold text-slate-100">Elasticsearch Search Index</h2>
                <p class="text-xs text-slate-400">Chỉ mục tìm kiếm toàn văn bản (Full-text search & autocomplete)</p>
              </div>
            </div>
            <span class="px-2.5 py-1 rounded-full text-xs font-medium bg-emerald-500/10 border border-emerald-500/20 text-emerald-400">
              Hoạt động
            </span>
          </div>

          <p class="text-xs text-slate-300 leading-relaxed">
            Hệ thống sử dụng Elasticsearch để phục vụ bộ lọc tìm kiếm và gợi ý từ khóa tức thì trên Storefront.
            Khi có sự sai lệch dữ liệu giữa PostgreSQL và Elasticsearch, quản trị viên có thể kích hoạt quá trình quét và nạp lại toàn bộ dữ liệu.
          </p>

          <!-- Result banner if done -->
          <div
            v-if="syncResult"
            class="p-4 rounded-xl border flex items-center gap-3 text-xs"
            :class="syncResult.success ? 'bg-emerald-500/10 border-emerald-500/20 text-emerald-300' : 'bg-rose-500/10 border-rose-500/20 text-rose-300'"
          >
            <CheckCircle v-if="syncResult.success" class="w-4 h-4 shrink-0 text-emerald-400" />
            <AlertCircle v-else class="w-4 h-4 shrink-0 text-rose-400" />
            <div>
              <p class="font-semibold">{{ syncResult.message }}</p>
              <p v-if="syncResult.count !== undefined" class="text-[11px] opacity-80 mt-0.5">
                Đã đánh chỉ mục thành công: <span class="font-mono font-bold">{{ syncResult.count }}</span> sản phẩm
              </p>
            </div>
          </div>

          <div class="pt-2 flex items-center gap-4">
            <button
              data-test="reindex-btn"
              type="button"
              @click="handleReindex"
              :disabled="isReindexing"
              class="px-5 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-500 text-white text-xs font-semibold shadow-md shadow-blue-600/30 flex items-center gap-2 transition-all disabled:opacity-50"
            >
              <RefreshCw class="w-4 h-4" :class="{ 'animate-spin': isReindexing }" />
              <span>{{ isReindexing ? 'Đang tạo lại chỉ mục...' : 'Đồng bộ lại chỉ mục' }}</span>
            </button>
            <span class="text-[11px] text-slate-500">
              Quá trình chạy ngầm qua API: <code class="font-mono text-slate-400">/admin/search/reindex-products</code>
            </span>
          </div>
        </div>

        <!-- Sync Activity Log -->
        <div class="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-sm space-y-4">
          <h3 class="text-xs font-semibold uppercase tracking-wider text-slate-400">Lịch sử tác vụ</h3>
          <div class="space-y-2">
            <div
              v-for="(log, idx) in activityLogs"
              :key="idx"
              class="p-3 rounded-xl bg-slate-950/60 border border-slate-800/80 flex items-center justify-between text-xs"
            >
              <div class="flex items-center gap-2.5">
                <span class="w-2 h-2 rounded-full" :class="log.success ? 'bg-emerald-400' : 'bg-rose-400'"></span>
                <span class="text-slate-200 font-medium">{{ log.action }}</span>
                <span class="text-slate-500">({{ log.detail }})</span>
              </div>
              <span class="text-[11px] text-slate-500 font-mono">{{ log.time }}</span>
            </div>
            <div v-if="activityLogs.length === 0" class="py-4 text-center text-xs text-slate-500">
              Chưa có thao tác nào trong phiên làm việc này.
            </div>
          </div>
        </div>
      </div>

      <!-- Right 1 Col: Service Architecture Status -->
      <div class="space-y-6">
        <div class="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-sm space-y-4">
          <h3 class="text-sm font-semibold text-slate-100 flex items-center gap-2">
            <Server class="w-4 h-4 text-blue-400" />
            <span>Dịch vụ nền tảng</span>
          </h3>

          <div class="space-y-3">
            <div
              v-for="svc in services"
              :key="svc.name"
              class="p-3.5 rounded-xl bg-slate-950/60 border border-slate-800 flex items-center justify-between"
            >
              <div>
                <p class="text-xs font-semibold text-slate-200">{{ svc.name }}</p>
                <p class="text-[11px] text-slate-500 font-mono">{{ svc.endpoint }}</p>
              </div>
              <span class="px-2 py-0.5 rounded text-[11px] font-semibold bg-emerald-500/10 border border-emerald-500/20 text-emerald-400">
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
import { Search, RefreshCw, Server, CheckCircle, AlertCircle } from 'lucide-vue-next'
import { reindexProducts } from '@/api/system'

interface ActivityLog {
  action: string
  detail: string
  time: string
  success: boolean
}

const isReindexing = ref(false)
const syncResult = ref<{ success: boolean; message: string; count?: number } | null>(null)

const activityLogs = ref<ActivityLog[]>([])

const services = [
  { name: 'Spring Boot Backend', endpoint: 'localhost:8080/api/v1', status: 'Online' },
  { name: 'Elasticsearch Engine', endpoint: 'localhost:9200', status: 'Ready' },
  { name: 'PostgreSQL Database', endpoint: 'localhost:5432/techstore', status: 'Connected' },
  { name: 'Redis Cache', endpoint: 'localhost:6379', status: 'Ready' },
  { name: 'AI Recommendation Service', endpoint: 'localhost:8000', status: 'Online' }
]

async function handleReindex() {
  isReindexing.value = true
  syncResult.value = null
  try {
    const res = await reindexProducts()
    syncResult.value = {
      success: true,
      message: res.message || 'Đã đồng bộ lại toàn bộ sản phẩm',
      count: res.data
    }
    activityLogs.value.unshift({
      action: 'Reindex Elasticsearch',
      detail: `Đã nạp ${res.data ?? 0} sản phẩm`,
      time: new Date().toLocaleTimeString('vi-VN'),
      success: true
    })
  } catch (err: any) {
    const msg = err.response?.data?.message || err.message || 'Lỗi khi gọi reindex'
    syncResult.value = {
      success: false,
      message: msg
    }
    activityLogs.value.unshift({
      action: 'Reindex Elasticsearch',
      detail: 'Thất bại',
      time: new Date().toLocaleTimeString('vi-VN'),
      success: false
    })
  } finally {
    isReindexing.value = false
  }
}
</script>
