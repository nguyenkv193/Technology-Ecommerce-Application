import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import CategoryMegaMenu from '../CategoryMegaMenu.vue'

describe('CategoryMegaMenu.vue', () => {
  beforeEach(() => {
    document.body.innerHTML = ''
  })

  it('renders menu trigger button with "Danh mục"', () => {
    const wrapper = mount(CategoryMegaMenu, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' },
          teleport: true
        }
      }
    })

    const triggerBtn = wrapper.find('button[aria-label="Mở danh mục thiết bị"]')
    expect(triggerBtn.exists()).toBe(true)
    expect(triggerBtn.text()).toContain('Danh mục')
  })

  it('toggles menu open and close on button click', async () => {
    const wrapper = mount(CategoryMegaMenu, {
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' },
          teleport: true
        }
      }
    })

    const triggerBtn = wrapper.find('button[aria-label="Mở danh mục thiết bị"]')
    
    // Initially closed
    expect(wrapper.text()).not.toContain('Danh mục sản phẩm')

    // Click to open
    await triggerBtn.trigger('click')
    await wrapper.vm.$nextTick()
    expect(wrapper.text()).toContain('Danh mục sản phẩm')
    expect(wrapper.text()).toContain('Chuyên trang thương hiệu')

    // Click again to close
    await triggerBtn.trigger('click')
    await wrapper.vm.$nextTick()
    expect(wrapper.text()).not.toContain('Danh mục sản phẩm')
  })

  it('closes menu when Escape key is pressed', async () => {
    const wrapper = mount(CategoryMegaMenu, {
      attachTo: document.body,
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' },
          teleport: true
        }
      }
    })

    const triggerBtn = wrapper.find('button[aria-label="Mở danh mục thiết bị"]')
    await triggerBtn.trigger('click')
    await wrapper.vm.$nextTick()
    expect(wrapper.text()).toContain('Danh mục sản phẩm')

    // Press Escape
    const event = new KeyboardEvent('keydown', { key: 'Escape' })
    document.dispatchEvent(event)
    await wrapper.vm.$nextTick()

    expect(wrapper.text()).not.toContain('Danh mục sản phẩm')
    wrapper.unmount()
  })
})
