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
      class="cart-table"
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

    <!-- 移动端商品卡片 -->
    <div class="mobile-cart-list">
      <div v-for="row in cartStore.items" :key="row.id" class="mobile-cart-item">
        <div class="mobile-cart-head">
          <el-checkbox
            :model-value="isSelected(row.id)"
            @change="(checked: boolean) => toggleMobileSelection(row, checked)"
          />
          <span class="mobile-cart-name">{{ row.name }}</span>
          <el-button link type="danger" @click="handleRemove(row.id)">删除</el-button>
        </div>
        <div class="mobile-cart-body">
          <el-image
            :src="getImageUrl(row.image)"
            :preview-src-list="[getImageUrl(row.image)]"
            fit="cover"
            class="mobile-cart-image"
            lazy
          />
          <div class="mobile-cart-info">
            <span class="mobile-cart-price">¥{{ row.price.toFixed(2) }} / m²</span>
            <el-input-number
              v-model="row.quantity"
              :min="1"
              size="small"
              @change="(value: number) => handleQuantityChange(row.id, value)"
            />
            <span class="mobile-cart-subtotal">小计 ¥{{ (row.price * row.quantity).toFixed(2) }}</span>
          </div>
        </div>
      </div>
    </div>
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
import { getImageUrl } from '@/utils/image'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

// 表格实例，用于全选操作
const multipleTable = ref()

// 表格选中的商品行
const selectedRows = ref<CartItem[]>([])


// 全选状态
const selectAll = computed({
  get: () => {
    return cartStore.items.length > 0 && selectedRows.value.length === cartStore.items.length
  },
  set: (val) => {
    selectedRows.value = val ? [...cartStore.items] : []
    if (multipleTable.value) {
      if (val) {
        cartStore.items.forEach(item => {
          multipleTable.value.toggleRowSelection(item, true)
        })
      } else {
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

const isSelected = (id: number) => selectedRows.value.some(item => item.id === id)

const toggleMobileSelection = (item: CartItem, checked: boolean) => {
  if (checked) {
    if (!isSelected(item.id)) selectedRows.value.push(item)
  } else {
    selectedRows.value = selectedRows.value.filter(selected => selected.id !== item.id)
  }
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
.mobile-cart-list {
  display: none;
}

@media (max-width: 767px) {
  .cart-table {
    display: none !important;
  }

  .mobile-cart-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .mobile-cart-item {
    padding: 12px;
    background: #fff;
    border: 1px solid #ebeef5;
    border-radius: 10px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  }

  .mobile-cart-head {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 10px;
  }

  .mobile-cart-name {
    flex: 1;
    min-width: 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-weight: 600;
  }

  .mobile-cart-body {
    display: flex;
    gap: 12px;
    align-items: center;
  }

  .mobile-cart-image {
    width: 78px;
    height: 78px;
    flex: 0 0 78px;
    border-radius: 8px;
    overflow: hidden;
  }

  .mobile-cart-info {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .mobile-cart-price {
    color: #f56c6c;
    font-weight: 600;
  }

  .mobile-cart-subtotal {
    font-size: 13px;
    color: #606266;
  }
}
</style>




