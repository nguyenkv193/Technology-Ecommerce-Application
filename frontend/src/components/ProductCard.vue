<template>
  <div class="group relative bg-white border border-zinc-200/80 hover:border-zinc-300 rounded-2xl p-4 transition-all duration-300 hover:shadow-lg hover:-translate-y-0.5 flex flex-col justify-between">
    <div>
      <!-- Product Image Canvas -->
      <div class="relative overflow-hidden rounded-xl bg-[#f6f6f8] aspect-square flex items-center justify-center mb-3.5">
        <img 
          :src="productThumbnail" 
          :alt="product.name"
          class="block h-full w-full object-contain group-hover:scale-105 transition-transform duration-300"
          loading="lazy"
        />

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

const wishlistStore = useWishlistStore()
const isFav = computed(() => wishlistStore.isWishlisted(props.product.id))

const selectedVariant = ref<ProductVariant | null>(null)

watch(() => props.product.id, () => {
  selectedVariant.value = null
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
