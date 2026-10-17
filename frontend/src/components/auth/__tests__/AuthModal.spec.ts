import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import AuthModal from '../AuthModal.vue'
import { createPinia, setActivePinia } from 'pinia'
import { useAuthStore } from '@/stores/auth'

vi.mock('vue-sonner', () => ({
  toast: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn()
  }
}))

describe('AuthModal.vue', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    document.body.innerHTML = ''
    vi.clearAllMocks()
  })

  it('does not render modal dialog when isAuthModalOpen is false', () => {
    const authStore = useAuthStore()
    authStore.isAuthModalOpen = false

    const wrapper = mount(AuthModal, {
      global: {
        stubs: {
          teleport: true
        }
      }
    })

    expect(wrapper.find('.modal-card').exists()).toBe(false)
  })

  it('renders login modal when isAuthModalOpen is true in login mode', () => {
    const authStore = useAuthStore()
    authStore.isAuthModalOpen = true
    authStore.authModalMode = 'login'

    const wrapper = mount(AuthModal, {
      global: {
        stubs: {
          teleport: true
        }
      }
    })

    expect(wrapper.text()).toContain('Chào mừng trở lại')
    expect(wrapper.text()).toContain('Địa chỉ Email')
    expect(wrapper.text()).toContain('Mật khẩu')
  })

  it('switches to register mode when clicking register tab', async () => {
    const authStore = useAuthStore()
    authStore.isAuthModalOpen = true
    authStore.authModalMode = 'login'

    const wrapper = mount(AuthModal, {
      global: {
        stubs: {
          teleport: true
        }
      }
    })

    const buttons = wrapper.findAll('button')
    const registerTab = buttons.find(b => b.text().includes('Đăng ký'))
    expect(registerTab).toBeDefined()
    await registerTab!.trigger('click')

    expect(authStore.authModalMode).toBe('register')
    expect(wrapper.text()).toContain('Tạo tài khoản TechStore')
    expect(wrapper.text()).toContain('Họ và tên')
    expect(wrapper.text()).toContain('Số điện thoại (tùy chọn)')
  })

  it('closes modal when clicking close button', async () => {
    const authStore = useAuthStore()
    authStore.isAuthModalOpen = true

    const wrapper = mount(AuthModal, {
      global: {
        stubs: {
          teleport: true
        }
      }
    })

    const closeBtn = wrapper.find('button[title="Đóng (Esc)"]')
    expect(closeBtn.exists()).toBe(true)
    await closeBtn.trigger('click')

    expect(authStore.isAuthModalOpen).toBe(false)
  })

  it('toggles password visibility', async () => {
    const authStore = useAuthStore()
    authStore.isAuthModalOpen = true
    authStore.authModalMode = 'login'

    const wrapper = mount(AuthModal, {
      global: {
        stubs: {
          teleport: true
        }
      }
    })

    const passwordInput = wrapper.find('input[autocomplete="current-password"]')
    expect(passwordInput.attributes('type')).toBe('password')

    const toggleEyeBtn = passwordInput.element.parentElement?.querySelector('button')
    expect(toggleEyeBtn).toBeDefined()
    await toggleEyeBtn?.dispatchEvent(new Event('click'))
    await wrapper.vm.$nextTick()

    expect(wrapper.find('input[autocomplete="current-password"]').attributes('type')).toBe('text')
  })

  it('disables submit button until terms are agreed', async () => {
    const authStore = useAuthStore()
    authStore.isAuthModalOpen = true
    authStore.authModalMode = 'login'

    const wrapper = mount(AuthModal, {
      global: {
        stubs: {
          teleport: true
        }
      }
    })

    const submitBtn = wrapper.find('button[type="submit"]')
    expect(submitBtn.attributes('disabled')).toBeDefined()

    const termsCheckbox = wrapper.find('input[type="checkbox"]')
    await termsCheckbox.setValue(true)

    expect(submitBtn.attributes('disabled')).toBeUndefined()
  })

  it('pre-fills initial login values properly', () => {
    const authStore = useAuthStore()
    authStore.isAuthModalOpen = true
    authStore.authModalMode = 'login'

    const wrapper = mount(AuthModal, {
      global: {
        stubs: {
          teleport: true
        }
      }
    })

    const emailInput = wrapper.find('input[autocomplete="email"]')
    expect((emailInput.element as HTMLInputElement).value).toBe('nguyenvana@gmail.com')
  })
})
