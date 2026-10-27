<template>
  <header class="h-16 bg-slate-900/60 backdrop-blur-md border-b border-slate-800 px-8 flex items-center justify-between shrink-0">
    <!-- Left: Page Title & Breadcrumb -->
    <div class="flex items-center gap-3">
      <h2 class="text-lg font-semibold text-slate-100 tracking-tight">{{ currentTitle }}</h2>
      <div class="hidden md:flex items-center gap-1.5 text-xs text-slate-400">
        <span class="text-slate-600">•</span>
        <span>TechStore Administration</span>
      </div>
    </div>

    <!-- Right: Status & Actions -->
    <div class="flex items-center gap-4">
      <!-- Live Server Indicator -->
      <div class="flex items-center gap-2 px-3 py-1.5 rounded-full bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-xs font-medium">
        <span class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
        <span>Hệ thống trực tuyến</span>
      </div>

      <!-- Role Badge -->
      <div class="px-2.5 py-1 rounded-md bg-blue-500/10 border border-blue-500/20 text-blue-400 text-xs font-semibold uppercase tracking-wider">
        {{ authStore.user?.role || 'ADMIN' }}
      </div>

      <!-- View Storefront Link -->
      <a
        href="http://localhost:5173"
        target="_blank"
        rel="noopener noreferrer"
        class="hidden sm:flex items-center gap-1.5 text-xs text-slate-400 hover:text-slate-200 transition-colors py-1.5 px-3 rounded-lg hover:bg-slate-800"
      >
        <ExternalLink class="w-3.5 h-3.5" />
        <span>Xem Storefront</span>
      </a>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { ExternalLink } from 'lucide-vue-next'
import { useAdminAuthStore } from '@/stores/auth'

const route = useRoute()
const authStore = useAdminAuthStore()

const currentTitle = computed(() => {
  return (route.meta?.title as string) || 'Bảng điều khiển'
})
</script>
