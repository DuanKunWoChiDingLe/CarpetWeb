// import request from './request'
// import type { Page } from '@/types'

// export interface OrderItem {
//   id: number
//   productId: number
//   productName: string
//   productImage: string
//   price: number
//   quantity: number
// }

// export interface Order {
//   id: number
//   orderNo: string
//   userId: number
//   userName: string
//   totalAmount: number
//   status: 'pending' | 'paid' | 'shipped' | 'completed' | 'cancelled'
//   consignee: string
//   phone: string
//   address: string
//   remark?: string
//   paidAt?: string
//   createdAt: string
//   items: OrderItem[]
// }

// export interface OrderQueryParams {
//   page?: number
//   size?: number
//   status?: string
// }

// export const orderApi = {
//   // 创建订单
//   async create(data: {
//     consignee: string
//     phone: string
//     address: string
//     remark?: string
//     items: { productId: number; quantity: number }[]
//   }) {
//     const response = await request.post<any, any>('/orders', data)
//     return (response.data || response) as Order
//   },

//   // 获取当前用户订单列表
//   async getMyOrders(params: OrderQueryParams) {
//     const response = await request.get<any, any>('/orders', { params })
//     const pageData = response.data || response
//     return {
//       content: pageData.records || pageData.content || [],
//       totalElements: pageData.total || pageData.totalElements || 0,
//     } as Page<Order>
//   },

//   // 获取订单详情
//   async getDetail(id: number) {
//     const response = await request.get<any, any>(`/orders/${id}`)
//     return (response.data || response) as Order
//   },

//   // 取消订单
//   cancel(id: number) {
//     return request.put<any, void>(`/orders/${id}/cancel`)
//   },

//   // 支付订单
//   async pay(id: number, data: { password: string }) {
//     const response = await request.put<any, any>(`/orders/${id}/pay`, data)
//     return (response.data || response) as Order
//   },

//   // 确认收货
//   confirm(id: number) {
//     return request.put<any, void>(`/orders/${id}/confirm`)
//   },

//   // ---------- 商家端 ----------
//   async getAllOrders(params: {
//     page?: number
//     size?: number
//     status?: string
//     keyword?: string
//     startDate?: string
//     endDate?: string
//   }) {
//     const response = await request.get<any, any>('/merchant/orders', { params })
//     const pageData = response.data || response
//     return {
//       content: pageData.records || pageData.content || [],
//       totalElements: pageData.total || pageData.totalElements || 0,
//     } as Page<Order>
//   },

//   async getMerchantOrderDetail(id: number) {
//     const response = await request.get<any, any>(`/merchant/orders/${id}`)
//     return (response.data || response) as Order
//   },

//   ship(id: number) {
//     return request.put<any, void>(`/merchant/orders/${id}/ship`)
//   },

//   merchantCancel(id: number) {
//     return request.put<any, void>(`/merchant/orders/${id}/cancel`)
//   }
// }

import request from './request'
import type { Page } from '@/types/api'

// 统一响应格式
interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

// MyBatis-Plus 分页结果（后端返回格式）
interface MyBatisPage<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface OrderItem {
  id: number
  productId: number
  productName: string
  productImage: string
  price: number
  quantity: number
}

export interface Order {
  id: number
  orderNo: string
  userId: number
  userName: string
  totalAmount: number
  status: 'pending' | 'paid' | 'shipped' | 'completed' | 'cancelled'
  consignee: string
  phone: string
  address: string
  remark?: string
  paidAt?: string
  createdAt: string
  items: OrderItem[]
}

export interface OrderQueryParams {
  page?: number
  size?: number
  status?: string
}

export const orderApi = {
  // 创建订单
  async create(data: {
    consignee: string
    phone: string
    address: string
    remark?: string
    items: { productId: number; quantity: number }[]
  }): Promise<Order> {
    const response = await request.post<ApiResponse<Order>>('/orders', data)
    return response.data
  },

  // 获取当前用户订单列表
  async getMyOrders(params: OrderQueryParams): Promise<Page<Order>> {
    const response = await request.get<ApiResponse<MyBatisPage<Order>>>('/orders', { params })
    const page = response.data
    const content = page.records
    const totalElements = page.total
    const size = page.size
    const number = page.current
    const totalPages = page.pages

    return {
      content,
      totalElements,
      size,
      number,
      totalPages,
      first: number === 1,        // MyBatis 页码从1开始
      last: number === totalPages,
      empty: content.length === 0,
    }
  },

  // 获取订单详情
  async getDetail(id: number): Promise<Order> {
    const response = await request.get<ApiResponse<Order>>(`/orders/${id}`)
    return response.data
  },

  // 取消订单
  async cancel(id: number): Promise<void> {
    await request.put<ApiResponse<void>>(`/orders/${id}/cancel`)
  },

  // 支付订单
  async pay(id: number, data: { password: string }): Promise<Order> {
    const response = await request.put<ApiResponse<Order>>(`/orders/${id}/pay`, data)
    return response.data
  },

  // 确认收货
  async confirm(id: number): Promise<void> {
    await request.put<ApiResponse<void>>(`/orders/${id}/confirm`)
  },

  // ---------- 商家端 ----------
  async getAllOrders(params: {
    page?: number
    size?: number
    status?: string
    keyword?: string
    startDate?: string
    endDate?: string
  }): Promise<Page<Order>> {
    const response = await request.get<ApiResponse<MyBatisPage<Order>>>('/merchant/orders', { params })
    const page = response.data
    const content = page.records
    const totalElements = page.total
    const size = page.size
    const number = page.current
    const totalPages = page.pages

    return {
      content,
      totalElements,
      size,
      number,
      totalPages,
      first: number === 1,
      last: number === totalPages,
      empty: content.length === 0,
    }
  },

  async getMerchantOrderDetail(id: number): Promise<Order> {
    const response = await request.get<ApiResponse<Order>>(`/merchant/orders/${id}`)
    return response.data
  },

  async ship(id: number): Promise<void> {
    await request.put<ApiResponse<void>>(`/merchant/orders/${id}/ship`)
  },

  async merchantCancel(id: number): Promise<void> {
    await request.put<ApiResponse<void>>(`/merchant/orders/${id}/cancel`)
  }
}