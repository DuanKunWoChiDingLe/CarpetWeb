import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi, type AuthResponse, type UserInfo } from '@/api/auth'
import { ElMessage } from 'element-plus'
import { useCartStore } from './cart'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)
  const isLoggedIn = ref(!!token.value)
  const cartStore = useCartStore()

  // 登录
  const login = async (username: string, password: string) => {
    try {
      const res = await authApi.login({ username, password })
      token.value = res.token
      userInfo.value = {
        id: res.id,
        username: res.username,
        nickname: res.nickname,
        role: res.role,
        avatar: res.avatar
      }
      localStorage.setItem('token', res.token)
      isLoggedIn.value = true
      cartStore.fetchCart()
      ElMessage.success('登录成功')
      return true
    } catch (error: any) {
      // 处理后端返回的错误信息
      if (error.response?.data) {
        const data = error.response.data
        if (typeof data === 'object' && data.error) {
          ElMessage.error(data.error) // 显示 "用户不存在" 或 "密码错误"
        } else {
          ElMessage.error('登录失败，请稍后重试')
        }
      } else if (error.request) {
        // 请求发出但没有收到响应（如后端未启动）
        ElMessage.error('无法连接到服务器，请检查网络或后端是否启动')
      } else {
        ElMessage.error('登录失败：' + error.message)
      }
      return false
    }
  }

  // 注册
  const register = async (username: string, password: string, role?: 'customer' | 'merchant') => {
    try {
      const res = await authApi.register({ username, password, role })
      token.value = res.token
      userInfo.value = {
        id: res.id,
        username: res.username,
        nickname: res.nickname,
        role: res.role,
        avatar: res.avatar
      }
      localStorage.setItem('token', res.token)
      isLoggedIn.value = true
      ElMessage.success('注册成功，已自动登录')
      return true
    } catch (error: any) {
      if (error.response?.data) {
        const data = error.response.data
        if (typeof data === 'object') {
          // 处理字段验证错误（如 { username: "用户名已存在" }）
          Object.values(data).forEach(msg => {
            ElMessage.error(msg as string)
          })
        } else {
          ElMessage.error(data || '注册失败')
        }
      } else if (error.request) {
        ElMessage.error('无法连接到服务器，请检查网络或后端是否启动')
      } else {
        ElMessage.error('注册失败：' + error.message)
      }
      return false
    }
  }

  // 退出
  const logout = () => {
    token.value = ''
    userInfo.value = null
    isLoggedIn.value = false
    localStorage.removeItem('token')
    const cartStore = useCartStore()
    cartStore.resetCart()  // 调用新增的方法
    ElMessage.success('已退出登录')
  }

  // 获取当前用户信息（用于初始化）
const fetchCurrentUser = async () => {
  if (!token.value) return
  try {
    const res = await authApi.getCurrentUser()
    userInfo.value = res  // 此时 res 应包含 nickname
    isLoggedIn.value = true
    
  } catch {
    logout()
  }
}

  return {
    token,
    userInfo,
    isLoggedIn,
    login,
    register,
    logout,
    fetchCurrentUser
  }
})