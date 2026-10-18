import { apiClient } from './client'
import type { ApiResponse, PageResponse, Product, ProductSuggestion, Category, Brand, ProductReview, ProductReviewSummary, ProductStorefrontContent } from '@/types'

export const catalogApi = {
  getBestSellers: (limit: number = 8): Promise<ApiResponse<Product[]>> =>
    apiClient.get('/products/best-sellers', { params: { limit } }),

  getCategories: (): Promise<ApiResponse<Category[]>> => 
    apiClient.get('/categories'),
    
  getCategoryBySlug: (slug: string): Promise<ApiResponse<Category>> => 
    apiClient.get(`/categories/slug/${slug}`),
  
  getBrands: (): Promise<ApiResponse<Brand[]>> => 
    apiClient.get('/brands'),
  
  getProducts: (params: Record<string, any> = {}): Promise<ApiResponse<PageResponse<Product>>> => 
    apiClient.get('/products', { params }),

  getSearchSuggestions: (keyword: string, limit: number = 6): Promise<ApiResponse<ProductSuggestion[]>> =>
    apiClient.get('/products/suggestions', { params: { keyword, limit } }),
    
  getProductById: (id: number): Promise<ApiResponse<Product>> => 
    apiClient.get(`/products/${id}`),
    
  getProductBySlug: (slug: string): Promise<ApiResponse<Product>> => 
    apiClient.get(`/products/slug/${slug}`),

  getProductStorefront: (productId: number): Promise<ApiResponse<ProductStorefrontContent>> =>
    apiClient.get(`/products/${productId}/storefront`),

  updateProductStorefront: (productId: number, content: ProductStorefrontContent): Promise<ApiResponse<ProductStorefrontContent>> =>
    apiClient.put(`/products/${productId}/storefront`, content),

  getReviews: (productId: number, params: Record<string, any> = {}): Promise<ApiResponse<PageResponse<ProductReview>>> => 
    apiClient.get(`/reviews/product/${productId}`, { params }),
    
  getReviewSummary: (productId: number): Promise<ApiResponse<ProductReviewSummary>> => 
    apiClient.get(`/reviews/product/${productId}/summary`),
    
  submitReview: (reviewData: { productId: number; rating: number; comment: string }): Promise<ApiResponse<ProductReview>> => 
    apiClient.post('/reviews', reviewData)
}
