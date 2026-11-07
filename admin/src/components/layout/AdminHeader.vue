<template>
  <header class="h-14 bg-white/80 backdrop-blur-md border-b border-zinc-200 px-6 flex items-center justify-between shrink-0 relative z-10">
    <!-- Left: Breadcrumb Navigation -->
    <div class="flex items-center gap-2 text-xs">
      <span class="text-zinc-400 font-mono">TechStore</span>
      <ChevronRight class="w-3.5 h-3.5 text-zinc-400" />
      <h2 class="font-semibold text-zinc-900 tracking-tight">{{ currentTitle }}</h2>
    </div>

    <!-- Right: Storefront Link & User Info -->
    <div class="flex items-center gap-3">
      <!-- View Storefront Link -->
      <a
        href="http://localhost:3000"
        target="_blank"
        rel="noopener noreferrer"
        class="flex items-center gap-1.5 text-xs text-zinc-600 hover:text-zinc-900 transition-colors py-1.5 px-2.5 rounded-md hover:bg-zinc-100 border border-transparent hover:border-zinc-200"
      >
        <ExternalLink class="w-3.5 h-3.5" />
        <span class="hidden sm:inline">Xem TechStore</span>
      </a>

      <!-- Role Badge -->
      <div class="px-2 py-0.5 rounded text-[10px] font-mono font-medium bg-zinc-100 text-zinc-700 border border-zinc-200">
        {{ authStore.user?.role || 'ADMIN' }}
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { ExternalLink, ChevronRight } from 'lucide-vue-next'
import { useAdminAuthStore } from '@/stores/auth'

const route = useRoute()
const authStore = useAdminAuthStore()

const currentTitle = computed(() => {
  return (route.meta?.title as string) || 'Bảng điều khiển'
})
</script>
