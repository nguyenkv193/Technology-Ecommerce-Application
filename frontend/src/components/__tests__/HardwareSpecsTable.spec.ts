import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import HardwareSpecsTable from '../HardwareSpecsTable.vue'

describe('HardwareSpecsTable.vue', () => {
  it('renders fallback message when attributes is empty or not provided', () => {
    const wrapper = mount(HardwareSpecsTable, {
      props: { attributes: [] }
    })

    expect(wrapper.text()).toContain('Thông số kỹ thuật đang được cập nhật.')
  })

  it('renders hardware attributes list when provided', () => {
    const attributes = [
      { id: 1, name: 'CPU', value: 'Apple M3 Max' },
      { id: 2, name: 'RAM', value: '36GB Unified' },
      { id: 3, name: 'Storage', value: '1TB SSD' }
    ]

    const wrapper = mount(HardwareSpecsTable, {
      props: { attributes }
    })

    expect(wrapper.text()).toContain('CPU')
    expect(wrapper.text()).toContain('Apple M3 Max')
    expect(wrapper.text()).toContain('RAM')
    expect(wrapper.text()).toContain('36GB Unified')
    expect(wrapper.text()).toContain('Storage')
    expect(wrapper.text()).toContain('1TB SSD')
  })
})
