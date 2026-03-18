import request from './request'

export interface CartItem {
  id: number
  productId: number
  name: string
  price: number
  quantity: number
  image: string
}

export interface AddToCartData {
  productId: number
  quantity: number
}

export const cartApi = {
  // 获取购物车列表
  async getCart() {
  const response = await request.get<any, any>('/api/cart')
  // 如果 response 已经是数组，直接返回；否则取 response.data
  return Array.isArray(response) ? response : (response.data || [])
},

  // 添加商品
  addToCart(data: AddToCartData) {
    return request.post<any, void>('/api/cart', data)
  },

  // 更新数量
  updateCartItem(id: number, data: { quantity: number }) {
    return request.put<any, void>(`/api/cart/${id}`, data)
  },

  // 删除项
  removeCartItem(id: number) {
    return request.delete<any, void>(`/api/cart/${id}`)
  },

  // 清空购物车
  clearCart() {
    return request.delete<any, void>('/api/cart/clear')
  }
}