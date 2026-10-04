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
          class="fixed inset-0 bg-black/60 backdrop-blur-sm" 
          @click="handleClose"
        ></div>

        <!-- Modal Dialog Card -->
        <div 
          class="modal-card relative w-full max-w-[430px] my-auto bg-white rounded-3xl shadow-2xl border border-zinc-200/80 p-6 sm:p-8 overflow-hidden z-10 text-zinc-900 select-none will-change-transform"
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

          <!-- Error Alert Message -->
          <div v-if="authError" class="mb-4 p-2.5 rounded-xl bg-red-50 border border-red-200 flex items-start gap-2 text-red-600 text-xs animate-shake">
            <svg class="w-4 h-4 shrink-0 mt-0.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10" />
              <line x1="12" y1="8" x2="12" y2="12" />
              <line x1="12" y1="16" x2="12.01" y2="16" />
            </svg>
            <div class="flex-1 text-[11px] leading-snug">{{ authError }}</div>
          </div>

          <!-- Login Form with VeeValidate -->
          <form v-if="isLogin" @submit.prevent="onLoginSubmit" class="space-y-3" novalidate>
            <div>
              <label class="block text-[11px] font-semibold text-zinc-700 mb-1">Địa chỉ Email</label>
              <input
                v-model="loginEmail"
                v-bind="loginEmailAttrs"
                @input="clearAuthError"
                type="email"
                autocomplete="email"
                :class="loginErrors.email ? 'border-red-400 focus:border-red-500 ring-1 ring-red-100 bg-red-50/20' : 'border-zinc-200 focus:border-zinc-900 bg-zinc-50/70 hover:bg-zinc-50 focus:bg-white'"
                class="w-full border rounded-xl px-3.5 py-2.5 text-xs text-zinc-900 placeholder:text-zinc-400 focus:outline-none transition shadow-2xs"
                placeholder="name@example.com"
              />
              <p v-if="loginErrors.email" class="text-[11px] text-red-500 font-medium flex items-center gap-1 mt-1">
                <svg class="w-3 h-3 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                <span>{{ loginErrors.email }}</span>
              </p>
            </div>

            <div>
              <div class="flex items-center justify-between mb-1">
                <label class="text-[11px] font-semibold text-zinc-700">Mật khẩu</label>
                <a href="#" @click.prevent="alertForgotPassword" class="text-[10px] text-zinc-500 hover:text-zinc-900 transition font-medium">Quên mật khẩu?</a>
              </div>
              <div class="relative">
                <input
                  v-model="loginPassword"
                  v-bind="loginPasswordAttrs"
                  @input="clearAuthError"
                  :type="showPassword ? 'text' : 'password'"
                  autocomplete="current-password"
                  :class="loginErrors.password ? 'border-red-400 focus:border-red-500 ring-1 ring-red-100 bg-red-50/20' : 'border-zinc-200 focus:border-zinc-900 bg-zinc-50/70 hover:bg-zinc-50 focus:bg-white'"
                  class="w-full border rounded-xl px-3.5 py-2.5 pr-10 text-xs text-zinc-900 placeholder:text-zinc-400 focus:outline-none transition shadow-2xs"
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
              <p v-if="loginErrors.password" class="text-[11px] text-red-500 font-medium flex items-center gap-1 mt-1">
                <svg class="w-3 h-3 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                <span>{{ loginErrors.password }}</span>
              </p>
            </div>

            <!-- Agreement Checkbox -->
            <label class="flex items-start gap-2 cursor-pointer select-none text-[11px] text-zinc-500 pt-0.5">
              <input
                type="checkbox"
                v-model="agreeTerms"
                class="mt-0.5 h-3.5 w-3.5 rounded border-zinc-300 text-zinc-900 focus:ring-zinc-900 cursor-pointer accent-zinc-900 shrink-0"
              />
              <span class="leading-relaxed">
                Bằng việc tiếp tục, tôi xác nhận đã đủ điều kiện và đồng ý với 
                <a href="#" @click.prevent.stop="alertTerms" class="text-zinc-800 font-medium underline hover:text-black">Điều khoản sử dụng dịch vụ</a> 
                cùng 
                <a href="#" @click.prevent.stop="alertPrivacy" class="text-zinc-800 font-medium underline hover:text-black">Chính sách quyền riêng tư</a> 
                của TechStore.
              </span>
            </label>

            <button
              type="submit"
              :disabled="authLoading || !agreeTerms"
              class="w-full mt-2 py-2.5 bg-zinc-900 hover:bg-black active:scale-[0.99] text-white font-medium rounded-xl text-xs shadow-md shadow-zinc-900/10 transition-all duration-150 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 cursor-pointer"
            >
              <svg v-if="authLoading" class="animate-spin h-3.5 w-3.5 text-white" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <span>{{ authLoading ? 'Đang xác thực...' : 'Đăng nhập' }}</span>
            </button>
          </form>

          <!-- Register Form with VeeValidate -->
          <form v-else @submit.prevent="onRegisterSubmit" class="space-y-2.5" novalidate>
            <div>
              <label class="block text-[11px] font-semibold text-zinc-700 mb-1">Họ và tên</label>
              <input
                v-model="registerFullName"
                v-bind="registerFullNameAttrs"
                @input="clearAuthError"
                type="text"
                :class="registerErrors.fullName ? 'border-red-400 focus:border-red-500 ring-1 ring-red-100 bg-red-50/20' : 'border-zinc-200 focus:border-zinc-900 bg-zinc-50/70 hover:bg-zinc-50 focus:bg-white'"
                class="w-full border rounded-xl px-3 py-2 text-xs text-zinc-900 placeholder:text-zinc-400 focus:outline-none transition shadow-2xs"
                placeholder="Nguyễn Văn A"
              />
              <p v-if="registerErrors.fullName" class="text-[11px] text-red-500 font-medium flex items-center gap-1 mt-1">
                <svg class="w-3 h-3 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                <span>{{ registerErrors.fullName }}</span>
              </p>
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-zinc-700 mb-1">Địa chỉ Email</label>
              <input
                v-model="registerEmail"
                v-bind="registerEmailAttrs"
                @input="clearAuthError"
                type="email"
                autocomplete="email"
                :class="registerErrors.email ? 'border-red-400 focus:border-red-500 ring-1 ring-red-100 bg-red-50/20' : 'border-zinc-200 focus:border-zinc-900 bg-zinc-50/70 hover:bg-zinc-50 focus:bg-white'"
                class="w-full border rounded-xl px-3 py-2 text-xs text-zinc-900 placeholder:text-zinc-400 focus:outline-none transition shadow-2xs"
                placeholder="name@example.com"
              />
              <p v-if="registerErrors.email" class="text-[11px] text-red-500 font-medium flex items-center gap-1 mt-1">
                <svg class="w-3 h-3 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                <span>{{ registerErrors.email }}</span>
              </p>
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-zinc-700 mb-1">Số điện thoại (tùy chọn)</label>
              <input
                v-model="registerPhoneNumber"
                v-bind="registerPhoneNumberAttrs"
                @input="clearAuthError"
                type="tel"
                :class="registerErrors.phoneNumber ? 'border-red-400 focus:border-red-500 ring-1 ring-red-100 bg-red-50/20' : 'border-zinc-200 focus:border-zinc-900 bg-zinc-50/70 hover:bg-zinc-50 focus:bg-white'"
                class="w-full border rounded-xl px-3 py-2 text-xs text-zinc-900 placeholder:text-zinc-400 focus:outline-none transition shadow-2xs"
                placeholder="0912345678"
              />
              <p v-if="registerErrors.phoneNumber" class="text-[11px] text-red-500 font-medium flex items-center gap-1 mt-1">
                <svg class="w-3 h-3 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                <span>{{ registerErrors.phoneNumber }}</span>
              </p>
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-zinc-700 mb-1">Mật khẩu (tối thiểu 6 ký tự)</label>
              <div class="relative">
                <input
                  v-model="registerPassword"
                  v-bind="registerPasswordAttrs"
                  @input="clearAuthError"
                  :type="showPassword ? 'text' : 'password'"
                  autocomplete="new-password"
                  :class="registerErrors.password ? 'border-red-400 focus:border-red-500 ring-1 ring-red-100 bg-red-50/20' : 'border-zinc-200 focus:border-zinc-900 bg-zinc-50/70 hover:bg-zinc-50 focus:bg-white'"
                  class="w-full border rounded-xl px-3 py-2 pr-10 text-xs text-zinc-900 placeholder:text-zinc-400 focus:outline-none transition shadow-2xs"
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
              <p v-if="registerErrors.password" class="text-[11px] text-red-500 font-medium flex items-center gap-1 mt-1">
                <svg class="w-3 h-3 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                <span>{{ registerErrors.password }}</span>
              </p>
            </div>

            <!-- Agreement Checkbox -->
            <label class="flex items-start gap-2 cursor-pointer select-none text-[11px] text-zinc-500 pt-0.5">
              <input
                type="checkbox"
                v-model="agreeTerms"
                class="mt-0.5 h-3.5 w-3.5 rounded border-zinc-300 text-zinc-900 focus:ring-zinc-900 cursor-pointer accent-zinc-900 shrink-0"
              />
              <span class="leading-relaxed">
                Bằng việc tiếp tục, tôi xác nhận đã đủ điều kiện và đồng ý với 
                <a href="#" @click.prevent.stop="alertTerms" class="text-zinc-800 font-medium underline hover:text-black">Điều khoản sử dụng dịch vụ</a> 
                cùng 
                <a href="#" @click.prevent.stop="alertPrivacy" class="text-zinc-800 font-medium underline hover:text-black">Chính sách quyền riêng tư</a> 
                của TechStore.
              </span>
            </label>

            <button
              type="submit"
              :disabled="authLoading || !agreeTerms"
              class="w-full mt-2 py-2.5 bg-zinc-900 hover:bg-black active:scale-[0.99] text-white font-medium rounded-xl text-xs shadow-md shadow-zinc-900/10 transition-all duration-150 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 cursor-pointer"
            >
              <svg v-if="authLoading" class="animate-spin h-3.5 w-3.5 text-white" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <span>{{ authLoading ? 'Đang đăng ký...' : 'Đăng ký' }}</span>
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
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useForm } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import * as z from 'zod'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { useWishlistStore } from '@/stores/wishlist'
import { toast } from 'vue-sonner'

const authStore = useAuthStore()
const cartStore = useCartStore()
const wishlistStore = useWishlistStore()

const showPassword = ref<boolean>(false)
const authLoading = ref<boolean>(false)
const authError = ref<string>('')
const agreeTerms = ref<boolean>(false)

const isLogin = computed(() => authStore.authModalMode === 'login')

// ==========================================
// 1. LOGIN VALIDATION SCHEMA & FORM (ZOD + VEE-VALIDATE)
// ==========================================
const loginSchema = toTypedSchema(
  z.object({
    email: z
      .string({ required_error: 'Vui lòng nhập địa chỉ email' })
      .min(1, 'Vui lòng nhập địa chỉ email')
      .email('Địa chỉ email không đúng định dạng (VD: ten@gmail.com)'),
    password: z
      .string({ required_error: 'Vui lòng nhập mật khẩu' })
      .min(1, 'Vui lòng nhập mật khẩu')
      .min(6, 'Mật khẩu phải chứa ít nhất 6 ký tự')
  })
)

const {
  defineField: defineLoginField,
  handleSubmit: handleLoginSubmit,
  errors: loginErrors,
  resetForm: resetLoginForm
} = useForm({
  validationSchema: loginSchema,
  initialValues: {
    email: 'nguyenvana@gmail.com',
    password: 'Admin@123'
  }
})

const [loginEmail, loginEmailAttrs] = defineLoginField('email')
const [loginPassword, loginPasswordAttrs] = defineLoginField('password')

// ==========================================
// 2. REGISTER VALIDATION SCHEMA & FORM (ZOD + VEE-VALIDATE)
// ==========================================
const registerSchema = toTypedSchema(
  z.object({
    fullName: z
      .string({ required_error: 'Vui lòng nhập họ và tên' })
      .min(1, 'Vui lòng nhập họ và tên')
      .min(2, 'Họ và tên tối thiểu 2 ký tự')
      .max(50, 'Họ và tên tối đa 50 ký tự'),
    email: z
      .string({ required_error: 'Vui lòng nhập địa chỉ email' })
      .min(1, 'Vui lòng nhập địa chỉ email')
      .email('Địa chỉ email không đúng định dạng (VD: ten@gmail.com)'),
    phoneNumber: z
      .string()
      .optional()
      .refine(
        (val) => !val || /^(0|\+84)[3|5|7|8|9][0-9]{8}$/.test(val.replace(/\s+/g, '')),
        'Số điện thoại không hợp lệ (gồm 10 chữ số, đầu 03, 05, 07, 08, 09)'
      ),
    password: z
      .string({ required_error: 'Vui lòng nhập mật khẩu' })
      .min(1, 'Vui lòng nhập mật khẩu')
      .min(6, 'Mật khẩu phải chứa ít nhất 6 ký tự')
  })
)

const {
  defineField: defineRegisterField,
  handleSubmit: handleRegisterSubmit,
  errors: registerErrors,
  resetForm: resetRegisterForm
} = useForm({
  validationSchema: registerSchema,
  initialValues: {
    fullName: '',
    email: '',
    phoneNumber: '',
    password: ''
  }
})

const [registerFullName, registerFullNameAttrs] = defineRegisterField('fullName')
const [registerEmail, registerEmailAttrs] = defineRegisterField('email')
const [registerPhoneNumber, registerPhoneNumberAttrs] = defineRegisterField('phoneNumber')
const [registerPassword, registerPasswordAttrs] = defineRegisterField('password')

// ==========================================
// 3. UI HANDLERS & AUTH LIFECYCLE
// ==========================================
const clearAuthError = () => {
  if (authError.value) {
    authError.value = ''
  }
}

// Tự động xóa thông báo lỗi khi người dùng gõ vào bất kỳ ô input nào hoặc thay đổi đồng ý điều khoản
watch(
  [loginEmail, loginPassword, registerFullName, registerEmail, registerPhoneNumber, registerPassword, agreeTerms],
  clearAuthError
)

const switchTab = (toLogin: boolean) => {
  authStore.authModalMode = toLogin ? 'login' : 'register'
  authError.value = ''
}

const handleClose = () => {
  authStore.closeAuthModal()
  authError.value = ''
  agreeTerms.value = false
}

const alertForgotPassword = () => {
  alert('Vui lòng liên hệ bộ phận hỗ trợ khách hàng (support@techstore.com) để được cấp lại mật khẩu.')
}

const alertTerms = () => {
  alert('Điều khoản sử dụng dịch vụ TechStore: Cam kết cung cấp sản phẩm công nghệ chính hãng, bảo hành minh bạch và bảo đảm đầy đủ quyền lợi khách hàng.')
}

const alertPrivacy = () => {
  alert('Chính sách quyền riêng tư TechStore: Bảo mật dữ liệu cá nhân và thông tin giao dịch theo tiêu chuẩn an toàn.')
}

const handleAuthSuccess = async () => {
  await Promise.all([
    cartStore.fetchCart(),
    wishlistStore.fetchWishlist()
  ])

  if (authStore.onAuthSuccess) {
    const cb = authStore.onAuthSuccess
    authStore.closeAuthModal()
    cb()
  } else {
    authStore.closeAuthModal()
  }
}

const onLoginSubmit = handleLoginSubmit(async (values) => {
  if (!agreeTerms.value) {
    authError.value = 'Vui lòng xác nhận đồng ý với Điều khoản sử dụng dịch vụ & Chính sách quyền riêng tư để tiếp tục.'
    toast.warning('Vui lòng đồng ý với điều khoản & chính sách')
    return
  }
  authLoading.value = true
  authError.value = ''
  try {
    const res = await authStore.login(values)
    if (res.success) {
      await handleAuthSuccess()
      toast.success('Đăng nhập thành công')
    } else {
      authError.value = res.message || 'Đăng nhập không thành công, vui lòng kiểm tra lại.'
      toast.error(authError.value)
    }
  } catch (err: any) {
    authError.value = err.response?.data?.message || err.message || 'Đăng nhập không thành công, vui lòng thử lại.'
    toast.error(authError.value)
  } finally {
    authLoading.value = false
  }
})

const onRegisterSubmit = handleRegisterSubmit(async (values) => {
  if (!agreeTerms.value) {
    authError.value = 'Vui lòng xác nhận đồng ý với Điều khoản sử dụng dịch vụ & Chính sách quyền riêng tư để tiếp tục.'
    toast.warning('Vui lòng đồng ý với điều khoản & chính sách')
    return
  }
  authLoading.value = true
  authError.value = ''
  try {
    const res = await authStore.register({
      fullName: values.fullName,
      email: values.email,
      password: values.password,
      phoneNumber: values.phoneNumber || undefined
    })
    if (res.success) {
      await handleAuthSuccess()
      toast.success('Đăng ký tài khoản thành công')
    } else {
      authError.value = res.message || 'Đăng ký không thành công, vui lòng thử lại.'
      toast.error(authError.value)
    }
  } catch (err: any) {
    authError.value = err.response?.data?.message || err.message || 'Đăng ký không thành công, vui lòng thử lại.'
    toast.error(authError.value)
  } finally {
    authLoading.value = false
  }
})

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
/* Container & Backdrop transitions */
.auth-modal-enter-active,
.auth-modal-leave-active {
  transition: opacity 0.22s ease;
}

/* Modal Card scale + fade transitions with Apple spring curve */
.auth-modal-enter-active .modal-card,
.auth-modal-leave-active .modal-card {
  transition: transform 0.25s cubic-bezier(0.16, 1, 0.3, 1), opacity 0.22s ease;
}

.auth-modal-enter-from,
.auth-modal-leave-to {
  opacity: 0;
}

.auth-modal-enter-from .modal-card,
.auth-modal-leave-to .modal-card {
  opacity: 0;
  transform: scale(0.95) translateY(12px);
}

.auth-modal-enter-to .modal-card,
.auth-modal-leave-from .modal-card {
  opacity: 1;
  transform: scale(1) translateY(0);
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
