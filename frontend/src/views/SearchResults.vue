<template>
  <div class="search-results">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>搜索 "{{ keyword }}"</el-breadcrumb-item>
    </el-breadcrumb>

    <div class="results-header">
      <span class="result-count">共 {{ total }} 件商品</span>
    </div>

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

    <el-empty v-if="!loading && products.length === 0" description="没有找到相关商品" />

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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ProductCard from '@/components/product/ProductCard.vue'
import { productApi, type Product } from '@/api/product'

const route = useRoute()
const router = useRouter()

const keyword = ref('')
const products = ref<Product[]>([])
const total = ref(0)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(12)

const fetchResults = async () => {
  const kw = route.query.keyword as string
  if (!kw) {
    router.push('/')
    return
  }
  keyword.value = kw
  loading.value = true
  try {
    const res = await productApi.search({
      keyword: kw,
      page: currentPage.value - 1,
      size: pageSize.value,
    })
    products.value = res.content
    total.value = res.totalElements
  } catch (error) {
    ElMessage.error('搜索失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchResults()
}
const handleCurrentChange = (page: number) => {
  currentPage.value = page
  fetchResults()
}

onMounted(() => {
  fetchResults()
})

watch(() => route.query.keyword, () => {
  currentPage.value = 1
  fetchResults()
})
</script>

<style scoped>
.search-results {
  max-width: 1400px;
  margin: 30px auto;
  padding: 0 20px;
}
.breadcrumb {
  margin-bottom: 20px;
}
.results-header {
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