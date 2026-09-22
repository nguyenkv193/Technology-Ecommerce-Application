import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import CatalogView from '@/views/CatalogView.vue'
import ProductDetailView from '@/views/ProductDetailView.vue'
import CartView from '@/views/CartView.vue'
import OrdersView from '@/views/OrdersView.vue'
import WishlistView from '@/views/WishlistView.vue'
import AdminDashboardView from '@/views/AdminDashboardView.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/products',
    name: 'catalog',
    component: CatalogView
  },
  {
    path: '/products/:id',
    name: 'product-detail',
    component: ProductDetailView
  },
  {
    path: '/cart',
    name: 'cart',
    component: CartView
  },
  {
    path: '/orders',
    name: 'orders',
    component: OrdersView
  },
  {
    path: '/wishlist',
    name: 'wishlist',
    component: WishlistView
  },
  {
    path: '/admin',
    name: 'admin-dashboard',
    component: AdminDashboardView
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView.vue'),
    meta: { hideHeaderFooter: true }
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/LoginView.vue'),
    meta: { hideHeaderFooter: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

export default router