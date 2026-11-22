import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import VietQrPaymentModal from '../VietQrPaymentModal.vue'
import { orderApi } from '@/api/orderApi'
import type { Order } from '@/types'

vi.mock('@/api/orderApi', () => ({
  orderApi: {
    getOrderById: vi.fn()
  }
}))

vi.mock('vue-sonner', () => ({
  toast: {
    success: vi.fn(),
    error: vi.fn(),
    info: vi.fn()
  }
}))

const createSampleOrder = (overrides: Partial<Order> = {}): Order => ({
  id: 88,
  orderCode: 'ORD20261122110000A1B2C3',
  userId: 1,
  recipientName: 'Nguyen Van A',
  phoneNumber: '0912345678',
  shippingAddress: 'Ha Noi',
  totalAmount: 15990000,
  shippingFee: 0,
  discountAmount: 0,
  finalAmount: 15990000,
  orderStatus: 'PENDING',
  paymentMethod: 'BANK_TRANSFER',
  paymentStatus: 'PENDING',
  paymentExpiresAt: new Date(Date.now() + 10 * 60 * 1000).toISOString(),
  items: [],
  createdAt: new Date().toISOString(),
  ...overrides
})

describe('VietQrPaymentModal.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders QR image, order transfer content, waiting banner, and check button', () => {
    const order = createSampleOrder()
    const wrapper = mount(VietQrPaymentModal, {
      props: { order },
      global: { stubs: { routerLink: { template: '<a><slot /></a>' } } }
    })

    expect(wrapper.find('[data-testid="vietqr-modal"]').exists()).toBe(true)
    expect(wrapper.find('[data-testid="vietqr-content"]').text()).toBe('ORD20261122110000A1B2C3')
    expect(wrapper.find('[data-testid="vietqr-waiting-banner"]').exists()).toBe(true)
    expect(wrapper.find('[data-testid="vietqr-check-btn"]').exists()).toBe(true)
  })

  it('checks payment status via orderApi.getOrderById and emits confirmed when PAID', async () => {
    const order = createSampleOrder()
    const paidOrder = createSampleOrder({
      paymentStatus: 'PAID',
      orderStatus: 'CONFIRMED',
      paidAt: new Date().toISOString()
    })
    vi.mocked(orderApi.getOrderById).mockResolvedValueOnce({
      success: true,
      message: 'OK',
      data: paidOrder
    })

    const wrapper = mount(VietQrPaymentModal, {
      props: { order },
      global: { stubs: { routerLink: { template: '<a><slot /></a>' } } }
    })

    await wrapper.find('[data-testid="vietqr-check-btn"]').trigger('click')
    await flushPromises()

    expect(orderApi.getOrderById).toHaveBeenCalledWith(88)
    expect(wrapper.emitted('confirmed')).toBeTruthy()
    expect(wrapper.emitted('confirmed')?.[0]).toEqual([paidOrder])
  })

  it('shows expired overlay and disables check button when paymentExpiresAt has passed', () => {
    const expiredOrder = createSampleOrder({
      paymentExpiresAt: new Date(Date.now() - 60 * 1000).toISOString()
    })

    const wrapper = mount(VietQrPaymentModal, {
      props: { order: expiredOrder },
      global: { stubs: { routerLink: { template: '<a><slot /></a>' } } }
    })

    expect(wrapper.find('[data-testid="vietqr-expired-overlay"]').exists()).toBe(true)
    expect((wrapper.find('[data-testid="vietqr-check-btn"]').element as HTMLButtonElement).disabled).toBe(true)
  })
})
