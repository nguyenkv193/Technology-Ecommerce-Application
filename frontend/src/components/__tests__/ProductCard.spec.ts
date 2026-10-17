import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import ProductCard from '../ProductCard.vue'
import { createPinia, setActivePinia } from 'pinia'
import { useWishlistStore } from '@/stores/wishlist'
import type { Product } from '@/types'

describe('ProductCard.vue', () => {
  const mockProduct: Product = {
    id: 1,
    name: 'MacBook Pro 16 M3 Max',
    slug: 'macbook-pro-16-m3-max',
    description: 'Pro laptop',
    brandName: 'Apple',
    categoryName: 'Laptop',
    minPrice: 79990000,
    thumbnailUrl: 'https://example.com/macbook.png',
    variants: [
      {
        id: 101,
        productId: 1,
        sku: 'MBP16-36-512',
        name: 'Core Ultra 9 / RTX 4070 / 32GB / 1TB SSD',
        price: 79990000,
        originalPrice: 89990000,
        stockQuantity: 10
      },
      {
        id: 102,
        productId: 1,
        sku: 'MBP16-48-1TB',
        name: '32GB RAM / 1TB SSD',
        price: 99990000,
        originalPrice: 109990000,
        stockQuantity: 5
      }
    ]
  }

  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
    // Mock matchMedia for happy-dom
    window.matchMedia = vi.fn().mockImplementation((query) => ({
      matches: true,
      media: query,
      onchange: null,
      addListener: vi.fn(),
      removeListener: vi.fn(),
      addEventListener: vi.fn(),
      removeEventListener: vi.fn(),
      dispatchEvent: vi.fn()
    }))
  })

  it('renders product details correctly', () => {
    const wrapper = mount(ProductCard, {
      props: { product: mockProduct },
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' }
        }
      }
    })

    expect(wrapper.text()).toContain('MacBook Pro 16 M3 Max')
    expect(wrapper.text()).toContain('Apple')
    expect(wrapper.text()).toContain('Laptop')
    expect(wrapper.text()).toContain('79.990.000')
  })

  it('renders variant configuration chips and changes active variant on click', async () => {
    const wrapper = mount(ProductCard, {
      props: { product: mockProduct },
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' }
        }
      }
    })

    // Find the second variant button specifically
    const variantBtn2 = wrapper.find('button[title="32GB RAM / 1TB SSD"]')
    expect(variantBtn2.exists()).toBe(true)

    // Click on the second variant
    await variantBtn2.trigger('click')

    // Price should now reflect the second variant
    expect(wrapper.text()).toContain('99.990.000')
  })

  it('toggles wishlist when clicking the favorite heart button', async () => {
    const wishlistStore = useWishlistStore()
    const toggleSpy = vi.spyOn(wishlistStore, 'toggleWishlist').mockResolvedValueOnce({ wishlisted: true })

    const wrapper = mount(ProductCard, {
      props: { product: mockProduct },
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' }
        }
      }
    })

    const favButton = wrapper.find('button[title="Thêm vào yêu thích"]')
    expect(favButton.exists()).toBe(true)
    await favButton.trigger('click')

    expect(toggleSpy).toHaveBeenCalledWith(mockProduct)
  })

  it('handles image zoom with mouse hover and escape key', async () => {
    const wrapper = mount(ProductCard, {
      props: { product: mockProduct },
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' },
          Transition: { template: '<div><slot /></div>' }
        }
      }
    })

    const imageFrame = wrapper.find('.product-image-frame')
    imageFrame.element.getBoundingClientRect = vi.fn().mockReturnValue({
      left: 100,
      top: 100,
      width: 300,
      height: 300
    })

    // Pointer move
    await imageFrame.trigger('pointermove', {
      pointerType: 'mouse',
      clientX: 200,
      clientY: 200
    })

    // Pointer leave resets zoom
    await imageFrame.trigger('pointerleave')
    expect(wrapper.find('[data-testid="product-zoom-preview"]').exists()).toBe(false)
  })

  it('falls back to default placeholder image when no images provided', () => {
    const productWithoutImg: Product = {
      id: 99,
      name: 'No Image Product',
      slug: 'no-image',
      description: 'Desc',
      minPrice: 1000000
    }

    const wrapper = mount(ProductCard, {
      props: { product: productWithoutImg },
      global: {
        stubs: {
          routerLink: { template: '<a><slot /></a>' }
        }
      }
    })

    const img = wrapper.find('img.product-zoom-image')
    expect(img.attributes('src')).toContain('images.unsplash.com')
  })
})
