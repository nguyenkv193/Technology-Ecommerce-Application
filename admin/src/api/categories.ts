import apiClient from './client'
import type { ApiResponse, Category } from '@/types'

export async function fetchCategories(): Promise<Category[]> {
  const res = await apiClient.get<ApiResponse<Category[]>>('/categories')
  return res.data.data || []
}

export async function createCategory(data: Partial<Category>): Promise<Category> {
  const res = await apiClient.post<ApiResponse<Category>>('/categories', data)
  return res.data.data
}

export async function updateCategory(id: number, data: Partial<Category>): Promise<Category> {
  const res = await apiClient.put<ApiResponse<Category>>(`/categories/${id}`, data)
  return res.data.data
}

export async function deleteCategory(id: number): Promise<void> {
  await apiClient.delete<ApiResponse<void>>(`/categories/${id}`)
}
