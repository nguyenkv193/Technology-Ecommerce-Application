<template>
  <div class="min-h-screen bg-slate-950 flex items-center justify-center p-4 relative overflow-hidden">
    <!-- Subtle Background Glow -->
    <div class="absolute -top-40 -left-40 w-96 h-96 bg-blue-600/10 rounded-full blur-3xl pointer-events-none"></div>
    <div class="absolute -bottom-40 -right-40 w-96 h-96 bg-indigo-600/10 rounded-full blur-3xl pointer-events-none"></div>

    <!-- Login Container -->
    <div class="w-full max-w-md bg-slate-900/80 border border-slate-800 rounded-2xl shadow-2xl backdrop-blur-xl p-8 relative z-10">
      <!-- Header -->
      <div class="text-center mb-8">
        <div class="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-blue-600/10 border border-blue-500/20 text-blue-400 mb-4 shadow-inner">
          <ShieldAlert class="w-7 h-7" />
        </div>
        <h1 class="text-2xl font-bold text-slate-100 tracking-tight">TechStore Backoffice</h1>
        <p class="text-sm text-slate-400 mt-1">Cổng điều hành & quản trị hệ thống</p>
        <div class="mt-3 inline-block px-3 py-1 bg-amber-500/10 border border-amber-500/20 text-amber-400 text-xs font-medium rounded-full">
          Dành riêng cho Quản trị viên (ADMIN)
        </div>
      </div>

      <!-- Alert Error Message -->
      <div v-if="errorMessage" class="mb-6 p-4 rounded-xl bg-rose-500/10 border border-rose-500/20 text-rose-400 text-sm flex items-start gap-3">
        <AlertCircle class="w-5 h-5 shrink-0 mt-0.5" />
        <div>
          <p class="font-medium">Đăng nhập không thành công</p>
          <p class="text-xs text-rose-400/80 mt-0.5">{{ errorMessage }}</p>
        </div>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleLogin" class="space-y-5">
        <div>
          <label class="block text-xs font-semibold uppercase tracking-wider text-slate-300 mb-2">Email Quản trị</label>
          <div class="relative">
            <Mail class="w-5 h-5 text-slate-500 absolute left-3.5 top-1/2 -translate-y-1/2" />
            <input
              v-model="email"
              type="email"
              required
              autocomplete="email"
              placeholder="admin@techstore.vn"
              class="w-full bg-slate-950/60 border border-slate-800 rounded-xl pl-11 pr-4 py-3 text-slate-100 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500/40 focus:border-blue-500 transition-all text-sm"
            />
          </div>
        </div>

        <div>
          <div class="flex items-center justify-between mb-2">
            <label class="block text-xs font-semibold uppercase tracking-wider text-slate-300">Mật khẩu</label>
          </div>
          <div class="relative">
            <Lock class="w-5 h-5 text-slate-500 absolute left-3.5 top-1/2 -translate-y-1/2" />
            <input
              v-model="password"
              :type="showPassword ? 'text' : 'password'"
              required
              autocomplete="current-password"
              placeholder="••••••••"
              class="w-full bg-slate-950/60 border border-slate-800 rounded-xl pl-11 pr-11 py-3 text-slate-100 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500/40 focus:border-blue-500 transition-all text-sm"
            />
            <button
              type="button"
              @click="showPassword = !showPassword"
              class="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-500 hover:text-slate-300"
            >
              <EyeOff v-if="showPassword" class="w-4 h-4" />
              <Eye v-else class="w-4 h-4" />
            </button>
          </div>
        </div>

        <button
          type="submit"
          :disabled="isLoading"
          class="w-full py-3.5 px-4 bg-blue-600 hover:bg-blue-500 active:bg-blue-700 text-white font-medium rounded-xl text-sm transition-all shadow-lg shadow-blue-600/20 flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <Loader2 v-if="isLoading" class="w-4 h-4 animate-spin" />
          <span>{{ isLoading ? 'Đang xác thực...' : 'Truy cập Hệ thống' }}</span>
        </button>
      </form>

      <div class="mt-8 pt-6 border-t border-slate-800/80 text-center">
        <p class="text-xs text-slate-500">
          TechStore Platform Architecture • Phiên bản 1.0.0
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
