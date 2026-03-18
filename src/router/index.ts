import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import { useUserStore } from '@/stores/user'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: "home",
    component: HomeView
  },
  {
    path: '/products',
    name: 'products',
    component: () => import("@/views/Orders/ProductsView.vue")
  },
  {
    path: '/about',
    name: 'about',
    component: () => import("@/views/AboutView.vue")
  },
  {
    path: '/contact',
    name: 'contact',
    component: () => import("@/views/ContactView.vue")
  },
  {
    path: '/cart',
    name: 'cart',
    component: () => import("@/views/CartView.vue")
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('@/views/ProfileView.vue')
  },
  {
    path: '/orders',
    name: 'orders',
    component: () => import('@/views/Orders/OrdersView.vue')
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
  meta: { requiresAuth: true } // 需要登录
},
{
  path: '/merchant/orders/:id',
  name: 'merchant-order-detail',
  component: () => import('@/views/Merchants/MerchantOrderDetail.vue'),
  meta: { requiresAuth: true, role: 'merchant' }
},
{
  path: '/orders',
  name: 'orders',
  component: () => import('@/views/Orders/OrdersView.vue'),
  meta: { requiresAuth: true }
},
{
  path: '/search',
  name: 'search',
  component: () => import('@/views/SearchResults.vue')
},
{
  path: '/order/:id',
  name: 'order-detail',
  component: () => import('@/views/Orders/OrderDetail.vue'),
  meta: { requiresAuth: true }
},
  {
    path: '/merchant/products',
    name: 'merchant-products',
    component: () => import('@/views/Merchants/MerchantProducts.vue'),
    meta: { requiresAuth: true, role: 'merchant' }
    // children:[
    //   {
    //     path:"series/:id",
    //     name: "merchant-series-detail",
    //     component:() => import("@/views/merchant/MerchantSeriesDetails.vue"),
    //     meta: { requiresAuth: true, role: 'merchant' }
    //   }
    // ]
  },
  {
  path: '/series/:id',
  name: 'series-detail',
  component: () => import('@/views/SeriesDetailView.vue')
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
    children:[
      {
        path: '',
        name: 'dashboard-overview',
        component:() => import ("@/components/dashboard/DashboardOverview.vue")
      },
      {
        path: 'carousel',
        name: 'carousel',
        component:() => import("@/components/dashboard/BannerManagement.vue")
      },
      {
        path: 'sales',
        name: 'sales',
        component:() => import("@/components/dashboard/SalesStats.vue")
      },
      {
        path: 'reports',
        name: 'reports',
        component:() => import("@/components/dashboard/Reports.vue")
      }
    ]
  },
  {
    path: '/merchant/orders',
    name: 'merchant-orders',
    component: () => import('@/views/Merchants/MerchantOrders.vue'),
    meta: { requiresAuth: true, role: 'merchant' }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

// 路由守卫
router.beforeEach((to, from) => {
  const userStore = useUserStore()

  if ((to.path === '/login' || to.path === '/register') && userStore.isLoggedIn) {
    return '/' // 重定向到首页
  }

  if (to.meta.requiresAuth) {
    if (!userStore.isLoggedIn) {
      return '/login' // 未登录，跳转登录页
    }
    // 修正：使用 userInfo 而不是 currentUser
    if (to.meta.role && userStore.userInfo?.role !== to.meta.role) {
      return '/' // 角色不匹配，跳转首页
    }
  }
  // 默认放行
  return
})

export default router