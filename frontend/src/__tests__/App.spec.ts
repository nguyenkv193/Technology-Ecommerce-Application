import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import App from '../App.vue'
import { createPinia, setActivePinia } from 'pinia'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { useWishlistStore } from '@/stores/wishlist'

describe('App.vue Root Component', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('renders root layout components (Navbar, Footer, AuthModal)', () => {
    const wrapper = mount(App, {
      global: {
        stubs: {
          Navbar: { template: '<nav>NavbarStub</nav>' },
          Footer: { template: '<footer>FooterStub</footer>' },
          AuthModal: { template: '<div>AuthModalStub</div>' },
          Toaster: { template: '<div>ToasterStub</div>' },
          RouterView: { template: '<main>RouterViewStub</main>' }
        }
      }
    })

    expect(wrapper.text()).toContain('NavbarStub')
    expect(wrapper.text()).toContain('FooterStub')
    expect(wrapper.text()).toContain('AuthModalStub')
  })

  it('fetches profile, cart and wishlist on mount if authenticated', async () => {
    const authStore = useAuthStore()
    const cartStore = useCartStore()
    const wishlistStore = useWishlistStore()

    authStore.token = 'active_token'
    const profileSpy = vi.spyOn(authStore, 'fetchProfile').mockResolvedValue()
    const cartSpy = vi.spyOn(cartStore, 'fetchCart').mockResolvedValue()
    const wishlistSpy = vi.spyOn(wishlistStore, 'fetchWishlist').mockResolvedValue()

    mount(App, {
      global: {
        stubs: {
          Navbar: true,
          Footer: true,
          AuthModal: true,
          Toaster: true,
          RouterView: true
        }
      }
    })
    await flushPromises()

    expect(profileSpy).toHaveBeenCalled()
    expect(cartSpy).toHaveBeenCalled()
    expect(wishlistSpy).toHaveBeenCalled()
  })

  it('does not fetch profile, cart or wishlist on mount if not authenticated', async () => {
    const authStore = useAuthStore()
    const cartStore = useCartStore()
    const wishlistStore = useWishlistStore()

    authStore.token = null
    const profileSpy = vi.spyOn(authStore, 'fetchProfile').mockResolvedValue()
    const cartSpy = vi.spyOn(cartStore, 'fetchCart').mockResolvedValue()
    const wishlistSpy = vi.spyOn(wishlistStore, 'fetchWishlist').mockResolvedValue()

    mount(App, {
      global: {
        stubs: {
          Navbar: true,
          Footer: true,
          AuthModal: true,
          Toaster: true,
          RouterView: true
        }
      }
    })
    await flushPromises()

    expect(profileSpy).not.toHaveBeenCalled()
    expect(cartSpy).not.toHaveBeenCalled()
    expect(wishlistSpy).not.toHaveBeenCalled()
  })
})
