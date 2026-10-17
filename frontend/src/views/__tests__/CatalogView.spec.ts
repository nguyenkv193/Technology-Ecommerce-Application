import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import CatalogView from '../CatalogView.vue'
import { catalogApi } from '@/api/catalogApi'

let mockRouteQuery: Record<string, any> = {}

vi.mock('vue-router', () => ({
  useRoute: () => ({
    query: mockRouteQuery
  }),
  useRouter: () => ({
    push: vi.fn()
  })
}))

vi.mock('@/api/catalogApi', () => ({
  catalogApi: {
    getCategories: vi.fn(),
    getBrands: vi.fn(),
    getProducts: vi.fn()
  }
}))

describe('CatalogView.vue', () => {
  beforeEach(() => {
    mockRouteQuery = {}
    vi.clearAllMocks()
    vi.mocked(catalogApi.getCategories).mockResolvedValue({
      success: true,
      message: 'OK',
      data: [
        { id: 1, name: 'Laptop', slug: 'laptop' },
        { id: 2, name: 'Điện thoại', slug: 'phone' }
      ]
    })
    vi.mocked(catalogApi.getBrands).mockResolvedValue({
      success: true,
      message: 'OK',
      data: [
        { id: 1, name: 'Apple', slug: 'apple' },
        { id: 2, name: 'Samsung', slug: 'samsung' }
      ]
    })
  })

  it('loads categories, brands and products on mount', async () => {
    vi.mocked(catalogApi.getProducts).mockResolvedValueOnce({
      success: true,
      message: 'OK',
      data: {
        content: [
          { id: 1, name: 'MacBook Air M2', minPrice: 24000000, slug: 'mba-m2', description: 'Desc' }
        ],
        totalPages: 1,
        totalElements: 1
      } as any
    })

    const wrapper = mount(CatalogView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' },
          ProductCard: {
            props: ['product'],
            template: '<div class="product-stub">{{ product.name }}</div>'
          }
        }
      }
    })
    await flushPromises()

    expect(catalogApi.getCategories).toHaveBeenCalled()
    expect(catalogApi.getBrands).toHaveBeenCalled()
    expect(catalogApi.getProducts).toHaveBeenCalled()
    expect(wrapper.text()).toContain('Danh Mục Sản Phẩm Công Nghệ')
    expect(wrapper.text()).toContain('MacBook Air M2')
  })

  it('renders empty products state when no results found', async () => {
    vi.mocked(catalogApi.getProducts).mockResolvedValueOnce({
      success: true,
      message: 'OK',
      data: {
        content: [],
        totalPages: 0,
        totalElements: 0
      } as any
    })

    const wrapper = mount(CatalogView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' },
          ProductCard: true
        }
      }
    })
    await flushPromises()

    expect(wrapper.text()).toContain('Không tìm thấy sản phẩm phù hợp')
  })

  it('resets filters when clicking reset button', async () => {
    vi.mocked(catalogApi.getProducts).mockResolvedValue({
      success: true,
      message: 'OK',
      data: { content: [], totalPages: 1 } as any
    })

    const wrapper = mount(CatalogView, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' },
          ProductCard: true
        }
      }
    })
    await flushPromises()

    const keywordInput = wrapper.find('input[placeholder="Ví dụ: M3, OLED, 16GB..."]')
    await keywordInput.setValue('iPhone')

    const resetBtn = wrapper.findAll('button').find(b => b.text() === 'Đặt lại')
    expect(resetBtn).toBeDefined()
    await resetBtn!.trigger('click')
    await flushPromises()

    expect(catalogApi.getProducts).toHaveBeenLastCalledWith(
      expect.objectContaining({
        page: 0,
        size: 9
      })
    )
  })
})
