// import request from './request'

// export interface CartItem {
//   id: number
//   productId: number
//   name: string
//   price: number
//   quantity: number
//   image: string
// }

// export interface AddToCartData {
//   productId: number
//   quantity: number
// }

// export const cartApi = {
//   // 获取购物车列表
//   async getCart() {
//   const response = await request.get<any, any>('/cart')
//   // 如果 response 已经是数组，直接返回；否则取 response.data
//   return Array.isArray(response) ? response : (response.data || [])
// },

//   // 添加商品
//   addToCart(data: AddToCartData) {
//     return request.post<any, void>('/cart', data)
//   },

//   // 更新数量
//   updateCartItem(id: number, data: { quantity: number }) {
//     return request.put<any, void>(`/cart/${id}`, data)
//   },

//   // 删除项
//   removeCartItem(id: number) {
//     return request.delete<any, void>(`/cart/${id}`)
//   },

//   // 清空购物车
//   clearCart() {
//     return request.delete<any, void>('/cart/clear')
//   }
// }

import request from './request'

// 统一响应格式
interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

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
  async getCart(): Promise<CartItem[]> {
    const response = await request.get<ApiResponse<CartItem[]>>('/cart')
    // 如果 response 已经是数组（可能拦截器已解包），直接返回
    if (Array.isArray(response)) {
      return response
    }
    // 否则期望是 ApiResponse 结构，返回 data
    if (response && response.data && Array.isArray(response.data)) {
      return response.data
    }
    // 兜底返回空数组
    return []
  },

  // 添加商品
  async addToCart(data: AddToCartData): Promise<void> {
    await request.post<ApiResponse<void>>('/cart', data)
  },

  // 更新数量
  async updateCartItem(id: number, data: { quantity: number }): Promise<void> {
    await request.put<ApiResponse<void>>(`/cart/${id}`, data)
  },

  // 删除项
  async removeCartItem(id: number): Promise<void> {
    await request.delete<ApiResponse<void>>(`/cart/${id}`)
  },

  // 清空购物车
  async clearCart(): Promise<void> {
    await request.delete<ApiResponse<void>>('/cart/clear')
  }
}