import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import HomeView from '../HomeView.vue'
import { createPinia, setActivePinia } from 'pinia'
import { catalogApi } from '@/api/catalogApi'
import { recommendationApi } from '@/api/recommendationApi'

vi.mock('@/api/catalogApi', () => ({
  catalogApi: {
    getBestSellers: vi.fn()
  }
}))

vi.mock('@/api/recommendationApi', () => ({
  recommendationApi: {
    getPersonalized: vi.fn()
  }
}))

describe('HomeView.vue', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()

    vi.mocked(catalogApi.getBestSellers).mockResolvedValue({
      success: true,
      message: 'OK',
      data: [
        { id: 1, name: 'iPhone 16 Pro', minPrice: 28000000, slug: 'ip16', description: 'Desc' }
      ]
    })

    vi.mocked(recommendationApi.getPersonalized).mockResolvedValue({
      success: true,
      message: 'OK',
      data: {
        recommendations: [
          {
            product: { id: 2, name: 'MacBook Air M3', minPrice: 27000000, slug: 'mba-m3', description: 'Desc' },
            reason: 'Phù hợp với nhu cầu thiết kế',
            score: 0.95
          }
        ]
      } as any
    })
  })

  it('renders hero flagship banner with headline and CTA buttons', () => {
    const wrapper = mount(HomeView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' },
          FeaturedCategoriesGrid: true,
          RecommendationCarousel: true,
          ProductCard: true
        }
      }
    })

    expect(wrapper.text()).toContain('Thiết Kế Đỉnh Cao.')
    expect(wrapper.text()).toContain('Hiệu Năng Vượt Bậc.')
    expect(wrapper.text()).toContain('Khám phá MacBook Pro')
  })

  it('fetches best sellers and personalized products on mount', async () => {
    const wrapper = mount(HomeView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' },
          FeaturedCategoriesGrid: true,
          RecommendationCarousel: true,
          ProductCard: true
        }
      }
    })
    await flushPromises()

    expect(catalogApi.getBestSellers).toHaveBeenCalledWith(4)
    expect(recommendationApi.getPersonalized).toHaveBeenCalledWith(4)
  })

  it('renders enterprise brand promises and service guarantees', () => {
    const wrapper = mount(HomeView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' },
          FeaturedCategoriesGrid: true,
          RecommendationCarousel: true,
          ProductCard: true
        }
      }
    })

    expect(wrapper.text()).toContain('100% Chính hãng')
    expect(wrapper.text()).toContain('Giao hỏa tốc 2H')
    expect(wrapper.text()).toContain('Bảo hành 12 tháng')
    expect(wrapper.text()).toContain('Đổi mới 30 ngày')
  })
})
