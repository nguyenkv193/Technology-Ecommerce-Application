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
    
  updateProfile: (data: { fullName: string; phoneNumber?: string }): Promise<ApiResponse<User>> => 
    apiClient.put('/users/profile', data),
    
  changePassword: (data: { oldPassword: string; newPassword: string }): Promise<ApiResponse<void>> => 
    apiClient.post('/users/change-password', data),
    
  getAddresses: (): Promise<ApiResponse<UserAddress[]>> => 
    apiClient.get('/users/addresses'),
    
  addAddress: (data: CreateAddressRequest): Promise<ApiResponse<UserAddress>> => 
    apiClient.post('/users/addresses', data)
}
