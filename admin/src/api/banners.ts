import apiClient from './client'
import type { ApiResponse, Banner } from '@/types'

export async function fetchBanners(position?: string): Promise<Banner[]> {
  const res = await apiClient.get<ApiResponse<Banner[]>>('/banners/admin', {
    params: position ? { position } : {}
  })
  return res.data.data || []
}

export async function createBanner(data: Partial<Banner>): Promise<Banner> {
  const res = await apiClient.post<ApiResponse<Banner>>('/banners', data)
  return res.data.data
}

export async function updateBanner(id: number, data: Partial<Banner>): Promise<Banner> {
  const res = await apiClient.put<ApiResponse<Banner>>(`/banners/${id}`, data)
  return res.data.data
}

export async function toggleBannerStatus(id: number): Promise<void> {
  await apiClient.patch<ApiResponse<void>>(`/banners/${id}/status`)
}

export async function deleteBanner(id: number): Promise<void> {
  await apiClient.delete<ApiResponse<void>>(`/banners/${id}`)
}
