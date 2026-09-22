<template>
  <section class="bg-white rounded-2xl sm:rounded-3xl border border-zinc-200/80 p-5 sm:p-7 shadow-xs">
    <!-- Header -->
    <div class="flex items-center justify-between mb-5 sm:mb-6">
      <h2 class="text-base sm:text-lg font-bold text-zinc-900 tracking-tight">
        Danh mục nổi bật
      </h2>
      <router-link 
        to="/products" 
        class="text-xs font-semibold text-zinc-500 hover:text-zinc-900 transition flex items-center gap-1"
      >
        <span>Tất cả thiết bị</span>
        <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M9 18l6-6-6-6"/>
        </svg>
      </router-link>
    </div>

    <!-- 16 Categories Grid: 8 cols on desktop, 6 on tablet, 4 on mobile -->
    <div class="grid grid-cols-4 sm:grid-cols-6 lg:grid-cols-8 gap-y-6 sm:gap-y-8 gap-x-2 sm:gap-x-4">
      <router-link
        v-for="item in featuredCategories"
        :key="item.name"
        :to="item.link"
        class="group flex flex-col items-center justify-start text-center cursor-pointer select-none transition-transform duration-200 hover:-translate-y-1"
      >
        <!-- Product Thumbnail Image -->
        <div class="relative w-14 h-14 sm:w-16 sm:h-16 flex items-center justify-center">
          <img
            :src="item.img"
            :alt="item.name"
            class="max-w-full max-h-full object-contain group-hover:scale-110 transition-transform duration-300 drop-shadow-2xs"
            loading="lazy"
          />
        </div>

        <!-- Label -->
        <span class="text-[11px] sm:text-xs font-medium text-zinc-700 group-hover:text-zinc-950 transition-colors line-clamp-2 leading-tight mt-2 sm:mt-2.5 max-w-[85px] sm:max-w-[100px]">
          {{ item.name }}
        </span>
      </router-link>
    </div>
  </section>
</template>

<script setup lang="ts">
interface FeaturedCategory {
  name: string
  img: string
  link: string
}

const featuredCategories: FeaturedCategory[] = [
  // Hàng 1
  { name: 'Điện thoại', img: '/categories/phone.png', link: '/products?categoryId=2' },
  { name: 'Máy tính bảng', img: '/categories/tablet.png', link: '/products?categoryId=3' },
  { name: 'Laptop', img: '/categories/laptop.png', link: '/products?categoryId=1' },
  { name: 'Màn hình', img: '/categories/monitor.png', link: '/products?keyword=Màn hình' },
  { name: 'PC - Máy tính để bàn', img: '/categories/pc.png', link: '/products?keyword=PC' },
  { name: 'Phụ kiện', img: '/categories/accessories.png', link: '/products?categoryId=5' },
  { name: 'SIM FPT', img: '/categories/sim.png', link: '/products?keyword=SIM' },
  { name: 'Đồng hồ thông minh', img: '/categories/smartwatch.png', link: '/products?keyword=Watch' },

  // Hàng 2
  { name: 'Tivi', img: '/categories/tv.png', link: '/products?keyword=Tivi' },
  { name: 'Máy lạnh - Điều hòa', img: '/categories/aircon.png', link: '/products?keyword=Điều hòa' },
  { name: 'Máy lọc nước', img: '/categories/water_purifier.png', link: '/products?keyword=Máy lọc nước' },
  { name: 'Robot hút bụi', img: '/categories/robot_vacuum.png', link: '/products?keyword=Robot hút bụi' },
  { name: 'Tủ lạnh', img: '/categories/fridge.png', link: '/products?keyword=Tủ lạnh' },
  { name: 'Quạt điều hòa', img: '/categories/cooler_fan.png', link: '/products?keyword=Quạt điều hòa' },
  { name: 'Máy giặt', img: '/categories/washing_machine.png', link: '/products?keyword=Máy giặt' },
  { name: 'Phần mềm bản quyền', img: '/categories/software.png', link: '/products?keyword=Phần mềm' }
]
</script>