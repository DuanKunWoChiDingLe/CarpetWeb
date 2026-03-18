import request from './request'
import type { Page } from '@/types/api'

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
  }) {
    const response = await request.post<any, any>('/api/orders', data)
    return (response.data || response) as Order
  },

  // 获取当前用户订单列表
  async getMyOrders(params: OrderQueryParams) {
    const response = await request.get<any, any>('/api/orders', { params })
    const pageData = response.data || response
    return {
      content: pageData.records || pageData.content || [],
      totalElements: pageData.total || pageData.totalElements || 0,
    } as Page<Order>
  },

  // 获取订单详情
  async getDetail(id: number) {
    const response = await request.get<any, any>(`/api/orders/${id}`)
    return (response.data || response) as Order
  },

  // 取消订单
  cancel(id: number) {
    return request.put<any, void>(`/api/orders/${id}/cancel`)
  },

  // 支付订单
  async pay(id: number, data: { password: string }) {
    const response = await request.put<any, any>(`/api/orders/${id}/pay`, data)
    return (response.data || response) as Order
  },

  // 确认收货
  confirm(id: number) {
    return request.put<any, void>(`/api/orders/${id}/confirm`)
  },

  // ---------- 商家端 ----------
  async getAllOrders(params: {
    page?: number
    size?: number
    status?: string
    keyword?: string
    startDate?: string
    endDate?: string
  }) {
    const response = await request.get<any, any>('/api/merchant/orders', { params })
    const pageData = response.data || response
    return {
      content: pageData.records || pageData.content || [],
      totalElements: pageData.total || pageData.totalElements || 0,
    } as Page<Order>
  },

  async getMerchantOrderDetail(id: number) {
    const response = await request.get<any, any>(`/api/merchant/orders/${id}`)
    return (response.data || response) as Order
  },

  ship(id: number) {
    return request.put<any, void>(`/api/merchant/orders/${id}/ship`)
  },

  merchantCancel(id: number) {
    return request.put<any, void>(`/api/merchant/orders/${id}/cancel`)
  }
}