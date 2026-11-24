import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import CustomSelect from '../common/CustomSelect.vue'

describe('CustomSelect', () => {
  const options = [
    { value: 'ACTIVE', label: 'Hoạt động' },
    { value: 'BLOCKED', label: 'Đã khóa' }
  ]

  it('renders selected label and emits update on option click', async () => {
    const wrapper = mount(CustomSelect, {
      props: {
        modelValue: 'ACTIVE',
        options,
        triggerTestId: 'select-trigger',
        optionTestIdPrefix: 'select-opt'
      }
    })

    expect(wrapper.find('[data-testid="select-trigger"]').text()).toContain('Hoạt động')

    await wrapper.find('[data-testid="select-trigger"]').trigger('click')
    await wrapper.find('[data-testid="select-opt-BLOCKED"]').trigger('click')

    expect(wrapper.emitted('update:modelValue')?.[0]).toEqual(['BLOCKED'])
    expect(wrapper.emitted('change')?.[0]).toEqual(['BLOCKED'])
  })

  it('does not open when disabled', async () => {
    const wrapper = mount(CustomSelect, {
      props: {
        modelValue: 'ACTIVE',
        options,
        disabled: true,
        triggerTestId: 'select-trigger',
        optionTestIdPrefix: 'select-opt'
      }
    })

    await wrapper.find('[data-testid="select-trigger"]').trigger('click')
    expect(wrapper.find('[data-testid="select-opt-BLOCKED"]').exists()).toBe(false)
  })
})
