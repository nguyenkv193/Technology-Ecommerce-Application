<template>
  <Teleport to="body">
    <Transition name="auth-modal">
      <div 
        v-if="authStore.isAuthModalOpen"
        class="fixed inset-0 z-[100] flex items-center justify-center p-4 sm:p-6 overflow-y-auto"
        @keydown.esc="handleClose"
        tabindex="-1"
      >
        <!-- Backdrop Blur -->
        <div 
          class="fixed inset-0 bg-black/60 backdrop-blur-sm transition-opacity" 
          @click="handleClose"
        ></div>

        <!-- Modal Dialog Card -->
        <div 
          class="relative w-full max-w-[430px] my-auto bg-white rounded-3xl shadow-2xl border border-zinc-200/80 p-6 sm:p-8 overflow-hidden z-10 text-zinc-900 select-none animate-in"
          @click.stop
        >
          <!-- Close Button -->
          <button 
            type="button"
            @click="handleClose"
            class="absolute top-4 right-4 p-2 rounded-full text-zinc-400 hover:text-zinc-700 hover:bg-zinc-100 transition-colors cursor-pointer"
            title="Đóng (Esc)"
          >
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          </button>

          <!-- Top Brand Header -->
          <div class="text-center mb-5">
            <div class="inline-flex items-center justify-center mb-3">
              <img src="/logo.png" alt="TechStore Logo" class="h-9 w-auto object-contain" />
            </div>
            <h3 class="text-xl font-extrabold tracking-tight text-zinc-900">
              {{ isLogin ? 'Chào mừng trở lại' : 'Tạo tài khoản TechStore' }}
            </h3>
            <p class="text-xs text-zinc-500 mt-1 max-w-[280px] mx-auto">
              {{ isLogin ? 'Đăng nhập để tiếp tục trải nghiệm mua sắm công nghệ đỉnh cao.' : 'Gia nhập cộng đồng TechStore để nhận các ưu đãi công nghệ độc quyền.' }}
            </p>
          </div>

          <!-- Apple-Style Tab Switcher -->
          <div class="p-1 bg-zinc-100 rounded-2xl mb-4 flex border border-zinc-200/60">
            <button
              type="button"
              @click="switchTab(true)"
              :class="isLogin ? 'bg-white text-zinc-900 shadow-sm font-semibold' : 'text-zinc-500 hover:text-zinc-800 font-medium'"
              class="flex-1 py-1.5 text-xs rounded-xl transition-all duration-200 cursor-pointer text-center"
            >
              Đăng nhập
            </button>
            <button
              type="button"
              @click="switchTab(false)"
              :class="!isLogin ? 'bg-white text-zinc-900 shadow-sm font-semibold' : 'text-zinc-500 hover:text-zinc-800 font-medium'"
              class="flex-1 py-1.5 text-xs rounded-xl transition-all duration-200 cursor-pointer text-center"
            >
              Đăng ký
            </button>
          </div>

          <!-- Quick 1-Click Demo Accounts (Only visible in Login tab) -->
          <div v-if="isLogin" class="mb-4 p-2.5 rounded-2xl bg-zinc-50 border border-zinc-200/70 text-xs">
            <div class="text-zinc-400 text-[10px] font-medium mb-1.5 flex items-center justify-between">
              <span>Đăng nhập thử nghiệm (1-Click):</span>
              <span class="text-emerald-600 font-semibold text-[9px] uppercase tracking-wider">Sẵn sàng</span>
            </div>
            <div class="flex gap-2">
              <button 
                type="button" 
                @click="fillDemo('ADMIN')"
                class="flex-1 py-1.5 px-2.5 rounded-xl bg-white hover:bg-zinc-100/80 text-zinc-800 border border-zinc-200 text-[11px] font-medium transition shadow-2xs cursor-pointer text-center flex items-center justify-center gap-1.5"
              >
                <span>👑</span>
                <span class="font-semibold">Admin</span>
              </button>
              <button 
                type="button" 
                @click="fillDemo('USER')"
                class="flex-1 py-1.5 px-2.5 rounded-xl bg-white hover:bg-zinc-100/80 text-zinc-800 border border-zinc-200 text-[11px] font-medium transition shadow-2xs cursor-pointer text-center flex items-center justify-center gap-1.5"
              >
                <span>👤</span>
                <span class="font-semibold">Khách hàng</span>
              </button>
            </div>
          </div>

          <!-- Error Alert Message -->
          <div v-if="authError" class="mb-4 p-2.5 rounded-xl bg-red-50 border border-red-200 flex items-start gap-2 text-red-600 text-xs animate-shake">
            <svg class="w-4 h-4 shrink-0 mt-0.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10" />
              <line x1="12" y1="8" x2="12" y2="12" />
              <line x1="12" y1="16" x2="12.01" y2="16" />
            </svg>
            <div class="flex-1 text-[11px] leading-snug">{{ authError }}</div>
          </div>

          <!-- Login Form -->
          <form v-if="isLogin" @submit.prevent="handleLogin" class="space-y-3">
            <div>
              <label class="block text-[11px] font-semibold text-zinc-700 mb-1">Địa chỉ Email</label>
              <input
                v-model="loginForm.email"
                type="email"
                required
                autocomplete="email"
                class="w-full bg-zinc-50/70 hover:bg-zinc-50 focus:bg-white border border-zinc-200 focus:border-zinc-900 rounded-xl px-3.5 py-2.5 text-xs text-zinc-900 placeholder:text-zinc-400 focus:outline-none transition shadow-2xs"
                placeholder="name@example.com"
              />
            </div>

            <div>
              <div class="flex items-center justify-between mb-1">
                <label class="text-[11px] font-semibold text-zinc-700">Mật khẩu</label>
                <a href="#" @click.prevent="alertForgotPassword" class="text-[10px] text-zinc-500 hover:text-zinc-900 transition font-medium">Quên mật khẩu?</a>
              </div>
              <div class="relative">
                <input
                  v-model="loginForm.password"
                  :type="showPassword ? 'text' : 'password'"
                  required
                  autocomplete="current-password"
                  class="w-full bg-zinc-50/70 hover:bg-zinc-50 focus:bg-white border border-zinc-200 focus:border-zinc-900 rounded-xl px-3.5 py-2.5 pr-10 text-xs text-zinc-900 placeholder:text-zinc-400 focus:outline-none transition shadow-2xs"
                  placeholder="••••••••"
                />
                <button
                  type="button"
                  @click="showPassword = !showPassword"
                  class="absolute inset-y-0 right-0 pr-3 flex items-center text-zinc-400 hover:text-zinc-700 transition cursor-pointer"
                  tabindex="-1"
                >
                  <svg v-if="!showPassword" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                  </svg>
                  <svg v-else class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l18 18" />
                  </svg>
                </button>
              </div>
            </div>

            <button
              type="submit"
              :disabled="authLoading"
              class="w-full mt-2 py-2.5 bg-zinc-900 hover:bg-black active:scale-[0.99] text-white font-medium rounded-xl text-xs shadow-md shadow-zinc-900/10 transition-all duration-150 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 cursor-pointer"
            >
              <svg v-if="authLoading" class="animate-spin h-3.5 w-3.5 text-white" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <span>{{ authLoading ? 'Đang xác thực...' : 'Đăng nhập vào hệ thống' }}</span>
            </button>
          </form>

          <!-- Register Form -->
          <form v-else @submit.prevent="handleRegister" class="space-y-2.5">
            <div>
              <label class="block text-[11px] font-semibold text-zinc-700 mb-1">Họ và tên</label>
              <input
                v-model="registerForm.fullName"
                type="text"
                required
                class="w-full bg-zinc-50/70 hover:bg-zinc-50 focus:bg-white border border-zinc-200 focus:border-zinc-900 rounded-xl px-3 py-2 text-xs text-zinc-900 placeholder:text-zinc-400 focus:outline-none transition shadow-2xs"
                placeholder="Nguyễn Văn A"
              />
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-zinc-700 mb-1">Địa chỉ Email</label>
              <input
                v-model="registerForm.email"
                type="email"
                required
                autocomplete="email"
                class="w-full bg-zinc-50/70 hover:bg-zinc-50 focus:bg-white border border-zinc-200 focus:border-zinc-900 rounded-xl px-3 py-2 text-xs text-zinc-900 placeholder:text-zinc-400 focus:outline-none transition shadow-2xs"
                placeholder="name@example.com"
              />
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-zinc-700 mb-1">Số điện thoại</label>
              <input
                v-model="registerForm.phoneNumber"
                type="tel"
                class="w-full bg-zinc-50/70 hover:bg-zinc-50 focus:bg-white border border-zinc-200 focus:border-zinc-900 rounded-xl px-3 py-2 text-xs text-zinc-900 placeholder:text-zinc-400 focus:outline-none transition shadow-2xs"
                placeholder="0912345678"
              />
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-zinc-700 mb-1">Mật khẩu (tối thiểu 6 ký tự)</label>
              <div class="relative">
                <input
                  v-model="registerForm.password"
                  :type="showPassword ? 'text' : 'password'"
                  required
                  minlength="6"
                  autocomplete="new-password"
                  class="w-full bg-zinc-50/70 hover:bg-zinc-50 focus:bg-white border border-zinc-200 focus:border-zinc-900 rounded-xl px-3 py-2 pr-10 text-xs text-zinc-900 placeholder:text-zinc-400 focus:outline-none transition shadow-2xs"
                  placeholder="••••••••"
                />
                <button
                  type="button"
                  @click="showPassword = !showPassword"
                  class="absolute inset-y-0 right-0 pr-3 flex items-center text-zinc-400 hover:text-zinc-700 transition cursor-pointer"
                  tabindex="-1"
                >
                  <svg v-if="!showPassword" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                  </svg>
                  <svg v-else class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l18 18" />
                  </svg>
                </button>
              </div>
            </div>

            <button
              type="submit"
              :disabled="authLoading"
              class="w-full mt-2 py-2.5 bg-zinc-900 hover:bg-black active:scale-[0.99] text-white font-medium rounded-xl text-xs shadow-md shadow-zinc-900/10 transition-all duration-150 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 cursor-pointer"
            >
              <svg v-if="authLoading" class="animate-spin h-3.5 w-3.5 text-white" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <span>{{ authLoading ? 'Đang tạo tài khoản...' : 'Tạo tài khoản TechStore' }}</span>
            </button>
          </form>

          <!-- Toggle Mode Link -->
          <div class="mt-4 pt-3 border-t border-zinc-100 text-center text-xs text-zinc-500">
            <span v-if="isLogin">Chưa có tài khoản? </span>
            <span v-else>Đã có tài khoản? </span>
            <button 
              type="button" 
              @click="switchTab(!isLogin)" 
              class="font-semibold text-zinc-900 hover:underline cursor-pointer ml-1"
            >
              {{ isLogin ? 'Đăng ký ngay' : 'Đăng nhập ngay' }}
            </button>
          </div>

          <!-- Bottom Security Badge -->
          <div class="mt-3 flex items-center justify-center gap-1.5 text-[10px] text-zinc-400">
            <svg class="w-3 h-3 text-emerald-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
            </svg>
            <span>Bảo mật chuẩn Enterprise • SSL 256-bit</span>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { useWishlistStore } from '@/stores/wishlist'
import type { LoginRequest, RegisterRequest } from '@/types'

const authStore = useAuthStore()
const cartStore = useCartStore()
const wishlistStore = useWishlistStore()

const showPassword = ref<boolean>(false)
const authLoading = ref<boolean>(false)
const authError = ref<string>('')

const loginForm = ref<LoginRequest>({ email: '', password: '' })
const registerForm = ref<RegisterRequest>({ fullName: '', email: '', phoneNumber: '', password: '' })

const isLogin = computed(() => authStore.authModalMode === 'login')

const switchTab = (toLogin: boolean) => {
  authStore.authModalMode = toLogin ? 'login' : 'register'
  authError.value = ''
}

const handleClose = () => {
  authStore.closeAuthModal()
  authError.value = ''
}

const fillDemo = (role: 'ADMIN' | 'USER') => {
  if (role === 'ADMIN') {
    loginForm.value.email = 'admin@techstore.com'
    loginForm.value.password = 'Admin@123'
  } else {
    loginForm.value.email = 'nguyenvana@gmail.com'
    loginForm.value.password = 'Admin@123'
  }
}

const alertForgotPassword = () => {
  alert('Vui lòng liên hệ quản trị viên (admin@techstore.com) hoặc sử dụng tính năng thử nghiệm 1-click để đăng nhập.')
}

const handleAuthSuccess = async () => {
  await Promise.all([
    cartStore.fetchCart(),
    wishlistStore.fetchWishlist()
  ])

  // Execute success callback if defined
  if (authStore.onAuthSuccess) {
    const cb = authStore.onAuthSuccess
    authStore.closeAuthModal()
    cb()
  } else {
    authStore.closeAuthModal()
  }
}

const handleLogin = async () => {
  authLoading.value = true
  authError.value = ''
  try {
    const res = await authStore.login(loginForm.value)
    if (res.success) {
      await handleAuthSuccess()
    } else {
      authError.value = res.message || 'Đăng nhập không thành công, vui lòng kiểm tra lại.'
    }
  } catch (err: any) {
    authError.value = err.response?.data?.message || err.message || 'Đăng nhập không thành công, vui lòng thử lại.'
  } finally {
    authLoading.value = false
  }
}

const handleRegister = async () => {
  authLoading.value = true
  authError.value = ''
  try {
    const res = await authStore.register(registerForm.value)
    if (res.success) {
      await handleAuthSuccess()
    } else {
      authError.value = res.message || 'Đăng ký không thành công, vui lòng thử lại.'
    }
  } catch (err: any) {
    authError.value = err.response?.data?.message || err.message || 'Đăng ký không thành công, vui lòng thử lại.'
  } finally {
    authLoading.value = false
  }
}

// Lock scroll when modal is active
watch(() => authStore.isAuthModalOpen, (isOpen) => {
  if (typeof document !== 'undefined') {
    if (isOpen) {
      document.body.style.overflow = 'hidden'
    } else {
      document.body.style.overflow = ''
    }
  }
})
</script>

<style scoped>
/* Modal Fade & Scale Transition */
.auth-modal-enter-active,
.auth-modal-leave-active {
  transition: opacity 0.25s ease, transform 0.25s cubic-bezier(0.16, 1, 0.3, 1);
}

.auth-modal-enter-from,
.auth-modal-leave-to {
  opacity: 0;
}

.auth-modal-enter-from .relative,
.auth-modal-leave-to .relative {
  transform: scale(0.95) translateY(8px);
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  20%, 60% { transform: translateX(-4px); }
  40%, 80% { transform: translateX(4px); }
}

.animate-shake {
  animation: shake 0.3s ease-in-out;
}
</style>
