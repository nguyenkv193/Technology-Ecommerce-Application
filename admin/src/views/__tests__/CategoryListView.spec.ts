import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { setActivePinia, createPinia } from 'pinia'
import CategoryListView from '../categories/CategoryListView.vue'
import * as categoriesApi from '@/api/categories'

vi.mock('@/api/categories', () => ({
  fetchCategories: vi.fn(),
  createCategory: vi.fn(),
  updateCategory: vi.fn(),
  deleteCategory: vi.fn()
}))

describe('CategoryListView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('renders category list and opens add modal', async () => {
    vi.mocked(categoriesApi.fetchCategories).mockResolvedValueOnce([
      {
        id: 1,
        name: 'Laptop & MacBook',
        slug: 'laptop-macbook',
        description: 'Máy tính xách tay văn phòng và gaming',
        productCount: 42
      }
    ])

    const wrapper = mount(CategoryListView)
    await new Promise((r) => setTimeout(r, 50))

    expect(wrapper.text()).toContain('Laptop & MacBook')
    expect(wrapper.text()).toContain('laptop-macbook')
    expect(wrapper.text()).toContain('Thêm danh mục')
  })
})
