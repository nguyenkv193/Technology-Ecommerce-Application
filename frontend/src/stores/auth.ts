import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/authApi'
import type { User } from '@/types'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('techstore_access_token') || null)
  const user = ref<User | null>(JSON.parse(localStorage.getItem('techstore_user') || 'null'))

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  const login = async (credentials: { email: string; password: string }) => {
    const res = await authApi.login(credentials)
    if (res.success && res.data) {
      token.value = res.data.accessToken
      user.value = res.data.user
      localStorage.setItem('techstore_access_token', res.data.accessToken)
      localStorage.setItem('techstore_user', JSON.stringify(res.data.user))
    }
    return res
  }

  const register = async (userData: { email: string; password: string; fullName: string; phoneNumber?: string }) => {
    const res = await authApi.register(userData)
    if (res.success && res.data) {
      token.value = res.data.accessToken
      user.value = res.data.user
      localStorage.setItem('techstore_access_token', res.data.accessToken)
      localStorage.setItem('techstore_user', JSON.stringify(res.data.user))
    }
    return res
  }

  const logout = () => {
    token.value = null
    user.value = null
    localStorage.removeItem('techstore_access_token')
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
    user,
    isAuthenticated,
    isAdmin,
    login,
    register,
    logout,
    fetchProfile
  }
})
