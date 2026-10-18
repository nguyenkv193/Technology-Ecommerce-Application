import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useAuthStore } from '../auth'
import { authApi } from '@/api/authApi'

vi.mock('@/api/authApi', () => ({
  authApi: {
    login: vi.fn(),
    register: vi.fn(),
    exchangeOAuthCode: vi.fn(),
    getProfile: vi.fn()
  }
}))

describe('useAuthStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('initializes with unauthenticated state when localStorage is empty', () => {
    const store = useAuthStore()
    expect(store.token).toBeNull()
    expect(store.user).toBeNull()
    expect(store.isAuthenticated).toBe(false)
    expect(store.isAdmin).toBe(false)
  })

  it('initializes with user and token if present in localStorage', () => {
    localStorage.setItem('techstore_access_token', 'mock_token_123')
    localStorage.setItem('techstore_user', JSON.stringify({ id: 1, email: 'admin@techstore.vn', role: 'ADMIN' }))

    const store = useAuthStore()
    expect(store.token).toBe('mock_token_123')
    expect(store.user?.email).toBe('admin@techstore.vn')
    expect(store.isAuthenticated).toBe(true)
    expect(store.isAdmin).toBe(true)
  })

  it('manages modal states properly', () => {
    const store = useAuthStore()
    const mockCallback = vi.fn()

    store.openLoginModal(mockCallback)
    expect(store.isAuthModalOpen).toBe(true)
    expect(store.authModalMode).toBe('login')
    expect(store.onAuthSuccess).toBe(mockCallback)

    store.openRegisterModal()
    expect(store.authModalMode).toBe('register')
    expect(store.onAuthSuccess).toBeNull()

    store.closeAuthModal()
    expect(store.isAuthModalOpen).toBe(false)
    expect(store.onAuthSuccess).toBeNull()

    store.openLogoutModal()
    expect(store.isLogoutModalOpen).toBe(true)
    store.closeLogoutModal()
    expect(store.isLogoutModalOpen).toBe(false)
  })

  it('handles login successfully and stores token/user in localStorage', async () => {
    const mockUser = { id: 1, email: 'user@techstore.vn', fullName: 'User 1', role: 'USER' }
    vi.mocked(authApi.login).mockResolvedValueOnce({
      success: true,
      message: 'OK',
      data: {
        accessToken: 'access_abc',
        refreshToken: 'refresh_xyz',
        tokenType: 'Bearer',
        expiresIn: 3600,
        user: mockUser
      }
    })

    const store = useAuthStore()
    const res = await store.login({ email: 'user@techstore.vn', password: 'password123' })

    expect(res.success).toBe(true)
    expect(store.token).toBe('access_abc')
    expect(store.user).toEqual(mockUser)
    expect(store.isAuthenticated).toBe(true)
    expect(localStorage.getItem('techstore_access_token')).toBe('access_abc')
    expect(JSON.parse(localStorage.getItem('techstore_user') || '{}')).toEqual(mockUser)
  })

  it('handles register successfully and stores token/user', async () => {
    const mockUser = { id: 2, email: 'new@techstore.vn', fullName: 'New User', role: 'USER' }
    vi.mocked(authApi.register).mockResolvedValueOnce({
      success: true,
      message: 'Created',
      data: {
        accessToken: 'reg_token',
        refreshToken: 'reg_refresh',
        tokenType: 'Bearer',
        expiresIn: 3600,
        user: mockUser
      }
    })

    const store = useAuthStore()
    const res = await store.register({ email: 'new@techstore.vn', password: '123', fullName: 'New User' })

    expect(res.success).toBe(true)
    expect(store.token).toBe('reg_token')
    expect(store.user).toEqual(mockUser)
  })

  it('handles OAuth code exchange and stores token/user', async () => {
    const mockUser = { id: 3, email: 'oauth@techstore.vn', fullName: 'OAuth User', role: 'USER' }
    vi.mocked(authApi.exchangeOAuthCode).mockResolvedValueOnce({
      success: true,
      message: 'OAuth login success',
      data: {
        accessToken: 'oauth_access',
        refreshToken: 'oauth_refresh',
        tokenType: 'Bearer',
        expiresIn: 3600,
        user: mockUser
      }
    })

    const store = useAuthStore()
    const res = await store.exchangeOAuthCode('oauth_code_123')

    expect(res.success).toBe(true)
    expect(store.token).toBe('oauth_access')
    expect(store.user).toEqual(mockUser)
    expect(localStorage.getItem('techstore_refresh_token')).toBe('oauth_refresh')
  })

  it('handles logout by clearing state and localStorage', () => {
    localStorage.setItem('techstore_access_token', 'token_to_remove')
    localStorage.setItem('techstore_user', JSON.stringify({ id: 1 }))

    const store = useAuthStore()
    store.logout()

    expect(store.token).toBeNull()
    expect(store.user).toBeNull()
    expect(store.isAuthenticated).toBe(false)
    expect(localStorage.getItem('techstore_access_token')).toBeNull()
    expect(localStorage.getItem('techstore_user')).toBeNull()
  })

  it('fetches profile and updates user when token exists', async () => {
    const store = useAuthStore()
    store.token = 'existing_token'

    const updatedUser = { id: 1, email: 'updated@techstore.vn', fullName: 'Updated', role: 'USER' }
    vi.mocked(authApi.getProfile).mockResolvedValueOnce({
      success: true,
      message: 'OK',
      data: updatedUser
    })

    await store.fetchProfile()
    expect(store.user).toEqual(updatedUser)
    expect(JSON.parse(localStorage.getItem('techstore_user') || '{}')).toEqual(updatedUser)
  })

  it('logs out if fetchProfile fails', async () => {
    const store = useAuthStore()
    store.token = 'bad_token'
    store.user = { id: 1, email: 'test@techstore.vn', fullName: 'Test', role: 'USER' }

    vi.mocked(authApi.getProfile).mockRejectedValueOnce(new Error('Unauthorized'))

    await store.fetchProfile()
    expect(store.token).toBeNull()
    expect(store.user).toBeNull()
  })
})
