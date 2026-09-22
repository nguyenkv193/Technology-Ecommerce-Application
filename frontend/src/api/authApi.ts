import { apiClient } from './client'
import type { ApiResponse, User } from '@/types'

export interface LoginResponseData {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  user: User
}

export const authApi = {
  login: (credentials: { email: string; password: string }): Promise<ApiResponse<LoginResponseData>> => 
    apiClient.post('/auth/login', credentials),
    
  register: (userData: { email: string; password: string; fullName: string; phoneNumber?: string }): Promise<ApiResponse<LoginResponseData>> => 
    apiClient.post('/auth/register', userData),
    
  refreshToken: (token: string): Promise<ApiResponse<LoginResponseData>> => 
    apiClient.post('/auth/refresh', { refreshToken: token }),
    
  getProfile: (): Promise<ApiResponse<User>> => 
    apiClient.get('/auth/me'),
    
  updateProfile: (data: { fullName: string; phoneNumber?: string }): Promise<ApiResponse<User>> => 
    apiClient.put('/users/profile', data),
    
  changePassword: (data: { oldPassword: string; newPassword: string }): Promise<ApiResponse<void>> => 
    apiClient.post('/users/change-password', data),
    
  getAddresses: (): Promise<ApiResponse<any[]>> => 
    apiClient.get('/users/addresses'),
    
  addAddress: (data: any): Promise<ApiResponse<any>> => 
    apiClient.post('/users/addresses', data)
}
