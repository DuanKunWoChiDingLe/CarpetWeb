import request from './request'

export interface Brand {
  id: number
  name: string
  logo?: string
  sortOrder?: number
}

export const brandApi = {
  async getAll() {
    // 使用 request.get，它可能已经通过拦截器返回了 response.data
    const response = await request.get<any, any>('/api/brands')
    // 如果 response 已经是数组，直接返回
    if (Array.isArray(response)) {
      return response
    }
    // 如果 response 是包装对象 { code, data }，返回 data
    if (response && response.data && Array.isArray(response.data)) {
      return response.data
    }
    // 如果 response 是其他格式（如 MyBatis-Plus 分页对象），可能需要进一步处理，但品牌接口不应分页
    return []
  }
}