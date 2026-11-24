import apiClient from './client'
import type { ApiResponse, PageResponse, Order, Product, DashboardMetrics, BehaviorAnalytics } from '@/types'

export interface DashboardOverview {
  metrics: DashboardMetrics
  recentOrders: Order[]
  allOrders: Order[]
}

export async function fetchBehaviorAnalytics(
  days = 30,
  limit = 10,
  from?: string,
  to?: string,
  throwOnError = false
): Promise<BehaviorAnalytics> {
  try {
    const res = await apiClient.get<ApiResponse<BehaviorAnalytics>>('/behaviors/admin/analytics', {
      params: {
        days,
        limit,
        from: from || undefined,
        to: to || undefined
      }
    })
    return (
      res.data?.data || {
        days,
        summary: {
          totalViews: 0,
          totalWishlists: 0,
          totalAddToCart: 0,
          totalPurchases: 0,
          totalSearches: 0
        },
        dailySeries: [],
        topProducts: []
      }
    )
  } catch (err) {
    if (throwOnError) {
      throw err
    }
    return {
      days,
      summary: {
        totalViews: 0,
        totalWishlists: 0,
        totalAddToCart: 0,
        totalPurchases: 0,
        totalSearches: 0
      },
      dailySeries: [],
      topProducts: []
    }
  }
}

export async function fetchDashboardOverview(): Promise<DashboardOverview> {
  try {
    const [productsRes, firstOrdersRes] = await Promise.all([
      apiClient.get<ApiResponse<PageResponse<Product>>>('/products?page=0&size=200'),
      apiClient.get<ApiResponse<PageResponse<Order>>>('/orders?page=0&size=100')
    ])

    const productsPage = productsRes.data?.data
    const products = productsPage?.content || []
    const totalProducts = productsPage?.totalElements || products.length

    const firstOrdersPage = firstOrdersRes.data?.data
    const orders = [...(firstOrdersPage?.content || [])]
    const totalOrders = firstOrdersPage?.totalElements || orders.length
    const totalOrderPages = Math.min(firstOrdersPage?.totalPages || 1, 10)

    if (totalOrderPages > 1) {
      const remainingPages = await Promise.all(
        Array.from({ length: totalOrderPages - 1 }, (_, idx) =>
          apiClient.get<ApiResponse<PageResponse<Order>>>(`/orders?page=${idx + 1}&size=100`)
        )
      )
      remainingPages.forEach((res) => {
        const content = res.data?.data?.content || []
        orders.push(...content)
      })
    }

    let totalRevenue = 0
    let pendingOrders = 0
    const lowStockCount = products.filter((p) => (p.totalStock ?? 0) <= 5).length

    orders.forEach((ord) => {
      // Calculate revenue from completed or paid orders
      if (ord.orderStatus === 'DELIVERED' || ord.paymentStatus === 'PAID') {
        totalRevenue += ord.finalAmount ?? ord.totalAmount ?? 0
      }
      if (ord.orderStatus === 'PENDING') {
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
