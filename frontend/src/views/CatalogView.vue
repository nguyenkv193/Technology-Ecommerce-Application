<template>
  <div class="space-y-8">
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between pb-5 border-b border-zinc-200/80 gap-4">
      <div>
        <h1 class="text-2xl sm:text-3xl font-bold text-zinc-900 tracking-tight">Danh Mục Sản Phẩm Công Nghệ</h1>
        <p class="text-xs sm:text-sm text-zinc-500 mt-1">Lọc sản phẩm theo cấu hình phần cứng, thương hiệu và mức giá đầu tư</p>
      </div>

      <!-- Sort Selection -->
      <div class="flex items-center gap-2.5">
        <span class="text-xs text-zinc-400">Sắp xếp:</span>
        <select 
          v-model="filters.sortBy" 
          @change="fetchProducts"
          class="bg-white border border-zinc-200 text-zinc-800 text-xs rounded-xl px-3 py-1.5 focus:outline-none focus:border-zinc-800 shadow-2xs"
        >
          <option value="createdAt">Mới nhất</option>
          <option value="name">Tên A-Z</option>
        </select>
        <select 
          v-model="filters.sortDirection" 
          @change="fetchProducts"
          class="bg-white border border-zinc-200 text-zinc-800 text-xs rounded-xl px-3 py-1.5 focus:outline-none focus:border-zinc-800 shadow-2xs"
        >
          <option value="DESC">Giảm dần</option>
          <option value="ASC">Tăng dần</option>
        </select>
      </div>
    </div>

    <!-- Main Content Layout (Sidebar + Grid) -->
    <div class="grid grid-cols-1 lg:grid-cols-4 gap-8 items-start">
      <!-- Filter Sidebar -->
      <aside class="bg-white border border-zinc-200/80 rounded-2xl p-5 space-y-5 lg:sticky lg:top-24 shadow-xs">
        <div class="flex items-center justify-between pb-3 border-b border-zinc-100">
          <h3 class="font-semibold text-zinc-900 text-xs uppercase tracking-wider">Bộ Lọc Tìm Kiếm</h3>
          <button @click="resetFilters" class="text-xs text-zinc-500 hover:text-zinc-900 transition">Đặt lại</button>
        </div>

        <!-- Keyword Filter -->
        <div>
          <label class="block text-xs font-medium text-zinc-700 mb-1.5">Từ khóa tìm kiếm</label>
          <input 
            v-model="filters.keyword"
            @keyup.enter="fetchProducts"
            type="text" 
            placeholder="Ví dụ: M3, OLED, 16GB..." 
            class="w-full bg-zinc-50 border border-zinc-200 rounded-xl px-3 py-2 text-xs text-zinc-900 placeholder-zinc-400 focus:bg-white focus:outline-none focus:border-zinc-800 transition"
          />
        </div>

        <!-- Category Filter -->
        <div>
          <label class="block text-xs font-medium text-zinc-700 mb-2">Danh mục thiết bị</label>
          <div class="space-y-1 max-h-48 overflow-y-auto pr-1">
            <label class="flex items-center gap-2 text-xs text-zinc-600 hover:text-zinc-900 cursor-pointer py-1">
              <input type="radio" :value="null" v-model="filters.categoryId" @change="fetchProducts" class="accent-zinc-900" />
              <span>Tất cả danh mục</span>
            </label>
            <label 
              v-for="cat in categories" 
              :key="cat.id" 
              class="flex items-center gap-2 text-xs text-zinc-600 hover:text-zinc-900 cursor-pointer py-1"
            >
              <input type="radio" :value="cat.id" v-model="filters.categoryId" @change="fetchProducts" class="accent-zinc-900" />
              <span>{{ cat.name }}</span>
            </label>
          </div>
        </div>

        <!-- Brand Filter -->
        <div>
          <label class="block text-xs font-medium text-zinc-700 mb-2">Thương hiệu</label>
          <div class="space-y-1 max-h-48 overflow-y-auto pr-1">
            <label class="flex items-center gap-2 text-xs text-zinc-600 hover:text-zinc-900 cursor-pointer py-1">
              <input type="radio" :value="null" v-model="filters.brandId" @change="fetchProducts" class="accent-zinc-900" />
              <span>Tất cả thương hiệu</span>
            </label>
            <label 
              v-for="brand in brands" 
              :key="brand.id" 
              class="flex items-center gap-2 text-xs text-zinc-600 hover:text-zinc-900 cursor-pointer py-1"
            >
              <input type="radio" :value="brand.id" v-model="filters.brandId" @change="fetchProducts" class="accent-zinc-900" />
              <span>{{ brand.name }}</span>
            </label>
          </div>
        </div>

        <!-- Price Range Filter -->
        <div>
          <label class="block text-xs font-medium text-zinc-700 mb-2">Khoảng giá</label>
          <div class="space-y-1.5">
            <button 
              v-for="(p, idx) in priceOptions" 
              :key="idx"
              @click="selectPriceRange(p.min, p.max)"
              :class="isPriceSelected(p.min, p.max) ? 'bg-zinc-900 text-white font-medium' : 'bg-zinc-50 text-zinc-600 border border-zinc-200/80 hover:bg-zinc-100'"
              class="w-full text-left px-3 py-1.5 rounded-lg text-xs transition"
            >
              {{ p.label }}
            </button>
          </div>
        </div>

        <button 
          @click="fetchProducts"
          class="w-full py-2 bg-zinc-900 hover:bg-zinc-800 text-white text-xs font-medium rounded-xl shadow-xs transition"
        >
          Áp dụng bộ lọc
        </button>
      </aside>

      <!-- Products Grid & Pagination -->
      <main class="lg:col-span-3 space-y-8">
        <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-5">
          <div v-for="i in 6" :key="i" class="bg-white border border-zinc-200/80 rounded-2xl p-4 animate-pulse space-y-3">
            <div class="bg-zinc-100 rounded-xl aspect-square"></div>
            <div class="h-3.5 bg-zinc-100 rounded w-3/4"></div>
            <div class="h-3 bg-zinc-100 rounded w-1/2"></div>
          </div>
        </div>

        <div v-else-if="products.length === 0" class="text-center py-16 bg-white rounded-2xl border border-zinc-200/80 p-8">
          <svg class="w-12 h-12 mx-auto text-zinc-400 mb-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/><line x1="8" y1="11" x2="14" y2="11"/></svg>
          <h3 class="text-base font-semibold text-zinc-800">Không tìm thấy sản phẩm phù hợp</h3>
          <p class="text-xs text-zinc-500 mt-1 max-w-sm mx-auto">Vui lòng điều chỉnh lại mức giá, thương hiệu hoặc từ khóa tìm kiếm.</p>
          <button @click="resetFilters" class="mt-4 px-4 py-2 rounded-xl text-xs font-medium bg-zinc-900 text-white hover:bg-zinc-800 transition">
            Xóa bộ lọc
          </button>
        </div>

        <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-5">
          <ProductCard 
            v-for="p in products" 
            :key="p.id" 
            :product="p" 
          />
        </div>

        <!-- Pagination -->
        <div v-if="totalPages > 1" class="flex items-center justify-center gap-2 pt-6 border-t border-zinc-200/80">
          <button 
            :disabled="filters.page === 0"
            @click="changePage(filters.page - 1)"
            class="px-3.5 py-1.5 rounded-xl text-xs font-medium bg-white border border-zinc-200 text-zinc-700 hover:bg-zinc-50 disabled:opacity-40 transition shadow-2xs"
          >
            Trang trước
          </button>

          <span class="text-xs text-zinc-500 px-3">
            Trang {{ filters.page + 1 }} / {{ totalPages }}
          </span>

          <button 
            :disabled="filters.page >= totalPages - 1"
            @click="changePage(filters.page + 1)"
            class="px-3.5 py-1.5 rounded-xl text-xs font-medium bg-white border border-zinc-200 text-zinc-700 hover:bg-zinc-50 disabled:opacity-40 transition shadow-2xs"
          >
            Trang sau
          </button>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import ProductCard from '@/components/ProductCard.vue'
import { catalogApi } from '@/api/catalogApi'
import type { Product, Category, Brand } from '@/types'

const route = useRoute()

const products = ref<Product[]>([])
const categories = ref<Category[]>([])
const brands = ref<Brand[]>([])
const loading = ref<boolean>(false)
const totalPages = ref<number>(1)

interface FilterState {
  keyword: string
  categoryId: number | null
  brandId: number | null
  minPrice: number | null
  maxPrice: number | null
  page: number
  size: number
  sortBy: string
  sortDirection: string
}

const filters = reactive<FilterState>({
  keyword: (route.query.keyword as string) || '',
  categoryId: route.query.categoryId ? Number(route.query.categoryId) : null,
  brandId: null,
  minPrice: null,
  maxPrice: null,
  page: 0,
  size: 9,
  sortBy: 'createdAt',
  sortDirection: 'DESC'
})

const priceOptions = [
  { label: 'Tất cả mức giá', min: null, max: null },
  { label: 'Dưới 15 triệu', min: null, max: 15000000 },
  { label: 'Từ 15 - 30 triệu', min: 15000000, max: 30000000 },
  { label: 'Từ 30 - 50 triệu', min: 30000000, max: 50000000 },
  { label: 'Trên 50 triệu', min: 50000000, max: null }
]

const selectPriceRange = (min: number | null, max: number | null) => {
  filters.minPrice = min
  filters.maxPrice = max
  fetchProducts()
}

const isPriceSelected = (min: number | null, max: number | null) => {
  return filters.minPrice === min && filters.maxPrice === max
}

const resetFilters = () => {
  filters.keyword = ''
  filters.categoryId = null
  filters.brandId = null
  filters.minPrice = null
  filters.maxPrice = null
  filters.page = 0
  fetchProducts()
}

const changePage = (newPage: number) => {
  filters.page = newPage
  fetchProducts()
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const params: Record<string, any> = {
      page: filters.page,
      size: filters.size,
      sortBy: filters.sortBy,
      sortDirection: filters.sortDirection
    }
    if (filters.keyword) params.keyword = filters.keyword
    if (filters.categoryId) params.categoryId = filters.categoryId
    if (filters.brandId) params.brandId = filters.brandId
    if (filters.minPrice != null) params.minPrice = filters.minPrice
    if (filters.maxPrice != null) params.maxPrice = filters.maxPrice

    const res = await catalogApi.getProducts(params)
    if (res.success && res.data) {
      products.value = res.data.content || []
      totalPages.value = res.data.totalPages || 1
    }
  } catch (err) {
    console.error('Lỗi tải sản phẩm:', err)
  } finally {
    loading.value = false
  }
}

const fetchFilterOptions = async () => {
  try {
    const [catRes, brandRes] = await Promise.all([
      catalogApi.getCategories(),
      catalogApi.getBrands()
    ])
    if (catRes.success) categories.value = catRes.data || []
    if (brandRes.success) brands.value = brandRes.data || []
  } catch (err) {
    console.error('Lỗi tải danh mục / thương hiệu:', err)
  }
}

watch(() => route.query, (newQuery) => {
  if (newQuery.keyword !== undefined) filters.keyword = newQuery.keyword as string
  if (newQuery.categoryId !== undefined) filters.categoryId = Number(newQuery.categoryId)
  fetchProducts()
})

onMounted(() => {
  fetchFilterOptions()
  fetchProducts()
})
</script>
