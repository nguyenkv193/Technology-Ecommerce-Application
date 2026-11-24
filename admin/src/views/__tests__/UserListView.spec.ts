import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import UserListView from '../users/UserListView.vue'
import * as usersApi from '@/api/users'
import { useAdminAuthStore } from '@/stores/auth'

vi.mock('@/api/users', () => ({
  fetchAdminUsers: vi.fn(),
  updateAdminUser: vi.fn()
}))

vi.mock('vue-sonner', () => ({
  toast: {
    success: vi.fn(),
    error: vi.fn()
  }
}))

describe('UserListView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
    const authStore = useAdminAuthStore()
    authStore.user = {
      id: 1,
      email: 'admin@techstore.vn',
      fullName: 'System Admin',
      role: 'ADMIN',
      status: 'ACTIVE'
    }
  })

  it('renders user table and blocks another user after confirmation', async () => {
    vi.mocked(usersApi.fetchAdminUsers).mockResolvedValueOnce({
      content: [
        {
          id: 1,
          email: 'admin@techstore.vn',
          fullName: 'System Admin',
          role: 'ADMIN',
          status: 'ACTIVE',
          authProvider: 'LOCAL',
          createdAt: '2026-11-01T10:00:00Z'
        },
        {
          id: 2,
          email: 'khach@techstore.vn',
          fullName: 'Nguyen Van Khach',
          phone: '0912345678',
          role: 'USER',
          status: 'ACTIVE',
          authProvider: 'GOOGLE',
          createdAt: '2026-11-10T10:00:00Z'
        }
      ],
      page: 0,
      size: 10,
      totalElements: 2,
      totalPages: 1,
      last: true
    })

    vi.mocked(usersApi.updateAdminUser).mockResolvedValueOnce({
      id: 2,
      email: 'khach@techstore.vn',
      fullName: 'Nguyen Van Khach',
      phone: '0912345678',
      role: 'USER',
      status: 'BLOCKED',
      authProvider: 'GOOGLE',
      createdAt: '2026-11-10T10:00:00Z'
    })

    const wrapper = mount(UserListView)
    await flushPromises()

    expect(wrapper.text()).toContain('Quản lý Người dùng')
    expect(wrapper.text()).toContain('Nguyen Van Khach')
    expect(wrapper.text()).toContain('GOOGLE')

    const blockButtons = wrapper.findAll('button').filter(b => b.text() === 'Khóa')
    expect(blockButtons).toHaveLength(2)
    // Self row block button should be disabled
    expect((blockButtons[0].element as HTMLButtonElement).disabled).toBe(true)
    expect((blockButtons[1].element as HTMLButtonElement).disabled).toBe(false)

    await blockButtons[1].trigger('click')
    expect(wrapper.text()).toContain('Khóa tài khoản người dùng?')

    const confirmBtn = wrapper.findAll('button').find(b => b.text().includes('Khóa tài khoản'))
    expect(confirmBtn).toBeDefined()
    await confirmBtn!.trigger('click')
    await flushPromises()

    expect(usersApi.updateAdminUser).toHaveBeenCalledWith(2, { status: 'BLOCKED' })
    expect(wrapper.text()).toContain('Đã khóa')
  })
})
