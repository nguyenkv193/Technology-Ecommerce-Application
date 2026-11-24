<template>
  <aside class="w-full md:w-72 shrink-0 bg-white border border-zinc-200/80 rounded-2xl shadow-xs overflow-hidden md:sticky md:top-20">
    <div class="p-6 flex flex-col items-center text-center border-b border-zinc-100">
      <div class="relative">
        <img
          v-if="authStore.user?.avatarUrl"
          :src="authStore.user.avatarUrl"
          alt="Ảnh đại diện"
          data-testid="profile-avatar-img"
          class="w-24 h-24 rounded-full object-cover ring-4 ring-zinc-100"
        />
        <div
          v-else
          data-testid="profile-avatar-initial"
          class="w-24 h-24 rounded-full bg-zinc-900 text-white text-3xl font-bold flex items-center justify-center ring-4 ring-zinc-100"
        >
          {{ initial }}
        </div>
        <button
          type="button"
          data-testid="avatar-trigger"
          :disabled="uploadingAvatar"
          @click="avatarInput?.click()"
          class="absolute bottom-0 right-0 w-8 h-8 rounded-full bg-white border border-zinc-200 shadow flex items-center justify-center text-zinc-700 hover:bg-zinc-100 transition cursor-pointer disabled:opacity-50"
          title="Đổi ảnh đại diện"
        >
          <Camera class="w-4 h-4" />
        </button>
        <div v-if="uploadingAvatar" class="absolute inset-0 rounded-full bg-black/40 flex items-center justify-center text-white text-[10px] font-medium">
          Đang tải...
        </div>
        <input ref="avatarInput" type="file" accept="image/*" class="hidden" data-testid="avatar-input" @change="handleAvatarChange" />
      </div>
      <div class="mt-4 font-semibold text-zinc-900 text-sm truncate max-w-full">{{ authStore.user?.fullName || 'Khách hàng' }}</div>
      <div class="text-[11px] text-zinc-400 truncate max-w-full">{{ authStore.user?.email }}</div>
      <span v-if="authStore.user?.role" class="mt-2 px-2 py-0.5 rounded-full bg-zinc-100 text-zinc-600 text-[10px] font-medium">{{ authStore.user.role }}</span>
      <button
        v-if="authStore.user?.avatarUrl"
        type="button"
        data-testid="avatar-remove"
        :disabled="uploadingAvatar"
        @click="handleRemoveAvatar"
        class="mt-2 text-[11px] text-red-600 hover:underline cursor-pointer disabled:opacity-50"
      >
        Xóa ảnh đại diện
      </button>
    </div>

    <nav class="p-2 space-y-1" role="tablist">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        type="button"
        role="tab"
        :data-testid="`tab-${tab.key}`"
        :aria-selected="activeTab === tab.key"
        @click="$emit('selectTab', tab.key)"
        :class="[
          'w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-xs font-medium transition cursor-pointer',
          activeTab === tab.key ? 'bg-zinc-900 text-white' : 'text-zinc-600 hover:bg-zinc-100 hover:text-zinc-900'
        ]"
      >
        <component :is="tab.icon" class="w-4 h-4" />
        <span class="flex-1 text-left">{{ tab.label }}</span>
        <span v-if="tab.key === 'addresses' && addressCount !== undefined" class="text-[10px] opacity-70">{{ addressCount }}</span>
        <span v-if="tab.key === 'orders' && orderCount !== undefined" class="text-[10px] opacity-70">{{ orderCount }}</span>
      </button>
    </nav>
  </aside>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { User as UserIcon, Lock, MapPin, Package, Camera } from 'lucide-vue-next'
import { toast } from 'vue-sonner'
import { useAuthStore } from '@/stores/auth'

export type AccountTabKey = 'profile' | 'password' | 'addresses' | 'orders'

defineProps<{
  activeTab: AccountTabKey
  addressCount?: number
  orderCount?: number
}>()

defineEmits<{
  (e: 'selectTab', tab: AccountTabKey): void
}>()

const AVATAR_SIZE = 256
const MAX_AVATAR_FILE_BYTES = 5 * 1024 * 1024

const authStore = useAuthStore()
const avatarInput = ref<HTMLInputElement | null>(null)
const uploadingAvatar = ref(false)

const initial = computed(() => authStore.user?.fullName?.trim().charAt(0).toUpperCase() || 'U')

const tabs: Array<{ key: AccountTabKey; label: string; icon: unknown }> = [
  { key: 'profile', label: 'Hồ sơ cá nhân', icon: UserIcon },
  { key: 'password', label: 'Đổi mật khẩu', icon: Lock },
  { key: 'addresses', label: 'Sổ địa chỉ', icon: MapPin },
  { key: 'orders', label: 'Lịch sử đơn hàng', icon: Package }
]

const resizeImage = (file: File): Promise<string> =>
  new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onerror = () => reject(new Error('Không đọc được file ảnh'))
    reader.onload = () => {
      const img = new Image()
      img.onerror = () => reject(new Error('File ảnh không hợp lệ'))
      img.onload = () => {
        const canvas = document.createElement('canvas')
        canvas.width = AVATAR_SIZE
        canvas.height = AVATAR_SIZE
        const ctx = canvas.getContext('2d')
        if (!ctx) return reject(new Error('Trình duyệt không hỗ trợ xử lý ảnh'))
        const side = Math.min(img.width, img.height)
        ctx.drawImage(img, (img.width - side) / 2, (img.height - side) / 2, side, side, 0, 0, AVATAR_SIZE, AVATAR_SIZE)
        resolve(canvas.toDataURL('image/jpeg', 0.85))
      }
      img.src = reader.result as string
    }
    reader.readAsDataURL(file)
  })

const saveAvatar = async (avatarUrl: string, successMsg: string) => {
  uploadingAvatar.value = true
  try {
    await authStore.updateProfile({
      fullName: authStore.user?.fullName || '',
      phone: authStore.user?.phone || undefined,
      avatarUrl
    })
    toast.success(successMsg)
  } catch (e: any) {
    toast.error(e?.message || 'Không thể cập nhật ảnh đại diện')
  } finally {
    uploadingAvatar.value = false
  }
}

const handleAvatarChange = async (event: Event) => {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  input.value = ''
  if (!file) return
  if (!file.type.startsWith('image/')) {
    toast.error('Vui lòng chọn file ảnh')
    return
  }
  if (file.size > MAX_AVATAR_FILE_BYTES) {
    toast.error('Ảnh tối đa 5MB')
    return
  }
  try {
    const dataUrl = await resizeImage(file)
    await saveAvatar(dataUrl, 'Đã cập nhật ảnh đại diện')
  } catch (e: any) {
    toast.error(e?.message || 'Không thể xử lý ảnh')
  }
}

const handleRemoveAvatar = () => saveAvatar('', 'Đã xóa ảnh đại diện')
</script>
