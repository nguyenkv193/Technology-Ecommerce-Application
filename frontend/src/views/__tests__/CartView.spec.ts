import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import CartView from '../CartView.vue'
import { createPinia, setActivePinia } from 'pinia'
import { useCartStore } from '@/stores/cart'
import { useAuthStore } from '@/stores/auth'
import { orderApi } from '@/api/orderApi'
import { behaviorApi } from '@/api/behaviorApi'

vi.mock('@/api/orderApi', () => ({
  orderApi: {
    checkout: vi.fn()
  }
}))

vi.mock('@/api/behaviorApi', () => ({
  behaviorApi: {
    track: vi.fn()
  }
}))

describe('CartView.vue', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('renders empty cart state when cart items array is empty', () => {
    const cartStore = useCartStore()
    vi.spyOn(cartStore, 'fetchCart').mockResolvedValue()
    cartStore.items = []

    const wrapper = mount(CartView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' }
        }
      }
    })

    expect(wrapper.text()).toContain('Giỏ Hàng Thiết Bị')
    expect(wrapper.text()).toContain('Giỏ hàng của bạn đang trống')
    expect(wrapper.text()).toContain('Khám phá sản phẩm')
  })

  it('renders cart items with quantity controls and subtotal', async () => {
    const cartStore = useCartStore()
    vi.spyOn(cartStore, 'fetchCart').mockResolvedValue()
    const updateSpy = vi.spyOn(cartStore, 'updateQuantity').mockResolvedValue({} as any)
    const removeSpy = vi.spyOn(cartStore, 'removeItem').mockResolvedValue({} as any)

    cartStore.items = [
      {
        id: 1,
        variantId: 10,
        productId: 100,
        productName: 'iPhone 15 Pro',
        variantName: '256GB Titan Tự Nhiên',
        sku: 'IP15P-256',
        price: 25000000,
        quantity: 2,
        subTotal: 50000000,
        thumbnailUrl: 'iphone.png'
      }
    ]

    const wrapper = mount(CartView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' }
        }
      }
    })

    expect(wrapper.text()).toContain('iPhone 15 Pro')
    expect(wrapper.text()).toContain('256GB Titan Tự Nhiên')
    expect(wrapper.text()).toContain('IP15P-256')

    // Click increment quantity (+)
    const incBtn = wrapper.findAll('button').find(b => b.text() === '+')
    expect(incBtn).toBeDefined()
    await incBtn!.trigger('click')
    expect(updateSpy).toHaveBeenCalledWith(1, 3)

    // Click decrement quantity (-)
    const decBtn = wrapper.findAll('button').find(b => b.text() === '-')
    expect(decBtn).toBeDefined()
    await decBtn!.trigger('click')
    expect(updateSpy).toHaveBeenCalledWith(1, 1)

    // Click remove item (✕)
    const removeBtn = wrapper.findAll('button').find(b => b.text() === '✕')
    expect(removeBtn).toBeDefined()
    await removeBtn!.trigger('click')
    expect(removeSpy).toHaveBeenCalledWith(1)
  })

  it('opens login modal if unauthenticated user attempts checkout', async () => {
    const authStore = useAuthStore()
    const cartStore = useCartStore()
    authStore.token = null
    cartStore.items = [{ id: 1, price: 1000, quantity: 1, subTotal: 1000 } as any]

    const wrapper = mount(CartView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' }
        }
      }
    })

    const form = wrapper.find('form')
    await form.trigger('submit')

    expect(authStore.isAuthModalOpen).toBe(true)
    expect(authStore.authModalMode).toBe('login')
    expect(orderApi.checkout).not.toHaveBeenCalled()
  })

  it('completes checkout, displays success modal, and tracks purchase behavior', async () => {
    const authStore = useAuthStore()
    const cartStore = useCartStore()
    authStore.token = 'auth_token'
    authStore.user = { id: 1, fullName: 'Nguyen Van A', phoneNumber: '0901234567', email: 'a@techstore.vn', role: 'USER' }
    cartStore.items = [{ id: 1, variantId: 10, productId: 100, price: 20000000, quantity: 1, subTotal: 20000000 } as any]

    const mockOrder = {
      id: 99,
      orderCode: 'ORD-2026-999',
      recipientName: 'Nguyen Van A',
      finalAmount: 20000000,
      orderStatus: 'PENDING',
      paymentStatus: 'UNPAID',
      items: [{ id: 1, productId: 100, quantity: 1 }]
    }
    vi.mocked(orderApi.checkout).mockResolvedValueOnce({
      success: true,
      message: 'OK',
      data: mockOrder as any
    })
    const fetchCartSpy = vi.spyOn(cartStore, 'fetchCart').mockResolvedValue()

    const wrapper = mount(CartView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' }
        }
      }
    })

    const addressInput = wrapper.find('textarea')
    await addressInput.setValue('123 Cong Quynh, Quan 1, TP HCM')

    const form = wrapper.find('form')
    await form.trigger('submit')
    await flushPromises()

    expect(orderApi.checkout).toHaveBeenCalled()
    expect(behaviorApi.track).toHaveBeenCalledWith('PURCHASE', 100, 'order=ORD-2026-999')
    expect(fetchCartSpy).toHaveBeenCalled()
    expect(wrapper.text()).toContain('Đặt Hàng Thành Công')
    expect(wrapper.text()).toContain('ORD-2026-999')
  })
})
