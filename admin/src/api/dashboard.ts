import apiClient from './client'
import type { ApiResponse, PageResponse, Order, Product, DashboardMetrics } from '@/types'

export async function fetchDashboardMetrics(): Promise<DashboardMetrics> {
  try {
    const [productsRes, ordersRes] = await Promise.all([
      apiClient.get<ApiResponse<PageResponse<Product>>>('/products?page=0&size=1'),
      apiClient.get<ApiResponse<PageResponse<Order>>>('/orders?page=0&size=50')
    ])

    const totalProducts = productsRes.data.data.totalElements || 0
    const orders = ordersRes.data.data.content || []
    const totalOrders = ordersRes.data.data.totalElements || orders.length

    let totalRevenue = 0
    let pendingOrders = 0
    let lowStockCount = 0

    orders.forEach((ord) => {
      if (ord.status === 'DELIVERED') {
        totalRevenue += ord.totalAmount
      }
      if (ord.status === 'PENDING') {
        pendingOrders += 1
      }
    })

    return {
      totalRevenue,
      totalOrders,
      pendingOrders,
      totalProducts,
      lowStockCount
    }
  } catch (error) {
    // Fallback default numbers if error
    return {
      totalRevenue: 0,
      totalOrders: 0,
      pendingOrders: 0,
      totalProducts: 0,
      lowStockCount: 0
    }
  }
}

export async function fetchRecentOrders(): Promise<Order[]> {
  try {
    const res = await apiClient.get<ApiResponse<PageResponse<Order>>>('/orders?page=0&size=5')
    return res.data.data.content || []
  } catch (error) {
    return []
  }
}
