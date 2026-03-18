import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { cartApi, type CartItem } from '@/api/cart'
import { ElMessage } from 'element-plus'

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([])
  const loading = ref(false)

  // 获取购物车（初始化）
  async function fetchCart() {
    loading.value = true
    try {
      const res = await cartApi.getCart()
      items.value = res
    } catch (error) {
      ElMessage.error('获取购物车失败')
      items.value = []
    } finally {
      loading.value = false
    }
  }

  // 添加商品
  async function addItem(item: { productId: number; quantity: number }) {
    try {
      await cartApi.addToCart(item)
      // 重新获取购物车（或手动更新 items）
      await fetchCart()
    } catch (error) {
      ElMessage.error('添加失败')
    }
  }

  // 更新数量
  async function updateQuantity(id: number, quantity: number) {
    try {
      await cartApi.updateCartItem(id, { quantity })
      // 更新本地 items（避免重新请求）
      const existing = items.value.find(i => i.id === id)
      if (existing) {
        existing.quantity = quantity
      }
    } catch (error) {
      ElMessage.error('更新失败')
    }
  }

  // 删除商品
  async function removeItem(id: number) {
    try {
      await cartApi.removeCartItem(id)
      items.value = items.value.filter(i => i.id !== id)
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }

  // 清空购物车
  async function clearCart() {
    try {
      await cartApi.clearCart()
      items.value = []
    } catch (error) {
      ElMessage.error('清空失败')
    }
  }

  // 计算总数量
  const totalCount = computed(() => {
    return items.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  // 计算总金额
  const totalPrice = computed(() => {
    return items.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
  })

  function resetCart() {
  items.value = []
}

  return {
    items,
    loading,
    totalCount,
    totalPrice,
    fetchCart,
    addItem,
    updateQuantity,
    removeItem,
    clearCart,
    resetCart
  }
})