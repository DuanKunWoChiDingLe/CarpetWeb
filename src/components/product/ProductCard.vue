<template>
  <el-card :body-style="{ padding: '0px' }" shadow="hover" class="product-card">
    <!-- 图片轮播区域，点击跳转详情（排除箭头和指示器） -->
    <div class="image-container" @click="goToDetail($event)">
      <!-- 有图片时显示轮播 -->
      <el-carousel
        v-if="product.images && product.images.length > 0"
        height="200px"
        :interval="3000"
        arrow="hover"
        indicator-position="none"
        class="product-carousel"
      >
        <el-carousel-item v-for="(img, index) in product.images" :key="index">
          <el-image :src="getImageUrl(img)" fit="cover" class="carousel-image" />
        </el-carousel-item>
      </el-carousel>
      <!-- 无图片时显示默认占位图 -->
      <el-image v-else :src="getImageUrl('/images/default.jpg')" fit="cover" class="single-image" />
    </div>

    <!-- 商品信息 -->
    <div class="product-info">
      <h3 class="product-title" @click="goToDetail">{{ product.name }}</h3>
      <div class="price-action">
        <div class="product-price">
          <span class="price-symbol">¥</span>
          <span class="price-number">
            {{ displayPrice }}
          </span>
          <span class="price-unit">/m²</span>
        </div>
        <!-- 购物车图标按钮 -->
        <el-button circle :icon="ShoppingCart" class="cart-btn-circle" @click.stop="addToCart" />
      </div>
      <!-- 查看详情按钮 -->
      <el-button type="primary" size="small" class="detail-btn" @click="goToDetail">
        查看详情
      </el-button>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart } from '@element-plus/icons-vue'
import { useCartStore } from '@/stores/cart'
import type { Product } from '@/types/product'
import { useUserStore } from '@/stores/user'

// 从环境变量获取 API 基础地址
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const userStore = useUserStore()

// 处理图片路径：如果是相对路径，拼接基础 URL
const getImageUrl = (path: string) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  // 确保路径以 / 开头
  return baseURL + (path.startsWith('/') ? path : '/' + path)
}

const props = defineProps<{
  product: Product
}>()

const router = useRouter()
const cartStore = useCartStore()

const displayPrice = computed(() => {
  return props.product.pricePerSqm ? props.product.pricePerSqm.toFixed(2) : '0.00'
})

// 跳转详情页，排除轮播图箭头和指示器的点击
const goToDetail = (event?: MouseEvent) => {
  if (event) {
    const target = event.target as HTMLElement
    if (target.closest('.el-carousel__arrow') || target.closest('.el-carousel__indicator')) {
      return
    }
  }
  router.push(`/product/${props.product.id}`)
}

const addToCart = () => {
  // 未登录，跳转登录页
  if (!userStore.isLoggedIn) {
    router.push({
      path: '/login',
      query: { redirect: router.currentRoute.value.fullPath }
    })
    return
  }
  // 已登录，执行加入购物车逻辑
  cartStore.addItem({
    productId: props.product.id,
    quantity: 1
  })
  ElMessage.success('已加入购物车')
}
</script>

<style scoped>
.product-card {
  width: 100%;
  border-radius: 8px;
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.image-container {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
  border-radius: 8px 8px 0 0;
  cursor: pointer;
}

.product-carousel,
.single-image {
  width: 100%;
  height: 100%;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.single-image {
  object-fit: cover;
}

.product-info {
  padding: 12px;
}

.product-title {
  font-size: 16px;
  font-weight: 500;
  margin: 0 0 8px 0;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
}

.product-title:hover {
  color: #409EFF;
}

.price-action {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.product-price {
  color: #f56c6c;
  font-weight: bold;
}

.price-symbol {
  font-size: 14px;
}

.price-number {
  font-size: 18px;
  margin-left: 2px;
}

.cart-btn {
  font-size: 18px;
  color: #909399;
  padding: 4px;
}

.cart-btn:hover {
  color: #409EFF;
}

.detail-btn {
  width: 100%;
}

.cart-btn-circle {
  width: 40px;
  /* 按钮宽度 */
  height: 40px;
  /* 按钮高度，与宽度相等实现圆形 */
  padding: 0;
  /* 去除内边距，使图标居中 */
  font-size: 20px;
  /* 图标大小 */
  border: 1px solid #dcdfe6;
  /* 浅灰色边框 */
  background-color: #fff;
  /* 白色背景 */
  color: #909399;
  /* 图标颜色 */
}

.cart-btn-circle:hover {
  color: #409EFF;
  /* 悬停时图标变为主题色 */
  border-color: #409EFF;
  /* 边框也变为主题色 */
  background-color: #ecf5ff;
  /* 浅蓝色背景 */
}
</style>