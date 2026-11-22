import { apiClient } from './client'
import type { ApiResponse, User, UserAddress, CreateAddressRequest } from '@/types'

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

  exchangeOAuthCode: (code: string): Promise<ApiResponse<LoginResponseData>> =>
    apiClient.post('/auth/oauth2/exchange', { code }),
    
  getProfile: (): Promise<ApiResponse<User>> => 
    apiClient.get('/auth/me'),
    
  updateProfile: (data: { fullName: string; phone?: string; avatarUrl?: string }): Promise<ApiResponse<User>> => 
    apiClient.put('/users/profile', data),
    
  changePassword: (data: { currentPassword: string; newPassword: string }): Promise<ApiResponse<void>> => 
    apiClient.post('/users/change-password', data),
    
  getAddresses: (): Promise<ApiResponse<UserAddress[]>> => 
    apiClient.get('/users/addresses'),
    
  addAddress: (data: CreateAddressRequest): Promise<ApiResponse<UserAddress>> =>
    apiClient.post('/users/addresses', data),

  updateAddress: (id: number, data: CreateAddressRequest): Promise<ApiResponse<UserAddress>> =>
    apiClient.put(`/users/addresses/${id}`, data),

  deleteAddress: (id: number): Promise<ApiResponse<void>> =>
    apiClient.delete(`/users/addresses/${id}`),

  setDefaultAddress: (id: number): Promise<ApiResponse<UserAddress>> =>
    apiClient.patch(`/users/addresses/${id}/default`)
}