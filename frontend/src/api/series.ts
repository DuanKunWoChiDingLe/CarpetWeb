// // src/api/series.ts
// import request from './request'
// import type { Page } from '@/types/api'

// export interface Series {
//   id: number
//   brandId: number
//   brandName?: string
//   name: string
//   coverImage?: string
//   layType: 'full' | 'modular'
//   material: string
//   spec: string
//   description?: string
//   colorCount?: number
//   createdAt?: string
// }

// export const seriesApi = {
//   // 分页查询系列列表
//   async getPage(params: any) {
//     // 发起请求，返回的是后端统一响应 Result 对象，data 部分为 MyBatis-Plus 分页对象
//     const response = await request.get<any, any>('/series', { params })
//     // response 已经是 request 拦截器处理后的 data？需确认
//     // 如果拦截器直接返回了 response.data，那么 response 就是后端返回的 data 部分
//     // 根据您之前的问题，拦截器可能未生效，所以我们按最保险的方式处理：
//     const result = response.data || response // 兼容两种情况
//     // 转换格式
//     return {
//       content: result.records || [],
//       totalElements: result.total || 0,
//       size: result.size,
//       number: result.current - 1, // 后端页码从1开始，前端通常从0开始，但您之前用的是 currentPage-1 传参，所以这里不需要转换，保持原样即可
//     } as Page<Series>
//   },
//   // 其他方法保持不变（getDetail、add、update、delete）
//   // 但也要注意提取 data
//   async getDetail(id: number) {
//     const response = await request.get<any, any>(`/series/${id}`)
//     return (response.data || response) as Series
//   },
//   async add(data: any) {
//     const response = await request.post<any, any>('/series', data)
//     return (response.data || response) as Series
//   },
//   async update(id: number, data: any) {
//     const response = await request.put<any, any>(`/series/${id}`, data)
//     return (response.data || response) as Series
//   },
//   delete(id: number) {
//     return request.delete<any, void>(`/series/${id}`)
//   }
// }

// src/api/series.ts
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

export interface Series {
  id: number
  brandId: number
  brandName?: string
  name: string
  coverImage?: string
  layType: 'full' | 'modular'
  material: string
  spec: string
  description?: string
  colorCount?: number
  createdAt?: string
}

// 查询参数接口
export interface SeriesQueryParams {
  page?: number
  size?: number
  brandId?: number
  layType?: string
  material?: string
  keyword?: string
  sort?: string
}

// 新增系列请求参数（从 Series 中省略自动生成的字段）
export type AddSeriesRequest = Omit<Series, 'id' | 'brandName' | 'colorCount' | 'createdAt'> & {
  // 确保 brandId 是必需的（已在 Series 中）
}

// 更新系列请求参数（所有字段可选，除了 id 通过路径传递）
export type UpdateSeriesRequest = Partial<Omit<Series, 'id' | 'brandName' | 'colorCount' | 'createdAt'>>

export const seriesApi = {
  // 分页查询系列列表
  async getPage(params: SeriesQueryParams): Promise<Page<Series>> {
    const response = await request.get<ApiResponse<PageResult<Series>>>('/series', { params })
    const page = response.data
    return {
      content: page.records || [],
      totalElements: page.total || 0,
      totalPages: page.pages || 0,
      size: page.size || 0,
      number: page.current - 1, // 后端从1开始，转为从0开始
      first: page.current === 1,
      last: page.current === page.pages,
      empty: page.records?.length === 0,
    }
  },

  // 获取系列详情
  async getDetail(id: number): Promise<Series> {
    const response = await request.get<ApiResponse<Series>>(`/series/${id}`)
    return response.data
  },

  // 新增系列
  async add(data: AddSeriesRequest): Promise<Series> {
    const response = await request.post<ApiResponse<Series>>('/series', data)
    return response.data
  },

  // 修改系列
  async update(id: number, data: UpdateSeriesRequest): Promise<Series> {
    const response = await request.put<ApiResponse<Series>>(`/series/${id}`, data)
    return response.data
  },

  // 删除系列
  async delete(id: number): Promise<void> {
    await request.delete<ApiResponse<void>>(`/series/${id}`)
  }
}