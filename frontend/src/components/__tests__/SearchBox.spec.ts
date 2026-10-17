import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import SearchBox from '../SearchBox.vue'
import { catalogApi } from '@/api/catalogApi'
import { behaviorApi } from '@/api/behaviorApi'

// Mock vue-router
const pushMock = vi.fn()
vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: pushMock
  })
}))

// Mock catalogApi and behaviorApi
vi.mock('@/api/catalogApi', () => ({
  catalogApi: {
    getSearchSuggestions: vi.fn()
  }
}))

vi.mock('@/api/behaviorApi', () => ({
  behaviorApi: {
    track: vi.fn()
  }
}))

describe('SearchBox.vue Component Test Suite', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  // 1. Initial State & Render
  it('TC-SEARCH-01: renders search input with correct placeholder and closed dropdown', () => {
    const wrapper = mount(SearchBox)
    const input = wrapper.find('input[type="text"]')

    expect(input.exists()).toBe(true)
    expect(input.attributes('placeholder')).toBe('Nhập tên điện thoại, laptop, phụ kiện... cần tìm')
    expect(wrapper.find('#search-dropdown-menu').exists()).toBe(false)
  })

  // 2. Open Dropdown on Focus
  it('TC-SEARCH-02: opens dropdown when input is focused, displaying banner, history, trending, and suggestions', async () => {
    const wrapper = mount(SearchBox)
    const input = wrapper.find('input[type="text"]')
    await input.trigger('focus')

    const dropdown = wrapper.find('#search-dropdown-menu')
    expect(dropdown.exists()).toBe(true)

    // Banner
    expect(dropdown.text()).toContain('TÍCH ĐIỂM ĐỔI ƯU ĐÃI')
    expect(dropdown.text()).toContain('Tìm hiểu ngay')

    // History items (default initialized)
    expect(dropdown.text()).toContain('iphone 18')
    expect(dropdown.text()).toContain('samsung')
    expect(dropdown.text()).toContain('Xem thêm')

    // Trending Section
    expect(dropdown.text()).toContain('Xu hướng tìm kiếm')
    expect(dropdown.text()).toContain('iPhone 18')
    expect(dropdown.text()).toContain('Laptop')

    // Suggestions Section
    expect(dropdown.text()).toContain('Gợi ý cho bạn')
    expect(dropdown.text()).toContain('Gợi ý khác')
  })

  // 3. Expand / Collapse Search History
  it('TC-SEARCH-03: expands history when clicking "Xem thêm" and collapses when clicking "Thu gọn"', async () => {
    const wrapper = mount(SearchBox)
    await wrapper.find('input[type="text"]').trigger('focus')

    // Default shows 2 items
    const expandBtn = wrapper.findAll('button').find((b) => b.text().includes('Xem thêm'))
    expect(expandBtn?.exists()).toBe(true)

    // Click "Xem thêm"
    await expandBtn?.trigger('click')
    expect(wrapper.text()).toContain('Thu gọn')
    expect(wrapper.text()).toContain('Macbook Pro')

    // Click "Thu gọn"
    const collapseBtn = wrapper.findAll('button').find((b) => b.text().includes('Thu gọn'))
    await collapseBtn?.trigger('click')
    expect(wrapper.text()).toContain('Xem thêm')
  })

  // 4. Remove Item from Search History
  it('TC-SEARCH-04: removes a specific item from search history when clicking "X" icon', async () => {
    const wrapper = mount(SearchBox)
    await wrapper.find('input[type="text"]').trigger('focus')

    const removeBtn = wrapper.find('button[title="Xóa khỏi lịch sử"]')
    expect(removeBtn.exists()).toBe(true)

    await removeBtn.trigger('click')
    const raw = localStorage.getItem('techstore_search_history')
    expect(raw).not.toContain('iphone 18')
  })

  // 5. Select Trending Keyword
  it('TC-SEARCH-05: selects trending keyword, saves to history, and navigates to catalog', async () => {
    const wrapper = mount(SearchBox)
    await wrapper.find('input[type="text"]').trigger('focus')

    const laptopTag = wrapper.findAll('button').find((b) => b.text().includes('Laptop'))
    expect(laptopTag?.exists()).toBe(true)

    await laptopTag?.trigger('click')

    expect(behaviorApi.track).toHaveBeenCalledWith('SEARCH', null, 'Laptop')
    expect(pushMock).toHaveBeenCalledWith({ path: '/products', query: { keyword: 'Laptop' } })
    expect(wrapper.find('#search-dropdown-menu').exists()).toBe(false)
  })

  // 6. Rotate Suggestions ("Gợi ý khác")
  it('TC-SEARCH-06: rotates to another set of suggestions when clicking "Gợi ý khác"', async () => {
    const wrapper = mount(SearchBox)
    await wrapper.find('input[type="text"]').trigger('focus')

    expect(wrapper.text()).toContain('Google hé lộ OnePlus')

    const rotateBtn = wrapper.find('button[title="Đổi gợi ý khác"]')
    expect(rotateBtn.exists()).toBe(true)

    await rotateBtn.trigger('click')
    expect(wrapper.text()).toContain('MacBook Pro M4 Max')
  })

  // 7. TechRewards Loyalty Modal
  it('TC-SEARCH-07: opens loyalty modal when clicking "TÍCH ĐIỂM ĐỔI ƯU ĐÃI" banner', async () => {
    const wrapper = mount(SearchBox)
    await wrapper.find('input[type="text"]').trigger('focus')

    const banner = wrapper.find('.bg-gradient-to-r')
    expect(banner.exists()).toBe(true)

    await banner.trigger('click')
    expect(document.body.innerHTML).toContain('TechRewards')
    expect(document.body.innerHTML).toContain('Tích lũy 1% - 3% mỗi đơn hàng')
  })

  // 8. Live Autocomplete when user types text
  it('TC-SEARCH-08: switches to autocomplete mode and fetches product suggestions when typing', async () => {
    const mockSuggestions = [
      {
        id: 101,
        name: 'iPhone 16 Pro Max',
        slug: 'iphone-16-pro-max',
        brandName: 'Apple',
        categoryName: 'Điện thoại',
        minPrice: 34990000,
        thumbnailUrl: '/test-iphone.png'
      }
    ]
    vi.mocked(catalogApi.getSearchSuggestions).mockResolvedValue({ data: mockSuggestions } as any)

    const wrapper = mount(SearchBox)
    const input = wrapper.find('input[type="text"]')
    await input.setValue('iphone')
    await input.trigger('focus')

    // Wait for debounce 250ms
    await new Promise((resolve) => setTimeout(resolve, 300))
    await flushPromises()

    expect(catalogApi.getSearchSuggestions).toHaveBeenCalledWith('iphone', 6)
    expect(wrapper.text()).toContain('Sản phẩm gợi ý')
    expect(wrapper.text()).toContain('iPhone 16 Pro Max')
    expect(wrapper.text()).toContain('34.990.000')
  })

  // 9. Select Product Suggestion
  it('TC-SEARCH-09: navigates to product detail page when clicking a product suggestion', async () => {
    const mockSuggestions = [
      {
        id: 202,
        name: 'Samsung Galaxy S24 Ultra',
        slug: 'samsung-galaxy-s24-ultra',
        minPrice: 29990000
      }
    ]
    ;(catalogApi.getSearchSuggestions as any).mockResolvedValueOnce({ data: mockSuggestions })

    const wrapper = mount(SearchBox)
    const input = wrapper.find('input[type="text"]')
    await input.setValue('samsung')
    await input.trigger('focus')

    await new Promise((resolve) => setTimeout(resolve, 300))
    await flushPromises()

    const productBtn = wrapper.findAll('button').find((b) => b.text().includes('Samsung Galaxy S24 Ultra'))
    expect(productBtn?.exists()).toBe(true)

    await productBtn?.trigger('click')
    expect(pushMock).toHaveBeenCalledWith('/products/202')
  })

  // 10. Clear Search Input Button
  it('TC-SEARCH-10: clears search keyword when clicking the clear "X" button', async () => {
    const wrapper = mount(SearchBox)
    const input = wrapper.find('input[type="text"]')
    await input.setValue('macbook')

    const clearBtn = wrapper.find('button[title="Xóa tìm kiếm"]')
    expect(clearBtn.exists()).toBe(true)

    await clearBtn.trigger('click')
    expect((wrapper.find('input[type="text"]').element as HTMLInputElement).value).toBe('')
  })

  // 11. Keyboard Navigation (Escape to close)
  it('TC-SEARCH-11: closes dropdown when pressing Escape key', async () => {
    const wrapper = mount(SearchBox)
    const input = wrapper.find('input[type="text"]')
    await input.trigger('focus')
    expect(wrapper.find('#search-dropdown-menu').exists()).toBe(true)

    await input.trigger('keydown', { key: 'Escape' })
    expect(wrapper.find('#search-dropdown-menu').exists()).toBe(false)
  })

  // 12. Submit Search via Form / Enter
  it('TC-SEARCH-12: submits search and saves keyword to history when submitting form', async () => {
    const wrapper = mount(SearchBox)
    const input = wrapper.find('input[type="text"]')
    await input.setValue('bàn phím cơ')

    await wrapper.find('form').trigger('submit')

    expect(behaviorApi.track).toHaveBeenCalledWith('SEARCH', null, 'bàn phím cơ')
    expect(pushMock).toHaveBeenCalledWith({ path: '/products', query: { keyword: 'bàn phím cơ' } })
    const history = JSON.parse(localStorage.getItem('techstore_search_history') || '[]')
    expect(history[0]).toBe('bàn phím cơ')
  })

  // 13. Keyboard ArrowDown and Enter to select product suggestion
  it('TC-SEARCH-13: navigates suggestions with ArrowDown / ArrowUp and selects with Enter', async () => {
    const mockSuggestions = [
      { id: 1, name: 'iPhone 15', minPrice: 19000000 },
      { id: 2, name: 'iPhone 16', minPrice: 22000000 }
    ]
    vi.mocked(catalogApi.getSearchSuggestions).mockResolvedValue({ data: mockSuggestions } as any)

    const wrapper = mount(SearchBox)
    const input = wrapper.find('input[type="text"]')
    await input.setValue('iPhone')
    await input.trigger('focus')

    await new Promise((resolve) => setTimeout(resolve, 300))
    await flushPromises()

    // Press ArrowDown to highlight first product
    await input.trigger('keydown', { key: 'ArrowDown' })
    let buttons = wrapper.findAll('[data-testid="product-suggestion-item"]')
    expect(buttons[0].classes()).toContain('bg-zinc-100')

    // Press ArrowDown again to highlight second product
    await input.trigger('keydown', { key: 'ArrowDown' })
    buttons = wrapper.findAll('[data-testid="product-suggestion-item"]')
    expect(buttons[1].classes()).toContain('bg-zinc-100')

    // Press Enter to select
    await input.trigger('keydown', { key: 'Enter' })
    expect(pushMock).toHaveBeenCalledWith('/products/2')
  })

  // 14. History Deduplication and Limit
  it('TC-SEARCH-14: avoids duplicate entries in history and places newest search at the top', async () => {
    const wrapper = mount(SearchBox)
    const input = wrapper.find('input[type="text"]')

    await input.setValue('iphone 18')
    await wrapper.find('form').trigger('submit')

    const history = JSON.parse(localStorage.getItem('techstore_search_history') || '[]')
    expect(history[0]).toBe('iphone 18')
    // No duplicate 'iphone 18' in history
    const duplicates = history.filter((h: string) => h.toLowerCase() === 'iphone 18')
    expect(duplicates.length).toBe(1)
  })

  // 15. Image Fallback on Error
  it('TC-SEARCH-15: gracefully applies fallback image when thumbnail fails to load', async () => {
    const wrapper = mount(SearchBox)
    await wrapper.find('input[type="text"]').trigger('focus')

    const img = wrapper.find('img')
    expect(img.exists()).toBe(true)

    // Trigger error event
    await img.trigger('error')
    expect((img.element as HTMLImageElement).src).toContain('/categories/phone.png')
  })
})
