<template>
  <div class="dashboard-overview">
    <div class="cards-container">
      <!-- 卡片1：数据（销量趋势折线图） -->
      <el-card class="dashboard-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>近7天销量趋势</span>
          </div>
        </template>
        <div class="card-content" v-loading="trendLoading">
          <SalesTrendChart :data="trendData" style="height: 200px;" />
        </div>
      </el-card>

      <!-- 卡片2：轮播图管理 -->
      <el-card class="dashboard-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>轮播图管理</span>
            <el-button link @click="goToCarousel">管理</el-button>
          </div>
        </template>
        <div class="card-content">
          <p>当前轮播图数量：{{ carouselCount }}</p>
          <p>最后更新：{{ lastCarouselUpdate }}</p>
        </div>
      </el-card>

      <!-- 卡片3：销量统计 -->
      <el-card class="dashboard-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>销量统计</span>
            <el-button link @click="goToSales">详情</el-button>
          </div>
        </template>
        <div class="card-content">
          <p>今日销量：{{ todaySales }} 元</p>
          <p>本月销量：{{ monthSales }} 元</p>
        </div>
      </el-card>

      <!-- 卡片4：报表 -->
      <el-card class="dashboard-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>报表</span>
            <el-button link @click="goToReports">查看</el-button>
          </div>
        </template>
        <div class="card-content">
          <div v-for="report in recentReports" :key="report.id" class="report-item">
            <span>{{ report.name }}</span>
            <span>{{ report.date }}</span>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { statsApi } from '@/api/stats'
import SalesTrendChart from '@/components/dashboard/SalesTrendChart.vue'

const router = useRouter()

// 销量趋势数据
const trendLoading = ref(false)
const trendData = ref({ dates: [], salesAmount: [] })

// 轮播图模拟数据
const carouselCount = ref(5)
const lastCarouselUpdate = ref('2026-03-07')

// 销量数据
const todaySales = ref(0)
const monthSales = ref(0)

// 报表模拟数据
const recentReports = ref([
  { id: 1, name: '2026-03-05 销售报表', date: '2026-03-05' },
  { id: 2, name: '2026-03-06 流量报表', date: '2026-03-06' },
  { id: 3, name: '本周热门商品', date: '2026-03-07' }
])

// 跳转函数
const goToCarousel = () => router.push('/merchant/dashboard/carousel')
const goToSales = () => router.push('/merchant/dashboard/sales')
const goToReports = () => router.push('/merchant/dashboard/reports')

// 获取销量概览（今日/本月）
const fetchOverview = async () => {
  try {
    const data = await statsApi.getOverview()
    todaySales.value = data.today.salesAmount
    monthSales.value = data.month.salesAmount
  } catch (error) {
    ElMessage.error('获取销量概览失败')
  }
}

// 获取近7天趋势
const fetchTrend = async () => {
  trendLoading.value = true
  try {
    const data = await statsApi.getTrend({ interval: 'day' })
    trendData.value = {
      dates: data.dates,
      salesAmount: data.salesAmount
    }
  } catch (error) {
    ElMessage.error('获取销量趋势失败')
  } finally {
    trendLoading.value = false
  }
}

onMounted(() => {
  fetchOverview()
  fetchTrend()
})
</script>

<style scoped>
.dashboard-overview {
  width: 100%;
}
.cards-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.dashboard-card {
  width: 100%;
  border-radius: 8px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-header span {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}
.card-content {
  padding: 15px;
  min-height: 100px;
  background-color: #fafafa;
  border-radius: 4px;
}
.card-content p {
  margin: 8px 0;
  color: #666;
}
.report-item {
  display: flex;
  justify-content: space-between;
  padding: 5px 0;
  border-bottom: 1px solid #f0f0f0;
}
.report-item:last-child {
  border-bottom: none;
}
</style>