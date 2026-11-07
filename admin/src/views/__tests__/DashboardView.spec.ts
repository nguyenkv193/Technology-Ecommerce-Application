import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { setActivePinia, createPinia } from 'pinia'
import DashboardView from '../DashboardView.vue'
import * as dashboardApi from '@/api/dashboard'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: { template: '<div>Dashboard</div>' } },
    { path: '/products/create', component: { template: '<div>Create Product</div>' } },
    { path: '/orders', component: { template: '<div>Orders</div>' } },
    { path: '/system-ops', component: { template: '<div>Ops</div>' } }
  ]
})

vi.mock('@/api/dashboard', () => ({
  fetchDashboardMetrics: vi.fn(),
  fetchRecentOrders: vi.fn(),
  fetchDashboardOverview: vi.fn()
}))

describe('DashboardView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('renders dashboard metrics and quick action shortcuts', async () => {
    const mockOrder = {
      id: 101,
      orderCode: 'ORD-101',
      userId: 1,
      customerName: 'Le Minh',
      customerEmail: 'leminh@example.com',
      customerPhone: '0987654321',
      shippingAddress: 'Hanoi',
      paymentMethod: 'COD',
      paymentStatus: 'PAID',
      status: 'PENDING' as const,
      subtotal: 25000000,
      shippingFee: 0,
      discountAmount: 0,
      totalAmount: 25000000,
      createdAt: '2026-10-27T10:00:00Z',
      updatedAt: '2026-10-27T10:00:00Z'
    }

    vi.mocked(dashboardApi.fetchDashboardOverview).mockResolvedValueOnce({
      metrics: {
        totalRevenue: 125000000,
        totalOrders: 48,
        pendingOrders: 5,
        totalProducts: 120,
        lowStockCount: 3
      },
      recentOrders: [mockOrder],
      allOrders: [mockOrder]
    })

    const wrapper = mount(DashboardView, {
      global: {
        plugins: [router]
      }
    })

    // Wait for async fetch
    await new Promise((r) => setTimeout(r, 50))

    const text = wrapper.text()
    expect(text).toContain('Tổng doanh thu')
    expect(text).toContain('Tổng đơn hàng')
    expect(text).toContain('Đơn chờ xác nhận')
    expect(text).toContain('ORD-101')
  })
})
