import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { setActivePinia, createPinia } from 'pinia'
import AdminSidebar from '../layout/AdminSidebar.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'dashboard', component: { template: '<div>Dashboard</div>' } },
    { path: '/products', name: 'products', component: { template: '<div>Products</div>' } },
    { path: '/categories', name: 'categories', component: { template: '<div>Categories</div>' } },
    { path: '/brands', name: 'brands', component: { template: '<div>Brands</div>' } },
    { path: '/orders', name: 'orders', component: { template: '<div>Orders</div>' } },
    { path: '/system-ops', name: 'system-ops', component: { template: '<div>Ops</div>' } }
  ]
})

describe('AdminSidebar', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('renders all core navigation items', () => {
    const wrapper = mount(AdminSidebar, {
      global: {
        plugins: [router]
      }
    })

    const text = wrapper.text()
    expect(text).toContain('Dashboard')
    expect(text).toContain('Sản phẩm')
    expect(text).toContain('Danh mục')
    expect(text).toContain('Thương hiệu')
    expect(text).toContain('Đơn hàng')
    expect(text).toContain('Vận hành')
  })
})
