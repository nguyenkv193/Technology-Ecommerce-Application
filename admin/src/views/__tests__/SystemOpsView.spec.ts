import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { setActivePinia, createPinia } from 'pinia'
import SystemOpsView from '../system/SystemOpsView.vue'
import * as systemApi from '@/api/system'

vi.mock('@/api/system', () => ({
  reindexProducts: vi.fn()
}))

describe('SystemOpsView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('renders system operations view and triggers reindex', async () => {
    vi.mocked(systemApi.reindexProducts).mockResolvedValueOnce({
      success: true,
      message: 'Đã tạo lại chỉ mục tìm kiếm sản phẩm',
      data: 120
    })

    const wrapper = mount(SystemOpsView)
    expect(wrapper.text()).toContain('Vận hành Hệ thống')
    expect(wrapper.text()).toContain('Elasticsearch')
    expect(wrapper.text()).toContain('Đồng bộ lại chỉ mục')

    // Find and click the reindex button
    const button = wrapper.find('button[data-test="reindex-btn"]')
    expect(button.exists()).toBe(true)
    await button.trigger('click')

    expect(systemApi.reindexProducts).toHaveBeenCalled()
  })
})
