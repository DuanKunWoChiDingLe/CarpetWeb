// src/api/series.ts
import request from './request'
import type { Page } from '@/types/api'

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

export const seriesApi = {
  // 分页查询系列列表
  async getPage(params: any) {
    // 发起请求，返回的是后端统一响应 Result 对象，data 部分为 MyBatis-Plus 分页对象
    const response = await request.get<any, any>('/api/series', { params })
    // response 已经是 request 拦截器处理后的 data？需确认
    // 如果拦截器直接返回了 response.data，那么 response 就是后端返回的 data 部分
    // 根据您之前的问题，拦截器可能未生效，所以我们按最保险的方式处理：
    const result = response.data || response // 兼容两种情况
    // 转换格式
    return {
      content: result.records || [],
      totalElements: result.total || 0,
      size: result.size,
      number: result.current - 1, // 后端页码从1开始，前端通常从0开始，但您之前用的是 currentPage-1 传参，所以这里不需要转换，保持原样即可
    } as Page<Series>
  },
  // 其他方法保持不变（getDetail、add、update、delete）
  // 但也要注意提取 data
  async getDetail(id: number) {
    const response = await request.get<any, any>(`/api/series/${id}`)
    return (response.data || response) as Series
  },
  async add(data: any) {
    const response = await request.post<any, any>('/api/series', data)
    return (response.data || response) as Series
  },
  async update(id: number, data: any) {
    const response = await request.put<any, any>(`/api/series/${id}`, data)
    return (response.data || response) as Series
  },
  delete(id: number) {
    return request.delete<any, void>(`/api/series/${id}`)
  }
}