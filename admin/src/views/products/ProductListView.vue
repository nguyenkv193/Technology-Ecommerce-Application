<template>
  <div class="space-y-6">
    <!-- Top Action Bar -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold tracking-tight text-slate-100">Quản lý Sản phẩm</h1>
        <p class="text-xs text-slate-400 mt-1">Danh mục sản phẩm công nghệ, thiết bị và linh kiện</p>
      </div>
      <router-link
        to="/products/create"
        class="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-500 text-white text-xs font-semibold shadow-md shadow-blue-600/30 transition-all self-start sm:self-auto"
      >
        <Plus class="w-4 h-4" />
        <span>Thêm sản phẩm</span>
      </router-link>
    </div>

    <!-- Filters Bar -->
    <div class="bg-slate-900 border border-slate-800 rounded-2xl p-4 flex flex-col md:flex-row gap-4 items-center justify-between shadow-sm">
      <div class="relative w-full md:w-80">
        <Search class="w-4 h-4 text-slate-500 absolute left-3.5 top-1/2 -translate-y-1/2" />
        <input
          v-model="keyword"
          @keyup.enter="handleSearch"
          type="text"
          placeholder="Tìm theo tên hoặc slug..."
          class="w-full bg-slate-950 border border-slate-800 rounded-xl pl-10 pr-4 py-2 text-xs text-slate-100 placeholder-slate-500 focus:outline-none focus:border-blue-500 transition-colors"
        />
      </div>

      <div class="flex items-center gap-3 w-full md:w-auto justify-end">
        <button
          @click="handleSearch"
          class="px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-medium border border-slate-700 transition-colors"
        >
          Lọc kết quả
        </button>
        <button
          @click="resetFilters"
          class="px-3 py-2 rounded-xl text-slate-400 hover:text-slate-200 text-xs font-medium transition-colors"
        >
          Đặt lại
        </button>
      </div>
    </div>

    <!-- Data Table -->
    <div class="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-sm">
      <div class="overflow-x-auto">
        <table class="w-full text-left text-sm text-slate-300">
          <thead class="text-xs font-semibold text-slate-400 uppercase bg-slate-950/60 border-b border-slate-800">
            <tr>
              <th class="py-3.5 px-4 w-16">Ảnh</th>
              <th class="py-3.5 px-4">Tên sản phẩm</th>
              <th class="py-3.5 px-4">Thương hiệu</th>
              <th class="py-3.5 px-4">Danh mục</th>
              <th class="py-3.5 px-4">Giá bán</th>
              <th class="py-3.5 px-4 text-center">Tồn kho</th>
              <th class="py-3.5 px-4 text-right">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-800/60">
            <tr v-if="isLoading">
              <td colspan="7" class="py-12 text-center text-slate-500">
                <div class="flex items-center justify-center gap-2">
                  <Loader2 class="w-5 h-5 animate-spin text-blue-500" />
                  <span>Đang tải danh sách sản phẩm...</span>
                </div>
              </td>
            </tr>
            <tr v-else-if="products.length === 0">
              <td colspan="7" class="py-12 text-center text-slate-500 text-xs">
                Không tìm thấy sản phẩm nào phù hợp.
              </td>
            </tr>
            <tr
              v-for="product in products"
              :key="product.id"
              class="hover:bg-slate-800/40 transition-colors"
            >
              <td class="py-3.5 px-4">
                <div class="w-12 h-12 rounded-xl bg-slate-950 border border-slate-800 flex items-center justify-center overflow-hidden shrink-0">
                  <img
                    v-if="product.primaryImageUrl"
                    :src="product.primaryImageUrl"
                    :alt="product.name"
                    class="w-full h-full object-cover"
                  />
                  <Package v-else class="w-5 h-5 text-slate-600" />
                </div>
              </td>
              <td class="py-3.5 px-4 max-w-xs">
                <div class="font-medium text-slate-100 truncate" :title="product.name">
                  {{ product.name }}
                </div>
                <div class="text-[11px] font-mono text-slate-500 truncate">{{ product.slug }}</div>
              </td>
              <td class="py-3.5 px-4">
                <span class="px-2.5 py-1 rounded-md bg-slate-800 text-slate-300 text-xs font-medium">
                  {{ product.brandName || 'Chưa gắn' }}
                </span>
              </td>
              <td class="py-3.5 px-4">
                <span class="text-xs text-slate-300">{{ product.categoryName || 'Mặc định' }}</span>
              </td>
              <td class="py-3.5 px-4">
                <div class="font-semibold text-slate-100 text-xs">
                  {{ formatCurrency(product.salePrice) }}
                </div>
                <div v-if="product.originalPrice > product.salePrice" class="text-[11px] text-slate-500 line-through">
                  {{ formatCurrency(product.originalPrice) }}
                </div>
              </td>
              <td class="py-3.5 px-4 text-center">
                <span
                  class="inline-block px-2.5 py-0.5 rounded-full text-xs font-semibold"
                  :class="(product.stockQuantity ?? 0) > 5 ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20' : 'bg-rose-500/10 text-rose-400 border border-rose-500/20'"
                >
                  {{ product.stockQuantity ?? 0 }}
                </span>
              </td>
              <td class="py-3.5 px-4 text-right">
                <div class="flex items-center justify-end gap-1">
                  <router-link
                    :to="`/products/${product.id}/edit`"
                    class="p-2 rounded-lg text-slate-400 hover:text-blue-400 hover:bg-slate-800 transition-colors"
                    title="Chỉnh sửa"
                  >
                    <Edit3 class="w-4 h-4" />
                  </router-link>
                  <button
                    @click="askDeleteProduct(product)"
                    class="p-2 rounded-lg text-slate-400 hover:text-rose-400 hover:bg-slate-800 transition-colors"
                    title="Xóa"
                  >
                    <Trash2 class="w-4 h-4" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination Footer -->
      <div class="p-4 border-t border-slate-800/80 flex items-center justify-between text-xs text-slate-400">
        <div>
          Tổng số <span class="font-semibold text-slate-200">{{ totalElements }}</span> sản phẩm (Trang {{ currentPage + 1 }} / {{ totalPages || 1 }})
        </div>
        <div class="flex items-center gap-2">
          <button
            @click="changePage(currentPage - 1)"
            :disabled="currentPage === 0"
            class="px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-200 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
          >
            Trước
          </button>
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

    <!-- Delete Confirmation Modal -->
    <ConfirmModal
      :is-open="isConfirmOpen"
      title="Xóa sản phẩm công nghệ"
      :message="`Bạn có chắc chắn muốn xóa sản phẩm '${productToDelete?.name}'? Hành động này sẽ gỡ bỏ sản phẩm khỏi cửa hàng và không thể hoàn tác.`"
      confirm-text="Xóa vĩnh viễn"
      :is-loading="isDeleting"
      @confirm="executeDelete"
      @cancel="isConfirmOpen = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Plus, Search, Edit3, Trash2, Package, Loader2 } from 'lucide-vue-next'
import ConfirmModal from '@/components/common/ConfirmModal.vue'
import { fetchProducts, deleteProduct } from '@/api/products'
import type { Product } from '@/types'

const products = ref<Product[]>([])
const isLoading = ref(false)
const keyword = ref('')
const currentPage = ref(0)
const pageSize = ref(10)
const totalPages = ref(1)
const totalElements = ref(0)

const isConfirmOpen = ref(false)
const isDeleting = ref(false)
const productToDelete = ref<Product | null>(null)

function formatCurrency(val: number): string {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val || 0)
}

async function loadProducts() {
  isLoading.value = true
  try {
    const res = await fetchProducts({
      keyword: keyword.value,
      page: currentPage.value,
      size: pageSize.value
    })
    products.value = res.content || []
    totalPages.value = res.totalPages || 1
    totalElements.value = res.totalElements || 0
  } finally {
    isLoading.value = false
  }
}

function handleSearch() {
  currentPage.value = 0
  loadProducts()
}

function resetFilters() {
  keyword.value = ''
  currentPage.value = 0
  loadProducts()
}

function changePage(page: number) {
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page
    loadProducts()
  }
}

function askDeleteProduct(p: Product) {
  productToDelete.value = p
  isConfirmOpen.value = true
}

async function executeDelete() {
  if (!productToDelete.value) return
  isDeleting.value = true
  try {
    await deleteProduct(productToDelete.value.id)
    isConfirmOpen.value = false
    productToDelete.value = null
    await loadProducts()
  } catch (err) {
    alert('Không thể xóa sản phẩm. Vui lòng thử lại.')
  } finally {
    isDeleting.value = false
  }
}

onMounted(() => {
  loadProducts()
})
</script>
