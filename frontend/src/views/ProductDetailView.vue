<template>
  <div v-if="loading" class="py-20 text-center text-zinc-400 animate-pulse text-xs">
    Đang tải thông tin sản phẩm...
  </div>

  <div v-else-if="!product" class="py-20 text-center text-zinc-400 text-xs">
    Không tìm thấy thông tin sản phẩm.
  </div>

  <div v-else class="space-y-12 sm:space-y-16">
    <!-- Breadcrumb -->
    <nav class="flex items-center gap-2 text-xs text-zinc-400">
      <router-link to="/" class="hover:text-zinc-800 transition">Trang chủ</router-link>
      <span>/</span>
      <router-link to="/products" class="hover:text-zinc-800 transition">Sản phẩm</router-link>
      <span>/</span>
      <span class="text-zinc-800 font-medium truncate">{{ product.name }}</span>
    </nav>

    <!-- Main Detail Section (Image + Buy Box) -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-10 items-start">
      <!-- Image Gallery -->
      <div class="space-y-4">
        <div class="bg-white border border-zinc-200/80 rounded-3xl p-8 aspect-square flex items-center justify-center relative overflow-hidden shadow-xs">
          <img 
            :src="activeImage || defaultImage" 
            :alt="product.name"
            class="object-contain max-h-full max-w-full transition-transform duration-300 hover:scale-105"
          />
          <button 
            @click="handleToggleWishlist"
            class="absolute top-4 right-4 p-2.5 rounded-full bg-white/90 border border-zinc-200 text-zinc-400 hover:text-red-500 shadow-2xs transition"
            :class="{ 'text-red-500': isFav }"
          >
            <svg class="w-4 h-4" :fill="isFav ? 'currentColor' : 'none'" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" /></svg>
          </button>
        </div>

        <!-- Thumbnails -->
        <div v-if="product.images && product.images.length > 1" class="flex gap-2.5 overflow-x-auto pb-1">
          <button 
            v-for="(img, idx) in product.images" 
            :key="idx"
            @click="activeImage = img.url"
            class="w-16 h-16 rounded-xl bg-white border p-1.5 shrink-0 transition"
            :class="activeImage === img.url ? 'border-zinc-900 ring-1 ring-zinc-900' : 'border-zinc-200 hover:border-zinc-300'"
          >
            <img :src="img.url" class="object-contain w-full h-full" />
          </button>
        </div>
      </div>

      <!-- Buy Box & Configuration -->
      <div class="space-y-6">
        <div>
          <div class="flex items-center gap-2 mb-2">
            <span class="text-[11px] font-semibold uppercase tracking-wider px-2 py-0.5 rounded-md bg-zinc-100 text-zinc-700 border border-zinc-200/80">
              {{ product.brand?.name || 'Chính hãng' }}
            </span>
            <span class="text-xs text-zinc-400">{{ product.category?.name }}</span>
          </div>

          <h1 class="text-2xl sm:text-3xl font-bold text-zinc-900 leading-tight">
            {{ product.name }}
          </h1>

          <!-- Reviews Rating Stars Header -->
          <div class="flex items-center gap-2 mt-2.5">
            <div class="flex items-center text-amber-400 text-xs">
              <span v-for="s in 5" :key="s">★</span>
            </div>
            <span class="text-xs font-semibold text-zinc-800">{{ reviewSummary.averageRating || '5.0' }}</span>
            <span class="text-xs text-zinc-400">({{ reviewSummary.totalReviews || 0 }} đánh giá)</span>
          </div>
        </div>

        <!-- Active Price Display -->
        <div class="p-4 rounded-2xl bg-zinc-50 border border-zinc-200/80 space-y-1">
          <div class="text-[11px] text-zinc-400">Giá niêm yết:</div>
          <div class="flex items-baseline gap-3">
            <span class="text-2xl sm:text-3xl font-black text-zinc-900">
              {{ formatPrice(selectedVariant?.price || product.minPrice) }}
            </span>
            <span v-if="selectedVariant?.originalPrice" class="text-xs text-zinc-400 line-through">
              {{ formatPrice(selectedVariant.originalPrice) }}
            </span>
          </div>
          <div class="pt-1 text-xs">
            <span v-if="currentStock > 0" class="inline-flex items-center gap-1.5 text-emerald-700 font-medium bg-emerald-50 px-2 py-0.5 rounded-md border border-emerald-200/60">
              <span class="w-1.5 h-1.5 rounded-full bg-emerald-600"></span>
              <span>Còn {{ currentStock }} sản phẩm có sẵn</span>
            </span>
            <span v-else class="inline-flex items-center gap-1.5 text-zinc-500 font-medium bg-zinc-100 px-2 py-0.5 rounded-md border border-zinc-200">
              <span>Tạm thời hết hàng</span>
            </span>
          </div>
        </div>

        <!-- Variant Selector (SKU) -->
        <div v-if="product.variants && product.variants.length > 0" class="space-y-2.5">
          <label class="block text-xs font-semibold text-zinc-700 uppercase tracking-wider">
            Phiên bản cấu hình:
          </label>
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-2.5">
            <button 
              v-for="v in product.variants" 
              :key="v.id"
              @click="selectedVariant = v"
              class="p-3 rounded-xl border text-left transition flex flex-col justify-between"
              :class="selectedVariant?.id === v.id ? 'bg-zinc-50 border-zinc-900 ring-1 ring-zinc-900 shadow-2xs' : 'bg-white border-zinc-200 hover:border-zinc-300'"
            >
              <div class="font-medium text-xs text-zinc-800">{{ v.name }}</div>
              <div class="text-xs font-bold text-zinc-900 mt-1">{{ formatPrice(v.price) }}</div>
            </button>
          </div>
        </div>

        <!-- Quantity & Add to Cart -->
        <div class="space-y-3 pt-3 border-t border-zinc-200/80">
          <div class="flex items-center gap-3">
            <div class="flex items-center bg-white border border-zinc-200 rounded-xl p-1 shadow-2xs">
              <button 
                @click="quantity = Math.max(1, quantity - 1)" 
                class="w-8 h-8 rounded-lg text-zinc-600 hover:bg-zinc-100 flex items-center justify-center font-bold text-xs"
              >
                -
              </button>
              <span class="w-10 text-center text-xs font-bold text-zinc-900">{{ quantity }}</span>
              <button 
                @click="quantity = Math.min(currentStock, quantity + 1)" 
                class="w-8 h-8 rounded-lg text-zinc-600 hover:bg-zinc-100 flex items-center justify-center font-bold text-xs"
              >
                +
              </button>
            </div>

            <button 
              @click="handleAddToCart"
              :disabled="currentStock <= 0 || addingToCart"
              class="flex-1 py-3 px-6 rounded-xl font-medium text-xs sm:text-sm bg-zinc-900 hover:bg-zinc-800 text-white shadow-xs transition-all active:scale-98 disabled:opacity-40 disabled:pointer-events-none flex items-center justify-center gap-2"
            >
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 11V7a4 4 0 0 0-8 0v4M5 9h14l1 12H4L5 9z"/></svg>
              <span>{{ addingToCart ? 'Đang thêm...' : 'Thêm vào giỏ hàng' }}</span>
            </button>
          </div>

          <div v-if="toastMessage" class="p-2.5 bg-emerald-50 border border-emerald-200/80 text-emerald-800 rounded-xl text-xs font-medium text-center">
            {{ toastMessage }}
          </div>
        </div>
      </div>
    </div>

    <!-- Hardware Specs Table Component -->
    <HardwareSpecsTable :attributes="product.attributes" />

    <!-- Similar Products -->
    <RecommendationCarousel
      title="Sản Phẩm Tương Tự"
      subtitle="Các thiết bị có cùng phân khúc và đặc tính kỹ thuật phù hợp"
      :items="similarProducts"
      :loading="loadingSimilar"
    />

    <!-- Reviews Section -->
    <section class="bg-white border border-zinc-200/80 rounded-3xl p-6 sm:p-8 space-y-6 shadow-xs">
      <div class="flex items-center justify-between pb-4 border-b border-zinc-100">
        <div>
          <h3 class="text-lg font-bold text-zinc-900">Đánh giá & Nhận xét</h3>
          <p class="text-xs text-zinc-500 mt-0.5">Nhận xét thực tế từ những khách hàng đã mua và sử dụng sản phẩm</p>
        </div>
        <div class="text-xl font-bold text-zinc-900 flex items-center gap-1">
          <span class="text-amber-400">★</span>
          <span>{{ reviewSummary.averageRating || '5.0' }}</span>
        </div>
      </div>

      <!-- Add Review Form -->
      <form @submit.prevent="handleSubmitReview" class="p-5 bg-zinc-50 rounded-2xl border border-zinc-200/80 space-y-3">
        <h4 class="text-xs font-semibold text-zinc-800">Gửi đánh giá trải nghiệm thiết bị</h4>
        
        <div class="flex items-center gap-2.5">
          <span class="text-xs text-zinc-500">Chấm điểm:</span>
          <div class="flex gap-1">
            <button 
              type="button" 
              v-for="star in 5" 
              :key="star"
              @click="newReview.rating = star"
              class="text-lg transition"
              :class="star <= newReview.rating ? 'text-amber-400 scale-110' : 'text-zinc-300 hover:text-zinc-400'"
            >
              ★
            </button>
          </div>
        </div>

        <div>
          <textarea 
            v-model="newReview.comment"
            rows="3"
            required
            placeholder="Chia sẻ nhận xét về hiệu năng, thời lượng pin, độ hoàn thiện..."
            class="w-full bg-white border border-zinc-200 rounded-xl p-3 text-xs text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-800 transition"
          ></textarea>
        </div>

        <div class="flex justify-end">
          <button 
            type="submit"
            :disabled="submittingReview"
            class="px-4 py-2 rounded-xl text-xs font-medium bg-zinc-900 hover:bg-zinc-800 text-white transition disabled:opacity-40"
          >
            {{ submittingReview ? 'Đang gửi...' : 'Gửi nhận xét' }}
          </button>
        </div>
      </form>

      <!-- Review List -->
      <div v-if="reviews.length === 0" class="text-center py-6 text-zinc-400 text-xs">
        Chưa có đánh giá nào cho sản phẩm này.
      </div>

      <div v-else class="space-y-3">
        <div 
          v-for="rev in reviews" 
          :key="rev.id"
          class="p-4 rounded-xl bg-zinc-50/60 border border-zinc-200/60 space-y-1.5"
        >
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-2">
              <div class="w-6 h-6 rounded-full bg-zinc-800 text-white font-bold text-[10px] flex items-center justify-center">
                {{ rev.userFullName?.charAt(0) || 'U' }}
              </div>
              <span class="text-xs font-semibold text-zinc-900">{{ rev.userFullName }}</span>
            </div>
            <div class="text-amber-400 text-xs">
              <span v-for="s in rev.rating" :key="s">★</span>
            </div>
          </div>
          <p class="text-xs text-zinc-600 leading-relaxed">{{ rev.comment }}</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import RecommendationCarousel from '@/components/RecommendationCarousel.vue'
import HardwareSpecsTable from '@/components/HardwareSpecsTable.vue'
import { catalogApi } from '@/api/catalogApi'
import { recommendationApi } from '@/api/recommendationApi'
import { behaviorApi } from '@/api/behaviorApi'
import { useCartStore } from '@/stores/cart'
import { useWishlistStore } from '@/stores/wishlist'
import { useAuthStore } from '@/stores/auth'
import type { Product, ProductVariant, ProductReview, ProductReviewSummary } from '@/types'

const route = useRoute()
const cartStore = useCartStore()
const wishlistStore = useWishlistStore()
const authStore = useAuthStore()

type EnrichedProduct = Product & { reason?: string; aiScore?: number }

const product = ref<Product | null>(null)
const selectedVariant = ref<ProductVariant | null>(null)
const activeImage = ref<string>('')
const quantity = ref<number>(1)
const loading = ref<boolean>(true)
const addingToCart = ref<boolean>(false)
const toastMessage = ref<string>('')

const similarProducts = ref<EnrichedProduct[]>([])
const loadingSimilar = ref<boolean>(false)

const reviews = ref<ProductReview[]>([])
const reviewSummary = ref<ProductReviewSummary>({
  productId: 0, averageRating: 0, totalReviews: 0,
  reviews: { content: [], page: 0, size: 10, totalElements: 0, totalPages: 0, last: true }
})
const newReview = ref<{ rating: number; comment: string }>({ rating: 5, comment: '' })
const submittingReview = ref<boolean>(false)

const defaultImage = 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500&auto=format&fit=crop&q=60'

const isFav = computed(() => product.value ? wishlistStore.isWishlisted(product.value.id) : false)
const currentStock = computed(() => selectedVariant.value?.stock ?? 0)

const formatPrice = (value?: number | null): string => {
  if (!value || isNaN(value)) return 'Liên hệ'
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
}

const handleToggleWishlist = async () => {
  if (product.value) {
    await wishlistStore.toggleWishlist(product.value)
  }
}

const handleAddToCart = async () => {
  if (!selectedVariant.value || !product.value) return
  addingToCart.value = true
  try {
    await cartStore.addToCart(selectedVariant.value.id, quantity.value, product.value.id)
    toastMessage.value = `Đã thêm ${quantity.value} sản phẩm vào giỏ hàng thành công!`
    setTimeout(() => { toastMessage.value = '' }, 3000)
  } catch (err: any) {
    toastMessage.value = err.message
  } finally {
    addingToCart.value = false
  }
}

const loadProductData = async (id: number | string) => {
  loading.value = true
  try {
    const res = await catalogApi.getProductById(Number(id))
    if (res.success && res.data) {
      product.value = res.data
      if (product.value.variants && product.value.variants.length > 0) {
        selectedVariant.value = product.value.variants[0]
      }
      if (product.value.images && product.value.images.length > 0) {
        activeImage.value = product.value.images[0].url
      }

      behaviorApi.track('VIEW', product.value.id)
      loadSimilarProducts(product.value.id)
      loadReviews(product.value.id)
    }
  } catch (err) {
    console.error('Không thể tải chi tiết sản phẩm:', err)
  } finally {
    loading.value = false
  }
}

let similarRequestId = 0
const loadSimilarProducts = async (productId: number) => {
  const requestId = ++similarRequestId
  loadingSimilar.value = true
  similarProducts.value = []
  try {
    const res = await recommendationApi.getSimilar(productId, 4)
    if (requestId !== similarRequestId || product.value?.id !== productId) return
    similarProducts.value = res.data.recommendations.map(item => ({
      ...item.product, reason: item.reason, aiScore: item.score ?? undefined
    }))
  } catch (error) {
    console.error('Lỗi tải sản phẩm tương tự:', error)
  } finally {
    if (requestId === similarRequestId) loadingSimilar.value = false
  }
}

const loadReviews = async (productId: number) => {
  try {
    const res = await catalogApi.getReviewSummary(productId)
    if (res.success && res.data) {
      reviewSummary.value = res.data
      reviews.value = res.data.reviews?.content || []
    }
  } catch (err) {
    console.error('Lỗi tải đánh giá:', err)
  }
}

const handleSubmitReview = async () => {
  if (!authStore.isAuthenticated) {
    authStore.openLoginModal()
    return
  }
  if (!product.value) return

  submittingReview.value = true
  try {
    await catalogApi.submitReview({
      productId: product.value.id,
      rating: newReview.value.rating,
      comment: newReview.value.comment
    })
    behaviorApi.track('RATING', product.value.id, `stars=${newReview.value.rating}`)
    newReview.value.comment = ''
    loadReviews(product.value.id)
  } catch (err: any) {
    alert(err.message)
  } finally {
    submittingReview.value = false
  }
}

watch(() => route.params.id, (newId) => {
  if (newId) loadProductData(newId as string)
})

onMounted(() => {
  if (route.params.id) {
    loadProductData(route.params.id as string)
  }
})
</script>
