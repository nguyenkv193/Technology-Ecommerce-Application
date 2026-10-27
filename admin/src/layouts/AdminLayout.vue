<template>
  <div class="h-screen w-screen flex bg-slate-950 overflow-hidden font-sans text-slate-100">
    <!-- Sidebar -->
    <AdminSidebar />

    <!-- Main Container -->
    <div class="flex-1 flex flex-col min-w-0 overflow-hidden">
      <!-- Top Header -->
      <AdminHeader />

      <!-- Scrollable Main View Area -->
      <main class="flex-1 overflow-y-auto p-8 bg-slate-950">
        <div class="max-w-7xl mx-auto space-y-8">
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
