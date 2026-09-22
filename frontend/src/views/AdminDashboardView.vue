<template>
  <div class="space-y-10 sm:space-y-12">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between pb-6 border-b border-zinc-200/80 gap-4">
      <div>
        <div class="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-md text-[11px] font-medium bg-zinc-100 text-zinc-700 border border-zinc-200/80 mb-2">
          Báo Cáo Thực Nghiệm Khoa Học
        </div>
        <h1 class="text-2xl sm:text-3xl font-bold text-zinc-900 tracking-tight">AI Analytics & Evaluation Dashboard</h1>
        <p class="text-xs sm:text-sm text-zinc-500 mt-1">Bảng điều khiển đo lường định lượng các thuật toán gợi ý và phân cụm khách hàng K-Means</p>
      </div>

      <button 
        @click="handleRetrain"
        :disabled="retraining"
        class="px-4 py-2.5 rounded-xl text-xs font-medium bg-zinc-900 hover:bg-zinc-800 text-white shadow-xs transition flex items-center gap-2 disabled:opacity-40"
      >
        <svg class="w-3.5 h-3.5" :class="{ 'animate-spin': retraining }" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21.5 2v6h-6M21.34 15.57a10 10 0 1 1-.57-8.38l5.67-5.67"/></svg>
        <span>{{ retraining ? 'Đang tái huấn luyện...' : 'Tái huấn luyện mô hình AI' }}</span>
      </button>
    </div>

    <!-- Section 1: Quantitative Evaluation Metrics (Chỉ số đánh giá khoa học) -->
    <section class="bg-white border border-zinc-200/80 rounded-2xl p-6 sm:p-7 space-y-6 shadow-xs">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-zinc-100">
        <div>
          <h2 class="text-base font-bold text-zinc-900 flex items-center gap-2">
            <svg class="w-4 h-4 text-zinc-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
            <span>Đánh Giá Hiệu Năng Thuật Toán Đề Xuất (Model Evaluation)</span>
          </h2>
          <p class="text-xs text-zinc-400 mt-0.5">Số liệu thực nghiệm định lượng phục vụ báo cáo Đồ án Tốt nghiệp</p>
        </div>

        <!-- Controls -->
        <div class="flex items-center gap-2.5">
          <select 
            v-model="evalAlgorithm"
            class="bg-zinc-50 border border-zinc-200 text-zinc-800 text-xs rounded-xl px-3 py-1.5 focus:outline-none focus:border-zinc-800"
          >
            <option value="hybrid">Hybrid Recommender (Lai kết hợp)</option>
            <option value="collaborative">Collaborative Filtering (Lọc cộng tác)</option>
            <option value="content_based">Content-Based (TF-IDF & Cosine)</option>
            <option value="popularity">Popularity (Độ phổ biến)</option>
          </select>

          <select 
            v-model="evalK"
            class="bg-zinc-50 border border-zinc-200 text-zinc-800 text-xs rounded-xl px-3 py-1.5 focus:outline-none focus:border-zinc-800"
          >
            <option :value="3">Top K = 3</option>
            <option :value="5">Top K = 5</option>
            <option :value="10">Top K = 10</option>
          </select>

          <button 
            @click="runEvaluation"
            :disabled="evaluating"
            class="px-3.5 py-1.5 bg-zinc-900 hover:bg-zinc-800 text-white rounded-xl text-xs font-medium transition disabled:opacity-40"
          >
            {{ evaluating ? 'Đang tính...' : 'Chạy thử nghiệm' }}
          </button>
        </div>
      </div>

      <!-- Metrics Cards -->
      <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <div class="p-5 rounded-xl bg-zinc-50/70 border border-zinc-200/70 space-y-1.5">
          <div class="text-xs text-zinc-500 font-medium">Precision@{{ evalMetrics?.k || evalK }}</div>
          <div class="text-2xl sm:text-3xl font-black text-zinc-900">
            {{ evalMetrics ? (evalMetrics.precision_at_k * 100).toFixed(1) + '%' : '--' }}
          </div>
          <p class="text-[11px] text-zinc-400">Tỷ lệ gợi ý trúng đích trong Top K</p>
        </div>

        <div class="p-5 rounded-xl bg-zinc-50/70 border border-zinc-200/70 space-y-1.5">
          <div class="text-xs text-zinc-500 font-medium">Recall@{{ evalMetrics?.k || evalK }}</div>
          <div class="text-2xl sm:text-3xl font-black text-zinc-900">
            {{ evalMetrics ? (evalMetrics.recall_at_k * 100).toFixed(1) + '%' : '--' }}
          </div>
          <p class="text-[11px] text-zinc-400">Tỷ lệ bao phủ các sản phẩm người dùng thích</p>
        </div>

        <div class="p-5 rounded-xl bg-zinc-50/70 border border-zinc-200/70 space-y-1.5">
          <div class="text-xs text-zinc-500 font-medium">NDCG@{{ evalMetrics?.k || evalK }}</div>
          <div class="text-2xl sm:text-3xl font-black text-zinc-900">
            {{ evalMetrics ? (evalMetrics.ndcg_at_k * 100).toFixed(1) + '%' : '--' }}
          </div>
          <p class="text-[11px] text-zinc-400">Chất lượng xếp hạng có chiết khấu vị trí</p>
        </div>

        <div class="p-5 rounded-xl bg-zinc-50/70 border border-zinc-200/70 space-y-1.5">
          <div class="text-xs text-zinc-500 font-medium">Hit Rate@{{ evalMetrics?.k || evalK }}</div>
          <div class="text-2xl sm:text-3xl font-black text-zinc-900">
            {{ evalMetrics ? (evalMetrics.hit_rate * 100).toFixed(1) + '%' : '--' }}
          </div>
          <p class="text-[11px] text-zinc-400">Tỷ lệ người dùng có ít nhất 1 gợi ý phù hợp</p>
        </div>
      </div>
    </section>

    <!-- Section 2: K-Means User Segmentation (Phân cụm người dùng) -->
    <section class="bg-white border border-zinc-200/80 rounded-2xl p-6 sm:p-7 space-y-6 shadow-xs">
      <div class="pb-4 border-b border-zinc-100">
        <h2 class="text-base font-bold text-zinc-900 flex items-center gap-2">
          <svg class="w-4 h-4 text-zinc-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
          <span>Phân Cụm Người Dùng (K-Means User Personas)</span>
        </h2>
        <p class="text-xs text-zinc-400 mt-0.5">Phân khúc chân dung khách hàng dựa trên tần suất và điểm số tương tác phần cứng</p>
      </div>

      <!-- Clusters Summary Cards -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-5">
        <div 
          v-for="c in clusterData.clusters" 
          :key="c.cluster_id"
          class="p-5 rounded-xl bg-zinc-50/70 border border-zinc-200/70 space-y-2.5"
        >
          <div class="flex items-center justify-between">
            <span class="text-[10px] font-mono font-medium px-2 py-0.5 rounded bg-white text-zinc-700 border border-zinc-200 shadow-2xs">
              Cluster #{{ c.cluster_id }}
            </span>
            <span class="text-xs text-zinc-500">{{ c.member_count }} người dùng</span>
          </div>
          <h3 class="text-sm font-bold text-zinc-900">{{ c.persona_name }}</h3>
          <div class="pt-2 border-t border-zinc-200/60 text-xs space-y-1 text-zinc-500">
            <div class="flex justify-between">
              <span>Điểm tương tác TB:</span>
              <span class="text-zinc-900 font-semibold">{{ c.avg_total_score }}</span>
            </div>
            <div class="flex justify-between">
              <span>Số lượt tương tác TB:</span>
              <span class="text-zinc-800">{{ c.avg_interactions }} lượt</span>
            </div>
          </div>
        </div>
      </div>

      <!-- User Assignments Table -->
      <div class="pt-2">
        <h4 class="text-xs font-semibold uppercase tracking-wider text-zinc-700 mb-3">Danh sách phân bổ mẫu người dùng (User Persona Mapping)</h4>
        <div class="overflow-x-auto rounded-xl border border-zinc-200/80">
          <table class="w-full text-left text-xs text-zinc-700">
            <thead class="bg-zinc-50 text-zinc-500 uppercase font-semibold border-b border-zinc-200">
              <tr>
                <th class="py-2.5 px-4">User ID</th>
                <th class="py-2.5 px-4">Cluster ID</th>
                <th class="py-2.5 px-4">Chân dung khách hàng (Persona)</th>
                <th class="py-2.5 px-4">Điểm TB</th>
                <th class="py-2.5 px-4 text-right">Tổng tương tác</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-zinc-100 bg-white">
              <tr v-for="u in clusterData.user_assignments" :key="u.user_id" class="hover:bg-zinc-50/80 transition">
                <td class="py-2.5 px-4 font-mono font-semibold text-zinc-900">#{{ u.user_id }}</td>
                <td class="py-2.5 px-4 font-mono text-zinc-600">{{ u.cluster_id }}</td>
                <td class="py-2.5 px-4 font-medium text-zinc-900">{{ u.persona_name }}</td>
                <td class="py-2.5 px-4 text-zinc-600">{{ u.avg_score }}</td>
                <td class="py-2.5 px-4 text-right font-semibold text-zinc-900">{{ u.total_interactions }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { aiApi } from '@/api/aiApi'
import type { EvaluationMetrics, ClusterAnalyticsResponse } from '@/types'

const evalAlgorithm = ref<string>('hybrid')
const evalK = ref<number>(5)
const evaluating = ref<boolean>(false)
const evalMetrics = ref<EvaluationMetrics | null>(null)

const clusterData = ref<ClusterAnalyticsResponse>({ total_clusters: 0, clusters: [], user_assignments: [] })
const retraining = ref<boolean>(false)

const runEvaluation = async () => {
  evaluating.value = true
  try {
    const res = await aiApi.evaluateMetrics(evalAlgorithm.value, evalK.value)
    evalMetrics.value = res
  } catch (err) {
    console.error('Lỗi tính toán chỉ số đánh giá:', err)
  } finally {
    evaluating.value = false
  }
}

const loadClusters = async () => {
  try {
    const res = await aiApi.getClusterAnalytics()
    clusterData.value = res
  } catch (err) {
    console.error('Lỗi tải dữ liệu phân cụm:', err)
  }
}

const handleRetrain = async () => {
  retraining.value = true
  try {
    await aiApi.retrain()
    await Promise.all([runEvaluation(), loadClusters()])
    alert('Đã tái huấn luyện thành công các mô hình AI!')
  } catch (err: any) {
    alert('Có lỗi khi tái huấn luyện: ' + err.message)
  } finally {
    retraining.value = false
  }
}

onMounted(() => {
  runEvaluation()
  loadClusters()
})
</script>
