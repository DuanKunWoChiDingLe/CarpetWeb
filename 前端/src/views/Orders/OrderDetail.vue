<template>
  <div class="order-detail">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/orders' }">我的订单</el-breadcrumb-item>
      <el-breadcrumb-item>订单详情</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card v-loading="loading" class="detail-card">
      <div v-if="order">
        <div class="order-header">
          <h2>订单号：{{ order.orderNo }}</h2>
          <el-tag :type="statusTagType(order.status)">{{ statusText(order.status) }}</el-tag>
        </div>

        <el-divider />

        <div class="info-section">
          <h3>收货信息</h3>
          <p>收货人：{{ order.consignee }}</p>
          <p>联系电话：{{ order.phone }}</p>
          <p>收货地址：{{ order.address }}</p>
          <p v-if="order.remark">买家备注：{{ order.remark }}</p>
        </div>

        <el-divider />

        <div class="info-section">
          <h3>商品清单</h3>
          <el-table :data="order.items" style="width: 100%">
            <el-table-column label="商品" width="80">
              <template #default="{ row }">
                <el-image :src="row.productImage" style="width: 50px; height: 50px;" fit="cover" />
              </template>
            </el-table-column>
            <el-table-column prop="productName" label="名称" />
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="price" label="单价" width="100">
              <template #default="{ row }">¥{{ row.price.toFixed(2) }}</template>
            </el-table-column>
            <el-table-column label="小计" width="100">
              <template #default="{ row }">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
            </el-table-column>
          </el-table>
        </div>

        <el-divider />

        <div class="info-section">
          <h3>订单信息</h3>
          <p>下单时间：{{ formatDate(order.createdAt) }}</p>
          <p v-if="order.paidAt">支付时间：{{ formatDate(order.paidAt) }}</p>
          <p>订单总金额：<span class="total-price">¥{{ order.totalAmount.toFixed(2) }}</span></p>
        </div>

        <div class="action-buttons">
          <el-button v-if="order.status === 'pending'" type="primary" @click="handlePay">去支付</el-button>
          <el-button v-if="order.status === 'pending'" type="danger" @click="handleCancel">取消订单</el-button>
          <el-button v-if="order.status === 'shipped'" type="success" @click="handleConfirm">确认收货</el-button>
          <el-button @click="router.back()">返回</el-button>
        </div>
      </div>
    </el-card>

    <!-- 支付弹窗 -->
    <PayDialog
      ref="payDialogRef"
      :order-id="order?.id"
      :order-no="order?.orderNo"
      @success="handlePaySuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi, type Order } from '@/api/order'
import PayDialog from '@/components/order/PayDialog.vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const order = ref<Order>()

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

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const fetchDetail = async () => {
  const id = route.params.id as string
  if (!id) return
  loading.value = true
  try {
    const res = await orderApi.getDetail(Number(id))
    order.value = res
  } catch (error) {
    ElMessage.error('获取订单详情失败')
    router.push('/orders')
  } finally {
    loading.value = false
  }
}

const payDialogRef = ref<InstanceType<typeof PayDialog>>()

const handlePay = () => {
  if (!order.value) return
  payDialogRef.value?.open()
}

const handlePaySuccess = () => {
  ElMessage.success('支付成功')
  fetchDetail()
}

const handleCancel = () => {
  if (!order.value) return
  ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
    .then(async () => {
      await orderApi.cancel(order.value!.id)
      ElMessage.success('订单已取消')
      fetchDetail()
    })
    .catch(() => {})
}

const handleConfirm = () => {
  if (!order.value) return
  ElMessageBox.confirm('确认收到商品了吗？', '提示', { type: 'info' })
    .then(async () => {
      await orderApi.confirm(order.value!.id)
      ElMessage.success('交易完成')
      fetchDetail()
    })
    .catch(() => {})
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.order-detail {
  max-width: 800px;
  margin: 30px auto;
  padding: 0 20px;
}
.breadcrumb { margin-bottom: 20px; }
.detail-card { padding: 20px; }
.order-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.order-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}
.info-section {
  margin: 20px 0;
}
.info-section h3 {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 10px;
  color: #333;
}
.info-section p {
  margin: 5px 0;
  color: #666;
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
</style>