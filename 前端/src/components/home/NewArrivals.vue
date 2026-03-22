
<template>
  <div class="new-arrivals">
    <h2 class="section-title">最新地毯</h2>
    <div v-loading="loading && page === 1" class="product-grid">
      <el-row :gutter="30">
        <el-col
          v-for="product in products"
          :key="product.id"
          :xs="12" :sm="8" :md="6" :lg="6"
          class="grid-item"
        >
          <ProductCard :product="product" />
        </el-col>
      </el-row>
    </div>
    <div ref="sentinel" class="sentinel"></div>
    <div v-if="loading && page > 1" class="loading-indicator">加载中...</div>
    <div v-if="!hasMore" class="no-more">没有更多商品了</div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import ProductCard from '@/components/product/ProductCard.vue'
import { productApi, type Product } from '@/api/product'
import { ElMessage } from 'element-plus'

const products = ref<Product[]>([])
const page = ref(1)
const pageSize = 8
const loading = ref(false)
const hasMore = ref(true)

const sentinel = ref<HTMLElement | null>(null)
let observer: IntersectionObserver | null = null

const fetchLatestProducts = async (reset = false) => {
  if (loading.value || (!reset && !hasMore.value)) return
  loading.value = true
  try {
    const params = {
      page: page.value - 1,
      size: pageSize,
    }
    const res = await productApi.getLatestProducts(params)
    const newProducts = res.content

    if (reset) {
      // 重置时直接替换
      products.value = newProducts
      page.value = 1
    } else {
      // 合并前先去重，防止因分页边界数据重复导致同一商品出现多次
      const existingIds = new Set(products.value.map(p => p.id))
      const uniqueNewProducts = newProducts.filter(p => !existingIds.has(p.id))
      products.value.push(...uniqueNewProducts)
      page.value++
    }

    // 根据总数判断是否还有更多
    hasMore.value = products.value.length < res.totalElements
  } catch (error) {
    ElMessage.error('获取最新商品失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchLatestProducts(true)

  observer = new IntersectionObserver(
    (entries) => {
      if (entries[0]?.isIntersecting && !loading.value && hasMore.value) {
        fetchLatestProducts()
      }
    },
    { threshold: 0, rootMargin: '0px 0px 200px 0px' }
  )
  if (sentinel.value) observer.observe(sentinel.value)
})

onUnmounted(() => {
  if (observer) observer.disconnect()
})
</script>

<style scoped>
.new-arrivals {
  margin-top: 30px;
}
.section-title {
  font-size: 22px;
  font-weight: 500;
  margin-bottom: 20px;
  color: #333;
}
.grid-item {
  margin-bottom: 30px;
}
.sentinel {
  height: 1px;
}
.loading-indicator,
.no-more {
  text-align: center;
  padding: 20px;
  color: #999;
}
</style>
