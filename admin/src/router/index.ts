import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAdminAuthStore } from '@/stores/auth'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'dashboard',
        component: () => import('@/views/DashboardView.vue'),
        meta: { title: 'Dashboard Tổng quan' }
      },
      {
        path: 'products',
        name: 'products',
        component: () => import('@/views/products/ProductListView.vue'),
        meta: { title: 'Quản lý Sản phẩm' }
      },
      {
        path: 'products/create',
        name: 'product-create',
        component: () => import('@/views/products/ProductFormView.vue'),
        meta: { title: 'Thêm sản phẩm mới' }
      },
      {
        path: 'products/:id/edit',
        name: 'product-edit',
        component: () => import('@/views/products/ProductFormView.vue'),
        meta: { title: 'Cập nhật sản phẩm' }
      },
      {
        path: 'categories',
        name: 'categories',
        component: () => import('@/views/categories/CategoryListView.vue'),
        meta: { title: 'Quản lý Danh mục' }
      },
      {
        path: 'brands',
        name: 'brands',
        component: () => import('@/views/brands/BrandListView.vue'),
        meta: { title: 'Quản lý Thương hiệu' }
      },
      {
        path: 'orders',
        name: 'orders',
        component: () => import('@/views/orders/OrderListView.vue'),
        meta: { title: 'Quản lý Đơn hàng' }
      },
      {
        path: 'system-ops',
        name: 'system-ops',
        component: () => import('@/views/system/SystemOpsView.vue'),
        meta: { title: 'Vận hành Hệ thống' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  const authStore = useAdminAuthStore()

  // If visiting login while already authenticated as ADMIN, redirect to dashboard
  if (to.name === 'login') {
    if (authStore.isAuthenticated) {
      return next('/')
    }
    return next()
  }

  // If route requires auth or is any protected route
  if (to.meta.requiresAuth || to.path !== '/login') {
    if (!authStore.token) {
      return next({ name: 'login' })
    }

    if (!authStore.user) {
      const profile = await authStore.fetchProfile()
      if (!profile || profile.role !== 'ADMIN') {
        return next({ name: 'login' })
      }
    }

    if (!authStore.isAuthenticated) {
      return next({ name: 'login' })
    }
  }

  next()
})

export default router
