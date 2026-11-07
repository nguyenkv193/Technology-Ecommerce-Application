<template>
  <aside class="w-64 bg-zinc-950 border-r border-zinc-800/80 flex flex-col shrink-0 select-none backdrop-blur-xl relative z-20">
    <!-- Brand / Workspace Header -->
    <div class="h-16 flex items-center px-5 gap-3 border-b border-zinc-800/70">
      <div class="w-8 h-8 rounded-lg bg-zinc-900 border border-zinc-800 flex items-center justify-center text-white shadow-inner">
        <Cpu class="w-4 h-4 text-zinc-100" />
      </div>
      <div class="flex-1 min-w-0">
        <span class="font-semibold text-zinc-100 text-sm tracking-tight truncate block">TechStore Admin</span>
        <span class="block text-[11px] text-zinc-500 truncate">Hệ thống quản trị bán hàng</span>
      </div>
    </div>

    <!-- Navigation Menu with Groups -->
    <nav class="flex-1 px-3 py-4 space-y-5 overflow-y-auto">
      <!-- Section: Tổng quan -->
      <div>
        <div class="px-3 pb-1.5 text-[10px] font-semibold tracking-wider text-zinc-500 uppercase font-mono">
          Tổng quan
        </div>
        <div class="space-y-1">
          <router-link
            to="/"
            class="flex items-center px-3 py-2 rounded-lg text-xs font-medium transition-all group"
            :class="[
              isActive('/')
                ? 'bg-zinc-800/90 text-white shadow-sm border border-zinc-700/60'
                : 'text-zinc-400 hover:text-zinc-200 hover:bg-zinc-900/60'
            ]"
          >
            <div class="flex items-center gap-2.5">
              <LayoutDashboard class="w-4 h-4 transition-colors" :class="isActive('/') ? 'text-white' : 'text-zinc-400 group-hover:text-zinc-200'" />
              <span>Dashboard</span>
            </div>
          </router-link>
        </div>
      </div>

      <!-- Section: Quản lý thương mại -->
      <div>
        <div class="px-3 pb-1.5 text-[10px] font-semibold tracking-wider text-zinc-500 uppercase font-mono">
          Thương mại & Kho
        </div>
        <div class="space-y-1">
          <router-link
            to="/products"
            class="flex items-center px-3 py-2 rounded-lg text-xs font-medium transition-all group"
            :class="[
              isActive('/products')
                ? 'bg-zinc-800/90 text-white shadow-sm border border-zinc-700/60'
                : 'text-zinc-400 hover:text-zinc-200 hover:bg-zinc-900/60'
            ]"
          >
            <div class="flex items-center gap-2.5">
              <Package class="w-4 h-4 transition-colors" :class="isActive('/products') ? 'text-white' : 'text-zinc-400 group-hover:text-zinc-200'" />
              <span>Sản phẩm</span>
            </div>
          </router-link>

          <router-link
            to="/categories"
            class="flex items-center px-3 py-2 rounded-lg text-xs font-medium transition-all group"
            :class="[
              isActive('/categories')
                ? 'bg-zinc-800/90 text-white shadow-sm border border-zinc-700/60'
                : 'text-zinc-400 hover:text-zinc-200 hover:bg-zinc-900/60'
            ]"
          >
            <div class="flex items-center gap-2.5">
              <Layers class="w-4 h-4 transition-colors" :class="isActive('/categories') ? 'text-white' : 'text-zinc-400 group-hover:text-zinc-200'" />
              <span>Danh mục</span>
            </div>
          </router-link>

          <router-link
            to="/brands"
            class="flex items-center px-3 py-2 rounded-lg text-xs font-medium transition-all group"
            :class="[
              isActive('/brands')
                ? 'bg-zinc-800/90 text-white shadow-sm border border-zinc-700/60'
                : 'text-zinc-400 hover:text-zinc-200 hover:bg-zinc-900/60'
            ]"
          >
            <div class="flex items-center gap-2.5">
              <Award class="w-4 h-4 transition-colors" :class="isActive('/brands') ? 'text-white' : 'text-zinc-400 group-hover:text-zinc-200'" />
              <span>Thương hiệu</span>
            </div>
          </router-link>

          <router-link
            to="/orders"
            class="flex items-center px-3 py-2 rounded-lg text-xs font-medium transition-all group"
            :class="[
              isActive('/orders')
                ? 'bg-zinc-800/90 text-white shadow-sm border border-zinc-700/60'
                : 'text-zinc-400 hover:text-zinc-200 hover:bg-zinc-900/60'
            ]"
          >
            <div class="flex items-center gap-2.5">
              <ShoppingCart class="w-4 h-4 transition-colors" :class="isActive('/orders') ? 'text-white' : 'text-zinc-400 group-hover:text-zinc-200'" />
              <span>Đơn hàng</span>
            </div>
          </router-link>
        </div>
      </div>

      <!-- Section: Hệ thống -->
      <div>
        <div class="px-3 pb-1.5 text-[10px] font-semibold tracking-wider text-zinc-500 uppercase font-mono">
          Hạ tầng
        </div>
        <div class="space-y-1">
          <router-link
            to="/system-ops"
            class="flex items-center px-3 py-2 rounded-lg text-xs font-medium transition-all group"
            :class="[
              isActive('/system-ops')
                ? 'bg-zinc-800/90 text-white shadow-sm border border-zinc-700/60'
                : 'text-zinc-400 hover:text-zinc-200 hover:bg-zinc-900/60'
            ]"
          >
            <div class="flex items-center gap-2.5">
              <Activity class="w-4 h-4 transition-colors" :class="isActive('/system-ops') ? 'text-white' : 'text-zinc-400 group-hover:text-zinc-200'" />
              <span>Vận hành</span>
            </div>
          </router-link>
        </div>
      </div>
    </nav>

    <!-- Footer Profile & Logout -->
    <div class="p-3 border-t border-zinc-800/70">
      <div class="p-2.5 rounded-xl bg-zinc-900/50 border border-zinc-800/80 flex items-center justify-between">
        <div class="flex items-center gap-2.5 overflow-hidden">
          <div class="w-7 h-7 rounded-lg bg-zinc-800 border border-zinc-700 text-zinc-200 flex items-center justify-center text-xs font-semibold shrink-0">
            {{ userInitial }}
          </div>
          <div class="truncate">
            <p class="text-xs font-medium text-zinc-200 truncate">{{ authStore.user?.fullName || 'Quản trị viên' }}</p>
            <p class="text-[10px] font-mono text-zinc-500 truncate">{{ authStore.user?.email || 'admin@techstore.vn' }}</p>
          </div>
        </div>
        <button
          @click="handleLogout"
          title="Đăng xuất"
          class="text-zinc-400 hover:text-rose-400 p-1.5 rounded-lg hover:bg-zinc-800 transition-colors shrink-0"
        >
          <LogOut class="w-3.5 h-3.5" />
        </button>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  LayoutDashboard,
  Package,
  Layers,
  Award,
  ShoppingCart,
  Activity,
  Cpu,
  LogOut
} from 'lucide-vue-next'
import { useAdminAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAdminAuthStore()

function isActive(path: string) {
  if (path === '/') {
    return route.path === '/'
  }
  return route.path.startsWith(path)
}

const userInitial = computed(() => {
  const name = authStore.user?.fullName || 'Admin'
  return name.charAt(0).toUpperCase()
})

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>
