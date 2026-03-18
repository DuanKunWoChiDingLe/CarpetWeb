<template>
  <el-header class="navbar" height="70px">
    <div class="navbar-container">
      <!-- 回退按钮 -->
      <div class="back-button" @click="goBack">
        <el-icon size="20"><ArrowLeft /></el-icon>
      </div>

      <!-- Logo -->
      <div class="logo">
        <router-link to="/" class="logo-link">
          <img src="@/assets/logo.jpg" alt="地毯公司" class="logo-image" />
        </router-link>
      </div>

      <!-- 可见菜单项（平滑过渡） -->
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
          <!-- 商家订单管理项：显示角标 -->
          <template v-if="item.isOrder && userStore.userInfo?.role === 'merchant'">
            <el-badge :value="notificationStore.newOrderCount" :hidden="notificationStore.newOrderCount === 0">
              <span class="menu-text">{{ item.name }}</span>
            </el-badge>
          </template>
          <!-- 其他菜单项 -->
          <template v-else>
            <span class="menu-text">{{ item.name }}</span>
          </template>
        </el-menu-item>
      </el-menu>

      <!-- 更多下拉菜单（科技感按钮） -->
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

      <!-- 右侧功能区（允许换行，防止溢出） -->
      <div class="navbar-actions">
        <!-- 搜索框（宽屏显示） -->
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
        <!-- 移动端搜索图标 -->
        <el-icon v-else class="search-icon-mobile" @click="goToSearchPage">
          <Search />
        </el-icon>

        <!-- 购物车图标 -->
        <div class="cart-icon" @click="goToCart">
          <el-badge :value="cartCount" :hidden="cartCount === 0" class="cart-badge">
            <el-icon size="24"><ShoppingCart /></el-icon>
          </el-badge>
        </div>

        <!-- 用户头像下拉菜单 -->
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
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, Search, ShoppingCart, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { useNotificationStore } from '@/stores/notification' // 新增导入

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()
const notificationStore = useNotificationStore() // 新增实例

// 当前激活菜单项
const activeIndex = computed(() => route.path)

// 搜索框文本
const searchText = ref('')

// 购物车数量
const cartCount = computed(() => cartStore.totalCount)

// 用户头像
const userAvatar = computed(() => {
  const avatar = userStore.userInfo?.avatar
  if (avatar) {
    return avatar.startsWith('http')
      ? avatar
      : (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080') + avatar
  }
  return 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
})

// 所有菜单项，统一为每个项添加 isOrder 字段（默认 false）
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
      { path: '/merchant/orders', name: '订单管理', isOrder: true } // 订单管理项标记为 true
    ]
  }
  return baseMenus
})

// 窗口宽度响应
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

// 移动端判断
const isMobile = computed(() => windowWidth.value < 768)

// 可见菜单数量（首页始终保留）
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
/* 原有样式保持不变，无需修改 */
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

/* 回退按钮 */
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

/* Logo */
.logo {
  flex-shrink: 0;
}
.logo-link {
  display: block;
  line-height: 0;
  transition: transform 0.3s ease;
}
.logo-link:hover {
  transform: scale(1.05);
}
.logo-image {
  height: 48px;
  width: auto;
  display: block;
  border-radius: 8px;
}

/* 导航菜单 - 平滑过渡 */
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
  transition: all 0.4s cubic-bezier(0.19, 1, 0.22, 1); /* 平滑过渡 */
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

/* 更多下拉菜单 - 科技感按钮 */
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

/* 右侧功能区 - 允许换行防止溢出 */
.navbar-actions {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: auto;
  flex-wrap: wrap;
  justify-content: flex-end;
}

/* 搜索框 */
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

/* 移动端搜索图标 */
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

/* 购物车图标 */
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

/* 用户头像 */
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

/* 响应式宽度调整 */
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
    height: 40px;
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