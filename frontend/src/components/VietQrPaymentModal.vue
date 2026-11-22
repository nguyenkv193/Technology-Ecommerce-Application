<template>
  <div
    v-if="order"
    data-testid="vietqr-modal"
    class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs"
    @click.self="emit('close')"
  >
    <div class="bg-white border border-zinc-200 rounded-2xl p-5 sm:p-6 max-w-2xl w-full shadow-2xl space-y-5 max-h-[92vh] overflow-y-auto">
      <!-- Header -->
      <div class="flex items-start justify-between gap-3 pb-3.5 border-b border-zinc-100">
        <div>
          <div class="flex items-center gap-2">
            <span class="px-2 py-0.5 rounded-md bg-zinc-900 text-white text-[10px] font-bold tracking-wider uppercase">VietQR</span>
            <h3 class="text-base sm:text-lg font-bold text-zinc-900">
              {{ isPaid ? 'Đã Xác Nhận Thanh Toán' : 'Quét Mã QR Để Thanh Toán' }}
            </h3>
          </div>
          <p class="text-xs text-zinc-500 mt-1">
            {{ isPaid ? 'Đơn hàng của bạn đã được ghi nhận thanh toán và xác nhận xử lý.' : 'Mở ứng dụng ngân hàng bất kỳ và quét mã VietQR bên dưới (tự động điền số tiền & nội dung).' }}
          </p>
        </div>
        <button
          type="button"
          data-testid="vietqr-close"
          @click="emit('close')"
          class="p-1.5 rounded-lg text-zinc-400 hover:text-zinc-700 hover:bg-zinc-100 transition cursor-pointer"
          title="Đóng"
        >
          ✕
        </button>
      </div>

      <!-- Paid State -->
      <div v-if="isPaid" data-testid="vietqr-paid-state" class="p-5 rounded-2xl bg-emerald-50/70 border border-emerald-200 text-center space-y-2">
        <div class="w-12 h-12 rounded-full bg-emerald-600 text-white mx-auto flex items-center justify-center text-lg font-bold">
          ✓
        </div>
        <div class="text-sm font-bold text-emerald-900">Thanh toán chuyển khoản thành công</div>
        <p class="text-xs text-emerald-700">
          Mã đơn <span class="font-mono font-bold">{{ order.orderCode }}</span> • Số tiền <span class="font-bold">{{ formatPrice(order.finalAmount) }}</span>
        </p>
      </div>

      <!-- QR + Bank Details Grid -->
      <div v-else class="grid grid-cols-1 sm:grid-cols-12 gap-5 items-center">
        <!-- QR Code Column -->
        <div class="sm:col-span-5 flex flex-col items-center">
          <div class="relative w-56 h-56 sm:w-full sm:h-auto aspect-square rounded-2xl border border-zinc-200 bg-zinc-50 p-3 flex items-center justify-center overflow-hidden">
            <div v-if="qrLoading && !qrError" class="text-[11px] text-zinc-400 animate-pulse text-center px-3">
              Đang tạo mã VietQR...
            </div>
            <img
              v-show="!qrError"
              :src="vietQrUrl"
              :alt="`VietQR ${order.orderCode}`"
              data-testid="vietqr-image"
              class="w-full h-full object-contain rounded-lg"
              @load="qrLoading = false"
              @error="handleQrError"
            />
            <div v-if="qrError" class="text-[11px] text-zinc-500 text-center px-3 space-y-1">
              <div class="font-semibold text-zinc-700">Không tải được ảnh QR</div>
              <div>Vui lòng chuyển khoản thủ công theo thông tin bên cạnh.</div>
            </div>
            <div v-if="isExpired" data-testid="vietqr-expired-overlay" class="absolute inset-0 bg-white/95 backdrop-blur-xs flex flex-col items-center justify-center p-4 text-center gap-1.5">
              <span class="text-xs font-semibold text-red-600">Đã hết thời gian thanh toán</span>
              <span class="text-[11px] text-zinc-500">Đơn hàng quá hạn thanh toán sẽ tự động hủy để hoàn tồn kho. Vui lòng đặt lại đơn mới.</span>
            </div>
          </div>
          <div class="mt-2.5 flex items-center gap-1.5 px-3 py-1 rounded-full text-[11px] font-medium border" :class="countdownBadgeClass" data-testid="vietqr-countdown">
            <span>{{ isExpired ? 'Đã hết hạn giao dịch' : 'Thời gian thanh toán còn lại:' }}</span>
            <span v-if="!isExpired" class="font-mono font-bold">{{ formattedCountdown }}</span>
          </div>
          <span class="text-[11px] text-zinc-400 mt-1.5 text-center">Hỗ trợ Napas247 bởi mọi ngân hàng & ví điện tử</span>
        </div>

        <!-- Transfer Details Column -->
        <div class="sm:col-span-7 space-y-2.5 text-xs">
          <div class="p-3 rounded-xl bg-zinc-50 border border-zinc-200/80 space-y-2.5">
            <div class="flex items-center justify-between gap-2">
              <span class="text-zinc-500">Ngân hàng:</span>
              <span class="font-semibold text-zinc-900 text-right">{{ bankName }}</span>
            </div>

            <div class="flex items-center justify-between gap-2">
              <span class="text-zinc-500">Chủ tài khoản:</span>
              <span class="font-semibold text-zinc-900 text-right">{{ accountName }}</span>
            </div>

            <div class="flex items-center justify-between gap-2 pt-2 border-t border-zinc-200/60">
              <div>
                <div class="text-zinc-500">Số tài khoản</div>
                <div class="font-mono font-bold text-sm text-zinc-900 mt-0.5" data-testid="vietqr-account-no">{{ accountNo }}</div>
              </div>
              <button
                type="button"
                data-testid="copy-account"
                @click="copyText(accountNo, 'số tài khoản')"
                class="px-2.5 py-1 rounded-lg border border-zinc-200 bg-white hover:bg-zinc-100 text-zinc-700 font-medium text-[11px] transition cursor-pointer"
              >
                Sao chép
              </button>
            </div>

            <div class="flex items-center justify-between gap-2 pt-2 border-t border-zinc-200/60">
              <div>
                <div class="text-zinc-500">Số tiền chuyển khoản</div>
                <div class="font-bold text-sm text-red-600 mt-0.5" data-testid="vietqr-amount">{{ formatPrice(order.finalAmount) }}</div>
              </div>
              <button
                type="button"
                data-testid="copy-amount"
                @click="copyText(String(Math.round(order.finalAmount)), 'số tiền')"
                class="px-2.5 py-1 rounded-lg border border-zinc-200 bg-white hover:bg-zinc-100 text-zinc-700 font-medium text-[11px] transition cursor-pointer"
              >
                Sao chép
              </button>
            </div>

            <div class="flex items-center justify-between gap-2 pt-2 border-t border-zinc-200/60">
              <div class="min-w-0">
                <div class="text-zinc-500">Nội dung chuyển khoản (bắt buộc)</div>
                <div class="font-mono font-bold text-xs text-zinc-900 mt-0.5 truncate" data-testid="vietqr-content">{{ order.orderCode }}</div>
              </div>
              <button
                type="button"
                data-testid="copy-content"
                @click="copyText(order.orderCode, 'nội dung chuyển khoản')"
                class="px-2.5 py-1 rounded-lg border border-zinc-200 bg-white hover:bg-zinc-100 text-zinc-700 font-medium text-[11px] transition cursor-pointer shrink-0"
              >
                Sao chép
              </button>
            </div>
          </div>

          <div class="p-2.5 rounded-xl bg-amber-50 border border-amber-200/80 text-[11px] text-amber-800">
            Lưu ý: Giữ nguyên nội dung chuyển khoản <span class="font-mono font-bold">{{ order.orderCode }}</span> để hệ thống tự động đối soát qua Webhook.
          </div>

          <div
            v-if="!isExpired"
            data-testid="vietqr-waiting-banner"
            class="p-2.5 rounded-xl bg-blue-50/80 border border-blue-200/80 text-[11px] text-blue-800 flex items-center gap-2"
          >
            <span class="inline-block w-2 h-2 rounded-full bg-blue-600 animate-ping shrink-0"></span>
            <span>Đang chờ nhận chuyển khoản... Trạng thái đơn hàng sẽ tự động cập nhật sau 3–5 giây khi giao dịch hoàn tất.</span>
          </div>
        </div>
      </div>

      <!-- Footer Actions -->
      <div class="flex flex-col sm:flex-row items-center justify-end gap-2.5 pt-3 border-t border-zinc-100">
        <router-link
          v-if="showOrdersLink"
          to="/orders"
          @click="emit('close')"
          class="w-full sm:w-auto text-center px-4 py-2.5 rounded-xl bg-zinc-100 hover:bg-zinc-200/80 text-zinc-700 text-xs font-medium transition"
        >
          {{ isPaid ? 'Xem lịch sử đơn hàng' : 'Thanh toán sau (Xem đơn hàng)' }}
        </router-link>
        <button
          v-else
          type="button"
          @click="emit('close')"
          class="w-full sm:w-auto px-4 py-2.5 rounded-xl bg-zinc-100 hover:bg-zinc-200/80 text-zinc-700 text-xs font-medium transition cursor-pointer"
        >
          {{ isPaid ? 'Đóng' : 'Thanh toán sau' }}
        </button>

        <button
          v-if="!isPaid"
          type="button"
          data-testid="vietqr-check-btn"
          :disabled="checkingStatus || isExpired"
          @click="handleCheckPaymentStatus(true)"
          class="w-full sm:w-auto px-5 py-2.5 rounded-xl bg-zinc-900 hover:bg-zinc-800 text-white text-xs font-semibold transition disabled:opacity-50 cursor-pointer"
        >
          {{ checkingStatus ? 'Đang kiểm tra...' : 'Kiểm tra trạng thái thanh toán' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onBeforeUnmount } from 'vue'
import { toast } from 'vue-sonner'
import { orderApi } from '@/api/orderApi'
import type { Order } from '@/types'

const props = withDefaults(
  defineProps<{
    order: Order | null
    showOrdersLink?: boolean
  }>(),
  {
    showOrdersLink: true
  }
)

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'confirmed', order: Order): void
}>()

const bankId = import.meta.env.VITE_VIETQR_BANK_ID || 'VCB'
const bankName = import.meta.env.VITE_VIETQR_BANK_NAME || 'Vietcombank'
const accountNo = import.meta.env.VITE_VIETQR_ACCOUNT_NO || '0000000001'
const accountName = import.meta.env.VITE_VIETQR_ACCOUNT_NAME || 'Techstore Ecommerce'

const DEFAULT_EXPIRE_SECONDS = 15 * 60
const POLL_INTERVAL_MS = 4000

const qrLoading = ref(true)
const qrError = ref(false)
const checkingStatus = ref(false)
const remainingSeconds = ref(DEFAULT_EXPIRE_SECONDS)
let countdownTimer: ReturnType<typeof setInterval> | null = null
let pollingTimer: ReturnType<typeof setInterval> | null = null

const isPaid = computed(() => props.order?.paymentStatus === 'PAID')
const isExpired = computed(() => remainingSeconds.value <= 0 || props.order?.orderStatus === 'CANCELLED')

const formattedCountdown = computed(() => {
  const mins = Math.floor(remainingSeconds.value / 60)
  const secs = remainingSeconds.value % 60
  return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
})

const countdownBadgeClass = computed(() => {
  if (isExpired.value || remainingSeconds.value <= 60) {
    return 'bg-red-50 text-red-700 border-red-200'
  }
  return 'bg-amber-50 text-amber-800 border-amber-200'
})

const computeInitialRemainingSeconds = (order: Order | null): number => {
  if (!order) return 0
  if (order.orderStatus === 'CANCELLED') return 0
  if (order.paymentExpiresAt) {
    const expMs = new Date(order.paymentExpiresAt).getTime()
    if (!Number.isNaN(expMs)) {
      return Math.max(0, Math.floor((expMs - Date.now()) / 1000))
    }
  }
  if (order.createdAt) {
    const createdMs = new Date(order.createdAt).getTime()
    if (!Number.isNaN(createdMs)) {
      const fallbackExpMs = createdMs + DEFAULT_EXPIRE_SECONDS * 1000
      const diff = Math.floor((fallbackExpMs - Date.now()) / 1000)
      if (diff > 0 && diff <= DEFAULT_EXPIRE_SECONDS) {
        return diff
      }
    }
  }
  return DEFAULT_EXPIRE_SECONDS
}

const stopTimers = () => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
  }
}

const startTimers = () => {
  stopTimers()
  if (!props.order || isPaid.value) return
  remainingSeconds.value = computeInitialRemainingSeconds(props.order)
  if (remainingSeconds.value <= 0) return

  countdownTimer = setInterval(() => {
    if (remainingSeconds.value > 0) {
      remainingSeconds.value--
    } else {
      stopTimers()
    }
  }, 1000)

  pollingTimer = setInterval(() => {
    if (!isPaid.value && !isExpired.value && !checkingStatus.value) {
      void handleCheckPaymentStatus(false)
    }
  }, POLL_INTERVAL_MS)
}

onBeforeUnmount(stopTimers)

const vietQrUrl = computed(() => {
  if (!props.order) return ''
  const amount = Math.max(0, Math.round(props.order.finalAmount || 0))
  const addInfo = encodeURIComponent(props.order.orderCode)
  const accName = encodeURIComponent(accountName)
  return `https://img.vietqr.io/image/${bankId}-${accountNo}-compact2.png?amount=${amount}&addInfo=${addInfo}&accountName=${accName}`
})

watch(
  () => [props.order?.id, props.order?.paymentStatus, props.order?.orderStatus],
  ([orderId, paymentStatus, orderStatus]) => {
    qrLoading.value = true
    qrError.value = false
    if (orderId && paymentStatus !== 'PAID' && orderStatus !== 'CANCELLED') {
      startTimers()
    } else {
      stopTimers()
    }
  },
  { immediate: true }
)

const handleQrError = () => {
  qrLoading.value = false
  qrError.value = true
}

const formatPrice = (value?: number | null): string => {
  if (!value || isNaN(value)) return '0 ₫'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}

const copyText = async (value: string, label: string) => {
  try {
    await navigator.clipboard.writeText(value)
    toast.success(`Đã sao chép ${label}`)
  } catch {
    toast.error(`Không thể sao chép ${label}`)
  }
}

const handleCheckPaymentStatus = async (notifyIfPending = true) => {
  if (!props.order) return
  if (notifyIfPending) {
    checkingStatus.value = true
  }
  try {
    const res = await orderApi.getOrderById(props.order.id)
    if (res.success && res.data) {
      if (res.data.paymentStatus === 'PAID') {
        stopTimers()
        toast.success('Đã nhận chuyển khoản VietQR thành công!')
        emit('confirmed', res.data)
      } else if (res.data.orderStatus === 'CANCELLED') {
        remainingSeconds.value = 0
        stopTimers()
        if (notifyIfPending) {
          toast.error('Đơn hàng đã hết hạn thanh toán và bị hủy.')
        }
        emit('confirmed', res.data)
      } else if (notifyIfPending) {
        toast.info('Hệ thống chưa ghi nhận giao dịch, vui lòng đợi thêm giây lát.')
      }
    }
  } catch (e: any) {
    if (notifyIfPending) {
      toast.error(e?.message || 'Không thể kiểm tra trạng thái thanh toán')
    }
  } finally {
    if (notifyIfPending) {
      checkingStatus.value = false
    }
  }
}
</script>
