import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import FeaturedCategoriesGrid from '../FeaturedCategoriesGrid.vue'

describe('FeaturedCategoriesGrid.vue', () => {
  it('renders section title and link to all products', () => {
    const wrapper = mount(FeaturedCategoriesGrid, {
      global: {
        stubs: {
          routerLink: {
            props: ['to'],
            template: '<a :href="to"><slot /></a>'
          }
        }
      }
    })

    expect(wrapper.text()).toContain('Danh mục nổi bật')
    expect(wrapper.text()).toContain('Tất cả thiết bị')
  })

  it('renders all featured category items with images and links', () => {
    const wrapper = mount(FeaturedCategoriesGrid, {
      global: {
        stubs: {
          routerLink: {
            props: ['to'],
            template: '<a :href="to"><slot /></a>'
          }
        }
      }
    })

    const links = wrapper.findAll('a')
    // 1 header link + 16 category links = 17
    expect(links.length).toBe(17)

    expect(wrapper.text()).toContain('Điện thoại')
    expect(wrapper.text()).toContain('Laptop')
    expect(wrapper.text()).toContain('Máy tính bảng')
    expect(wrapper.text()).toContain('Đồng hồ thông minh')
  })
})
