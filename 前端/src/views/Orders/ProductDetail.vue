<template>
  <div class="product-detail">
    <!-- 面包屑导航 -->
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/products' }">产品中心</el-breadcrumb-item>
      <el-breadcrumb-item v-if="product" :to="{ path: `/series/${product.seriesId}` }">
        {{ product.seriesName }}
      </el-breadcrumb-item>
      <el-breadcrumb-item>{{ product?.name || '商品详情' }}</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 加载状态 -->
    <div v-loading="loading" class="loading-container" v-if="loading">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 主体内容 -->
    <el-row :gutter="40" v-else-if="product">
      <!-- 左侧：图片轮播 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <div class="gallery-container">
          <el-carousel
            v-if="product.images && product.images.length > 0"
            height="400px"
            :interval="4000"
            arrow="always"
            indicator-position="outside"
          >
            <el-carousel-item v-for="(img, index) in product.images" :key="index">
              <el-image
                :src="getImageUrl(img)"
                :preview-src-list="product.images.map(getImageUrl)"
                fit="cover"
                class="gallery-image"
                lazy
              />
            </el-carousel-item>
          </el-carousel>
          <div v-else class="no-image">暂无图片</div>
        </div>
      </el-col>

      <!-- 右侧：商品信息及购买 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <div class="info-container">
          <h1 class="product-name">{{ product.name }}</h1>
          
          <!-- 品牌和类型标签 -->
          <div class="product-tags">
            <el-tag size="small" type="info">{{ product.brandName }}</el-tag>
            <el-tag size="small" :type="product.layType === 'full' ? 'warning' : 'success'">
              {{ product.layType === 'full' ? '满铺毯' : '方块毯' }}
            </el-tag>
            <el-tag size="small" v-if="product.material">{{ materialMap[product.material] }}</el-tag>
            <el-tag size="small">{{ product.spec }}</el-tag>
          </div>

          <!-- 价格 -->
          <div class="product-price">
            <span class="label">每平米售价：</span>
            <span class="price">¥{{ product.pricePerSqm?.toFixed(2) }}</span>
            <span class="unit">/m²</span>
          </div>

          <!-- 简短描述 -->
          <div class="product-desc" v-if="product.description">
            {{ product.description }}
          </div>

          <!-- 库存信息 -->
          <div class="extra-info">
            <span>库存：{{ product.stock }}</span>
            <el-divider direction="vertical" />
            <span>色号：{{ product.colorCode }}</span>
          </div>

          <!-- 购买操作区 -->
          <div class="purchase-section">
            <div class="quantity-wrapper">
              <span class="label">数量：</span>
              <el-input-number
                v-model="quantity"
                :min="1"
                :max="product.stock || 99"
                size="default"
              />
              <span class="stock">{{ product.stock > 0 ? '有货' : '缺货' }}</span>
            </div>
            <div class="button-wrapper">
              <el-button 
                type="primary" 
                size="large" 
                @click="addToCart" 
                :disabled="product.stock <= 0"
              >
                加入购物车
              </el-button>
              <el-button 
                type="danger" 
                size="large" 
                @click="buyNow" 
                :disabled="product.stock <= 0"
              >
                立即购买
              </el-button>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 商品不存在提示 -->
    <el-empty v-else description="商品不存在" />

    <!-- 下方：详情选项卡（如果需要扩展） -->
    <!-- 目前无更多详情，可后续添加 -->
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useCartStore } from '@/stores/cart'
import { productApi, type Product } from '@/api/product'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

// 材质映射
const materialMap: Record<string, string> = {
  polypropylene: '丙纶',
  polyester: '涤纶',
  nylon: '尼龙'
}

// 商品ID
const productId = computed(() => route.params.id as string)

// 商品详情数据
const product = ref<Product | null>(null)
const loading = ref(false)

// 数量
const quantity = ref(1)

// 获取图片完整URL
const getImageUrl = (path: string) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  if (path.startsWith('/uploads')) return path  
  return baseURL + path
}

// 获取商品详情
const fetchProductDetail = async () => {
  if (!productId.value) return
  loading.value = true
  try {
    const res = await productApi.getDetail(Number(productId.value))
    product.value = res
    // 确保库存为正数才可选
    if (res.stock <= 0) {
      ElMessage.warning('该商品暂时缺货')
    }
  } catch (error) {
    ElMessage.error('获取商品详情失败')
    product.value = null
  } finally {
    loading.value = false
  }
}

// 加入购物车
const addToCart = () => {
  if (!userStore.isLoggedIn) {
    router.push({
      path: '/login',
      query: { redirect: router.currentRoute.value.fullPath }
    })
    return
  }
  if (!product.value || product.value.stock <= 0) return

  cartStore.addItem({
    productId: product.value.id,
    quantity: quantity.value
  })
  ElMessage.success(`成功加入 ${quantity.value} 件商品到购物车`)
}

// 立即购买
const buyNow = () => {
  if (!userStore.isLoggedIn) {
    router.push({
      path: '/login',
      query: { redirect: router.currentRoute.value.fullPath }
    })
    return
  }
  if (!product.value || product.value.stock <= 0) return
  // 先加入购物车，然后跳转到购物车（或直接跳转到结算，暂未实现）
  addToCart()
  router.push('/cart')
}

// 初始化
onMounted(() => {
  fetchProductDetail()
})
</script>

<style scoped>
.product-detail {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
}

.breadcrumb {
  margin-bottom: 30px;
}

.loading-container {
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.gallery-container {
  background-color: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
  height: 400px;
}
.gallery-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
}
.no-image {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
}

.info-container {
  padding: 20px 0;
}
.product-name {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
}
.product-tags {
  margin-bottom: 15px;
}
.product-tags .el-tag {
  margin-right: 10px;
}
.product-price {
  margin-bottom: 20px;
  font-size: 18px;
}
.product-price .label {
  color: #666;
}
.product-price .price {
  color: #f56c6c;
  font-size: 28px;
  font-weight: bold;
  margin-left: 10px;
}
.product-price .unit {
  font-size: 14px;
  color: #999;
  margin-left: 5px;
}
.product-desc {
  margin-bottom: 20px;
  line-height: 1.6;
  color: #666;
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 6px;
}
.extra-info {
  margin-bottom: 20px;
  color: #666;
  font-size: 14px;
}
.purchase-section {
  border-top: 1px solid #eee;
  padding-top: 20px;
}
.quantity-wrapper {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
.quantity-wrapper .label {
  width: 60px;
  color: #666;
}
.stock {
  margin-left: 15px;
  color: #999;
  font-size: 14px;
}
.button-wrapper {
  display: flex;
  gap: 20px;
}
.button-wrapper .el-button {
  flex: 1;
}
</style>