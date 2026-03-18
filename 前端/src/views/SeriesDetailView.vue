<template>
  <div class="series-detail">
    <!-- 面包屑导航 -->
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/products' }">产品中心</el-breadcrumb-item>
      <el-breadcrumb-item>{{ seriesInfo.name || '系列详情' }}</el-breadcrumb-item>
    </el-breadcrumb>

    <el-row :gutter="30">
      <!-- 左侧：系列信息卡片 -->
      <el-col :xs="24" :sm="24" :md="6" :lg="5">
        <el-card class="series-info-card" shadow="hover">
          <template v-slot:header>
<div  class="info-header">
            <h3>系列信息</h3>
          </div>
</template>
          <div class="info-content">
            <p><strong>系列名称：</strong>{{ seriesInfo.name }}</p>
            <p><strong>品牌：</strong>{{ seriesInfo.brandName }}</p>
            <p><strong>铺设方式：</strong>{{ seriesInfo.layType === 'full' ? '满铺毯' : '方块毯' }}</p>
            <p><strong>材质：</strong>{{ materialMap[seriesInfo.material] || seriesInfo.material }}</p>
            <p><strong>规格：</strong>{{ seriesInfo.spec }}</p>
            <!-- 系列描述暂时隐藏，可取消注释以显示 -->
            <!-- <p v-if="seriesInfo.description"><strong>描述：</strong>{{ seriesInfo.description }}</p> -->
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：色号网格 -->
      <el-col :xs="24" :sm="24" :md="18" :lg="19">
        <!-- 排序和统计 -->
        <div class="products-header">
          <span class="result-count">共 {{ total }} 个色号</span>
          <el-select v-model="sortBy" placeholder="排序" size="small" style="width: 150px;" @change="handleSortChange">
            <el-option label="默认" value="default" />
            <el-option label="价格低到高" value="price_asc" />
            <el-option label="价格高到低" value="price_desc" />
            <el-option label="色号" value="colorCode" />
          </el-select>
        </div>

        <!-- 商品网格 -->
        <div v-loading="loading" class="product-grid">
          <el-row :gutter="20">
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

        <!-- 空状态 -->
        <el-empty v-if="!loading && products.length === 0" description="该系列暂无色号" />

        <!-- 分页（当总页数大于1时显示） -->
        <el-pagination
          v-if="total > pageSize"
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[12, 24, 36]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          class="pagination"
        />
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ProductCard from '@/components/product/ProductCard.vue'
import { seriesApi, type Series } from '@/api/series'
import { productApi, type Product } from '@/api/product'

const route = useRoute()
const router = useRouter()

// 材质映射
const materialMap: Record<string, string> = {
  polypropylene: '丙纶',
  polyester: '涤纶',
  nylon: '尼龙'
}

// 系列ID
const seriesId = computed(() => route.params.id as string)

// 系列信息
const seriesInfo = ref<Series>({
  id: 0,
  brandId: 0,
  brandName: '',
  name: '',
  layType: 'full',
  material: '',
  spec: '',
  description: ''
})

// 色号列表
const loading = ref(false)
const products = ref<Product[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)
const sortBy = ref('default')

// 获取系列详情
const fetchSeriesDetail = async () => {
  if (!seriesId.value) return
  try {
    const res = await seriesApi.getDetail(Number(seriesId.value))
    seriesInfo.value = res
  } catch (error) {
    ElMessage.error('获取系列详情失败')
    router.push('/products')
  }
}

// 获取色号列表
const fetchProducts = async () => {
  if (!seriesId.value) return
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      sort: sortBy.value
    }
    const res = await productApi.getListBySeries(Number(seriesId.value), params)
    products.value = res.content || []
    total.value = res.totalElements || 0
  } catch (error) {
    ElMessage.error('获取色号列表失败')
    products.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 排序变化
const handleSortChange = () => {
  currentPage.value = 1
  fetchProducts()
  // 同步 URL query
  router.replace({ query: { sort: sortBy.value } })
}

// 分页变化
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchProducts()
  router.replace({ query: { ...route.query, size: String(size) } })
}
const handleCurrentChange = (page: number) => {
  currentPage.value = page
  fetchProducts()
  router.replace({ query: { ...route.query, page: String(page) } })
}

// 从 URL query 初始化
const initFromQuery = () => {
  const query = route.query
  if (query.sort) sortBy.value = query.sort as string
  if (query.page) currentPage.value = Number(query.page)
  if (query.size) pageSize.value = Number(query.size)
}

// 监听路由参数变化（系列ID变化）
watch(() => route.params.id, (newId, oldId) => {
  if (newId && newId !== oldId) {
    fetchSeriesDetail()
    currentPage.value = 1
    fetchProducts()
  }
})

// 初始化
onMounted(async () => {
  initFromQuery()
  await fetchSeriesDetail()
  fetchProducts()
})
</script>

<style scoped>
.series-detail {
  max-width: 1400px;
  margin: 30px auto;
  padding: 0 20px;
}
.breadcrumb {
  margin-bottom: 20px;
}
.series-info-card {
  margin-bottom: 20px;
}
.info-header {
  font-weight: 600;
  font-size: 16px;
}
.info-content p {
  margin: 8px 0;
  line-height: 1.6;
}
.products-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.result-count {
  font-size: 14px;
  color: #666;
}
.product-grid {
  margin-bottom: 20px;
  min-height: 400px;
}
.grid-item {
  margin-bottom: 20px;
}
.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: flex-end;
}
</style>