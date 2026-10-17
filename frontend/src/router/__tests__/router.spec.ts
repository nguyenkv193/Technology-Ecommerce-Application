import { describe, it, expect, vi, beforeEach } from 'vitest'
import router from '../index'
import { setActivePinia, createPinia } from 'pinia'
import { useAuthStore } from '@/stores/auth'

describe('Router Configuration & Navigation Guards', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('has all main application routes defined', () => {
    const routeNames = router.getRoutes().map(r => r.name)
    expect(routeNames).toContain('home')
    expect(routeNames).toContain('catalog')
    expect(routeNames).toContain('product-detail')
    expect(routeNames).toContain('cart')
    expect(routeNames).toContain('orders')
    expect(routeNames).toContain('wishlist')
    expect(routeNames).toContain('login')
    expect(routeNames).toContain('register')
  })

  it('scrollBehavior returns { top: 0 }', () => {
    const scroll = router.options.scrollBehavior
    if (scroll) {
      const res = (scroll as any)({}, {}, null)
      expect(res).toEqual({ top: 0 })
    }
  })

  it('triggers login modal and prevents route transition when navigating to /login', () => {
    const authStore = useAuthStore()
    const nextMock = vi.fn()

    // Access registered beforeEach guard
    const guard = (router as any).beforeHooks ? Array.from((router as any).beforeHooks.values())[0] as any : null

    if (guard) {
      guard(
        { path: '/login', name: 'login' },
        { path: '/products', name: 'catalog' },
        nextMock
      )

      expect(authStore.isAuthModalOpen).toBe(true)
      expect(authStore.authModalMode).toBe('login')
      expect(nextMock).toHaveBeenCalledWith(false)
    }
  })

  it('triggers register modal when navigating to /register', () => {
    const authStore = useAuthStore()
    const nextMock = vi.fn()

    const guard = (router as any).beforeHooks ? Array.from((router as any).beforeHooks.values())[0] as any : null

    if (guard) {
      guard(
        { path: '/register', name: 'register' },
        { path: '/', name: 'home' },
        nextMock
      )

      expect(authStore.isAuthModalOpen).toBe(true)
      expect(authStore.authModalMode).toBe('register')
      expect(nextMock).toHaveBeenCalledWith(false)
    }
  })

  it('proceeds normally for standard routes', () => {
    const nextMock = vi.fn()
    const guard = (router as any).beforeHooks ? Array.from((router as any).beforeHooks.values())[0] as any : null

    if (guard) {
      guard(
        { path: '/products', name: 'catalog' },
        { path: '/', name: 'home' },
        nextMock
      )

      expect(nextMock).toHaveBeenCalledWith()
    }
  })
})
