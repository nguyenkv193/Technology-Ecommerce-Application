import { defineStore } from 'pinia'
import { ref } from 'vue'
import { wishlistApi } from '@/api/wishlistApi'
import { behaviorApi } from '@/api/behaviorApi'
import { useAuthStore } from './auth'

export const useWishlistStore = defineStore('wishlist', () => {
  const items = ref<any[]>([])
  const wishlistedIds = ref<Set<number>>(new Set())
  const loading = ref(false)

  const fetchWishlist = async () => {
    const authStore = useAuthStore()
    if (!authStore.isAuthenticated) {
      items.value = []
      wishlistedIds.value.clear()
      return
    }

    loading.value = true
    try {
      const res = await wishlistApi.getWishlist()
      if (res.success && res.data) {
        items.value = res.data || []
        wishlistedIds.value = new Set(items.value.map(i => i.productId))
      }
    } catch (err) {
      console.error('Không thể lấy danh sách yêu thích:', err)
    } finally {
      loading.value = false
    }
  }

  const toggleWishlist = async (product: any) => {
    const authStore = useAuthStore()
    if (!authStore.isAuthenticated) {
      return { needLogin: true }
    }

    const productId = product.id || product.productId
    if (wishlistedIds.value.has(productId)) {
      await wishlistApi.removeFromWishlist(productId)
      wishlistedIds.value.delete(productId)
      items.value = items.value.filter(i => i.productId !== productId)
      return { wishlisted: false }
    } else {
      await wishlistApi.addToWishlist(productId)
      wishlistedIds.value.add(productId)
      items.value.unshift({
        productId,
        productName: product.name || product.productName,
        thumbnailUrl: product.thumbnailUrl,
        minPrice: product.minPrice,
        brandName: product.brandName
      })
      // Ghi nhận hành vi WISHLIST có trọng số cao cho AI
      behaviorApi.track('WISHLIST', productId)
      return { wishlisted: true }
    }
  }

  const isWishlisted = (productId: number) => wishlistedIds.value.has(productId)

  return {
    items,
    wishlistedIds,
    loading,
    fetchWishlist,
    toggleWishlist,
    isWishlisted
  }
})
