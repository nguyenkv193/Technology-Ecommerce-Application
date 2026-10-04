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
  stock: number
  status: string
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
  reviews: PageResponse<ProductReview>
}

// Recommendation data is enriched by the backend, including fallback provenance.
export type RecommendationSource = 'content_based' | 'best_seller' | 'catalog'

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
