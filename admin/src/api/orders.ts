import apiClient from './client'
import type { ApiResponse, PageResponse, Order, OrderStatus } from '@/types'

export async function fetchOrders(page = 0, size = 10, status?: string): Promise<PageResponse<Order>> {
  const res = await apiClient.get<ApiResponse<PageResponse<Order>>>('/orders', {
    params: {
      page,
      size,
      status: status && status !== 'ALL' ? status : undefined
    }
  })
  return res.data.data
}

export async function fetchOrderDetail(id: number): Promise<Order> {
  const res = await apiClient.get<ApiResponse<Order>>(`/orders/${id}`)
  return res.data.data
}

export async function updateOrderStatus(id: number, status: OrderStatus, note?: string): Promise<Order> {
  const res = await apiClient.put<ApiResponse<Order>>(`/orders/${id}/status`, {
    status,
    note
  })
  return res.data.data
}

export async function cancelOrder(id: number): Promise<Order> {
  const res = await apiClient.put<ApiResponse<Order>>(`/orders/${id}/cancel`)
  return res.data.data
}
