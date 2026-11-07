import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import apiClient, { ADMIN_TOKEN_KEY } from '@/api/client'
import type { User, ApiResponse, AuthLoginResponse } from '@/types'

export const useAdminAuthStore = defineStore('adminAuth', () => {
  const token = ref<string | null>(localStorage.getItem(ADMIN_TOKEN_KEY))
  const user = ref<User | null>(null)
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const isAuthenticated = computed(() => !!token.value && user.value?.role === 'ADMIN')

  async function login(email: string, password: string):Promise<User> {
    isLoading.value = true
    error.value = null
    try {
      const response = await apiClient.post<ApiResponse<AuthLoginResponse>>('/auth/login', {
        email,
        password
      })

      const authData = response.data.data
      if (authData.user.role !== 'ADMIN') {
        logout()
        throw new Error('Tài khoản không có quyền truy cập Backoffice Admin')
      }

      token.value = authData.accessToken
      user.value = authData.user
      localStorage.setItem(ADMIN_TOKEN_KEY, authData.accessToken)
      return authData.user
    } catch (err: any) {
      error.value = err.response?.data?.message || err.message || 'Đăng nhập thất bại'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  async function fetchProfile(): Promise<User | null> {
    if (!token.value) return null
    try {
      const response = await apiClient.get<ApiResponse<User>>('/users/profile')
      const profile = response.data.data
      if (profile.role !== 'ADMIN') {
        logout()
        return null
      }
      user.value = profile
      return profile
    } catch (err) {
      logout()
      return null
    }
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem(ADMIN_TOKEN_KEY)
  }

  return {
    token,
    user,
    isLoading,
    error,
    isAuthenticated,
    login,
    fetchProfile,
    logout
  }
})
