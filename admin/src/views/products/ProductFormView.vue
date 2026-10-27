<template>
  <div class="space-y-6">
    <!-- Top Bar -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold tracking-tight text-slate-100">
          {{ isEditMode ? 'Cập nhật sản phẩm' : 'Thêm sản phẩm mới' }}
        </h1>
        <p class="text-xs text-slate-400 mt-1">
          Thiết lập thông tin thương mại, hình ảnh, biến thể và thông số kỹ thuật
        </p>
      </div>
      <div class="flex items-center gap-3">
        <router-link
          to="/products"
          class="px-4 py-2 rounded-xl text-xs font-semibold text-slate-300 hover:bg-slate-800 transition-colors"
        >
          Hủy bỏ
        </router-link>
        <button
          type="button"
          @click="saveProduct"
          :disabled="isSaving"
          class="px-5 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-500 text-white text-xs font-semibold shadow-md shadow-blue-600/30 flex items-center gap-2 transition-all disabled:opacity-50"
        >
          <Loader2 v-if="isSaving" class="w-4 h-4 animate-spin" />
          <span>{{ isSaving ? 'Đang lưu...' : 'Lưu sản phẩm' }}</span>
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="isLoadingData" class="py-16 text-center text-slate-500">
      <Loader2 class="w-6 h-6 animate-spin text-blue-500 mx-auto mb-2" />
      <span>Đang tải thông tin sản phẩm...</span>
    </div>

    <!-- Main Form Grid -->
    <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- Left 2 Cols: Core Information & Specs -->
      <div class="lg:col-span-2 space-y-6">
        <!-- Card 1: Basic Information -->
        <div class="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-sm space-y-4">
          <h2 class="text-sm font-semibold uppercase tracking-wider text-blue-400">Thông tin cơ bản</h2>

          <div class="space-y-4">
            <div>
              <label class="block text-xs font-medium text-slate-300 mb-1.5">Tên sản phẩm *</label>
              <input
                v-model="form.name"
                @input="autoGenerateSlug"
                type="text"
                required
                placeholder="e.g. Laptop ASUS ROG Zephyrus G16 (2024)"
                class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-xs text-slate-100 placeholder-slate-500 focus:outline-none focus:border-blue-500 transition-colors"
              />
            </div>

            <div>
              <label class="block text-xs font-medium text-slate-300 mb-1.5">Slug URL *</label>
              <input
                v-model="form.slug"
                type="text"
                required
                placeholder="e.g. laptop-asus-rog-zephyrus-g16-2024"
                class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-xs font-mono text-slate-100 placeholder-slate-500 focus:outline-none focus:border-blue-500 transition-colors"
              />
            </div>

            <div>
              <label class="block text-xs font-medium text-slate-300 mb-1.5">Mô tả ngắn</label>
              <textarea
                v-model="form.shortDescription"
                rows="2"
                placeholder="Tóm tắt điểm nhấn nổi bật của sản phẩm..."
                class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-xs text-slate-100 placeholder-slate-500 focus:outline-none focus:border-blue-500 transition-colors"
              ></textarea>
            </div>

            <div>
              <label class="block text-xs font-medium text-slate-300 mb-1.5">Mô tả chi tiết</label>
              <textarea
                v-model="form.description"
                rows="5"
                placeholder="Bài viết giới thiệu chi tiết sản phẩm..."
                class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-xs text-slate-100 placeholder-slate-500 focus:outline-none focus:border-blue-500 transition-colors"
              ></textarea>
            </div>
          </div>
        </div>

        <!-- Card 2: Technical Specifications -->
        <div class="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-sm">
          <ProductSpecsEditor v-model="formSpecs" />
        </div>
      </div>

      <!-- Right 1 Col: Pricing, Brand, Category, Image -->
      <div class="space-y-6">
        <!-- Pricing & Inventory -->
        <div class="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-sm space-y-4">
          <h2 class="text-sm font-semibold uppercase tracking-wider text-blue-400">Giá bán & Tồn kho</h2>

          <div class="space-y-4">
            <div>
              <label class="block text-xs font-medium text-slate-300 mb-1.5">Giá gốc niêm yết (VND) *</label>
              <input
                v-model.number="form.originalPrice"
                type="number"
                min="0"
                step="1000"
                required
                class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-xs font-semibold text-slate-100 focus:outline-none focus:border-blue-500 transition-colors"
              />
            </div>

            <div>
              <label class="block text-xs font-medium text-slate-300 mb-1.5">Giá khuyến mãi / Thực bán (VND) *</label>
              <input
                v-model.number="form.salePrice"
                type="number"
                min="0"
                step="1000"
                required
                class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-xs font-semibold text-emerald-400 focus:outline-none focus:border-blue-500 transition-colors"
              />
            </div>

            <div>
              <label class="block text-xs font-medium text-slate-300 mb-1.5">Thời hạn bảo hành (Tháng)</label>
              <input
                v-model.number="form.warrantyMonths"
                type="number"
                min="0"
                class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-xs text-slate-100 focus:outline-none focus:border-blue-500 transition-colors"
              />
            </div>
          </div>
        </div>

        <!-- Taxonomy -->
        <div class="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-sm space-y-4">
          <h2 class="text-sm font-semibold uppercase tracking-wider text-blue-400">Phân loại</h2>

          <div class="space-y-4">
            <div>
              <label class="block text-xs font-medium text-slate-300 mb-1.5">Danh mục sản phẩm *</label>
              <select
                v-model.number="form.categoryId"
                required
                class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-xs text-slate-100 focus:outline-none focus:border-blue-500 transition-colors"
              >
                <option value="" disabled>-- Chọn danh mục --</option>
                <option v-for="c in categories" :key="c.id" :value="c.id">
                  {{ c.name }}
                </option>
              </select>
            </div>

            <div>
              <label class="block text-xs font-medium text-slate-300 mb-1.5">Thương hiệu *</label>
              <select
                v-model.number="form.brandId"
                required
                class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-xs text-slate-100 focus:outline-none focus:border-blue-500 transition-colors"
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
        <div class="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-sm space-y-4">
          <h2 class="text-sm font-semibold uppercase tracking-wider text-blue-400">Hình ảnh sản phẩm</h2>

          <div>
            <label class="block text-xs font-medium text-slate-300 mb-1.5">URL ảnh đại diện chính</label>
            <input
              v-model="form.primaryImageUrl"
              type="url"
              placeholder="https://example.com/image.png"
              class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-xs text-slate-100 placeholder-slate-500 focus:outline-none focus:border-blue-500 transition-colors mb-3"
            />
            <div
              v-if="form.primaryImageUrl"
              class="w-full h-44 rounded-xl bg-slate-950 border border-slate-800 overflow-hidden flex items-center justify-center p-2"
            >
              <img
                :src="form.primaryImageUrl"
                alt="Preview"
                class="max-w-full max-h-full object-contain rounded-lg"
              />
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
import { Loader2 } from 'lucide-vue-next'
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
