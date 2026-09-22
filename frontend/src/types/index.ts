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

// AI Recommendation Interfaces
export interface AIRecommendationItem {
  product_id: number
  score: number
  reason: string
}

export interface AIRecommendationResponse {
  algorithm: string
  user_id?: number
  target_product_id?: number
  total: number
  recommendations: AIRecommendationItem[]
}

export interface AIEvaluationResponse {
  algorithm: string
  k: number
  precision_at_k: number
  recall_at_k: number
  ndcg_at_k: number
  hit_rate: number
  total_users_evaluated: number
}

export interface UserClusterItem {
  user_id: number
  cluster_id: number
  persona_name: string
  avg_score: number
  total_interactions: number
}

export interface ClusterSummary {
  cluster_id: number
  persona_name: string
  member_count: number
  avg_total_score: number
  avg_interactions: number
}

export interface AIClusterAnalysisResponse {
  total_clusters: number
  clusters: ClusterSummary[]
  user_assignments: UserClusterItem[]
}
