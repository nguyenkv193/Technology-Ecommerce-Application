import apiClient from './client'
import type { ApiResponse } from '@/types'

export interface SystemHealthData {
  status: string
  service: string
  version: string
  environment: string
  currentTime: string
}

export async function checkSystemHealth(): Promise<ApiResponse<SystemHealthData>> {
  const res = await apiClient.get<ApiResponse<SystemHealthData>>('/health')
  return res.data
}
