import { apiClient } from './client'
import type { ApiResponse } from '@/types'

export const wishlistApi = {
  getWishlist: (): Promise<ApiResponse<any[]>> => 
    apiClient.get('/wishlist'),
    
  addToWishlist: (productId: number): Promise<ApiResponse<any>> => 
    apiClient.post(`/wishlist/${productId}`),
    
  removeFromWishlist: (productId: number): Promise<ApiResponse<void>> => 
    apiClient.delete(`/wishlist/${productId}`),
    
  checkWishlist: (productId: number): Promise<ApiResponse<{ isWishlisted: boolean }>> => 
    apiClient.get(`/wishlist/check/${productId}`)
}
