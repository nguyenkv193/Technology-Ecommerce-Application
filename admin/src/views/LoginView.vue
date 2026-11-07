<template>
  <div class="min-h-screen bg-[#fafafc] flex items-center justify-center p-4 relative overflow-hidden font-sans">
    <!-- Subtle Ambient Glow -->
    <div class="absolute inset-0 bg-[radial-gradient(circle_at_50%_0%,rgba(59,130,246,0.06),transparent_50%)] pointer-events-none"></div>

    <!-- Login Container -->
    <div class="w-full max-w-sm bg-white border border-zinc-200 rounded-2xl shadow-xl p-7 relative z-10 text-zinc-900">
      <!-- Header -->
      <div class="text-center mb-6">
        <div class="inline-flex items-center justify-center w-10 h-10 rounded-xl bg-zinc-900 border border-zinc-800 text-white mb-3 shadow-xs">
          <ShieldAlert class="w-5 h-5 text-white" />
        </div>
        <h1 class="text-lg font-semibold text-zinc-900 tracking-tight">Hệ thống TechStore</h1>
        <p class="text-xs text-zinc-500 mt-0.5 font-mono">Đăng nhập vào trang quản lý hệ thống</p>
        <div class="mt-2.5 inline-block px-2.5 py-0.5 bg-zinc-100 border border-zinc-200 text-zinc-700 text-[10px] font-mono font-medium rounded-full">
          Quản trị viên (ADMIN)
        </div>
      </div>

      <!-- Alert Error Message -->
      <div v-if="errorMessage" class="mb-5 p-3 rounded-lg bg-rose-50 border border-rose-200 text-rose-700 text-xs flex items-start gap-2.5 font-mono">
        <AlertCircle class="w-4 h-4 shrink-0 mt-0.5 text-rose-600" />
        <div>
          <p class="font-medium font-sans">Đăng nhập không thành công</p>
          <p class="text-[11px] opacity-90 mt-0.5">{{ errorMessage }}</p>
        </div>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleLogin" class="space-y-4">
        <div>
          <label class="block text-[11px] font-mono font-medium text-zinc-600 mb-1.5">EMAIL</label>
          <div class="relative">
            <Mail class="w-4 h-4 text-zinc-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              v-model="email"
              type="email"
              required
              autocomplete="email"
              placeholder="admin@techstore.vn"
              class="w-full bg-white border border-zinc-200 rounded-lg pl-9 pr-3.5 py-2 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900/10 transition-all text-xs font-mono"
            />
          </div>
        </div>

        <div>
          <div class="flex items-center justify-between mb-1.5">
            <label class="block text-[11px] font-mono font-medium text-zinc-600">MẬT KHẨU</label>
          </div>
          <div class="relative">
            <Lock class="w-4 h-4 text-zinc-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              v-model="password"
              :type="showPassword ? 'text' : 'password'"
              required
              autocomplete="current-password"
              placeholder="••••••••"
              class="w-full bg-white border border-zinc-200 rounded-lg pl-9 pr-9 py-2 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900/10 transition-all text-xs font-mono"
            />
            <button
              type="button"
              @click="showPassword = !showPassword"
              class="absolute right-3 top-1/2 -translate-y-1/2 text-zinc-400 hover:text-zinc-600"
            >
              <EyeOff v-if="showPassword" class="w-3.5 h-3.5" />
              <Eye v-else class="w-3.5 h-3.5" />
            </button>
          </div>
        </div>

        <button
          type="submit"
          :disabled="isLoading"
          class="w-full py-2.5 px-4 bg-zinc-900 hover:bg-zinc-800 active:bg-zinc-950 text-white font-medium rounded-lg text-xs transition-all shadow-sm flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed font-mono mt-2"
        >
          <Loader2 v-if="isLoading" class="w-3.5 h-3.5 animate-spin" />
          <span>{{ isLoading ? 'Đang xác thực...' : 'Đăng nhập' }}</span>
        </button>
      </form>

      <div class="mt-6 pt-4 border-t border-zinc-100 text-center font-mono">
        <p class="text-[10px] text-zinc-400">
          TechStore Platform Architecture • v2.4.0
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ShieldAlert, Mail, Lock, Eye, EyeOff, AlertCircle, Loader2 } from 'lucide-vue-next'
import { toast } from 'vue-sonner'
import { useAdminAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAdminAuthStore()

const email = ref('')
const password = ref('')
const showPassword = ref(false)
const isLoading = ref(false)
const errorMessage = ref('')

async function handleLogin() {
  errorMessage.value = ''
  isLoading.value = true
  try {
    await authStore.login(email.value, password.value)
    toast.success('Đăng nhập thành công! Chào mừng trở lại.')
    router.push('/')
  } catch (err: any) {
    const msg = err.response?.data?.message || err.message || 'Email hoặc mật khẩu không chính xác.'
    errorMessage.value = msg
    toast.error(msg)
  } finally {
    isLoading.value = false
  }
}
</script>
