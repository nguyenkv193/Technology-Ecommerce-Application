import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import Navbar from '../Navbar.vue'
import { createPinia, setActivePinia } from 'pinia'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { useWishlistStore } from '@/stores/wishlist'
import { toast } from 'vue-sonner'

// Mock vue-router
const pushMock = vi.fn()
let currentPath = '/'
vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: pushMock,
    currentRoute: { value: { path: currentPath } }
  }),
  useRoute: () => ({
    path: currentPath
  })
}))

vi.mock('vue-sonner', () => ({
  toast: {
    success: vi.fn(),
    error: vi.fn()
  }
}))

describe('Navbar.vue Component Test Suite', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    currentPath = '/'
    vi.clearAllMocks()
    document.body.innerHTML = ''
  })

  it('TC-NAV-01: renders desktop SearchBox and mobile search trigger button', () => {
    const wrapper = mount(Navbar, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' },
          CategoryMegaMenu: { template: '<div>CategoryMenu</div>' }
        }
      }
    })

    // SearchBox should be rendered
    expect(wrapper.findComponent({ name: 'SearchBox' }).exists()).toBe(true)

    // Mobile trigger button
    const mobileBtn = wrapper.find('[data-testid="mobile-search-trigger"]')
    expect(mobileBtn.exists()).toBe(true)
  })

  it('TC-NAV-02: opens mobile search drawer modal when clicking mobile search button', async () => {
    const wrapper = mount(Navbar, {
      attachTo: document.body,
      global: {
        stubs: {
          transition: false,
          routerLink: { template: '<a><slot /></a>' },
          CategoryMegaMenu: { template: '<div>CategoryMenu</div>' }
        }
      }
    })

    const mobileBtn = wrapper.find('[data-testid="mobile-search-trigger"]')
    await mobileBtn.trigger('click')
    await wrapper.vm.$nextTick()

    expect(document.body.innerHTML).toContain('Đóng')
    wrapper.unmount()
  })

  it('renders login button when unauthenticated and opens modal on click', async () => {
    const authStore = useAuthStore()
    authStore.token = null

    const wrapper = mount(Navbar, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' },
          CategoryMegaMenu: { template: '<div>CategoryMenu</div>' }
        }
      }
    })

    const buttons = wrapper.findAll('button')
    const loginButton = buttons.find(b => b.text().includes('Đăng nhập'))
    expect(loginButton).toBeDefined()

    await loginButton!.trigger('click')
    expect(authStore.isAuthModalOpen).toBe(true)
    expect(authStore.authModalMode).toBe('login')
  })

  it('renders cart and wishlist badges when items are present', () => {
    const cartStore = useCartStore()
    const wishlistStore = useWishlistStore()

    cartStore.items = [
      { id: 1, quantity: 3 } as any
    ]
    wishlistStore.items = [
      { productId: 10 } as any,
      { productId: 20 } as any
    ]

    const wrapper = mount(Navbar, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' },
          CategoryMegaMenu: { template: '<div>CategoryMenu</div>' }
        }
      }
    })

    expect(wrapper.text()).toContain('3') // Cart badge
    expect(wrapper.text()).toContain('2') // Wishlist badge
  })

  it('displays user profile and handles logout', async () => {
    const authStore = useAuthStore()
    authStore.token = 'user_token'
    authStore.user = { id: 1, fullName: 'Nguyen Van B', email: 'b@techstore.vn', role: 'USER', status: 'ACTIVE' }
    currentPath = '/orders'

    const wrapper = mount(Navbar, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' },
          CategoryMegaMenu: { template: '<div>CategoryMenu</div>' }
        }
      }
    })

    expect(wrapper.text()).toContain('Nguyen Van B')

    // Open user menu
    const userBtn = wrapper.find('button.bg-zinc-100')
    await userBtn.trigger('click')

    expect(wrapper.text()).toContain('b@techstore.vn')
    expect(wrapper.text()).toContain('Lịch sử đơn hàng')

    // Click logout
    const logoutBtn = wrapper.findAll('button').find(b => b.text().includes('Đăng xuất'))
    expect(logoutBtn).toBeDefined()
    await logoutBtn!.trigger('click')

    expect(authStore.token).toBeNull()
    expect(toast.success).toHaveBeenCalledWith('Đã đăng xuất')
    expect(pushMock).toHaveBeenCalledWith('/')
  })
})
