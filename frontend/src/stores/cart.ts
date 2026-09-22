import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { cartApi } from '@/api/cartApi'
import { behaviorApi } from '@/api/behaviorApi'
import { useAuthStore } from './auth'
import type { CartItem } from '@/types'

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([])
  const totalItems = computed(() => items.value.reduce((sum, item) => sum + item.quantity, 0))
  const totalPrice = computed(() => items.value.reduce((sum, item) => sum + item.subTotal, 0))
  const loading = ref(false)

  const fetchCart = async () => {
    const authStore = useAuthStore()
    if (!authStore.isAuthenticated) {
      items.value = []
      return
    }

    loading.value = true
    try {
      const res = await cartApi.getCart()
      if (res.success && res.data) {
        items.value = res.data.items || []
      }
    } catch (err) {
      console.error('Không thể lấy giỏ hàng:', err)
    } finally {
      loading.value = false
    }
  }

  const addToCart = async (variantId: number, quantity: number = 1, productId: number | null = null) => {
    const res = await cartApi.addToCart(variantId, quantity)
    if (res.success && res.data) {
      items.value = res.data.items || []
      // Ghi nhận hành vi ADD_TO_CART cho AI
      if (productId) {
        behaviorApi.track('ADD_TO_CART', productId, `quantity=${quantity}`)
      }
    }
    return res
  }

  const updateQuantity = async (itemId: number, quantity: number) => {
    const res = await cartApi.updateQuantity(itemId, quantity)
    if (res.success && res.data) {
      items.value = res.data.items || []
    }
    return res
  }

  const removeItem = async (itemId: number) => {
    const res = await cartApi.removeItem(itemId)
    if (res.success && res.data) {
      items.value = res.data.items || []
    }
    return res
  }

  const clearCart = async () => {
    const res = await cartApi.clearCart()
    if (res.success) {
      items.value = []
    }
    return res
  }

  return {
    items,
    totalItems,
    totalPrice,
    loading,
    fetchCart,
    addToCart,
    updateQuantity,
    removeItem,
    clearCart
  }
})
