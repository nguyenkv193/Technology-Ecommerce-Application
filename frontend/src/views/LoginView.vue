<template>
  <div class="h-screen w-full flex flex-col lg:flex-row bg-[#fafafc] overflow-hidden select-none">
    <!-- LEFT COLUMN: High-End Flagship Showcase Slider (58% on Desktop, hidden on mobile) -->
    <div 
      class="hidden lg:flex lg:w-7/12 xl:w-7/12 h-screen relative overflow-hidden bg-zinc-950 text-white flex-col justify-between p-8 xl:p-12 shrink-0"
      @mouseenter="stopTimer"
      @mouseleave="startTimer"
    >
      <!-- Background Image Slides with Smooth Fade & Ken-Burns Zoom -->
      <div class="absolute inset-0 z-0 pointer-events-none">
        <TransitionGroup name="fade-kenburns">
          <div 
            v-for="(slide, idx) in slides" 
            :key="slide.id"
            v-show="currentSlideIndex === idx"
            class="absolute inset-0 bg-cover bg-center transition-all duration-1000 ease-out"
            :style="{ backgroundImage: `url('${slide.image}')` }"
          ></div>
        </TransitionGroup>
      </div>

      <!-- Dark Gradient Overlay for high-end contrast -->
      <div class="absolute inset-0 bg-gradient-to-t from-black/95 via-black/50 to-black/40 z-10 pointer-events-none"></div>

      <!-- Top Header on Slider Side -->
      <div class="relative z-20 flex items-center justify-between">
        <router-link to="/" class="flex items-center gap-3 group">
          <div class="h-11 px-2.5 py-1 rounded-xl bg-white flex items-center justify-center shadow-lg shadow-white/10 group-hover:scale-105 transition-transform duration-300">
            <img src="/logo.png" alt="TechStore Logo" class="h-9 w-auto object-contain" />
          </div>
        </router-link>

        <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-white/10 backdrop-blur-md border border-white/15 text-zinc-300 text-xs font-medium">
          <span class="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse"></span>
          <span>Hệ Thống Xác Thực Chuẩn Enterprise</span>
        </div>
      </div>

      <!-- Bottom Slide Content -->
      <div class="relative z-20 space-y-4 max-w-xl">
        <!-- Text with Smooth Slide-Fade Transition -->
        <Transition name="slide-fade" mode="out-in">
          <div :key="currentSlideIndex" class="space-y-2.5">
            <div class="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full bg-white/15 border border-white/25 text-white text-[11px] font-medium backdrop-blur-md">
              <span>{{ currentSlide.badge }}</span>
            </div>
            <h2 class="text-2xl xl:text-3xl font-extrabold tracking-tight text-white leading-tight">
              {{ currentSlide.title }}
            </h2>
            <p class="text-xs xl:text-sm text-zinc-300 leading-relaxed font-light">
              {{ currentSlide.description }}
            </p>
          </div>
        </Transition>

        <!-- Slider Controls: Dots & Prev/Next Arrows -->
        <div class="flex items-center justify-between pt-4 border-t border-white/15">
          <!-- Slide Progress Bars -->
          <div class="flex items-center gap-2">
            <button 
              v-for="(_, idx) in slides" 
              :key="idx" 
              @click="goToSlide(idx)"
              class="h-1.5 rounded-full transition-all duration-500 cursor-pointer"
              :class="currentSlideIndex === idx ? 'w-8 bg-white' : 'w-2 bg-white/30 hover:bg-white/60'"
              :title="`Slide ${idx + 1}`"
            ></button>
          </div>

          <!-- Arrow Controls -->
          <div class="flex items-center gap-2">
            <button 
              @click="prevSlide" 
              class="w-8 h-8 rounded-lg bg-white/10 hover:bg-white/20 border border-white/15 backdrop-blur-md flex items-center justify-center text-white transition active:scale-95 cursor-pointer"
              title="Slide trước"
            >
              <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M15 18l-6-6 6-6"/></svg>
            </button>
            <button 
              @click="nextSlide" 
              class="w-8 h-8 rounded-lg bg-white/10 hover:bg-white/20 border border-white/15 backdrop-blur-md flex items-center justify-center text-white transition active:scale-95 cursor-pointer"
              title="Slide kế tiếp"
            >
              <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M9 18l6-6-6-6"/></svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- RIGHT COLUMN: Auth Form (Locked to 100vh with 0 Vertical Scroll) -->
    <div class="w-full lg:w-5/12 xl:w-5/12 h-screen bg-[#fafafc] flex flex-col justify-between p-5 sm:p-7 xl:p-9 overflow-hidden">
      <!-- Top Bar: Mobile Logo & Back Button -->
      <div class="flex items-center justify-between w-full shrink-0">
        <!-- Mobile Logo (shown only on mobile) -->
        <router-link to="/" class="lg:hidden flex items-center gap-2">
          <img src="/logo.png" alt="TechStore Logo" class="h-8 w-auto object-contain" />
        </router-link>

        <router-link 
          to="/" 
          class="ml-auto inline-flex items-center gap-1.5 text-xs font-medium text-zinc-500 hover:text-zinc-900 transition-colors py-1 px-2.5 rounded-lg hover:bg-zinc-100 border border-zinc-200/60"
        >
          <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><path d="M19 12H5M12 19l-7-7 7-7"/></svg>
          <span>Về trang chủ</span>
        </router-link>
      </div>

      <!-- Center Form Wrapper (Centered Vertically with my-auto) -->
      <div class="w-full max-w-sm mx-auto my-auto py-1 shrink">
        <!-- Heading -->
        <div class="mb-3.5 text-left">
          <h1 class="text-xl sm:text-2xl font-extrabold tracking-tight text-zinc-900">
            {{ isLogin ? 'Chào mừng trở lại' : 'Tạo tài khoản mới' }}
          </h1>
          <p class="text-xs text-zinc-500 mt-1">
            {{ isLogin ? 'Đăng nhập để tiếp tục trải nghiệm mua sắm công nghệ tinh gọn.' : 'Gia nhập cộng đồng TechStore để sở hữu các thiết bị đỉnh cao.' }}
          </p>
        </div>

        <!-- Segmented Tab Switcher (Apple Style) -->
        <div class="p-1 bg-zinc-200/70 rounded-xl mb-3 flex shrink-0">
          <button
            type="button"
            @click="switchTab(true)"
            :class="isLogin ? 'bg-white text-zinc-900 shadow-2xs font-semibold' : 'text-zinc-600 hover:text-zinc-900 font-medium'"
            class="flex-1 py-1.5 text-xs rounded-lg transition-all duration-150 cursor-pointer text-center"
          >
            Đăng nhập
          </button>
          <button
            type="button"
            @click="switchTab(false)"
            :class="!isLogin ? 'bg-white text-zinc-900 shadow-2xs font-semibold' : 'text-zinc-600 hover:text-zinc-900 font-medium'"
            class="flex-1 py-1.5 text-xs rounded-lg transition-all duration-150 cursor-pointer text-center"
          >
            Đăng ký
          </button>
        </div>

        <!-- Quick 1-Click Demo Accounts (Only in Login) -->
        <div v-if="isLogin" class="mb-3 p-2 rounded-xl bg-zinc-100 border border-zinc-200/80 text-[11px] space-y-1.5">
          <div class="flex items-center justify-between text-zinc-500 text-[10px] font-medium">
            <span>Tài khoản thử nghiệm (1-Click):</span>
          </div>
          <div class="flex gap-2">
            <button 
              type="button" 
              @click="fillDemo('ADMIN')"
              class="flex-1 py-1 px-2 rounded-lg bg-white hover:bg-zinc-50 text-zinc-800 border border-zinc-200 text-[10px] font-medium transition shadow-2xs truncate cursor-pointer text-center"
            >
              👑 <span class="font-semibold">Admin</span>
            </button>
            <button 
              type="button" 
              @click="fillDemo('USER')"
              class="flex-1 py-1 px-2 rounded-lg bg-white hover:bg-zinc-50 text-zinc-800 border border-zinc-200 text-[10px] font-medium transition shadow-2xs truncate cursor-pointer text-center"
            >
              👤 <span class="font-semibold">Khách</span>
            </button>
          </div>
        </div>

        <!-- Error Alert Message -->
        <div v-if="authError" class="mb-3 p-2.5 rounded-xl bg-red-50 border border-red-200 flex items-start gap-2 text-red-600 text-xs">
          <svg class="w-3.5 h-3.5 shrink-0 mt-0.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10" />
            <line x1="12" y1="8" x2="12" y2="12" />
            <line x1="12" y1="16" x2="12.01" y2="16" />
          </svg>
          <div class="flex-1 text-[11px] leading-tight">{{ authError }}</div>
        </div>

        <!-- Login Form -->
        <form v-if="isLogin" @submit.prevent="handleLogin" class="space-y-2.5">
          <div>
            <label class="block text-[11px] font-semibold text-zinc-700 mb-1">Địa chỉ Email</label>
            <input
              v-model="loginForm.email"
              type="email"
              required
              autocomplete="email"
              class="w-full bg-white border border-zinc-200 rounded-xl px-3 py-2 text-xs text-zinc-900 placeholder:text-zinc-400 focus:outline-none focus:border-zinc-900 transition shadow-2xs"
              placeholder="name@example.com"
            />
          </div>

          <div>
            <div class="flex items-center justify-between mb-1">
              <label class="text-[11px] font-semibold text-zinc-700">Mật khẩu</label>
              <a href="#" class="text-[10px] text-zinc-500 hover:text-zinc-900 transition font-medium">Quên mật khẩu?</a>
            </div>
            <div class="relative">
              <input
                v-model="loginForm.password"
                :type="showPassword ? 'text' : 'password'"
                required
                autocomplete="current-password"
                class="w-full bg-white border border-zinc-200 rounded-xl px-3 py-2 pr-9 text-xs text-zinc-900 placeholder:text-zinc-400 focus:outline-none focus:border-zinc-900 transition shadow-2xs"
                placeholder="••••••••"
              />
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="absolute inset-y-0 right-0 pr-2.5 flex items-center text-zinc-400 hover:text-zinc-700 transition cursor-pointer"
              >
                <svg v-if="!showPassword" class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path stroke-linecap="round" stroke-linejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
                <svg v-else class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l18 18" />
                </svg>
              </button>
            </div>
          </div>

          <button
            type="submit"
            :disabled="authLoading"
            class="w-full mt-1 py-2.5 bg-zinc-900 hover:bg-black active:scale-[0.99] text-white font-medium rounded-xl text-xs shadow-xs transition-all duration-150 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 cursor-pointer"
          >
            <svg v-if="authLoading" class="animate-spin h-3.5 w-3.5 text-white" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <span>{{ authLoading ? 'Đang xác thực...' : 'Đăng nhập vào hệ thống' }}</span>
          </button>
        </form>

        <!-- Register Form -->
        <form v-else @submit.prevent="handleRegister" class="space-y-2">
          <div>
            <label class="block text-[11px] font-semibold text-zinc-700 mb-1">Họ và tên</label>
            <input
              v-model="registerForm.fullName"
              type="text"
              required
              class="w-full bg-white border border-zinc-200 rounded-xl px-3 py-1.5 text-xs text-zinc-900 placeholder:text-zinc-400 focus:outline-none focus:border-zinc-900 transition shadow-2xs"
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
              class="w-full bg-white border border-zinc-200 rounded-xl px-3 py-1.5 text-xs text-zinc-900 placeholder:text-zinc-400 focus:outline-none focus:border-zinc-900 transition shadow-2xs"
              placeholder="name@example.com"
            />
          </div>

          <div>
            <label class="block text-[11px] font-semibold text-zinc-700 mb-1">Số điện thoại</label>
            <input
              v-model="registerForm.phoneNumber"
              type="tel"
              class="w-full bg-white border border-zinc-200 rounded-xl px-3 py-1.5 text-xs text-zinc-900 placeholder:text-zinc-400 focus:outline-none focus:border-zinc-900 transition shadow-2xs"
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
                class="w-full bg-white border border-zinc-200 rounded-xl px-3 py-1.5 pr-9 text-xs text-zinc-900 placeholder:text-zinc-400 focus:outline-none focus:border-zinc-900 transition shadow-2xs"
                placeholder="••••••••"
              />
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="absolute inset-y-0 right-0 pr-2.5 flex items-center text-zinc-400 hover:text-zinc-700 transition cursor-pointer"
              >
                <svg v-if="!showPassword" class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path stroke-linecap="round" stroke-linejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
                <svg v-else class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l18 18" />
                </svg>
              </button>
            </div>
          </div>

          <button
            type="submit"
            :disabled="authLoading"
            class="w-full mt-1 py-2.5 bg-zinc-900 hover:bg-black active:scale-[0.99] text-white font-medium rounded-xl text-xs shadow-xs transition-all duration-150 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 cursor-pointer"
          >
            <svg v-if="authLoading" class="animate-spin h-3.5 w-3.5 text-white" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <span>{{ authLoading ? 'Đang tạo tài khoản...' : 'Tạo tài khoản TechStore' }}</span>
          </button>
        </form>

        <!-- Toggle Switch Text -->
        <div class="mt-3 text-center text-xs text-zinc-500">
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

      <!-- Bottom Security & Terms Footer -->
      <div class="pt-3 border-t border-zinc-200/60 flex items-center justify-between text-[10px] text-zinc-400 shrink-0">
        <div class="flex items-center gap-1.5">
          <svg class="w-3 h-3 text-emerald-600 shrink-0" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
            <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
          </svg>
          <span>SSL 256-bit Encrypted</span>
        </div>
        <div>
          © 2026 TechStore Inc.
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { useWishlistStore } from '@/stores/wishlist'
import type { LoginRequest, RegisterRequest } from '@/types'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const cartStore = useCartStore()
const wishlistStore = useWishlistStore()

const isLogin = ref<boolean>(true)
const showPassword = ref<boolean>(false)
const authLoading = ref<boolean>(false)
const authError = ref<string>('')

const loginForm = ref<LoginRequest>({ email: '', password: '' })
const registerForm = ref<RegisterRequest>({ fullName: '', email: '', phoneNumber: '', password: '' })

// Showcase Slider Data
interface AuthSlide {
  id: number
  badge: string
  title: string
  description: string
  image: string
}

const slides = ref<AuthSlide[]>([
  {
    id: 1,
    badge: 'Flagship Ecosystem',
    title: 'Hệ sinh thái công nghệ đỉnh cao thế hệ mới',
    description: 'Khám phá các thiết bị Flagship hàng đầu: MacBook M3 Pro, iPhone 16 Pro Max và máy trạm đồ họa chuyên nghiệp với trải nghiệm mua sắm tối giản.',
    image: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=1600&auto=format&fit=crop&q=80'
  },
  {
    id: 2,
    badge: 'Titanium Engineering',
    title: 'Thiết kế tinh xảo, hiệu năng vượt bậc',
    description: 'Trải nghiệm sức mạnh xử lý hàng đầu kết hợp cùng vật liệu titan chuẩn hàng không vũ trụ và màn hình OLED siêu nét.',
    image: 'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=1600&auto=format&fit=crop&q=80'
  },
  {
    id: 3,
    badge: 'AI-Powered Workstation',
    title: 'Cỗ máy tối thượng cho sáng tạo và đồ họa',
    description: 'Trang bị card đồ họa kiến trúc NVIDIA Ada Lovelace và vi xử lý AI NPU chuyên biệt phục vụ đa nhiệm tác vụ nặng.',
    image: 'https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=1600&auto=format&fit=crop&q=80'
  },
  {
    id: 4,
    badge: 'Acoustic Perfection',
    title: 'Âm thanh Hi-Res Audio & Chống ồn chủ động',
    description: 'Thưởng thức không gian âm thanh chuẩn phòng thu với bộ xử lý chống ồn kép tiên tiến và kết nối không dây siêu tốc.',
    image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=1600&auto=format&fit=crop&q=80'
  }
])

const currentSlideIndex = ref<number>(0)
let timer: number | null = null

const currentSlide = computed(() => slides.value[currentSlideIndex.value] || slides.value[0])

const nextSlide = () => {
  currentSlideIndex.value = (currentSlideIndex.value + 1) % slides.value.length
}

const prevSlide = () => {
  currentSlideIndex.value = (currentSlideIndex.value - 1 + slides.value.length) % slides.value.length
}

const goToSlide = (idx: number) => {
  currentSlideIndex.value = idx
}

const startTimer = () => {
  stopTimer()
  timer = window.setInterval(nextSlide, 5000)
}

const stopTimer = () => {
  if (timer !== null) {
    clearInterval(timer)
    timer = null
  }
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

onMounted(() => {
  if (route.query.mode === 'register' || route.name === 'register') {
    isLogin.value = false
  }
  if (authStore.isAuthenticated) {
    router.replace('/')
  }
  startTimer()
})

onUnmounted(() => {
  stopTimer()
})

const switchTab = (val: boolean) => {
  isLogin.value = val
  authError.value = ''
}

const redirectAfterAuth = () => {
  cartStore.fetchCart()
  wishlistStore.fetchWishlist()
  const redirect = route.query.redirect as string
  if (redirect && redirect.startsWith('/')) {
    router.push(redirect)
  } else {
    router.push('/')
  }
}

const handleLogin = async () => {
  authLoading.value = true
  authError.value = ''
  try {
    await authStore.login(loginForm.value)
    redirectAfterAuth()
  } catch (err: any) {
    authError.value = err.message || 'Đăng nhập không thành công, vui lòng thử lại.'
  } finally {
    authLoading.value = false
  }
}

const handleRegister = async () => {
  authLoading.value = true
  authError.value = ''
  try {
    await authStore.register(registerForm.value)
    redirectAfterAuth()
  } catch (err: any) {
    authError.value = err.message || 'Đăng ký không thành công, vui lòng thử lại.'
  } finally {
    authLoading.value = false
  }
}
</script>

<style scoped>
/* Image Ken-Burns fade transition */
.fade-kenburns-enter-active,
.fade-kenburns-leave-active {
  transition: opacity 1.2s ease-in-out, transform 10s ease-out;
}

.fade-kenburns-enter-from {
  opacity: 0;
  transform: scale(1.06);
}

.fade-kenburns-leave-to {
  opacity: 0;
  transform: scale(1.0);
}

/* Slide-Fade Transition for Text */
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: opacity 0.4s ease, transform 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.slide-fade-enter-from {
  opacity: 0;
  transform: translateY(12px);
}

.slide-fade-leave-to {
  opacity: 0;
  transform: translateY(-12px);
}
</style>