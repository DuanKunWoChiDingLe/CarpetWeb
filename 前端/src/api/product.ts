// import request from './request'
// import type { Page } from '@/types/api'

// export interface Product {
//   id: number
//   seriesId: number
//   seriesName?: string
//   brandName?: string
//   colorCode: string
//   name: string
//   layType?: 'full' | 'modular'
//   material?: string
//   spec?: string
//   pricePerSqm: number
//   stock: number
//   images: string[]
//   description?: string
//   status: 0 | 1
//   createdAt?: string
// }

// export interface ProductQueryParams {
//   page?: number
//   size?: number
//   sort?: string
// }

// export const productApi = {
//   // 获取系列下的商品列表
//   async getListBySeries(seriesId: number, params: ProductQueryParams) {
//     const response = await request.get<any, any>(`/products/series/${seriesId}`, { params })
//     // 兼容后端的 MyBatis-Plus 分页格式 { records, total } 或 { content, totalElements }
//     const pageData = response.data || response
//     return {
//       content: pageData.records || pageData.content || [],
//       totalElements: pageData.total || pageData.totalElements || 0,
//     } as Page<Product>
//   },

//   // 获取商品详情
//   async getDetail(id: number) {
//     const response = await request.get<any, any>(`/products/${id}`)
//     return (response.data || response) as Product
//   },

//   // 新增商品
//   async add(data: Omit<Product, 'id' | 'seriesName' | 'brandName'>) {
//     const response = await request.post<any, any>('/products', data)
//     return (response.data || response) as Product
//   },

//   // 修改商品
//   async update(id: number, data: Partial<Omit<Product, 'id' | 'seriesName' | 'brandName'>>) {
//     const response = await request.put<any, any>(`/products/${id}`, data)
//     return (response.data || response) as Product
//   },

//   // 删除商品
//   async delete(id: number) {
//     const response = await request.delete<any, any>(`/products/${id}`)
//     return response.data || response
//   },

//   // 获取热门推荐商品（按销量降序，若销量均为0则按创建时间倒序或随机）
// async getHotProducts(limit: number = 4) {
//   const response = await request.get<any, any>('/products/hot', { params: { limit } })
//   return (response.data || response) as Product[]
// },

// // 获取最新商品（按创建时间倒序，支持分页）
// async getLatestProducts(params: { page?: number; size?: number }) {
//   const response = await request.get<any, any>('/products/latest', { params })
//   // 返回分页格式，兼容后端可能返回 { content, totalElements } 或直接数组
//   const pageData = response.data || response
//   return {
//     content: pageData.content || pageData.records || pageData,
//     totalElements: pageData.totalElements || pageData.total || 0,
//   } as Page<Product>
// },

// async search(params: { keyword: string; page?: number; size?: number }) {
//     const response = await request.get<any, any>('/products/search', { params })
//     const pageData = response.data || response
//     return {
//       content: pageData.content || pageData.records || [],
//       totalElements: pageData.totalElements || pageData.total || 0,
//     } as Page<Product>
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

// MyBatis-Plus 分页结果
interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

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
  async getListBySeries(seriesId: number, params: ProductQueryParams): Promise<Page<Product>> {
    const response = await request.get<ApiResponse<PageResult<Product>>>(`/products/series/${seriesId}`, { params })
    const page = response.data
    return {
      content: page.records || [],
      totalElements: page.total || 0,
      totalPages: page.pages || 0,
      size: page.size || 0,
      number: page.current - 1, // 转换为从0开始
      first: page.current === 1,
      last: page.current === page.pages,
      empty: page.records?.length === 0,
    }
  },

  // 获取商品详情
  async getDetail(id: number): Promise<Product> {
    const response = await request.get<ApiResponse<Product>>(`/products/${id}`)
    return response.data
  },

  // 新增商品
  async add(data: Omit<Product, 'id' | 'seriesName' | 'brandName'>): Promise<Product> {
    const response = await request.post<ApiResponse<Product>>('/products', data)
    return response.data
  },

  // 修改商品
  async update(id: number, data: Partial<Omit<Product, 'id' | 'seriesName' | 'brandName'>>): Promise<Product> {
    const response = await request.put<ApiResponse<Product>>(`/products/${id}`, data)
    return response.data
  },

  // 删除商品
  async delete(id: number): Promise<void> {
    await request.delete<ApiResponse<void>>(`/products/${id}`)
  },

  // 获取热门推荐商品（按销量降序）
  async getHotProducts(limit: number = 4): Promise<Product[]> {
    const response = await request.get<ApiResponse<Product[]>>('/products/hot', { params: { limit } })
    return response.data
  },

  // 获取最新商品（按创建时间倒序，支持分页）
  async getLatestProducts(params: { page?: number; size?: number }): Promise<Page<Product>> {
    const response = await request.get<ApiResponse<PageResult<Product>>>('/products/latest', { params })
    const page = response.data
    return {
      content: page.records || [],
      totalElements: page.total || 0,
      totalPages: page.pages || 0,
      size: page.size || 0,
      number: page.current - 1,
      first: page.current === 1,
      last: page.current === page.pages,
      empty: page.records?.length === 0,
    }
  },

  // 搜索商品
  async search(params: { keyword: string; page?: number; size?: number }): Promise<Page<Product>> {
    const response = await request.get<ApiResponse<PageResult<Product>>>('/products/search', { params })
    const page = response.data
    return {
      content: page.records || [],
      totalElements: page.total || 0,
      totalPages: page.pages || 0,
      size: page.size || 0,
      number: page.current - 1,
      first: page.current === 1,
      last: page.current === page.pages,
      empty: page.records?.length === 0,
    }
  }
}