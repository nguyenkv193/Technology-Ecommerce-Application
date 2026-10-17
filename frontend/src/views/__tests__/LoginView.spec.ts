import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import LoginView from '../LoginView.vue'
import { createPinia, setActivePinia } from 'pinia'
import { useAuthStore } from '@/stores/auth'
import { toast } from 'vue-sonner'

const pushMock = vi.fn()
const replaceMock = vi.fn()
let mockQuery: Record<string, any> = {}

vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: pushMock,
    replace: replaceMock
  }),
  useRoute: () => ({
    query: mockQuery,
    name: 'login'
  })
}))

vi.mock('vue-sonner', () => ({
  toast: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn()
  }
}))

describe('LoginView.vue', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    mockQuery = {}
    vi.clearAllMocks()
  })

  it('redirects to home if user is already authenticated on mount', () => {
    const authStore = useAuthStore()
    authStore.token = 'existing_token'

    mount(LoginView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' }
        }
      }
    })

    expect(replaceMock).toHaveBeenCalledWith('/')
  })

  it('renders login view with demo account buttons and slide controls', () => {
    const authStore = useAuthStore()
    authStore.token = null

    const wrapper = mount(LoginView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' }
        }
      }
    })

    expect(wrapper.text()).toContain('Admin')
    expect(wrapper.text()).toContain('Khách')
    expect(wrapper.text()).toContain('Hệ Thống Xác Thực Chuẩn Enterprise')
  })

  it('fills demo credentials when clicking demo buttons', async () => {
    const wrapper = mount(LoginView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' }
        }
      }
    })

    const buttons = wrapper.findAll('button')
    const adminBtn = buttons.find(b => b.text().includes('Admin'))
    expect(adminBtn).toBeDefined()
    await adminBtn!.trigger('click')

    const emailInput = wrapper.find('input[type="email"]')
    expect((emailInput.element as HTMLInputElement).value).toBe('admin@techstore.com')
  })

  it('switches between Login and Register tabs', async () => {
    const wrapper = mount(LoginView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' }
        }
      }
    })

    const buttons = wrapper.findAll('button')
    const registerTab = buttons.find(b => b.text() === 'Đăng ký')
    expect(registerTab).toBeDefined()
    await registerTab!.trigger('click')

    expect(wrapper.text()).toContain('Tạo tài khoản mới')
    expect(wrapper.text()).toContain('Họ và tên')
  })

  it('logs in successfully and redirects to target redirect query', async () => {
    mockQuery = { redirect: '/cart' }
    const authStore = useAuthStore()
    const loginSpy = vi.spyOn(authStore, 'login').mockResolvedValueOnce({ success: true, message: 'OK', data: {} as any })

    const wrapper = mount(LoginView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' }
        }
      }
    })

    // Fill credentials using Admin button
    const adminBtn = wrapper.findAll('button').find(b => b.text().includes('Admin'))
    expect(adminBtn).toBeDefined()
    await adminBtn!.trigger('click')

    // Agree terms
    const checkbox = wrapper.find('input[type="checkbox"]')
    await checkbox.setValue(true)

    // Submit form
    const form = wrapper.find('form')
    await form.trigger('submit')
    await flushPromises()

    expect(loginSpy).toHaveBeenCalledWith({
      email: 'admin@techstore.com',
      password: 'Admin@123'
    })
    expect(toast.success).toHaveBeenCalledWith('Đăng nhập thành công')
    expect(pushMock).toHaveBeenCalledWith('/cart')
  })
})
