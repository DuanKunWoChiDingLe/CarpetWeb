// import request from './request'

// export interface LoginRequest {
//   username: string
//   password: string
// }

// export interface RegisterRequest {
//   username: string
//   password: string
//   role?: 'customer' | 'merchant'
// }

// export interface AuthResponse {
//   id: number
//   username: string
//   nickname: string
//   role: 'customer' | 'merchant'
//   avatar: string | null
//   token: string
// }

// export interface UserInfo {
//   id: number
//   username: string
//   nickname: string
//   role: 'customer' | 'merchant'
//   avatar: string | null
// }

// export const authApi = {
//   async login(data: LoginRequest) {
//     const response = await request.post<any, any>('/auth/login', data)
//     return response.data as AuthResponse   // 手动提取 data
//   },
//   async register(data: RegisterRequest) {
//     const response = await request.post<any, any>('/auth/register', data)
//     return response.data as AuthResponse
//   },
//   async getCurrentUser() {
//     const response = await request.get<any, any>('/auth/me')
//     return response.data as UserInfo
//   }
// }
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

// 后端统一响应格式
interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export const authApi = {
  async login(data: LoginRequest) {
    const response = await request.post<ApiResponse<AuthResponse>>('/auth/login', data)
    return response.data
  },
  async register(data: RegisterRequest) {
    const response = await request.post<ApiResponse<AuthResponse>>('/auth/register', data)
    return response.data
  },
  async getCurrentUser() {
    const response = await request.get<ApiResponse<UserInfo>>('/auth/me')
    return response.data
  }
}