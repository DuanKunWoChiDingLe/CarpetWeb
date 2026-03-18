import request from './request'

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  role?: 'customer' | 'merchant'
}

export interface AuthResponse {
  id: number
  username: string
  nickname: string
  role: 'customer' | 'merchant'
  avatar: string | null
  token: string
}

export interface UserInfo {
  id: number
  username: string
  nickname: string
  role: 'customer' | 'merchant'
  avatar: string | null
}

export const authApi = {
  async login(data: LoginRequest) {
    const response = await request.post<any, any>('/api/auth/login', data)
    return response.data as AuthResponse   // 手动提取 data
  },
  async register(data: RegisterRequest) {
    const response = await request.post<any, any>('/api/auth/register', data)
    return response.data as AuthResponse
  },
  async getCurrentUser() {
    const response = await request.get<any, any>('/api/auth/me')
    return response.data as UserInfo
  }
}