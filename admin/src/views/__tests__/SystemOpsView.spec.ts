import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { setActivePinia, createPinia } from 'pinia'
import SystemOpsView from '../system/SystemOpsView.vue'
import * as systemApi from '@/api/system'

vi.mock('@/api/system', () => ({
  checkSystemHealth: vi.fn()
}))

describe('SystemOpsView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('renders system operations view and triggers health check', async () => {
    vi.mocked(systemApi.checkSystemHealth).mockResolvedValueOnce({
      success: true,
      message: 'Hệ thống TechStore đang hoạt động ổn định',
      data: {
        status: 'UP',
        service: 'TechStore E-Commerce & AI Backend',
        version: '1.0.0',
        environment: 'development',
        currentTime: '2026-11-07T08:00:00Z'
      }
    })

    const wrapper = mount(SystemOpsView)
    expect(wrapper.text()).toContain('Vận hành Hệ thống')
    expect(wrapper.text()).toContain('PostgreSQL 17')
    expect(wrapper.text()).toContain('Kiểm tra sức khỏe hệ thống')

    // Find and click the health check button
    const button = wrapper.find('button[data-test="health-check-btn"]')
    expect(button.exists()).toBe(true)
    await button.trigger('click')

    expect(systemApi.checkSystemHealth).toHaveBeenCalled()
  })
})
