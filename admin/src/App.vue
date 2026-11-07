<template>
  <div class="min-h-screen bg-[#fafafc] text-zinc-900 flex flex-col font-sans antialiased">
    <router-view />
    <Toaster position="top-right" :duration="3000" :closeButton="true" richColors />
  </div>
</template>

<script setup lang="ts">
import { Toaster } from 'vue-sonner'
</script>
