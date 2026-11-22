// Domain Types for TechStore Backoffice Admin Portal

export interface ApiResponse<T> {
  success: boolean
  code?: string
  message: string
  data: T
  timestamp?: string
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  last: boolean
}

export type Role = 'USER' | 'ADMIN' | 'STAFF'

export interface User {
  id: number
  email: string
  fullName: string
  phoneNumber?: string
  role: Role
  status?: 'ACTIVE' | 'INACTIVE' | 'BLOCKED'
  avatarUrl?: string
  createdAt?: string
}

export interface AuthLoginResponse {
  accessToken: string
  tokenType: string
  user: User
}

export interface Category {
  id: number
  name: string
  slug: string
  description?: string
  parentId?: number | null
  iconUrl?: string
  displayOrder?: number
  productCount?: number
  children?: Category[]
}

export interface Brand {
  id: number
  name: string
  slug: string
  logoUrl?: string
  description?: string
  originCountry?: string
  productCount?: number
}

export interface ProductAttribute {
  id?: number
  name: string
  value: string
}

export interface ProductImage {
  id?: number
  url: string
  sortOrder?: number
  isThumbnail?: boolean
}

export interface ProductVariant {
  id?: number
  sku?: string
  name: string
  price: number
  originalPrice?: number
  colorName?: string
  colorHex?: string
  rewardPoints?: number
  stock: number
  status?: string
}

export type ProductStatus = 'ACTIVE' | 'INACTIVE' | 'DRAFT' | 'OUT_OF_STOCK'

export interface Product {
  id: number
  name: string
  slug: string
  description?: string
  status?: ProductStatus
  categoryId: number
  categoryName?: string
  categorySlug?: string
  brandId: number
  brandName?: string
  thumbnailUrl?: string
  minPrice?: number
  maxPrice?: number
  totalStock?: number
  createdAt?: string
  updatedAt?: string
  attributes?: ProductAttribute[]
  variants?: ProductVariant[]
  images?: ProductImage[]
}

export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'SHIPPING' | 'DELIVERED' | 'CANCELLED'

export interface OrderItem {
  id: number
  variantId?: number
  productId?: number
  productName: string
  variantName?: string
  sku?: string
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
  orderStatus: OrderStatus
  paymentMethod: string
  paymentStatus: string
  paymentExpiresAt?: string
  paidAt?: string
  createdAt: string
  updatedAt: string
  items?: OrderItem[]
}

export interface DashboardMetrics {
  totalRevenue: number
  totalOrders: number
  pendingOrders: number
  totalProducts: number
  lowStockCount: number
}

export interface InteractionSummary {
  totalViews: number
  totalWishlists: number
  totalAddToCart: number
  totalPurchases: number
  totalSearches: number
}

export interface DailyInteractionPoint {
  date: string
  views: number
  wishlists: number
  addToCarts: number
  purchases: number
}

export interface TopInteractedProduct {
  productId: number
  productName: string
  slug: string
  categoryName?: string
  thumbnailUrl?: string
  minPrice?: number
  viewCount: number
  wishlistCount: number
  addToCartCount: number
  purchaseCount: number
  interactionScore: number
}

export interface BehaviorAnalytics {
  days: number
  summary: InteractionSummary
  dailySeries: DailyInteractionPoint[]
  topProducts: TopInteractedProduct[]
}


export interface Banner {
  id: number
  title: string
  imageUrl: string
  mobileImageUrl?: string
  linkUrl: string
  targetType: string
  position: string
  displayOrder: number
  status: string
  startDate?: string
  endDate?: string
  clickCount?: number
  createdAt?: string
}
