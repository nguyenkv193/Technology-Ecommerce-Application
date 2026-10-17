import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import Navbar from '../Navbar.vue'
import { createPinia, setActivePinia } from 'pinia'

// Mock vue-router
const pushMock = vi.fn()
vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: pushMock,
    currentRoute: { value: { path: '/' } }
  }),
  useRoute: () => ({
    path: '/'
  })
}))

describe('Navbar.vue Component Test Suite', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
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
})
