import axios, { type AxiosResponse, type InternalAxiosRequestConfig } from 'axios'
import type { ApiResponse } from '@/types'

// Axios instance cho Backend Spring Boot
const apiClient = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json'
  }
})

// Axios instance cho AI Recommendation Service (FastAPI)
const aiClient = axios.create({
  baseURL: '/ai-api',
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request Interceptor: Tự động đính kèm Bearer Token nếu có
apiClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = localStorage.getItem('techstore_access_token')
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, (error) => {
  return Promise.reject(error)
})

// Response Interceptor: Xử lý hết hạn token hoặc mã lỗi chuẩn ApiResponse
apiClient.interceptors.response.use(
  (response: AxiosResponse<ApiResponse<any>>) => response.data as any,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('techstore_access_token')
      localStorage.removeItem('techstore_user')
    }
    const message = error.response?.data?.message || 'Có lỗi xảy ra, vui lòng thử lại sau.'
    return Promise.reject(new Error(message))
  }
)

aiClient.interceptors.response.use(
  (response: AxiosResponse) => response.data,
  (error) => {
    return Promise.reject(error)
  }
)

export { apiClient, aiClient }
