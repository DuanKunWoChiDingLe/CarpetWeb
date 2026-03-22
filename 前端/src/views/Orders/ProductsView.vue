<template>
  <div class="products-view">
    <!-- 面包屑导航 -->
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/products' }">产品中心</el-breadcrumb-item>
    </el-breadcrumb>

    <el-row :gutter="30">
      <!-- 左侧筛选栏 -->
      <el-col :xs="24" :sm="24" :md="6" :lg="5">
        <div class="filter-sidebar">
          <h3 class="filter-title">筛选</h3>

          <!-- 品牌（单选） -->
          <div class="filter-section">
            <h4>品牌</h4>
            <el-radio-group v-model="localFilter.brandName" @change="handleFilterChange">
              <el-radio value="">全部品牌</el-radio>
              <el-radio
                v-for="brand in brandList"
                :key="brand.id"
                :value="brand.name"
              >{{ brand.name }}</el-radio>
            </el-radio-group>
          </div>

          <!-- 铺设方式（单选） -->
          <div class="filter-section">
            <h4>铺设方式</h4>
            <el-radio-group v-model="localFilter.layType" @change="handleFilterChange">
              <el-radio value="">全部</el-radio>
              <el-radio value="full">满铺毯</el-radio>
              <el-radio value="modular">方块毯</el-radio>
            </el-radio-group>
          </div>

          <!-- 材质（单选） -->
          <div class="filter-section">
            <h4>材质</h4>
            <el-radio-group v-model="localFilter.material" @change="handleFilterChange">
              <el-radio value="">全部</el-radio>
              <el-radio value="polypropylene">丙纶</el-radio>
              <el-radio value="polyester">涤纶</el-radio>
              <el-radio value="nylon">尼龙</el-radio>
            </el-radio-group>
          </div>

          <!-- 关键词搜索 -->
          <div class="filter-section">
            <h4>系列名称</h4>
            <el-input
              v-model="localFilter.keyword"
              placeholder="输入系列名称"
              clearable
              @change="handleFilterChange"
            />
          </div>

          <!-- 已选条件数量和重置 -->
          <div class="filter-actions">
            <span class="filter-count">已选 {{ filterCount }} 项</span>
            <el-button link @click="resetFilter">重置</el-button>
          </div>
        </div>
      </el-col>

      <!-- 右侧系列网格 -->
      <el-col :xs="24" :sm="24" :md="18" :lg="19">
        <div class="products-header">
          <span class="result-count">共 {{ total }} 个系列</span>
          <el-select v-model="localSort" placeholder="排序" size="small" @change="handleSortChange">
            <el-option label="默认" value="default" />
            <el-option label="系列名" value="name_asc" />
            <el-option label="新品" value="newest" />
          </el-select>
        </div>

        <!-- 系列卡片列表 -->
        <div v-loading="loading" class="series-list">
          <!-- 空状态提示：当系列列表为空且不在加载状态时显示 -->
          <el-empty
            v-if="!loading && seriesList.length === 0"
            description="没有找到符合条件的系列"
          />

          <div
            v-for="series in seriesList"
            :key="series.id"
            class="series-card-wrapper"
          >
            <el-card class="series-card" shadow="hover">
              <template #header>
                <div class="series-header">
                  <div class="series-title">
                    <h3>{{ series.name }}</h3>
                    <span class="series-brand">{{ series.brandName }}</span>
                  </div>
                  <div class="series-meta">
                    <span>{{ series.layType === 'full' ? '满铺毯' : '方块毯' }}</span> ·
                    <span>{{ materialMap[series.material] }}</span> ·
                    <span>{{ series.spec }}</span>
                  </div>
                  <el-button
                    type="primary"
                    link
                    @click="goToSeriesDetail(series.id)"
                  >
                    查看全部 {{ series.colorCount }} 个色号 >
                  </el-button>
                </div>
              </template>

              <!-- 系列内的色号小卡片 -->
              <el-row :gutter="20" v-if="series.products.length > 0">
                <el-col
                  v-for="product in series.products"
                  :key="product.id"
                  :xs="12" :sm="8" :md="6" :lg="6"
                >
                  <ProductCard :product="product" />
                </el-col>
              </el-row>

              <!-- 当系列色号数量为0时，显示提示 -->
              <el-empty
                v-else
                description="该系列暂无色号"
                :image-size="80"
              />

              <!-- 当色号数量超过4个时，显示“更多”提示 -->
              <div v-if="series.colorCount > 4" class="more-hint">
                <el-button type="primary" link @click="goToSeriesDetail(series.id)">
                  还有 {{ series.colorCount - 4 }} 个色号，点击查看全部 >
                </el-button>
              </div>
            </el-card>
          </div>
        </div>

        <!-- 底部哨兵（无限滚动触发） -->
        <div ref="sentinel" class="sentinel"></div>

        <!-- 加载状态（由 v-loading 控制，这里不再需要额外的骨架屏） -->
        <div v-if="loading && seriesList.length === 0" class="loading-indicator">
          <el-skeleton :rows="3" animated />
        </div>
        <div v-if="!loading && !hasMore && seriesList.length > 0" class="no-more">
          没有更多系列了
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ProductCard from '@/components/product/ProductCard.vue'
import { brandApi, type Brand } from '@/api/brand'
import { seriesApi, type Series, type SeriesQueryParams } from '@/api/series'
import { productApi, type Product } from '@/api/product'

const route = useRoute()
const router = useRouter()

// 材质映射
const materialMap: Record<string, string> = {
  polypropylene: '丙纶',
  polyester: '涤纶',
  nylon: '尼龙'
}

// ---------- 品牌列表 ----------
const brandList = ref<Brand[]>([])
const fetchBrands = async () => {
  try {
    const res = await brandApi.getAll()
    brandList.value = res
  } catch (error) {
    console.log(error);
    
    // 错误已由拦截器处理
  }
}

// ---------- 筛选条件 ----------
const localFilter = reactive({
  brandName: '',
  layType: '',
  material: '',
  keyword: ''
})

// 计算已选条件数量
const filterCount = computed(() => {
  let count = 0
  if (localFilter.brandName) count++
  if (localFilter.layType) count++
  if (localFilter.material) count++
  if (localFilter.keyword) count++
  return count
})

// 排序
const localSort = ref('default')

// 分页
const currentPage = ref(1)
const pageSize = ref(10) // 每页系列数量
const total = ref(0)
const hasMore = ref(true)
const loading = ref(false)

// 系列列表（每个系列附加 products 字段，存放预览色号）
interface SeriesWithProducts extends Series {
  products: Product[]
}
const seriesList = ref<SeriesWithProducts[]>([])

// 将品牌名称转换为品牌ID
const getBrandIdByName = (name: string): number | undefined => {
  const brand = brandList.value.find(b => b.name === name)
  return brand?.id
}

// 获取系列列表及每个系列的前4个色号
const fetchSeries = async (reset = false) => {
  if (loading.value) return
  if (!reset && !hasMore.value) return

  loading.value = true
  try {
    const params: SeriesQueryParams = {
      page: currentPage.value - 1,
      size: pageSize.value,
      keyword: localFilter.keyword || undefined,
      layType: localFilter.layType || undefined,
      material: localFilter.material || undefined,
      brandId: getBrandIdByName(localFilter.brandName),
      sort: localSort.value !== 'default' ? localSort.value : undefined
    }

    const res = await seriesApi.getPage(params)
    let newSeries = res.content

    // 过滤掉没有色号的系列
    newSeries = newSeries.filter(series => series.colorCount != null && series.colorCount > 0)

    // 如果当前页过滤后为空，且还有更多数据，则递归加载下一页
    if (newSeries.length === 0 && res.number < res.totalPages - 1) {
      currentPage.value++
      loading.value = false
      await fetchSeries(reset)
      return
    }

    // 为每个系列获取前4个色号（并行请求），并补充品牌、类型和销量字段
    const seriesWithProducts = await Promise.all(
  newSeries.map(async (series) => {
    try {
      const productRes = await productApi.getListBySeries(series.id, {
        page: 0,
        size: 4,
        sort: 'default'
      })
      const products = productRes.content.map(product => ({
        ...product,
        brand: series.brandName ?? '',                         // 补充品牌（后备空字符串）
        type: series.layType === 'full' ? '满铺毯' : '方块毯',   // 补充类型（中文）
        sales: 0                                                // 补充销量（默认0）
      }))
      return { ...series, products }
    } catch (error) {
      console.error(`获取系列 ${series.id} 的色号失败`, error)
      return { ...series, products: [] }
    }
  })
)


    if (reset) {
      seriesList.value = seriesWithProducts
      currentPage.value = 1
    } else {
      seriesList.value.push(...seriesWithProducts)
      currentPage.value++
    }
    total.value = res.totalElements
    hasMore.value = res.number < res.totalPages - 1
  } catch (error) {
    console.error('获取系列列表失败', error)
    ElMessage.error('获取系列列表失败')
  } finally {
    loading.value = false
  }
}

// 重置筛选
const resetFilter = () => {
  localFilter.brandName = ''
  localFilter.layType = ''
  localFilter.material = ''
  localFilter.keyword = ''
  localSort.value = 'default'
  currentPage.value = 1
  hasMore.value = true
  fetchSeries(true)
}

// 防抖处理筛选变化
let filterTimer: ReturnType<typeof setTimeout> | undefined
const handleFilterChange = () => {
  if (filterTimer) clearTimeout(filterTimer)
  filterTimer = setTimeout(() => {
    currentPage.value = 1
    hasMore.value = true
    fetchSeries(true)
    // 更新 URL query（可选）
    const query: Record<string, string> = {}
    if (localFilter.brandName) query.brand = localFilter.brandName
    if (localFilter.layType) query.layType = localFilter.layType
    if (localFilter.material) query.material = localFilter.material
    if (localFilter.keyword) query.keyword = localFilter.keyword
    if (localSort.value !== 'default') query.sort = localSort.value
    router.replace({ query })
  }, 500)
}

const handleSortChange = () => {
  handleFilterChange()
}

// 跳转到系列详情页
const goToSeriesDetail = (seriesId: number) => {
  router.push(`/series/${seriesId}`)
}

// 无限滚动哨兵
const sentinel = ref<HTMLElement | null>(null)
let observer: IntersectionObserver | null = null

// 从 URL query 初始化
const initFromQuery = () => {
  const query = route.query
  if (query.brand) localFilter.brandName = query.brand as string
  if (query.layType) localFilter.layType = query.layType as string
  if (query.material) localFilter.material = query.material as string
  if (query.keyword) localFilter.keyword = query.keyword as string
  if (query.sort) localSort.value = query.sort as string
}

// 初始化
onMounted(async () => {
  await fetchBrands()
  initFromQuery()
  fetchSeries(true)

  observer = new IntersectionObserver(
    (entries) => {
      if (entries[0]?.isIntersecting && !loading.value && hasMore.value) {
        fetchSeries()
      }
    },
    { threshold: 0, rootMargin: '0px 0px 200px 0px' }
  )
  if (sentinel.value) observer.observe(sentinel.value)
})

onUnmounted(() => {
  if (observer) observer.disconnect()
})

// 监听筛选和排序变化（防抖已在 handleFilterChange 中处理）
</script>

<style scoped>
/* 原有样式保持不变，此处略去以节省篇幅，请保留之前的样式 */
.products-view {
  max-width: 1400px;
  margin: 30px auto;
  padding: 0 20px;
}
.breadcrumb {
  margin-bottom: 20px;
}
.filter-sidebar {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}
.filter-title {
  font-size: 18px;
  margin-top: 0;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}
.filter-section {
  margin-bottom: 25px;
}
.filter-section h4 {
  font-size: 16px;
  margin-bottom: 12px;
  color: #333;
}
.filter-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}
.filter-count {
  color: #666;
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
.series-list {
  margin-bottom: 20px;
}
.series-card-wrapper {
  margin-bottom: 30px;
}
.series-card :deep(.el-card__header) {
  padding: 15px 20px;
}
.series-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 10px;
}
.series-title {
  display: flex;
  align-items: center;
  gap: 10px;
}
.series-title h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}
.series-brand {
  font-size: 14px;
  color: #666;
  background: #f0f0f0;
  padding: 2px 8px;
  border-radius: 4px;
}
.series-meta {
  font-size: 14px;
  color: #999;
}
.more-hint {
  margin-top: 15px;
  text-align: center;
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