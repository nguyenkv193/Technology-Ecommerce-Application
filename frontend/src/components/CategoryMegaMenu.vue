<template>
  <div class="relative" ref="menuContainerRef">
    <!-- Trigger Button in Navbar -->
    <button
      type="button"
      @click="toggleMenu"
      class="flex items-center gap-2 px-3 py-2 rounded-xl text-xs font-semibold transition-all duration-200 cursor-pointer select-none"
      :class="isOpen 
        ? 'bg-zinc-900 text-white shadow-sm' 
        : 'bg-zinc-100 hover:bg-zinc-200/80 text-zinc-800 border border-zinc-200/70'"
      title="Danh mục thiết bị công nghệ"
      aria-label="Mở danh mục thiết bị"
    >
      <!-- Hamburger / Grid Icon -->
      <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
        <line x1="3" y1="6" x2="21" y2="6"/>
        <line x1="3" y1="12" x2="21" y2="12"/>
        <line x1="3" y1="18" x2="21" y2="18"/>
      </svg>
      <span class="hidden sm:inline">Danh mục</span>
      <svg 
        class="w-3.5 h-3.5 transition-transform duration-200" 
        :class="isOpen ? 'rotate-180' : ''" 
        viewBox="0 0 24 24" 
        fill="none" 
        stroke="currentColor" 
        stroke-width="2"
      >
        <polyline points="6 9 12 15 18 9"/>
      </svg>
    </button>

    <!-- Semi-transparent Backdrop Overlay -->
    <Teleport to="body">
      <Transition name="fade">
        <div 
          v-if="isOpen" 
          @click="closeMenu"
          class="fixed inset-0 bg-black/25 backdrop-blur-[2px] z-40"
        ></div>
      </Transition>
    </Teleport>

    <!-- Mega Menu Dropdown Panel -->
    <Transition name="mega-dropdown">
      <div 
        v-if="isOpen"
        class="fixed left-4 right-4 sm:left-6 sm:right-6 lg:left-auto lg:right-auto lg:absolute lg:top-full lg:mt-2.5 lg:left-0 w-auto lg:w-[980px] xl:w-[1120px] max-w-[calc(100vw-2rem)] bg-white rounded-2xl border border-zinc-200 shadow-2xl z-50 overflow-hidden"
      >
        <div class="grid grid-cols-1 lg:grid-cols-12 min-h-[500px] max-h-[calc(100vh-6rem)] overflow-y-auto lg:overflow-hidden">
          
          <!-- COLUMN 1: Categories Left Sidebar (3 cols = 25%) -->
          <div class="lg:col-span-3 bg-[#fafafc] border-b lg:border-b-0 lg:border-r border-zinc-200/80 p-3 flex flex-col justify-between select-none">
            <div class="space-y-3">
              <!-- Category Header -->
              <div class="space-y-1">
                <div class="px-2 py-1 text-[10px] font-bold uppercase tracking-wider text-zinc-400">
                  Danh mục sản phẩm
                </div>

                <div class="space-y-0.5">
                  <button
                    v-for="cat in categoryData"
                    :key="cat.id"
                    @mouseenter="activeCatId = cat.id"
                    @click="activeCatId = cat.id"
                    class="w-full flex items-center justify-between px-2.5 py-2 rounded-xl text-xs font-medium transition-all text-left cursor-pointer"
                    :class="activeCatId === cat.id 
                      ? 'bg-white text-zinc-950 font-bold shadow-xs border border-zinc-200/80' 
                      : 'text-zinc-600 hover:text-zinc-900 hover:bg-zinc-200/50 border border-transparent'"
                  >
                    <div class="flex items-center gap-2.5">
                      <span 
                        class="w-7 h-7 rounded-lg flex items-center justify-center transition-colors shrink-0"
                        :class="activeCatId === cat.id ? 'bg-zinc-900 text-white' : 'bg-zinc-200/80 text-zinc-700'"
                        v-html="cat.iconSvg"
                      ></span>
                      <span class="truncate">{{ cat.name }}</span>
                    </div>
                    <svg 
                      class="w-3.5 h-3.5 transition-transform shrink-0"
                      :class="activeCatId === cat.id ? 'text-zinc-900 translate-x-0.5' : 'text-zinc-400'"
                      viewBox="0 0 24 24" 
                      fill="none" 
                      stroke="currentColor" 
                      stroke-width="2"
                    >
                      <polyline points="9 18 15 12 9 6"/>
                    </svg>
                  </button>
                </div>
              </div>

              <!-- Chuyên trang thương hiệu (Brand Portals with Official Logos) -->
              <div class="pt-2 border-t border-zinc-200/70 space-y-1.5">
                <div class="px-2 text-[10px] font-bold uppercase tracking-wider text-zinc-400 flex items-center justify-between">
                  <span>Chuyên trang thương hiệu</span>
                </div>

                <div class="grid grid-cols-2 gap-1 px-0.5">
                  <router-link
                    v-for="b in featuredBrandPortals"
                    :key="b.id"
                    :to="`/products?brandId=${b.id}`"
                    @click="closeMenu"
                    class="flex items-center gap-2 px-2 py-1.5 rounded-lg text-xs font-semibold text-zinc-700 hover:text-zinc-950 hover:bg-white transition border border-transparent hover:border-zinc-200/80 shadow-2xs group"
                  >
                    <div class="w-5 h-5 rounded-md bg-white border border-zinc-200/80 flex items-center justify-center shrink-0 p-0.5">
                      <img :src="`${b.logoUrl}?v=3`" :alt="b.name" class="max-h-full max-w-full object-contain" />
                    </div>
                    <span class="truncate text-[11px] font-medium">{{ b.name }}</span>
                  </router-link>
                </div>
              </div>
            </div>

            <!-- Bottom Left Footer Link -->
            <div class="pt-2.5 border-t border-zinc-200/70 px-1">
              <router-link
                to="/products"
                @click="closeMenu"
                class="flex items-center justify-between text-xs font-semibold text-zinc-700 hover:text-zinc-950 transition py-1.5 px-2 rounded-lg hover:bg-white"
              >
                <span>Tất cả sản phẩm</span>
                <span class="text-zinc-400">→</span>
              </router-link>
            </div>
          </div>

          <!-- COLUMN 2: Subcategories, Brands & Groups (6 cols = 55%) -->
          <div class="lg:col-span-6 xl:col-span-6 p-5 sm:p-6 bg-white overflow-y-auto flex flex-col justify-between">
            <div class="space-y-5">
              <!-- Top Row: "Gợi ý cho bạn" VỚI LOGO CHÍNH HÃNG CHUẨN FPT SHOP -->
              <div>
                <div class="flex items-center justify-between mb-3">
                  <div class="flex items-center gap-1.5 text-xs font-bold text-zinc-900">
                    <span class="text-amber-500">🔥</span>
                    <span>Gợi ý cho bạn</span>
                  </div>
                  <router-link 
                    :to="`/products?categoryId=${currentCat.id}`" 
                    @click="closeMenu"
                    class="text-[11px] font-medium text-zinc-500 hover:text-zinc-900 transition"
                  >
                    Xem tất cả {{ currentCat.name }} →
                  </router-link>
                </div>

                <!-- Brand Cards with Real Official SVG Logos -->
                <div class="flex flex-wrap gap-2">
                  <router-link
                    v-for="brand in currentCat.brands"
                    :key="brand.id"
                    :to="`/products?categoryId=${currentCat.id}&brandId=${brand.id}`"
                    @click="closeMenu"
                    class="h-8 w-[72px] sm:w-[78px] rounded-xl bg-white hover:bg-zinc-50 border border-zinc-200/90 hover:border-zinc-300 shadow-2xs hover:shadow-xs flex items-center justify-center px-2 py-1 transition-all cursor-pointer group shrink-0"
                    :title="brand.name"
                  >
                    <img 
                      :src="`${brand.logoUrl}?v=3`" 
                      :alt="brand.name" 
                      class="max-h-[14px] max-w-[46px] w-auto h-auto object-contain transition-transform duration-200 group-hover:scale-105"
                      loading="lazy"
                    />
                  </router-link>
                </div>
              </div>

              <!-- Feature Highlights with Visual Thumbnail Cards (FPT Shop Style) -->
              <div>
                <div class="text-[10px] font-bold text-zinc-400 uppercase tracking-wider mb-2.5">
                  Phân loại đặc sắc
                </div>
                <div class="grid grid-cols-3 sm:grid-cols-5 gap-2">
                  <router-link
                    v-for="(hl, hIdx) in currentCat.highlights"
                    :key="hIdx"
                    :to="`/products?categoryId=${currentCat.id}&keyword=${encodeURIComponent(hl.keyword)}`"
                    @click="closeMenu"
                    class="flex flex-col items-center text-center p-2 rounded-xl bg-zinc-50/80 hover:bg-zinc-100/90 border border-zinc-200/60 hover:border-zinc-300 transition-all group cursor-pointer"
                  >
                    <div class="w-10 h-10 rounded-xl bg-white flex items-center justify-center mb-1.5 shadow-2xs border border-zinc-100 group-hover:scale-105 transition-transform">
                      <span class="text-lg">{{ hl.icon }}</span>
                    </div>
                    <span class="text-[11px] font-semibold text-zinc-700 group-hover:text-zinc-950 leading-tight">
                      {{ hl.label }}
                    </span>
                  </router-link>
                </div>
              </div>

              <!-- Multi-column Sub-groups -->
              <div class="pt-3 border-t border-zinc-100 grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-4">
                <div v-for="(group, gIdx) in currentCat.groups" :key="gIdx" class="space-y-2">
                  <h4 class="text-xs font-bold text-zinc-900 flex items-center gap-1">
                    <span>{{ group.title }}</span>
                  </h4>
                  <ul class="space-y-1.5">
                    <li v-for="(item, iIdx) in group.items" :key="iIdx">
                      <router-link
                        :to="item.link || `/products?categoryId=${currentCat.id}&keyword=${encodeURIComponent(item.name)}`"
                        @click="closeMenu"
                        class="text-[11px] text-zinc-600 hover:text-zinc-950 hover:underline transition-colors block py-0.5"
                      >
                        {{ item.name }}
                      </router-link>
                    </li>
                  </ul>
                </div>
              </div>
            </div>

            <!-- Bottom Quality Assurance -->
            <div class="pt-4 mt-4 border-t border-zinc-100 flex items-center justify-between text-[11px] text-zinc-400">
              <span>Cam kết 100% hàng chính hãng đầy đủ VAT</span>
              <span class="text-emerald-600 font-medium flex items-center gap-1">
                <span class="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
                Sẵn hàng tại kho
              </span>
            </div>
          </div>

          <!-- COLUMN 3: Services & Featured Promo Banner (3 cols = 20%) -->
          <div class="lg:col-span-3 xl:col-span-3 bg-zinc-50/60 border-t lg:border-t-0 lg:border-l border-zinc-200/80 p-4 flex flex-col justify-between">
            <!-- Services / Perks List -->
            <div class="space-y-3">
              <div class="text-[11px] font-bold uppercase tracking-wider text-zinc-400 px-1">
                Dịch vụ & Tiện ích
              </div>

              <div class="space-y-1">
                <div 
                  v-for="(service, sIdx) in services" 
                  :key="sIdx"
                  class="flex items-start gap-2.5 p-2 rounded-xl hover:bg-white transition border border-transparent hover:border-zinc-200/60 shadow-2xs"
                >
                  <span class="text-base shrink-0">{{ service.icon }}</span>
                  <div class="min-w-0">
                    <div class="text-xs font-semibold text-zinc-900 leading-snug">{{ service.title }}</div>
                    <div class="text-[10px] text-zinc-500 leading-tight mt-0.5">{{ service.desc }}</div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Dynamic Category Promo Banner Card -->
            <div class="mt-4 pt-3 border-t border-zinc-200/70">
              <router-link
                :to="currentCat.promoBanner.link"
                @click="closeMenu"
                class="block relative rounded-xl overflow-hidden group border border-zinc-200 shadow-sm"
              >
                <!-- Promo Image -->
                <div class="aspect-[16/10] bg-zinc-900 relative overflow-hidden">
                  <img
                    :src="currentCat.promoBanner.image"
                    :alt="currentCat.promoBanner.title"
                    class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500 opacity-80 group-hover:opacity-90"
                    loading="lazy"
                  />
                  <div class="absolute inset-0 bg-gradient-to-t from-black/85 via-black/30 to-transparent"></div>
                  
                  <!-- Tag Badge -->
                  <div class="absolute top-2 left-2">
                    <span class="text-[9px] font-bold uppercase px-2 py-0.5 rounded bg-zinc-900/90 text-white backdrop-blur-md border border-white/20">
                      {{ currentCat.promoBanner.tag }}
                    </span>
                  </div>

                  <!-- Text on Card -->
                  <div class="absolute bottom-2 left-2 right-2 text-white">
                    <div class="font-bold text-xs leading-tight drop-shadow-sm truncate">
                      {{ currentCat.promoBanner.title }}
                    </div>
                    <div class="text-[10px] text-zinc-300 mt-0.5 truncate">
                      {{ currentCat.promoBanner.subtitle }}
                    </div>
                  </div>
                </div>
              </router-link>
            </div>
          </div>

        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'

const isOpen = ref<boolean>(false)
const menuContainerRef = ref<HTMLElement | null>(null)
const activeCatId = ref<number>(2) // Mặc định mở "Điện thoại"

const toggleMenu = () => {
  isOpen.value = !isOpen.value
}

const closeMenu = () => {
  isOpen.value = false
}

// Click outside handler & Escape key listener
const handleClickOutside = (event: MouseEvent) => {
  if (menuContainerRef.value && !menuContainerRef.value.contains(event.target as Node)) {
    closeMenu()
  }
}

const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'Escape') {
    closeMenu()
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  document.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  document.removeEventListener('keydown', handleKeydown)
})

// Chuyên trang thương hiệu bên cột trái (Brand Portals)
const featuredBrandPortals = [
  { id: 1, name: 'Apple', logoUrl: '/brands/apple.svg' },
  { id: 2, name: 'Samsung', logoUrl: '/brands/samsung.svg' },
  { id: 4, name: 'ASUS', logoUrl: '/brands/asus.svg' },
  { id: 3, name: 'Dell', logoUrl: '/brands/dell.svg' },
  { id: 5, name: 'Sony', logoUrl: '/brands/sony.svg' },
  { id: 6, name: 'NVIDIA', logoUrl: '/brands/nvidia.svg' },
  { id: 7, name: 'Logitech', logoUrl: '/brands/logitech.svg' },
  { id: 2, name: 'Xiaomi', logoUrl: '/brands/xiaomi.svg' }
]

// Dịch vụ & Tiện ích TechStore
const services = [
  {
    icon: '🔄',
    title: 'Thu cũ đổi mới',
    desc: 'Trợ giá lên tới 3.000.000đ'
  },
  {
    icon: '⚡',
    title: 'Giao hỏa tốc 2H',
    desc: 'Miễn phí nội thành siêu tốc'
  },
  {
    icon: '🛡️',
    title: 'Bảo hành 12 tháng',
    desc: '100% Chính hãng Apple, Samsung'
  },
  {
    icon: '🏢',
    title: 'Khách hàng Doanh nghiệp',
    desc: 'Chiết khấu dự án & hợp đồng'
  }
]

// Dữ liệu danh mục chi tiết với LOGO CHÍNH THỨC CỦA CÁC HÃNG (FPT Shop Style)
const categoryData = [
  {
    id: 2,
    name: 'Điện thoại',
    slug: 'dien-thoai',
    iconSvg: `<svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="5" y="2" width="14" height="20" rx="2" ry="2"/><line x1="12" y1="18" x2="12.01" y2="18"/></svg>`,
    brands: [
      { id: 1, name: 'Apple', logoUrl: '/brands/apple.svg' },
      { id: 2, name: 'Samsung', logoUrl: '/brands/samsung.svg' },
      { id: 2, name: 'Xiaomi', logoUrl: '/brands/xiaomi.svg' },
      { id: 2, name: 'OPPO', logoUrl: '/brands/oppo.svg' },
      { id: 2, name: 'HONOR', logoUrl: '/brands/honor.svg' },
      { id: 4, name: 'ASUS ROG', logoUrl: '/brands/asus.svg' }
    ],
    highlights: [
      { label: 'Điện thoại 5G', icon: '⚡', keyword: '5G' },
      { label: 'Galaxy / Apple AI', icon: '🤖', keyword: 'AI' },
      { label: 'Điện thoại gập', icon: '📱', keyword: 'Fold' },
      { label: 'Gaming Phone', icon: '🎮', keyword: 'Gaming' },
      { label: 'Titan Tự Nhiên', icon: '💎', keyword: 'Titan' }
    ],
    groups: [
      {
        title: 'Apple (iPhone)',
        items: [
          { name: 'iPhone 16 Pro Max 256GB', link: '/products/1' },
          { name: 'iPhone 16 Pro 128GB' },
          { name: 'iPhone 16 Plus' },
          { name: 'iPhone 15 Pro Max' },
          { name: 'Phụ kiện sạc MagSafe Apple' }
        ]
      },
      {
        title: 'Samsung Galaxy',
        items: [
          { name: 'Galaxy S24 Ultra AI 256GB', link: '/products/2' },
          { name: 'Galaxy Z Fold6 512GB', link: '/products/10' },
          { name: 'Galaxy S24+ 5G' },
          { name: 'Galaxy Z Flip6' },
          { name: 'Galaxy AI Features' }
        ]
      },
      {
        title: 'Theo phân khúc giá',
        items: [
          { name: 'Dưới 15 triệu' },
          { name: 'Từ 15 - 25 triệu' },
          { name: 'Flagship trên 25 triệu' },
          { name: 'Bảo hành Apple Care+' }
        ]
      }
    ],
    promoBanner: {
      title: 'iPhone 16 Pro Max',
      subtitle: 'Khung viền Titan & chip Apple A18 Pro',
      image: 'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=500&auto=format&fit=crop&q=80',
      link: '/products/1',
      tag: 'Flagship Đỉnh Cao'
    }
  },
  {
    id: 1,
    name: 'Laptop & Máy tính',
    slug: 'laptop',
    iconSvg: `<svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="3" width="20" height="14" rx="2" ry="2"/><line x1="2" y1="20" x2="22" y2="20"/></svg>`,
    brands: [
      { id: 1, name: 'Apple', logoUrl: '/brands/apple.svg' },
      { id: 3, name: 'Dell', logoUrl: '/brands/dell.svg' },
      { id: 4, name: 'ASUS', logoUrl: '/brands/asus.svg' },
      { id: 6, name: 'NVIDIA', logoUrl: '/brands/nvidia.svg' }
    ],
    highlights: [
      { label: 'Apple Silicon M3', icon: '🍏', keyword: 'M3' },
      { label: 'Intel Core Ultra AI', icon: '⚡', keyword: 'Ultra' },
      { label: 'Gaming RTX 4070', icon: '🎮', keyword: 'RTX' },
      { label: 'OLED 240Hz', icon: '🖥️', keyword: 'OLED' },
      { label: 'Ultrabook nhẹ', icon: '🪶', keyword: 'Ultrabook' }
    ],
    groups: [
      {
        title: 'Apple MacBook',
        items: [
          { name: 'MacBook Pro 14 M3 Pro', link: '/products/3' },
          { name: 'MacBook Air M3 13 inch' },
          { name: 'MacBook Air M3 15 inch' },
          { name: 'MacBook Pro 16 M3 Max' }
        ]
      },
      {
        title: 'Dell & ASUS',
        items: [
          { name: 'Dell XPS 14 OLED 2024', link: '/products/4' },
          { name: 'ASUS ROG Zephyrus G16', link: '/products/5' },
          { name: 'ASUS Zenbook 14 OLED' },
          { name: 'Dell Alienware m16' }
        ]
      },
      {
        title: 'Nhu cầu sử dụng',
        items: [
          { name: 'Lập trình & Kỹ thuật phần mềm' },
          { name: 'Thiết kế đồ họa 3D & Render' },
          { name: 'Doanh nhân sang trọng mỏng nhẹ' },
          { name: 'Gaming đỉnh cao 240Hz' }
        ]
      }
    ],
    promoBanner: {
      title: 'MacBook Pro 14 M3 Pro',
      subtitle: 'Apple M3 Pro 11-Core, pin 18h liên tục',
      image: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=500&auto=format&fit=crop&q=80',
      link: '/products/3',
      tag: 'Bán chạy số 1'
    }
  },
  {
    id: 3,
    name: 'Máy tính bảng',
    slug: 'may-tinh-bang',
    iconSvg: `<svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="4" y="2" width="16" height="20" rx="2" ry="2"/><line x1="12" y1="18" x2="12.01" y2="18"/></svg>`,
    brands: [
      { id: 1, name: 'Apple iPad', logoUrl: '/brands/apple.svg' },
      { id: 2, name: 'Samsung Tab', logoUrl: '/brands/samsung.svg' },
      { id: 2, name: 'Xiaomi Pad', logoUrl: '/brands/xiaomi.svg' }
    ],
    highlights: [
      { label: 'Chip Apple M4', icon: '🚀', keyword: 'M4' },
      { label: 'Tandem OLED kép', icon: '🎨', keyword: 'OLED' },
      { label: 'Apple Pencil Pro', icon: '🖊️', keyword: 'Pencil' },
      { label: 'Bút S-Pen kèm máy', icon: '✍️', keyword: 'SPen' },
      { label: 'Kết nối 5G', icon: '📶', keyword: 'Cellular' }
    ],
    groups: [
      {
        title: 'Apple iPad',
        items: [
          { name: 'iPad Pro 11 M4 OLED', link: '/products/6' },
          { name: 'iPad Pro 13 M4 OLED' },
          { name: 'iPad Air M2 11 inch' },
          { name: 'iPad Mini 6' }
        ]
      },
      {
        title: 'Samsung Tab',
        items: [
          { name: 'Galaxy Tab S9 Ultra' },
          { name: 'Galaxy Tab S9 Plus' },
          { name: 'Galaxy Tab S9 FE' }
        ]
      },
      {
        title: 'Phụ kiện Tablet',
        items: [
          { name: 'Bút Apple Pencil Pro' },
          { name: 'Magic Keyboard cho iPad' },
          { name: 'Bao da Smart Folio chính hãng' }
        ]
      }
    ],
    promoBanner: {
      title: 'iPad Pro 11 M4 OLED',
      subtitle: 'Siêu mỏng 5.3mm, chip Apple M4 đột phá',
      image: 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=500&auto=format&fit=crop&q=80',
      link: '/products/6',
      tag: 'Siêu phẩm M4'
    }
  },
  {
    id: 4,
    name: 'Âm thanh & Tai nghe',
    slug: 'am-thanh',
    iconSvg: `<svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 1a9 9 0 0 0-9 9v7a3 3 0 0 0 3 3h1a2 2 0 0 0 2-2v-5a2 2 0 0 0-2-2H5v-1a7 7 0 0 1 14 0v1h-2a2 2 0 0 0-2 2v5a2 2 0 0 0 2 2h1a3 3 0 0 0 3-3v-7a9 9 0 0 0-9-9z"/></svg>`,
    brands: [
      { id: 5, name: 'Sony Audio', logoUrl: '/brands/sony.svg' },
      { id: 1, name: 'Apple AirPods', logoUrl: '/brands/apple.svg' },
      { id: 2, name: 'Samsung Galaxy Buds', logoUrl: '/brands/samsung.svg' }
    ],
    highlights: [
      { label: 'Chống ồn ANC', icon: '🎧', keyword: 'Chống ồn' },
      { label: 'Hi-Res LDAC', icon: '🎼', keyword: 'Hi-Res' },
      { label: 'Pin 30 giờ', icon: '🔋', keyword: 'Pin' },
      { label: 'Đàm thoại AI', icon: '🎙️', keyword: 'Mic' },
      { label: 'Studio Monitor', icon: '🔊', keyword: 'Studio' }
    ],
    groups: [
      {
        title: 'Tai nghe Chụp tai (Over-ear)',
        items: [
          { name: 'Sony WH-1000XM5 Wireless', link: '/products/7' },
          { name: 'Apple AirPods Max Space Gray' },
          { name: 'Sony WH-1000XM4' }
        ]
      },
      {
        title: 'Tai nghe In-ear (True Wireless)',
        items: [
          { name: 'Apple AirPods Pro 2 USB-C' },
          { name: 'Sony WF-1000XM5' },
          { name: 'Galaxy Buds3 Pro' }
        ]
      },
      {
        title: 'Loa & Thiết bị Studio',
        items: [
          { name: 'Loa Marshall Stanmore III' },
          { name: 'Loa di động Sony Extra Bass' },
          { name: 'DAC chuyển đổi âm thanh Hi-Res' }
        ]
      }
    ],
    promoBanner: {
      title: 'Sony WH-1000XM5',
      subtitle: 'Bộ xử lý kép V1+QN1, chống ồn tốt nhất thế giới',
      image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&auto=format&fit=crop&q=80',
      link: '/products/7',
      tag: 'Hi-Res Audio'
    }
  },
  {
    id: 5,
    name: 'Linh kiện & Phụ kiện',
    slug: 'linh-kien-phu-kien',
    iconSvg: `<svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="4" y="4" width="16" height="16" rx="2"/><rect x="9" y="9" width="6" height="6"/><line x1="9" y1="1" x2="9" y2="4"/><line x1="15" y1="1" x2="15" y2="4"/><line x1="9" y1="20" x2="9" y2="23"/><line x1="15" y1="20" x2="15" y2="23"/><line x1="20" y1="9" x2="23" y2="9"/><line x1="20" y1="14" x2="23" y2="14"/><line x1="1" y1="9" x2="4" y2="9"/><line x1="1" y1="14" x2="4" y2="14"/></svg>`,
    brands: [
      { id: 7, name: 'Logitech', logoUrl: '/brands/logitech.svg' },
      { id: 6, name: 'NVIDIA RTX', logoUrl: '/brands/nvidia.svg' },
      { id: 4, name: 'ASUS ROG', logoUrl: '/brands/asus.svg' },
      { id: 1, name: 'Apple', logoUrl: '/brands/apple.svg' }
    ],
    highlights: [
      { label: 'RTX 4090 OC', icon: '🎮', keyword: 'RTX 4090' },
      { label: 'MX Master 3S', icon: '🖱️', keyword: 'MX Master' },
      { label: 'Phím cơ Quiet', icon: '⌨️', keyword: 'Mechanical' },
      { label: 'Apple Watch', icon: '⌚', keyword: 'Watch' },
      { label: 'Sạc nhanh GaN', icon: '⚡', keyword: 'GaN' }
    ],
    groups: [
      {
        title: 'Ngoại vi Logitech Master',
        items: [
          { name: 'Chuột Logitech MX Master 3S', link: '/products/11' },
          { name: 'Bàn phím cơ MX Mechanical', link: '/products/12' },
          { name: 'Chuột MX Anywhere 3S' }
        ]
      },
      {
        title: 'Linh kiện Card đồ họa AI',
        items: [
          { name: 'ASUS ROG Strix RTX 4090 24GB', link: '/products/8' },
          { name: 'Card đồ họa NVIDIA RTX 4080 Super' },
          { name: 'Nguồn ROG Thor 1000W Platinum' }
        ]
      },
      {
        title: 'Smartwatch & Tiện ích',
        items: [
          { name: 'Apple Watch Ultra 2 GPS + Cellular', link: '/products/9' },
          { name: 'Củ sạc GaN 100W 3 cổng' },
          { name: 'Cáp Thunderbolt 4 Pro Apple' }
        ]
      }
    ],
    promoBanner: {
      title: 'ROG Strix RTX 4090 24GB',
      subtitle: 'Kiến trúc Ada Lovelace, 24GB GDDR6X',
      image: 'https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=500&auto=format&fit=crop&q=80',
      link: '/products/8',
      tag: 'Tối Thượng AI'
    }
  }
]

const currentCat = computed(() => {
  return categoryData.find(c => c.id === activeCatId.value) || categoryData[0]
})
</script>

<style scoped>
/* Backdrop Fade */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Mega Dropdown Scale & Slide Transition */
.mega-dropdown-enter-active,
.mega-dropdown-leave-active {
  transition: opacity 0.25s cubic-bezier(0.16, 1, 0.3, 1), transform 0.25s cubic-bezier(0.16, 1, 0.3, 1);
}
.mega-dropdown-enter-from,
.mega-dropdown-leave-to {
  opacity: 0;
  transform: translateY(-6px) scale(0.99);
}
</style>
