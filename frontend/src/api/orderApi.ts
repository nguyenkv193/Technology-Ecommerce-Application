import { apiClient } from './client'
import type { ApiResponse, PageResponse, Order } from '@/types'

export interface CheckoutRequest {
  recipientName: string
  phoneNumber: string
  shippingAddress: string
  note?: string
  paymentMethod: 'COD' | 'VNPAY' | 'MOMO' | 'BANK_TRANSFER'
}

export const orderApi = {
  checkout: (checkoutData: CheckoutRequest): Promise<ApiResponse<Order>> => 
    apiClient.post('/orders/checkout', checkoutData),
    
  getMyOrders: (params: Record<string, any> = {}): Promise<ApiResponse<PageResponse<Order>>> => 
    apiClient.get('/orders', { params }),
    
  getOrderById: (id: number): Promise<ApiResponse<Order>> => 
    apiClient.get(`/orders/${id}`),
    
  cancelOrder: (id: number): Promise<ApiResponse<Order>> => 
    apiClient.put(`/orders/${id}/cancel`),
    
  updateStatus: (id: number, data: { orderStatus: string; paymentStatus?: string }): Promise<ApiResponse<Order>> => 
    apiClient.put(`/orders/${id}/status`, data)
}
