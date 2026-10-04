import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import CatalogView from '@/views/CatalogView.vue'
import ProductDetailView from '@/views/ProductDetailView.vue'
import CartView from '@/views/CartView.vue'
import OrdersView from '@/views/OrdersView.vue'
import WishlistView from '@/views/WishlistView.vue'
import { useAuthStore } from '@/stores/auth'

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
    path: '/login',
    name: 'login',
    redirect: () => '/'
  },
  {
    path: '/register',
    name: 'register',
    redirect: () => '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  if (to.path === '/login') {
    authStore.openLoginModal()
    if (from.name && from.path !== '/login' && from.path !== '/register') {
      return next(false)
    }
    return next('/')
  }
  if (to.path === '/register') {
    authStore.openRegisterModal()
    if (from.name && from.path !== '/login' && from.path !== '/register') {
      return next(false)
    }
    return next('/')
  }
  next()
})

export default router
