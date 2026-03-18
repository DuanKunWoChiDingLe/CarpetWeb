<template>
  <div class="merchant-orders">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/merchant' }">商家中心</el-breadcrumb-item>
      <el-breadcrumb-item>订单管理</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 筛选区 -->
    <el-card class="filter-card">
      <el-row :gutter="20" align="middle">
        <el-col :span="4">
          <el-select v-model="filter.status" placeholder="订单状态" clearable>
            <el-option label="待付款" value="pending" />
            <el-option label="已付款" value="paid" />
            <el-option label="已发货" value="shipped" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-input v-model="filter.keyword" placeholder="订单号/用户名" clearable />
        </el-col>
        <el-col :span="6">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 订单表格 -->
    <el-table :data="orders" v-loading="loading" border stripe>
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="userName" label="用户" width="120" />
      <el-table-column prop="totalAmount" label="金额" width="120">
        <template #default="{ row }">¥{{ row.totalAmount.toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="下单时间" width="180">
        <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="200">
        <template #default="{ row }">
          <el-button link type="primary" @click="viewDetail(row)">查看详情</el-button>
          <el-button
            v-if="row.status === 'paid'"
            link
            type="success"
            @click="handleShip(row)"
          >发货</el-button>
          <el-button
            v-if="row.status === 'pending'"
            link
            type="danger"
            @click="handleCancel(row)"
          >取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      class="pagination"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi, type Order } from '@/api/order'

const router = useRouter()

const loading = ref(false)
const orders = ref<Order[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const filter = reactive({
  status: '',
  keyword: ''
})
const dateRange = ref<string[]>([])

const statusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待付款',
    paid: '已付款',
    shipped: '已发货',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[status] || status
}

const statusTagType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'info',
    paid: 'success',
    shipped: 'warning',
    completed: '',
    cancelled: 'danger'
  }
  return map[status] || 'info'
}

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const params: any = {
      page: currentPage.value - 1,
      size: pageSize.value,
      status: filter.status || undefined,
      keyword: filter.keyword || undefined,
      startDate: dateRange.value?.[0],
      endDate: dateRange.value?.[1]
    }
    const res = await orderApi.getAllOrders(params)
    orders.value = res.content
    total.value = res.totalElements
  } catch (error) {
    ElMessage.error('获取订单失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchOrders()
}

const resetFilter = () => {
  filter.status = ''
  filter.keyword = ''
  dateRange.value = []
  currentPage.value = 1
  fetchOrders()
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchOrders()
}
const handleCurrentChange = (page: number) => {
  currentPage.value = page
  fetchOrders()
}

const viewDetail = (order: Order) => {
  router.push(`/merchant/orders/${order.id}`)
}

const handleShip = (order: Order) => {
  ElMessageBox.confirm('确定要发货吗？', '提示', { type: 'info' })
    .then(async () => {
      await orderApi.ship(order.id)
      ElMessage.success('发货成功')
      fetchOrders()
    })
    .catch(() => {})
}

const handleCancel = (order: Order) => {
  ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
    .then(async () => {
      await orderApi.merchantCancel(order.id)
      ElMessage.success('订单已取消')
      fetchOrders()
    })
    .catch(() => {})
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.merchant-orders {
  max-width: 1400px;
  margin: 30px auto;
  padding: 0 20px;
}
.breadcrumb { margin-bottom: 20px; }
.filter-card { margin-bottom: 20px; padding: 20px 20px 10px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>