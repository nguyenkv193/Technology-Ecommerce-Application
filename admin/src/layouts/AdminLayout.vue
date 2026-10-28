<template>
  <div class="h-screen w-screen flex bg-zinc-950 overflow-hidden font-sans text-zinc-100 antialiased selection:bg-zinc-800 selection:text-white">
    <!-- Sidebar -->
    <AdminSidebar />

    <!-- Main Container -->
    <div class="flex-1 flex flex-col min-w-0 overflow-hidden bg-zinc-950 relative">
      <!-- Ambient light effect at top -->
      <div class="absolute inset-x-0 top-0 h-96 bg-[radial-gradient(ellipse_80%_50%_at_50%_-20%,rgba(120,119,198,0.1),transparent)] pointer-events-none"></div>

      <!-- Top Header -->
      <AdminHeader />

      <!-- Scrollable Main View Area -->
      <main class="flex-1 overflow-y-auto px-6 py-8 lg:px-8 bg-transparent relative z-10">
        <div class="max-w-7xl mx-auto space-y-8 pb-12">
          <router-view />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import AdminSidebar from '@/components/layout/AdminSidebar.vue'
import AdminHeader from '@/components/layout/AdminHeader.vue'
</script>
