<template>
  <div class="merchant-order-detail">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/merchant' }">商家中心</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/merchant/orders' }">订单管理</el-breadcrumb-item>
      <el-breadcrumb-item>订单详情</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card v-loading="loading" class="detail-card">
      <div v-if="order">
        <!-- 订单头部 -->
        <div class="order-header">
          <h2>订单号：{{ order.orderNo }}</h2>
          <el-tag :type="statusTagType(order.status)" size="large">
            {{ statusText(order.status) }}
          </el-tag>
        </div>

        <el-divider />

        <!-- 基本信息 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="info-group">
              <h3>收货信息</h3>
              <p><span class="label">收货人：</span>{{ order.consignee }}</p>
              <p><span class="label">联系电话：</span>{{ order.phone }}</p>
              <p><span class="label">收货地址：</span>{{ order.address }}</p>
              <p v-if="order.remark"><span class="label">买家备注：</span>{{ order.remark }}</p>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-group">
              <h3>订单信息</h3>
              <p><span class="label">下单用户：</span>{{ order.userName }}</p>
              <p><span class="label">下单时间：</span>{{ formatDateTime(order.createdAt) }}</p>
              <p v-if="order.paidAt"><span class="label">支付时间：</span>{{ formatDateTime(order.paidAt) }}</p>
              <p><span class="label">订单金额：</span><span class="total-price">¥{{ order.totalAmount.toFixed(2) }}</span></p>
            </div>
          </el-col>
        </el-row>

        <el-divider />

        <!-- 商品清单 -->
        <div class="info-group">
          <h3>商品清单</h3>
          <el-table :data="order.items" border style="width: 100%">
            <el-table-column label="商品" width="80">
              <template #default="{ row }">
                <el-image :src="getImageUrl(row.productImage)" style="width: 50px; height: 50px;" fit="cover">
                  <template #error>
                    <div class="image-error">加载失败</div>
                  </template>
                </el-image>
              </template>
            </el-table-column>
            <el-table-column prop="productName" label="商品名称" min-width="150" />
            <el-table-column prop="quantity" label="数量" width="80" align="center" />
            <el-table-column prop="price" label="单价" width="120" align="right">
              <template #default="{ row }">¥{{ row.price.toFixed(2) }}</template>
            </el-table-column>
            <el-table-column label="小计" width="120" align="right">
              <template #default="{ row }">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button
            v-if="order.status === 'paid'"
            type="success"
            size="large"
            @click="handleShip"
          >发货</el-button>
          <el-button
            v-if="order.status === 'pending'"
            type="danger"
            size="large"
            @click="handleCancel"
          >取消订单</el-button>
          <el-button @click="router.back()">返回</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi, type Order } from '@/api/order'

const route = useRoute()
const router = useRouter()
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const loading = ref(false)
const order = ref<Order>()

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

const formatDateTime = (dateStr: string) => {
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const getImageUrl = (path?: string) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return baseURL + path
}

const fetchDetail = async () => {
  const id = route.params.id as string
  if (!id) return
  loading.value = true
  try {
    const res = await orderApi.getMerchantOrderDetail(Number(id))
    order.value = res
  } catch (error) {
    ElMessage.error('获取订单详情失败')
    router.push('/merchant/orders')
  } finally {
    loading.value = false
  }
}

const handleShip = () => {
  if (!order.value) return
  ElMessageBox.confirm('确定要发货吗？', '提示', { type: 'info' })
    .then(async () => {
      await orderApi.ship(order.value!.id)
      ElMessage.success('发货成功')
      fetchDetail()
    })
    .catch(() => {})
}

const handleCancel = () => {
  if (!order.value) return
  ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
    .then(async () => {
      await orderApi.merchantCancel(order.value!.id)
      ElMessage.success('订单已取消')
      fetchDetail()
    })
    .catch(() => {})
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.merchant-order-detail {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
}
.breadcrumb {
  margin-bottom: 20px;
}
.detail-card {
  padding: 20px;
}
.order-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
.order-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}
.info-group {
  margin-bottom: 20px;
}
.info-group h3 {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 15px;
  color: #333;
}
.info-group p {
  margin: 8px 0;
  color: #666;
}
.info-group .label {
  color: #999;
  margin-right: 8px;
}
.total-price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}
.action-buttons {
  margin-top: 30px;
  display: flex;
  gap: 15px;
  justify-content: center;
}
.image-error {
  width: 50px;
  height: 50px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 12px;
}
</style>