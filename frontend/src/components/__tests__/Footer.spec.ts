import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import Footer from '../Footer.vue'

describe('Footer.vue', () => {
  it('renders footer brand, links, and contact information', () => {
    const wrapper = mount(Footer, {
      global: {
        stubs: {
          routerLink: {
            props: ['to'],
            template: '<a :href="to"><slot /></a>'
          }
        }
      }
    })

    expect(wrapper.text()).toContain('TechStore')
    expect(wrapper.text()).toContain('Điện thoại thông minh')
    expect(wrapper.text()).toContain('Laptop & Máy trạm')
    expect(wrapper.text()).toContain('1900 8888')
    expect(wrapper.text()).toContain('support@techstore.vn')
    expect(wrapper.text()).toContain('Graduation Thesis')
  })
})
