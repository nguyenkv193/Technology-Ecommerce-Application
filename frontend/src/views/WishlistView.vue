<template>
  <div class="space-y-8">
    <div class="pb-5 border-b border-zinc-200/80">
      <h1 class="text-2xl sm:text-3xl font-bold text-zinc-900 tracking-tight">Danh Sách Yêu Thích</h1>
      <p class="text-xs sm:text-sm text-zinc-500 mt-1">Các thiết bị công nghệ bạn quan tâm (đóng góp trọng số cao vào mô hình gợi ý AI)</p>
    </div>

    <div v-if="wishlistStore.items.length === 0" class="text-center py-16 bg-white rounded-2xl border border-zinc-200/80 p-8 space-y-4 shadow-xs">
      <div class="w-14 h-14 mx-auto rounded-2xl bg-zinc-100 flex items-center justify-center text-zinc-500">
        <svg class="w-7 h-7" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" /></svg>
      </div>
      <div>
        <h3 class="text-base font-semibold text-zinc-900">Danh sách yêu thích đang trống</h3>
        <p class="text-xs text-zinc-500 max-w-sm mx-auto mt-1">Lưu lại những thiết bị bạn quan tâm để hệ thống AI hiểu rõ hơn gu công nghệ của bạn.</p>
      </div>
      <router-link to="/products" class="inline-block px-5 py-2.5 rounded-xl text-xs font-medium bg-zinc-900 text-white hover:bg-zinc-800 transition">
        Khám phá sản phẩm
      </router-link>
    </div>

    <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-5">
      <ProductCard 
        v-for="item in wishlistStore.items" 
        :key="item.id || item.productId"
        :product="{
          id: item.productId,
          name: item.productName,
          slug: '',
          description: '',
          thumbnailUrl: item.thumbnailUrl,
          minPrice: item.minPrice,
          brandName: item.brandName,
          categoryName: item.categoryName
        }"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import ProductCard from '@/components/ProductCard.vue'
import { useWishlistStore } from '@/stores/wishlist'

const wishlistStore = useWishlistStore()

onMounted(() => {
  wishlistStore.fetchWishlist()
})
</script>
