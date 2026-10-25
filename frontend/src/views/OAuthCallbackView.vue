<template>
  <section class="min-h-[50vh] flex items-center justify-center">
    <div class="w-full max-w-md rounded-xl border border-zinc-200 bg-white p-8 text-center shadow-sm">
      <div
        v-if="loading"
        class="mx-auto mb-4 h-10 w-10 animate-spin rounded-full border-4 border-zinc-200 border-t-zinc-900"
        aria-label="Đang xử lý đăng nhập"
      ></div>

      <h1 class="text-xl font-bold text-zinc-900">
        {{ loading ? 'Đang hoàn tất đăng nhập...' : errorMessage ? 'Đăng nhập chưa thành công' : 'Đăng nhập thành công' }}
      </h1>

      <p v-if="loading" class="mt-2 text-sm text-zinc-500">
        Vui lòng đợi trong giây lát.
      </p>
      <p v-else-if="errorMessage" class="mt-2 text-sm text-red-600">
        {{ errorMessage }}
      </p>
      <p v-else class="mt-2 text-sm text-zinc-500">
        Bạn sẽ được chuyển về trang chủ.
      </p>

      <button
        v-if="!loading"
        type="button"
        class="mt-6 rounded-xl bg-zinc-900 px-5 py-2.5 text-sm font-medium text-white transition hover:bg-black"
        @click="router.replace('/')"
      >
        Về trang chủ
      </button>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { toast } from 'vue-sonner'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { useWishlistStore } from '@/stores/wishlist'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()
const wishlistStore = useWishlistStore()

const loading = ref(true)
const errorMessage = ref('')

onMounted(async () => {
  const code = typeof route.query.code === 'string' ? route.query.code : ''
  const providerError = typeof route.query.error === 'string' ? route.query.error : ''

  if (providerError || !code) {
    loading.value = false
    errorMessage.value = providerError
      ? 'Nhà cung cấp OAuth đã từ chối hoặc hủy đăng nhập.'
      : 'Không tìm thấy mã xác thực OAuth.'
    return
  }

  try {
    const response = await authStore.exchangeOAuthCode(code)
    if (!response.success) {
      throw new Error(response.message || 'Không thể hoàn tất đăng nhập OAuth.')
    }

    await Promise.all([
      cartStore.fetchCart(),
      wishlistStore.fetchWishlist()
    ])

    authStore.closeAuthModal()
    toast.success('Đăng nhập Google thành công')
    await router.replace('/')
  } catch (error: any) {
    errorMessage.value = error?.message || 'Không thể hoàn tất đăng nhập OAuth.'
    toast.error(errorMessage.value)
  } finally {
    loading.value = false
  }
})
</script>
