<template>
  <div class="min-h-screen flex flex-col bg-[#fafafc] text-zinc-900 selection:bg-zinc-900 selection:text-white">
    <Navbar />
    
    <main class="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8 sm:py-12">
      <router-view />
    </main>

    <Footer />

    <!-- Global Auth & Logout Modals -->
    <AuthModal />
    <LogoutConfirmModal />

    <!-- Global Modern Toast Notifications -->
    <Toaster position="top-right" richColors closeButton :duration="3000" />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { Toaster } from 'vue-sonner'
import Navbar from '@/components/Navbar.vue'
import Footer from '@/components/Footer.vue'
import AuthModal from '@/components/auth/AuthModal.vue'
import LogoutConfirmModal from '@/components/auth/LogoutConfirmModal.vue'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { useWishlistStore } from '@/stores/wishlist'

const authStore = useAuthStore()
const cartStore = useCartStore()
const wishlistStore = useWishlistStore()

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