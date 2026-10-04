<template>
  <div 
    :class="isAuthRoute 
      ? 'h-screen w-screen overflow-hidden bg-[#fafafc] flex flex-col' 
      : 'min-h-screen flex flex-col bg-[#fafafc] text-zinc-900 selection:bg-zinc-900 selection:text-white'"
  >
    <Navbar v-if="!isAuthRoute" />
    
    <main :class="isAuthRoute ? 'flex-1 h-full w-full overflow-hidden' : 'flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8 sm:py-12'">
      <router-view />
    </main>

    <Footer v-if="!isAuthRoute" />
  </div>
</template>

<script setup lang="ts">
import { computed, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from '@/components/Navbar.vue'
import Footer from '@/components/Footer.vue'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { useWishlistStore } from '@/stores/wishlist'

const route = useRoute()
const authStore = useAuthStore()
const cartStore = useCartStore()
const wishlistStore = useWishlistStore()

const isAuthRoute = computed(() => {
  return route.name === 'login' || 
         route.name === 'register' || 
         route.path === '/login' || 
         route.path === '/register' ||
         Boolean(route.meta?.hideHeaderFooter)
})

// Khóa hoàn toàn thanh cuộn của trình duyệt khi ở trang Login / Register
watch(isAuthRoute, (authActive) => {
  if (typeof document !== 'undefined') {
    if (authActive) {
      document.documentElement.classList.add('overflow-hidden', 'h-full')
      document.body.classList.add('overflow-hidden', 'h-full')
    } else {
      document.documentElement.classList.remove('overflow-hidden', 'h-full')
      document.body.classList.remove('overflow-hidden', 'h-full')
    }
  }
}, { immediate: true })

onMounted(async () => {
  if (authStore.isAuthenticated) {
    await authStore.fetchProfile()
    await Promise.all([
      cartStore.fetchCart(),
      wishlistStore.fetchWishlist()
    ])
  }
})
</script>