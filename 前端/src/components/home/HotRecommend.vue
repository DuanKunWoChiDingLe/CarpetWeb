<template>
  <div class="hot-recommend">
    <div class="card-header">
      <el-button type="primary" @click="goToHot">热门推荐</el-button>
      <el-button text @click="goToMore">更多好物 &gt;</el-button>
    </div>

    <!-- 商品列表 -->
    <div v-loading="loading" class="product-row">
      <ProductCard
        v-for="product in products"
        :key="product.id"
        :product="product"
        class="product-card"
      />
    </div>

    <!-- 空状态 -->
    <el-empty v-if="!loading && products.length === 0" description="暂无热门商品" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ProductCard from '@/components/product/ProductCard.vue'
import { productApi, type Product } from '@/api/product'

const router = useRouter()
const loading = ref(false)
const products = ref<Product[]>([])

const fetchHotProducts = async () => {
  loading.value = true
  try {
    const res = await productApi.getHotProducts(4)
    products.value = res
  } catch (error) {
    ElMessage.error('获取热门推荐失败')
  } finally {
    loading.value = false
  }
}

const goToHot = () => {
  router.push({ path: '/products', query: { sort: 'sales' } })
}

const goToMore = () => {
  router.push('/products')
}

onMounted(() => {
  fetchHotProducts()
})
</script>

<style scoped>
.hot-recommend {
  margin: 30px 0;
  background-color: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
/* 商品行：减小间隙，更紧凑 */
.product-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px; /* 原20px，改为12px */
  justify-content: space-between;
}
/* 卡片基础样式 */
.product-card {
  flex: 0 0 calc((100% - 36px) / 4); /* 总间隙 gap * (4-1) = 36px */
  max-width: calc((100% - 36px) / 4);
}
/* 调整卡片内部图片高度和内边距 */
.product-card :deep(.el-card__body) {
  padding: 10px; /* 原12px，改为10px */
}
.product-card .image-container {
  height: 160px; /* 原200px，改为160px */
}
/* 商品标题和价格字体微调 */
.product-card .product-title {
  font-size: 15px; /* 原16px，稍微减小 */
  margin-bottom: 6px;
}
.product-card .price-number {
  font-size: 16px; /* 原18px */
}
/* 响应式：中等屏幕，一行2个 */
@media screen and (max-width: 900px) {
  .product-row {
    gap: 12px;
  }
  .product-card {
    flex: 0 0 calc((100% - 12px) / 2);
    max-width: calc((100% - 12px) / 2);
  }
}
/* 小屏幕：一行1个 */
@media screen and (max-width: 500px) {
  .product-card {
    flex: 0 0 100%;
    max-width: 100%;
  }
}
</style>