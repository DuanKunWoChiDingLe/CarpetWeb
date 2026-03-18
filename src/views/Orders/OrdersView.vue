<template>
  <div class="orders-view">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>我的订单</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 状态筛选 tabs -->
    <el-tabs v-model="activeStatus" @tab-click="handleTabClick">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="待付款" name="pending" />
      <el-tab-pane label="待发货" name="paid" />
      <el-tab-pane label="待收货" name="shipped" />
      <el-tab-pane label="已完成" name="completed" />
      <el-tab-pane label="已取消" name="cancelled" />
    </el-tabs>

    <!-- 订单列表 -->
    <div v-loading="loading" class="order-list">
      <el-card v-for="order in orders" :key="order.id" class="order-card" shadow="hover">
        <div class="order-header">
          <span class="order-no">订单号：{{ order.orderNo }}</span>
          <span class="order-time">{{ formatDate(order.createdAt) }}</span>
          <el-tag :type="statusTagType(order.status)" class="order-status">
            {{ statusText(order.status) }}
          </el-tag>
        </div>
        <div class="order-items">
          <div v-for="item in order.items || []" :key="item.id" class="order-item">
            <el-image :src="getImageUrl(item.productImage)" class="item-image" fit="cover">
  <template #error>
    <div class="image-error">图片加载失败</div>
  </template>
</el-image>
            <div class="item-info">
              <div class="item-name">{{ item.productName }}</div>
              <div class="item-price">¥{{ item.price.toFixed(2) }} × {{ item.quantity }}</div>
            </div>
          </div>
        </div>
        <div class="order-footer">
          <span class="order-total">
            共 {{ order.items?.length || [] }} 件商品 合计：
            <span class="total-price">¥{{ order.totalAmount.toFixed(2) }}</span>
          </span>
          <div class="order-actions">
            <el-button size="small" @click="viewDetail(order)">查看详情</el-button>
            <el-button
              v-if="order.status === 'pending'"
              type="primary"
              size="small"
              @click="handlePay(order)"
            >去支付</el-button>
            <el-button
              v-if="order.status === 'pending'"
              type="danger"
              size="small"
              @click="handleCancel(order)"
            >取消订单</el-button>
            <el-button
              v-if="order.status === 'shipped'"
              type="success"
              size="small"
              @click="handleConfirm(order)"
            >确认收货</el-button>
          </div>
        </div>
      </el-card>

      <el-empty v-if="!loading && orders.length === 0" description="暂无订单" />
    </div>

    <!-- 分页 -->
    <el-pagination
      v-if="total > pageSize"
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[5, 10, 20]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      class="pagination"
    />

    <!-- 支付弹窗 -->
    <PayDialog
      ref="payDialogRef"
      :order-id="currentOrderId"
      :order-no="currentOrderNo"
      @success="handlePaySuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi, type Order } from '@/api/order'
import PayDialog from '@/components/order/PayDialog.vue'

const router = useRouter()
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const getImageUrl = (path?: string) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return baseURL + path
}

// 状态筛选
const activeStatus = ref('all')
const loading = ref(false)
const orders = ref<Order[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 当前支付的订单
const payDialogRef = ref<InstanceType<typeof PayDialog>>()
const currentOrderId = ref(0)
const currentOrderNo = ref('')

// 状态映射
const statusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待付款',
    paid: '待发货',
    shipped: '待收货',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[status] || status
}

const statusTagType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'danger',
    paid: 'warning',
    shipped: 'primary',
    completed: 'success',
    cancelled: 'info'
  }
  return map[status] || ''
}

// 格式化日期
const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 获取订单列表
const fetchOrders = async () => {
  loading.value = true
  try {
    const params: any = {
      page: currentPage.value - 1,
      size: pageSize.value
    }
    if (activeStatus.value !== 'all') {
      params.status = activeStatus.value
    }
    const res = await orderApi.getMyOrders(params)
    res.content = res.content.map(order => ({
  ...order,
  items: order.items || []
}))
    orders.value = res.content
    total.value = res.totalElements
  } catch (error) {
    ElMessage.error('获取订单失败')
  } finally {
    loading.value = false
  }
}

// 切换标签
const handleTabClick = () => {
  currentPage.value = 1
  fetchOrders()
}

// 分页变化
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchOrders()
}
const handleCurrentChange = (page: number) => {
  currentPage.value = page
  fetchOrders()
}

// 查看详情
const viewDetail = (order: Order) => {
  router.push(`/order/${order.id}`)
}

// 去支付
const handlePay = (order: Order) => {
  currentOrderId.value = order.id
  currentOrderNo.value = order.orderNo
  payDialogRef.value?.open()
}

// 支付成功回调
const handlePaySuccess = () => {
  ElMessage.success('支付成功')
  fetchOrders()
}

// 取消订单
const handleCancel = (order: Order) => {
  ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
    .then(async () => {
      await orderApi.cancel(order.id)
      ElMessage.success('订单已取消')
      fetchOrders()
    })
    .catch(() => {})
}

// 确认收货
const handleConfirm = (order: Order) => {
  ElMessageBox.confirm('确认收到商品了吗？', '提示', { type: 'info' })
    .then(async () => {
      await orderApi.confirm(order.id)
      ElMessage.success('交易完成')
      fetchOrders()
    })
    .catch(() => {})
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.orders-view {
  max-width: 1000px;
  margin: 30px auto;
  padding: 0 20px;
}
.breadcrumb { margin-bottom: 20px; }
.order-list { margin-top: 20px; }
.order-card { margin-bottom: 20px; }
.order-header {
  display: flex;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
  font-size: 14px;
}
.order-no { flex: 1; font-weight: 500; }
.order-time { color: #999; margin-right: 20px; }
.order-items { padding: 15px 0; }
.order-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.item-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  margin-right: 15px;
}
.item-info {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.item-name { font-size: 16px; }
.item-price { color: #f56c6c; }
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #eee;
  padding-top: 15px;
}
.order-total { font-size: 14px; color: #666; }
.total-price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
  margin-left: 5px;
}
.order-actions { display: flex; gap: 10px; }
.pagination { margin-top: 30px; display: flex; justify-content: flex-end; }
</style>