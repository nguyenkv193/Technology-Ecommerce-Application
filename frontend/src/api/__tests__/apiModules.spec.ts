import { describe, it, expect, vi, beforeEach } from 'vitest'
import { apiClient } from '../client'
import { catalogApi } from '../catalogApi'
import { authApi } from '../authApi'
import { cartApi } from '../cartApi'
import { orderApi } from '../orderApi'
import { recommendationApi } from '../recommendationApi'
import { behaviorApi } from '../behaviorApi'
import { wishlistApi } from '../wishlistApi'

vi.mock('../client', () => ({
  apiClient: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn()
  }
}))

describe('Frontend API Modules', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('catalogApi', () => {
    it('calls getBestSellers with limit', async () => {
      vi.mocked(apiClient.get).mockResolvedValueOnce({ success: true, data: [] })
      await catalogApi.getBestSellers(4)
      expect(apiClient.get).toHaveBeenCalledWith('/products/best-sellers', { params: { limit: 4 } })
    })

    it('calls getCategories and getBrands', async () => {
      vi.mocked(apiClient.get).mockResolvedValueOnce({ success: true, data: [] })
      await catalogApi.getCategories()
      expect(apiClient.get).toHaveBeenCalledWith('/categories')

      await catalogApi.getBrands()
      expect(apiClient.get).toHaveBeenCalledWith('/brands')
    })

    it('calls getProductById and getProductBySlug', async () => {
      vi.mocked(apiClient.get).mockResolvedValueOnce({ success: true, data: {} })
      await catalogApi.getProductById(10)
      expect(apiClient.get).toHaveBeenCalledWith('/products/10')

      await catalogApi.getProductBySlug('macbook-pro')
      expect(apiClient.get).toHaveBeenCalledWith('/products/slug/macbook-pro')
    })

    it('calls getSearchSuggestions with params', async () => {
      vi.mocked(apiClient.get).mockResolvedValueOnce({ success: true, data: [] })
      await catalogApi.getSearchSuggestions('macbook', 5)
      expect(apiClient.get).toHaveBeenCalledWith('/products/suggestions', { params: { keyword: 'macbook', limit: 5 } })
    })

    it('calls getReviews, getReviewSummary and submitReview', async () => {
      vi.mocked(apiClient.get).mockResolvedValueOnce({ success: true, data: {} })
      await catalogApi.getReviews(1, { page: 0 })
      expect(apiClient.get).toHaveBeenCalledWith('/reviews/product/1', { params: { page: 0 } })

      await catalogApi.getReviewSummary(1)
      expect(apiClient.get).toHaveBeenCalledWith('/reviews/product/1/summary')

      vi.mocked(apiClient.post).mockResolvedValueOnce({ success: true, data: {} })
      await catalogApi.submitReview({ productId: 1, rating: 5, comment: 'Great!' })
      expect(apiClient.post).toHaveBeenCalledWith('/reviews', { productId: 1, rating: 5, comment: 'Great!' })
    })
  })

  describe('authApi', () => {
    it('calls login, register, refreshToken, exchangeOAuthCode, and getProfile', async () => {
      vi.mocked(apiClient.post).mockResolvedValueOnce({ success: true, data: {} })
      await authApi.login({ email: 'test@techstore.vn', password: '123' })
      expect(apiClient.post).toHaveBeenCalledWith('/auth/login', { email: 'test@techstore.vn', password: '123' })

      await authApi.register({ email: 'test@techstore.vn', password: '123', fullName: 'Tester' })
      expect(apiClient.post).toHaveBeenCalledWith('/auth/register', { email: 'test@techstore.vn', password: '123', fullName: 'Tester' })

      await authApi.refreshToken('tok_123')
      expect(apiClient.post).toHaveBeenCalledWith('/auth/refresh', { refreshToken: 'tok_123' })

      await authApi.exchangeOAuthCode('oauth_code_123')
      expect(apiClient.post).toHaveBeenCalledWith('/auth/oauth2/exchange', { code: 'oauth_code_123' })

      vi.mocked(apiClient.get).mockResolvedValueOnce({ success: true, data: {} })
      await authApi.getProfile()
      expect(apiClient.get).toHaveBeenCalledWith('/auth/me')
    })
  })

  describe('cartApi', () => {
    it('calls getCart, addToCart, updateQuantity, removeItem, clearCart', async () => {
      vi.mocked(apiClient.get).mockResolvedValueOnce({ success: true, data: {} })
      await cartApi.getCart()
      expect(apiClient.get).toHaveBeenCalledWith('/cart')

      vi.mocked(apiClient.post).mockResolvedValueOnce({ success: true, data: {} })
      await cartApi.addToCart(1, 2)
      expect(apiClient.post).toHaveBeenCalledWith('/cart/items', { variantId: 1, quantity: 2 })

      vi.mocked(apiClient.put).mockResolvedValueOnce({ success: true, data: {} })
      await cartApi.updateQuantity(10, 3)
      expect(apiClient.put).toHaveBeenCalledWith('/cart/items/10', { quantity: 3 })

      vi.mocked(apiClient.delete).mockResolvedValueOnce({ success: true, data: {} })
      await cartApi.removeItem(10)
      expect(apiClient.delete).toHaveBeenCalledWith('/cart/items/10')

      await cartApi.clearCart()
      expect(apiClient.delete).toHaveBeenCalledWith('/cart')
    })
  })

  describe('orderApi', () => {
    it('calls checkout, getMyOrders, getOrderById, cancelOrder, updateStatus', async () => {
      const checkoutData = {
        recipientName: 'Nguyen Van A',
        phoneNumber: '0901234567',
        shippingAddress: '123 Le Loi',
        paymentMethod: 'COD' as const
      }
      vi.mocked(apiClient.post).mockResolvedValueOnce({ success: true, data: {} })
      await orderApi.checkout(checkoutData)
      expect(apiClient.post).toHaveBeenCalledWith('/orders/checkout', checkoutData)

      vi.mocked(apiClient.get).mockResolvedValueOnce({ success: true, data: {} })
      await orderApi.getMyOrders({ page: 0 })
      expect(apiClient.get).toHaveBeenCalledWith('/orders', { params: { page: 0 } })

      await orderApi.getOrderById(5)
      expect(apiClient.get).toHaveBeenCalledWith('/orders/5')

      vi.mocked(apiClient.put).mockResolvedValueOnce({ success: true, data: {} })
      await orderApi.cancelOrder(5)
      expect(apiClient.put).toHaveBeenCalledWith('/orders/5/cancel')

      await orderApi.updateStatus(5, { orderStatus: 'DELIVERED' })
      expect(apiClient.put).toHaveBeenCalledWith('/orders/5/status', { orderStatus: 'DELIVERED' })
    })
  })

  describe('recommendationApi', () => {
    it('calls getPersonalized and getSimilar', async () => {
      vi.mocked(apiClient.get).mockResolvedValueOnce({ success: true, data: {} })
      await recommendationApi.getPersonalized(8)
      expect(apiClient.get).toHaveBeenCalledWith('/recommendations/me', { params: { limit: 8 } })

      await recommendationApi.getSimilar(10, 4)
      expect(apiClient.get).toHaveBeenCalledWith('/recommendations/similar/10', { params: { limit: 4 } })
    })
  })

  describe('wishlistApi', () => {
    it('calls getWishlist, addToWishlist, removeFromWishlist, checkWishlist', async () => {
      vi.mocked(apiClient.get).mockResolvedValueOnce({ success: true, data: [] })
      await wishlistApi.getWishlist()
      expect(apiClient.get).toHaveBeenCalledWith('/wishlist')

      vi.mocked(apiClient.post).mockResolvedValueOnce({ success: true, data: {} })
      await wishlistApi.addToWishlist(99)
      expect(apiClient.post).toHaveBeenCalledWith('/wishlist/99')

      vi.mocked(apiClient.delete).mockResolvedValueOnce({ success: true, data: {} })
      await wishlistApi.removeFromWishlist(99)
      expect(apiClient.delete).toHaveBeenCalledWith('/wishlist/99')

      await wishlistApi.checkWishlist(99)
      expect(apiClient.get).toHaveBeenCalledWith('/wishlist/check/99')
    })
  })

  describe('behaviorApi', () => {
    it('tracks behavior with sessionId and action payload', async () => {
      vi.mocked(apiClient.post).mockResolvedValueOnce({ success: true })
      await behaviorApi.track('VIEW', 123, 'section=hero')

      expect(apiClient.post).toHaveBeenCalledWith(
        '/behaviors/track',
        expect.objectContaining({
          actionType: 'VIEW',
          productId: 123,
          actionValue: 'section=hero'
        })
      )
    })
  })
})
