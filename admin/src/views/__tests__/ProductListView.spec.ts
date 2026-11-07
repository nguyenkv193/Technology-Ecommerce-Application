import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { setActivePinia, createPinia } from 'pinia'
import ProductListView from '../products/ProductListView.vue'
import * as productsApi from '@/api/products'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/products', component: { template: '<div>Products</div>' } },
    { path: '/products/create', component: { template: '<div>Create Product</div>' } },
    { path: '/products/:id/edit', component: { template: '<div>Edit Product</div>' } }
  ]
})

vi.mock('@/api/products', () => ({
  fetchProducts: vi.fn(),
  deleteProduct: vi.fn()
}))

describe('ProductListView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('renders products table with items and handles pagination', async () => {
    vi.mocked(productsApi.fetchProducts).mockResolvedValueOnce({
      content: [
        {
          id: 1,
          name: 'MacBook Pro M3 Max 16 inch',
          slug: 'macbook-pro-m3-max-16-inch',
          categoryId: 1,
          categoryName: 'Laptop',
          brandId: 1,
          brandName: 'Apple',
          originalPrice: 89990000,
          salePrice: 84990000,
          stockQuantity: 15,
          primaryImageUrl: 'https://example.com/macbook.png'
        }
      ],
      page: 0,
      size: 10,
      totalElements: 1,
      totalPages: 1,
      last: true
    })

    const wrapper = mount(ProductListView, {
      global: {
        plugins: [router]
      }
    })

    await new Promise((r) => setTimeout(r, 50))

    expect(wrapper.text()).toContain('MacBook Pro M3 Max 16 inch')
    expect(wrapper.text()).toContain('Apple')
    expect(wrapper.text()).toContain('Laptop')
    expect(wrapper.text()).toContain('Thêm sản phẩm')
  })
})
