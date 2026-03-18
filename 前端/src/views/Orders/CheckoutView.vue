<template>
  <div class="checkout-view">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/cart' }">购物车</el-breadcrumb-item>
      <el-breadcrumb-item>订单确认</el-breadcrumb-item>
    </el-breadcrumb>

    <el-row :gutter="30">
      <el-col :span="16">
        <el-card class="checkout-card">
          <template #header>
            <span>收货信息</span>
          </template>
          <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
            <el-form-item label="收货人" prop="consignee">
              <el-input v-model="form.consignee" placeholder="请输入收货人姓名" />
            </el-form-item>
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" maxlength="11" placeholder="请输入联系电话" />
            </el-form-item>
            <el-form-item label="收货地址" prop="address">
              <el-input v-model="form.address" placeholder="请输入详细地址" />
            </el-form-item>
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="选填，可备注特殊要求" />
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="checkout-card">
          <template #header>
            <span>订单汇总</span>
          </template>
          <div class="product-summary">
            <div v-for="item in selectedItems" :key="item.id" class="summary-item">
              <span>{{ item.name }} x {{ item.quantity }}</span>
              <span>¥{{ (item.price * item.quantity).toFixed(2) }}</span>
            </div>
          </div>
          <el-divider />
          <div class="total-line">
            <span>商品合计</span>
            <span class="total-price">¥{{ totalAmount.toFixed(2) }}</span>
          </div>
        </el-card>

        <div style="margin-top: 20px; display: flex; gap: 10px;">
          <el-button @click="router.back()">返回购物车</el-button>
          <el-button type="primary" size="large" @click="submitOrder" :loading="submitting">提交订单</el-button>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useCartStore } from '@/stores/cart'
import { orderApi } from '@/api/order'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

// 从路由获取选中的商品ID列表
const selectedIds = computed(() => {
  const ids = route.query.selected as string
  return ids ? ids.split(',').map(Number) : []
})

// 计算选中的商品
const selectedItems = computed(() => {
  return cartStore.items
    .filter(item => selectedIds.value.includes(item.id))
    .map(item => ({
      id: item.id,
      name: item.name,
      price: item.price,
      quantity: item.quantity,
      productId: item.productId
    }))
})

const totalAmount = computed(() => {
  return selectedItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

// 表单
const formRef = ref()
const form = ref({
  consignee: '',
  phone: '',
  address: '',
  remark: ''
})

const rules = {
  consignee: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  address: [{ required: true, message: '请输入收货地址', trigger: 'blur' }]
}

const submitting = ref(false)

// 提交订单
const submitOrder = async () => {
  await formRef.value.validate()
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择要购买的商品')
    return
  }
  submitting.value = true
  try {
    const orderData = {
      consignee: form.value.consignee,
      phone: form.value.phone,
      address: form.value.address,
      remark: form.value.remark,
      items: selectedItems.value.map(item => ({
        productId: item.productId,
        quantity: item.quantity
      }))
    }
    const newOrder = await orderApi.create(orderData)
    // 从购物车移除已下单商品
    selectedItems.value.forEach(item => {
      cartStore.removeItem(item.id)
    })
    ElMessage.success('订单创建成功')
    router.push(`/order/${newOrder.id}`)
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '创建订单失败')
  } finally {
    submitting.value = false
  }
}

// 检查购物车中是否有选中的商品
onMounted(() => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('购物车中没有选中商品')
    router.push('/cart')
  }
})
</script>

<style scoped>
.checkout-view {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
}
.breadcrumb { margin-bottom: 20px; }
.checkout-card { margin-bottom: 20px; }
.product-summary {
  max-height: 300px;
  overflow-y: auto;
}
.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
}
.total-line {
  display: flex;
  justify-content: space-between;
  font-size: 16px;
  font-weight: 500;
}
.total-price {
  color: #f56c6c;
  font-size: 20px;
}
</style>