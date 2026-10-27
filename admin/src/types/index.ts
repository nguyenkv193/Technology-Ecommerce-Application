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

export interface ProductSpec {
  id?: number
  specKey: string
  specValue: string
  displayOrder?: number
}

export interface ProductImage {
  id?: number
  imageUrl: string
  isPrimary?: boolean
  displayOrder?: number
}

export interface ProductSku {
  id?: number
  skuCode: string
  skuName: string
  price: number
  originalPrice?: number
  stockQuantity: number
  attributesJson?: string
  status?: string
}

export interface Product {
  id: number
  name: string
  slug: string
  shortDescription?: string
  description?: string
  categoryId: number
  categoryName?: string
  brandId: number
  brandName?: string
  originalPrice: number
  salePrice: number
  warrantyMonths?: number
  primaryImageUrl?: string
  stockQuantity?: number
  isActive?: boolean
  isFeatured?: boolean
  createdAt?: string
  updatedAt?: string
  specs?: ProductSpec[]
  images?: ProductImage[]
  skus?: ProductSku[]
}

export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'SHIPPING' | 'DELIVERED' | 'CANCELLED'

export interface OrderItem {
  id: number
  productId: number
  productName: string
  skuId?: number
  skuCode?: string
  productImage?: string
  unitPrice: number
  quantity: number
  totalPrice: number
}

export interface Order {
  id: number
  orderCode: string
  userId: number
  customerName: string
  customerEmail: string
  customerPhone: string
  shippingAddress: string
  paymentMethod: string
  paymentStatus: string
  status: OrderStatus
  subtotal: number
  shippingFee: number
  discountAmount: number
  totalAmount: number
  notes?: string
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
