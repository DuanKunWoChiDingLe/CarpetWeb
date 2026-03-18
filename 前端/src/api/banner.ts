import request from './request'

export interface Banner {
  id: number
  imageUrl: string
  linkType: 'product' | 'url'
  linkValue: string
  sortOrder: number
  enabled: boolean
  createdAt?: string
  updatedAt?: string
}

export interface BannerQueryParams {
  page?: number
  size?: number
  enabled?: boolean
}

export const bannerApi = {
  // 分页获取（商家端）
async getPage(params: BannerQueryParams) {
  const response = await request.get<any, any>('/api/banners', { params })
  // 提取 data 部分，因为后端返回的是 {code, data}
  const pageData = response.data || response
  return {
    content: pageData.records || [],      // MyBatis-Plus 分页字段为 records
    totalElements: pageData.total || 0,   // 总记录数字段为 total
  }
},

  // 新增
  async add(data: Omit<Banner, 'id' | 'createdAt' | 'updatedAt'>) {
    const response = await request.post<any, any>('/api/banners', data)
    return (response.data || response) as Banner
  },

  // 修改
  async update(id: number, data: Omit<Banner, 'id' | 'createdAt' | 'updatedAt'>) {
    const response = await request.put<any, any>(`/api/banners/${id}`, data)
    return (response.data || response) as Banner
  },

  // 更新状态
  async updateStatus(id: number, enabled: boolean) {
    const response = await request.patch<any, any>(`/api/banners/${id}/status`, { enabled })
    return response.data || response
  },

  // 删除
  async delete(id: number) {
    const response = await request.delete<any, any>(`/api/banners/${id}`)
    return response.data || response
  },

  // 获取启用的轮播图（公共）
  async getEnabled() {
    const response = await request.get<any, any>('/api/public/banners')
    // 关键：返回 response.data（即数组）
    return (response.data || response) as Banner[]
  }
}