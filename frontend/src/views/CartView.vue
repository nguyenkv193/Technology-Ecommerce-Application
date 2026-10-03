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
        <h3 class="text-base font-bold text-zinc-900 pb-3 border-b border-zinc-100">Thông Tin Nhận Hàng</h3>

        <form @submit.prevent="handleCheckout" class="space-y-3.5">
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

          <div>
            <label class="block text-xs font-medium text-zinc-700 mb-1">Địa chỉ giao hàng chi tiết *</label>
            <textarea 
              v-model="checkoutForm.shippingAddress"
              rows="2" 
              required 
              placeholder="Số nhà, tên đường, phường/xã, quận/huyện, thành phố..." 
              class="w-full bg-zinc-50 border border-zinc-200 rounded-xl p-3 text-xs text-zinc-900 focus:bg-white focus:outline-none focus:border-zinc-800 transition"
            ></textarea>
          </div>

          <div>
            <label class="block text-xs font-medium text-zinc-700 mb-1">Ghi chú vận chuyển</label>
            <input 
              v-model="checkoutForm.note"
              type="text" 
              placeholder="Giao giờ hành chính, gọi trước..." 
              class="w-full bg-zinc-50 border border-zinc-200 rounded-xl px-3 py-2 text-xs text-zinc-900 focus:bg-white focus:outline-none focus:border-zinc-800 transition"
            />
          </div>

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
      <div class="bg-white border border-zinc-200 rounded-3xl p-8 max-w-md w-full text-center space-y-4 shadow-2xl">
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
import { ref, reactive, onMounted } from 'vue'
import { useCartStore } from '@/stores/cart'
import { useAuthStore } from '@/stores/auth'
import { orderApi } from '@/api/orderApi'
import { behaviorApi } from '@/api/behaviorApi'
import type { Order } from '@/types'
import type { CheckoutRequest } from '@/api/orderApi'

const cartStore = useCartStore()
const authStore = useAuthStore()

const submitting = ref<boolean>(false)
const checkoutError = ref<string>('')
const placedOrder = ref<Order | null>(null)

const defaultImage = 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500&auto=format&fit=crop&q=60'

const checkoutForm = reactive<CheckoutRequest>({
  recipientName: authStore.user?.fullName || '',
  phoneNumber: authStore.user?.phoneNumber || '',
  shippingAddress: '',
  note: '',
  paymentMethod: 'COD'
})

const formatPrice = (value?: number | null): string => {
  if (!value || isNaN(value)) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}

const handleCheckout = async () => {
  if (!authStore.isAuthenticated) {
    alert('Vui lòng đăng nhập tài khoản để thực hiện đặt hàng!')
    return
  }

  submitting.value = true
  checkoutError.value = ''
  try {
    const res = await orderApi.checkout(checkoutForm)
    if (res.success && res.data) {
      placedOrder.value = res.data

      if (res.data.items && res.data.items.length > 0) {
        for (const item of res.data.items) {
          behaviorApi.track('PURCHASE', item.productId, `order=${res.data.orderCode}`)
        }
      }

      await cartStore.fetchCart()
    }
  } catch (err: any) {
    checkoutError.value = err.message
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  cartStore.fetchCart()
})
</script>
