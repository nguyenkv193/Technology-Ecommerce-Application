<template>
  <div class="space-y-6">
    <!-- Top Action Bar -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-5 border-b border-zinc-200">
      <div>
        <div class="flex items-center gap-2">
          <h1 class="text-xl font-semibold tracking-tight text-zinc-900">Quản lý Sản phẩm</h1>
          <span class="px-2 py-0.5 rounded-full text-[10px] font-mono font-medium bg-zinc-100 text-zinc-700 border border-zinc-200">
            {{ totalElements }} items
          </span>
        </div>
        <p class="text-xs text-zinc-500 mt-1 font-mono">Danh mục thiết bị công nghệ, cấu hình thông số và giá bán sàn</p>
      </div>

      <router-link
        to="/products/create"
        class="inline-flex items-center gap-2 px-3.5 py-2 rounded-lg bg-zinc-900 hover:bg-zinc-800 text-white text-xs font-medium shadow-sm transition-all self-start sm:self-auto"
      >
        <Plus class="w-3.5 h-3.5" />
        <span>Thêm sản phẩm</span>
      </router-link>
    </div>

    <!-- Filters Bar (Linear Style) -->
    <div class="bg-white border border-zinc-200 rounded-xl p-3 flex flex-col lg:flex-row gap-3 items-stretch lg:items-center justify-between shadow-xs">
      <!-- Search Input -->
      <div class="relative w-full lg:w-72">
        <Search class="w-3.5 h-3.5 text-zinc-400 absolute left-3 top-1/2 -translate-y-1/2" />
        <input
          v-model="keyword"
          @keyup.enter="handleSearch"
          type="text"
          placeholder="Tìm theo tên sản phẩm, slug..."
          class="w-full bg-white border border-zinc-200 rounded-lg pl-9 pr-8 py-2 text-xs text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900/10 transition-colors"
        />
        <button
          v-if="keyword"
          @click="keyword = ''; handleSearch()"
          class="absolute right-2.5 top-1/2 -translate-y-1/2 text-zinc-400 hover:text-zinc-600 text-xs"
        >
          ✕
        </button>
      </div>

      <!-- Criteria Filters & Quick Action Buttons -->
      <div class="flex flex-wrap items-center gap-2 justify-end">
        <CustomSelect
          v-model="selectedCategoryId"
          :options="categoryFilterOptions"
          @change="handleSearch"
        />
        <CustomSelect
          v-model="selectedBrandId"
          :options="brandFilterOptions"
          @change="handleSearch"
        />
        <CustomSelect
          v-model="sortOption"
          :options="sortOptions"
          align="right"
          @change="handleSearch"
        />
        <button
          @click="handleSearch"
          class="h-9 px-3.5 rounded-lg bg-zinc-900 hover:bg-zinc-800 text-white text-xs font-medium shadow-xs transition-colors flex items-center gap-1.5 cursor-pointer"
        >
          <span>Lọc</span>
        </button>
        <button
          @click="resetFilters"
          class="h-9 px-2.5 rounded-lg text-zinc-600 hover:text-zinc-900 hover:bg-zinc-100 text-xs transition-colors cursor-pointer"
        >
          Đặt lại
        </button>
      </div>
    </div>

    <!-- Data Table (Linear High-Density) -->
    <div class="bg-white border border-zinc-200 rounded-xl overflow-hidden shadow-sm">
      <div class="overflow-x-auto">
        <table class="w-full text-left text-xs text-zinc-700">
          <thead class="text-[11px] font-mono font-medium text-zinc-500 uppercase bg-zinc-50/80 border-b border-zinc-200">
            <tr>
              <th class="py-3 px-4 w-14">Ảnh</th>
              <th class="py-3 px-4">Tên sản phẩm</th>
              <th class="py-3 px-4">Thương hiệu</th>
              <th class="py-3 px-4">Danh mục</th>
              <th class="py-3 px-4">Giá bán</th>
              <th class="py-3 px-4 text-center">Tồn kho</th>
              <th class="py-3 px-4 text-right">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-zinc-100 font-mono">
            <tr v-if="isLoading">
              <td colspan="7" class="py-12 text-center text-zinc-500 font-sans">
                <div class="flex items-center justify-center gap-2">
                  <Loader2 class="w-4 h-4 animate-spin text-zinc-500" />
                  <span>Đang tải danh sách sản phẩm...</span>
                </div>
              </td>
            </tr>
            <tr v-else-if="products.length === 0">
              <td colspan="7" class="py-12 text-center text-zinc-400 text-xs font-sans">
                Không tìm thấy sản phẩm nào phù hợp.
              </td>
            </tr>
            <tr
              v-for="product in products"
              :key="product.id"
              class="hover:bg-zinc-50/70 transition-colors group"
            >
              <td class="py-3 px-4">
                <div class="w-10 h-10 rounded-lg bg-zinc-50 border border-zinc-200 flex items-center justify-center overflow-hidden shrink-0">
                  <img
                    v-if="product.thumbnailUrl"
                    :src="product.thumbnailUrl"
                    :alt="product.name"
                    class="w-full h-full object-cover"
                  />
                  <Package v-else class="w-4 h-4 text-zinc-400" />
                </div>
              </td>
              <td class="py-3 px-4 max-w-xs font-sans">
                <div class="font-medium text-zinc-900 truncate group-hover:text-blue-600 transition-colors" :title="product.name">
                  {{ product.name }}
                </div>
                <div class="text-[11px] font-mono text-zinc-400 truncate">{{ product.slug }}</div>
              </td>
              <td class="py-3 px-4">
                <span class="px-2 py-0.5 rounded-md bg-zinc-100 border border-zinc-200 text-zinc-700 text-[11px]">
                  {{ product.brandName || 'Chưa gắn' }}
                </span>
              </td>
              <td class="py-3 px-4 font-sans text-xs text-zinc-600">
                <span>{{ product.categoryName || 'Mặc định' }}</span>
              </td>
              <td class="py-3 px-4">
                <div class="font-semibold text-zinc-900 tabular-nums">
                  {{ formatCurrency(getProductMinPrice(product)) }}
                  <span v-if="getProductMaxPrice(product) > getProductMinPrice(product)" class="text-zinc-500 font-normal">
                    – {{ formatCurrency(getProductMaxPrice(product)) }}
                  </span>
                </div>
                <div v-if="getProductOriginalPrice(product) > getProductMinPrice(product)" class="text-[10px] text-zinc-400 line-through tabular-nums">
                  {{ formatCurrency(getProductOriginalPrice(product)) }}
                </div>
              </td>
              <td class="py-3 px-4 text-center">
                <span
                  class="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[10px] font-medium"
                  :class="(product.totalStock ?? 0) > 5 ? 'bg-emerald-50 text-emerald-700 border border-emerald-200' : 'bg-rose-50 text-rose-700 border border-rose-200'"
                >
                  <span class="w-1 h-1 rounded-full" :class="(product.totalStock ?? 0) > 5 ? 'bg-emerald-500' : 'bg-rose-500'"></span>
                  <span>{{ product.totalStock ?? 0 }}</span>
                </span>
              </td>
              <td class="py-3 px-4 text-right">
                <div class="flex items-center justify-end gap-1 font-sans">
                  <router-link
                    :to="`/products/${product.id}/edit`"
                    class="p-1.5 rounded-md text-zinc-500 hover:text-zinc-900 hover:bg-zinc-100 transition-colors"
                    title="Chỉnh sửa"
                  >
                    <Edit3 class="w-3.5 h-3.5" />
                  </router-link>
                  <button
                    @click="askDeleteProduct(product)"
                    class="p-1.5 rounded-md text-zinc-500 hover:text-rose-600 hover:bg-rose-50 transition-colors"
                    title="Xóa"
                  >
                    <Trash2 class="w-3.5 h-3.5" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination Footer -->
      <div class="p-3.5 border-t border-zinc-200 flex items-center justify-between text-xs text-zinc-500 font-mono">
        <div>
          Tổng số <span class="font-semibold text-zinc-900">{{ totalElements }}</span> sản phẩm (Trang {{ currentPage + 1 }} / {{ totalPages || 1 }})
        </div>
        <div class="flex items-center gap-1.5">
          <button
            @click="changePage(currentPage - 1)"
            :disabled="currentPage === 0"
            class="px-2.5 py-1 rounded-md bg-white border border-zinc-200 hover:bg-zinc-50 text-zinc-700 hover:text-zinc-900 disabled:opacity-40 disabled:cursor-not-allowed transition-colors text-xs shadow-xs"
          >
            Trước
          </button>
          <button
            @click="changePage(currentPage + 1)"
            :disabled="currentPage >= totalPages - 1"
            class="px-2.5 py-1 rounded-md bg-white border border-zinc-200 hover:bg-zinc-50 text-zinc-700 hover:text-zinc-900 disabled:opacity-40 disabled:cursor-not-allowed transition-colors text-xs shadow-xs"
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
import { ref, computed, onMounted } from 'vue'
import { Plus, Search, Edit3, Trash2, Package, Loader2 } from 'lucide-vue-next'
import { toast } from 'vue-sonner'
import ConfirmModal from '@/components/common/ConfirmModal.vue'
import CustomSelect, { type CustomSelectOption } from '@/components/common/CustomSelect.vue'
import { fetchProducts, deleteProduct } from '@/api/products'
import { fetchCategories } from '@/api/categories'
import { fetchBrands } from '@/api/brands'
import type { Brand, Category, Product } from '@/types'

const products = ref<Product[]>([])
const categories = ref<Category[]>([])
const brands = ref<Brand[]>([])
const isLoading = ref(false)
const keyword = ref('')
const selectedCategoryId = ref<number | ''>('')
const selectedBrandId = ref<number | ''>('')
const sortOption = ref('createdAt:DESC')
const currentPage = ref(0)
const pageSize = ref(10)
const totalPages = ref(1)
const totalElements = ref(0)

const categoryFilterOptions = computed<CustomSelectOption[]>(() => [
  { value: '', label: 'Tất cả danh mục' },
  ...categories.value.map((c) => ({ value: c.id, label: c.name }))
])

const brandFilterOptions = computed<CustomSelectOption[]>(() => [
  { value: '', label: 'Tất cả thương hiệu' },
  ...brands.value.map((b) => ({ value: b.id, label: b.name }))
])

const sortOptions: CustomSelectOption[] = [
  { value: 'createdAt:DESC', label: 'Mới nhất' },
  { value: 'name:ASC', label: 'Tên A–Z' },
  { value: 'minPrice:ASC', label: 'Giá thấp → cao' },
  { value: 'minPrice:DESC', label: 'Giá cao → thấp' }
]

const isConfirmOpen = ref(false)
const isDeleting = ref(false)
const productToDelete = ref<Product | null>(null)

function formatCurrency(val: number): string {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val || 0)
}

function getProductMinPrice(product: Product): number {
  if (typeof product.minPrice === 'number') return product.minPrice
  if (product.variants && product.variants.length > 0) {
    return Math.min(...product.variants.map((v) => v.price || 0))
  }
  return 0
}

function getProductMaxPrice(product: Product): number {
  if (typeof product.maxPrice === 'number') return product.maxPrice
  if (product.variants && product.variants.length > 0) {
    return Math.max(...product.variants.map((v) => v.price || 0))
  }
  return getProductMinPrice(product)
}

function getProductOriginalPrice(product: Product): number {
  if (!product.variants || product.variants.length === 0) return 0
  const minPrice = getProductMinPrice(product)
  const matched = product.variants.find((v) => v.price === minPrice) ?? product.variants[0]
  return matched?.originalPrice || 0
}

async function loadProducts() {
  isLoading.value = true
  try {
    const [sortBy, sortDirection] = sortOption.value.split(':') as [string, 'ASC' | 'DESC']
    const res = await fetchProducts({
      keyword: keyword.value,
      categoryId: selectedCategoryId.value || undefined,
      brandId: selectedBrandId.value || undefined,
      sortBy,
      sortDirection,
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

async function loadFilterOptions() {
  try {
    const [cats, brs] = await Promise.all([fetchCategories(), fetchBrands()])
    categories.value = cats || []
    brands.value = brs || []
  } catch {
    // Bỏ qua nếu không tải được danh mục/thương hiệu cho filter
  }
}

function handleSearch() {
  currentPage.value = 0
  loadProducts()
}

function resetFilters() {
  keyword.value = ''
  selectedCategoryId.value = ''
  selectedBrandId.value = ''
  sortOption.value = 'createdAt:DESC'
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
  const deletedName = productToDelete.value.name
  isDeleting.value = true
  try {
    await deleteProduct(productToDelete.value.id)
    isConfirmOpen.value = false
    productToDelete.value = null
    toast.success(`Đã xóa sản phẩm "${deletedName}" thành công`)
    await loadProducts()
  } catch (err: any) {
    toast.error(err.response?.data?.message || 'Không thể xóa sản phẩm. Vui lòng thử lại.')
  } finally {
    isDeleting.value = false
  }
}

onMounted(() => {
  loadFilterOptions()
  loadProducts()
})
</script>
