import request from './request'
import type { UserInfo } from './auth'

export interface UpdateProfileRequest {
  nickname?: string
  avatar?: string
}

export const userApi = {
  // 获取个人资料（复用 /api/auth/me）
  async getProfile() {
  const response = await request.get<any, any>('/api/auth/me')
  return response.data || response
},
  // 更新个人资料
  async updateProfile(data: UpdateProfileRequest) {
  const response = await request.put<any, any>('/api/auth/profile', data)
  if (response.code !== 200) {
    throw new Error(response.message || '更新失败')
  }
  return response.data as UserInfo
}
}