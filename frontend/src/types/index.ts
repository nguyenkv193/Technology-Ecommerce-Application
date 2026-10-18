// =========================================================
// TYPESCRIPT DOMAIN INTERFACES
// Hệ thống: TechStore E-Commerce & AI Recommendation
// =========================================================

export interface ApiResponse<T> {
  success: boolean
  code?: string
  message: string
  data: T
  timestamp: string
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  last: boolean
}

export interface User {
  id: number
  email: string
  fullName: string
  phoneNumber?: string
  role: 'USER' | 'ADMIN' | 'STAFF'
  status: 'ACTIVE' | 'INACTIVE' | 'BLOCKED'
  createdAt?: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest extends LoginRequest {
  fullName: string
  phoneNumber?: string
}

export interface Category {
  id: number
  name: string
  slug: string
  description?: string
  imageUrl?: string
  parentId?: number
  children?: Category[]
}

export interface Brand {
  id: number
  name: string
  slug: string
  description?: string
  logoUrl?: string
  websiteUrl?: string
}

export interface ProductAttribute {
  id?: number
  name: string
  value: string
}

export interface ProductVariant {
  id: number
  sku: string
  name: string
  price: number
  originalPrice?: number
  colorName?: string
  colorHex?: string
  rewardPoints?: number
  stock: number
  status: string
}

export interface ProductStorySection {
  heading?: string
  body?: string
  imageUrl?: string
  imageCaption?: string
}

export interface ProductOffer {
  id?: string
  title: string
  description?: string
  partnerName?: string
  terms?: string
  startsAt?: string
  endsAt?: string
  quota?: number
  claimed?: number
  discountAmount?: number
}

export interface ProductBundleOffer {
  id?: string
  title: string
  description?: string
  originalPrice?: number
  price?: number
  startsAt?: string
  endsAt?: string
}

export interface ProductFaq {
  question: string
  answer: string
}

export interface ProductCityAvailability {
  cityCode: string
  cityName: string
  storeCount?: number
  stockCount?: number
  deliveryEstimate?: string
}

export interface ProductInstallmentPlan {
  providerName: string
  months: number
  interestRatePercent?: number
  monthlyPayment?: number
  downPayment?: number
  terms?: string
  startsAt?: string
  endsAt?: string
}

export interface ProductTradeInOffer {
  deviceModel: string
  maximumSubsidy?: number
  minimumTopUp?: number
  maximumTopUp?: number
  condition?: string
  terms?: string
  startsAt?: string
  endsAt?: string
}

export interface ProductStorefrontContent {
  storySections: ProductStorySection[]
  media: {
    reviewVideoUrl?: string
    unboxingVideoUrl?: string
    unboxingDescription?: string
    view360Url?: string
  }
  promotions: ProductOffer[]
  paymentOffers: ProductOffer[]
  bundleOffers: ProductBundleOffer[]
  faqs: ProductFaq[]
  cityAvailability: ProductCityAvailability[]
  installmentPlans: ProductInstallmentPlan[]
  tradeInOffers: ProductTradeInOffer[]
  storePolicy: {
    officialProductText?: string
    warrantyText?: string
    exchangeText?: string
    shippingText?: string
    hotline?: string
    hotlineHours?: string
  }
  comparisonProductIds: number[]
}

export interface ProductImage {
  id?: number
  url: string
  sortOrder?: number
  isThumbnail?: boolean
}

export interface Product {
  id: number
  name: string
  slug: string
  description?: string
  categoryId?: number
  categoryName?: string
  categorySlug?: string
  brandId?: number
  brandName?: string
  thumbnailUrl?: string
  minPrice?: number
  maxPrice?: number
  totalStock?: number
  status?: string
  attributes?: ProductAttribute[]
  variants?: ProductVariant[]
  images?: ProductImage[]
  category?: Category
  brand?: Brand
  reason?: string
  aiScore?: number
}

export interface ProductSuggestion {
  id: number
  name: string
  slug: string
  brandName?: string
  categoryName?: string
  thumbnailUrl?: string
  minPrice?: number
}

export interface CartItem {
  id: number
  variantId: number
  productId: number
  productName: string
  productSlug: string
  variantName: string
  sku: string
  thumbnailUrl?: string
  price: number
  stock: number
  quantity: number
  subTotal: number
}

export interface Cart {
  id: number
  userId: number
  items: CartItem[]
  totalItems: number
  totalPrice: number
}

export interface OrderItem {
  id: number
  productId: number
  variantId: number
  productName: string
  variantName: string
  sku: string
  thumbnailUrl?: string
  price: number
  quantity: number
  totalPrice: number
}

export interface Order {
  id: number
  orderCode: string
  userId: number
  recipientName: string
  phoneNumber: string
  shippingAddress: string
  note?: string
  totalAmount: number
  shippingFee: number
  discountAmount: number
  finalAmount: number
  orderStatus: 'PENDING' | 'CONFIRMED' | 'SHIPPING' | 'DELIVERED' | 'CANCELLED'
  paymentMethod: 'COD' | 'VNPAY' | 'MOMO' | 'BANK_TRANSFER'
  paymentStatus: 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED'
  items: OrderItem[]
  createdAt: string
  updatedAt?: string
}

export interface ProductReview {
  id: number
  userId: number
  userFullName: string
  productId: number
  rating: number
  comment: string
  createdAt: string
}

export interface ProductReviewSummary {
  productId: number
  averageRating: number
  totalReviews: number
  ratingBreakdown?: Array<{ stars: number; count: number }>
  reviews: PageResponse<ProductReview>
}

// Recommendation data is enriched by the backend, including fallback provenance.
export type RecommendationSource = 'hybrid' | 'collaborative' | 'content_based' | 'best_seller' | 'catalog'

export interface RecommendationItem {
  product: Product
  source: RecommendationSource
  score: number | null
  reason: string
}

export interface RecommendationResponse {
  source: RecommendationSource
  fallbackReason: 'no_history' | 'ai_unavailable' | 'insufficient_matches' | null
  recommendations: RecommendationItem[]
}
