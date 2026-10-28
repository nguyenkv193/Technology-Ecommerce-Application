<template>
  <div class="space-y-6">
    <!-- Top Bar -->
    <div class="flex items-center justify-between pb-4 border-b border-zinc-800/80">
      <div>
        <div class="flex items-center gap-2">
          <router-link
            to="/products"
            class="p-1 rounded-md text-zinc-400 hover:text-zinc-200 hover:bg-zinc-800/60 transition-colors"
            title="Quay lại danh sách"
          >
            <ArrowLeft class="w-4 h-4" />
          </router-link>
          <h1 class="text-lg font-semibold tracking-tight text-zinc-100">
            {{ isEditMode ? 'Cập nhật sản phẩm' : 'Thêm sản phẩm mới' }}
          </h1>
          <span
            v-if="isEditMode"
            class="px-2 py-0.5 rounded text-[10px] font-mono font-medium bg-zinc-800 text-zinc-400 border border-zinc-700/50"
          >
            #{{ productId }}
          </span>
        </div>
        <p class="text-xs text-zinc-400 mt-1 pl-6">
          Thiết lập thông tin thương mại, hình ảnh, biến thể và thông số kỹ thuật
        </p>
      </div>
      <div class="flex items-center gap-2.5">
        <router-link
          to="/products"
          class="px-3.5 py-1.5 rounded-lg text-xs font-medium text-zinc-300 hover:text-white bg-zinc-900 hover:bg-zinc-800 border border-zinc-800 transition-colors"
        >
          Hủy bỏ
        </router-link>
        <button
          type="button"
          @click="saveProduct"
          :disabled="isSaving"
          class="px-4 py-1.5 rounded-lg bg-white hover:bg-zinc-200 text-zinc-950 text-xs font-semibold shadow-sm flex items-center gap-2 transition-all disabled:opacity-50"
        >
          <Loader2 v-if="isSaving" class="w-3.5 h-3.5 animate-spin" />
          <span>{{ isSaving ? 'Đang lưu...' : 'Lưu sản phẩm' }}</span>
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="isLoadingData" class="py-16 text-center text-zinc-500">
      <Loader2 class="w-5 h-5 animate-spin text-zinc-400 mx-auto mb-2" />
      <span class="text-xs">Đang tải thông tin sản phẩm...</span>
    </div>

    <!-- Main Form Grid -->
    <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Left 2 Cols: Core Information & Specs -->
      <div class="lg:col-span-2 space-y-5">
        <!-- Card 1: Basic Information -->
        <div class="bg-zinc-900/60 border border-zinc-800/80 rounded-xl p-5 shadow-sm space-y-4">
          <div class="flex items-center justify-between pb-3 border-b border-zinc-800/60">
            <h2 class="text-xs font-mono font-semibold uppercase tracking-wider text-zinc-300">Thông tin cơ bản</h2>
            <span class="text-[10px] text-zinc-500">Bắt buộc nhập tên & URL</span>
          </div>

          <div class="space-y-4">
            <div>
              <label class="block text-xs font-medium text-zinc-300 mb-1.5">Tên sản phẩm *</label>
              <input
                v-model="form.name"
                @input="autoGenerateSlug"
                type="text"
                required
                placeholder="e.g. Laptop ASUS ROG Zephyrus G16 (2024)"
                class="w-full bg-zinc-950 border border-zinc-800 rounded-lg px-3.5 py-2 text-xs text-zinc-100 placeholder-zinc-500 focus:outline-none focus:border-zinc-500 focus:ring-1 focus:ring-zinc-500 transition-colors"
              />
            </div>

            <div>
              <label class="block text-xs font-medium text-zinc-300 mb-1.5">Slug URL *</label>
              <input
                v-model="form.slug"
                type="text"
                required
                placeholder="e.g. laptop-asus-rog-zephyrus-g16-2024"
                class="w-full bg-zinc-950 border border-zinc-800 rounded-lg px-3.5 py-2 text-xs font-mono text-zinc-100 placeholder-zinc-500 focus:outline-none focus:border-zinc-500 focus:ring-1 focus:ring-zinc-500 transition-colors"
              />
            </div>

            <div>
              <label class="block text-xs font-medium text-zinc-300 mb-1.5">Mô tả ngắn</label>
              <textarea
                v-model="form.shortDescription"
                rows="2"
                placeholder="Tóm tắt điểm nhấn nổi bật của sản phẩm..."
                class="w-full bg-zinc-950 border border-zinc-800 rounded-lg px-3.5 py-2 text-xs text-zinc-100 placeholder-zinc-500 focus:outline-none focus:border-zinc-500 focus:ring-1 focus:ring-zinc-500 transition-colors"
              ></textarea>
            </div>

            <div>
              <label class="block text-xs font-medium text-zinc-300 mb-1.5">Mô tả chi tiết</label>
              <textarea
                v-model="form.description"
                rows="6"
                placeholder="Bài viết giới thiệu chi tiết sản phẩm..."
                class="w-full bg-zinc-950 border border-zinc-800 rounded-lg px-3.5 py-2 text-xs text-zinc-100 placeholder-zinc-500 focus:outline-none focus:border-zinc-500 focus:ring-1 focus:ring-zinc-500 transition-colors"
              ></textarea>
            </div>
          </div>
        </div>

        <!-- Card 2: Technical Specifications -->
        <div class="bg-zinc-900/60 border border-zinc-800/80 rounded-xl p-5 shadow-sm">
          <ProductSpecsEditor v-model="formSpecs" />
        </div>
      </div>

      <!-- Right 1 Col: Pricing, Brand, Category, Image -->
      <div class="space-y-5">
        <!-- Pricing & Inventory -->
        <div class="bg-zinc-900/60 border border-zinc-800/80 rounded-xl p-5 shadow-sm space-y-4">
          <h2 class="text-xs font-mono font-semibold uppercase tracking-wider text-zinc-300 pb-3 border-b border-zinc-800/60">
            Giá bán & Bảo hành
          </h2>

          <div class="space-y-3.5">
            <div>
              <label class="block text-xs font-medium text-zinc-300 mb-1.5">Giá gốc niêm yết (VND) *</label>
              <input
                v-model.number="form.originalPrice"
                type="number"
                min="0"
                step="1000"
                required
                class="w-full bg-zinc-950 border border-zinc-800 rounded-lg px-3.5 py-2 text-xs font-mono tabular-nums text-zinc-100 focus:outline-none focus:border-zinc-500 focus:ring-1 focus:ring-zinc-500 transition-colors"
              />
            </div>

            <div>
              <label class="block text-xs font-medium text-zinc-300 mb-1.5">Giá khuyến mãi / Thực bán (VND) *</label>
              <input
                v-model.number="form.salePrice"
                type="number"
                min="0"
                step="1000"
                required
                class="w-full bg-zinc-950 border border-zinc-800 rounded-lg px-3.5 py-2 text-xs font-mono tabular-nums font-semibold text-emerald-400 focus:outline-none focus:border-zinc-500 focus:ring-1 focus:ring-zinc-500 transition-colors"
              />
            </div>

            <div>
              <label class="block text-xs font-medium text-zinc-300 mb-1.5">Thời hạn bảo hành (Tháng)</label>
              <input
                v-model.number="form.warrantyMonths"
                type="number"
                min="0"
                class="w-full bg-zinc-950 border border-zinc-800 rounded-lg px-3.5 py-2 text-xs font-mono tabular-nums text-zinc-100 focus:outline-none focus:border-zinc-500 focus:ring-1 focus:ring-zinc-500 transition-colors"
              />
            </div>
          </div>
        </div>

        <!-- Taxonomy -->
        <div class="bg-zinc-900/60 border border-zinc-800/80 rounded-xl p-5 shadow-sm space-y-4">
          <h2 class="text-xs font-mono font-semibold uppercase tracking-wider text-zinc-300 pb-3 border-b border-zinc-800/60">
            Phân loại
          </h2>

          <div class="space-y-3.5">
            <div>
              <label class="block text-xs font-medium text-zinc-300 mb-1.5">Danh mục sản phẩm *</label>
              <select
                v-model.number="form.categoryId"
                required
                class="w-full bg-zinc-950 border border-zinc-800 rounded-lg px-3.5 py-2 text-xs text-zinc-100 focus:outline-none focus:border-zinc-500 focus:ring-1 focus:ring-zinc-500 transition-colors"
              >
                <option value="" disabled>-- Chọn danh mục --</option>
                <option v-for="c in categories" :key="c.id" :value="c.id">
                  {{ c.name }}
                </option>
              </select>
            </div>

            <div>
              <label class="block text-xs font-medium text-zinc-300 mb-1.5">Thương hiệu *</label>
              <select
                v-model.number="form.brandId"
                required
                class="w-full bg-zinc-950 border border-zinc-800 rounded-lg px-3.5 py-2 text-xs text-zinc-100 focus:outline-none focus:border-zinc-500 focus:ring-1 focus:ring-zinc-500 transition-colors"
              >
                <option value="" disabled>-- Chọn thương hiệu --</option>
                <option v-for="b in brands" :key="b.id" :value="b.id">
                  {{ b.name }}
                </option>
              </select>
            </div>
          </div>
        </div>

        <!-- Primary Image -->
        <div class="bg-zinc-900/60 border border-zinc-800/80 rounded-xl p-5 shadow-sm space-y-4">
          <h2 class="text-xs font-mono font-semibold uppercase tracking-wider text-zinc-300 pb-3 border-b border-zinc-800/60">
            Hình ảnh đại diện
          </h2>

          <div>
            <label class="block text-xs font-medium text-zinc-300 mb-1.5">URL ảnh sản phẩm</label>
            <input
              v-model="form.primaryImageUrl"
              type="url"
              placeholder="https://example.com/image.png"
              class="w-full bg-zinc-950 border border-zinc-800 rounded-lg px-3.5 py-2 text-xs text-zinc-100 placeholder-zinc-500 focus:outline-none focus:border-zinc-500 focus:ring-1 focus:ring-zinc-500 transition-colors mb-3"
            />
            <div
              v-if="form.primaryImageUrl"
              class="w-full h-44 rounded-lg bg-zinc-950 border border-zinc-800/80 overflow-hidden flex items-center justify-center p-2"
            >
              <img
                :src="form.primaryImageUrl"
                alt="Preview"
                class="max-w-full max-h-full object-contain rounded"
              />
            </div>
            <div
              v-else
              class="w-full h-32 rounded-lg bg-zinc-950/40 border border-dashed border-zinc-800 flex items-center justify-center text-[11px] text-zinc-500"
            >
              Chưa có ảnh đại diện
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Loader2, ArrowLeft } from 'lucide-vue-next'
import ProductSpecsEditor from '@/components/products/ProductSpecsEditor.vue'
import { fetchProductById, createProduct, updateProduct, type ProductFormData } from '@/api/products'
import { fetchCategories } from '@/api/categories'
import { fetchBrands } from '@/api/brands'
import type { Category, Brand } from '@/types'

const route = useRoute()
const router = useRouter()

const isEditMode = computed(() => !!route.params.id)
const productId = computed(() => Number(route.params.id))

const isLoadingData = ref(false)
const isSaving = ref(false)
const categories = ref<Category[]>([])
const brands = ref<Brand[]>([])

const form = reactive<ProductFormData>({
  name: '',
  slug: '',
  shortDescription: '',
  description: '',
  categoryId: 1,
  brandId: 1,
  originalPrice: 0,
  salePrice: 0,
  warrantyMonths: 12,
  primaryImageUrl: ''
})

const formSpecs = ref<Array<{ specKey: string; specValue: string; displayOrder?: number }>>([])

function autoGenerateSlug() {
  if (!isEditMode.value && form.name) {
    form.slug = form.name
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/[đĐ]/g, 'd')
      .replace(/([^0-9a-z-\s])/g, '')
      .replace(/\s+/g, '-')
      .replace(/-+/g, '-')
      .replace(/^-+|-+$/g, '')
  }
}

async function loadDropdowns() {
  const [cats, brs] = await Promise.all([fetchCategories(), fetchBrands()])
  categories.value = cats
  brands.value = brs
  if (cats.length > 0 && !form.categoryId) form.categoryId = cats[0].id
  if (brs.length > 0 && !form.brandId) form.brandId = brs[0].id
}

async function loadProductDetail() {
  if (!isEditMode.value) return
  isLoadingData.value = true
  try {
    const product = await fetchProductById(productId.value)
    form.name = product.name
    form.slug = product.slug
    form.shortDescription = product.shortDescription || ''
    form.description = product.description || ''
    form.categoryId = product.categoryId
    form.brandId = product.brandId
    form.originalPrice = product.originalPrice
    form.salePrice = product.salePrice
    form.warrantyMonths = product.warrantyMonths || 12
    form.primaryImageUrl = product.primaryImageUrl || ''

    if (product.specs) {
      formSpecs.value = product.specs.map((s) => ({
        specKey: s.specKey,
        specValue: s.specValue,
        displayOrder: s.displayOrder
      }))
    }
  } catch (err) {
    alert('Không thể tải thông tin chi tiết sản phẩm.')
    router.push('/products')
  } finally {
    isLoadingData.value = false
  }
}

async function saveProduct() {
  if (!form.name || !form.slug || form.salePrice <= 0) {
    alert('Vui lòng nhập đầy đủ tên, slug và giá bán hợp lệ.')
    return
  }

  isSaving.value = true
  try {
    const payload: ProductFormData = {
      ...form,
      specs: formSpecs.value
    }

    if (isEditMode.value) {
      await updateProduct(productId.value, payload)
    } else {
      await createProduct(payload)
    }
    router.push('/products')
  } catch (err: any) {
    alert(err.response?.data?.message || 'Có lỗi xảy ra khi lưu sản phẩm.')
  } finally {
    isSaving.value = false
  }
}

onMounted(async () => {
  await loadDropdowns()
  await loadProductDetail()
})
</script>
