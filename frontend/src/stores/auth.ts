import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/authApi'
import type { User } from '@/types'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('techstore_access_token') || null)
  const refreshToken = ref<string | null>(localStorage.getItem('techstore_refresh_token') || null)
  const user = ref<User | null>(JSON.parse(localStorage.getItem('techstore_user') || 'null'))

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  // Modal Control States
  const isAuthModalOpen = ref<boolean>(false)
  const authModalMode = ref<'login' | 'register'>('login')
  const isLogoutModalOpen = ref<boolean>(false)
  const onAuthSuccess = ref<(() => void) | null>(null)

  const openLoginModal = (callback?: () => void) => {
    authModalMode.value = 'login'
    isAuthModalOpen.value = true
    onAuthSuccess.value = callback || null
  }

  const openRegisterModal = (callback?: () => void) => {
    authModalMode.value = 'register'
    isAuthModalOpen.value = true
    onAuthSuccess.value = callback || null
  }

  const closeAuthModal = () => {
    isAuthModalOpen.value = false
    onAuthSuccess.value = null
  }

  const openLogoutModal = () => {
    isLogoutModalOpen.value = true
  }

  const closeLogoutModal = () => {
    isLogoutModalOpen.value = false
  }

  const persistSession = (session: {
    accessToken: string
    refreshToken: string
    user: User
  }) => {
    token.value = session.accessToken
    refreshToken.value = session.refreshToken
    user.value = session.user
    localStorage.setItem('techstore_access_token', session.accessToken)
    localStorage.setItem('techstore_refresh_token', session.refreshToken)
    localStorage.setItem('techstore_user', JSON.stringify(session.user))
  }

  const login = async (credentials: { email: string; password: string }) => {
    const res = await authApi.login(credentials)
    if (res.success && res.data) {
      persistSession(res.data)
    }
    return res
  }

  const register = async (userData: { email: string; password: string; fullName: string; phoneNumber?: string }) => {
    const res = await authApi.register(userData)
    if (res.success && res.data) {
      persistSession(res.data)
    }
    return res
  }

  const exchangeOAuthCode = async (code: string) => {
    const res = await authApi.exchangeOAuthCode(code)
    if (res.success && res.data) {
      persistSession(res.data)
    }
    return res
  }

  const logout = () => {
    token.value = null
    refreshToken.value = null
    user.value = null
    localStorage.removeItem('techstore_access_token')
    localStorage.removeItem('techstore_refresh_token')
    localStorage.removeItem('techstore_user')
  }

  const fetchProfile = async () => {
    if (!token.value) return
    try {
      const res = await authApi.getProfile()
      if (res.success && res.data) {
        user.value = res.data
        localStorage.setItem('techstore_user', JSON.stringify(res.data))
      }
    } catch (e) {
      logout()
    }
  }

  return {
    token,
    refreshToken,
    user,
    isAuthenticated,
    isAdmin,
    isAuthModalOpen,
    authModalMode,
    isLogoutModalOpen,
    onAuthSuccess,
    openLoginModal,
    openRegisterModal,
    closeAuthModal,
    openLogoutModal,
    closeLogoutModal,
    exchangeOAuthCode,
    login,
    register,
    logout,
    fetchProfile
  }
})
