<template>
  <div class="space-y-6">
    <!-- Top Bar -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-5 border-b border-zinc-200">
      <div>
        <div class="flex items-center gap-2">
          <h1 class="text-xl font-semibold tracking-tight text-zinc-900">Quản lý Người dùng</h1>
          <span class="px-2 py-0.5 rounded-full text-[10px] font-mono font-medium bg-zinc-100 text-zinc-700 border border-zinc-200">
            {{ totalElements }} tài khoản
          </span>
        </div>
        <p class="text-xs text-zinc-500 mt-1 font-mono">Theo dõi tài khoản khách hàng, phân quyền vai trò và khóa/mở khóa truy cập</p>
      </div>

      <button
        @click="loadUsers"
        class="px-3.5 py-2 rounded-lg bg-white hover:bg-zinc-50 text-zinc-700 hover:text-zinc-900 text-xs font-medium border border-zinc-200 shadow-xs flex items-center gap-2 transition-all self-start sm:self-auto font-mono"
      >
        <RefreshCw class="w-3.5 h-3.5" :class="{ 'animate-spin': isLoading }" />
        <span>Làm mới</span>
      </button>
    </div>

    <!-- Filters -->
    <div class="bg-white border border-zinc-200 rounded-xl p-2.5 flex flex-wrap items-center gap-2.5 shadow-xs">
      <div class="relative flex-1 min-w-[220px]">
        <Search class="w-3.5 h-3.5 text-zinc-400 absolute left-3 top-1/2 -translate-y-1/2" />
        <input
          v-model="keyword"
          type="text"
          placeholder="Tìm theo họ tên, email, số điện thoại..."
          @keyup.enter="handleSearch"
          class="w-full h-9 pl-9 pr-3 rounded-lg bg-zinc-50/70 focus:bg-white border border-zinc-200 text-xs text-zinc-900 placeholder:text-zinc-400 focus:outline-none focus:border-zinc-900 transition-colors"
        />
      </div>

      <div class="flex flex-wrap items-center gap-2">
        <CustomSelect
          v-model="selectedRole"
          :options="roleFilterOptions"
          @change="handleFilterChange"
        />

        <CustomSelect
          v-model="selectedStatus"
          :options="statusFilterOptions"
          align="right"
          @change="handleFilterChange"
        />

        <button
          type="button"
          @click="handleSearch"
          class="h-9 px-3.5 inline-flex items-center justify-center rounded-lg bg-zinc-900 hover:bg-zinc-800 border border-zinc-900 text-white text-xs font-medium shadow-xs transition-colors cursor-pointer shrink-0"
        >
          Tìm kiếm
        </button>
        <button
          type="button"
          @click="resetFilters"
          class="h-9 px-3 inline-flex items-center justify-center rounded-lg bg-white hover:bg-zinc-50 border border-zinc-200 text-zinc-700 hover:text-zinc-900 text-xs font-medium shadow-xs transition-colors cursor-pointer shrink-0"
        >
          Đặt lại
        </button>
      </div>
    </div>

    <!-- Data Table -->
    <div class="bg-white border border-zinc-200 rounded-xl shadow-sm">
      <div class="overflow-x-auto">
        <table class="w-full text-left text-xs text-zinc-700">
          <thead class="text-[11px] font-mono font-medium text-zinc-500 uppercase bg-zinc-50/80 border-b border-zinc-200">
            <tr>
              <th class="py-3 px-5">Người dùng</th>
              <th class="py-3 px-4">Số điện thoại</th>
              <th class="py-3 px-4">Xác thực</th>
              <th class="py-3 px-4">Vai trò</th>
              <th class="py-3 px-4">Trạng thái</th>
              <th class="py-3 px-4">Ngày tham gia</th>
              <th class="py-3 px-5 text-right">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-zinc-100">
            <tr v-if="isLoading">
              <td colspan="7" class="py-12 text-center text-zinc-500">
                <div class="flex items-center justify-center gap-2">
                  <Loader2 class="w-4 h-4 animate-spin text-zinc-500" />
                  <span>Đang tải danh sách người dùng...</span>
                </div>
              </td>
            </tr>
            <tr v-else-if="users.length === 0">
              <td colspan="7" class="py-12 text-center text-zinc-400 text-xs">
                Không tìm thấy tài khoản nào phù hợp.
              </td>
            </tr>
            <tr
              v-for="user in users"
              :key="user.id"
              class="hover:bg-zinc-50/70 transition-colors"
            >
              <td class="py-3 px-5">
                <div class="flex items-center gap-3">
                  <img
                    v-if="user.avatarUrl"
                    :src="user.avatarUrl"
                    :alt="user.fullName"
                    class="w-8 h-8 rounded-lg object-cover border border-zinc-200 shrink-0"
                  />
                  <div
                    v-else
                    class="w-8 h-8 rounded-lg bg-zinc-100 border border-zinc-200 text-zinc-700 flex items-center justify-center text-xs font-semibold shrink-0"
                  >
                    {{ getInitial(user.fullName) }}
                  </div>
                  <div class="min-w-0">
                    <div class="font-medium text-zinc-900 truncate flex items-center gap-1.5">
                      <span>{{ user.fullName }}</span>
                      <span
                        v-if="isSelf(user.id)"
                        class="px-1.5 py-0.5 rounded text-[10px] font-mono bg-zinc-100 text-zinc-600 border border-zinc-200"
                      >
                        Bạn
                      </span>
                    </div>
                    <div class="text-[11px] font-mono text-zinc-500 truncate">{{ user.email }}</div>
                  </div>
                </div>
              </td>
              <td class="py-3 px-4 font-mono text-zinc-600">
                {{ user.phone || user.phoneNumber || '—' }}
              </td>
              <td class="py-3 px-4 font-mono">
                <span class="px-2 py-0.5 rounded bg-zinc-100 border border-zinc-200 text-zinc-700 text-[11px]">
                  {{ user.authProvider || 'LOCAL' }}
                </span>
              </td>
              <td class="py-3 px-4">
                <CustomSelect
                  :model-value="user.role"
                  :options="roleOptions"
                  size="sm"
                  :disabled="isSelf(user.id) || updatingUserId === user.id"
                  @change="handleRoleChange(user, $event as Role)"
                />
              </td>
              <td class="py-3 px-4">
                <span
                  class="inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-medium border"
                  :class="getStatusBadgeClass(user.status)"
                >
                  {{ getStatusLabel(user.status) }}
                </span>
              </td>
              <td class="py-3 px-4 font-mono text-zinc-500 tabular-nums">
                {{ formatDate(user.createdAt) }}
              </td>
              <td class="py-3 px-5 text-right">
                <button
                  v-if="user.status !== 'BLOCKED'"
                  type="button"
                  :disabled="isSelf(user.id) || updatingUserId === user.id"
                  @click="askToggleBlock(user, 'BLOCKED')"
                  class="px-2.5 py-1 rounded-lg text-xs font-medium text-rose-600 hover:bg-rose-50 border border-rose-200 transition disabled:opacity-40 disabled:cursor-not-allowed"
                >
                  Khóa
                </button>
                <button
                  v-else
                  type="button"
                  :disabled="isSelf(user.id) || updatingUserId === user.id"
                  @click="askToggleBlock(user, 'ACTIVE')"
                  class="px-2.5 py-1 rounded-lg text-xs font-medium text-emerald-700 hover:bg-emerald-50 border border-emerald-200 transition disabled:opacity-40 disabled:cursor-not-allowed"
                >
                  Mở khóa
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div
        v-if="totalPages > 1"
        class="px-5 py-3 border-t border-zinc-200 bg-zinc-50/50 flex items-center justify-between text-xs font-mono text-zinc-600"
      >
        <span>Trang {{ currentPage + 1 }} / {{ totalPages }}</span>
        <div class="flex items-center gap-2">
          <button
            :disabled="currentPage <= 0 || isLoading"
            @click="changePage(currentPage - 1)"
            class="px-3 py-1 rounded-md bg-white border border-zinc-200 hover:bg-zinc-50 disabled:opacity-40"
          >
            Trước
          </button>
          <button
            :disabled="currentPage >= totalPages - 1 || isLoading"
            @click="changePage(currentPage + 1)"
            class="px-3 py-1 rounded-md bg-white border border-zinc-200 hover:bg-zinc-50 disabled:opacity-40"
          >
            Sau
          </button>
        </div>
      </div>
    </div>

    <ConfirmModal
      :is-open="confirmState.isOpen"
      :title="confirmState.title"
      :message="confirmState.message"
      :confirm-text="confirmState.confirmText"
      :is-danger="confirmState.targetStatus === 'BLOCKED'"
      :is-loading="updatingUserId !== null"
      @confirm="confirmToggleStatus"
      @cancel="confirmState.isOpen = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { RefreshCw, Search, Loader2 } from 'lucide-vue-next'
import { toast } from 'vue-sonner'
import { fetchAdminUsers, updateAdminUser } from '@/api/users'
import ConfirmModal from '@/components/common/ConfirmModal.vue'
import CustomSelect, { type CustomSelectOption } from '@/components/common/CustomSelect.vue'
import { useAdminAuthStore } from '@/stores/auth'
import type { Role, User, UserStatus } from '@/types'

const roleOptions: CustomSelectOption[] = [
  { value: 'USER', label: 'USER' },
  { value: 'STAFF', label: 'STAFF' },
  { value: 'ADMIN', label: 'ADMIN' }
]

const roleFilterOptions: CustomSelectOption[] = [
  { value: '', label: 'Tất cả vai trò' },
  ...roleOptions
]

const statusFilterOptions: CustomSelectOption[] = [
  { value: '', label: 'Tất cả trạng thái' },
  { value: 'ACTIVE', label: 'Hoạt động' },
  { value: 'BLOCKED', label: 'Đã khóa' },
  { value: 'INACTIVE', label: 'Chưa kích hoạt' }
]

const authStore = useAdminAuthStore()

const users = ref<User[]>([])
const isLoading = ref(false)
const updatingUserId = ref<number | null>(null)
const keyword = ref('')
const selectedRole = ref<Role | ''>('')
const selectedStatus = ref<UserStatus | ''>('')
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)

const confirmState = ref<{
  isOpen: boolean
  user: User | null
  targetStatus: UserStatus
  title: string
  message: string
  confirmText: string
}>({
  isOpen: false,
  user: null,
  targetStatus: 'BLOCKED',
  title: '',
  message: '',
  confirmText: 'Xác nhận'
})

function isSelf(userId: number) {
  return authStore.user?.id === userId
}

function getInitial(name?: string) {
  return (name || 'U').trim().charAt(0).toUpperCase()
}

function formatDate(iso?: string) {
  if (!iso) return '—'
  return new Date(iso).toLocaleDateString('vi-VN')
}

function getStatusLabel(status?: UserStatus) {
  if (status === 'BLOCKED') return 'Đã khóa'
  if (status === 'INACTIVE') return 'Chưa kích hoạt'
  return 'Hoạt động'
}

function getStatusBadgeClass(status?: UserStatus) {
  if (status === 'BLOCKED') return 'bg-rose-50 text-rose-700 border-rose-200'
  if (status === 'INACTIVE') return 'bg-amber-50 text-amber-700 border-amber-200'
  return 'bg-emerald-50 text-emerald-700 border-emerald-200'
}

async function loadUsers() {
  isLoading.value = true
  try {
    const res = await fetchAdminUsers({
      keyword: keyword.value,
      role: selectedRole.value,
      status: selectedStatus.value,
      page: currentPage.value,
      size: 10
    })
    users.value = res.content || []
    totalPages.value = res.totalPages || 0
    totalElements.value = res.totalElements || 0
  } catch (err: any) {
    toast.error(err?.message || 'Không thể tải danh sách người dùng')
  } finally {
    isLoading.value = false
  }
}

function handleSearch() {
  currentPage.value = 0
  loadUsers()
}

function handleFilterChange() {
  currentPage.value = 0
  loadUsers()
}

function resetFilters() {
  keyword.value = ''
  selectedRole.value = ''
  selectedStatus.value = ''
  currentPage.value = 0
  loadUsers()
}

function changePage(page: number) {
  currentPage.value = page
  loadUsers()
}

async function handleRoleChange(user: User, newRole: Role) {
  if (newRole === user.role) return
  updatingUserId.value = user.id
  try {
    const updated = await updateAdminUser(user.id, { role: newRole })
    const idx = users.value.findIndex(u => u.id === user.id)
    if (idx !== -1) users.value[idx] = updated
    toast.success(`Đã đổi vai trò của ${user.email} thành ${newRole}`)
  } catch (err: any) {
    toast.error(err?.message || 'Không thể cập nhật vai trò')
    await loadUsers()
  } finally {
    updatingUserId.value = null
  }
}

function askToggleBlock(user: User, targetStatus: UserStatus) {
  const isBlocking = targetStatus === 'BLOCKED'
  confirmState.value = {
    isOpen: true,
    user,
    targetStatus,
    title: isBlocking ? 'Khóa tài khoản người dùng?' : 'Mở khóa tài khoản người dùng?',
    message: isBlocking
      ? `Tài khoản ${user.email} sẽ bị chặn đăng nhập và không thể thao tác trên hệ thống.`
      : `Tài khoản ${user.email} sẽ được kích hoạt trở lại.`,
    confirmText: isBlocking ? 'Khóa tài khoản' : 'Mở khóa'
  }
}

async function confirmToggleStatus() {
  const target = confirmState.value.user
  if (!target) return
  updatingUserId.value = target.id
  try {
    const updated = await updateAdminUser(target.id, { status: confirmState.value.targetStatus })
    const idx = users.value.findIndex(u => u.id === target.id)
    if (idx !== -1) users.value[idx] = updated
    toast.success(
      confirmState.value.targetStatus === 'BLOCKED'
        ? `Đã khóa tài khoản ${target.email}`
        : `Đã mở khóa tài khoản ${target.email}`
    )
    confirmState.value.isOpen = false
  } catch (err: any) {
    toast.error(err?.message || 'Không thể cập nhật trạng thái tài khoản')
  } finally {
    updatingUserId.value = null
  }
}

onMounted(loadUsers)
</script>
