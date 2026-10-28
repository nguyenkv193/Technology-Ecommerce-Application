<template>
  <div class="min-h-screen bg-zinc-950 flex items-center justify-center p-4 relative overflow-hidden font-sans">
    <!-- Subtle Ambient Glow -->
    <div class="absolute inset-0 bg-[radial-gradient(circle_at_50%_0%,rgba(120,119,198,0.12),transparent_50%)] pointer-events-none"></div>

    <!-- Login Container (Linear/Vercel Minimalist) -->
    <div class="w-full max-w-sm bg-zinc-900/80 border border-zinc-800/80 rounded-2xl shadow-2xl backdrop-blur-xl p-7 relative z-10">
      <!-- Header -->
      <div class="text-center mb-6">
        <div class="inline-flex items-center justify-center w-10 h-10 rounded-xl bg-zinc-800 border border-zinc-700 text-white mb-3 shadow-inner">
          <ShieldAlert class="w-5 h-5 text-zinc-200" />
        </div>
        <h1 class="text-lg font-semibold text-white tracking-tight">TechStore Backoffice</h1>
        <p class="text-xs text-zinc-400 mt-0.5 font-mono">Cổng điều hành & quản trị hệ thống</p>
        <div class="mt-2.5 inline-block px-2.5 py-0.5 bg-zinc-800/80 border border-zinc-700/60 text-zinc-300 text-[10px] font-mono font-medium rounded-full">
          Quản trị viên (ADMIN)
        </div>
      </div>

      <!-- Alert Error Message -->
      <div v-if="errorMessage" class="mb-5 p-3 rounded-lg bg-rose-500/10 border border-rose-500/20 text-rose-400 text-xs flex items-start gap-2.5 font-mono">
        <AlertCircle class="w-4 h-4 shrink-0 mt-0.5" />
        <div>
          <p class="font-medium font-sans">Đăng nhập không thành công</p>
          <p class="text-[11px] opacity-80 mt-0.5">{{ errorMessage }}</p>
        </div>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleLogin" class="space-y-4">
        <div>
          <label class="block text-[11px] font-mono font-medium text-zinc-400 mb-1.5">EMAIL QUẢN TRỊ</label>
          <div class="relative">
            <Mail class="w-4 h-4 text-zinc-500 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              v-model="email"
              type="email"
              required
              autocomplete="email"
              placeholder="admin@techstore.vn"
              class="w-full bg-zinc-950/80 border border-zinc-800/80 rounded-lg pl-9 pr-3.5 py-2 text-zinc-100 placeholder-zinc-500 focus:outline-none focus:border-zinc-600 transition-all text-xs font-mono"
            />
          </div>
        </div>

        <div>
          <div class="flex items-center justify-between mb-1.5">
            <label class="block text-[11px] font-mono font-medium text-zinc-400">MẬT KHẨU</label>
          </div>
          <div class="relative">
            <Lock class="w-4 h-4 text-zinc-500 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              v-model="password"
              :type="showPassword ? 'text' : 'password'"
              required
              autocomplete="current-password"
              placeholder="••••••••"
              class="w-full bg-zinc-950/80 border border-zinc-800/80 rounded-lg pl-9 pr-9 py-2 text-zinc-100 placeholder-zinc-500 focus:outline-none focus:border-zinc-600 transition-all text-xs font-mono"
            />
            <button
              type="button"
              @click="showPassword = !showPassword"
              class="absolute right-3 top-1/2 -translate-y-1/2 text-zinc-500 hover:text-zinc-300"
            >
              <EyeOff v-if="showPassword" class="w-3.5 h-3.5" />
              <Eye v-else class="w-3.5 h-3.5" />
            </button>
          </div>
        </div>

        <button
          type="submit"
          :disabled="isLoading"
          class="w-full py-2.5 px-4 bg-white hover:bg-zinc-200 active:bg-zinc-300 text-zinc-950 font-medium rounded-lg text-xs transition-all shadow-sm flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed font-mono mt-2"
        >
          <Loader2 v-if="isLoading" class="w-3.5 h-3.5 animate-spin" />
          <span>{{ isLoading ? 'Đang xác thực...' : 'Truy cập Hệ thống' }}</span>
        </button>
      </form>

      <div class="mt-6 pt-4 border-t border-zinc-800/60 text-center font-mono">
        <p class="text-[10px] text-zinc-500">
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
    router.push('/')
  } catch (err: any) {
    errorMessage.value = err.response?.data?.message || err.message || 'Email hoặc mật khẩu không chính xác.'
  } finally {
    isLoading.value = false
  }
}
</script>
