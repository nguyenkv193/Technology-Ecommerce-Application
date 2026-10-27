<template>
  <div class="space-y-6">
    <!-- Top Action Bar -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold tracking-tight text-slate-100">Quản lý Thương hiệu</h1>
        <p class="text-xs text-slate-400 mt-1">Các hãng công nghệ, nhà sản xuất phân phối trên TechStore</p>
      </div>
      <button
        type="button"
        @click="openAddModal"
        class="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-500 text-white text-xs font-semibold shadow-md shadow-blue-600/30 transition-all"
      >
        <Plus class="w-4 h-4" />
        <span>Thêm thương hiệu</span>
      </button>
    </div>

    <!-- Data Table -->
    <div class="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-sm">
      <div class="overflow-x-auto">
        <table class="w-full text-left text-sm text-slate-300">
          <thead class="text-xs font-semibold text-slate-400 uppercase bg-slate-950/60 border-b border-slate-800">
            <tr>
              <th class="py-3.5 px-6 w-16">Logo</th>
              <th class="py-3.5 px-4">Tên thương hiệu</th>
              <th class="py-3.5 px-4 font-mono">Slug URL</th>
              <th class="py-3.5 px-4">Xuất xứ</th>
              <th class="py-3.5 px-4 text-center">Số sản phẩm</th>
              <th class="py-3.5 px-6 text-right">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-800/60">
            <tr v-if="isLoading">
              <td colspan="6" class="py-12 text-center text-slate-500">
                <div class="flex items-center justify-center gap-2">
                  <Loader2 class="w-5 h-5 animate-spin text-blue-500" />
                  <span>Đang tải danh sách thương hiệu...</span>
                </div>
              </td>
            </tr>
            <tr v-else-if="brands.length === 0">
              <td colspan="6" class="py-12 text-center text-slate-500 text-xs">
                Chưa có thương hiệu nào.
              </td>
            </tr>
            <tr
              v-for="brand in brands"
              :key="brand.id"
              class="hover:bg-slate-800/40 transition-colors"
            >
              <td class="py-3.5 px-6">
                <div class="w-10 h-10 rounded-xl bg-slate-950 border border-slate-800 flex items-center justify-center overflow-hidden p-1 shrink-0">
                  <img
                    v-if="brand.logoUrl"
                    :src="brand.logoUrl"
                    :alt="brand.name"
                    class="max-w-full max-h-full object-contain"
                  />
                  <Award v-else class="w-5 h-5 text-slate-600" />
                </div>
              </td>
              <td class="py-3.5 px-4 font-semibold text-slate-100">
                {{ brand.name }}
              </td>
              <td class="py-3.5 px-4 font-mono text-xs text-slate-400">
                {{ brand.slug }}
              </td>
              <td class="py-3.5 px-4 text-xs text-slate-300">
                {{ brand.originCountry || 'Toàn cầu' }}
              </td>
              <td class="py-3.5 px-4 text-center">
                <span class="px-2.5 py-0.5 rounded-full text-xs font-medium bg-slate-800 text-slate-300">
                  {{ brand.productCount ?? 0 }}
                </span>
              </td>
              <td class="py-3.5 px-6 text-right">
                <div class="flex items-center justify-end gap-1">
                  <button
                    @click="openEditModal(brand)"
                    class="p-2 rounded-lg text-slate-400 hover:text-blue-400 hover:bg-slate-800 transition-colors"
                    title="Chỉnh sửa"
                  >
                    <Edit3 class="w-4 h-4" />
                  </button>
                  <button
                    @click="askDelete(brand)"
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
    </div>

    <!-- Brand Form Modal -->
    <div
      v-if="isModalOpen"
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm"
      @click.self="isModalOpen = false"
    >
      <div class="w-full max-w-md bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-2xl space-y-5">
        <h3 class="text-base font-semibold text-slate-100">
          {{ editingId ? 'Cập nhật thương hiệu' : 'Thêm thương hiệu mới' }}
        </h3>

        <form @submit.prevent="submitBrand" class="space-y-4">
          <div>
            <label class="block text-xs font-medium text-slate-300 mb-1.5">Tên thương hiệu *</label>
            <input
              v-model="modalForm.name"
              @input="autoGenerateSlug"
              type="text"
              required
              placeholder="e.g. Apple, ASUS, Dell, Sony"
              class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-xs text-slate-100 placeholder-slate-500 focus:outline-none focus:border-blue-500 transition-colors"
            />
          </div>

          <div>
            <label class="block text-xs font-medium text-slate-300 mb-1.5">Slug URL *</label>
            <input
              v-model="modalForm.slug"
              type="text"
              required
              placeholder="e.g. apple, asus"
              class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-xs font-mono text-slate-100 placeholder-slate-500 focus:outline-none focus:border-blue-500 transition-colors"
            />
          </div>

          <div>
            <label class="block text-xs font-medium text-slate-300 mb-1.5">Quốc gia xuất xứ</label>
            <input
              v-model="modalForm.originCountry"
              type="text"
              placeholder="e.g. Hoa Kỳ, Đài Loan, Nhật Bản"
              class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-xs text-slate-100 placeholder-slate-500 focus:outline-none focus:border-blue-500 transition-colors"
            />
          </div>

          <div>
            <label class="block text-xs font-medium text-slate-300 mb-1.5">URL Logo</label>
            <input
              v-model="modalForm.logoUrl"
              type="url"
              placeholder="https://example.com/logo.png"
              class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-xs text-slate-100 placeholder-slate-500 focus:outline-none focus:border-blue-500 transition-colors"
            />
          </div>

          <div class="pt-3 border-t border-slate-800/80 flex items-center justify-end gap-3">
            <button
              type="button"
              @click="isModalOpen = false"
              class="px-4 py-2 rounded-xl text-xs font-semibold text-slate-300 hover:bg-slate-800 transition-colors"
            >
              Hủy
            </button>
            <button
              type="submit"
              :disabled="isSubmitting"
              class="px-4 py-2 rounded-xl bg-blue-600 hover:bg-blue-500 text-white text-xs font-semibold shadow-md shadow-blue-600/30 flex items-center gap-2 transition-all disabled:opacity-50"
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
  if (!modalForm.name || !modalForm.slug) return
  isSubmitting.value = true
  try {
    if (editingId.value) {
      await updateBrand(editingId.value, modalForm)
    } else {
      await createBrand(modalForm)
    }
    isModalOpen.value = false
    await loadBrands()
  } catch (err: any) {
    alert(err.response?.data?.message || 'Có lỗi xảy ra khi lưu thương hiệu.')
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
  isDeleting.value = true
  try {
    await deleteBrand(brandToDelete.value.id)
    isConfirmOpen.value = false
    brandToDelete.value = null
    await loadBrands()
  } catch (err: any) {
    alert(err.response?.data?.message || 'Không thể xóa thương hiệu.')
  } finally {
    isDeleting.value = false
  }
}

onMounted(() => {
  loadBrands()
})
</script>
