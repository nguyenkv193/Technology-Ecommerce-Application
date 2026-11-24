import apiClient from './client'
import type {
  ApiResponse,
  PageResponse,
  Product,
  ProductAttribute,
  ProductImage,
  ProductStatus,
  ProductVariant
} from '@/types'

export interface ProductFilterParams {
  keyword?: string
  categoryId?: number
  brandId?: number
  minPrice?: number
  maxPrice?: number
  page?: number
  size?: number
  sortBy?: string
  sortDirection?: 'ASC' | 'DESC'
}

export interface ProductFormData {
  name: string
  slug: string
  description?: string
  categoryId: number
  brandId: number
  status?: ProductStatus
  attributes?: ProductAttribute[]
  variants: ProductVariant[]
  images?: ProductImage[]
}

export async function fetchProducts(params: ProductFilterParams = {}): Promise<PageResponse<Product>> {
  const res = await apiClient.get<ApiResponse<PageResponse<Product>>>('/products', {
    params: {
      page: params.page ?? 0,
      size: params.size ?? 10,
      keyword: params.keyword || undefined,
      categoryId: params.categoryId || undefined,
      brandId: params.brandId || undefined,
      sortBy: params.sortBy || 'createdAt',
      sortDirection: params.sortDirection || 'DESC'
    }
  })
  return res.data.data
}

export async function fetchProductById(id: number): Promise<Product> {
  const res = await apiClient.get<ApiResponse<Product>>(`/products/${id}`)
  return res.data.data
}

export async function createProduct(data: ProductFormData): Promise<Product> {
  const res = await apiClient.post<ApiResponse<Product>>('/products', data)
  return res.data.data
}

export async function updateProduct(id: number, data: ProductFormData): Promise<Product> {
  const res = await apiClient.put<ApiResponse<Product>>(`/products/${id}`, data)
  return res.data.data
}

export async function deleteProduct(id: number): Promise<void> {
  await apiClient.delete<ApiResponse<void>>(`/products/${id}`)
}
