<template>
  <aside class="w-64 bg-slate-900 border-r border-slate-800 flex flex-col shrink-0 select-none">
    <!-- Brand / Logo -->
    <div class="h-16 flex items-center px-6 gap-3 border-b border-slate-800/80">
      <div class="w-9 h-9 rounded-xl bg-blue-600 flex items-center justify-center text-white shadow-lg shadow-blue-600/30">
        <Cpu class="w-5 h-5" />
      </div>
      <div>
        <span class="font-bold text-slate-100 text-base tracking-tight">TechStore</span>
        <span class="block text-[10px] uppercase font-semibold tracking-wider text-blue-400">Backoffice Admin</span>
      </div>
    </div>

    <!-- Navigation Menu -->
    <nav class="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
      <router-link
        v-for="item in menuItems"
        :key="item.path"
        :to="item.path"
        class="flex items-center gap-3 px-3.5 py-2.5 rounded-xl text-sm font-medium transition-all group"
        :class="[
          isActive(item.path)
            ? 'bg-blue-600 text-white shadow-md shadow-blue-600/20'
            : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
        ]"
      >
        <component
          :is="item.icon"
          class="w-5 h-5 transition-transform group-hover:scale-110"
          :class="isActive(item.path) ? 'text-white' : 'text-slate-400 group-hover:text-slate-200'"
        />
        <span>{{ item.label }}</span>
      </router-link>
    </nav>

    <!-- Footer Profile & Logout -->
    <div class="p-3 border-t border-slate-800/80">
      <div class="p-3 rounded-xl bg-slate-950/60 border border-slate-800/60 flex items-center justify-between">
        <div class="flex items-center gap-2.5 overflow-hidden">
          <div class="w-8 h-8 rounded-full bg-blue-500/20 border border-blue-500/30 text-blue-400 flex items-center justify-center text-xs font-bold uppercase shrink-0">
            {{ userInitial }}
          </div>
          <div class="truncate">
            <p class="text-xs font-semibold text-slate-200 truncate">{{ authStore.user?.fullName || 'Quản trị viên' }}</p>
            <p class="text-[10px] text-slate-400 truncate">{{ authStore.user?.email || 'admin@techstore.vn' }}</p>
          </div>
        </div>
        <button
          @click="handleLogout"
          title="Đăng xuất"
          class="text-slate-400 hover:text-rose-400 p-1.5 rounded-lg hover:bg-slate-800 transition-colors"
        >
          <LogOut class="w-4 h-4" />
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

const menuItems = [
  { path: '/', label: 'Dashboard', icon: LayoutDashboard },
  { path: '/products', label: 'Sản phẩm', icon: Package },
  { path: '/categories', label: 'Danh mục', icon: Layers },
  { path: '/brands', label: 'Thương hiệu', icon: Award },
  { path: '/orders', label: 'Đơn hàng', icon: ShoppingCart },
  { path: '/system-ops', label: 'Vận hành', icon: Activity }
]

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
