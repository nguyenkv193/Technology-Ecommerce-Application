import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import RecommendationCarousel from '../RecommendationCarousel.vue'

describe('RecommendationCarousel.vue', () => {
  it('renders title, subtitle, and badge correctly', () => {
    const wrapper = mount(RecommendationCarousel, {
      props: {
        title: 'Gợi ý cho bạn',
        subtitle: 'Dựa trên AI gợi ý',
        badge: 'AI Powered',
        items: []
      },
      global: {
        stubs: {
          ProductCard: true
        }
      }
    })

    expect(wrapper.text()).toContain('Gợi ý cho bạn')
    expect(wrapper.text()).toContain('Dựa trên AI gợi ý')
    expect(wrapper.text()).toContain('AI Powered')
  })

  it('renders loading skeleton when loading is true', () => {
    const wrapper = mount(RecommendationCarousel, {
      props: {
        title: 'Gợi ý cho bạn',
        loading: true
      },
      global: {
        stubs: {
          ProductCard: true
        }
      }
    })

    const skeletons = wrapper.findAll('.animate-pulse')
    expect(skeletons.length).toBe(4)
  })

  it('renders error message and emits retry on button click', async () => {
    const wrapper = mount(RecommendationCarousel, {
      props: {
        title: 'Gợi ý cho bạn',
        errorMessage: 'Không thể tải dữ liệu gợi ý'
      },
      global: {
        stubs: {
          ProductCard: true
        }
      }
    })

    expect(wrapper.text()).toContain('Không thể tải dữ liệu gợi ý')
    const retryBtn = wrapper.find('button')
    await retryBtn.trigger('click')

    expect(wrapper.emitted('retry')).toBeTruthy()
  })

  it('renders empty message when no items', () => {
    const wrapper = mount(RecommendationCarousel, {
      props: {
        title: 'Gợi ý cho bạn',
        items: [],
        emptyMessage: 'Không có gợi ý nào'
      },
      global: {
        stubs: {
          ProductCard: true
        }
      }
    })

    expect(wrapper.text()).toContain('Không có gợi ý nào')
  })

  it('renders ProductCard components when items are present', () => {
    const mockItems = [
      { id: 1, name: 'Sản phẩm 1', minPrice: 1000000, slug: 'sp-1', description: 'Desc 1' },
      { id: 2, name: 'Sản phẩm 2', minPrice: 2000000, slug: 'sp-2', description: 'Desc 2' }
    ]

    const wrapper = mount(RecommendationCarousel, {
      props: {
        title: 'Gợi ý cho bạn',
        items: mockItems
      },
      global: {
        stubs: {
          ProductCard: {
            props: ['product'],
            template: '<div class="product-card-stub">{{ product.name }}</div>'
          }
        }
      }
    })

    const cards = wrapper.findAll('.product-card-stub')
    expect(cards.length).toBe(2)
    expect(cards[0].text()).toBe('Sản phẩm 1')
    expect(cards[1].text()).toBe('Sản phẩm 2')
  })
})
