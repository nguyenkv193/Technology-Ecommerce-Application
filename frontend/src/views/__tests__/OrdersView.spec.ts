import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import OrdersView from '../OrdersView.vue'
import { orderApi } from '@/api/orderApi'

vi.mock('@/api/orderApi', () => ({
  orderApi: {
    getMyOrders: vi.fn(),
    getOrderById: vi.fn(),
    cancelOrder: vi.fn(),
    confirmPayment: vi.fn()
  }
}))

describe('OrdersView.vue', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
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
    const cancelBtn = wrapper.findAll('button').find(b => b.text().includes('Hủy đơn'))
    expect(cancelBtn).toBeTruthy()
    await cancelBtn!.trigger('click')

    expect(wrapper.find('[data-testid="tab-orders"]').attributes('aria-selected')).toBe('true')
    expect(confirmSpy).toHaveBeenCalled()
    expect(orderApi.cancelOrder).toHaveBeenCalledWith(10)
  })

  it('shows VietQR button for pending BANK_TRANSFER order and opens VietQR modal', async () => {
    vi.mocked(orderApi.getMyOrders).mockResolvedValueOnce({
      success: true,
      message: 'OK',
      data: {
        content: [
          {
            id: 22,
            orderCode: 'ORD-2026-QR01',
            createdAt: '2026-03-01T10:00:00Z',
            orderStatus: 'PENDING',
            paymentMethod: 'BANK_TRANSFER',
            paymentStatus: 'PENDING',
            recipientName: 'Le Van B',
            phoneNumber: '0912345678',
            shippingAddress: 'Ha Noi',
            finalAmount: 19990000,
            items: []
          }
        ],
        totalElements: 1
      } as any
    })

    const wrapper = mount(OrdersView, {
      global: { stubs: { routerLink: { template: '<a><slot /></a>' } } }
    })
    await flushPromises()

    expect(wrapper.text()).toContain('VietQR • Chờ thanh toán')
    const qrBtn = wrapper.find('[data-testid="open-vietqr-btn"]')
    expect(qrBtn.exists()).toBe(true)

    await qrBtn.trigger('click')
    expect(wrapper.find('[data-testid="vietqr-modal"]').exists()).toBe(true)
    expect(wrapper.find('[data-testid="vietqr-check-btn"]').exists()).toBe(true)
  })

  it('supports filtering by status, keyword search, and pagination', async () => {
    vi.mocked(orderApi.getMyOrders).mockResolvedValue({
      success: true,
      message: 'OK',
      data: {
        content: [
          {
            id: 1,
            orderCode: 'ORD-001',
            createdAt: '2026-03-01T10:00:00Z',
            orderStatus: 'PENDING',
            recipientName: 'Nguyen Van A',
            phoneNumber: '0912345678',
            shippingAddress: 'Ha Noi',
            finalAmount: 5000000,
            items: []
          }
        ],
        totalElements: 12,
        totalPages: 3
      } as any
    })

    const wrapper = mount(OrdersView, {
      global: { stubs: { routerLink: { template: '<a><slot /></a>' } } }
    })
    await flushPromises()

    expect(wrapper.find('[data-testid="orders-pagination"]').exists()).toBe(true)

    await wrapper.find('[data-testid="order-status-tab-DELIVERED"]').trigger('click')
    await flushPromises()
    expect(orderApi.getMyOrders).toHaveBeenLastCalledWith({
      page: 0,
      size: 5,
      status: 'DELIVERED',
      keyword: undefined
    })

    await wrapper.find('[data-testid="order-search-input"]').setValue('ORD-001')
    await wrapper.find('[data-testid="order-search-btn"]').trigger('click')
    await flushPromises()
    expect(orderApi.getMyOrders).toHaveBeenLastCalledWith({
      page: 0,
      size: 5,
      status: 'DELIVERED',
      keyword: 'ORD-001'
    })

    await wrapper.find('[data-testid="orders-next-page"]').trigger('click')
    await flushPromises()
    expect(orderApi.getMyOrders).toHaveBeenLastCalledWith({
      page: 1,
      size: 5,
      status: 'DELIVERED',
      keyword: 'ORD-001'
    })

    await wrapper.find('[data-testid="order-reset-btn"]').trigger('click')
    await flushPromises()
    expect(orderApi.getMyOrders).toHaveBeenLastCalledWith({
      page: 0,
      size: 5,
      status: undefined,
      keyword: undefined
    })
  })
})
