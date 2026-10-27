<template>
  <div class="space-y-6">
    <!-- Top Action Bar -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold tracking-tight text-slate-100">Quản lý Danh mục</h1>
        <p class="text-xs text-slate-400 mt-1">Cấu trúc cây danh mục ngành hàng sản phẩm</p>
      </div>
      <button
        type="button"
        @click="openAddModal"
        class="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-500 text-white text-xs font-semibold shadow-md shadow-blue-600/30 transition-all"
      >
        <Plus class="w-4 h-4" />
        <span>Thêm danh mục</span>
      </button>
    </div>

    <!-- Data Table -->
    <div class="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-sm">
      <div class="overflow-x-auto">
        <table class="w-full text-left text-sm text-slate-300">
          <thead class="text-xs font-semibold text-slate-400 uppercase bg-slate-950/60 border-b border-slate-800">
            <tr>
              <th class="py-3.5 px-6">Tên danh mục</th>
              <th class="py-3.5 px-4 font-mono">Slug URL</th>
              <th class="py-3.5 px-4">Mô tả</th>
              <th class="py-3.5 px-4 text-center">Số sản phẩm</th>
              <th class="py-3.5 px-6 text-right">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-800/60">
            <tr v-if="isLoading">
              <td colspan="5" class="py-12 text-center text-slate-500">
                <div class="flex items-center justify-center gap-2">
                  <Loader2 class="w-5 h-5 animate-spin text-blue-500" />
                  <span>Đang tải danh mục...</span>
                </div>
              </td>
            </tr>
            <tr v-else-if="categories.length === 0">
              <td colspan="5" class="py-12 text-center text-slate-500 text-xs">
                Chưa có danh mục nào.
              </td>
            </tr>
            <tr
              v-for="cat in categories"
              :key="cat.id"
              class="hover:bg-slate-800/40 transition-colors"
            >
              <td class="py-3.5 px-6 font-semibold text-slate-100 flex items-center gap-3">
                <div class="w-8 h-8 rounded-lg bg-blue-500/10 text-blue-400 flex items-center justify-center">
                  <Folder class="w-4 h-4" />
                </div>
                <span>{{ cat.name }}</span>
              </td>
              <td class="py-3.5 px-4 font-mono text-xs text-slate-400">
                {{ cat.slug }}
              </td>
              <td class="py-3.5 px-4 text-xs text-slate-400 max-w-sm truncate">
                {{ cat.description || '—' }}
              </td>
              <td class="py-3.5 px-4 text-center">
                <span class="px-2.5 py-0.5 rounded-full text-xs font-medium bg-slate-800 text-slate-300">
                  {{ cat.productCount ?? 0 }}
                </span>
              </td>
              <td class="py-3.5 px-6 text-right">
                <div class="flex items-center justify-end gap-1">
                  <button
                    @click="openEditModal(cat)"
                    class="p-2 rounded-lg text-slate-400 hover:text-blue-400 hover:bg-slate-800 transition-colors"
                    title="Chỉnh sửa"
                  >
                    <Edit3 class="w-4 h-4" />
                  </button>
                  <button
                    @click="askDelete(cat)"
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

    <!-- Category Form Modal (Add / Edit) -->
    <div
      v-if="isModalOpen"
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm"
      @click.self="isModalOpen = false"
    >
      <div class="w-full max-w-md bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-2xl space-y-5">
        <h3 class="text-base font-semibold text-slate-100">
          {{ editingId ? 'Cập nhật danh mục' : 'Thêm danh mục mới' }}
        </h3>

        <form @submit.prevent="submitCategory" class="space-y-4">
          <div>
            <label class="block text-xs font-medium text-slate-300 mb-1.5">Tên danh mục *</label>
            <input
              v-model="modalForm.name"
              @input="autoGenerateSlug"
              type="text"
              required
              placeholder="e.g. Điện thoại & Phụ kiện"
              class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-xs text-slate-100 placeholder-slate-500 focus:outline-none focus:border-blue-500 transition-colors"
            />
          </div>

          <div>
            <label class="block text-xs font-medium text-slate-300 mb-1.5">Slug URL *</label>
            <input
              v-model="modalForm.slug"
              type="text"
              required
              placeholder="e.g. dien-thoai-phu-kien"
              class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-xs font-mono text-slate-100 placeholder-slate-500 focus:outline-none focus:border-blue-500 transition-colors"
            />
          </div>

          <div>
            <label class="block text-xs font-medium text-slate-300 mb-1.5">Mô tả</label>
            <textarea
              v-model="modalForm.description"
              rows="3"
              placeholder="Mô tả ngành hàng..."
              class="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-xs text-slate-100 placeholder-slate-500 focus:outline-none focus:border-blue-500 transition-colors"
            ></textarea>
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
              <span>{{ isSubmitting ? 'Đang lưu...' : 'Lưu danh mục' }}</span>
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Confirm Delete Modal -->
    <ConfirmModal
      :is-open="isConfirmOpen"
      title="Xóa danh mục"
      :message="`Bạn có chắc chắn muốn xóa danh mục '${categoryToDelete?.name}'? Các sản phẩm thuộc danh mục này có thể bị ảnh hưởng.`"
      confirm-text="Xóa danh mục"
      :is-loading="isDeleting"
      @confirm="executeDelete"
      @cancel="isConfirmOpen = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus, Edit3, Trash2, Folder, Loader2 } from 'lucide-vue-next'
import ConfirmModal from '@/components/common/ConfirmModal.vue'
import { fetchCategories, createCategory, updateCategory, deleteCategory } from '@/api/categories'
import type { Category } from '@/types'

const categories = ref<Category[]>([])
const isLoading = ref(false)

const isModalOpen = ref(false)
const isSubmitting = ref(false)
const editingId = ref<number | null>(null)

const modalForm = reactive({
  name: '',
  slug: '',
  description: ''
})

const isConfirmOpen = ref(false)
const isDeleting = ref(false)
const categoryToDelete = ref<Category | null>(null)

async function loadCategories() {
  isLoading.value = true
  try {
    categories.value = await fetchCategories()
  } finally {
    isLoading.value = false
  }
}

function openAddModal() {
  editingId.value = null
  modalForm.name = ''
  modalForm.slug = ''
  modalForm.description = ''
  isModalOpen.value = true
}

function openEditModal(c: Category) {
  editingId.value = c.id
  modalForm.name = c.name
  modalForm.slug = c.slug
  modalForm.description = c.description || ''
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

async function submitCategory() {
  if (!modalForm.name || !modalForm.slug) return
  isSubmitting.value = true
  try {
    if (editingId.value) {
      await updateCategory(editingId.value, modalForm)
    } else {
      await createCategory(modalForm)
    }
    isModalOpen.value = false
    await loadCategories()
  } catch (err: any) {
    alert(err.response?.data?.message || 'Có lỗi xảy ra khi lưu danh mục.')
  } finally {
    isSubmitting.value = false
  }
}

function askDelete(c: Category) {
  categoryToDelete.value = c
  isConfirmOpen.value = true
}

async function executeDelete() {
  if (!categoryToDelete.value) return
  isDeleting.value = true
  try {
    await deleteCategory(categoryToDelete.value.id)
    isConfirmOpen.value = false
    categoryToDelete.value = null
    await loadCategories()
  } catch (err: any) {
    alert(err.response?.data?.message || 'Không thể xóa danh mục.')
  } finally {
    isDeleting.value = false
  }
}

onMounted(() => {
  loadCategories()
})
</script>
