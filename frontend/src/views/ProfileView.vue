<template>
  <div class="space-y-8">
    <div class="pb-4 border-b border-zinc-200/80">
      <h1 class="text-lg sm:text-xl font-bold text-zinc-900 tracking-tight">Thông Tin Cá Nhân</h1>
      <p class="text-xs text-zinc-500 mt-1">Quản lý hồ sơ, mật khẩu và sổ địa chỉ giao hàng của bạn</p>
    </div>

    <div v-if="!authStore.isAuthenticated" class="text-center py-16 bg-white rounded-2xl border border-zinc-200/80 p-8 space-y-4">
      <p class="text-sm text-zinc-600">Vui lòng đăng nhập để xem thông tin cá nhân.</p>
      <button type="button" data-testid="profile-login" @click="authStore.openLoginModal()" class="px-5 py-2.5 rounded-xl text-xs font-medium bg-zinc-900 text-white hover:bg-zinc-800 transition cursor-pointer">
        Đăng nhập
      </button>
    </div>

    <div v-else class="flex flex-col md:flex-row gap-6 items-start">
      <!-- LEFT: avatar + tabs -->
      <AccountSidebar
        :active-tab="activeTab"
        :address-count="addresses.length"
        @select-tab="handleTabSelect"
      />

      <!-- RIGHT: tab content -->
      <section class="flex-1 min-w-0 w-full space-y-4">
        <h2 class="text-base font-semibold text-zinc-900" data-testid="active-tab-title">{{ activeTabLabel }}</h2>

        <div :key="activeTab">
        <!-- Profile -->
        <form v-if="activeTab === 'profile'" data-testid="profile-form" @submit.prevent="handleUpdateProfile" class="bg-white border border-zinc-200/80 rounded-2xl p-5 sm:p-6 space-y-5 shadow-xs">
          <div class="grid grid-cols-2 gap-4 text-xs">
            <div>
              <div class="text-zinc-400">Vai trò</div>
              <div class="font-semibold text-zinc-900 mt-0.5">{{ authStore.user?.role }}</div>
            </div>
            <div>
              <div class="text-zinc-400">Ngày tham gia</div>
              <div class="font-semibold text-zinc-900 mt-0.5">{{ formatDate(authStore.user?.createdAt) }}</div>
            </div>
          </div>
          <div class="space-y-1.5">
            <label class="text-xs font-medium text-zinc-700">Email</label>
            <input :value="authStore.user?.email" disabled class="w-full rounded-xl border border-zinc-200 bg-zinc-100 text-zinc-500 text-xs px-3 py-2.5" />
          </div>
          <div class="grid sm:grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <label class="text-xs font-medium text-zinc-700" for="fullName">Họ và tên</label>
              <input id="fullName" v-model="profileForm.fullName" type="text" class="w-full rounded-xl border border-zinc-200 text-xs px-3 py-2.5 focus:outline-none focus:border-zinc-900" />
              <p v-if="profileErrors.fullName" class="text-[11px] text-red-600">{{ profileErrors.fullName }}</p>
            </div>
            <div class="space-y-1.5">
              <label class="text-xs font-medium text-zinc-700" for="phone">Số điện thoại</label>
              <input id="phone" v-model="profileForm.phone" type="tel" placeholder="0912345678" class="w-full rounded-xl border border-zinc-200 text-xs px-3 py-2.5 focus:outline-none focus:border-zinc-900" />
              <p v-if="profileErrors.phone" class="text-[11px] text-red-600">{{ profileErrors.phone }}</p>
            </div>
          </div>
          <button type="submit" :disabled="savingProfile" class="px-5 py-2.5 rounded-xl text-xs font-medium bg-zinc-900 text-white hover:bg-zinc-800 transition disabled:opacity-50 cursor-pointer">
            {{ savingProfile ? 'Đang lưu...' : 'Lưu thay đổi' }}
          </button>
        </form>

        <!-- Password -->
        <form v-else-if="activeTab === 'password'" data-testid="password-form" @submit.prevent="handleChangePassword" class="bg-white border border-zinc-200/80 rounded-2xl p-5 sm:p-6 space-y-5 shadow-xs">
          <div class="space-y-1.5">
            <label class="text-xs font-medium text-zinc-700" for="currentPassword">Mật khẩu hiện tại</label>
            <input id="currentPassword" v-model="passwordForm.currentPassword" :type="showPassword ? 'text' : 'password'" class="w-full rounded-xl border border-zinc-200 text-xs px-3 py-2.5 focus:outline-none focus:border-zinc-900" />
          </div>
          <div class="grid sm:grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <label class="text-xs font-medium text-zinc-700" for="newPassword">Mật khẩu mới</label>
              <input id="newPassword" v-model="passwordForm.newPassword" :type="showPassword ? 'text' : 'password'" class="w-full rounded-xl border border-zinc-200 text-xs px-3 py-2.5 focus:outline-none focus:border-zinc-900" />
            </div>
            <div class="space-y-1.5">
              <label class="text-xs font-medium text-zinc-700" for="confirmPassword">Xác nhận mật khẩu mới</label>
              <input id="confirmPassword" v-model="passwordForm.confirmPassword" :type="showPassword ? 'text' : 'password'" class="w-full rounded-xl border border-zinc-200 text-xs px-3 py-2.5 focus:outline-none focus:border-zinc-900" />
            </div>
          </div>
          <p v-if="passwordError" data-testid="password-error" class="text-[11px] text-red-600">{{ passwordError }}</p>
          <label class="flex items-center gap-2 text-xs text-zinc-600 cursor-pointer">
            <input type="checkbox" v-model="showPassword" /> Hiện mật khẩu
          </label>
          <button type="submit" :disabled="savingPassword" class="px-5 py-2.5 rounded-xl text-xs font-medium bg-zinc-900 text-white hover:bg-zinc-800 transition disabled:opacity-50 cursor-pointer">
            {{ savingPassword ? 'Đang xử lý...' : 'Đổi mật khẩu' }}
          </button>
        </form>

        <!-- Addresses -->
        <div v-else class="space-y-4">
          <div class="flex items-center justify-between">
            <p class="text-xs text-zinc-500">Bạn có {{ addresses.length }} địa chỉ giao hàng</p>
            <button v-if="!showAddressForm" type="button" data-testid="add-address" @click="openAddressForm()" class="px-4 py-2 rounded-xl text-xs font-medium bg-zinc-900 text-white hover:bg-zinc-800 transition cursor-pointer">
              + Thêm địa chỉ
            </button>
          </div>

          <form v-if="showAddressForm" data-testid="address-form" @submit.prevent="handleSaveAddress" class="bg-white border border-zinc-200/80 rounded-2xl p-5 sm:p-6 space-y-4 shadow-xs">
            <h3 class="text-sm font-semibold text-zinc-900">{{ editingId ? 'Chỉnh sửa địa chỉ' : 'Thêm địa chỉ mới' }}</h3>
            <div class="grid sm:grid-cols-2 gap-4">
              <input v-model="addressForm.recipientName" placeholder="Tên người nhận" class="rounded-xl border border-zinc-200 text-xs px-3 py-2.5 focus:outline-none focus:border-zinc-900" />
              <input v-model="addressForm.phone" placeholder="Số điện thoại" class="rounded-xl border border-zinc-200 text-xs px-3 py-2.5 focus:outline-none focus:border-zinc-900" />
            </div>
            <p v-if="editingId" class="text-[11px] text-zinc-400">Vui lòng chọn lại Tỉnh / Quận / Phường khi chỉnh sửa. Hiện tại: {{ editingFullAddress }}</p>
            <div class="grid sm:grid-cols-3 gap-4">
              <CustomSearchSelect v-model="selectedProvinceCode" :options="provinces" placeholder="-- Tỉnh / Thành phố --" :loading="loadingDivisions.provinces" @change="handleProvinceChange" />
              <CustomSearchSelect v-model="selectedDistrictCode" :options="districts" :disabled="!selectedProvinceCode" :loading="loadingDivisions.districts" placeholder="-- Quận / Huyện --" @change="handleDistrictChange" />
              <CustomSearchSelect v-model="selectedWardCode" :options="wards" :disabled="!selectedDistrictCode" :loading="loadingDivisions.wards" placeholder="-- Phường / Xã --" />
            </div>
            <input v-model="addressForm.addressDetail" placeholder="Số nhà, tên đường" class="w-full rounded-xl border border-zinc-200 text-xs px-3 py-2.5 focus:outline-none focus:border-zinc-900" />
            <label class="flex items-center gap-2 text-xs text-zinc-600 cursor-pointer">
              <input type="checkbox" v-model="addressForm.isDefault" /> Đặt làm địa chỉ mặc định
            </label>
            <p v-if="addressError" class="text-[11px] text-red-600">{{ addressError }}</p>
            <div class="flex gap-2">
              <button type="submit" :disabled="savingAddress" class="px-5 py-2.5 rounded-xl text-xs font-medium bg-zinc-900 text-white hover:bg-zinc-800 transition disabled:opacity-50 cursor-pointer">
                {{ savingAddress ? 'Đang lưu...' : 'Lưu địa chỉ' }}
              </button>
              <button type="button" @click="closeAddressForm" class="px-5 py-2.5 rounded-xl text-xs font-medium text-zinc-600 hover:bg-zinc-100 transition cursor-pointer">Hủy</button>
            </div>
          </form>

          <div v-if="loadingAddresses" class="text-center py-10 text-zinc-400 text-xs animate-pulse">Đang tải địa chỉ...</div>
          <div v-else-if="addresses.length === 0 && !showAddressForm" class="text-center py-10 bg-white rounded-2xl border border-zinc-200/80 text-xs text-zinc-500">
            Bạn chưa có địa chỉ nào.
          </div>
          <div v-else class="space-y-3">
            <div v-for="addr in addresses" :key="addr.id" data-testid="address-item" class="bg-white border border-zinc-200/80 rounded-2xl p-4 sm:p-5 flex flex-col sm:flex-row sm:items-start justify-between gap-3 shadow-xs">
              <div class="text-xs space-y-1">
                <div class="flex items-center gap-2">
                  <span class="font-semibold text-zinc-900">{{ addr.recipientName }}</span>
                  <span class="text-zinc-400">{{ addr.phone }}</span>
                  <span v-if="addr.isDefault" class="px-2 py-0.5 rounded-full bg-zinc-900 text-white text-[10px] font-medium">Mặc định</span>
                </div>
                <div class="text-zinc-600">{{ addr.fullAddress }}</div>
              </div>
              <div class="flex gap-1.5 shrink-0">
                <button v-if="!addr.isDefault" type="button" data-testid="set-default" @click="handleSetDefault(addr)" class="px-2.5 py-1 rounded-lg text-xs font-medium text-zinc-700 border border-zinc-200 hover:bg-zinc-100 transition cursor-pointer">Mặc định</button>
                <button type="button" @click="openAddressForm(addr)" class="px-2.5 py-1 rounded-lg text-xs font-medium text-zinc-700 border border-zinc-200 hover:bg-zinc-100 transition cursor-pointer">Sửa</button>
                <button type="button" data-testid="delete-address" @click="handleDelete(addr)" class="px-2.5 py-1 rounded-lg text-xs font-medium text-red-600 border border-red-200 hover:bg-red-50 transition cursor-pointer">Xóa</button>
              </div>
            </div>
          </div>
        </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch, getCurrentInstance } from 'vue'
import { toast } from 'vue-sonner'
import { useAuthStore } from '@/stores/auth'
import { authApi } from '@/api/authApi'
import AccountSidebar, { type AccountTabKey } from '@/components/AccountSidebar.vue'
import CustomSearchSelect, { type SelectOption } from '@/components/CustomSearchSelect.vue'
import { vietnamAddressApi } from '@/api/vietnamAddressApi'
import type { UserAddress } from '@/types'

type TabKey = 'profile' | 'password' | 'addresses'

const PHONE_REGEX = /^(0|\+84)[35789][0-9]{8}$/

const instance = getCurrentInstance()
const router = instance?.appContext.config.globalProperties.$router
const route = instance?.appContext.config.globalProperties.$route

const authStore = useAuthStore()
const tabLabels: Record<TabKey, string> = {
  profile: 'Hồ sơ cá nhân',
  password: 'Đổi mật khẩu',
  addresses: 'Sổ địa chỉ'
}
const initialQueryTab = route?.query?.tab as string | undefined
const activeTab = ref<TabKey>(
  initialQueryTab === 'password' || initialQueryTab === 'addresses' ? initialQueryTab : 'profile'
)
const activeTabLabel = computed(() => tabLabels[activeTab.value] || '')

const handleTabSelect = (tab: AccountTabKey) => {
  if (tab === 'orders') {
    router?.push('/orders')
    return
  }
  activeTab.value = tab
}

const formatDate = (d?: string) => (d ? new Date(d).toLocaleDateString('vi-VN') : '—')

// ---------- Profile ----------
const profileForm = reactive({ fullName: '', phone: '' })
const profileErrors = reactive({ fullName: '', phone: '' })
const savingProfile = ref(false)

const syncProfileForm = () => {
  profileForm.fullName = authStore.user?.fullName || ''
  profileForm.phone = authStore.user?.phone || authStore.user?.phoneNumber || ''
}

const handleUpdateProfile = async () => {
  profileErrors.fullName = ''
  profileErrors.phone = ''
  const name = profileForm.fullName.trim()
  const phone = profileForm.phone.trim()
  if (name.length < 2 || name.length > 100) profileErrors.fullName = 'Họ và tên phải từ 2 đến 100 ký tự'
  if (phone && !PHONE_REGEX.test(phone)) profileErrors.phone = 'Số điện thoại không đúng định dạng Việt Nam'
  if (profileErrors.fullName || profileErrors.phone) return

  savingProfile.value = true
  try {
    await authStore.updateProfile({ fullName: name, phone: phone || undefined })
    toast.success('Cập nhật thông tin thành công')
  } catch (e: any) {
    toast.error(e?.message || 'Không thể cập nhật thông tin')
  } finally {
    savingProfile.value = false
  }
}

// ---------- Password ----------
const passwordForm = reactive({ currentPassword: '', newPassword: '', confirmPassword: '' })
const passwordError = ref('')
const showPassword = ref(false)
const savingPassword = ref(false)

const handleChangePassword = async () => {
  passwordError.value = ''
  if (!passwordForm.currentPassword) {
    passwordError.value = 'Vui lòng nhập mật khẩu hiện tại'
    return
  }
  if (passwordForm.newPassword.length < 6 || passwordForm.newPassword.length > 32) {
    passwordError.value = 'Mật khẩu mới phải từ 6 đến 32 ký tự'
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    passwordError.value = 'Xác nhận mật khẩu không khớp'
    return
  }
  savingPassword.value = true
  try {
    await authApi.changePassword({
      currentPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword
    })
    toast.success('Đổi mật khẩu thành công')
    passwordForm.currentPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (e: any) {
    passwordError.value = e?.message || 'Không thể đổi mật khẩu'
  } finally {
    savingPassword.value = false
  }
}

// ---------- Addresses ----------
const addresses = ref<UserAddress[]>([])
const loadingAddresses = ref(false)
const showAddressForm = ref(false)
const editingId = ref<number | null>(null)
const editingFullAddress = ref('')
const savingAddress = ref(false)
const addressError = ref('')
const addressForm = reactive({ recipientName: '', phone: '', addressDetail: '', isDefault: false })

const provinces = ref<SelectOption[]>([])
const districts = ref<SelectOption[]>([])
const wards = ref<SelectOption[]>([])
const selectedProvinceCode = ref<number | string | null>(null)
const selectedDistrictCode = ref<number | string | null>(null)
const selectedWardCode = ref<number | string | null>(null)
const loadingDivisions = reactive({ provinces: false, districts: false, wards: false })

const loadAddresses = async () => {
  loadingAddresses.value = true
  try {
    const res = await authApi.getAddresses()
    addresses.value = res.data || []
  } catch (e: any) {
    toast.error(e?.message || 'Không thể tải sổ địa chỉ')
  } finally {
    loadingAddresses.value = false
  }
}

const loadProvinces = async () => {
  if (provinces.value.length) return
  loadingDivisions.provinces = true
  try {
    provinces.value = await vietnamAddressApi.getProvinces()
  } catch {
    toast.error('Không thể tải danh sách tỉnh/thành phố')
  } finally {
    loadingDivisions.provinces = false
  }
}

const handleProvinceChange = async () => {
  selectedDistrictCode.value = null
  selectedWardCode.value = null
  districts.value = []
  wards.value = []
  if (!selectedProvinceCode.value) return
  loadingDivisions.districts = true
  try {
    districts.value = await vietnamAddressApi.getDistrictsByProvince(Number(selectedProvinceCode.value))
  } catch {
    toast.error('Không thể tải danh sách quận/huyện')
  } finally {
    loadingDivisions.districts = false
  }
}

const handleDistrictChange = async () => {
  selectedWardCode.value = null
  wards.value = []
  if (!selectedDistrictCode.value) return
  loadingDivisions.wards = true
  try {
    wards.value = await vietnamAddressApi.getWardsByDistrict(Number(selectedDistrictCode.value))
  } catch {
    toast.error('Không thể tải danh sách phường/xã')
  } finally {
    loadingDivisions.wards = false
  }
}

const resetAddressForm = () => {
  addressForm.recipientName = authStore.user?.fullName || ''
  addressForm.phone = authStore.user?.phone || authStore.user?.phoneNumber || ''
  addressForm.addressDetail = ''
  addressForm.isDefault = false
  selectedProvinceCode.value = null
  selectedDistrictCode.value = null
  selectedWardCode.value = null
  districts.value = []
  wards.value = []
  addressError.value = ''
}

const openAddressForm = (addr?: UserAddress) => {
  resetAddressForm()
  editingId.value = addr?.id ?? null
  editingFullAddress.value = addr?.fullAddress || ''
  if (addr) {
    addressForm.recipientName = addr.recipientName
    addressForm.phone = addr.phone
    addressForm.addressDetail = addr.addressDetail
    addressForm.isDefault = addr.isDefault
  }
  showAddressForm.value = true
  loadProvinces()
}

const closeAddressForm = () => {
  showAddressForm.value = false
  editingId.value = null
}

const nameOf = (list: SelectOption[], code: number | string | null) =>
  list.find(o => String(o.code) === String(code))?.name || ''

const handleSaveAddress = async () => {
  addressError.value = ''
  const payload = {
    recipientName: addressForm.recipientName.trim(),
    phone: addressForm.phone.trim(),
    province: nameOf(provinces.value, selectedProvinceCode.value),
    district: nameOf(districts.value, selectedDistrictCode.value),
    ward: nameOf(wards.value, selectedWardCode.value),
    addressDetail: addressForm.addressDetail.trim(),
    isDefault: addressForm.isDefault
  }
  if (!payload.recipientName || !payload.addressDetail) {
    addressError.value = 'Vui lòng nhập đầy đủ tên người nhận và địa chỉ chi tiết'
    return
  }
  if (!PHONE_REGEX.test(payload.phone)) {
    addressError.value = 'Số điện thoại không đúng định dạng Việt Nam'
    return
  }
  if (!payload.province || !payload.district || !payload.ward) {
    addressError.value = 'Vui lòng chọn đầy đủ Tỉnh / Quận / Phường'
    return
  }
  savingAddress.value = true
  try {
    if (editingId.value) {
      await authApi.updateAddress(editingId.value, payload)
      toast.success('Đã cập nhật địa chỉ')
    } else {
      await authApi.addAddress(payload)
      toast.success('Đã thêm địa chỉ mới')
    }
    closeAddressForm()
    await loadAddresses()
  } catch (e: any) {
    addressError.value = e?.message || 'Không thể lưu địa chỉ'
  } finally {
    savingAddress.value = false
  }
}

const handleSetDefault = async (addr: UserAddress) => {
  try {
    await authApi.setDefaultAddress(addr.id)
    toast.success('Đã đặt làm địa chỉ mặc định')
    await loadAddresses()
  } catch (e: any) {
    toast.error(e?.message || 'Không thể đặt địa chỉ mặc định')
  }
}

const handleDelete = async (addr: UserAddress) => {
  if (!window.confirm(`Xóa địa chỉ của ${addr.recipientName}?`)) return
  try {
    await authApi.deleteAddress(addr.id)
    toast.success('Đã xóa địa chỉ')
    await loadAddresses()
  } catch (e: any) {
    toast.error(e?.message || 'Không thể xóa địa chỉ')
  }
}

const init = () => {
  if (!authStore.isAuthenticated) return
  syncProfileForm()
  loadAddresses()
}

onMounted(init)
watch(() => authStore.isAuthenticated, init)
</script>
