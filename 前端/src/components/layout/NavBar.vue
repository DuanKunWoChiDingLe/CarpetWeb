<template>
  <el-header class="navbar" height="70px">
    <div class="navbar-container">
      <!-- 回退按钮 -->
      <div class="back-button" @click="goBack">
        <el-icon size="20"><ArrowLeft /></el-icon>
      </div>

      <!-- Logo：使用高清 PNG，确保透明背景 -->
      <div class="logo">
        <router-link to="/" class="logo-link">
          <!-- 注意：确保图片路径是 ../../assets/logo.png 且是高清透明 PNG -->
          <img src="../../assets/touming_logo.png" alt="道成国际" class="logo-image" />
        </router-link>
      </div>

      <!-- 其余结构完全不变 -->
      <el-menu
        :default-active="activeIndex"
        class="nav-menu"
        mode="horizontal"
        :router="true"
        :ellipsis="false"
        background-color="transparent"
        text-color="#fff"
        active-text-color="#409EFF"
      >
        <el-menu-item
          v-for="item in visibleMenus"
          :key="item.path"
          :index="item.path"
        >
          <template v-if="item.isOrder && userStore.userInfo?.role === 'merchant'">
            <el-badge :value="notificationStore.newOrderCount" :hidden="notificationStore.newOrderCount === 0">
              <span class="menu-text">{{ item.name }}</span>
            </el-badge>
          </template>
          <template v-else>
            <span class="menu-text">{{ item.name }}</span>
          </template>
        </el-menu-item>
      </el-menu>

      <el-dropdown v-if="foldedMenus.length > 0" class="more-dropdown" @command="handleMoreCommand">
        <span class="more-link">
          更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item
              v-for="item in foldedMenus"
              :key="item.path"
              :command="item.path"
            >
              <span v-if="!item.isOrder">{{ item.name }}</span>
              <span v-else>
                {{ item.name }} ({{ notificationStore.newOrderCount }})
              </span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>

      <div class="navbar-actions">
        <div v-if="!isMobile" class="search-wrapper">
          <el-autocomplete
            v-model="searchText"
            :fetch-suggestions="() => []"
            placeholder="搜索地毯（名称/品牌）"
            :trigger-on-focus="false"
            clearable
            class="search-input"
            @keyup.enter="handleSearch"
            @select="() => {}"
          >
            <template #prefix>
              <el-icon class="search-icon"><Search /></el-icon>
            </template>
            <template #suffix>
              <el-button
                v-if="!searchText"
                type="text"
                :icon="Search"
                @click="handleSearch"
                class="search-btn"
              />
            </template>
          </el-autocomplete>
        </div>
        <el-icon v-else class="search-icon-mobile" @click="goToSearchPage">
          <Search />
        </el-icon>

        <div class="cart-icon" @click="goToCart">
          <el-badge :value="cartCount" :hidden="cartCount === 0" class="cart-badge">
            <el-icon size="24"><ShoppingCart /></el-icon>
          </el-badge>
        </div>

        <el-dropdown class="user-dropdown" @command="handleUserCommand">
          <el-avatar :size="40" :src="userAvatar" class="user-avatar" />
          <template #dropdown>
            <el-dropdown-menu class="user-dropdown-menu">
              <template v-if="!userStore.isLoggedIn">
                <el-dropdown-item command="login" class="dropdown-item">登录</el-dropdown-item>
                <el-dropdown-item command="register" class="dropdown-item">注册</el-dropdown-item>
              </template>
              <template v-else>
                <el-dropdown-item disabled class="dropdown-item nickname-item">
                  {{ userStore.userInfo?.nickname || userStore.userInfo?.username }}
                </el-dropdown-item>
                <el-dropdown-item disabled divided class="dropdown-divider" />
                <el-dropdown-item command="profile" class="dropdown-item">个人资料</el-dropdown-item>
                <el-dropdown-item command="orders" class="dropdown-item">我的订单</el-dropdown-item>
                <el-dropdown-item divided command="logout" class="dropdown-item logout-item">
                  退出登录
                </el-dropdown-item>
              </template>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </el-header>
</template>

<script setup lang="ts">
// Script部分代码不变，直接沿用你的代码
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Search, ShoppingCart, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { useNotificationStore } from '@/stores/notification'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()
const notificationStore = useNotificationStore()

const activeIndex = computed(() => route.path)
const searchText = ref('')
const cartCount = computed(() => cartStore.totalCount)

const userAvatar = computed(() => {
  const avatar = userStore.userInfo?.avatar
  if (avatar) {
    if (avatar.startsWith('http')) return avatar
    if (avatar.startsWith('/uploads')) return avatar
    return (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080') + avatar
  }
  return 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
})

const allMenus = computed(() => {
  const baseMenus = [
    { path: '/', name: '首页', isOrder: false },
    { path: '/products', name: '产品中心', isOrder: false }
  ]
  if (!userStore.isLoggedIn || userStore.userInfo?.role === 'customer') {
    return [
      ...baseMenus,
      { path: '/about', name: '关于我们', isOrder: false },
      { path: '/contact', name: '联系我们', isOrder: false }
    ]
  }
  if (userStore.userInfo?.role === 'merchant') {
    return [
      ...baseMenus,
      { path: '/merchant/dashboard', name: '商家控制台', isOrder: false },
      { path: '/merchant/products', name: '商品管理', isOrder: false },
      { path: '/merchant/orders', name: '订单管理', isOrder: true }
    ]
  }
  return baseMenus
})

const windowWidth = ref(window.innerWidth)
const updateWidth = () => {
  windowWidth.value = window.innerWidth
}
onMounted(() => {
  window.addEventListener('resize', updateWidth)
})
onUnmounted(() => {
  window.removeEventListener('resize', updateWidth)
})

const isMobile = computed(() => windowWidth.value < 768)
const visibleCount = computed(() => {
  const width = windowWidth.value
  if (width >= 1200) return allMenus.value.length
  if (width >= 992) return Math.min(allMenus.value.length, 4)
  if (width >= 768) return Math.min(allMenus.value.length, 3)
  if (width >= 576) return Math.min(allMenus.value.length, 2)
  return 1
})

const visibleMenus = computed(() => allMenus.value.slice(0, visibleCount.value))
const foldedMenus = computed(() => allMenus.value.slice(visibleCount.value))

const handleMoreCommand = (path: string) => router.push(path)
const goBack = () => router.back()
const goToCart = () => router.push('/cart')

const handleSearch = () => {
  const keyword = searchText.value.trim()
  if (!keyword) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  router.push({ path: '/search', query: { keyword } })
  searchText.value = ''
}

const goToSearchPage = () => router.push('/search')

const handleUserCommand = (command: string) => {
  if (!userStore.isLoggedIn) {
    if (command === 'login') router.push('/login')
    else if (command === 'register') router.push('/register')
    return
  }
  switch (command) {
    case 'profile': router.push('/profile'); break
    case 'orders': router.push('/orders'); break
    case 'logout':
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/')
      break
  }
}
</script>

<style scoped>
.navbar {
  background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 1000;
  height: 70px;
  transition: all 0.3s ease;
}

.navbar-container {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 16px;
  flex-wrap: nowrap;
}

.back-button {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  cursor: pointer;
  color: #fff;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.1);
}
.back-button:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(1.05);
}
.back-button:active {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(0.98);
}

/* ========== Logo 样式优化（清晰+完美适配导航栏） ========== */
/* ========== Logo 样式：明显增大，适配导航栏 ========== */
.logo {
  flex-shrink: 0;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 16px;          /* 左右留白稍大，突出Logo */
  background: transparent;
}

.logo-link {
  display: flex;
  align-items: center;
  line-height: 0;
  transition: transform 0.25s ease, filter 0.25s ease;
}

.logo-link:hover {
  transform: scale(1.05);
  filter: drop-shadow(0 2px 10px rgba(64, 158, 255, 0.4));
}

.logo-image {
  /* 关键：明显增大的高度，与70px导航栏匹配（上下各留7px） */
  height: 82px;
  width: auto;
  display: block;
  background: transparent;
  object-fit: contain;
  image-rendering: -webkit-optimize-contrast;
  image-rendering: crisp-edges;
  -ms-interpolation-mode: bicubic;
  backface-visibility: hidden;
}

.nav-menu {
  flex-shrink: 1;
  min-width: 0;
  border-bottom: none !important;
  background-color: transparent;
  height: 100%;
}
.nav-menu :deep(.el-menu-item) {
  font-size: 16px;
  padding: 0 20px;
  line-height: 70px;
  height: 70px;
  position: relative;
  transition: all 0.4s cubic-bezier(0.19, 1, 0.22, 1);
  overflow: hidden;
  white-space: nowrap;
}
.nav-menu :deep(.el-menu-item::before) {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(64, 158, 255, 0.15), transparent);
  transition: left 0.6s ease;
  z-index: 0;
}
.nav-menu :deep(.el-menu-item:hover) {
  background: rgba(64, 158, 255, 0.08);
  color: #409eff !important;
  text-shadow: 0 0 8px rgba(64, 158, 255, 0.6);
  box-shadow: 0 0 12px rgba(64, 158, 255, 0.15);
}
.nav-menu :deep(.el-menu-item:hover::before) {
  left: 100%;
}
.nav-menu :deep(.el-menu-item.is-active) {
  color: #409eff !important;
  text-shadow: 0 0 10px rgba(64, 158, 255, 0.8);
}
.nav-menu :deep(.el-menu-item.is-active)::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 30px;
  height: 3px;
  background: linear-gradient(90deg, #409eff, #66b1ff);
  border-radius: 3px;
  box-shadow: 0 0 8px rgba(64, 158, 255, 0.6);
}
.menu-text {
  font-weight: 500;
  letter-spacing: 0.5px;
  position: relative;
  z-index: 1;
}

.more-dropdown {
  flex-shrink: 0;
  margin-left: 4px;
}
.more-link {
  color: #fff;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 0 8px;
  height: 70px;
  transition: color 0.3s, transform 0.3s, text-shadow 0.3s;
}
.more-link:hover {
  color: #409eff;
  transform: scale(1.05);
  text-shadow: 0 0 8px rgba(64, 158, 255, 0.6);
}

.navbar-actions {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: auto;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.search-wrapper {
  flex-shrink: 0;
}
.search-input {
  width: 280px;
  transition: all 0.3s ease;
}
.search-input :deep(.el-input__wrapper) {
  border-radius: 24px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.08), rgba(255, 255, 255, 0.03));
  border: 1px solid rgba(255, 255, 255, 0.15);
  padding: 8px 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.4s ease;
}
.search-input :deep(.el-input__wrapper:hover) {
  border-color: rgba(64, 158, 255, 0.4);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.12), rgba(255, 255, 255, 0.05));
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12), 0 0 8px rgba(64, 158, 255, 0.15);
  transform: translateY(-1px);
}
.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: rgba(64, 158, 255, 0.6);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15), 0 0 12px rgba(64, 158, 255, 0.25);
  outline: none;
}
.search-input :deep(.el-input__inner) {
  color: #fff;
  font-size: 14px;
  padding: 4px 0;
  background: transparent;
}
.search-input :deep(.el-input__inner)::placeholder {
  color: rgba(255, 255, 255, 0.6);
  font-size: 13px;
  letter-spacing: 0.3px;
}
.search-icon {
  color: #409eff;
  font-size: 16px;
  transition: all 0.3s cubic-bezier(0.19, 1, 0.22, 1);
}
.search-icon:hover {
  color: #66b1ff;
  transform: translateY(-2px) scale(1.1);
  filter: drop-shadow(0 4px 8px rgba(64, 158, 255, 0.4));
}
.search-btn {
  color: #409eff;
  transition: all 0.3s cubic-bezier(0.19, 1, 0.22, 1);
  padding: 0 8px;
  font-size: 16px;
}
.search-btn:hover {
  color: #66b1ff;
  transform: translateY(-2px) scale(1.15);
  filter: drop-shadow(0 4px 8px rgba(64, 158, 255, 0.4));
}

.search-icon-mobile {
  font-size: 24px;
  color: #fff;
  cursor: pointer;
  transition: color 0.3s;
  display: flex;
  align-items: center;
}
.search-icon-mobile:hover {
  color: #409eff;
}

.cart-icon {
  cursor: pointer;
  display: flex;
  align-items: center;
  color: #fff;
  transition: all 0.3s ease;
  padding: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}
.cart-icon:hover {
  color: #409eff;
  background: rgba(255, 255, 255, 0.1);
  transform: scale(1.1);
  text-shadow: 0 0 8px rgba(64, 158, 255, 0.6);
}
.cart-badge :deep(.el-badge__content) {
  background-color: #e74c3c;
  box-shadow: 0 2px 8px rgba(231, 76, 60, 0.5);
  top: -5px;
  right: -5px;
}

.user-avatar {
  cursor: pointer;
  border: 2px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
  flex-shrink: 0;
}
.user-avatar:hover {
  border-color: #409eff;
  transform: scale(1.05);
  box-shadow: 0 0 12px rgba(64, 158, 255, 0.4);
}
.user-dropdown-menu {
  background: rgba(44, 62, 80, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  padding: 8px 0;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}
.dropdown-item {
  color: #fff;
  transition: all 0.2s ease;
}
.dropdown-item:hover {
  background: rgba(64, 158, 255, 0.1);
  color: #409eff;
  text-shadow: 0 0 6px rgba(64, 158, 255, 0.4);
}
.nickname-item {
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
}
.logout-item {
  color: #e74c3c;
}
.logout-item:hover {
  color: #fff;
  background: #e74c3c;
}
.dropdown-divider {
  border-color: rgba(255, 255, 255, 0.1);
}

/* 响应式适配（Logo 已跟随容器自适应，无需额外修改） */
@media screen and (max-width: 1200px) {
  .search-input {
    width: 240px;
  }
  .nav-menu :deep(.el-menu-item) {
    padding: 0 16px;
    font-size: 15px;
  }
}
@media screen and (max-width: 992px) {
  .search-input {
    width: 200px;
  }
  .nav-menu :deep(.el-menu-item) {
    padding: 0 12px;
    font-size: 14px;
  }
}
@media screen and (max-width: 768px) {
  .search-input {
    width: 160px;
  }
  .nav-menu :deep(.el-menu-item) {
    padding: 0 8px;
  }
}
@media screen and (max-width: 576px) {
  .back-button {
    width: 30px;
  }
  .logo-image {
    height: 40px; /* 移动端缩小logo，保持适配 */
  }
  .navbar-container {
    gap: 8px;
    padding: 0 8px;
  }
  .navbar-actions {
    gap: 6px;
  }
}
</style>