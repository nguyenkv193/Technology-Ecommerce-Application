import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useAdminAuthStore } from '../auth'
import apiClient from '@/api/client'

vi.mock('@/api/client', () => ({
  ADMIN_TOKEN_KEY: 'techstore_admin_token',
  default: {
    post: vi.fn(),
    get: vi.fn(),
    interceptors: {
      request: { use: vi.fn() },
      response: { use: vi.fn() }
    }
  }
}))

describe('useAdminAuthStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('initializes with unauthenticated state', () => {
    const store = useAdminAuthStore()
    expect(store.isAuthenticated).toBe(false)
    expect(store.user).toBeNull()
    expect(store.token).toBeNull()
  })

  it('rejects login if user role is not ADMIN', async () => {
    const store = useAdminAuthStore()
    const mockResponse = {
      data: {
        success: true,
        data: {
          accessToken: 'fake-user-token',
          user: {
            id: 2,
            email: 'customer@gmail.com',
            fullName: 'Nguyen Van Customer',
            role: 'USER'
          }
        }
      }
    }
    vi.mocked(apiClient.post).mockResolvedValueOnce(mockResponse)

    await expect(store.login('customer@gmail.com', 'password123')).rejects.toThrow(
      'Tài khoản không có quyền truy cập Backoffice Admin'
    )
    expect(store.isAuthenticated).toBe(false)
    expect(store.token).toBeNull()
    expect(localStorage.getItem('techstore_admin_token')).toBeNull()
  })

  it('accepts login and saves session when user role is ADMIN', async () => {
    const store = useAdminAuthStore()
    const mockResponse = {
      data: {
        success: true,
        data: {
          accessToken: 'fake-admin-token',
          user: {
            id: 1,
            email: 'admin@techstore.vn',
            fullName: 'Administrator',
            role: 'ADMIN'
          }
        }
      }
    }
    vi.mocked(apiClient.post).mockResolvedValueOnce(mockResponse)

    await store.login('admin@techstore.vn', 'adminPass123')

    expect(store.isAuthenticated).toBe(true)
    expect(store.token).toBe('fake-admin-token')
    expect(store.user?.role).toBe('ADMIN')
    expect(localStorage.getItem('techstore_admin_token')).toBe('fake-admin-token')
  })

  it('clears session on logout', () => {
    const store = useAdminAuthStore()
    store.token = 'existing-token'
    store.user = { id: 1, email: 'admin@techstore.vn', fullName: 'Admin', role: 'ADMIN', status: 'ACTIVE' }
    localStorage.setItem('techstore_admin_token', 'existing-token')

    store.logout()

    expect(store.isAuthenticated).toBe(false)
    expect(store.token).toBeNull()
    expect(store.user).toBeNull()
    expect(localStorage.getItem('techstore_admin_token')).toBeNull()
  })
})
