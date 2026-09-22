<template>
  <nav class="sticky top-0 z-50 bg-white/85 backdrop-blur-md border-b border-zinc-200/80 transition-colors">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-16 gap-3 sm:gap-4">
        <!-- Logo Chính Thức -->
        <router-link to="/" class="flex items-center gap-2 shrink-0 group py-1" title="TechStore - Trang chủ">
          <img 
            src="/logo.png" 
            alt="TechStore Logo" 
            class="h-10 sm:h-11 w-auto max-w-[170px] object-contain transition-transform duration-200 group-hover:scale-[1.03]" 
          />
        </router-link>

        <!-- Category Mega Menu Dropdown -->
        <CategoryMegaMenu />

        <!-- Minimal Search Bar -->
        <div class="flex-1 max-w-lg mx-2 hidden md:block">
          <form @submit.prevent="handleSearch" class="relative">
            <input 
              v-model="searchKeyword"
              type="text" 
              placeholder="Nhập tên điện thoại, laptop, phụ kiện... cần tìm" 
              class="w-full bg-zinc-100/80 hover:bg-zinc-100 border border-transparent hover:border-zinc-300/80 focus:bg-white focus:border-zinc-400 focus:ring-2 focus:ring-zinc-100 rounded-xl py-2 pl-9 pr-20 text-xs text-zinc-900 placeholder:text-zinc-400 transition outline-none"
            />
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-zinc-400">
              <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>
            </div>
            <button 
              type="submit"
              class="absolute inset-y-1 right-1 px-3 bg-zinc-900 hover:bg-zinc-800 text-white rounded-lg text-[11px] font-medium transition cursor-pointer"
            >
              Tìm
            </button>
          </form>
        </div>

        <!-- Navigation Links & Actions -->
        <div class="flex items-center gap-1.5 sm:gap-3 shrink-0">
          <router-link to="/products" class="text-xs font-medium text-zinc-600 hover:text-zinc-900 px-3 py-2 rounded-lg hover:bg-zinc-100 transition hidden sm:inline-block">
            Sản phẩm
          </router-link>

          <router-link to="/admin" class="text-xs font-medium text-zinc-700 hover:text-zinc-900 transition flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-zinc-100 hover:bg-zinc-200/70 border border-zinc-200/60">
            <svg class="w-3.5 h-3.5 text-zinc-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/><polyline points="3.27 6.96 12 12.01 20.73 6.96"/><line x1="12" y1="22.08" x2="12" y2="12"/></svg>
            <span class="hidden lg:inline">Quản trị</span>
          </router-link>

          <!-- Wishlist -->
          <router-link to="/wishlist" class="relative p-2 text-zinc-600 hover:text-zinc-900 rounded-lg hover:bg-zinc-100 transition" title="Danh sách yêu thích">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" /></svg>
            <span v-if="wishlistStore.items.length > 0" class="absolute top-1 right-1 w-4 h-4 rounded-full bg-zinc-900 text-white text-[9px] font-bold flex items-center justify-center">
              {{ wishlistStore.items.length }}
            </span>
          </router-link>

          <!-- Cart -->
          <router-link to="/cart" class="relative p-2 text-zinc-600 hover:text-zinc-900 rounded-lg hover:bg-zinc-100 transition" title="Giỏ hàng">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M16 11V7a4 4 0 0 0-8 0v4M5 9h14l1 12H4L5 9z" /></svg>
            <span v-if="cartStore.totalItems > 0" class="absolute top-1 right-1 w-4 h-4 rounded-full bg-zinc-900 text-white text-[9px] font-bold flex items-center justify-center">
              {{ cartStore.totalItems }}
            </span>
          </router-link>

          <!-- User Account -->
          <div v-if="authStore.isAuthenticated" class="relative ml-1">
            <button 
              @click="userMenuOpen = !userMenuOpen"
              class="flex items-center gap-2 p-1 pl-2 pr-2.5 rounded-lg bg-zinc-100 hover:bg-zinc-200/80 border border-zinc-200/60 transition cursor-pointer"
            >
              <div class="w-6 h-6 rounded-md bg-zinc-900 text-white font-bold text-[11px] flex items-center justify-center">
                {{ authStore.user?.fullName?.charAt(0) || 'U' }}
              </div>
              <span class="text-xs font-medium text-zinc-800 hidden md:inline-block max-w-[90px] truncate">
                {{ authStore.user?.fullName || 'Tài khoản' }}
              </span>
            </button>

            <!-- Dropdown Menu -->
            <div 
              v-if="userMenuOpen" 
              @click.outside="userMenuOpen = false"
              class="absolute right-0 mt-2 w-48 rounded-xl bg-white border border-zinc-200 shadow-xl p-1.5 z-50 space-y-0.5"
            >
              <div class="px-3 py-2 border-b border-zinc-100 text-xs">
                <div class="font-semibold text-zinc-900">{{ authStore.user?.fullName }}</div>
                <div class="text-zinc-400 truncate text-[11px]">{{ authStore.user?.email }}</div>
              </div>
              <router-link to="/orders" @click="userMenuOpen = false" class="flex items-center gap-2 px-3 py-2 text-xs font-medium text-zinc-700 hover:bg-zinc-100 rounded-lg transition">
                <svg class="w-3.5 h-3.5 text-zinc-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/><rect x="9" y="3" width="6" height="4" rx="2"/></svg>
                Lịch sử đơn hàng
              </router-link>
              <button @click="handleLogout" class="w-full text-left flex items-center gap-2 px-3 py-2 text-xs font-medium text-red-600 hover:bg-red-50 rounded-lg transition cursor-pointer">
                <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
                <span>Đăng xuất</span>
              </button>
            </div>
          </div>

          <router-link 
            v-else
            to="/login"
            class="px-3.5 py-1.5 rounded-lg text-xs font-semibold bg-zinc-900 hover:bg-zinc-800 text-white transition ml-1 cursor-pointer"
          >
            Đăng nhập
          </router-link>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import CategoryMegaMenu from '@/components/CategoryMegaMenu.vue'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { useWishlistStore } from '@/stores/wishlist'
import { behaviorApi } from '@/api/behaviorApi'

const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()
const wishlistStore = useWishlistStore()

const searchKeyword = ref<string>('')
const userMenuOpen = ref<boolean>(false)

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    behaviorApi.track('SEARCH', null, searchKeyword.value.trim())
    router.push({ path: '/products', query: { keyword: searchKeyword.value.trim() } })
  }
}

const handleLogout = () => {
  authStore.logout()
  userMenuOpen.value = false
  cartStore.clearCart()
  wishlistStore.items = []
  router.push('/')
}
</script>