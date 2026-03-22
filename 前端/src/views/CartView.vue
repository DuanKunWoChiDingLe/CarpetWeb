<!-- 购物车组件 -->
<template>
  <div class="cart-view">
    <h2 class="page-title">购物车</h2>
    <!-- 未登录提示 -->
    <el-card v-if="!userStore.isLoggedIn" class="login-prompt">
      <el-empty description="请先登录后再查看购物车">
        <el-button type="primary" @click="goToLogin">去登录</el-button>
      </el-empty>
    </el-card>

    <!-- 购物车为空时的占位 -->
    <el-empty v-else-if="cartStore.items.length === 0" description="购物车空空如也，去逛逛吧">
      <el-button type="primary" @click="goToHome">去首页</el-button>
    </el-empty>

    <!-- 购物车商品列表 -->
    <el-table
      v-else
      ref="multipleTable"
      :data="cartStore.items"
      style="width: 100%"
      @selection-change="handleSelectionChange"
      row-key="id"
    >
      <!-- 多选框列 -->
      <el-table-column type="selection" width="55" />

      <!-- 商品图片列 -->
      <el-table-column label="商品图片" width="100">
        <template #default="{ row }">
          <el-image
            :src="getImageUrl(row.image)"
            :preview-src-list="[getImageUrl(row.image)]"
            fit="cover"
            style="width: 60px; height: 60px; border-radius: 4px;"
            lazy
          />
        </template>
      </el-table-column>

      <!-- 商品名称 -->
      <el-table-column prop="name" label="商品名称" min-width="180" />

      <!-- 单价 -->
      <el-table-column label="单价" width="100">
        <template #default="{ row }">
          ¥{{ row.price.toFixed(2) }}
        </template>
      </el-table-column>

      <!-- 数量 -->
      <el-table-column label="数量" width="150">
        <template #default="{ row }">
          <el-input-number
            v-model="row.quantity"
            :min="1"
            size="small"
            @change="(value: number) => handleQuantityChange(row.id, value)"
          />
        </template>
      </el-table-column>

      <!-- 小计 -->
      <el-table-column label="小计" width="100">
        <template #default="{ row }">
          <span style="color: #f56c6c; font-weight: bold;">
            ¥{{ (row.price * row.quantity).toFixed(2) }}
          </span>
        </template>
      </el-table-column>

      <!-- 操作 -->
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button type="danger" size="small" @click="handleRemove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 底部操作栏 -->
    <div v-if="cartStore.items.length > 0" class="cart-footer">
      <div class="footer-left">
        <el-checkbox v-model="selectAll" @change="handleSelectAll">全选</el-checkbox>
        <el-button link @click="handleClearCart">清空购物车</el-button>
      </div>
      <div class="footer-right">
        <span class="total-text">
          已选 <span class="highlight">{{ selectedCount }}</span> 件商品，合计
          <span class="total-price">¥{{ selectedTotal.toFixed(2) }}</span>
        </span>
        <el-button type="primary" size="large" @click="handleCheckout">去结算</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import type { CartItem } from '@/api/cart'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

// 表格实例，用于全选操作
const multipleTable = ref()

// 表格选中的商品行
const selectedRows = ref<CartItem[]>([])

const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const getImageUrl = (path: string) => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  // 如果路径以 /uploads 开头，直接返回（不拼接 baseURL）
  if (path.startsWith('/uploads')) {
    return path
  }
  // 其他情况（如开发环境可能直接相对路径）才拼接 baseURL
  return baseURL + path
}

// 全选状态
const selectAll = computed({
  get: () => {
    return cartStore.items.length > 0 && selectedRows.value.length === cartStore.items.length
  },
  set: (val) => {
    if (multipleTable.value) {
      if (val) {
        // 全选
        cartStore.items.forEach(item => {
          multipleTable.value.toggleRowSelection(item, true)
        })
      } else {
        // 取消全选
        multipleTable.value.clearSelection()
      }
    }
  }
})

// 选中商品数量
const selectedCount = computed(() => selectedRows.value.length)

// 选中商品总价
const selectedTotal = computed(() => {
  return selectedRows.value.reduce((sum: number, item: CartItem) => sum + item.price * item.quantity, 0)
})

const ids = selectedRows.value.map((item: CartItem) => item.id).join(',')

// 监听表格选择变化
const handleSelectionChange = (rows: CartItem[]) => {
  selectedRows.value = rows
}

// 全选复选框变化时的额外处理（如果需要）
const handleSelectAll = (checked: boolean) => {
  // 这里无需额外操作，因为 v-model 已经通过 setter 处理了表格选中
}

// 处理数量变化
const handleQuantityChange = (id: number, quantity: number) => {
  cartStore.updateQuantity(id, quantity)
}

// 删除单个商品
const handleRemove = (id: number) => {
  ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
    type: 'warning'
  }).then(() => {
    cartStore.removeItem(id)
    ElMessage.success('已删除')
  }).catch(() => {})
}

// 清空购物车
const handleClearCart = () => {
  ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
    type: 'warning'
  }).then(() => {
    cartStore.clearCart()
    ElMessage.success('购物车已清空')
  }).catch(() => {})
}

// 去结算
const handleCheckout = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一件商品')
    return
  }
  // 将选中商品的 ID 列表传递给结算页
  const ids = selectedRows.value.map(item => item.id).join(',')
  router.push(`/checkout?selected=${ids}`)
}

// 去登录
const goToLogin = () => {
  router.push('/login')
}

// 去首页
const goToHome = () => {
  router.push('/')
}
</script>

<style scoped>
.cart-view {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #333;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding: 15px 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 30px;
}

.total-text {
  font-size: 16px;
  color: #666;
}

.highlight {
  color: #f56c6c;
  font-weight: bold;
}

.total-price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}

.image-error {
  width: 60px;
  height: 60px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 12px;
}
</style>