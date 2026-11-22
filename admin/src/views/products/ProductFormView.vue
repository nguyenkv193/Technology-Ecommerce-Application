<template>
  <div class="space-y-6">
    <!-- Top Bar -->
    <div class="flex items-center justify-between pb-4 border-b border-zinc-200">
      <div>
        <div class="flex items-center gap-2">
          <router-link
            to="/products"
            class="p-1 rounded-md text-zinc-500 hover:text-zinc-900 hover:bg-zinc-100 transition-colors"
            title="Quay lại danh sách"
          >
            <ArrowLeft class="w-4 h-4" />
          </router-link>
          <h1 class="text-lg font-semibold tracking-tight text-zinc-900">
            {{ isEditMode ? 'Cập nhật sản phẩm' : 'Thêm sản phẩm mới' }}
          </h1>
          <span
            v-if="isEditMode"
            class="px-2 py-0.5 rounded text-[10px] font-mono font-medium bg-zinc-100 text-zinc-700 border border-zinc-200"
          >
            #{{ productId }}
          </span>
        </div>
        <p class="text-xs text-zinc-500 mt-1 pl-6 font-mono">
          Thiết lập thông tin thương mại, hình ảnh, biến thể cấu hình và thông số kỹ thuật
        </p>
      </div>
      <div class="flex items-center gap-2.5">
        <router-link
          to="/products"
          class="px-3.5 py-1.5 rounded-lg text-xs font-medium text-zinc-700 hover:text-zinc-900 bg-white hover:bg-zinc-50 border border-zinc-200 shadow-xs transition-colors"
        >
          Hủy bỏ
        </router-link>
        <button
          type="button"
          @click="saveProduct"
          :disabled="isSaving"
          class="px-4 py-1.5 rounded-lg bg-zinc-900 hover:bg-zinc-800 text-white text-xs font-semibold shadow-sm flex items-center gap-2 transition-all disabled:opacity-50"
        >
          <Loader2 v-if="isSaving" class="w-3.5 h-3.5 animate-spin" />
          <span>{{ isSaving ? 'Đang lưu...' : 'Lưu sản phẩm' }}</span>
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="isLoadingData" class="py-16 text-center text-zinc-500">
      <Loader2 class="w-5 h-5 animate-spin text-zinc-500 mx-auto mb-2" />
      <span class="text-xs font-mono">Đang tải thông tin sản phẩm...</span>
    </div>

    <!-- Main Form Grid -->
    <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Left 2 Cols: Core Information, Variants & Specs -->
      <div class="lg:col-span-2 space-y-5">
        <!-- Card 1: Basic Information -->
        <div class="bg-white border border-zinc-200 rounded-xl p-5 shadow-sm space-y-4">
          <div class="flex items-center justify-between pb-3 border-b border-zinc-100">
            <h2 class="text-xs font-mono font-semibold uppercase tracking-wider text-zinc-900">Thông tin cơ bản</h2>
            <span class="text-[10px] text-zinc-400 font-mono">Bắt buộc nhập tên &amp; danh mục</span>
          </div>

          <div class="space-y-4">
            <div>
              <label class="block text-xs font-medium text-zinc-700 mb-1.5">Tên sản phẩm *</label>
              <input
                v-model="form.name"
                @input="autoGenerateSlug"
                type="text"
                required
                placeholder="e.g. Laptop ASUS ROG Zephyrus G16 (2024)"
                class="w-full bg-white border border-zinc-200 rounded-lg px-3.5 py-2 text-xs text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900/10 transition-colors"
              />
            </div>

            <div>
              <label class="block text-xs font-medium text-zinc-700 mb-1.5">Slug URL</label>
              <input
                v-model="form.slug"
                type="text"
                placeholder="e.g. laptop-asus-rog-zephyrus-g16-2024"
                class="w-full bg-white border border-zinc-200 rounded-lg px-3.5 py-2 text-xs font-mono text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900/10 transition-colors"
              />
            </div>

            <div>
              <label class="block text-xs font-medium text-zinc-700 mb-1.5">Mô tả chi tiết</label>
              <textarea
                v-model="form.description"
                rows="6"
                placeholder="Bài viết giới thiệu chi tiết sản phẩm..."
                class="w-full bg-white border border-zinc-200 rounded-lg px-3.5 py-2 text-xs text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900/10 transition-colors"
              ></textarea>
            </div>
          </div>
        </div>

        <!-- Card 2: Variants / SKUs -->
        <div class="bg-white border border-zinc-200 rounded-xl p-5 shadow-sm space-y-4">
          <div class="flex items-center justify-between pb-3 border-b border-zinc-100">
            <div class="flex items-center gap-2">
              <h2 class="text-xs font-mono font-semibold uppercase tracking-wider text-zinc-900">
                Biến thể cấu hình &amp; Giá bán *
              </h2>
              <span class="text-[10px] font-mono px-1.5 py-0.5 rounded bg-zinc-100 text-zinc-600 border border-zinc-200">
                {{ form.variants.length }} phiên bản
              </span>
            </div>
            <button
              type="button"
              @click="addVariant"
              class="text-xs font-medium text-zinc-700 hover:text-zinc-900 flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-white hover:bg-zinc-50 border border-zinc-200 shadow-xs transition-all"
            >
              <Plus class="w-3.5 h-3.5" />
              <span>Thêm phiên bản</span>
            </button>
          </div>

          <div class="space-y-3">
            <div
              v-for="(variant, idx) in form.variants"
              :key="variant.id ?? idx"
              class="p-4 rounded-xl bg-zinc-50/80 border border-zinc-200 space-y-3"
            >
              <div class="flex items-center justify-between">
                <span class="text-[11px] font-mono font-semibold text-zinc-700">
                  Phiên bản #{{ idx + 1 }}
                </span>
                <button
                  v-if="form.variants.length > 1"
                  type="button"
                  @click="removeVariant(idx)"
                  class="p-1.5 rounded-lg text-zinc-400 hover:text-rose-600 hover:bg-zinc-200/60 transition-colors"
                  title="Xóa phiên bản"
                >
                  <Trash2 class="w-3.5 h-3.5" />
                </button>
              </div>

              <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label class="block text-[11px] font-medium text-zinc-700 mb-1">Tên phiên bản *</label>
                  <input
                    v-model="variant.name"
                    type="text"
                    required
                    placeholder="e.g. 16GB RAM / 512GB SSD - Đen"
                    class="w-full bg-white border border-zinc-200 rounded-lg px-3 py-2 text-xs text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900"
                  />
                </div>
                <div>
                  <label class="block text-[11px] font-medium text-zinc-700 mb-1">Mã SKU</label>
                  <input
                    v-model="variant.sku"
                    type="text"
                    placeholder="e.g. MBP-M3-16-512"
                    class="w-full bg-white border border-zinc-200 rounded-lg px-3 py-2 text-xs font-mono text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900"
                  />
                </div>
              </div>

              <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
                <div>
                  <label class="block text-[11px] font-medium text-zinc-700 mb-1">Giá bán (VND) *</label>
                  <input
                    v-model.number="variant.price"
                    type="number"
                    min="1000"
                    step="1000"
                    required
                    class="w-full bg-white border border-zinc-200 rounded-lg px-3 py-2 text-xs font-mono tabular-nums font-semibold text-emerald-700 focus:outline-none focus:border-zinc-900"
                  />
                </div>
                <div>
                  <label class="block text-[11px] font-medium text-zinc-700 mb-1">Giá niêm yết (VND)</label>
                  <input
                    v-model.number="variant.originalPrice"
                    type="number"
                    min="0"
                    step="1000"
                    class="w-full bg-white border border-zinc-200 rounded-lg px-3 py-2 text-xs font-mono tabular-nums text-zinc-900 focus:outline-none focus:border-zinc-900"
                  />
                </div>
                <div>
                  <label class="block text-[11px] font-medium text-zinc-700 mb-1">Tồn kho *</label>
                  <input
                    v-model.number="variant.stock"
                    type="number"
                    min="0"
                    required
                    class="w-full bg-white border border-zinc-200 rounded-lg px-3 py-2 text-xs font-mono tabular-nums text-zinc-900 focus:outline-none focus:border-zinc-900"
                  />
                </div>
              </div>

              <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label class="block text-[11px] font-medium text-zinc-700 mb-1">Tên màu sắc</label>
                  <input
                    v-model="variant.colorName"
                    type="text"
                    placeholder="e.g. Titan Tự Nhiên"
                    class="w-full bg-white border border-zinc-200 rounded-lg px-3 py-2 text-xs text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900"
                  />
                </div>
                <div>
                  <label class="block text-[11px] font-medium text-zinc-700 mb-1">Mã màu (#RRGGBB)</label>
                  <input
                    v-model="variant.colorHex"
                    type="text"
                    placeholder="#8C8C8C"
                    class="w-full bg-white border border-zinc-200 rounded-lg px-3 py-2 text-xs font-mono text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Card 3: Technical Specifications -->
        <div class="bg-white border border-zinc-200 rounded-xl p-5 shadow-sm">
          <ProductSpecsEditor v-model="formAttributes" />
        </div>
      </div>

      <!-- Right 1 Col: Status, Taxonomy, Images -->
      <div class="space-y-5">
        <!-- Status & Summary -->
        <div class="bg-white border border-zinc-200 rounded-xl p-5 shadow-sm space-y-4">
          <h2 class="text-xs font-mono font-semibold uppercase tracking-wider text-zinc-900 pb-3 border-b border-zinc-100">
            Trạng thái hiển thị
          </h2>

          <div>
            <label class="block text-xs font-medium text-zinc-700 mb-1.5">Trạng thái sản phẩm</label>
            <select
              v-model="form.status"
              class="w-full bg-white border border-zinc-200 rounded-lg px-3.5 py-2 text-xs font-mono text-zinc-900 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900/10 transition-colors"
            >
              <option value="ACTIVE">ACTIVE (Đang mở bán)</option>
              <option value="INACTIVE">INACTIVE (Ngừng kinh doanh)</option>
              <option value="DRAFT">DRAFT (Bản nháp)</option>
              <option value="OUT_OF_STOCK">OUT_OF_STOCK (Hết hàng)</option>
            </select>
          </div>
        </div>

        <!-- Taxonomy -->
        <div class="bg-white border border-zinc-200 rounded-xl p-5 shadow-sm space-y-4">
          <h2 class="text-xs font-mono font-semibold uppercase tracking-wider text-zinc-900 pb-3 border-b border-zinc-100">
            Phân loại
          </h2>

          <div class="space-y-3.5">
            <div>
              <label class="block text-xs font-medium text-zinc-700 mb-1.5">Danh mục sản phẩm *</label>
              <select
                v-model.number="form.categoryId"
                required
                class="w-full bg-white border border-zinc-200 rounded-lg px-3.5 py-2 text-xs text-zinc-900 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900/10 transition-colors"
              >
                <option value="" disabled>-- Chọn danh mục --</option>
                <option v-for="c in categories" :key="c.id" :value="c.id">
                  {{ c.name }}
                </option>
              </select>
            </div>

            <div>
              <label class="block text-xs font-medium text-zinc-700 mb-1.5">Thương hiệu *</label>
              <select
                v-model.number="form.brandId"
                required
                class="w-full bg-white border border-zinc-200 rounded-lg px-3.5 py-2 text-xs text-zinc-900 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900/10 transition-colors"
              >
                <option value="" disabled>-- Chọn thương hiệu --</option>
                <option v-for="b in brands" :key="b.id" :value="b.id">
                  {{ b.name }}
                </option>
              </select>
            </div>
          </div>
        </div>

        <!-- Images -->
        <div class="bg-white border border-zinc-200 rounded-xl p-5 shadow-sm space-y-4">
          <h2 class="text-xs font-mono font-semibold uppercase tracking-wider text-zinc-900 pb-3 border-b border-zinc-100">
            Hình ảnh đại diện
          </h2>

          <div>
            <label class="block text-xs font-medium text-zinc-700 mb-1.5">URL ảnh đại diện</label>
            <input
              v-model="thumbnailUrl"
              type="url"
              placeholder="https://example.com/image.png"
              class="w-full bg-white border border-zinc-200 rounded-lg px-3.5 py-2 text-xs text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900/10 transition-colors mb-3"
            />
            <div
              v-if="thumbnailUrl"
              class="w-full h-44 rounded-lg bg-zinc-50 border border-zinc-200 overflow-hidden flex items-center justify-center p-2"
            >
              <img
                :src="thumbnailUrl"
                alt="Preview"
                class="max-w-full max-h-full object-contain rounded"
              />
            </div>
            <div
              v-else
              class="w-full h-32 rounded-lg bg-zinc-50 border border-dashed border-zinc-200 flex items-center justify-center text-[11px] text-zinc-400 font-mono"
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
import { Loader2, ArrowLeft, Plus, Trash2 } from 'lucide-vue-next'
import { toast } from 'vue-sonner'
import ProductSpecsEditor from '@/components/products/ProductSpecsEditor.vue'
import { fetchProductById, createProduct, updateProduct, type ProductFormData } from '@/api/products'
import { fetchCategories } from '@/api/categories'
import { fetchBrands } from '@/api/brands'
import type { Category, Brand, ProductAttribute, ProductImage, ProductStatus, ProductVariant } from '@/types'

const route = useRoute()
const router = useRouter()

const isEditMode = computed(() => !!route.params.id)
const productId = computed(() => Number(route.params.id))

const isLoadingData = ref(false)
const isSaving = ref(false)
const categories = ref<Category[]>([])
const brands = ref<Brand[]>([])

function createDefaultVariant(): ProductVariant {
  return {
    name: 'Tiêu chuẩn',
    sku: '',
    price: 0,
    originalPrice: undefined,
    colorName: '',
    colorHex: '',
    stock: 10,
    status: 'ACTIVE'
  }
}

const form = reactive<{
  name: string
  slug: string
  description: string
  categoryId: number
  brandId: number
  status: ProductStatus
  variants: ProductVariant[]
}>({
  name: '',
  slug: '',
  description: '',
  categoryId: 1,
  brandId: 1,
  status: 'ACTIVE',
  variants: [createDefaultVariant()]
})

const thumbnailUrl = ref('')
const existingImages = ref<ProductImage[]>([])
const formAttributes = ref<ProductAttribute[]>([])

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

function addVariant() {
  form.variants.push(createDefaultVariant())
}

function removeVariant(index: number) {
  if (form.variants.length <= 1) return
  form.variants.splice(index, 1)
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
    form.description = product.description || ''
    form.categoryId = product.categoryId
    form.brandId = product.brandId
    form.status = product.status || 'ACTIVE'

    if (product.variants && product.variants.length > 0) {
      form.variants = product.variants.map((v) => ({
        id: v.id,
        sku: v.sku || '',
        name: v.name,
        price: v.price,
        originalPrice: v.originalPrice,
        colorName: v.colorName || '',
        colorHex: v.colorHex || '',
        rewardPoints: v.rewardPoints,
        stock: v.stock ?? 0,
        status: v.status || 'ACTIVE'
      }))
    } else {
      form.variants = [createDefaultVariant()]
    }

    existingImages.value = product.images ? [...product.images] : []
    const thumb = product.images?.find((img) => img.isThumbnail)?.url || product.thumbnailUrl || product.images?.[0]?.url || ''
    thumbnailUrl.value = thumb

    if (product.attributes) {
      formAttributes.value = product.attributes.map((a) => ({
        id: a.id,
        name: a.name,
        value: a.value
      }))
    }
  } catch (err) {
    toast.error('Không thể tải thông tin chi tiết sản phẩm.')
    router.push('/products')
  } finally {
    isLoadingData.value = false
  }
}

async function saveProduct() {
  if (!form.name.trim() || !form.categoryId || !form.brandId) {
    toast.warning('Vui lòng nhập tên, danh mục và thương hiệu sản phẩm.')
    return
  }

  const validVariants = form.variants
    .filter((v) => v.name.trim())
    .map((v) => ({
      ...(v.id ? { id: v.id } : {}),
      ...(v.sku?.trim() ? { sku: v.sku.trim() } : {}),
      name: v.name.trim(),
      price: Number(v.price),
      ...(v.originalPrice && Number(v.originalPrice) > 0 ? { originalPrice: Number(v.originalPrice) } : {}),
      ...(v.colorName?.trim() ? { colorName: v.colorName.trim() } : {}),
      ...(v.colorHex?.trim() ? { colorHex: v.colorHex.trim() } : {}),
      ...(typeof v.rewardPoints === 'number' ? { rewardPoints: v.rewardPoints } : {}),
      stock: Number(v.stock ?? 0),
      status: v.status || 'ACTIVE'
    }))

  if (validVariants.length === 0 || validVariants.some((v) => !v.price || v.price <= 0 || v.stock < 0)) {
    toast.warning('Sản phẩm phải có ít nhất 1 phiên bản với giá bán > 0 và tồn kho >= 0.')
    return
  }

  const cleanAttributes = formAttributes.value
    .filter((a) => a.name.trim() && a.value.trim())
    .map((a) => ({
      ...(a.id ? { id: a.id } : {}),
      name: a.name.trim(),
      value: a.value.trim()
    }))

  const cleanImages: ProductImage[] = []
  const trimmedThumb = thumbnailUrl.value.trim()
  if (trimmedThumb) {
    cleanImages.push({
      url: trimmedThumb,
      isThumbnail: true,
      sortOrder: 0
    })
  }
  existingImages.value
    .filter((img) => img.url && img.url.trim() !== trimmedThumb)
    .forEach((img, idx) => {
      cleanImages.push({
        url: img.url.trim(),
        isThumbnail: false,
        sortOrder: idx + 1
      })
    })

  isSaving.value = true
  try {
    const payload: ProductFormData = {
      name: form.name.trim(),
      slug: form.slug.trim(),
      description: form.description,
      categoryId: form.categoryId,
      brandId: form.brandId,
      status: form.status,
      variants: validVariants,
      attributes: cleanAttributes,
      images: cleanImages
    }

    if (isEditMode.value) {
      await updateProduct(productId.value, payload)
      toast.success('Đã cập nhật sản phẩm thành công!')
    } else {
      await createProduct(payload)
      toast.success('Đã tạo sản phẩm mới thành công!')
    }
    router.push('/products')
  } catch (err: any) {
    toast.error(err.response?.data?.message || 'Có lỗi xảy ra khi lưu sản phẩm.')
  } finally {
    isSaving.value = false
  }
}

onMounted(async () => {
  await loadDropdowns()
  await loadProductDetail()
})
</script>
