import apiClient from './client'
import type { ApiResponse } from '@/types'

export async function reindexProducts(): Promise<ApiResponse<number>> {
  const res = await apiClient.post<ApiResponse<number>>('/admin/search/reindex-products')
  return res.data
}
