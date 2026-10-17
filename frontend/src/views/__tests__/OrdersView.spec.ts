import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import OrdersView from '../OrdersView.vue'
import { orderApi } from '@/api/orderApi'

vi.mock('@/api/orderApi', () => ({
  orderApi: {
    getMyOrders: vi.fn(),
    cancelOrder: vi.fn()
  }
}))

describe('OrdersView.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders loading state initially while fetching', () => {
    vi.mocked(orderApi.getMyOrders).mockReturnValue(new Promise(() => {}))

    const wrapper = mount(OrdersView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' }
        }
      }
    })

    expect(wrapper.text()).toContain('Đang tải danh sách đơn hàng...')
  })

  it('renders empty orders state when no orders returned', async () => {
    vi.mocked(orderApi.getMyOrders).mockResolvedValueOnce({
      success: true,
      message: 'OK',
      data: { content: [], totalElements: 0 } as any
    })

    const wrapper = mount(OrdersView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' }
        }
      }
    })
    await flushPromises()

    expect(wrapper.text()).toContain('Lịch Sử Đơn Hàng')
    expect(wrapper.text()).toContain('Bạn chưa có đơn hàng nào')
    expect(wrapper.text()).toContain('Khám phá sản phẩm')
  })

  it('renders list of orders and allows cancelling pending order', async () => {
    const mockOrders = [
      {
        id: 10,
        orderCode: 'ORD-1001',
        createdAt: '2026-03-01T10:00:00Z',
        orderStatus: 'PENDING',
        recipientName: 'Tran Van C',
        phoneNumber: '0988776655',
        shippingAddress: '456 Hai Ba Trung, Ha Noi',
        finalAmount: 15000000,
        items: [
          {
            id: 1,
            productName: 'iPad Air 5',
            variantName: '64GB Wi-Fi',
            quantity: 1,
            totalPrice: 15000000,
            thumbnailUrl: 'ipad.png'
          }
        ]
      }
    ]

    vi.mocked(orderApi.getMyOrders).mockResolvedValueOnce({
      success: true,
      message: 'OK',
      data: { content: mockOrders, totalElements: 1 } as any
    })
    vi.mocked(orderApi.cancelOrder).mockResolvedValueOnce({
      success: true,
      message: 'Cancelled',
      data: {} as any
    })

    // Confirm prompt mock
    const confirmSpy = vi.spyOn(window, 'confirm').mockReturnValue(true)

    const wrapper = mount(OrdersView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' }
        }
      }
    })
    await flushPromises()

    expect(wrapper.text()).toContain('ORD-1001')
    expect(wrapper.text()).toContain('Chờ xác nhận')
    expect(wrapper.text()).toContain('iPad Air 5')
    expect(wrapper.text()).toContain('Tran Van C')

    // Click cancel button
    const cancelBtn = wrapper.find('button')
    expect(cancelBtn.text()).toContain('Hủy đơn')
    await cancelBtn.trigger('click')

    expect(confirmSpy).toHaveBeenCalled()
    expect(orderApi.cancelOrder).toHaveBeenCalledWith(10)
  })
})
