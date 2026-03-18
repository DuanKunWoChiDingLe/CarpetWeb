<template>
  <div class="sales-stats">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/merchant/dashboard' }">控制台</el-breadcrumb-item>
      <el-breadcrumb-item>销量统计</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 顶部指标卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">今日销量</div>
            <div class="stat-value">¥{{ overview?.today?.salesAmount?.toFixed(2) ?? '0.00' }}</div>
            <div class="stat-sub">订单数：{{ overview.today.orderCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">本月销量</div>
            <div class="stat-value">¥{{ overview?.today?.salesAmount?.toFixed(2) ?? '0.00' }}</div>
            <div class="stat-sub">订单数：{{ overview.month.orderCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-label">累计销量</div>
            <div class="stat-value">¥{{ overview?.today?.salesAmount?.toFixed(2) ?? '0.00' }}</div>
            <div class="stat-sub">订单数：{{ overview.total.orderCount }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 销量趋势图 -->
    <el-card class="chart-card" shadow="hover">
      <template #header>
        <span>近7天销量趋势</span>
      </template>
      <div ref="trendChartRef" style="height: 300px;"></div>
    </el-card>

    <el-row :gutter="20">
      <!-- 系列销量占比 -->
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <span>系列销量占比</span>
          </template>
          <div ref="seriesChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <!-- 品牌销量对比 -->
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <span>品牌销量对比</span>
          </template>
          <div ref="brandChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 热销商品排行 -->
    <el-card class="table-card" shadow="hover">
      <template #header>
        <span>热销商品排行</span>
      </template>
      <el-table :data="topProducts" border stripe v-loading="loading">
        <el-table-column prop="productName" label="商品名称" min-width="200" />
        <el-table-column prop="salesAmount" label="销售额(元)" width="150" align="right">
          <template #default="{ row }">¥{{ row.salesAmount.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="salesVolume" label="销量(m²)" width="120" align="right">
          <template #default="{ row }">{{ row.salesVolume.toFixed(2) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { statsApi, type SalesOverview, type SeriesSales, type BrandSales, type TopProduct } from '@/api/stats'

// 加载状态
const loading = ref(false)

// 概览数据
const overview = ref<SalesOverview>({
  today: { salesAmount: 0, orderCount: 0, salesVolume: 0 },
  month: { salesAmount: 0, orderCount: 0, salesVolume: 0 },
  total: { salesAmount: 0, orderCount: 0, salesVolume: 0 }
})

// 图表容器引用
const trendChartRef = ref<HTMLElement>()
const seriesChartRef = ref<HTMLElement>()
const brandChartRef = ref<HTMLElement>()
let trendChart: echarts.ECharts | null = null
let seriesChart: echarts.ECharts | null = null
let brandChart: echarts.ECharts | null = null

// 热销商品数据
const topProducts = ref<TopProduct[]>([])

// 获取概览数据
const fetchOverview = async () => {
  try {
    const data = await statsApi.getOverview()
    overview.value = {
      today: Object.assign({ salesAmount: 0, orderCount: 0, salesVolume: 0, ...data.today }),
      month:  Object.assign({ salesAmount: 0, orderCount: 0, salesVolume: 0, ...data.month }),
      total:  Object.assign({ salesAmount: 0, orderCount: 0, salesVolume: 0, ...data.total })
    }
  } catch (error) {
    ElMessage.error('获取概览数据失败')
  }
}

// 获取趋势数据
const fetchTrend = async () => {
  try {
    const data = await statsApi.getTrend({ interval: 'day' })
    renderTrendChart(data)
  } catch (error) {
    ElMessage.error('获取趋势数据失败')
  }
}

// 获取系列占比
const fetchSeries = async () => {
  try {
    const data = await statsApi.getSalesBySeries({ type: 'amount' })
    renderSeriesChart(data)
  } catch (error) {
    ElMessage.error('获取系列占比失败')
  }
}

// 获取品牌对比
const fetchBrands = async () => {
  try {
    const data = await statsApi.getSalesByBrand({ type: 'amount' })
    renderBrandChart(data)
  } catch (error) {
    ElMessage.error('获取品牌对比失败')
  }
}

// 获取热销商品
const fetchTopProducts = async () => {
  try {
    const data = await statsApi.getTopProducts({ limit: 10 })
    topProducts.value = data
  } catch (error) {
    ElMessage.error('获取热销商品失败')
  }
}

// 渲染趋势图
const renderTrendChart = (data: any) => {
  if (!trendChartRef.value) return
  if (!trendChart) {
    trendChart = echarts.init(trendChartRef.value)
  }
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: data.dates },
    yAxis: { type: 'value', name: '金额(元)' },
    series: [
      {
        name: '销售额',
        type: 'line',
        data: data.salesAmount,
        smooth: true,
        lineStyle: { color: '#409EFF' }
      }
    ]
  })
}

// 渲染系列饼图
const renderSeriesChart = (data: SeriesSales[]) => {
  if (!seriesChartRef.value) return
  if (!seriesChart) {
    seriesChart = echarts.init(seriesChartRef.value)
  }
  seriesChart.setOption({
    tooltip: { trigger: 'item' },
    series: [
      {
        name: '系列销量',
        type: 'pie',
        radius: '50%',
        data: data.map(item => ({ name: item.seriesName, value: item.value })),
        emphasis: { itemStyle: { shadowBlur: 10 } }
      }
    ]
  })
}

// 渲染品牌柱状图
const renderBrandChart = (data: BrandSales[]) => {
  if (!brandChartRef.value) return
  if (!brandChart) {
    brandChart = echarts.init(brandChartRef.value)
  }
  brandChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: data.map(item => item.brandName) },
    yAxis: { type: 'value', name: '销售额(元)' },
    series: [
      {
        name: '品牌销售额',
        type: 'bar',
        data: data.map(item => item.value),
        barWidth: '40%',
        itemStyle: { color: '#67c23a' }
      }
    ]
  })
}

// 窗口大小变化自适应
const handleResize = () => {
  trendChart?.resize()
  seriesChart?.resize()
  brandChart?.resize()
}

onMounted(async () => {
  loading.value = true
  await Promise.all([
    fetchOverview(),
    fetchTrend(),
    fetchSeries(),
    fetchBrands(),
    fetchTopProducts()
  ])
  loading.value = false
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  seriesChart?.dispose()
  brandChart?.dispose()
})
</script>

<style scoped>
.sales-stats {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}
.breadcrumb {
  margin-bottom: 20px;
}
.stat-cards {
  margin-bottom: 20px;
}
.stat-item {
  text-align: center;
}
.stat-label {
  font-size: 14px;
  color: #999;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
  margin: 8px 0;
}
.stat-sub {
  font-size: 12px;
  color: #666;
}
.chart-card {
  margin-bottom: 20px;
}
.table-card {
  margin-top: 20px;
}
</style>