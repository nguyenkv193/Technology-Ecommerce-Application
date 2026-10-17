import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import ProductDetailView from '../ProductDetailView.vue'
import { createPinia, setActivePinia } from 'pinia'
import { catalogApi } from '@/api/catalogApi'
import { recommendationApi } from '@/api/recommendationApi'
import { behaviorApi } from '@/api/behaviorApi'
import { useCartStore } from '@/stores/cart'

vi.mock('vue-router', () => ({
  useRoute: () => ({
    params: { id: '1' }
  }),
  useRouter: () => ({
    push: vi.fn()
  })
}))

vi.mock('@/api/catalogApi', () => ({
  catalogApi: {
    getProductById: vi.fn(),
    getReviews: vi.fn(),
    getReviewSummary: vi.fn(),
    submitReview: vi.fn()
  }
}))

vi.mock('@/api/recommendationApi', () => ({
  recommendationApi: {
    getSimilar: vi.fn()
  }
}))

vi.mock('@/api/behaviorApi', () => ({
  behaviorApi: {
    track: vi.fn()
  }
}))

describe('ProductDetailView.vue', () => {
  const mockProduct = {
    id: 1,
    name: 'MacBook Pro 14 M3',
    slug: 'macbook-pro-14-m3',
    description: 'Pro laptop with Liquid Retina XDR display',
    brand: { id: 1, name: 'Apple' },
    category: { id: 1, name: 'Laptop' },
    minPrice: 39990000,
    images: [{ id: 1, url: 'https://example.com/mbp14.png' }],
    attributes: [
      { id: 1, name: 'CPU', value: 'M3 Pro' },
      { id: 2, name: 'RAM', value: '18GB' }
    ],
    variants: [
      {
        id: 201,
        productId: 1,
        sku: 'MBP14-M3-18GB',
        name: '18GB / 512GB SSD Space Black',
        price: 39990000,
        stock: 5
      }
    ]
  }

  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()

    vi.mocked(catalogApi.getProductById).mockResolvedValue({
      success: true,
      message: 'OK',
      data: mockProduct as any
    })

    vi.mocked(recommendationApi.getSimilar).mockResolvedValue({
      success: true,
      message: 'OK',
      data: { recommendations: [] } as any
    })

    vi.mocked(catalogApi.getReviews).mockResolvedValue({
      success: true,
      message: 'OK',
      data: { content: [] } as any
    })

    vi.mocked(catalogApi.getReviewSummary).mockResolvedValue({
      success: true,
      message: 'OK',
      data: { productId: 1, averageRating: 4.8, totalReviews: 5, reviews: { content: [] } } as any
    })
  })

  it('loads and renders product details and tracks view behavior', async () => {
    const wrapper = mount(ProductDetailView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' },
          HardwareSpecsTable: { props: ['attributes'], template: '<div>HardwareSpecsTable</div>' },
          RecommendationCarousel: true
        }
      }
    })
    await flushPromises()

    expect(catalogApi.getProductById).toHaveBeenCalledWith(1)
    expect(behaviorApi.track).toHaveBeenCalledWith('VIEW', 1)
    expect(wrapper.text()).toContain('MacBook Pro 14 M3')
    expect(wrapper.text()).toContain('Apple')
    expect(wrapper.text()).toContain('39.990.000')
  })

  it('adds product variant to cart on button click', async () => {
    const cartStore = useCartStore()
    const addToCartSpy = vi.spyOn(cartStore, 'addToCart').mockResolvedValue({} as any)

    const wrapper = mount(ProductDetailView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' },
          HardwareSpecsTable: { props: ['attributes'], template: '<div>HardwareSpecsTable</div>' },
          RecommendationCarousel: true
        }
      }
    })
    await flushPromises()

    const addBtn = wrapper.findAll('button').find(b => b.text().includes('Thêm vào giỏ'))
    expect(addBtn).toBeDefined()
    await addBtn!.trigger('click')
    await flushPromises()

    expect(addToCartSpy).toHaveBeenCalledWith(201, 1, 1)
  })

  it('renders "Không tìm thấy thông tin sản phẩm" when product is null', async () => {
    vi.mocked(catalogApi.getProductById).mockResolvedValueOnce({
      success: false,
      message: 'Not found',
      data: null as any
    })

    const wrapper = mount(ProductDetailView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' },
          HardwareSpecsTable: { props: ['attributes'], template: '<div>HardwareSpecsTable</div>' },
          RecommendationCarousel: true
        }
      }
    })
    await flushPromises()

    expect(wrapper.text()).toContain('Không tìm thấy thông tin sản phẩm')
  })
})
