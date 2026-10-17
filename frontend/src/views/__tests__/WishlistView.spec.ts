import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import WishlistView from '../WishlistView.vue'
import { createPinia, setActivePinia } from 'pinia'
import { useWishlistStore } from '@/stores/wishlist'

describe('WishlistView.vue', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('calls fetchWishlist on mounted and renders empty state when no items', () => {
    const store = useWishlistStore()
    const fetchSpy = vi.spyOn(store, 'fetchWishlist').mockResolvedValue()
    store.items = []

    const wrapper = mount(WishlistView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' },
          ProductCard: true
        }
      }
    })

    expect(fetchSpy).toHaveBeenCalled()
    expect(wrapper.text()).toContain('Danh Sách Yêu Thích')
    expect(wrapper.text()).toContain('Danh sách yêu thích đang trống')
    expect(wrapper.text()).toContain('Khám phá sản phẩm')
  })

  it('renders ProductCard items when wishlist has products', () => {
    const store = useWishlistStore()
    vi.spyOn(store, 'fetchWishlist').mockResolvedValue()
    store.items = [
      {
        id: 1,
        productId: 101,
        productName: 'iPhone 16 Pro',
        thumbnailUrl: 'iphone.jpg',
        minPrice: 28990000,
        brandName: 'Apple',
        categoryName: 'Điện thoại'
      },
      {
        id: 2,
        productId: 102,
        productName: 'Sony WH-1000XM5',
        thumbnailUrl: 'sony.jpg',
        minPrice: 7990000,
        brandName: 'Sony',
        categoryName: 'Tai nghe'
      }
    ]

    const wrapper = mount(WishlistView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' },
          ProductCard: {
            props: ['product'],
            template: '<div class="product-card-stub">{{ product.name }}</div>'
          }
        }
      }
    })

    const cards = wrapper.findAll('.product-card-stub')
    expect(cards.length).toBe(2)
    expect(cards[0].text()).toBe('iPhone 16 Pro')
    expect(cards[1].text()).toBe('Sony WH-1000XM5')
  })
})
