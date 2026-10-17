import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useCartStore } from '../cart'
import { useAuthStore } from '../auth'
import { cartApi } from '@/api/cartApi'
import { behaviorApi } from '@/api/behaviorApi'

vi.mock('@/api/cartApi', () => ({
  cartApi: {
    getCart: vi.fn(),
    addToCart: vi.fn(),
    updateQuantity: vi.fn(),
    removeItem: vi.fn(),
    clearCart: vi.fn()
  }
}))

vi.mock('@/api/behaviorApi', () => ({
  behaviorApi: {
    track: vi.fn()
  }
}))

describe('useCartStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('computes totalItems and totalPrice correctly', () => {
    const store = useCartStore()
    expect(store.totalItems).toBe(0)
    expect(store.totalPrice).toBe(0)

    store.items = [
      { id: 1, variantId: 10, productId: 100, productName: 'Laptop', variantName: '16GB', sku: 'LAP-1', price: 20000000, quantity: 2, subTotal: 40000000 },
      { id: 2, variantId: 20, productId: 200, productName: 'Mouse', variantName: 'Wireless', sku: 'MOU-1', price: 500000, quantity: 3, subTotal: 1500000 }
    ]

    expect(store.totalItems).toBe(5)
    expect(store.totalPrice).toBe(41500000)
  })

  it('clears items on fetchCart when not authenticated', async () => {
    const authStore = useAuthStore()
    authStore.token = null

    const store = useCartStore()
    store.items = [{ id: 1 } as any]

    await store.fetchCart()
    expect(store.items).toEqual([])
    expect(cartApi.getCart).not.toHaveBeenCalled()
  })

  it('fetches cart successfully when authenticated', async () => {
    const authStore = useAuthStore()
    authStore.token = 'valid_token'

    const mockCartItems = [
      { id: 1, variantId: 1, productId: 10, productName: 'iPhone 15', variantName: '128GB', sku: 'IP15', price: 22000000, quantity: 1, subTotal: 22000000 }
    ]
    vi.mocked(cartApi.getCart).mockResolvedValueOnce({
      success: true,
      message: 'OK',
      data: { id: 1, userId: 1, items: mockCartItems, totalAmount: 22000000 } as any
    })

    const store = useCartStore()
    await store.fetchCart()

    expect(store.loading).toBe(false)
    expect(store.items).toEqual(mockCartItems)
  })

  it('adds item to cart and tracks behavior when productId is provided', async () => {
    const mockUpdatedItems = [
      { id: 1, variantId: 5, productId: 50, productName: 'MacBook', variantName: 'M3', sku: 'MB-M3', price: 30000000, quantity: 1, subTotal: 30000000 }
    ]
    vi.mocked(cartApi.addToCart).mockResolvedValueOnce({
      success: true,
      message: 'Added',
      data: { id: 1, userId: 1, items: mockUpdatedItems, totalAmount: 30000000 } as any
    })

    const store = useCartStore()
    const res = await store.addToCart(5, 1, 50)

    expect(res.success).toBe(true)
    expect(store.items).toEqual(mockUpdatedItems)
    expect(behaviorApi.track).toHaveBeenCalledWith('ADD_TO_CART', 50, 'quantity=1')
  })

  it('updates quantity of an item', async () => {
    const mockUpdatedItems = [
      { id: 1, variantId: 5, productId: 50, productName: 'MacBook', variantName: 'M3', sku: 'MB-M3', price: 30000000, quantity: 2, subTotal: 60000000 }
    ]
    vi.mocked(cartApi.updateQuantity).mockResolvedValueOnce({
      success: true,
      message: 'Updated',
      data: { id: 1, userId: 1, items: mockUpdatedItems, totalAmount: 60000000 } as any
    })

    const store = useCartStore()
    await store.updateQuantity(1, 2)
    expect(store.items).toEqual(mockUpdatedItems)
  })

  it('removes an item from cart', async () => {
    vi.mocked(cartApi.removeItem).mockResolvedValueOnce({
      success: true,
      message: 'Removed',
      data: { id: 1, userId: 1, items: [], totalAmount: 0 } as any
    })

    const store = useCartStore()
    await store.removeItem(1)
    expect(store.items).toEqual([])
  })

  it('clears cart and resets cart', async () => {
    vi.mocked(cartApi.clearCart).mockResolvedValueOnce({
      success: true,
      message: 'Cleared',
      data: null as any
    })

    const store = useCartStore()
    store.items = [{ id: 1 } as any]
    await store.clearCart()
    expect(store.items).toEqual([])

    store.items = [{ id: 2 } as any]
    store.resetCart()
    expect(store.items).toEqual([])
  })
})
