import apiClient from './client'
import type { ApiResponse, Brand } from '@/types'

export async function fetchBrands(): Promise<Brand[]> {
  const res = await apiClient.get<ApiResponse<Brand[]>>('/brands')
  return res.data.data || []
}

export async function createBrand(data: Partial<Brand>): Promise<Brand> {
  const res = await apiClient.post<ApiResponse<Brand>>('/brands', data)
  return res.data.data
}

export async function updateBrand(id: number, data: Partial<Brand>): Promise<Brand> {
  const res = await apiClient.put<ApiResponse<Brand>>(`/brands/${id}`, data)
  return res.data.data
}

export async function deleteBrand(id: number): Promise<void> {
  await apiClient.delete<ApiResponse<void>>(`/brands/${id}`)
}
