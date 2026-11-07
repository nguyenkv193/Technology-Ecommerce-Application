import apiClient from './client'
import type { ApiResponse, PageResponse, Order, Product, DashboardMetrics } from '@/types'

export interface DashboardOverview {
  metrics: DashboardMetrics
  recentOrders: Order[]
  allOrders: Order[]
}

export async function fetchDashboardOverview(): Promise<DashboardOverview> {
  try {
    const [productsRes, ordersRes] = await Promise.all([
      apiClient.get<ApiResponse<PageResponse<Product>>>('/products?page=0&size=1'),
      apiClient.get<ApiResponse<PageResponse<Order>>>('/orders?page=0&size=100')
    ])

    const totalProducts = productsRes.data?.data?.totalElements || 0
    const orders = ordersRes.data?.data?.content || []
    const totalOrders = ordersRes.data?.data?.totalElements || orders.length

    let totalRevenue = 0
    let pendingOrders = 0
    let lowStockCount = 0

    orders.forEach((ord) => {
      // Calculate revenue from completed or paid orders
      if (ord.status === 'DELIVERED' || ord.paymentStatus === 'PAID') {
        totalRevenue += ord.totalAmount
      }
      if (ord.status === 'PENDING') {
        pendingOrders += 1
      }
    })

    return {
      metrics: {
        totalRevenue,
        totalOrders,
        pendingOrders,
        totalProducts,
        lowStockCount
      },
      recentOrders: orders.slice(0, 5),
      allOrders: orders
    }
  } catch (error) {
    return {
      metrics: {
        totalRevenue: 0,
        totalOrders: 0,
        pendingOrders: 0,
        totalProducts: 0,
        lowStockCount: 0
      },
      recentOrders: [],
      allOrders: []
    }
  }
}

export async function fetchDashboardMetrics(): Promise<DashboardMetrics> {
  const overview = await fetchDashboardOverview()
  return overview.metrics
}

export async function fetchRecentOrders(): Promise<Order[]> {
  const overview = await fetchDashboardOverview()
  return overview.recentOrders
}
