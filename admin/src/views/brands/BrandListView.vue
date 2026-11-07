<template>
  <div class="space-y-6">
    <!-- Top Action Bar -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-5 border-b border-zinc-200">
      <div>
        <div class="flex items-center gap-2">
          <h1 class="text-xl font-semibold tracking-tight text-zinc-900">Quản lý Thương hiệu</h1>
          <span class="px-2 py-0.5 rounded-full text-[10px] font-mono font-medium bg-zinc-100 text-zinc-700 border border-zinc-200">
            {{ brands.length }} thương hiệu
          </span>
        </div>
        <p class="text-xs text-zinc-500 mt-1 font-mono">Các hãng công nghệ, nhà sản xuất phân phối chính hãng trên TechStore</p>
      </div>

      <button
        type="button"
        @click="openAddModal"
        class="inline-flex items-center gap-2 px-3.5 py-2 rounded-lg bg-zinc-900 hover:bg-zinc-800 text-white text-xs font-medium shadow-sm transition-all self-start sm:self-auto"
      >
        <Plus class="w-3.5 h-3.5" />
        <span>Thêm thương hiệu</span>
      </button>
    </div>

    <!-- Data Table (Linear High-Density) -->
    <div class="bg-white border border-zinc-200 rounded-xl overflow-hidden shadow-sm">
      <div class="overflow-x-auto">
        <table class="w-full text-left text-xs text-zinc-700">
          <thead class="text-[11px] font-mono font-medium text-zinc-500 uppercase bg-zinc-50/80 border-b border-zinc-200">
            <tr>
              <th class="py-3 px-5 w-16">Logo</th>
              <th class="py-3 px-4">Tên thương hiệu</th>
              <th class="py-3 px-4 font-mono">Slug URL</th>
              <th class="py-3 px-4">Xuất xứ</th>
              <th class="py-3 px-4 text-center">Số sản phẩm</th>
              <th class="py-3 px-5 text-right">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-zinc-100 font-mono">
            <tr v-if="isLoading">
              <td colspan="6" class="py-12 text-center text-zinc-500 font-sans">
                <div class="flex items-center justify-center gap-2">
                  <Loader2 class="w-4 h-4 animate-spin text-zinc-500" />
                  <span>Đang tải danh sách thương hiệu...</span>
                </div>
              </td>
            </tr>
            <tr v-else-if="brands.length === 0">
              <td colspan="6" class="py-12 text-center text-zinc-400 text-xs font-sans">
                Chưa có thương hiệu nào.
              </td>
            </tr>
            <tr
              v-for="brand in brands"
              :key="brand.id"
              class="hover:bg-zinc-50/70 transition-colors group"
            >
              <td class="py-3 px-5">
                <div class="w-9 h-9 rounded-lg bg-zinc-50 border border-zinc-200 flex items-center justify-center overflow-hidden p-1 shrink-0">
                  <img
                    v-if="brand.logoUrl"
                    :src="brand.logoUrl"
                    :alt="brand.name"
                    class="max-w-full max-h-full object-contain"
                  />
                  <Award v-else class="w-4 h-4 text-zinc-400" />
                </div>
              </td>
              <td class="py-3 px-4 font-semibold text-zinc-900 font-sans">
                {{ brand.name }}
              </td>
              <td class="py-3 px-4 font-mono text-[11px] text-zinc-500">
                {{ brand.slug }}
              </td>
              <td class="py-3 px-4 text-xs font-sans text-zinc-600">
                {{ brand.originCountry || 'Toàn cầu' }}
              </td>
              <td class="py-3 px-4 text-center">
                <span class="px-2 py-0.5 rounded-full text-[10px] font-medium bg-zinc-100 text-zinc-700 border border-zinc-200">
                  {{ brand.productCount ?? 0 }}
                </span>
              </td>
              <td class="py-3 px-5 text-right font-sans">
                <div class="flex items-center justify-end gap-1">
                  <button
                    @click="openEditModal(brand)"
                    class="p-1.5 rounded-md text-zinc-500 hover:text-zinc-900 hover:bg-zinc-100 transition-colors"
                    title="Chỉnh sửa"
                  >
                    <Edit3 class="w-3.5 h-3.5" />
                  </button>
                  <button
                    @click="askDelete(brand)"
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
    </div>

    <!-- Brand Form Modal -->
    <div
      v-if="isModalOpen"
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-zinc-900/40 backdrop-blur-sm"
      @click.self="isModalOpen = false"
    >
      <div class="w-full max-w-md bg-white border border-zinc-200 rounded-xl p-5 shadow-2xl space-y-4">
        <div class="flex items-center justify-between pb-2 border-b border-zinc-100">
          <h3 class="text-sm font-semibold text-zinc-900">
            {{ editingId ? 'Cập nhật thương hiệu' : 'Thêm thương hiệu mới' }}
          </h3>
          <button @click="isModalOpen = false" class="text-zinc-400 hover:text-zinc-600 text-xs">✕</button>
        </div>

        <form @submit.prevent="submitBrand" class="space-y-3.5">
          <div>
            <label class="block text-xs font-mono font-medium text-zinc-700 mb-1">Tên thương hiệu *</label>
            <input
              v-model="modalForm.name"
              @input="autoGenerateSlug"
              type="text"
              required
              placeholder="e.g. Apple, ASUS, Dell, Sony"
              class="w-full bg-white border border-zinc-200 rounded-lg px-3 py-2 text-xs text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900/10 transition-colors"
            />
          </div>

          <div>
            <label class="block text-xs font-mono font-medium text-zinc-700 mb-1">Slug URL *</label>
            <input
              v-model="modalForm.slug"
              type="text"
              required
              placeholder="e.g. apple, asus"
              class="w-full bg-white border border-zinc-200 rounded-lg px-3 py-2 text-xs font-mono text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900/10 transition-colors"
            />
          </div>

          <div>
            <label class="block text-xs font-mono font-medium text-zinc-700 mb-1">Quốc gia xuất xứ</label>
            <input
              v-model="modalForm.originCountry"
              type="text"
              placeholder="e.g. Hoa Kỳ, Đài Loan, Nhật Bản"
              class="w-full bg-white border border-zinc-200 rounded-lg px-3 py-2 text-xs text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900/10 transition-colors"
            />
          </div>

          <div>
            <label class="block text-xs font-mono font-medium text-zinc-700 mb-1">URL Logo</label>
            <input
              v-model="modalForm.logoUrl"
              type="url"
              placeholder="https://example.com/logo.png"
              class="w-full bg-white border border-zinc-200 rounded-lg px-3 py-2 text-xs text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900/10 transition-colors"
            />
          </div>

          <div class="pt-3 border-t border-zinc-100 flex items-center justify-end gap-2.5">
            <button
              type="button"
              @click="isModalOpen = false"
              class="px-3 py-1.5 rounded-lg text-xs font-medium text-zinc-700 hover:text-zinc-900 bg-white hover:bg-zinc-50 border border-zinc-200 shadow-xs transition-colors font-mono"
            >
              Hủy
            </button>
            <button
              type="submit"
              :disabled="isSubmitting"
              class="px-3.5 py-1.5 rounded-lg bg-zinc-900 hover:bg-zinc-800 text-white text-xs font-medium shadow-sm flex items-center gap-2 transition-all disabled:opacity-50 font-mono"
            >
              <Loader2 v-if="isSubmitting" class="w-3.5 h-3.5 animate-spin" />
              <span>{{ isSubmitting ? 'Đang lưu...' : 'Lưu thương hiệu' }}</span>
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Confirm Delete Modal -->
    <ConfirmModal
      :is-open="isConfirmOpen"
      title="Xóa thương hiệu"
      :message="`Bạn có chắc chắn muốn xóa thương hiệu '${brandToDelete?.name}'?`"
      confirm-text="Xóa thương hiệu"
      :is-loading="isDeleting"
      @confirm="executeDelete"
      @cancel="isConfirmOpen = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus, Edit3, Trash2, Award, Loader2 } from 'lucide-vue-next'
import { toast } from 'vue-sonner'
import ConfirmModal from '@/components/common/ConfirmModal.vue'
import { fetchBrands, createBrand, updateBrand, deleteBrand } from '@/api/brands'
import type { Brand } from '@/types'

const brands = ref<Brand[]>([])
const isLoading = ref(false)

const isModalOpen = ref(false)
const isSubmitting = ref(false)
const editingId = ref<number | null>(null)

const modalForm = reactive({
  name: '',
  slug: '',
  originCountry: '',
  logoUrl: '',
  description: ''
})

const isConfirmOpen = ref(false)
const isDeleting = ref(false)
const brandToDelete = ref<Brand | null>(null)

async function loadBrands() {
  isLoading.value = true
  try {
    brands.value = await fetchBrands()
  } finally {
    isLoading.value = false
  }
}

function openAddModal() {
  editingId.value = null
  modalForm.name = ''
  modalForm.slug = ''
  modalForm.originCountry = ''
  modalForm.logoUrl = ''
  modalForm.description = ''
  isModalOpen.value = true
}

function openEditModal(b: Brand) {
  editingId.value = b.id
  modalForm.name = b.name
  modalForm.slug = b.slug
  modalForm.originCountry = b.originCountry || ''
  modalForm.logoUrl = b.logoUrl || ''
  modalForm.description = b.description || ''
  isModalOpen.value = true
}

function autoGenerateSlug() {
  if (!editingId.value && modalForm.name) {
    modalForm.slug = modalForm.name
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

async function submitBrand() {
  if (!modalForm.name || !modalForm.slug) {
    toast.warning('Vui lòng nhập tên và slug cho thương hiệu.')
    return
  }
  isSubmitting.value = true
  try {
    if (editingId.value) {
      await updateBrand(editingId.value, modalForm)
      toast.success('Đã cập nhật thương hiệu thành công!')
    } else {
      await createBrand(modalForm)
      toast.success('Đã thêm thương hiệu mới thành công!')
    }
    isModalOpen.value = false
    await loadBrands()
  } catch (err: any) {
    toast.error(err.response?.data?.message || 'Có lỗi xảy ra khi lưu thương hiệu.')
  } finally {
    isSubmitting.value = false
  }
}

function askDelete(b: Brand) {
  brandToDelete.value = b
  isConfirmOpen.value = true
}

async function executeDelete() {
  if (!brandToDelete.value) return
  const deletedName = brandToDelete.value.name
  isDeleting.value = true
  try {
    await deleteBrand(brandToDelete.value.id)
    isConfirmOpen.value = false
    brandToDelete.value = null
    toast.success(`Đã xóa thương hiệu "${deletedName}" thành công`)
    await loadBrands()
  } catch (err: any) {
    toast.error(err.response?.data?.message || 'Không thể xóa thương hiệu.')
  } finally {
    isDeleting.value = false
  }
}

onMounted(() => {
  loadBrands()
})
</script>
