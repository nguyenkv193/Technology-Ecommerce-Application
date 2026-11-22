<template>
  <div class="space-y-6">
    <!-- Top Action Bar -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-5 border-b border-zinc-200">
      <div>
        <div class="flex items-center gap-2">
          <h1 class="text-xl font-semibold tracking-tight text-zinc-900">Quản lý Banner & Slider</h1>
          <span class="px-2 py-0.5 rounded-full text-[10px] font-mono font-medium bg-zinc-100 text-zinc-700 border border-zinc-200">
            {{ filteredBanners.length }} banner
          </span>
        </div>
        <p class="text-xs text-zinc-500 mt-1 font-mono">Quản trị các hình ảnh banner quảng cáo trang chủ, hero slider và sự kiện khuyến mãi</p>
      </div>

      <div class="flex items-center gap-2.5">
        <!-- Filter by Position -->
        <select
          v-model="selectedPosition"
          class="h-9 px-3 text-xs bg-white border border-zinc-200 rounded-lg text-zinc-700 focus:outline-none focus:ring-1 focus:ring-zinc-900 font-sans"
        >
          <option value="ALL">Tất cả vị trí</option>
          <option value="HERO_SLIDER">Hero Slider (Chính giữa)</option>
          <option value="SIDE_PROMO">Side Promo (Góc phải)</option>
          <option value="BOTTOM_CARD">Bottom Card (Dưới cùng)</option>
          <option value="POPUP">Popup khuyến mãi</option>
        </select>

        <button
          type="button"
          @click="openAddModal"
          class="inline-flex items-center gap-2 px-3.5 py-2 rounded-lg bg-zinc-900 hover:bg-zinc-800 text-white text-xs font-medium shadow-sm transition-all self-start sm:self-auto cursor-pointer"
        >
          <Plus class="w-3.5 h-3.5" />
          <span>Thêm Banner</span>
        </button>
      </div>
    </div>

    <!-- Data Table -->
    <div class="bg-white border border-zinc-200 rounded-xl overflow-hidden shadow-sm">
      <div class="overflow-x-auto">
        <table class="w-full text-left text-xs text-zinc-700">
          <thead class="text-[11px] font-mono font-medium text-zinc-500 uppercase bg-zinc-50/80 border-b border-zinc-200">
            <tr>
              <th class="py-3 px-5 w-32">Ảnh Banner</th>
              <th class="py-3 px-4">Tiêu đề & Thông tin</th>
              <th class="py-3 px-4">Vị trí</th>
              <th class="py-3 px-4 text-center">Thứ tự</th>
              <th class="py-3 px-4 text-center">Lượt click</th>
              <th class="py-3 px-4 text-center">Trạng thái</th>
              <th class="py-3 px-5 text-right">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-zinc-100 font-mono">
            <tr v-if="isLoading">
              <td colspan="7" class="py-12 text-center text-zinc-500 font-sans">
                <div class="flex items-center justify-center gap-2">
                  <Loader2 class="w-4 h-4 animate-spin text-zinc-500" />
                  <span>Đang tải danh sách banner...</span>
                </div>
              </td>
            </tr>
            <tr v-else-if="filteredBanners.length === 0">
              <td colspan="7" class="py-12 text-center text-zinc-400 text-xs font-sans">
                Chưa có banner nào ở vị trí này.
              </td>
            </tr>
            <tr
              v-for="b in filteredBanners"
              :key="b.id"
              class="hover:bg-zinc-50/70 transition-colors group"
            >
              <!-- Thumbnail Preview -->
              <td class="py-3 px-5">
                <div class="w-28 h-14 rounded-lg bg-zinc-100 border border-zinc-200 overflow-hidden relative group/img">
                  <img
                    :src="b.imageUrl"
                    :alt="b.title"
                    class="w-full h-full object-cover"
                  />
                  <a
                    :href="b.imageUrl"
                    target="_blank"
                    class="absolute inset-0 bg-black/40 text-white flex items-center justify-center opacity-0 group-hover/img:opacity-100 transition-opacity text-[10px]"
                  >
                    Xem ảnh
                  </a>
                </div>
              </td>

              <!-- Title & Link -->
              <td class="py-3 px-4 max-w-xs font-sans">
                <div class="font-semibold text-zinc-900 truncate" :title="b.title">
                  {{ b.title }}
                </div>
                <div class="text-[11px] font-mono text-blue-600 truncate mt-0.5" :title="b.linkUrl">
                  {{ b.linkUrl }}
                </div>
              </td>

              <!-- Position -->
              <td class="py-3 px-4">
                <span
                  class="px-2 py-0.5 rounded-full text-[10px] font-semibold tracking-wide"
                  :class="getPositionBadgeClass(b.position)"
                >
                  {{ formatPosition(b.position) }}
                </span>
              </td>

              <!-- Display Order -->
              <td class="py-3 px-4 text-center font-mono font-bold text-zinc-800">
                #{{ b.displayOrder }}
              </td>

              <!-- Click Count -->
              <td class="py-3 px-4 text-center font-mono text-zinc-600">
                {{ b.clickCount || 0 }}
              </td>

              <!-- Status Toggle -->
              <td class="py-3 px-4 text-center">
                <button
                  type="button"
                  @click="handleToggleStatus(b)"
                  class="px-2 py-0.5 rounded-full text-[10px] font-bold cursor-pointer transition-all"
                  :class="b.status === 'ACTIVE' ? 'bg-emerald-50 text-emerald-700 border border-emerald-200 hover:bg-emerald-100' : 'bg-zinc-100 text-zinc-500 border border-zinc-200 hover:bg-zinc-200'"
                >
                  {{ b.status === 'ACTIVE' ? 'Hoạt động' : 'Tạm ẩn' }}
                </button>
              </td>

              <!-- Actions -->
              <td class="py-3 px-5 text-right font-sans">
                <div class="inline-flex items-center gap-1.5 opacity-90 group-hover:opacity-100">
                  <button
                    type="button"
                    @click="openEditModal(b)"
                    class="p-1.5 rounded-lg hover:bg-zinc-100 text-zinc-500 hover:text-zinc-900 transition-colors cursor-pointer"
                    title="Chỉnh sửa"
                  >
                    <Edit3 class="w-4 h-4" />
                  </button>
                  <button
                    type="button"
                    @click="handleDelete(b)"
                    class="p-1.5 rounded-lg hover:bg-red-50 text-zinc-400 hover:text-red-600 transition-colors cursor-pointer"
                    title="Xóa banner"
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

    <!-- Modal Add/Edit Banner -->
    <div
      v-if="showModal"
      class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs"
    >
      <div class="bg-white rounded-2xl border border-zinc-200 max-w-lg w-full p-6 shadow-2xl space-y-5 animate-in fade-in zoom-in-95 duration-150">
        <div class="flex items-center justify-between pb-3 border-b border-zinc-100">
          <h3 class="text-base font-semibold text-zinc-900">
            {{ isEditing ? 'Cập nhật Banner' : 'Thêm Banner Mới' }}
          </h3>
          <button @click="closeModal" class="p-1 text-zinc-400 hover:text-zinc-700 rounded-lg">
            <X class="w-4 h-4" />
          </button>
        </div>

        <form @submit.prevent="saveBanner" class="space-y-4">
          <div>
            <label class="block text-xs font-semibold text-zinc-700 mb-1">Tiêu đề Banner *</label>
            <input
              v-model="form.title"
              type="text"
              required
              placeholder="VD: Siêu Phẩm MacBook Pro M3 Pro - Trợ Giá 3 Triệu"
              class="w-full px-3 py-2 text-xs bg-zinc-50 border border-zinc-200 rounded-lg focus:bg-white focus:outline-none focus:ring-1 focus:ring-zinc-900"
            />
          </div>

          <div>
            <label class="block text-xs font-semibold text-zinc-700 mb-1">URL Ảnh Banner (Desktop) *</label>
            <input
              v-model="form.imageUrl"
              type="url"
              required
              placeholder="https://images.unsplash.com/..."
              class="w-full px-3 py-2 text-xs bg-zinc-50 border border-zinc-200 rounded-lg focus:bg-white focus:outline-none focus:ring-1 focus:ring-zinc-900 font-mono"
            />
            <!-- Image live preview -->
            <div v-if="form.imageUrl" class="mt-2 h-28 rounded-lg border border-zinc-200 overflow-hidden bg-zinc-100">
              <img :src="form.imageUrl" alt="Preview" class="w-full h-full object-cover" />
            </div>
          </div>

          <div>
            <label class="block text-xs font-semibold text-zinc-700 mb-1">Đường dẫn khi Click (Link URL) *</label>
            <input
              v-model="form.linkUrl"
              type="text"
              required
              placeholder="/products/3 hoặc https://..."
              class="w-full px-3 py-2 text-xs bg-zinc-50 border border-zinc-200 rounded-lg focus:bg-white focus:outline-none focus:ring-1 focus:ring-zinc-900 font-mono"
            />
          </div>

          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="block text-xs font-semibold text-zinc-700 mb-1">Vị trí hiển thị</label>
              <select
                v-model="form.position"
                class="w-full px-3 py-2 text-xs bg-zinc-50 border border-zinc-200 rounded-lg focus:bg-white focus:outline-none focus:ring-1 focus:ring-zinc-900"
              >
                <option value="HERO_SLIDER">Hero Slider (Chính giữa)</option>
                <option value="SIDE_PROMO">Side Promo (Góc phải)</option>
                <option value="BOTTOM_CARD">Bottom Card (Dưới cùng)</option>
                <option value="POPUP">Popup</option>
              </select>
            </div>

            <div>
              <label class="block text-xs font-semibold text-zinc-700 mb-1">Thứ tự hiển thị</label>
              <input
                v-model.number="form.displayOrder"
                type="number"
                min="0"
                class="w-full px-3 py-2 text-xs bg-zinc-50 border border-zinc-200 rounded-lg focus:bg-white focus:outline-none focus:ring-1 focus:ring-zinc-900 font-mono"
              />
            </div>
          </div>

          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="block text-xs font-semibold text-zinc-700 mb-1">Trạng thái</label>
              <select
                v-model="form.status"
                class="w-full px-3 py-2 text-xs bg-zinc-50 border border-zinc-200 rounded-lg focus:bg-white focus:outline-none focus:ring-1 focus:ring-zinc-900"
              >
                <option value="ACTIVE">Hoạt động (Hiển thị ngay)</option>
                <option value="INACTIVE">Tạm ẩn</option>
              </select>
            </div>

            <div>
              <label class="block text-xs font-semibold text-zinc-700 mb-1">Mở liên kết</label>
              <select
                v-model="form.targetType"
                class="w-full px-3 py-2 text-xs bg-zinc-50 border border-zinc-200 rounded-lg focus:bg-white focus:outline-none focus:ring-1 focus:ring-zinc-900"
              >
                <option value="_self">Cùng tab (_self)</option>
                <option value="_blank">Tab mới (_blank)</option>
              </select>
            </div>
          </div>

          <div class="flex items-center justify-end gap-2.5 pt-4 border-t border-zinc-100">
            <button
              type="button"
              @click="closeModal"
              class="px-4 py-2 rounded-lg border border-zinc-200 text-xs font-medium text-zinc-600 hover:bg-zinc-50"
            >
              Hủy bỏ
            </button>
            <button
              type="submit"
              :disabled="isSubmitting"
              class="px-4 py-2 rounded-lg bg-zinc-900 hover:bg-zinc-800 text-white text-xs font-medium shadow-sm transition-all disabled:opacity-50"
            >
              {{ isSubmitting ? 'Đang lưu...' : (isEditing ? 'Lưu thay đổi' : 'Tạo Banner') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Plus, Edit3, Trash2, Loader2, X } from 'lucide-vue-next'
import type { Banner } from '@/types'
import { fetchBanners, createBanner, updateBanner, deleteBanner, toggleBannerStatus } from '@/api/banners'

const banners = ref<Banner[]>([])
const isLoading = ref<boolean>(true)
const selectedPosition = ref<string>('ALL')

const showModal = ref<boolean>(false)
const isEditing = ref<boolean>(false)
const isSubmitting = ref<boolean>(false)
const currentBannerId = ref<number | null>(null)

const form = ref({
  title: '',
  imageUrl: '',
  linkUrl: '',
  targetType: '_self',
  position: 'HERO_SLIDER',
  displayOrder: 1,
  status: 'ACTIVE'
})

const filteredBanners = computed(() => {
  if (selectedPosition.value === 'ALL') return banners.value
  return banners.value.filter(b => b.position === selectedPosition.value)
})

const loadBanners = async () => {
  isLoading.value = true
  try {
    banners.value = await fetchBanners()
  } catch (err) {
    console.error('Không thể tải banner:', err)
  } finally {
    isLoading.value = false
  }
}

const openAddModal = () => {
  isEditing.value = false
  currentBannerId.value = null
  form.value = {
    title: '',
    imageUrl: '',
    linkUrl: '',
    targetType: '_self',
    position: selectedPosition.value !== 'ALL' ? selectedPosition.value : 'HERO_SLIDER',
    displayOrder: banners.value.length + 1,
    status: 'ACTIVE'
  }
  showModal.value = true
}

const openEditModal = (b: Banner) => {
  isEditing.value = true
  currentBannerId.value = b.id
  form.value = {
    title: b.title,
    imageUrl: b.imageUrl,
    linkUrl: b.linkUrl,
    targetType: b.targetType || '_self',
    position: b.position,
    displayOrder: b.displayOrder,
    status: b.status
  }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}

const saveBanner = async () => {
  isSubmitting.value = true
  try {
    if (isEditing.value && currentBannerId.value) {
      await updateBanner(currentBannerId.value, form.value)
    } else {
      await createBanner(form.value)
    }
    closeModal()
    await loadBanners()
  } catch (err) {
    console.error('Lỗi khi lưu banner:', err)
    alert('Có lỗi xảy ra khi lưu banner. Vui lòng thử lại.')
  } finally {
    isSubmitting.value = false
  }
}

const handleToggleStatus = async (b: Banner) => {
  try {
    await toggleBannerStatus(b.id)
    b.status = b.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
  } catch (err) {
    console.error('Lỗi chuyển trạng thái:', err)
  }
}

const handleDelete = async (b: Banner) => {
  if (!confirm(`Bạn có chắc muốn xóa banner "${b.title}"?`)) return
  try {
    await deleteBanner(b.id)
    banners.value = banners.value.filter(item => item.id !== b.id)
  } catch (err) {
    console.error('Lỗi khi xóa banner:', err)
    alert('Không thể xóa banner này.')
  }
}

const formatPosition = (pos: string) => {
  switch (pos) {
    case 'HERO_SLIDER': return 'Hero Slider'
    case 'SIDE_PROMO': return 'Side Promo'
    case 'BOTTOM_CARD': return 'Bottom Card'
    case 'POPUP': return 'Popup'
    default: return pos
  }
}

const getPositionBadgeClass = (pos: string) => {
  switch (pos) {
    case 'HERO_SLIDER': return 'bg-violet-50 text-violet-700 border border-violet-200'
    case 'SIDE_PROMO': return 'bg-blue-50 text-blue-700 border border-blue-200'
    case 'BOTTOM_CARD': return 'bg-amber-50 text-amber-700 border border-amber-200'
    default: return 'bg-zinc-100 text-zinc-700 border border-zinc-200'
  }
}

onMounted(() => {
  void loadBanners()
})
</script>
