import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useWishlistStore } from '../wishlist'
import { useAuthStore } from '../auth'
import { wishlistApi } from '@/api/wishlistApi'
import { behaviorApi } from '@/api/behaviorApi'

vi.mock('@/api/wishlistApi', () => ({
  wishlistApi: {
    getWishlist: vi.fn(),
    addToWishlist: vi.fn(),
    removeFromWishlist: vi.fn(),
    checkWishlist: vi.fn()
  }
}))

vi.mock('@/api/behaviorApi', () => ({
  behaviorApi: {
    track: vi.fn()
  }
}))

describe('useWishlistStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('resets wishlist when fetching without authentication', async () => {
    const authStore = useAuthStore()
    authStore.token = null

    const store = useWishlistStore()
    store.items = [{ productId: 10 }]
    store.wishlistedIds.add(10)

    await store.fetchWishlist()
    expect(store.items).toEqual([])
    expect(store.wishlistedIds.size).toBe(0)
    expect(wishlistApi.getWishlist).not.toHaveBeenCalled()
  })

  it('fetches wishlist and sets wishlistedIds when authenticated', async () => {
    const authStore = useAuthStore()
    authStore.token = 'auth_token'

    const mockWishlist = [
      { id: 1, productId: 101, productName: 'Sony WH-1000XM5', minPrice: 8000000 },
      { id: 2, productId: 102, productName: 'iPad Pro', minPrice: 25000000 }
    ]
    vi.mocked(wishlistApi.getWishlist).mockResolvedValueOnce({
      success: true,
      message: 'OK',
      data: mockWishlist
    })

    const store = useWishlistStore()
    await store.fetchWishlist()

    expect(store.loading).toBe(false)
    expect(store.items).toEqual(mockWishlist)
    expect(store.isWishlisted(101)).toBe(true)
    expect(store.isWishlisted(102)).toBe(true)
    expect(store.isWishlisted(999)).toBe(false)
  })

  it('opens login modal if unauthenticated when toggling wishlist', async () => {
    const authStore = useAuthStore()
    authStore.token = null

    const store = useWishlistStore()
    const res = await store.toggleWishlist({ id: 50, name: 'AirPods Pro' })

    expect(res).toEqual({ needLogin: true })
    expect(authStore.isAuthModalOpen).toBe(true)
    expect(authStore.authModalMode).toBe('login')
  })

  it('adds product to wishlist and tracks behavior if not in wishlist', async () => {
    const authStore = useAuthStore()
    authStore.token = 'auth_token'

    vi.mocked(wishlistApi.addToWishlist).mockResolvedValueOnce({
      success: true,
      message: 'Added',
      data: {}
    })

    const store = useWishlistStore()
    const product = { id: 77, name: 'Keychron Q1 Pro', minPrice: 4500000, brandName: 'Keychron', thumbnailUrl: 'keychron.jpg' }
    
    const res = await store.toggleWishlist(product)

    expect(res).toEqual({ wishlisted: true })
    expect(wishlistApi.addToWishlist).toHaveBeenCalledWith(77)
    expect(store.isWishlisted(77)).toBe(true)
    expect(store.items.length).toBe(1)
    expect(store.items[0].productName).toBe('Keychron Q1 Pro')
    expect(behaviorApi.track).toHaveBeenCalledWith('WISHLIST', 77)
  })

  it('removes product from wishlist if already in wishlist', async () => {
    const authStore = useAuthStore()
    authStore.token = 'auth_token'

    vi.mocked(wishlistApi.removeFromWishlist).mockResolvedValueOnce({
      success: true,
      message: 'Removed',
      data: undefined
    })

    const store = useWishlistStore()
    store.items = [{ productId: 77, productName: 'Keychron Q1 Pro' }]
    store.wishlistedIds.add(77)

    const res = await store.toggleWishlist({ id: 77 })

    expect(res).toEqual({ wishlisted: false })
    expect(wishlistApi.removeFromWishlist).toHaveBeenCalledWith(77)
    expect(store.isWishlisted(77)).toBe(false)
    expect(store.items.length).toBe(0)
  })
})
