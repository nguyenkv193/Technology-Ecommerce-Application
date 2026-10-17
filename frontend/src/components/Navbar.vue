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

        <!-- Search Bar -->
        <div class="flex-1 max-w-xl mx-2 hidden md:block">
          <SearchBox />
        </div>

        <!-- Navigation Links & Actions -->
        <div class="flex items-center gap-1.5 sm:gap-3 shrink-0">
          <!-- Mobile Search Trigger -->
          <button 
            type="button"
            @click="mobileSearchOpen = true"
            class="p-2 text-zinc-600 hover:text-zinc-900 rounded-lg hover:bg-zinc-100 transition md:hidden cursor-pointer" 
            title="Tìm kiếm"
          >
            <Search class="w-4 h-4" />
          </button>

          <router-link to="/products" class="text-xs font-medium text-zinc-600 hover:text-zinc-900 px-3 py-2 rounded-lg hover:bg-zinc-100 transition hidden sm:inline-block">
            Sản phẩm
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

          <button 
            v-else
            type="button"
            @click="authStore.openLoginModal()"
            class="px-3.5 py-1.5 rounded-lg text-xs font-semibold bg-zinc-900 hover:bg-zinc-800 text-white transition ml-1 cursor-pointer"
          >
            Đăng nhập
          </button>
        </div>
      </div>
    </div>

    <!-- Mobile Search Modal Drawer -->
    <Teleport to="body">
      <Transition name="fade">
        <div 
          v-if="mobileSearchOpen" 
          class="fixed inset-0 bg-black/50 backdrop-blur-sm z-[100] md:hidden flex flex-col"
        >
          <div class="bg-white p-4 shadow-xl border-b border-zinc-200">
            <div class="flex items-center gap-3">
              <div class="flex-1">
                <SearchBox />
              </div>
              <button 
                type="button"
                @click="mobileSearchOpen = false"
                class="px-3 py-2 rounded-xl text-xs font-semibold text-zinc-600 hover:bg-zinc-100 transition cursor-pointer shrink-0"
              >
                Đóng
              </button>
            </div>
          </div>
          <div class="flex-1" @click="mobileSearchOpen = false"></div>
        </div>
      </Transition>
    </Teleport>
  </nav>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from 'lucide-vue-next'
import CategoryMegaMenu from '@/components/CategoryMegaMenu.vue'
import SearchBox from '@/components/SearchBox.vue'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import { useWishlistStore } from '@/stores/wishlist'
import { toast } from 'vue-sonner'

const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()
const wishlistStore = useWishlistStore()

const userMenuOpen = ref<boolean>(false)
const mobileSearchOpen = ref<boolean>(false)

const handleLogout = () => {
  userMenuOpen.value = false
  authStore.logout()
  cartStore.resetCart()
  wishlistStore.items = []
  toast.success('Đã đăng xuất')

  if (router.currentRoute.value.path === '/orders') {
    router.push('/')
  }
}
</script>
