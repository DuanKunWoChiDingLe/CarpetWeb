import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import { useUserStore } from '@/stores/user'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/products',
    name: 'products',
    component: () => import('@/views/Orders/ProductsView.vue')
  },
  {
    path: '/about',
    name: 'about',
    component: () => import('@/views/AboutView.vue')
  },
  {
    path: '/contact',
    name: 'contact',
    component: () => import('@/views/ContactView.vue')
  },
  {
    path: '/cart',
    name: 'cart',
    component: () => import('@/views/CartView.vue')
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('@/views/ProfileView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'orders',
    component: () => import('@/views/Orders/OrdersView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/product/:id',
    name: 'product-detail',
    component: () => import('@/views/Orders/ProductDetail.vue')
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/Account/LoginView.vue')
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/Account/RegisterView.vue')
  },
  {
    path: '/checkout',
    name: 'checkout',
    component: () => import('@/views/Orders/CheckoutView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/order/:id',
    name: 'order-detail',
    component: () => import('@/views/Orders/OrderDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/search',
    name: 'search',
    component: () => import('@/views/SearchResults.vue')
  },
  {
    path: '/series/:id',
    name: 'series-detail',
    component: () => import('@/views/SeriesDetailView.vue')
  },
  {
    path: '/merchant/orders',
    name: 'merchant-orders',
    component: () => import('@/views/Merchants/MerchantOrders.vue'),
    meta: { requiresAuth: true, role: 'merchant' }
  },
  {
    path: '/merchant/orders/:id',
    name: 'merchant-order-detail',
    component: () => import('@/views/Merchants/MerchantOrderDetail.vue'),
    meta: { requiresAuth: true, role: 'merchant' }
  },
  {
    path: '/merchant/products',
    name: 'merchant-products',
    component: () => import('@/views/Merchants/MerchantProducts.vue'),
    meta: { requiresAuth: true, role: 'merchant' }
  },
  {
    path: '/merchant/series/:id',
    name: 'merchant-series-detail',
    component: () => import('@/views/Merchants/MerchantSeriesDetails.vue'),
    meta: { requiresAuth: true, role: 'merchant' }
  },
  {
    path: '/merchant/dashboard',
    name: 'merchant-dashboard',
    component: () => import('@/views/Merchants/MerchantDashboard.vue'),
    meta: { requiresAuth: true, role: 'merchant' },
    children: [
      {
        path: '',
        name: 'dashboard-overview',
        component: () => import('@/components/dashboard/DashboardOverview.vue')
      },
      {
        path: 'carousel',
        name: 'carousel',
        component: () => import('@/components/dashboard/BannerManagement.vue')
      },
      {
        path: 'sales',
        name: 'sales',
        component: () => import('@/components/dashboard/SalesStats.vue')
      },
      {
        path: 'reports',
        name: 'reports',
        component: () => import('@/components/dashboard/Reports.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach(async (to) => {
  const userStore = useUserStore()

  // 刷新页面时先从服务端恢复角色，避免商家后台被误判为无权限
  if (userStore.token && !userStore.sessionRestored) {
    await userStore.fetchCurrentUser()
  }

  if ((to.path === '/login' || to.path === '/register') && userStore.isLoggedIn) {
    return '/'
  }

  if (to.meta.requiresAuth) {
    if (!userStore.isLoggedIn) {
      return { path: '/login', query: { redirect: to.fullPath } }
    }
    if (to.meta.role && userStore.userInfo?.role !== to.meta.role) {
      return '/'
    }
  }
})

export default router


