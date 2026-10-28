<template>
  <header class="h-14 bg-zinc-950/80 backdrop-blur-md border-b border-zinc-800/80 px-6 flex items-center justify-between shrink-0 relative z-10">
    <!-- Left: Breadcrumb Navigation -->
    <div class="flex items-center gap-2 text-xs">
      <span class="text-zinc-500 font-mono">TechStore</span>
      <ChevronRight class="w-3.5 h-3.5 text-zinc-700" />
      <h2 class="font-medium text-zinc-100 tracking-tight">{{ currentTitle }}</h2>
    </div>

    <!-- Center: Search Trigger (Linear style) -->
    <div class="hidden sm:flex items-center gap-2 px-3 py-1.5 rounded-lg bg-zinc-900/80 border border-zinc-800/80 text-zinc-400 hover:border-zinc-700/80 transition-all text-xs cursor-pointer w-64 md:w-80 justify-between">
      <div class="flex items-center gap-2">
        <Search class="w-3.5 h-3.5 text-zinc-500" />
        <span class="text-zinc-500 text-[11px]">Tìm sản phẩm, đơn hàng, khách...</span>
      </div>
      <kbd class="text-[10px] font-mono text-zinc-500 bg-zinc-950 px-1.5 py-0.5 rounded border border-zinc-800">⌘K</kbd>
    </div>

    <!-- Right: Status, Storefront & Notifications -->
    <div class="flex items-center gap-3">
      <!-- Live Server Indicator -->
      <div class="hidden lg:flex items-center gap-2 px-2.5 py-1 rounded-md bg-zinc-900 border border-zinc-800/80 text-zinc-300 text-[11px] font-mono">
        <span class="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse"></span>
        <span>Online</span>
      </div>

      <!-- View Storefront Link -->
      <a
        href="http://localhost:5173"
        target="_blank"
        rel="noopener noreferrer"
        class="flex items-center gap-1.5 text-xs text-zinc-400 hover:text-zinc-100 transition-colors py-1.5 px-2.5 rounded-md hover:bg-zinc-900 border border-transparent hover:border-zinc-800"
      >
        <ExternalLink class="w-3.5 h-3.5" />
        <span class="hidden md:inline">Storefront</span>
      </a>

      <!-- Notification Bell -->
      <button
        type="button"
        title="Thông báo"
        class="relative p-1.5 rounded-md text-zinc-400 hover:text-zinc-100 hover:bg-zinc-900 transition-colors border border-transparent hover:border-zinc-800"
      >
        <Bell class="w-4 h-4" />
        <span class="absolute top-1 right-1 w-1.5 h-1.5 rounded-full bg-blue-500"></span>
      </button>

      <!-- Role Badge -->
      <div class="px-2 py-0.5 rounded text-[10px] font-mono font-medium bg-zinc-900 text-zinc-300 border border-zinc-800">
        {{ authStore.user?.role || 'ADMIN' }}
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { ExternalLink, Search, Bell, ChevronRight } from 'lucide-vue-next'
import { useAdminAuthStore } from '@/stores/auth'

const route = useRoute()
const authStore = useAdminAuthStore()

const currentTitle = computed(() => {
  return (route.meta?.title as string) || 'Bảng điều khiển'
})
</script>
