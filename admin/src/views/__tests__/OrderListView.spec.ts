import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { setActivePinia, createPinia } from 'pinia'
import OrderListView from '../orders/OrderListView.vue'
import * as ordersApi from '@/api/orders'

vi.mock('@/api/orders', () => ({
  fetchOrders: vi.fn(),
  updateOrderStatus: vi.fn(),
  cancelOrder: vi.fn()
}))

describe('OrderListView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('renders order list with order code and status filter tabs', async () => {
    vi.mocked(ordersApi.fetchOrders).mockResolvedValueOnce({
      content: [
        {
          id: 55,
          orderCode: 'ORD-TECH-55',
          userId: 10,
          customerName: 'Hoang Nam',
          customerEmail: 'nam@tech.vn',
          customerPhone: '0901234567',
          shippingAddress: '123 Nguyen Trai, Thanh Xuan, Hanoi',
          paymentMethod: 'COD',
          paymentStatus: 'PENDING',
          status: 'PENDING',
          subtotal: 18500000,
          shippingFee: 30000,
          discountAmount: 0,
          totalAmount: 18530000,
          createdAt: '2026-10-27T12:00:00Z',
          updatedAt: '2026-10-27T12:00:00Z'
        }
      ],
      page: 0,
      size: 10,
      totalElements: 1,
      totalPages: 1,
      last: true
    })

    const wrapper = mount(OrderListView)
    await new Promise((r) => setTimeout(r, 50))

    expect(wrapper.text()).toContain('ORD-TECH-55')
    expect(wrapper.text()).toContain('Hoang Nam')
    expect(wrapper.text()).toContain('Tất cả')
    expect(wrapper.text()).toContain('Chờ xác nhận')
  })
})
