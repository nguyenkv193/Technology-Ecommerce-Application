<template>
  <div class="group relative bg-white border border-zinc-200/80 hover:border-zinc-300 rounded-2xl p-4 transition-all duration-300 hover:shadow-lg hover:-translate-y-0.5 flex flex-col justify-between">
    <div>
      <!-- Product Image Canvas -->
      <div
        ref="imageFrame"
        class="product-image-frame relative overflow-hidden rounded-xl bg-[#f6f6f8] aspect-square flex items-center justify-center mb-3.5 select-none focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-zinc-900 focus-visible:ring-offset-2"
        tabindex="0"
        role="group"
        :aria-label="`Ảnh ${product.name}. Ô phóng to riêng: dùng phím mũi tên để xem các góc, Escape để đóng.`"
        @pointerenter="updateImageZoom"
        @pointermove="updateImageZoom"
        @pointerleave="resetImageZoom"
        @pointercancel="resetImageZoom"
        @focus="focusImageZoom"
        @blur="resetImageZoom"
        @keydown="handleImageZoomKey"
      >
        <img 
          :src="productThumbnail" 
          :alt="product.name"
          class="product-zoom-image block h-full w-full object-contain pointer-events-none select-none"
          :draggable="false"
          loading="lazy"
        />

        <div v-if="imageZoomed" aria-hidden="true" class="absolute left-0 top-0 pointer-events-none rounded border border-zinc-900/50 bg-white/10" :style="zoomLensStyle" />

        <!-- Wishlist Button -->
        <button 
          @click.stop.prevent="handleToggleWishlist"
          class="absolute top-2.5 right-2.5 p-1.5 rounded-full bg-white/90 hover:bg-white border border-zinc-200/80 transition text-zinc-400 hover:text-red-500 shadow-xs cursor-pointer"
          :class="{ 'text-red-500': isFav }"
          title="Thêm vào yêu thích"
        >
          <svg class="w-4 h-4" :fill="isFav ? 'currentColor' : 'none'" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
          </svg>
        </button>

        <!-- Brand Badge -->
        <div v-if="brandDisplayName" class="absolute bottom-2.5 left-2.5">
          <span class="text-[10px] font-semibold uppercase tracking-wider px-2 py-0.5 rounded-md bg-white/95 text-zinc-700 border border-zinc-200/80 shadow-xs">
            {{ brandDisplayName }}
          </span>
        </div>
      </div>

      <Teleport to="body">
        <div
          v-if="imageZoomed"
          data-testid="product-zoom-preview"
          aria-hidden="true"
          class="fixed z-[60] pointer-events-none overflow-hidden rounded-xl bg-[#f6f6f8] shadow-xl ring-1 ring-zinc-900/15"
          :style="zoomPreviewStyle"
        >
          <img :src="productThumbnail" alt="" :draggable="false" class="absolute left-0 top-0 max-w-none object-contain select-none" :style="zoomPreviewImageStyle" />
          <span class="absolute right-2 top-2 rounded-md bg-white/90 px-2 py-0.5 text-[10px] font-semibold text-zinc-700 shadow-xs">2×</span>
        </div>
      </Teleport>

      <!-- Product Meta -->
      <div class="space-y-1">
        <span class="text-[11px] text-zinc-400 font-medium block">{{ categoryDisplayName }}</span>
        <router-link :to="`/products/${product.id}`" class="block">
          <h3 class="font-medium text-zinc-900 group-hover:text-zinc-600 transition-colors line-clamp-2 text-xs sm:text-sm leading-snug">
            {{ product.name }}
          </h3>
        </router-link>
      </div>

      <!-- Variant / Configuration Chips -->
      <div v-if="product.variants && product.variants.length > 0" class="mt-2.5 flex flex-wrap gap-1.5 items-center">
        <button
          v-for="variant in product.variants"
          :key="variant.id"
          type="button"
          @click.stop.prevent="selectVariant(variant)"
          :title="variant.name"
          class="px-2 py-0.5 rounded-md text-[11px] font-medium border transition-all duration-200 cursor-pointer select-none"
          :class="activeVariant?.id === variant.id 
            ? 'bg-zinc-900 text-white border-zinc-900 shadow-xs ring-1 ring-zinc-900' 
            : 'bg-zinc-50 hover:bg-zinc-100 text-zinc-600 border-zinc-200/80 hover:border-zinc-300'"
        >
          {{ getVariantLabel(variant) }}
        </button>
      </div>
    </div>

    <!-- Pricing & Action -->
    <div class="mt-4 pt-3 border-t border-zinc-100 flex items-center justify-between gap-2">
      <div class="min-w-0 flex-1">
        <div class="text-[10px] text-zinc-400 font-medium truncate" :title="activeVariant ? activeVariant.name : 'Giá tham khảo'">
          {{ activeVariant ? activeVariant.name : 'Giá tham khảo' }}
        </div>
        <div class="flex items-baseline gap-1.5 flex-wrap">
          <span class="text-sm sm:text-base font-bold text-zinc-900">
            {{ formatPrice(displayPrice) }}
          </span>
          <span 
            v-if="displayOriginalPrice && displayOriginalPrice > displayPrice" 
            class="text-[11px] text-zinc-400 line-through"
          >
            {{ formatPrice(displayOriginalPrice) }}
          </span>
        </div>
      </div>

      <router-link 
        :to="`/products/${product.id}`"
        class="inline-flex items-center justify-center px-3 py-1.5 rounded-lg text-xs font-medium bg-zinc-900 hover:bg-zinc-800 text-white transition-all active:scale-95 shadow-xs shrink-0"
      >
        Chi tiết
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useWishlistStore } from '@/stores/wishlist'
import type { Product, ProductVariant } from '@/types'

const imageZoomed = ref(false)
const imageFrame = ref<HTMLElement | null>(null)
const imageZoomOrigin = ref({ x: 50, y: 50 })
const zoomFrameSize = ref({ width: 1, height: 1 })
const zoomPreview = ref({ left: 0, top: 0, size: 220 })
const zoomRegion = computed(() => {
  const { width, height } = zoomFrameSize.value
  const lensWidth = Math.min(width, zoomPreview.value.size / 2)
  const lensHeight = Math.min(height, zoomPreview.value.size / 2)
  return {
    width: lensWidth,
    height: lensHeight,
    left: Math.max(0, Math.min(width - lensWidth, imageZoomOrigin.value.x / 100 * width - lensWidth / 2)),
    top: Math.max(0, Math.min(height - lensHeight, imageZoomOrigin.value.y / 100 * height - lensHeight / 2))
  }
})
const zoomLensStyle = computed(() => ({
  width: `${zoomRegion.value.width}px`, height: `${zoomRegion.value.height}px`,
  transform: `translate(${zoomRegion.value.left}px, ${zoomRegion.value.top}px)`
}))
const zoomPreviewStyle = computed(() => ({
  left: `${zoomPreview.value.left}px`, top: `${zoomPreview.value.top}px`,
  width: `${zoomPreview.value.size}px`, height: `${zoomPreview.value.size}px`
}))
const zoomPreviewImageStyle = computed(() => ({
  width: `${zoomFrameSize.value.width * 2}px`, height: `${zoomFrameSize.value.height * 2}px`,
  transform: `translate(${-zoomRegion.value.left * 2}px, ${-zoomRegion.value.top * 2}px)`
}))

const clampZoomPosition = (value: number) => Math.max(0, Math.min(100, value))

const resetImageZoom = () => { imageZoomed.value = false }

const positionZoomPreview = () => {
  const frame = imageFrame.value?.getBoundingClientRect()
  if (!frame || frame.width <= 0 || frame.height <= 0) return null
  const margin = 8, gap = 12
  const size = Math.max(1, Math.min(220, window.innerWidth - margin * 2, window.innerHeight - margin * 2))
  let left = frame.right + gap
  let top = frame.top + (frame.height - size) / 2
  if (left + size > window.innerWidth - margin) {
    left = frame.left - size - gap
    if (left < margin) {
      left = frame.left + (frame.width - size) / 2
      top = frame.bottom + gap + size <= window.innerHeight - margin
        ? frame.bottom + gap : frame.top - size - gap
    }
  }
  zoomFrameSize.value = { width: frame.width, height: frame.height }
  zoomPreview.value = {
    size,
    left: Math.max(margin, Math.min(window.innerWidth - size - margin, left)),
    top: Math.max(margin, Math.min(window.innerHeight - size - margin, top))
  }
  return frame
}

// Only an open preview needs listeners; Vue cleans them up on unmount too.
watch(imageZoomed, (visible, _previous, onCleanup) => {
  if (!visible) return
  window.addEventListener('scroll', resetImageZoom, { capture: true, passive: true })
  window.addEventListener('resize', resetImageZoom)
  onCleanup(() => {
    window.removeEventListener('scroll', resetImageZoom, true)
    window.removeEventListener('resize', resetImageZoom)
  })
})

const updateImageZoom = (event: PointerEvent) => {
  if (event.pointerType !== 'mouse' || !window.matchMedia('(hover: hover) and (pointer: fine)').matches) return
  if ((event.target as HTMLElement).closest('button')) {
    resetImageZoom()
    return
  }
  const frame = positionZoomPreview()
  if (!frame) return
  imageZoomOrigin.value = {
    x: clampZoomPosition((event.clientX - frame.left) / frame.width * 100),
    y: clampZoomPosition((event.clientY - frame.top) / frame.height * 100)
  }
  imageZoomed.value = true
}

const focusImageZoom = (event: FocusEvent) => {
  if (!(event.currentTarget as HTMLElement).matches(':focus-visible')) return
  if (!positionZoomPreview()) return
  imageZoomOrigin.value = { x: 50, y: 50 }
  imageZoomed.value = true
}

const handleImageZoomKey = (event: KeyboardEvent) => {
  // Do not intercept keyboard events from the nested wishlist button.
  if (event.target !== event.currentTarget) return
  if (event.key === 'Escape') {
    event.preventDefault()
    resetImageZoom()
    return
  }
  const directions: Record<string, [number, number]> = {
    ArrowLeft: [-10, 0], ArrowRight: [10, 0], ArrowUp: [0, -10], ArrowDown: [0, 10]
  }
  const direction = directions[event.key]
  if (!direction) return
  event.preventDefault()
  if (!positionZoomPreview()) return
  imageZoomOrigin.value = {
    x: clampZoomPosition(imageZoomOrigin.value.x + direction[0]),
    y: clampZoomPosition(imageZoomOrigin.value.y + direction[1])
  }
  imageZoomed.value = true
}

const props = withDefaults(defineProps<{
  product: Product
  reason?: string
}>(), {
  reason: ''
})

const defaultImage = 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500&auto=format&fit=crop&q=60'

const brandDisplayName = computed(() => props.product.brandName || props.product.brand?.name || '')
const categoryDisplayName = computed(() => props.product.categoryName || props.product.category?.name || 'Thiết bị công nghệ')

const productThumbnail = computed(() => {
  if (props.product.thumbnailUrl) return props.product.thumbnailUrl
  if (props.product.images && props.product.images.length > 0) {
    const thumb = props.product.images.find(img => img.isThumbnail)
    return thumb?.url || props.product.images[0].url
  }
  return defaultImage
})

watch(productThumbnail, () => {
  resetImageZoom()
  imageZoomOrigin.value = { x: 50, y: 50 }
})

const wishlistStore = useWishlistStore()
const isFav = computed(() => wishlistStore.isWishlisted(props.product.id))

const selectedVariant = ref<ProductVariant | null>(null)

watch(() => props.product.id, () => {
  selectedVariant.value = null
  resetImageZoom()
})

const activeVariant = computed<ProductVariant | null>(() => {
  if (selectedVariant.value) return selectedVariant.value
  if (props.product.variants && props.product.variants.length > 0) {
    return props.product.variants[0]
  }
  return null
})

const displayPrice = computed(() => {
  if (activeVariant.value?.price) {
    return activeVariant.value.price
  }
  return props.product.minPrice ?? 0
})

const displayOriginalPrice = computed(() => {
  return activeVariant.value?.originalPrice ?? null
})

const selectVariant = (variant: ProductVariant) => {
  selectedVariant.value = variant
}

const getVariantLabel = (variant: ProductVariant): string => {
  if (!variant || !variant.name) return ''
  const name = variant.name.trim()

  // RTX and SSD: e.g. "Core Ultra 9 / RTX 4070 / 32GB / 1TB SSD" -> "RTX 4070 • 1TB"
  const rtxMatch = name.match(/RTX\s*\d{4}/i)
  const ssdMatch = name.match(/(\d+(?:GB|TB))\s*SSD/i)
  if (rtxMatch && ssdMatch) {
    return `${rtxMatch[0]} • ${ssdMatch[1]}`
  } else if (rtxMatch) {
    return rtxMatch[0]
  }

  // RAM and SSD combo: e.g. "16GB RAM / 512GB SSD" or "Core Ultra 7 / 16GB RAM / 512GB SSD"
  const ramMatch = name.match(/(\d+\s*GB)\s*RAM/i)
  if (ramMatch && ssdMatch) {
    return `${ramMatch[1]} / ${ssdMatch[1]}`
  }

  // Storage with Wi-Fi / 5G / Cellular: e.g. "256GB Wi-Fi Space Black" -> "256GB Wi-Fi"
  const storageWifiMatch = name.match(/^(\d+\s*(?:GB|TB))\s+(Wi-Fi|5G)/i)
  if (storageWifiMatch) {
    return `${storageWifiMatch[1]} ${storageWifiMatch[2]}`
  }

  // Leading storage: e.g. "256GB Titan Tự Nhiên", "512GB Xanh Navy" -> "256GB"
  const storageMatch = name.match(/^(\d+\s*(?:GB|TB))/i)
  if (storageMatch) {
    return storageMatch[1]
  }

  // Color e.g. "Màu Đen Midnight Black" -> "Đen"
  const colorMatch = name.match(/^Màu\s+([\p{L}]+)/iu)
  if (colorMatch) {
    return colorMatch[1]
  }

  // Smartwatch size e.g. "49mm Titan Dây Trail Loop" -> "49mm Titan"
  const watchMatch = name.match(/^(\d+mm\s+[^\s]+)/i)
  if (watchMatch) {
    return watchMatch[1]
  }

  // Keyboard switch e.g. "Bàn phím cơ full-size Switch Tactile Quiet" -> "Tactile Quiet"
  const switchMatch = name.match(/Switch\s+(.+)$/i)
  if (switchMatch) {
    return switchMatch[1]
  }

  if (name.length > 18) {
    return name.slice(0, 16) + '...'
  }
  return name
}

const handleToggleWishlist = async () => {
  await wishlistStore.toggleWishlist(props.product)
}

const formatPrice = (value?: number | null): string => {
  if (!value || isNaN(value)) return 'Liên hệ'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}
</script>

<style scoped>
@media (hover: hover) and (pointer: fine) {
  .product-image-frame {
    cursor: zoom-in;
  }
}

</style>
