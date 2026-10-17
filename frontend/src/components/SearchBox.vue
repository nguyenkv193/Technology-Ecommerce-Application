<template>
  <div ref="containerRef" class="relative w-full">
    <!-- Search Bar Input Form -->
    <form @submit.prevent="submitSearch(searchKeyword)" class="relative flex items-center">
      <input
        ref="inputRef"
        v-model="searchKeyword"
        type="text"
        placeholder="Nhập tên điện thoại, laptop, phụ kiện... cần tìm"
        role="combobox"
        aria-autocomplete="list"
        aria-haspopup="true"
        :aria-expanded="isOpen"
        :aria-controls="isOpen ? 'search-dropdown-menu' : undefined"
        @focus="handleFocus"
        @click="handleFocus"
        @keydown="handleKeydown"
        class="w-full bg-white hover:bg-zinc-50/60 focus:bg-white border border-zinc-200 hover:border-zinc-300 focus:border-[#d70018] focus:ring-2 focus:ring-[#d70018]/15 rounded-full py-2 sm:py-2.5 pl-4 sm:pl-5 pr-11 text-xs sm:text-[13px] text-zinc-900 placeholder:text-zinc-400 transition-all outline-none shadow-xs"
      />

      <!-- Clear text icon (when typing) -->
      <button
        v-if="searchKeyword.trim().length > 0"
        type="button"
        @click="clearSearch"
        class="absolute right-10 p-1 text-zinc-400 hover:text-zinc-600 rounded-full hover:bg-zinc-100 transition cursor-pointer"
        title="Xóa tìm kiếm"
      >
        <X class="w-3.5 h-3.5" />
      </button>

      <!-- Search Submit Button with Red Magnifying Glass -->
      <button
        type="submit"
        class="absolute inset-y-1 right-1.5 w-7 h-7 sm:w-8 sm:h-8 rounded-full flex items-center justify-center text-[#d70018] hover:text-[#b80014] hover:bg-red-50 transition cursor-pointer"
        title="Tìm kiếm"
      >
        <Search class="w-4 h-4 stroke-[2.2]" />
      </button>
    </form>

    <!-- Backdrop Overlay for Focus Effect -->
    <Teleport to="body">
      <Transition name="fade">
        <div
          v-if="isOpen"
          @click="closeDropdown"
          class="fixed inset-0 bg-black/20 backdrop-blur-[1px] z-40 transition-opacity"
        ></div>
      </Transition>
    </Teleport>

    <!-- Dropdown Popup Menu -->
    <Transition name="dropdown-scale">
      <div
        v-if="isOpen"
        id="search-dropdown-menu"
        class="absolute left-1/2 -translate-x-1/2 top-full mt-2.5 z-50 w-[min(92vw,560px)] bg-white rounded-2xl border border-zinc-200 shadow-2xl p-4 sm:p-5 text-left select-none max-h-[85vh] overflow-y-auto"
      >
        <!-- ================= MODE 1: DEFAULT STATE (NO TEXT TYPED) ================= -->
        <div v-if="searchKeyword.trim().length === 0" class="space-y-4">
          <!-- 1. Top Loyalty Banner matching screenshot -->
          <div
            @click="showRewardModal = true"
            class="group relative overflow-hidden rounded-xl bg-gradient-to-r from-zinc-950 via-zinc-900 to-black px-3.5 py-2.5 sm:px-4 sm:py-2.5 text-white flex items-center justify-between border border-zinc-800/80 shadow-xs cursor-pointer transition hover:border-zinc-700"
          >
            <!-- Decorative coins/sparkles on left and background -->
            <div class="absolute left-1/3 top-0 bottom-0 w-32 bg-[radial-gradient(ellipse_at_center,_var(--tw-gradient-stops))] from-amber-500/15 via-transparent to-transparent pointer-events-none"></div>

            <!-- Left: Bold Red/Gold Gradient Title -->
            <div class="flex items-center gap-2.5 min-w-0 relative z-10">
              <span class="text-xs sm:text-sm font-extrabold uppercase tracking-wide text-transparent bg-clip-text bg-gradient-to-r from-red-500 via-amber-300 to-yellow-400 drop-shadow-xs">
                TÍCH ĐIỂM ĐỔI ƯU ĐÃI
              </span>
              <div class="hidden sm:flex items-center gap-1 opacity-90">
                <span class="text-xs">🪙</span>
                <span class="text-[11px] text-amber-300 font-semibold">Ưu đãi kép</span>
              </div>
            </div>

            <!-- Right: "Tìm hiểu ngay" Red Pill Button -->
            <button
              type="button"
              class="relative z-10 bg-[#d70018] hover:bg-[#b80014] text-white text-[11px] sm:text-xs font-bold px-3.5 sm:px-4 py-1.5 rounded-full transition shadow-xs active:scale-95 shrink-0 cursor-pointer"
            >
              Tìm hiểu ngay
            </button>
          </div>

          <!-- 2. Search History Section (Lịch sử tìm kiếm) -->
          <div v-if="searchHistory.length > 0" class="space-y-0.5">
            <div
              v-for="item in visibleHistory"
              :key="item"
              @mousedown.prevent
              @click="submitSearch(item)"
              class="flex items-center justify-between py-1.5 px-1 hover:bg-zinc-50/80 rounded-lg transition cursor-pointer group"
            >
              <!-- History Icon & Text -->
              <div class="flex items-center gap-3 min-w-0 flex-1">
                <History class="w-4 h-4 text-zinc-500 group-hover:text-[#d70018] shrink-0 transition" />
                <span class="text-[13px] text-zinc-800 group-hover:text-[#d70018] truncate transition font-normal">
                  {{ item }}
                </span>
              </div>

              <!-- Remove History Item 'X' -->
              <button
                type="button"
                @click.stop="removeHistoryItem(item)"
                class="p-1 text-zinc-400 hover:text-zinc-700 hover:bg-zinc-100 rounded-full transition cursor-pointer"
                title="Xóa khỏi lịch sử"
              >
                <X class="w-4 h-4" />
              </button>
            </div>

            <!-- "Xem thêm" / "Thu gọn" Button (Blue, centered, exactly as in screenshot) -->
            <div v-if="searchHistory.length > 2" class="pt-1 text-center">
              <button
                type="button"
                @mousedown.prevent
                @click="isHistoryExpanded = !isHistoryExpanded"
                class="text-xs sm:text-[13px] font-medium text-blue-600 hover:text-blue-700 hover:underline py-0.5 px-3 transition cursor-pointer"
              >
                {{ isHistoryExpanded ? 'Thu gọn' : 'Xem thêm' }}
              </button>
            </div>
          </div>

          <!-- 3. Trending Searches Section (Xu hướng tìm kiếm) -->
          <div class="pt-1">
            <h4 class="text-xs sm:text-sm font-bold text-zinc-900 mb-2.5">
              Xu hướng tìm kiếm
            </h4>
            <div class="flex flex-wrap gap-2 sm:gap-2.5">
              <button
                v-for="trend in trendingKeywords"
                :key="trend"
                type="button"
                @mousedown.prevent
                @click="submitSearch(trend)"
                class="flex items-center gap-1.5 px-3 sm:px-3.5 py-1.5 rounded-full border border-zinc-200 hover:border-zinc-300 hover:bg-zinc-50 text-xs sm:text-[13px] text-zinc-800 bg-white transition cursor-pointer shadow-2xs group active:scale-95"
              >
                <Search class="w-3.5 h-3.5 text-zinc-500 group-hover:text-[#d70018] transition" />
                <span class="group-hover:text-[#d70018] transition">{{ trend }}</span>
              </button>
            </div>
          </div>

          <!-- 4. Suggestions For You Section (Gợi ý cho bạn) -->
          <div class="pt-1">
            <div class="flex items-center justify-between mb-2.5">
              <h4 class="text-xs sm:text-sm font-bold text-zinc-900">
                Gợi ý cho bạn
              </h4>
              <button
                type="button"
                @mousedown.prevent
                @click="rotateSuggestions"
                class="text-xs sm:text-[13px] text-blue-600 hover:text-blue-700 font-medium flex items-center gap-1 cursor-pointer transition select-none active:scale-95 group"
                title="Đổi gợi ý khác"
              >
                <span>Gợi ý khác</span>
                <RotateCw
                  class="w-3.5 h-3.5 transition-transform duration-500"
                  :class="{ 'rotate-180': isRotating }"
                />
              </button>
            </div>

            <!-- 2-Column Grid of 4 Items -->
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-2.5 sm:gap-3">
              <div
                v-for="item in currentSuggestions"
                :key="item.id"
                @mousedown.prevent
                @click="handleSuggestionClick(item)"
                class="flex items-center gap-2.5 p-1 rounded-xl hover:bg-zinc-50 transition cursor-pointer group"
              >
                <!-- Thumbnail with clean fallback -->
                <div class="w-14 h-12 sm:w-16 sm:h-12 rounded-lg overflow-hidden bg-zinc-100 border border-zinc-100 shrink-0">
                  <img
                    :src="item.image"
                    :alt="item.title"
                    @error="handleImgFallback($event)"
                    class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-200"
                    loading="lazy"
                  />
                </div>

                <!-- 2-line title -->
                <p class="text-xs sm:text-[12.5px] text-zinc-800 group-hover:text-[#d70018] line-clamp-2 leading-[1.35] transition-colors flex-1 font-normal">
                  {{ item.title }}
                </p>
              </div>
            </div>
          </div>
        </div>

        <!-- ================= MODE 2: LIVE SEARCH AUTOCOMPLETE (USER TYPED TEXT) ================= -->
        <div v-else class="space-y-3">
          <!-- Matching History Queries (if any) -->
          <div v-if="matchingHistory.length > 0" class="border-b border-zinc-100 pb-2">
            <div class="px-2 pb-1 text-[11px] font-semibold text-zinc-400 uppercase tracking-wider">
              Lịch sử tìm kiếm
            </div>
            <div
              v-for="item in matchingHistory"
              :key="item"
              @mousedown.prevent
              @click="submitSearch(item)"
              class="flex items-center justify-between px-2.5 py-1.5 rounded-lg hover:bg-zinc-50 transition cursor-pointer group"
            >
              <div class="flex items-center gap-2.5 min-w-0">
                <History class="w-3.5 h-3.5 text-zinc-400 group-hover:text-zinc-600 shrink-0" />
                <span class="text-xs text-zinc-800 group-hover:text-[#d70018] truncate">
                  {{ item }}
                </span>
              </div>
              <button
                type="button"
                @click.stop="removeHistoryItem(item)"
                class="p-1 text-zinc-400 hover:text-zinc-700 rounded-full"
              >
                <X class="w-3 h-3" />
              </button>
            </div>
          </div>

          <!-- Loading State -->
          <div v-if="isLoadingSuggestions" class="py-6 text-center space-y-2">
            <div class="inline-block w-5 h-5 border-2 border-zinc-300 border-t-[#d70018] rounded-full animate-spin"></div>
            <p class="text-xs text-zinc-500">Đang tìm kiếm sản phẩm...</p>
          </div>

          <!-- Product Suggestions List -->
          <div v-else-if="productSuggestions.length > 0" class="space-y-1">
            <div class="px-2 pb-1 text-[11px] font-semibold text-zinc-400 uppercase tracking-wider">
              Sản phẩm gợi ý
            </div>
            <button
              v-for="(product, index) in productSuggestions"
              :key="product.id"
              type="button"
              @mousedown.prevent
              @click="selectProduct(product)"
              @mouseenter="activeSuggestionIndex = index"
              class="flex w-full items-center gap-3 rounded-xl px-2.5 py-2 text-left transition"
              :class="activeSuggestionIndex === index ? 'bg-zinc-100' : 'hover:bg-zinc-50'"
            >
              <img
                v-if="product.thumbnailUrl"
                :src="product.thumbnailUrl"
                :alt="product.name"
                class="h-11 w-11 shrink-0 rounded-lg border border-zinc-100 bg-white object-contain p-1"
                loading="lazy"
              />
              <div v-else class="flex h-11 w-11 shrink-0 items-center justify-center rounded-lg border border-zinc-100 bg-zinc-50 text-base">
                📦
              </div>
              <div class="min-w-0 flex-1">
                <div class="truncate text-xs font-semibold text-zinc-900 group-hover:text-[#d70018]">
                  <span v-html="highlightKeyword(product.name, searchKeyword)"></span>
                </div>
                <div class="mt-0.5 truncate text-[11px] text-zinc-500">
                  {{ [product.brandName, product.categoryName].filter(Boolean).join(' · ') }}
                </div>
              </div>
              <div class="shrink-0 text-right text-xs font-bold text-zinc-900">
                {{ formatPrice(product.minPrice) }}
              </div>
            </button>
          </div>

          <!-- Empty Suggestions State -->
          <div v-else class="py-6 text-center space-y-1">
            <p class="text-xs text-zinc-500">
              Không tìm thấy sản phẩm phù hợp cho "<span class="font-semibold text-zinc-700">{{ searchKeyword }}</span>"
            </p>
            <p class="text-[11px] text-zinc-400">
              Nhấn Enter hoặc nút bên dưới để tìm kiếm trong toàn bộ danh mục
            </p>
          </div>

          <!-- Bottom Action: View all results -->
          <button
            type="button"
            @mousedown.prevent
            @click="submitSearch(searchKeyword)"
            class="w-full border-t border-zinc-100 pt-3 pb-1 px-2 text-left text-xs font-semibold text-[#d70018] hover:text-[#b80014] hover:bg-red-50/40 rounded-lg transition flex items-center justify-between cursor-pointer"
          >
            <span>Xem tất cả kết quả cho “{{ searchKeyword.trim() }}”</span>
            <span class="text-xs font-normal text-zinc-400">↵ Enter</span>
          </button>
        </div>
      </div>
    </Transition>

    <!-- TechRewards Loyalty Program Info Modal -->
    <Teleport to="body">
      <Transition name="fade">
        <div
          v-if="showRewardModal"
          @click.self="showRewardModal = false"
          class="fixed inset-0 bg-black/50 backdrop-blur-sm z-[100] flex items-center justify-center p-4"
        >
          <div class="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-zinc-100 space-y-5 animate-in fade-in zoom-in-95 duration-200">
            <!-- Header -->
            <div class="flex items-center justify-between border-b border-zinc-100 pb-3">
              <div class="flex items-center gap-2.5">
                <div class="w-9 h-9 rounded-xl bg-amber-500/10 border border-amber-500/20 text-amber-600 flex items-center justify-center text-lg">
                  🪙
                </div>
                <div>
                  <h3 class="text-sm font-bold text-zinc-900">Tích Điểm Đổi Ưu Đãi</h3>
                  <p class="text-[11px] text-zinc-500">Chương trình TechRewards độc quyền</p>
                </div>
              </div>
              <button
                type="button"
                @click="showRewardModal = false"
                class="p-1.5 text-zinc-400 hover:text-zinc-700 rounded-lg hover:bg-zinc-100 transition cursor-pointer"
              >
                <X class="w-4 h-4" />
              </button>
            </div>

            <!-- Perks -->
            <div class="space-y-3">
              <div class="flex items-start gap-3 p-3 rounded-xl bg-zinc-50 border border-zinc-100">
                <span class="text-base mt-0.5">💰</span>
                <div>
                  <div class="text-xs font-bold text-zinc-900">Tích lũy 1% - 3% mỗi đơn hàng</div>
                  <div class="text-[11px] text-zinc-500 mt-0.5">Mỗi 1.000đ tích lũy = 1 điểm thưởng TechPoints dùng khi mua thiết bị tiếp theo.</div>
                </div>
              </div>

              <div class="flex items-start gap-3 p-3 rounded-xl bg-zinc-50 border border-zinc-100">
                <span class="text-base mt-0.5">🎁</span>
                <div>
                  <div class="text-xs font-bold text-zinc-900">Đổi Voucher giảm giá lên đến 500.000đ</div>
                  <div class="text-[11px] text-zinc-500 mt-0.5">Dễ dàng đổi voucher mua phụ kiện, điện thoại, laptop ngay tại trang thanh toán.</div>
                </div>
              </div>

              <div class="flex items-start gap-3 p-3 rounded-xl bg-zinc-50 border border-zinc-100">
                <span class="text-base mt-0.5">⭐</span>
                <div>
                  <div class="text-xs font-bold text-zinc-900">Đặc quyền Hội Viên VIP</div>
                  <div class="text-[11px] text-zinc-500 mt-0.5">Miễn phí giao hàng hỏa tốc, quà sinh nhật đặc biệt và ưu tiên bảo hành chính hãng.</div>
                </div>
              </div>
            </div>

            <!-- Actions -->
            <div class="pt-2 flex items-center justify-end gap-2">
              <button
                type="button"
                @click="showRewardModal = false"
                class="px-4 py-2 rounded-xl text-xs font-semibold text-zinc-700 hover:bg-zinc-100 transition cursor-pointer"
              >
                Đóng
              </button>
              <button
                type="button"
                @click="showRewardModal = false; submitSearch('ưu đãi')"
                class="px-4 py-2 rounded-xl text-xs font-bold bg-[#d70018] hover:bg-[#b80014] text-white shadow-xs transition cursor-pointer"
              >
                Khám phá ưu đãi ngay
              </button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, History, X, RotateCw } from 'lucide-vue-next'
import { catalogApi } from '@/api/catalogApi'
import { behaviorApi } from '@/api/behaviorApi'
import type { ProductSuggestion } from '@/types'

const router = useRouter()

const searchKeyword = ref<string>('')
const isOpen = ref<boolean>(false)
const inputRef = ref<HTMLInputElement | null>(null)
const containerRef = ref<HTMLElement | null>(null)
const isHistoryExpanded = ref<boolean>(false)
const showRewardModal = ref<boolean>(false)
const isRotating = ref<boolean>(false)

// Trending Keywords matching exactly the screenshot
const trendingKeywords = [
  'iPhone 18',
  'iPhone Duo',
  'Laptop',
  'Điện thoại Samsung',
  'Macbook',
  'Samsung Z Fold 8'
]

// Search History (persisted in localStorage, initialized with realistic items matching screenshot)
const STORAGE_KEY = 'techstore_search_history'
const searchHistory = ref<string[]>([])

const loadHistory = () => {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (raw) {
      const parsed = JSON.parse(raw)
      if (Array.isArray(parsed) && parsed.length >= 3) {
        searchHistory.value = parsed
      } else {
        searchHistory.value = ['iphone 18', 'samsung', 'Macbook Pro', 'Tai nghe Sony']
        localStorage.setItem(STORAGE_KEY, JSON.stringify(searchHistory.value))
      }
    } else {
      // Default to items matching screenshot with expandable list
      searchHistory.value = ['iphone 18', 'samsung', 'Macbook Pro', 'Tai nghe Sony']
      localStorage.setItem(STORAGE_KEY, JSON.stringify(searchHistory.value))
    }
  } catch {
    searchHistory.value = ['iphone 18', 'samsung', 'Macbook Pro', 'Tai nghe Sony']
  }
}

const saveHistory = () => {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(searchHistory.value))
  } catch {
    // ignore
  }
}

const addHistoryItem = (keyword: string) => {
  const clean = keyword.trim()
  if (!clean) return
  // Remove existing
  searchHistory.value = searchHistory.value.filter(
    (item) => item.toLowerCase() !== clean.toLowerCase()
  )
  // Prepend to top
  searchHistory.value.unshift(clean)
  // Max 10 items
  if (searchHistory.value.length > 10) {
    searchHistory.value = searchHistory.value.slice(0, 10)
  }
  saveHistory()
}

const removeHistoryItem = (itemToRemove: string) => {
  searchHistory.value = searchHistory.value.filter((item) => item !== itemToRemove)
  saveHistory()
}

// History visible count based on "Xem thêm" / "Thu gọn"
const visibleHistory = computed(() => {
  if (isHistoryExpanded.value) {
    return searchHistory.value
  }
  return searchHistory.value.slice(0, 2)
})

// Matching history items when user is typing
const matchingHistory = computed(() => {
  const query = searchKeyword.value.trim().toLowerCase()
  if (!query) return []
  return searchHistory.value.filter((item) =>
    item.toLowerCase().includes(query)
  ).slice(0, 3)
})

// Curated recommendation sets for "Gợi ý cho bạn"
interface SuggestionArticle {
  id: number
  title: string
  image: string
  keyword: string
}

const suggestionSets: SuggestionArticle[][] = [
  // Set 1: Exactly the 4 items from the user's screenshot
  [
    {
      id: 1,
      title: 'Google hé lộ OnePlus Ace 7 và Ace 7 Pro: Chip mạnh,...',
      image: 'https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=160&auto=format&fit=crop&q=80',
      keyword: 'OnePlus'
    },
    {
      id: 2,
      title: 'iPhone Duo ra mắt: iPhone gập đầu tiên của Apple với...',
      image: 'https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?w=160&auto=format&fit=crop&q=80',
      keyword: 'iPhone'
    },
    {
      id: 3,
      title: 'Đánh giá RedMagic 12 Pro Plus: Màn hình 185 Hz, pin...',
      image: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=160&auto=format&fit=crop&q=80',
      keyword: 'RedMagic'
    },
    {
      id: 4,
      title: 'REDMAGIC 12 Pro+ ra mắt: Chiến game hết nấc với ch...',
      image: 'https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=160&auto=format&fit=crop&q=80',
      keyword: 'Gaming Phone'
    }
  ],
  // Set 2: Flagship laptops & devices
  [
    {
      id: 5,
      title: 'MacBook Pro M4 Max: Đỉnh cao hiệu năng đồ họa và AI...',
      image: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=160&auto=format&fit=crop&q=80',
      keyword: 'Macbook'
    },
    {
      id: 6,
      title: 'Galaxy S25 Ultra có gì mới: Khung Titan, Snapdragon 8 Elite...',
      image: 'https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=160&auto=format&fit=crop&q=80',
      keyword: 'Samsung Galaxy'
    },
    {
      id: 7,
      title: 'Top 5 Laptop gaming đáng mua nhất phân khúc 20 - 30 triệu...',
      image: 'https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=160&auto=format&fit=crop&q=80',
      keyword: 'Laptop gaming'
    },
    {
      id: 8,
      title: 'iPad Air M3: Thiết kế mỏng nhẹ, màn hình 120Hz mượt mà...',
      image: 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=160&auto=format&fit=crop&q=80',
      keyword: 'iPad'
    }
  ],
  // Set 3: Accessories & Tech innovations
  [
    {
      id: 9,
      title: 'Tai nghe chống ồn Sony WH-1000XM6 rò rỉ thiết kế mới...',
      image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=160&auto=format&fit=crop&q=80',
      keyword: 'Tai nghe Sony'
    },
    {
      id: 10,
      title: 'Xiaomi 15 Ultra lộ diện ống kính tiềm vọng 200MP siêu khủng...',
      image: 'https://images.unsplash.com/photo-1580910051074-3eb694886505?w=160&auto=format&fit=crop&q=80',
      keyword: 'Xiaomi'
    },
    {
      id: 11,
      title: 'So sánh iPhone 16 Pro Max và Galaxy S24 Ultra chi tiết...',
      image: 'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=160&auto=format&fit=crop&q=80',
      keyword: 'iPhone 16'
    },
    {
      id: 12,
      title: 'Bàn phím cơ không dây tốt nhất cho dân lập trình 2026...',
      image: 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=160&auto=format&fit=crop&q=80',
      keyword: 'Bàn phím cơ'
    }
  ]
]

const currentSetIndex = ref(0)
const currentSuggestions = computed(() => suggestionSets[currentSetIndex.value])

const rotateSuggestions = () => {
  isRotating.value = true
  currentSetIndex.value = (currentSetIndex.value + 1) % suggestionSets.length
  setTimeout(() => {
    isRotating.value = false
  }, 500)
}

const handleSuggestionClick = (item: SuggestionArticle) => {
  submitSearch(item.keyword)
}

const handleImgFallback = (e: Event) => {
  const target = e.target as HTMLImageElement
  target.src = '/categories/phone.png'
}

// Live Autocomplete Suggestions
const productSuggestions = ref<ProductSuggestion[]>([])
const isLoadingSuggestions = ref<boolean>(false)
const activeSuggestionIndex = ref<number>(-1)
let debounceTimer: ReturnType<typeof setTimeout> | undefined
let latestRequestId = 0

const fetchLiveSuggestions = (query: string) => {
  if (debounceTimer) clearTimeout(debounceTimer)
  const reqId = ++latestRequestId

  isLoadingSuggestions.value = true
  activeSuggestionIndex.value = -1

  debounceTimer = setTimeout(async () => {
    try {
      const response = await catalogApi.getSearchSuggestions(query, 6)
      if (reqId !== latestRequestId) return
      productSuggestions.value = response.data ?? []
    } catch {
      if (reqId !== latestRequestId) return
      productSuggestions.value = []
    } finally {
      if (reqId === latestRequestId) {
        isLoadingSuggestions.value = false
      }
    }
  }, 220)
}

watch(searchKeyword, (val) => {
  const query = val.trim()
  if (query.length >= 1) {
    fetchLiveSuggestions(query)
  } else {
    productSuggestions.value = []
    isLoadingSuggestions.value = false
  }
})

const handleFocus = () => {
  isOpen.value = true
}

const closeDropdown = () => {
  isOpen.value = false
  activeSuggestionIndex.value = -1
}

const clearSearch = () => {
  searchKeyword.value = ''
  productSuggestions.value = []
  inputRef.value?.focus()
}

const submitSearch = (keyword: string) => {
  const clean = keyword.trim()
  if (!clean) return

  addHistoryItem(clean)
  behaviorApi.track('SEARCH', null, clean)
  closeDropdown()
  searchKeyword.value = clean
  router.push({ path: '/products', query: { keyword: clean } })
}

const selectProduct = (product: ProductSuggestion) => {
  addHistoryItem(product.name)
  behaviorApi.track('SEARCH', null, product.name)
  closeDropdown()
  router.push(`/products/${product.id}`)
}

const formatPrice = (value?: number) => {
  if (value == null) return 'Xem giá'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}

const highlightKeyword = (text: string, keyword: string) => {
  if (!keyword.trim()) return text
  const escaped = keyword.trim().replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
  const regex = new RegExp(`(${escaped})`, 'gi')
  return text.replace(regex, '<mark class="bg-amber-100 text-zinc-900 font-bold rounded-xs px-0.5">$1</mark>')
}

const handleKeydown = (event: KeyboardEvent) => {
  if (!isOpen.value) {
    if (event.key === 'ArrowDown') {
      isOpen.value = true
    }
    return
  }

  if (event.key === 'Escape') {
    event.preventDefault()
    closeDropdown()
  } else if (event.key === 'ArrowDown') {
    if (productSuggestions.value.length > 0) {
      event.preventDefault()
      activeSuggestionIndex.value = (activeSuggestionIndex.value + 1) % productSuggestions.value.length
    }
  } else if (event.key === 'ArrowUp') {
    if (productSuggestions.value.length > 0) {
      event.preventDefault()
      activeSuggestionIndex.value =
        activeSuggestionIndex.value <= 0
          ? productSuggestions.value.length - 1
          : activeSuggestionIndex.value - 1
    }
  } else if (event.key === 'Enter') {
    if (activeSuggestionIndex.value >= 0 && productSuggestions.value[activeSuggestionIndex.value]) {
      event.preventDefault()
      selectProduct(productSuggestions.value[activeSuggestionIndex.value])
    }
  }
}

// Click outside handler
const handleClickOutside = (event: MouseEvent) => {
  if (
    containerRef.value &&
    !containerRef.value.contains(event.target as Node) &&
    !(event.target as HTMLElement)?.closest('#search-dropdown-menu')
  ) {
    closeDropdown()
  }
}

onMounted(() => {
  loadHistory()
  document.addEventListener('mousedown', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('mousedown', handleClickOutside)
  if (debounceTimer) clearTimeout(debounceTimer)
})
</script>

<style scoped>
.dropdown-scale-enter-active,
.dropdown-scale-leave-active {
  transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
}
.dropdown-scale-enter-from,
.dropdown-scale-leave-to {
  opacity: 0;
  transform: translate(-50%, -8px) scale(0.97);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
