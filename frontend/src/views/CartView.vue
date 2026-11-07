<template>
  <div class="space-y-8 sm:space-y-10">
    <div class="pb-5 border-b border-zinc-200/80">
      <h1 class="text-2xl sm:text-3xl font-bold text-zinc-900 tracking-tight">Giỏ Hàng Thiết Bị</h1>
      <p class="text-xs sm:text-sm text-zinc-500 mt-1">Xác nhận các phiên bản cấu hình đã chọn và tiến hành đặt hàng</p>
    </div>

    <!-- Empty Cart -->
    <div v-if="cartStore.items.length === 0" class="text-center py-16 bg-white rounded-2xl border border-zinc-200/80 p-8 space-y-4">
      <div class="w-14 h-14 mx-auto rounded-2xl bg-zinc-100 flex items-center justify-center text-zinc-500">
        <svg class="w-7 h-7" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M16 11V7a4 4 0 0 0-8 0v4M5 9h14l1 12H4L5 9z"/></svg>
      </div>
      <div>
        <h3 class="text-base font-semibold text-zinc-900">Giỏ hàng của bạn đang trống</h3>
        <p class="text-xs text-zinc-500 max-w-sm mx-auto mt-1">Khám phá các sản phẩm công nghệ tiên tiến được AI đề xuất cho bạn.</p>
      </div>
      <router-link to="/products" class="inline-block px-5 py-2.5 rounded-xl text-xs font-medium bg-zinc-900 text-white hover:bg-zinc-800 transition">
        Khám phá sản phẩm
      </router-link>
    </div>

    <!-- Cart Items & Checkout -->
    <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-8 items-start">
      <!-- Items List -->
      <div class="lg:col-span-2 space-y-3">
        <div 
          v-for="item in cartStore.items" 
          :key="item.id"
          class="p-4 sm:p-5 rounded-2xl bg-white border border-zinc-200/80 flex items-center gap-4 sm:gap-5 shadow-2xs"
        >
          <!-- Thumbnail -->
          <div class="w-16 h-16 sm:w-20 sm:h-20 rounded-xl bg-[#f6f6f8] p-2 shrink-0 flex items-center justify-center border border-zinc-100">
            <img :src="item.thumbnailUrl || defaultImage" class="max-w-full max-h-full object-contain" />
          </div>

          <!-- Product Details -->
          <div class="flex-1 min-w-0 space-y-0.5">
            <router-link :to="`/products/${item.productId}`" class="text-xs sm:text-sm font-semibold text-zinc-900 hover:text-zinc-600 truncate block">
              {{ item.productName }}
            </router-link>
            <div class="text-[11px] text-zinc-500">Cấu hình: {{ item.variantName }}</div>
            <div class="text-[10px] font-mono text-zinc-400">SKU: {{ item.sku }}</div>
            <div class="text-xs font-bold text-zinc-900 sm:hidden mt-1">{{ formatPrice(item.price) }}</div>
          </div>

          <!-- Price (Desktop) -->
          <div class="text-right hidden sm:block">
            <div class="text-xs sm:text-sm font-semibold text-zinc-900">{{ formatPrice(item.price) }}</div>
          </div>

          <!-- Quantity Controls -->
          <div class="flex items-center bg-zinc-50 border border-zinc-200 rounded-lg p-0.5 shrink-0">
            <button 
              @click="cartStore.updateQuantity(item.id, item.quantity - 1)"
              class="w-6 h-6 text-xs font-bold text-zinc-600 hover:text-zinc-900 rounded hover:bg-white"
            >
              -
            </button>
            <span class="w-7 text-center text-xs font-bold text-zinc-900">{{ item.quantity }}</span>
            <button 
              @click="cartStore.updateQuantity(item.id, item.quantity + 1)"
              class="w-6 h-6 text-xs font-bold text-zinc-600 hover:text-zinc-900 rounded hover:bg-white"
            >
              +
            </button>
          </div>

          <!-- Total & Remove -->
          <div class="text-right flex flex-col items-end gap-1.5 shrink-0">
            <div class="text-xs sm:text-sm font-bold text-zinc-900">{{ formatPrice(item.subTotal) }}</div>
            <button 
              @click="cartStore.removeItem(item.id)"
              class="text-xs text-zinc-400 hover:text-red-500 transition p-1"
              title="Xóa khỏi giỏ"
            >
              ✕
            </button>
          </div>
        </div>
      </div>

      <!-- Checkout Form Sidebar -->
      <div class="bg-white border border-zinc-200/80 rounded-2xl p-6 sm:p-7 space-y-5 lg:sticky lg:top-24 shadow-xs">
        <h3 class="text-base font-bold text-zinc-900 pb-3 border-b border-zinc-100 flex items-center justify-between">
          <span>Thông Tin Nhận Hàng</span>
          <span v-if="authStore.isAuthenticated" class="text-[10px] font-normal text-zinc-400">Tài khoản xác thực</span>
        </h3>

        <form @submit.prevent="handleCheckout" class="space-y-4">
          <!-- Người nhận hàng -->
          <div>
            <label class="block text-xs font-medium text-zinc-700 mb-1">Người nhận hàng *</label>
            <input 
              v-model="checkoutForm.recipientName"
              type="text" 
              required 
              placeholder="Nguyễn Văn A" 
              class="w-full bg-zinc-50 border border-zinc-200 rounded-xl px-3 py-2 text-xs text-zinc-900 focus:bg-white focus:outline-none focus:border-zinc-800 transition"
            />
          </div>

          <!-- Số điện thoại -->
          <div>
            <label class="block text-xs font-medium text-zinc-700 mb-1">Số điện thoại *</label>
            <input 
              v-model="checkoutForm.phoneNumber"
              type="tel" 
              required 
              placeholder="0912345678" 
              class="w-full bg-zinc-50 border border-zinc-200 rounded-xl px-3 py-2 text-xs text-zinc-900 focus:bg-white focus:outline-none focus:border-zinc-800 transition"
            />
          </div>

          <!-- Chọn từ sổ địa chỉ đã lưu (nếu có) -->
          <div v-if="savedAddresses.length > 0" class="space-y-2 pt-1">
            <label class="block text-xs font-medium text-zinc-700">Chọn địa chỉ giao hàng</label>
            <div class="space-y-2">
              <div 
                v-for="addr in savedAddresses" 
                :key="addr.id"
                @click="selectSavedAddress(addr)"
                :class="[
                  'p-3 rounded-xl border text-xs cursor-pointer transition flex items-start gap-2.5',
                  selectedAddressId === addr.id
                    ? 'border-zinc-900 bg-zinc-50/90 ring-1 ring-zinc-900'
                    : 'border-zinc-200 hover:border-zinc-300 bg-white'
                ]"
              >
                <input 
                  type="radio" 
                  name="saved_address_choice"
                  :checked="selectedAddressId === addr.id" 
                  class="mt-0.5 accent-zinc-900 shrink-0" 
                />
                <div class="flex-1 min-w-0">
                  <div class="flex items-center gap-1.5 flex-wrap">
                    <span class="font-bold text-zinc-900">{{ addr.recipientName }}</span>
                    <span class="text-zinc-500 font-mono text-[11px]">({{ addr.phone }})</span>
                    <span v-if="addr.isDefault" class="text-[9px] bg-zinc-900 text-white px-1.5 py-0.2 rounded font-medium">Mặc định</span>
                  </div>
                  <p class="text-zinc-600 text-[11px] mt-0.5 leading-relaxed">{{ addr.fullAddress }}</p>
                </div>
              </div>

              <!-- Nút chuyển sang nhập địa chỉ mới -->
              <div 
                @click="switchToNewAddress"
                :class="[
                  'p-2.5 rounded-xl border text-xs cursor-pointer transition flex items-center gap-2',
                  selectedAddressId === 'new'
                    ? 'border-zinc-900 bg-zinc-50/90 ring-1 ring-zinc-900 font-semibold text-zinc-900'
                    : 'border-dashed border-zinc-300 hover:border-zinc-400 bg-white text-zinc-600'
                ]"
              >
                <input 
                  type="radio" 
                  name="saved_address_choice"
                  :checked="selectedAddressId === 'new'" 
                  class="accent-zinc-900 shrink-0" 
                />
                <span class="text-[11px]">+ Giao đến địa chỉ mới khác</span>
              </div>
            </div>
          </div>

          <!-- Bộ chọn Địa chỉ mới từ Open API hành chính Việt Nam (Custom Dropdowns) -->
          <div v-if="selectedAddressId === 'new'" class="space-y-3 p-3.5 bg-zinc-50/80 border border-zinc-200/90 rounded-xl">
            <div class="text-[11px] font-semibold text-zinc-800 flex items-center justify-between">
              <span class="flex items-center gap-1.5">
                <svg class="w-3.5 h-3.5 text-zinc-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 21s-8-4.5-8-11.8A8 8 0 0 1 20 9.2c0 7.3-8 11.8-8 11.8z"/><circle cx="12" cy="9" r="3"/></svg>
                Địa chỉ giao hàng (Tỉnh / Huyện / Xã)
              </span>
              <span v-if="loadingDivisions.provinces" class="text-[10px] text-zinc-400 animate-pulse">Đang nạp dữ liệu...</span>
            </div>

            <!-- Tỉnh / Thành phố -->
            <div>
              <label class="block text-[11px] font-medium text-zinc-600 mb-1">Tỉnh / Thành phố *</label>
              <CustomSearchSelect
                v-model="selectedProvinceCode"
                :options="provinces"
                placeholder="-- Chọn Tỉnh / Thành phố --"
                search-placeholder="Tìm tỉnh, thành phố..."
                :loading="loadingDivisions.provinces"
                @change="handleProvinceChange"
              />
            </div>

            <!-- Quận / Huyện -->
            <div>
              <label class="block text-[11px] font-medium text-zinc-600 mb-1">Quận / Huyện *</label>
              <CustomSearchSelect
                v-model="selectedDistrictCode"
                :options="districts"
                :disabled="!selectedProvinceCode"
                :loading="loadingDivisions.districts"
                placeholder="-- Chọn Quận / Huyện --"
                search-placeholder="Tìm quận, huyện, thị xã..."
                @change="handleDistrictChange"
              />
            </div>

            <!-- Phường / Xã -->
            <div>
              <label class="block text-[11px] font-medium text-zinc-600 mb-1">Phường / Xã *</label>
              <CustomSearchSelect
                v-model="selectedWardCode"
                :options="wards"
                :disabled="!selectedDistrictCode"
                :loading="loadingDivisions.wards"
                placeholder="-- Chọn Phường / Xã --"
                search-placeholder="Tìm phường, xã, thị trấn..."
                @change="updateComputedShippingAddress"
              />
            </div>

            <!-- Số nhà, tên đường -->
            <div>
              <label class="block text-[11px] font-medium text-zinc-600 mb-1">Số nhà, ngõ ngách, tên đường *</label>
              <input 
                v-model="streetAddress"
                @input="updateComputedShippingAddress"
                type="text"
                required
                placeholder="VD: Số 123 đường Cầu Giấy, Tòa nhà Indochina"
                class="w-full bg-white border border-zinc-200 rounded-xl px-3 py-2 text-xs text-zinc-900 focus:outline-none focus:border-zinc-800 transition"
              />
            </div>

            <!-- Preview địa chỉ đầy đủ -->
            <div v-if="checkoutForm.shippingAddress" class="p-2.5 bg-emerald-50/80 border border-emerald-200/80 rounded-lg text-[11px] text-emerald-900 leading-relaxed">
              <span class="font-bold text-emerald-950">Địa chỉ đầy đủ:</span> {{ checkoutForm.shippingAddress }}
            </div>

            <!-- Checkbox lưu vào sổ địa chỉ (nếu đã đăng nhập) -->
            <div v-if="authStore.isAuthenticated" class="pt-1">
              <label class="flex items-center gap-2 cursor-pointer text-xs text-zinc-700 select-none">
                <input type="checkbox" v-model="saveAddressToProfile" class="rounded accent-zinc-900 w-3.5 h-3.5" />
                <span>Lưu địa chỉ này vào tài khoản cho lần sau</span>
              </label>
            </div>
          </div>

          <!-- Ghi chú vận chuyển -->
          <div>
            <label class="block text-xs font-medium text-zinc-700 mb-1">Ghi chú vận chuyển</label>
            <input 
              v-model="checkoutForm.note"
              type="text" 
              placeholder="Giao giờ hành chính, gọi trước..." 
              class="w-full bg-zinc-50 border border-zinc-200 rounded-xl px-3 py-2 text-xs text-zinc-900 focus:bg-white focus:outline-none focus:border-zinc-800 transition"
            />
          </div>

          <!-- Hình thức thanh toán -->
          <div>
            <label class="block text-xs font-medium text-zinc-700 mb-1.5">Hình thức thanh toán</label>
            <div class="grid grid-cols-2 gap-2 text-xs">
              <label class="flex items-center gap-2 p-2.5 rounded-xl border border-zinc-200 bg-zinc-50 cursor-pointer hover:border-zinc-400 transition">
                <input type="radio" value="COD" v-model="checkoutForm.paymentMethod" class="accent-zinc-900" />
                <span class="text-zinc-800">COD (Tiền mặt)</span>
              </label>
              <label class="flex items-center gap-2 p-2.5 rounded-xl border border-zinc-200 bg-zinc-50 cursor-pointer hover:border-zinc-400 transition">
                <input type="radio" value="VNPAY" v-model="checkoutForm.paymentMethod" class="accent-zinc-900" />
                <span class="text-zinc-800">VNPAY QR</span>
              </label>
            </div>
          </div>

          <!-- Summary Math -->
          <div class="pt-3.5 border-t border-zinc-100 space-y-1.5 text-xs">
            <div class="flex justify-between text-zinc-500">
              <span>Tổng tiền hàng:</span>
              <span class="text-zinc-800 font-medium">{{ formatPrice(cartStore.totalPrice) }}</span>
            </div>
            <div class="flex justify-between text-zinc-500">
              <span>Phí vận chuyển:</span>
              <span class="text-emerald-700 font-medium">Miễn phí</span>
            </div>
            <div class="flex justify-between text-sm font-bold text-zinc-900 pt-2 border-t border-zinc-100">
              <span>Tổng thanh toán:</span>
              <span class="text-zinc-900 text-base font-black">{{ formatPrice(cartStore.totalPrice) }}</span>
            </div>
          </div>

          <p v-if="checkoutError" class="text-xs text-red-500 text-center">{{ checkoutError }}</p>

          <button 
            type="submit" 
            :disabled="submitting"
            class="w-full py-3 rounded-xl font-medium text-xs sm:text-sm bg-zinc-900 hover:bg-zinc-800 text-white shadow-xs transition disabled:opacity-40"
          >
            {{ submitting ? 'Đang xử lý đơn hàng...' : 'Xác nhận đặt hàng' }}
          </button>
        </form>
      </div>
    </div>

    <!-- Success Modal -->
    <div v-if="placedOrder" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-xs">
      <div class="bg-white border border-zinc-200 rounded-xl p-8 max-w-md w-full text-center space-y-4 shadow-2xl">
        <div class="w-14 h-14 rounded-full bg-emerald-50 text-emerald-700 border border-emerald-200 mx-auto flex items-center justify-center text-xl font-bold">
          ✓
        </div>
        <div>
          <h3 class="text-xl font-bold text-zinc-900">Đặt Hàng Thành Công</h3>
          <p class="text-xs text-zinc-500 mt-1">Đơn hàng của bạn đã được ghi nhận và đang được xử lý.</p>
        </div>
        <div class="p-4 rounded-xl bg-zinc-50 border border-zinc-200/80 text-left text-xs space-y-1.5">
          <div class="flex justify-between text-zinc-500">
            <span>Mã đơn hàng:</span>
            <span class="font-mono text-zinc-900 font-bold">{{ placedOrder.orderCode }}</span>
          </div>
          <div class="flex justify-between text-zinc-500">
            <span>Người nhận:</span>
            <span class="text-zinc-800 font-medium">{{ placedOrder.recipientName }}</span>
          </div>
          <div class="flex justify-between text-zinc-500">
            <span>Địa chỉ nhận:</span>
            <span class="text-zinc-800 font-medium text-right max-w-[200px] truncate" :title="placedOrder.shippingAddress">{{ placedOrder.shippingAddress }}</span>
          </div>
          <div class="flex justify-between text-zinc-500">
            <span>Tổng tiền:</span>
            <span class="text-zinc-900 font-bold">{{ formatPrice(placedOrder.finalAmount) }}</span>
          </div>
        </div>
        <div class="flex gap-2.5 pt-2">
          <router-link to="/orders" class="flex-1 py-2.5 bg-zinc-900 hover:bg-zinc-800 text-white rounded-xl text-xs font-medium transition">
            Xem lịch sử đơn hàng
          </router-link>
          <button @click="placedOrder = null" class="py-2.5 px-4 bg-zinc-100 hover:bg-zinc-200/70 text-zinc-700 rounded-xl text-xs font-medium transition">
            Đóng
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue'
import { toast } from 'vue-sonner'
import { useCartStore } from '@/stores/cart'
import { useAuthStore } from '@/stores/auth'
import { orderApi } from '@/api/orderApi'
import { authApi } from '@/api/authApi'
import { behaviorApi } from '@/api/behaviorApi'
import CustomSearchSelect from '@/components/CustomSearchSelect.vue'
import { 
  vietnamAddressApi, 
  type VietnamProvince, 
  type VietnamDistrict, 
  type VietnamWard 
} from '@/api/vietnamAddressApi'
import type { Order, UserAddress } from '@/types'
import type { CheckoutRequest } from '@/api/orderApi'

const cartStore = useCartStore()
const authStore = useAuthStore()

const submitting = ref<boolean>(false)
const checkoutError = ref<string>('')
const placedOrder = ref<Order | null>(null)

const defaultImage = 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500&auto=format&fit=crop&q=60'

// Sổ địa chỉ đã lưu
const savedAddresses = ref<UserAddress[]>([])
const selectedAddressId = ref<number | 'new'>('new')

// Địa chỉ hành chính Việt Nam
const provinces = ref<VietnamProvince[]>([])
const districts = ref<VietnamDistrict[]>([])
const wards = ref<VietnamWard[]>([])

const selectedProvinceCode = ref<number | null>(null)
const selectedDistrictCode = ref<number | null>(null)
const selectedWardCode = ref<number | null>(null)
const streetAddress = ref<string>('')
const saveAddressToProfile = ref<boolean>(true)

const loadingDivisions = reactive({
  provinces: false,
  districts: false,
  wards: false
})

const checkoutForm = reactive<CheckoutRequest>({
  recipientName: authStore.user?.fullName || '',
  phoneNumber: authStore.user?.phoneNumber || '',
  shippingAddress: '',
  note: '',
  paymentMethod: 'COD'
})

// Tải sổ địa chỉ đã lưu của tài khoản
const loadSavedAddresses = async () => {
  if (!authStore.isAuthenticated) {
    selectedAddressId.value = 'new'
    savedAddresses.value = []
    return
  }
  try {
    const res = await authApi.getAddresses()
    if (res.success && res.data && res.data.length > 0) {
      savedAddresses.value = res.data
      const defaultAddr = res.data.find(a => a.isDefault) || res.data[0]
      selectSavedAddress(defaultAddr)
    } else {
      selectedAddressId.value = 'new'
      savedAddresses.value = []
    }
  } catch (err) {
    console.error('Không thể tải sổ địa chỉ:', err)
    selectedAddressId.value = 'new'
  }
}

// Chọn địa chỉ từ sổ địa chỉ đã lưu
const selectSavedAddress = (addr: UserAddress) => {
  selectedAddressId.value = addr.id
  checkoutForm.recipientName = addr.recipientName
  checkoutForm.phoneNumber = addr.phone
  checkoutForm.shippingAddress = addr.fullAddress
}

// Chuyển sang nhập địa chỉ mới
const switchToNewAddress = () => {
  selectedAddressId.value = 'new'
  if (authStore.user) {
    checkoutForm.recipientName = authStore.user.fullName || ''
    checkoutForm.phoneNumber = authStore.user.phoneNumber || ''
  }
  updateComputedShippingAddress()
}

// Tải danh sách tỉnh/thành phố
const loadProvinces = async () => {
  loadingDivisions.provinces = true
  try {
    provinces.value = await vietnamAddressApi.getProvinces()
  } catch (err) {
    console.error('Lỗi tải danh mục tỉnh/thành:', err)
    toast.error('Không thể tải danh sách tỉnh/thành phố')
  } finally {
    loadingDivisions.provinces = false
  }
}

// Xử lý khi chọn tỉnh/thành
const handleProvinceChange = async () => {
  selectedDistrictCode.value = null
  selectedWardCode.value = null
  districts.value = []
  wards.value = []
  updateComputedShippingAddress()

  if (!selectedProvinceCode.value) return
  loadingDivisions.districts = true
  try {
    districts.value = await vietnamAddressApi.getDistrictsByProvince(selectedProvinceCode.value)
  } catch (err) {
    console.error('Lỗi tải quận/huyện:', err)
    toast.error('Không thể tải danh sách quận/huyện')
  } finally {
    loadingDivisions.districts = false
  }
}

// Xử lý khi chọn quận/huyện
const handleDistrictChange = async () => {
  selectedWardCode.value = null
  wards.value = []
  updateComputedShippingAddress()

  if (!selectedDistrictCode.value) return
  loadingDivisions.wards = true
  try {
    wards.value = await vietnamAddressApi.getWardsByDistrict(selectedDistrictCode.value)
  } catch (err) {
    console.error('Lỗi tải phường/xã:', err)
    toast.error('Không thể tải danh sách phường/xã')
  } finally {
    loadingDivisions.wards = false
  }
}

// Cập nhật chuỗi địa chỉ giao hàng hoàn chỉnh
const updateComputedShippingAddress = () => {
  if (selectedAddressId.value !== 'new') return

  const prov = provinces.value.find(p => p.code === selectedProvinceCode.value)
  const dist = districts.value.find(d => d.code === selectedDistrictCode.value)
  const ward = wards.value.find(w => w.code === selectedWardCode.value)

  const parts = [
    streetAddress.value.trim(),
    ward?.name,
    dist?.name,
    prov?.name
  ].filter(Boolean)

  checkoutForm.shippingAddress = parts.join(', ')
}

watch(() => authStore.user, (u) => {
  if (u && selectedAddressId.value === 'new') {
    if (!checkoutForm.recipientName) checkoutForm.recipientName = u.fullName || ''
    if (!checkoutForm.phoneNumber) checkoutForm.phoneNumber = u.phoneNumber || ''
  }
})

const formatPrice = (value?: number | null): string => {
  if (!value || isNaN(value)) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}

const handleCheckout = async () => {
  if (!authStore.isAuthenticated) {
    authStore.openLoginModal()
    return
  }

  // Kiểm tra thông tin người nhận
  if (!checkoutForm.recipientName.trim()) {
    toast.error('Vui lòng nhập tên người nhận hàng')
    return
  }
  if (!checkoutForm.phoneNumber.trim()) {
    toast.error('Vui lòng nhập số điện thoại nhận hàng')
    return
  }

  // Kiểm tra địa chỉ
  if (selectedAddressId.value === 'new') {
    if (!selectedProvinceCode.value || !selectedDistrictCode.value || !selectedWardCode.value) {
      toast.error('Vui lòng chọn đầy đủ Tỉnh/Thành, Quận/Huyện và Phường/Xã')
      return
    }
    if (!streetAddress.value.trim()) {
      toast.error('Vui lòng nhập số nhà, tên đường chi tiết')
      return
    }
    updateComputedShippingAddress()
  }

  if (!checkoutForm.shippingAddress.trim()) {
    toast.error('Địa chỉ giao hàng không hợp lệ')
    return
  }

  submitting.value = true
  checkoutError.value = ''
  try {
    const res = await orderApi.checkout(checkoutForm)
    if (res.success && res.data) {
      placedOrder.value = res.data

      // Nếu người dùng tích chọn lưu vào sổ địa chỉ
      if (selectedAddressId.value === 'new' && saveAddressToProfile.value && authStore.isAuthenticated) {
        try {
          const prov = provinces.value.find(p => p.code === selectedProvinceCode.value)
          const dist = districts.value.find(d => d.code === selectedDistrictCode.value)
          const ward = wards.value.find(w => w.code === selectedWardCode.value)
          await authApi.addAddress({
            recipientName: checkoutForm.recipientName.trim(),
            phone: checkoutForm.phoneNumber.trim(),
            province: prov?.name || '',
            district: dist?.name || '',
            ward: ward?.name || '',
            addressDetail: streetAddress.value.trim(),
            isDefault: savedAddresses.value.length === 0
          })
          // Tải lại sổ địa chỉ
          await loadSavedAddresses()
        } catch (err) {
          console.warn('Không thể tự động lưu sổ địa chỉ:', err)
        }
      }

      if (res.data.items && res.data.items.length > 0) {
        for (const item of res.data.items) {
          behaviorApi.track('PURCHASE', item.productId, `order=${res.data.orderCode}`)
        }
      }

      await cartStore.fetchCart()
      toast.success('Đặt hàng thành công!')
    }
  } catch (err: any) {
    checkoutError.value = err.message
    toast.error(err.message || 'Đặt hàng thất bại')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await Promise.all([
    cartStore.fetchCart(),
    loadSavedAddresses(),
    loadProvinces()
  ])
})
</script>
