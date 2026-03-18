import request from './request'
import type { Page } from '@/types/api'

export interface Product {
  id: number
  seriesId: number
  seriesName?: string
  brandName?: string
  colorCode: string
  name: string
  layType?: 'full' | 'modular'
  material?: string
  spec?: string
  pricePerSqm: number
  stock: number
  images: string[]
  description?: string
  status: 0 | 1
  createdAt?: string
}

export interface ProductQueryParams {
  page?: number
  size?: number
  sort?: string
}

export const productApi = {
  // 获取系列下的商品列表
  async getListBySeries(seriesId: number, params: ProductQueryParams) {
    const response = await request.get<any, any>(`/api/products/series/${seriesId}`, { params })
    // 兼容后端的 MyBatis-Plus 分页格式 { records, total } 或 { content, totalElements }
    const pageData = response.data || response
    return {
      content: pageData.records || pageData.content || [],
      totalElements: pageData.total || pageData.totalElements || 0,
    } as Page<Product>
  },

  // 获取商品详情
  async getDetail(id: number) {
    const response = await request.get<any, any>(`/api/products/${id}`)
    return (response.data || response) as Product
  },

  // 新增商品
  async add(data: Omit<Product, 'id' | 'seriesName' | 'brandName'>) {
    const response = await request.post<any, any>('/api/products', data)
    return (response.data || response) as Product
  },

  // 修改商品
  async update(id: number, data: Partial<Omit<Product, 'id' | 'seriesName' | 'brandName'>>) {
    const response = await request.put<any, any>(`/api/products/${id}`, data)
    return (response.data || response) as Product
  },

  // 删除商品
  async delete(id: number) {
    const response = await request.delete<any, any>(`/api/products/${id}`)
    return response.data || response
  },

  // 获取热门推荐商品（按销量降序，若销量均为0则按创建时间倒序或随机）
async getHotProducts(limit: number = 4) {
  const response = await request.get<any, any>('/api/products/hot', { params: { limit } })
  return (response.data || response) as Product[]
},

// 获取最新商品（按创建时间倒序，支持分页）
async getLatestProducts(params: { page?: number; size?: number }) {
  const response = await request.get<any, any>('/api/products/latest', { params })
  // 返回分页格式，兼容后端可能返回 { content, totalElements } 或直接数组
  const pageData = response.data || response
  return {
    content: pageData.content || pageData.records || pageData,
    totalElements: pageData.totalElements || pageData.total || 0,
  } as Page<Product>
},

async search(params: { keyword: string; page?: number; size?: number }) {
    const response = await request.get<any, any>('/api/products/search', { params })
    const pageData = response.data || response
    return {
      content: pageData.content || pageData.records || [],
      totalElements: pageData.totalElements || pageData.total || 0,
    } as Page<Product>
  }

}