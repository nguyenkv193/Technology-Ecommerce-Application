import { apiClient } from './client'
import type { ApiResponse, Cart } from '@/types'

export const cartApi = {
  getCart: (): Promise<ApiResponse<Cart>> => 
    apiClient.get('/cart'),
    
  addToCart: (variantId: number, quantity: number = 1): Promise<ApiResponse<Cart>> => 
    apiClient.post('/cart/items', { variantId, quantity }),
    
  updateQuantity: (itemId: number, quantity: number): Promise<ApiResponse<Cart>> => 
    apiClient.put(`/cart/items/${itemId}`, { quantity }),
    
  removeItem: (itemId: number): Promise<ApiResponse<Cart>> => 
    apiClient.delete(`/cart/items/${itemId}`),
    
  clearCart: (): Promise<ApiResponse<Cart>> => 
    apiClient.delete('/cart')
}
